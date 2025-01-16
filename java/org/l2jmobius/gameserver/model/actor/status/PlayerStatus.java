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
package org.l2jmobius.gameserver.model.actor.status;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.ai.CtrlIntention;
import org.l2jmobius.gameserver.data.xml.NpcNameLocalisationData;
import org.l2jmobius.gameserver.enums.PrivateStoreType;
import org.l2jmobius.gameserver.enums.SkillFinishType;
import org.l2jmobius.gameserver.instancemanager.DuelManager;
import org.l2jmobius.gameserver.model.Duel;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.Summon;
import org.l2jmobius.gameserver.model.actor.stat.PlayerStat;
import org.l2jmobius.gameserver.model.effects.EffectFlag;
import org.l2jmobius.gameserver.model.skill.AbnormalType;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.stats.Formulas;
import org.l2jmobius.gameserver.model.stats.Stat;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ActionFailed;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import org.l2jmobius.gameserver.util.Util;

public class PlayerStatus extends PlayableStatus
{
	private double _currentCp = 0; // Current CP of the Player
	
	public PlayerStatus(Player player)
	{
		super(player);
	}
	
	@Override
	public void reduceCp(int value)
	{
		if (_currentCp > value)
		{
			setCurrentCp(_currentCp - value);
		}
		else
		{
			setCurrentCp(0);
		}
	}
	
	@Override
	public void reduceHp(double value, Creature attacker)
	{
		reduceHp(value, attacker, null, true, false, false, false);
	}
	
	@Override
	public void reduceHp(double value, Creature attacker, boolean awake, boolean isDOT, boolean isHPConsumption)
	{
		reduceHp(value, attacker, null, awake, isDOT, isHPConsumption, false);
	}
	
