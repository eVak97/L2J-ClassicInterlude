/*
 * Copyright (c) 2013 L2jMobius
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.l2jmobius.gameserver.model.actor.stat;

import java.util.concurrent.atomic.AtomicInteger;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.data.sql.CharInfoTable;
import org.l2jmobius.gameserver.data.xml.ExperienceData;
import org.l2jmobius.gameserver.enums.ElementalType;
import org.l2jmobius.gameserver.enums.PartySmallWindowUpdateType;
import org.l2jmobius.gameserver.enums.UserInfoType;
import org.l2jmobius.gameserver.model.Party;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.Summon;
import org.l2jmobius.gameserver.model.actor.instance.Pet;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.events.EventDispatcher;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerLevelChanged;
import org.l2jmobius.gameserver.model.holders.ItemSkillHolder;
import org.l2jmobius.gameserver.model.holders.SubClassHolder;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.item.type.WeaponType;
import org.l2jmobius.gameserver.model.skill.AbnormalType;
import org.l2jmobius.gameserver.model.stats.Formulas;
import org.l2jmobius.gameserver.model.stats.Stat;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.AcquireSkillList;
import org.l2jmobius.gameserver.network.serverpackets.ExVitalityPointInfo;
import org.l2jmobius.gameserver.network.serverpackets.ExVoteSystemInfo;
import org.l2jmobius.gameserver.network.serverpackets.PartySmallWindowUpdate;
import org.l2jmobius.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import org.l2jmobius.gameserver.network.serverpackets.SocialAction;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import org.l2jmobius.gameserver.network.serverpackets.dailymission.ExConnectedTimeAndGettableReward;
import org.l2jmobius.gameserver.network.serverpackets.dailymission.ExOneDayReceiveRewardList;
import org.l2jmobius.gameserver.network.serverpackets.friend.FriendStatus;
import org.l2jmobius.gameserver.util.Util;

public class PlayerStat extends PlayableStat
{
	private long _startingXp;
	private final AtomicInteger _talismanSlots = new AtomicInteger();
	private boolean _cloakSlot = false;
	private int _vitalityPoints = 0;
	
	public static final int MAX_VITALITY_POINTS = 140000;
	public static final int MIN_VITALITY_POINTS = 0;
	
	private static final int FANCY_FISHING_ROD_SKILL = 21484;
	
	public PlayerStat(Player player)
	{
		super(player);
	}
	
	@Override
	public boolean addExp(long value)
	{
		final Player player = getActiveChar();
		
		// Allowed to gain exp?
		if (!player.getAccessLevel().canGainExp())
		{
			return false;
		}
		
		if (!super.addExp(value))
		{
			return false;
		}
		
		// Set new karma
		if (!player.isCursedWeaponEquipped() && (player.getReputation() < 0) && (player.isGM() || !player.isInsideZone(ZoneId.PVP)))
		{
			final int karmaLost = Formulas.calculateKarmaLost(player, value);
			if (karmaLost > 0)
			{
				player.setReputation(Math.min((player.getReputation() + karmaLost), 0));
			}
		}
		
		// EXP status update currently not used in retail
		player.updateUserInfo();
		return true;
	}
	
	public void addExpAndSp(double addToExpValue, double addToSpValue, boolean useBonuses)
	{
		final Player player = getActiveChar();
		
		// Allowed to gain exp/sp?
		if (!player.getAccessLevel().canGainExp())
		{
			return;
		}
		
		double addToExp = addToExpValue;
		double addToSp = addToSpValue;
		
		final double baseExp = addToExp;
		final double baseSp = addToSp;
		double bonusExp = 1;
		double bonusSp = 1;
		if (useBonuses)
		{
			if (player.isFishing())
			{
				// rod fishing skills
				final Item rod = player.getActiveWeaponInstance();
				if ((rod != null) && (rod.getItemType() == WeaponType.FISHINGROD) && (rod.getTemplate().getAllSkills() != null))
				{
					for (ItemSkillHolder s : rod.getTemplate().getAllSkills())
					{
						if (s.getSkill().getId() == FANCY_FISHING_ROD_SKILL)
						{
							bonusExp *= 1.5;
							bonusSp *= 1.5;
						}
					}
				}
			}
			else
			{
				bonusExp = getExpBonusMultiplier();
				bonusSp = getSpBonusMultiplier();
			}
		}
		
		addToExp *= bonusExp;
		addToSp *= bonusSp;
		double ratioTakenByPlayer = 0;
		
		// if this player has a pet and it is in his range he takes from the owner's Exp, give the pet Exp now
		final Summon sPet = player.getPet();
		if ((sPet != null) && Util.checkIfInShortRange(Config.ALT_PARTY_RANGE, player, sPet, false))
		{
			final Pet pet = sPet.asPet();
			ratioTakenByPlayer = pet.getPetLevelData().getOwnerExpTaken() / 100f;
			
			// only give exp/sp to the pet by taking from the owner if the pet has a non-zero, positive ratio
			// allow possible customizations that would have the pet earning more than 100% of the owner's exp/sp
			if (ratioTakenByPlayer > 1)
			{
				ratioTakenByPlayer = 1;
			}
			
			if (!pet.isDead())
			{
				pet.addExpAndSp(addToExp * (1 - ratioTakenByPlayer), addToSp * (1 - ratioTakenByPlayer));
			}
			
			// now adjust the max ratio to avoid the owner earning negative exp/sp
			addToExp *= ratioTakenByPlayer;
			addToSp *= ratioTakenByPlayer;
		}
		
		final long finalExp = Math.round(addToExp);
		final long finalSp = Math.round(addToSp);
		final boolean expAdded = addExp(finalExp);
		final boolean spAdded = addSp(finalSp);
		if (!expAdded && spAdded)
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_ACQUIRED_S1_SP);
			sm.addLong(finalSp);
			player.sendPacket(sm);
		}
		else if (expAdded && !spAdded)
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1_XP);
			sm.addLong(finalExp);
			player.sendPacket(sm);
		}
		else if ((finalExp > 0) || (finalSp > 0))
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_ACQUIRED_S1_XP_BONUS_S2_AND_S3_SP_BONUS_S4);
			sm.addLong(finalExp);
			sm.addLong(Math.round(addToExp - baseExp));
			sm.addLong(finalSp);
			sm.addLong(Math.round(addToSp - baseSp));
			player.sendPacket(sm);
		}
	}
	
	@Override
	public boolean removeExpAndSp(long addToExp, long addToSp)
	{
		return removeExpAndSp(addToExp, addToSp, true);
	}
	
	public boolean removeExpAndSp(long addToExp, long addToSp, boolean sendMessage)
	{
		final int level = getLevel();
		if (!super.removeExpAndSp(addToExp, addToSp))
		{
			return false;
		}
		
		if (sendMessage)
		{
			// Send a Server->Client System Message to the Player
			SystemMessage sm = new SystemMessage(SystemMessageId.YOUR_XP_HAS_DECREASED_BY_S1);
			sm.addLong(addToExp);
			final Player player = getActiveChar();
			player.sendPacket(sm);
			sm = new SystemMessage(SystemMessageId.YOUR_SP_HAS_DECREASED_BY_S1);
			sm.addLong(addToSp);
			player.sendPacket(sm);
			if (getLevel() < level)
			{
				player.broadcastStatusUpdate();
			}
		}
		return true;
	}
	
	@Override
	public boolean addLevel(byte value)
	{
		if ((getLevel() + value) > (ExperienceData.getInstance().getMaxLevel() - 1))
		{
			return false;
		}
		
		final Player player = getActiveChar();
		final boolean levelIncreased = super.addLevel(value);
		if (levelIncreased)
		{
			player.setCurrentCp(getMaxCp());
			player.broadcastPacket(new SocialAction(player.getObjectId(), SocialAction.LEVEL_UP));
			player.sendPacket(SystemMessageId.YOUR_LEVEL_HAS_INCREASED);
			player.notifyFriends(FriendStatus.MODE_LEVEL);
		}
		
		// Notify to scripts
		if (EventDispatcher.getInstance().hasListener(EventType.ON_PLAYER_LEVEL_CHANGED, player))
		{
			EventDispatcher.getInstance().notifyEventAsync(new OnPlayerLevelChanged(player, getLevel() - value, getLevel()), player);
		}
		
		// Update daily mission count.
		player.sendPacket(new ExConnectedTimeAndGettableReward(player));
		
		// Give AutoGet skills and all normal skills if Auto-Learn is activated.
		player.rewardSkills();
		
		final Clan clan = player.getClan();
		if (clan != null)
		{
			clan.updateClanMember(player);
			clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(player));
		}
		if (player.isInParty())
		{
			player.getParty().recalculatePartyLevel(); // Recalculate the party level
		}
		
		// Maybe add some skills when player levels up in transformation.
		player.getTransformation().ifPresent(transform -> transform.onLevelUp(player));
		
		// Synchronize level with pet if possible.
		final Summon sPet = player.getPet();
		if (sPet != null)
		{
			final Pet pet = sPet.asPet();
			if (pet.getPetData().isSynchLevel() && (pet.getLevel() != getLevel()))
			{
				final byte availableLevel = (byte) Math.min(pet.getPetData().getMaxLevel(), getLevel());
				pet.getStat().setLevel(availableLevel);
				pet.getStat().getExpForLevel(availableLevel);
				pet.setCurrentHp(pet.getMaxHp());
				pet.setCurrentMp(pet.getMaxMp());
				pet.broadcastPacket(new SocialAction(player.getObjectId(), SocialAction.LEVEL_UP));
				pet.updateAndBroadcastStatus(1);
			}
		}
		
		player.broadcastStatusUpdate();
		// Update the overloaded status of the Player
		player.refreshOverloaded(true);
		// Update the expertise status of the Player
		player.refreshExpertisePenalty();
		// Send a Server->Client packet UserInfo to the Player
		player.updateUserInfo();
		// Send acquirable skill list
		player.sendPacket(new AcquireSkillList(player));
		player.sendPacket(new ExVoteSystemInfo(player));
		player.sendPacket(new ExOneDayReceiveRewardList(player, true));
		return levelIncreased;
	}
	
	@Override
	public boolean addSp(long value)
	{
		if (!super.addSp(value))
		{
			return false;
		}
		
		getActiveChar().broadcastUserInfo(UserInfoType.CURRENT_HPMPCP_EXP_SP);
		
		return true;
	}
	
	@Override
	public long getExpForLevel(int level)
	{
		return ExperienceData.getInstance().getExpForLevel(level);
	}
	
	@Override
	public Player getActiveChar()
	{
		return super.getActiveChar().asPlayer();
	}
	
	@Override
	public long getExp()
	{
		final Player player = getActiveChar();
		if (player.isSubClassActive())
		{
			return player.getSubClasses().get(player.getClassIndex()).getExp();
		}
		return super.getExp();
	}
	
	public long getBaseExp()
	{
		return super.getExp();
	}
	
	@Override
	public void setExp(long value)
	{
		final Player player = getActiveChar();
		if (player.isSubClassActive())
		{
			player.getSubClasses().get(player.getClassIndex()).setExp(value);
		}
		else
		{
			super.setExp(value);
		}
	}
	
	public void setStartingExp(long value)
	{
		if (Config.BOTREPORT_ENABLE)
		{
			_startingXp = value;
		}
	}
	
	public long getStartingExp()
	{
		return _startingXp;
	}
	
	/**
	 * Gets the maximum talisman count.
	 * @return the maximum talisman count
	 */
	public int getTalismanSlots()
	{
		if (!getActiveChar().hasEnteredWorld())
		{
			return 6;
		}
		
		return _talismanSlots.get();
	}
	
	public void addTalismanSlots(int count)
	{
		_talismanSlots.addAndGet(count);
	}
	
	public boolean canEquipCloak()
	{
		return _cloakSlot;
	}
	
	public void setCloakSlotStatus(boolean cloakSlot)
	{
		_cloakSlot = cloakSlot;
	}
	
	@Override
	public byte getLevel()
	{
		final Player player = getActiveChar();
		if (player.isDualClassActive())
		{
			return player.getDualClass().getLevel();
		}
		if (player.isSubClassActive())
		{
			final SubClassHolder holder = player.getSubClasses().get(player.getClassIndex());
			if (holder != null)
			{
				return holder.getLevel();
			}
		}
		return super.getLevel();
	}
	
	public byte getBaseLevel()
	{
		return super.getLevel();
	}
	
	@Override
	public void setLevel(byte value)
	{
		byte level = value;
		if (level > (ExperienceData.getInstance().getMaxLevel() - 1))
		{
			level = (byte) (ExperienceData.getInstance().getMaxLevel() - 1);
		}
		
		final Player player = getActiveChar();
		CharInfoTable.getInstance().setLevel(player.getObjectId(), level);
		
		if (player.isSubClassActive())
		{
			player.getSubClasses().get(player.getClassIndex()).setLevel(level);
		}
		else
		{
			super.setLevel(level);
		}
	}
	
	@Override
	public long getSp()
	{
		final Player player = getActiveChar();
		if (player.isSubClassActive())
		{
			return player.getSubClasses().get(player.getClassIndex()).getSp();
		}
		return super.getSp();
	}
	
	public long getBaseSp()
	{
		return super.getSp();
	}
	
	@Override
	public void setSp(long value)
	{
		final Player player = getActiveChar();
		if (player.isSubClassActive())
		{
			player.getSubClasses().get(player.getClassIndex()).setSp(value);
		}
		else
		{
			super.setSp(value);
		}
	}
	
	/*
	 * Return current vitality points in integer format
	 */
	public int getVitalityPoints()
	{
		final Player player = getActiveChar();
		if (player.isSubClassActive())
		{
			return Math.min(MAX_VITALITY_POINTS, player.getSubClasses().get(player.getClassIndex()).getVitalityPoints());
		}
		return Math.min(Math.max(_vitalityPoints, MIN_VITALITY_POINTS), MAX_VITALITY_POINTS);
	}
	
	public int getBaseVitalityPoints()
	{
		return Math.min(Math.max(_vitalityPoints, MIN_VITALITY_POINTS), MAX_VITALITY_POINTS);
	}
	
	public double getVitalityExpBonus()
	{
		return (getVitalityPoints() > 0) ? getMul(Stat.VITALITY_EXP_RATE, Config.RATE_VITALITY_EXP_MULTIPLIER) : 1.0;
	}
	
	public void setVitalityPoints(int value)
	{
		final Player player = getActiveChar();
		if (player.isSubClassActive())
		{
			player.getSubClasses().get(player.getClassIndex()).setVitalityPoints(value);
			return;
		}
		_vitalityPoints = Math.min(Math.max(value, MIN_VITALITY_POINTS), MAX_VITALITY_POINTS);
	}
	
	/*
	 * Set current vitality points to this value if quiet = true - does not send system messages
	 */
	public void setVitalityPoints(int value, boolean quiet)
	{
		final int points = Math.min(Math.max(value, MIN_VITALITY_POINTS), MAX_VITALITY_POINTS);
		if (points == getVitalityPoints())
		{
			return;
		}
		
		if (!quiet)
		{
			if (points < getVitalityPoints())
			{
				getActiveChar().sendPacket(SystemMessageId.YOUR_VITALITY_HAS_DECREASED);
			}
			else
			{
				getActiveChar().sendPacket(SystemMessageId.YOUR_VITALITY_HAS_INCREASED);
			}
		}
		
		setVitalityPoints(points);
		
		if (points == 0)
		{
			getActiveChar().sendPacket(SystemMessageId.YOUR_VITALITY_IS_FULLY_EXHAUSTED);
		}
		else if (points == MAX_VITALITY_POINTS)
		{
			getActiveChar().sendPacket(SystemMessageId.YOUR_VITALITY_IS_AT_MAXIMUM);
		}
		
		final Player player = getActiveChar();
		player.sendPacket(new ExVitalityPointInfo(getVitalityPoints()));
		player.broadcastUserInfo(UserInfoType.VITA_FAME);
		final Party party = player.getParty();
		if (party != null)
		{
			final PartySmallWindowUpdate partyWindow = new PartySmallWindowUpdate(player, false);
			partyWindow.addComponentType(PartySmallWindowUpdateType.VITALITY_POINTS);
			party.broadcastToPartyMembers(player, partyWindow);
		}
	}
	
	public synchronized void updateVitalityPoints(int value, boolean useRates, boolean quiet)
	{
		if ((value == 0) || !Config.ENABLE_VITALITY)
		{
			return;
		}
		
		int points = value;
		if (useRates)
		{
			if (getActiveChar().isLucky())
			{
				return;
			}
			
			if (points < 0) // vitality consumed
			{
				double consumeRate = getMul(Stat.VITALITY_CONSUME_RATE, 1);
				if (consumeRate <= 0)
				{
					return;
				}
				points *= consumeRate;
			}
			
			if (points > 0)
			{
				// vitality increased
				points *= Config.RATE_VITALITY_GAIN;
			}
			else
			{
				// vitality decreased
				points *= Config.RATE_VITALITY_LOST;
			}
		}
		
		if (points > 0)
		{
			points = Math.min(getVitalityPoints() + points, MAX_VITALITY_POINTS);
		}
		else
		{
			points = Math.max(getVitalityPoints() + points, MIN_VITALITY_POINTS);
		}
		
		if (Math.abs(points - getVitalityPoints()) <= 1e-6)
		{
			return;
		}
		
		setVitalityPoints(points);
	}
	
	public double getExpBonusMultiplier()
	{
		double bonus = 1.0;
		double vitality = 1.0;
		double bonusExp = 1.0;
		
		// Bonus from Vitality System
		vitality = getVitalityExpBonus();
		
		// Bonus exp from skills
		bonusExp = 1 + (getValue(Stat.BONUS_EXP, 0) / 100);
		if (vitality > 1.0)
		{
			bonus += (vitality - 1);
		}
		
		if (bonusExp > 1)
		{
			bonus += (bonusExp - 1);
		}
		
		// Check for abnormal bonuses
		bonus = Math.max(bonus, 1);
		if (Config.MAX_BONUS_EXP > 0)
		{
			bonus = Math.min(bonus, Config.MAX_BONUS_EXP);
		}
		
		return bonus;
	}
	
	public double getSpBonusMultiplier()
	{
		double bonus = 1.0;
		double vitality = 1.0;
		double bonusSp = 1.0;
		
		// Bonus from Vitality System
		vitality = getVitalityExpBonus();
		
		// Bonus sp from skills
		bonusSp = 1 + (getValue(Stat.BONUS_SP, 0) / 100);
		if (vitality > 1.0)
		{
			bonus += (vitality - 1);
		}
		
		if (bonusSp > 1)
		{
			bonus += (bonusSp - 1);
		}
		
		// Check for abnormal bonuses
		bonus = Math.max(bonus, 1);
		if (Config.MAX_BONUS_SP > 0)
		{
			bonus = Math.min(bonus, Config.MAX_BONUS_SP);
		}
		
		return bonus;
	}
	
	/**
	 * Gets the maximum brooch jewel count.
	 * @return the maximum brooch jewel count
	 */
	public int getBroochJewelSlots()
	{
		return (int) getValue(Stat.BROOCH_JEWELS, 0);
	}
	
	/**
	 * Gets the maximum agathion count.
	 * @return the maximum agathion count
	 */
	public int getAgathionSlots()
	{
		return (int) getValue(Stat.AGATHION_SLOTS, 0);
	}
	
	/**
	 * Gets the maximum artifact book count.
	 * @return the maximum artifact book count
	 */
	public int getArtifactSlots()
	{
		return (int) getValue(Stat.ARTIFACT_SLOTS, 0);
	}
	
	public double getElementalSpiritXpBonus()
	{
		return getValue(Stat.ELEMENTAL_SPIRIT_BONUS_EXP, 1);
	}
	
	public double getElementalSpiritPower(ElementalType type, double base)
	{
		return type == null ? 0 : getValue(type.getAttackStat(), base);
	}
	
	public double getElementalSpiritCriticalRate(int base)
	{
		return getValue(Stat.ELEMENTAL_SPIRIT_CRITICAL_RATE, base);
	}
	
	public double getElementalSpiritCriticalDamage(double base)
	{
		return getValue(Stat.ELEMENTAL_SPIRIT_CRITICAL_DAMAGE, base);
	}
	
	public double getElementalSpiritDefense(ElementalType type, double base)
	{
		return type == null ? 0 : getValue(type.getAttackStat(), base);
	}
	
	@Override
	public void recalculateStats(boolean broadcast)
	{
		if (!getActiveChar().isChangingClass())
		{
			super.recalculateStats(broadcast);
		}
	}
	
	@Override
	protected void onRecalculateStats(boolean broadcast)
	{
		super.onRecalculateStats(broadcast);
		
		final Player player = getActiveChar();
		if (player.hasAbnormalType(AbnormalType.ABILITY_CHANGE) && player.hasServitors())
		{
			player.getServitors().values().forEach(servitor -> servitor.getStat().recalculateStats(broadcast));
		}
	}
}
