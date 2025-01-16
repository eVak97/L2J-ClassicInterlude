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

import java.util.logging.Logger;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.data.xml.ExperienceData;
import org.l2jmobius.gameserver.data.xml.PetDataTable;
import org.l2jmobius.gameserver.data.xml.SkillTreeData;
import org.l2jmobius.gameserver.model.actor.Playable;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.clan.ClanMember;
import org.l2jmobius.gameserver.model.events.EventDispatcher;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayableExpChanged;
import org.l2jmobius.gameserver.model.events.returns.TerminateReturn;
import org.l2jmobius.gameserver.model.item.Weapon;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ExNewSkillToLearnByLevelUp;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

public class PlayableStat extends CreatureStat
{
	protected static final Logger LOGGER = Logger.getLogger(PlayableStat.class.getName());
	
	public PlayableStat(Playable player)
	{
		super(player);
	}
	
	public boolean addExp(long amount)
	{
		final Playable playable = getActiveChar();
		if (EventDispatcher.getInstance().hasListener(EventType.ON_PLAYABLE_EXP_CHANGED, playable))
		{
			final TerminateReturn term = EventDispatcher.getInstance().notifyEvent(new OnPlayableExpChanged(playable, getExp(), getExp() + amount), playable, TerminateReturn.class);
			if ((term != null) && term.terminate())
			{
				return false;
			}
		}
		
		if (((getExp() + amount) < 0) || ((amount > 0) && (getExp() == (getExpForLevel(getMaxLevel()) - 1))))
		{
			return true;
		}
		
		long value = amount;
		if ((getExp() + value) >= getExpForLevel(getMaxLevel()))
		{
			value = getExpForLevel(getMaxLevel()) - 1 - getExp();
		}
		
		final int oldLevel = getLevel();
		setExp(getExp() + value);
		byte minimumLevel = 1;
		if (playable.isPet())
		{
			// get minimum level from NpcTemplate
			minimumLevel = (byte) PetDataTable.getInstance().getPetMinLevel(playable.asPet().getTemplate().getId());
		}
		
		byte level = minimumLevel; // minimum level
		for (byte tmp = level; tmp <= getMaxLevel(); tmp++)
		{
			if (getExp() >= getExpForLevel(tmp))
			{
				continue;
			}
			level = --tmp;
			break;
		}
		
		if ((level != getLevel()) && (level >= minimumLevel))
		{
			addLevel((byte) (level - getLevel()));
		}
		
		byte newLevel = getLevel();
		if ((newLevel > oldLevel) && playable.isPlayer())
		{
			final Player player = playable.asPlayer();
			if (SkillTreeData.getInstance().hasAvailableSkills(player, player.getClassId()))
			{
				player.sendPacket(ExNewSkillToLearnByLevelUp.STATIC_PACKET);
			}
			
			// Check last rewarded level - prevent reputation farming via deleveling
			int lastPledgedLevel = player.getVariables().getInt(PlayerVariables.LAST_PLEDGE_REPUTATION_LEVEL, 0);
			if (lastPledgedLevel < newLevel)
			{
				int leveledUpCount = newLevel - lastPledgedLevel;
				addReputationToClanBasedOnLevel(player, leveledUpCount);
				
				player.getVariables().set(PlayerVariables.LAST_PLEDGE_REPUTATION_LEVEL, (int) newLevel);
			}
		}
		
		return true;
	}
	
	public boolean removeExp(long amount)
	{
		long value = amount;
		if (((getExp() - value) < getExpForLevel(getLevel())) && (!Config.PLAYER_DELEVEL || (Config.PLAYER_DELEVEL && (getLevel() <= Config.DELEVEL_MINIMUM))))
		{
			value = getExp() - getExpForLevel(getLevel());
		}
		
		if ((getExp() - value) < 0)
		{
			value = getExp() - 1;
		}
		
		setExp(getExp() - value);
		byte minimumLevel = 1;
		final Playable playable = getActiveChar();
		if (playable.isPet())
		{
			// get minimum level from NpcTemplate
			minimumLevel = (byte) PetDataTable.getInstance().getPetMinLevel(playable.asPet().getTemplate().getId());
		}
		byte level = minimumLevel;
		for (byte tmp = level; tmp <= getMaxLevel(); tmp++)
		{
			if (getExp() >= getExpForLevel(tmp))
			{
				continue;
			}
			level = --tmp;
			break;
		}
		if ((level != getLevel()) && (level >= minimumLevel))
		{
			addLevel((byte) (level - getLevel()));
		}
		return true;
	}
	
	public boolean removeExpAndSp(long removeExp, long removeSp)
	{
		boolean expRemoved = false;
		boolean spRemoved = false;
		if (removeExp > 0)
		{
			expRemoved = removeExp(removeExp);
		}
		if (removeSp > 0)
		{
			spRemoved = removeSp(removeSp);
		}
		return expRemoved || spRemoved;
	}
	