	public void reduceHp(double value, Creature attacker, Skill skill, boolean awake, boolean isDOT, boolean isHPConsumption, boolean ignoreCP)
	{
		final Player player = getActiveChar();
		if (player.isDead())
		{
			return;
		}
		
		// If OFFLINE_MODE_NO_DAMAGE is enabled and player is offline and he is in store/craft mode, no damage is taken.
		if (Config.OFFLINE_MODE_NO_DAMAGE && (player.getClient() != null) && player.getClient().isDetached() && ((Config.OFFLINE_TRADE_ENABLE && ((player.getPrivateStoreType() == PrivateStoreType.SELL) || (player.getPrivateStoreType() == PrivateStoreType.BUY))) || (Config.OFFLINE_CRAFT_ENABLE && (player.isCrafting() || (player.getPrivateStoreType() == PrivateStoreType.MANUFACTURE)))))
		{
			return;
		}
		
		if (player.isHpBlocked() && !(isDOT || isHPConsumption))
		{
			return;
		}
		
		if (player.isAffected(EffectFlag.DUELIST_FURY) && !attacker.isAffected(EffectFlag.FACEOFF))
		{
			return;
		}
		
		if (!isHPConsumption)
		{
			if (awake)
			{
				player.stopEffectsOnDamage();
			}
			// Attacked players in craft/shops stand up.
			if (player.isCrafting() || player.isInStoreMode())
			{
				player.setPrivateStoreType(PrivateStoreType.NONE);
				player.standUp();
				player.broadcastUserInfo();
			}
			else if (player.isSitting())
			{
				player.standUp();
			}
			
			if (!isDOT)
			{
				if (Formulas.calcStunBreak(player))
				{
					player.stopStunning(true);
				}
				if (Formulas.calcRealTargetBreak())
				{
					player.getEffectList().stopEffects(AbnormalType.REAL_TARGET);
				}
			}
		}
		
		double amount = value;
		int fullValue = (int) amount;
		int tDmg = 0;
		int mpDam = 0;
		if ((attacker != null) && (attacker != player))
		{
			final Player attackerPlayer = attacker.asPlayer();
			if (attackerPlayer != null)
			{
				if (attackerPlayer.isGM() && !attackerPlayer.getAccessLevel().canGiveDamage())
				{
					return;
				}
				
				if (player.isInDuel())
				{
					if (player.getDuelState() == Duel.DUELSTATE_DEAD)
					{
						return;
					}
					else if (player.getDuelState() == Duel.DUELSTATE_WINNER)
					{
						return;
					}
					
					// cancel duel if player got hit by another player, that is not part of the duel
					if (attackerPlayer.getDuelId() != player.getDuelId())
					{
						player.setDuelState(Duel.DUELSTATE_INTERRUPTED);
					}
				}
			}
			
			// Check and calculate transfered damage
			final PlayerStat stat = player.getStat();
			final Summon summon = player.getFirstServitor();
			if ((summon != null) && Util.checkIfInRange(1000, player, summon, true))
			{
				tDmg = ((int) amount * (int) stat.getValue(Stat.TRANSFER_DAMAGE_SUMMON_PERCENT, 0)) / 100;
				
				// Only transfer dmg up to current HP, it should not be killed
				tDmg = Math.min((int) summon.getCurrentHp() - 1, tDmg);
				if (tDmg > 0)
				{
					summon.reduceCurrentHp(tDmg, attacker, null);
					amount -= tDmg;
					fullValue = (int) amount; // reduce the announced value here as player will get a message about summon damage
				}
			}
			
			mpDam = ((int) amount * (int) stat.getValue(Stat.MANA_SHIELD_PERCENT, 0)) / 100;
			if (mpDam > 0)
			{
				mpDam = (int) (amount - mpDam);
				if (mpDam > player.getCurrentMp())
				{
					player.sendPacket(SystemMessageId.MP_BECAME_0_AND_THE_ARCANE_SHIELD_IS_DISAPPEARING);
					player.stopSkillEffects(SkillFinishType.REMOVED, 1556);
					amount = mpDam - player.getCurrentMp();
					player.setCurrentMp(0);
				}
				else
				{
					player.reduceCurrentMp(mpDam);
					final SystemMessage smsg = new SystemMessage(SystemMessageId.ARCANE_SHIELD_DECREASED_YOUR_MP_BY_S1_INSTEAD_OF_HP);
					smsg.addInt(mpDam);
					player.sendPacket(smsg);
					return;
				}
			}
			
			final Player caster = player.getTransferingDamageTo();
			if ((caster != null) && (player.getParty() != null) && Util.checkIfInRange(1000, player, caster, true) && !caster.isDead() && (player != caster) && player.getParty().getMembers().contains(caster))
			{
				int transferDmg = 0;
				transferDmg = ((int) amount * (int) stat.getValue(Stat.TRANSFER_DAMAGE_TO_PLAYER, 0)) / 100;
				transferDmg = Math.min((int) caster.getCurrentHp() - 1, transferDmg);
				if (transferDmg > 0)
				{
					int membersInRange = 0;
					for (Player member : caster.getParty().getMembers())
					{
						if (Util.checkIfInRange(1000, member, caster, false) && (member != caster))
						{
							membersInRange++;
						}
					}
					
					if ((attacker.isPlayable() || attacker.isFakePlayer()) && (caster.getCurrentCp() > 0))
					{
						if (caster.getCurrentCp() > transferDmg)
						{
							caster.getStatus().reduceCp(transferDmg);
						}
						else
						{
							transferDmg = (int) (transferDmg - caster.getCurrentCp());
							caster.getStatus().reduceCp((int) caster.getCurrentCp());
						}
					}
					
					if (membersInRange > 0)
					{
						caster.reduceCurrentHp(transferDmg / membersInRange, attacker, null);
						amount -= transferDmg;
						fullValue = (int) amount;
					}
				}
			}
			
			if (!ignoreCP && (attacker.isPlayable() || attacker.isFakePlayer()))
			{
				if (_currentCp >= amount)
				{
					setCurrentCp(_currentCp - amount); // Set Cp to diff of Cp vs value
					amount = 0; // No need to subtract anything from Hp
				}
				else
				{
					amount -= _currentCp; // Get diff from value vs Cp; will apply diff to Hp
					setCurrentCp(0, false); // Set Cp to 0
				}
			}
			
			if ((fullValue > 0) && !isDOT)
			{
				// Send a System Message to the Player
				SystemMessage smsg = new SystemMessage(SystemMessageId.C1_HAS_RECEIVED_S3_DAMAGE_FROM_C2);
				smsg.addString(player.getName());
				
				// Localisation related.
				String targetName = attacker.getName();
				if (Config.MULTILANG_ENABLE && attacker.isNpc())
				{
					final String[] localisation = NpcNameLocalisationData.getInstance().getLocalisation(player.getLang(), attacker.getId());
					if (localisation != null)
					{
						targetName = localisation[0];
					}
				}
				
				smsg.addString(targetName);
				smsg.addInt(fullValue);
				smsg.addPopup(player.getObjectId(), attacker.getObjectId(), -fullValue);
				player.sendPacket(smsg);
				
				if ((tDmg > 0) && (summon != null) && (attackerPlayer != null))
				{
					smsg = new SystemMessage(SystemMessageId.YOU_HAVE_DEALT_S1_DAMAGE_TO_YOUR_TARGET_AND_S2_DAMAGE_TO_THE_SERVITOR);
					smsg.addInt(fullValue);
					smsg.addInt(tDmg);
					attackerPlayer.sendPacket(smsg);
				}
			}
		}
		
		if (amount > 0)
		{
			double newHp = Math.max(getCurrentHp() - amount, player.isUndying() ? 1 : 0);
			if (newHp <= 0)
			{
				if (player.isInDuel())
				{
					player.disableAllSkills();
					stopHpMpRegeneration();
					if (attacker != null)
					{
						attacker.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
						attacker.sendPacket(ActionFailed.STATIC_PACKET);
					}
					// let the DuelManager know of his defeat
					DuelManager.getInstance().onPlayerDefeat(player);
					newHp = 1;
				}
				else
				{
					newHp = 0;
				}
			}
			setCurrentHp(newHp);
		}
		
		if ((player.getCurrentHp() < 0.5) && !isHPConsumption && !player.isUndying())
		{
			player.abortAttack();
			player.abortCast();
			
			if (player.isInOlympiadMode())
			{
				stopHpMpRegeneration();
				player.setDead(true);
				player.setIsPendingRevive(true);
				final Summon pet = player.getPet();
				if (pet != null)
				{
					pet.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
				}
				player.getServitors().values().forEach(s -> s.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE));
				return;
			}
			
			player.doDie(attacker);
		}
	}
	
	@Override
	public double getCurrentCp()
	{
		return _currentCp;
	}
	
	@Override
	public void setCurrentCp(double newCp)
	{
		setCurrentCp(newCp, true);
	}
	
	@Override
	public void setCurrentCp(double value, boolean broadcastPacket)
	{
		// Get the Max CP of the Creature
		final int currentCp = (int) _currentCp;
		final Player player = getActiveChar();
		final int maxCp = player.getStat().getMaxCp();
		
		synchronized (this)
		{
			if (player.isDead())
			{
				return;
			}
			
			final double newCp = Math.max(0, value);
			if (newCp >= maxCp)
			{
				// Set the RegenActive flag to false
				_currentCp = maxCp;
				_flagsRegenActive &= ~REGEN_FLAG_CP;
				
				// Stop the HP/MP/CP Regeneration task
				if (_flagsRegenActive == 0)
				{
					stopHpMpRegeneration();
				}
			}
			else
			{
				// Set the RegenActive flag to true
				_currentCp = newCp;
				_flagsRegenActive |= REGEN_FLAG_CP;
				
				// Start the HP/MP/CP Regeneration task with Medium priority
				startHpMpRegeneration();
			}
		}
		
		// Send the Server->Client packet StatusUpdate with current HP and MP to all other Player to inform
		if ((currentCp != _currentCp) && broadcastPacket)
		{
			player.broadcastStatusUpdate();
		}
	}
	
	@Override
	protected void doRegeneration()
	{
		final Player player = getActiveChar();
		final PlayerStat stat = player.getStat();
		
		// Modify the current CP of the Creature and broadcast Server->Client packet StatusUpdate
		if (_currentCp < stat.getMaxRecoverableCp())
		{
			setCurrentCp(_currentCp + stat.getValue(Stat.REGENERATE_CP_RATE), false);
		}
		
		// Modify the current HP of the Creature and broadcast Server->Client packet StatusUpdate
		if (getCurrentHp() < stat.getMaxRecoverableHp())
		{
			setCurrentHp(getCurrentHp() + stat.getValue(Stat.REGENERATE_HP_RATE), false);
		}
		
		// Modify the current MP of the Creature and broadcast Server->Client packet StatusUpdate
		if (getCurrentMp() < stat.getMaxRecoverableMp())
		{
			setCurrentMp(getCurrentMp() + stat.getValue(Stat.REGENERATE_MP_RATE), false);
		}
		
		player.broadcastStatusUpdate(); // send the StatusUpdate packet
	}
	
	@Override
	public Player getActiveChar()
	{
		return super.getActiveChar().asPlayer();
	}
}