	public boolean addLevel(byte amount)
	{
		byte value = amount;
		if ((getLevel() + value) > (getMaxLevel() - 1))
		{
			if (getLevel() < (getMaxLevel() - 1))
			{
				value = (byte) (getMaxLevel() - 1 - getLevel());
			}
			else
			{
				return false;
			}
		}
		
		final boolean levelIncreased = (getLevel() + value) > getLevel();
		value += getLevel();
		setLevel(value);
		
		// Sync up exp with current level
		if ((getExp() >= getExpForLevel(getLevel() + 1)) || (getExpForLevel(getLevel()) > getExp()))
		{
			setExp(getExpForLevel(getLevel()));
		}
		
		final Playable playable = getActiveChar();
		if (!levelIncreased && playable.isPlayer() && !playable.isGM() && Config.DECREASE_SKILL_LEVEL)
		{
			playable.asPlayer().checkPlayerSkills();
		}
		
		if (!levelIncreased)
		{
			return false;
		}
		
		playable.getStatus().setCurrentHp(playable.getStat().getMaxHp());
		playable.getStatus().setCurrentMp(playable.getStat().getMaxMp());
		
		return true;
	}
	
	public boolean addSp(long amount)
	{
		if (amount < 0)
		{
			LOGGER.warning("wrong usage");
			return false;
		}
		
		final long currentSp = getSp();
		if (currentSp >= Config.MAX_SP)
		{
			return false;
		}
		
		long value = amount;
		if (currentSp > (Config.MAX_SP - value))
		{
			value = Config.MAX_SP - currentSp;
		}
		
		setSp(currentSp + value);
		return true;
	}
	
	public boolean removeSp(long amount)
	{
		final long currentSp = getSp();
		if (currentSp < amount)
		{
			setSp(getSp() - currentSp);
			return true;
		}
		setSp(getSp() - amount);
		return true;
	}
	
	public long getExpForLevel(int level)
	{
		return ExperienceData.getInstance().getExpForLevel(level);
	}
	
	@Override
	public Playable getActiveChar()
	{
		return super.getActiveChar().asPlayable();
	}
	
	public int getMaxLevel()
	{
		return ExperienceData.getInstance().getMaxLevel();
	}
	
	@Override
	public int getPhysicalAttackRadius()
	{
		final Weapon weapon = getActiveChar().getActiveWeaponItem();
		return weapon != null ? weapon.getBaseAttackRadius() : super.getPhysicalAttackRadius();
	}
	
	@Override
	public int getPhysicalAttackAngle()
	{
		final Weapon weapon = getActiveChar().getActiveWeaponItem();
		return weapon != null ? weapon.getBaseAttackAngle() : super.getPhysicalAttackAngle();
	}
	
	private void addReputationToClanBasedOnLevel(Player player, int leveledUpCount)
	{
		Clan clan = player.getClan();
		if (clan == null)
		{
			return;
		}
		
		if (clan.getLevel() < 3) // When a character from clan level 3 or above increases its level, CRP are added
		{
			return;
		}
		
		int reputation = 0;
		for (int i = 0; i < leveledUpCount; i++)
		{
			int level = player.getLevel() - i;
			if ((level >= 20) && (level <= 25))
			{
				reputation += Config.LVL_UP_20_AND_25_REP_SCORE;
			}
			else if ((level >= 26) && (level <= 30))
			{
				reputation += Config.LVL_UP_26_AND_30_REP_SCORE;
			}
			else if ((level >= 31) && (level <= 35))
			{
				reputation += Config.LVL_UP_31_AND_35_REP_SCORE;
			}
			else if ((level >= 36) && (level <= 40))
			{
				reputation += Config.LVL_UP_36_AND_40_REP_SCORE;
			}
			else if ((level >= 41) && (level <= 45))
			{
				reputation += Config.LVL_UP_41_AND_45_REP_SCORE;
			}
			else if ((level >= 46) && (level <= 50))
			{
				reputation += Config.LVL_UP_46_AND_50_REP_SCORE;
			}
			else if ((level >= 51) && (level <= 55))
			{
				reputation += Config.LVL_UP_51_AND_55_REP_SCORE;
			}
			else if ((level >= 56) && (level <= 60))
			{
				reputation += Config.LVL_UP_56_AND_60_REP_SCORE;
			}
			else if ((level >= 61) && (level <= 65))
			{
				reputation += Config.LVL_UP_61_AND_65_REP_SCORE;
			}
			else if ((level >= 66) && (level <= 70))
			{
				reputation += Config.LVL_UP_66_AND_70_REP_SCORE;
			}
			else if ((level >= 71) && (level <= 75))
			{
				reputation += Config.LVL_UP_71_AND_75_REP_SCORE;
			}
			else if ((level >= 76) && (level <= 80))
			{
				reputation += Config.LVL_UP_76_AND_80_REP_SCORE;
			}
			else if ((level >= 81) && (level <= 120))
			{
				reputation += Config.LVL_UP_81_PLUS_REP_SCORE;
			}
		}
		
		if (reputation == 0)
		{
			return;
		}
		
		reputation = (int) Math.ceil(reputation * Config.LVL_OBTAINED_REP_SCORE_MULTIPLIER);
		
		clan.addReputationScore(reputation);
		
		for (ClanMember member : clan.getMembers())
		{
			if (member.isOnline())
			{
				final SystemMessage sm = new SystemMessage(SystemMessageId.YOUR_CLAN_HAS_ADDED_S1_POINT_S_TO_ITS_CLAN_REPUTATION);
				sm.addInt(reputation);
				member.getPlayer().sendPacket(sm);
			}
		}
	}
}
