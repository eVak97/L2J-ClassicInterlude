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
package org.l2jmobius.gameserver.taskmanager;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jmobius.Config;
import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.data.xml.ActionData;
import org.l2jmobius.gameserver.data.xml.PetSkillData;
import org.l2jmobius.gameserver.handler.IItemHandler;
import org.l2jmobius.gameserver.handler.IPlayerActionHandler;
import org.l2jmobius.gameserver.handler.ItemHandler;
import org.l2jmobius.gameserver.handler.PlayerActionHandler;
import org.l2jmobius.gameserver.model.ActionDataHolder;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Playable;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.Summon;
import org.l2jmobius.gameserver.model.actor.instance.Guard;
import org.l2jmobius.gameserver.model.actor.transform.TransformTemplate;
import org.l2jmobius.gameserver.model.effects.AbstractEffect;
import org.l2jmobius.gameserver.model.effects.EffectType;
import org.l2jmobius.gameserver.model.events.EventDispatcher;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.impl.item.OnItemUse;
import org.l2jmobius.gameserver.model.holders.AttachSkillHolder;
import org.l2jmobius.gameserver.model.holders.ItemSkillHolder;
import org.l2jmobius.gameserver.model.item.EtcItem;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.skill.AbnormalType;
import org.l2jmobius.gameserver.model.skill.BuffInfo;
import org.l2jmobius.gameserver.model.skill.EffectScope;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.targets.AffectScope;
import org.l2jmobius.gameserver.model.skill.targets.TargetType;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.SystemMessageId;

/**
 * @author Mobius
 */
public class AutoUseTaskManager
{
	private static final Set<Set<Player>> POOLS = ConcurrentHashMap.newKeySet();
	private static final int POOL_SIZE = 200;
	private static final int TASK_DELAY = 300;
	private static final int REUSE_MARGIN_TIME = 3;
	
	protected AutoUseTaskManager()
	{
	}
	
	private class AutoUse implements Runnable
	{
		private final Set<Player> _players;
		
		public AutoUse(Set<Player> players)
		{
			_players = players;
		}
		
		@Override
		public void run()
		{
			if (_players.isEmpty())
			{
				return;
			}
			
			for (Player player : _players)
			{
				if (player.getAutoUseSettings().isEmpty() || !player.isOnline() || (player.isInOfflineMode() && !player.isOfflinePlay()))
				{
					stopAutoUseTask(player);
					continue;
				}
				
				if (player.isSitting() || player.hasBlockActions() || player.isControlBlocked() || player.isAlikeDead() || player.isMounted() || (player.isTransformed() && player.getTransformation().get().isRiding()))
				{
					continue;
				}
				
				final boolean isInPeaceZone = player.isInsideZone(ZoneId.PEACE) || player.isInsideZone(ZoneId.SAYUNE);
				
				if (Config.ENABLE_AUTO_ITEM && !isInPeaceZone)
				{
					ITEMS: for (Integer itemId : player.getAutoUseSettings().getAutoSupplyItems())
					{
						if (player.isTeleporting())
						{
							break ITEMS;
						}
						
						final Item item = player.getInventory().getItemByItemId(itemId.intValue());
						if (item == null)
						{
							player.getAutoUseSettings().getAutoSupplyItems().remove(itemId);
							continue ITEMS;
						}
						
						final ItemTemplate it = item.getTemplate();
						if (it != null)
						{
							if (!it.checkCondition(player, player, false))
							{
								continue ITEMS;
							}
							
							final List<ItemSkillHolder> skills = it.getAllSkills();
							if (skills != null)
							{
								for (ItemSkillHolder itemSkillHolder : skills)
								{
									final Skill skill = itemSkillHolder.getSkill();
									if (player.isAffectedBySkill(skill.getId()) || player.hasSkillReuse(skill.getReuseHashCode()) || !skill.checkCondition(player, player, false))
									{
										continue ITEMS;
									}
								}
							}
						}
						
						final int reuseDelay = item.getReuseDelay();
						if ((reuseDelay <= 0) || (player.getItemRemainingReuseTime(item.getObjectId()) <= 0))
						{
							final EtcItem etcItem = item.getEtcItem();
							final IItemHandler handler = ItemHandler.getInstance().getHandler(etcItem);
							if ((handler != null) && handler.useItem(player, item, false))
							{
								if (reuseDelay > 0)
								{
									player.addTimeStampItem(item, reuseDelay);
								}
								
								// Notify events.
								if (EventDispatcher.getInstance().hasListener(EventType.ON_ITEM_USE, item.getTemplate()))
								{
									EventDispatcher.getInstance().notifyEventAsync(new OnItemUse(player, item), item.getTemplate());
								}
							}
						}
					}
				}
				
				if (Config.ENABLE_AUTO_POTION && !isInPeaceZone && (player.getCurrentHpPercent() < player.getAutoPlaySettings().getAutoPotionPercent()))
				{
					final int itemId = player.getAutoUseSettings().getAutoPotionItem();
					if (itemId > 0)
					{
						final Item item = player.getInventory().getItemByItemId(itemId);
						if (item == null)
						{
							player.getAutoUseSettings().setAutoPotionItem(0);
						}
						else
						{
							final int reuseDelay = item.getReuseDelay();
							if ((reuseDelay <= 0) || (player.getItemRemainingReuseTime(item.getObjectId()) <= 0))
							{
								final EtcItem etcItem = item.getEtcItem();
								final IItemHandler handler = ItemHandler.getInstance().getHandler(etcItem);
								if ((handler != null) && handler.useItem(player, item, false))
								{
									if (reuseDelay > 0)
									{
										player.addTimeStampItem(item, reuseDelay);
									}
									
									// Notify events.
									if (EventDispatcher.getInstance().hasListener(EventType.ON_ITEM_USE, item.getTemplate()))
									{
										EventDispatcher.getInstance().notifyEventAsync(new OnItemUse(player, item), item.getTemplate());
									}
								}
							}
						}
					}
				}
				
				if (Config.ENABLE_AUTO_SKILL)
				{
					BUFFS: for (Integer skillId : player.getAutoUseSettings().getAutoBuffs())
					{
						// Fixes start area issue.
						if (isInPeaceZone)
						{
							break BUFFS;
						}
						
						// Already casting.
						if (player.isCastingNow())
						{
							break BUFFS;
						}
						
						// Attacking.
						if (player.isAttackingNow())
						{
							break BUFFS;
						}
						
						// Player is teleporting.
						if (player.isTeleporting())
						{
							break BUFFS;
						}
						
						Playable pet = null;
						Skill skill = player.getKnownSkill(skillId.intValue());
						if (skill == null)
						{
							if (player.hasServitors())
							{
								SUMMON_SEARCH: for (Summon summon : player.getServitors().values())
								{
									skill = summon.getKnownSkill(skillId.intValue());
									if (skill != null)
									{
										pet = summon;
										break SUMMON_SEARCH;
									}
								}
							}
							if ((skill == null) && player.hasPet())
							{
								pet = player.getPet();
								skill = pet.getKnownSkill(skillId.intValue());
							}
							if (skill == null)
							{
								player.getAutoUseSettings().getAutoBuffs().remove(skillId);
								continue BUFFS;
							}
						}
						
						// Buff use check.
						final WorldObject target = player.getTarget();
						if (!canCastBuff(player, target, skill))
						{
							continue BUFFS;
						}
						
						ATTACH_SEARCH: for (AttachSkillHolder holder : skill.getAttachSkills())
						{
							if (player.isAffectedBySkill(holder.getRequiredSkillId()))
							{
								skill = holder.getSkill();
								break ATTACH_SEARCH;
							}
						}
						
						// Playable target cast.
						final Playable caster = pet != null ? pet : player;
						if ((target != null) && (target.isPlayable()))
						{
							final Player targetPlayer = target.asPlayer();
							if (((targetPlayer.getPvpFlag() == 0) && (targetPlayer.getReputation() >= 0)) || (targetPlayer.getParty() == caster.getParty()))
							{
								caster.doCast(skill);
							}
							else
							{
								if (!caster.getEffectList().isAffectedBySkill(skill.getId()))
								{
									final WorldObject savedTarget = target;
									caster.setTarget(caster);
									caster.doCast(skill);
									caster.setTarget(savedTarget);
								}
							}
						}
						else // Target self, cast and re-target.
						{
							final WorldObject savedTarget = target;
							caster.setTarget(caster);
							caster.doCast(skill);
							caster.setTarget(savedTarget);
						}
					}
					
					// Continue when auto play is not enabled.
					if (!player.isAutoPlaying())
					{
						continue;
					}
					
					final int count = player.getAutoUseSettings().getAutoSkills().size();
					SKILLS: for (int i = 0; i < count; i++)
					{
						// Already casting.
						if (player.isCastingNow())
						{
							break SKILLS;
						}
						
						// Player is teleporting.
						if (player.isTeleporting())
						{
							break SKILLS;
						}
						
						// Acquire next skill.
						Playable pet = null;
						final WorldObject target = player.getTarget();
						final Integer skillId = player.getAutoUseSettings().getNextSkillId();
						Skill skill = player.getKnownSkill(skillId);
						if (skill == null)
						{
							if (player.hasServitors())
							{
								SUMMON_SEARCH: for (Summon summon : player.getServitors().values())
								{
									skill = summon.getKnownSkill(skillId.intValue());
									if (skill == null)
									{
										skill = PetSkillData.getInstance().getKnownSkill(summon, skillId);
									}
									if (skill != null)
									{
										pet = summon;
										pet.setTarget(target);
										break SUMMON_SEARCH;
									}
								}
							}
							if ((skill == null) && player.hasPet())
							{
								pet = player.getPet();
								skill = pet.getKnownSkill(skillId);
								if (skill == null)
								{
									skill = PetSkillData.getInstance().getKnownSkill(pet.asSummon(), skillId);
								}
								if (pet.isSkillDisabled(skill))
								{
									player.getAutoUseSettings().incrementSkillOrder();
									break SKILLS;
								}
							}
							if (skill == null)
							{
								player.getAutoUseSettings().getAutoSkills().remove(skillId);
								player.getAutoUseSettings().resetSkillOrder();
								break SKILLS;
							}
						}
						
						// Casting on self stops movement.
						if (target == player)
						{
							break SKILLS;
						}
						
						// Check bad skill target.
						if ((target == null) || target.asCreature().isDead())
						{
							break SKILLS;
						}
						
						// Peace zone and auto attackable checks.
						if (target.isInsideZone(ZoneId.PEACE) || !target.isAutoAttackable(player))
						{
							break SKILLS;
						}
						
						// Do not attack guards.
						if (target instanceof Guard)
						{
							final int targetMode = player.getAutoPlaySettings().getNextTargetMode();
							if ((targetMode != 3 /* NPC */) && (targetMode != 0 /* Any Target */))
							{
								break SKILLS;
							}
						}
						
						// Increment skill order.
						player.getAutoUseSettings().incrementSkillOrder();
						
						// Skill use check.
						final Playable caster = pet != null ? pet : player;
						if (!canUseMagic(caster, target, skill))
						{
							continue SKILLS;
						}
						
						// Use the skill.
						caster.useMagic(skill, null, true, false);
						
						break SKILLS;
					}
					
					ACTIONS: for (Integer actionId : player.getAutoUseSettings().getAutoActions())
					{
						final BuffInfo info = player.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
						if (info != null)
						{
							for (AbstractEffect effect : info.getEffects())
							{
								if (!effect.checkCondition(actionId))
								{
									player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
									break ACTIONS;
								}
							}
						}
						
						// Do not allow to do some action if player is transformed.
						if (player.isTransformed())
						{
							final TransformTemplate transformTemplate = player.getTransformation().get().getTemplate(player);
							final int[] allowedActions = transformTemplate.getBasicActionList();
							if ((allowedActions == null) || (Arrays.binarySearch(allowedActions, actionId) < 0))
							{
								continue ACTIONS;
							}
						}
						
						final ActionDataHolder actionHolder = ActionData.getInstance().getActionData(actionId);
						if (actionHolder != null)
						{
							final IPlayerActionHandler actionHandler = PlayerActionHandler.getInstance().getHandler(actionHolder.getHandler());
							if (actionHandler != null)
							{
								if (!actionHandler.isPetAction())
								{
									actionHandler.useAction(player, actionHolder, false, false);
								}
								else
								{
									final Summon summon = player.getAnyServitor();
									if ((summon != null) && !summon.isAlikeDead())
									{
										final Skill skill = summon.getKnownSkill(actionHolder.getOptionId());
										if ((skill != null) && !canSummonCastSkill(player, summon, skill))
										{
											continue ACTIONS;
										}
										
										actionHandler.useAction(player, actionHolder, false, false);
									}
								}
							}
						}
					}
				}
			}
		}
		
		private boolean canCastBuff(Player player, WorldObject target, Skill skill)
		{
			// Summon check.
			if ((skill.getAffectScope() == AffectScope.SUMMON_EXCEPT_MASTER) || (skill.getTargetType() == TargetType.SUMMON))
			{
				if (!player.hasServitors())
				{
					return false;
				}
				int occurrences = 0;
				for (Summon servitor : player.getServitors().values())
				{
					if (servitor.isAffectedBySkill(skill.getId()))
					{
						occurrences++;
					}
				}
				if (occurrences == player.getServitors().size())
				{
					return false;
				}
			}
			
			if ((target != null) && target.isCreature() && target.asCreature().isAlikeDead() && (skill.getTargetType() != TargetType.SELF) && (skill.getTargetType() != TargetType.NPC_BODY) && (skill.getTargetType() != TargetType.PC_BODY))
			{
				return false;
			}
			
			final Playable playableTarget = (target == null) || !target.isPlayable() || (skill.getTargetType() == TargetType.SELF) ? player : target.asPlayable();
			if ((player != playableTarget) && (player.calculateDistance3D(playableTarget) > skill.getCastRange()))
			{
				return false;
			}
			
			if (!canUseMagic(player, playableTarget, skill))
			{
				return false;
			}
			
			final BuffInfo buffInfo = playableTarget.getEffectList().getBuffInfoBySkillId(skill.getId());
			final BuffInfo abnormalBuffInfo = playableTarget.getEffectList().getFirstBuffInfoByAbnormalType(skill.getAbnormalType());
			if (abnormalBuffInfo != null)
			{
				if (buffInfo != null)
				{
					return (abnormalBuffInfo.getSkill().getId() == buffInfo.getSkill().getId()) && ((buffInfo.getTime() <= REUSE_MARGIN_TIME) || (buffInfo.getSkill().getLevel() < skill.getLevel()));
				}
				return (abnormalBuffInfo.getSkill().getAbnormalLevel() < skill.getAbnormalLevel()) || abnormalBuffInfo.isAbnormalType(AbnormalType.NONE);
			}
			return buffInfo == null;
		}
		
		private boolean canUseMagic(Playable playable, WorldObject target, Skill skill)
		{
			if ((skill.getItemConsumeCount() > 0) && (playable.getInventory().getInventoryItemCount(skill.getItemConsumeId(), -1) < skill.getItemConsumeCount()))
			{
				return false;
			}
			
			final int mpConsume = skill.getMpInitialConsume() + skill.getMpConsume();
			if ((mpConsume > 0) && (playable.getCurrentMp() < mpConsume))
			{
				return false;
			}
			
			// Check if monster is spoiled to avoid Spoil (254) skill recast.
			if ((skill.getId() == 254) && (target != null) && target.isMonster() && target.asMonster().isSpoiled())
			{
				return false;
			}
			
			for (AttachSkillHolder holder : skill.getAttachSkills())
			{
				if (playable.isAffectedBySkill(holder.getRequiredSkillId()) //
					&& (playable.hasSkillReuse(holder.getSkill().getReuseHashCode()) || playable.isAffectedBySkill(holder)))
				{
					return false;
				}
			}
			
			return !playable.isSkillDisabled(skill) && skill.checkCondition(playable, target, false);
		}
		
		private boolean canSummonCastSkill(Player player, Summon summon, Skill skill)
		{
			if (skill.isBad() && (player.getTarget() == null))
			{
				return false;
			}
			
			final int mpConsume = skill.getMpConsume() + skill.getMpInitialConsume();
			if ((((mpConsume != 0) && (mpConsume > (int) Math.floor(summon.getCurrentMp()))) || ((skill.getHpConsume() != 0) && (skill.getHpConsume() > (int) Math.floor(summon.getCurrentHp())))))
			{
				return false;
			}
			
			if (summon.isSkillDisabled(skill))
			{
				return false;
			}
			
			if (((player.getTarget() != null) && !skill.checkCondition(summon, player.getTarget(), false)) || ((player.getTarget() == null) && !skill.checkCondition(summon, player, false)))
			{
				return false;
			}
			
			if ((skill.getItemConsumeCount() > 0) && (summon.getInventory().getInventoryItemCount(skill.getItemConsumeId(), -1) < skill.getItemConsumeCount()))
			{
				return false;
			}
			
			if (skill.getTargetType().equals(TargetType.SELF) || skill.getTargetType().equals(TargetType.SUMMON))
			{
				final BuffInfo summonInfo = summon.getEffectList().getBuffInfoBySkillId(skill.getId());
				return (summonInfo != null) && (summonInfo.getTime() >= REUSE_MARGIN_TIME);
			}
			
			if ((skill.getEffects(EffectScope.GENERAL) != null) && skill.getEffects(EffectScope.GENERAL).stream().anyMatch(a -> a.getEffectType().equals(EffectType.MANAHEAL_BY_LEVEL)) && (player.getCurrentMpPercent() > 80))
			{
				return false;
			}
			
			final BuffInfo buffInfo = player.getEffectList().getBuffInfoBySkillId(skill.getId());
			final BuffInfo abnormalBuffInfo = player.getEffectList().getFirstBuffInfoByAbnormalType(skill.getAbnormalType());
			if (abnormalBuffInfo != null)
			{
				if (buffInfo != null)
				{
					return (abnormalBuffInfo.getSkill().getId() == buffInfo.getSkill().getId()) && ((buffInfo.getTime() <= REUSE_MARGIN_TIME) || (buffInfo.getSkill().getLevel() < skill.getLevel()));
				}
				return (abnormalBuffInfo.getSkill().getAbnormalLevel() < skill.getAbnormalLevel()) || abnormalBuffInfo.isAbnormalType(AbnormalType.NONE);
			}
			
			return true;
		}
	}
	
	public synchronized void startAutoUseTask(Player player)
	{
		for (Set<Player> pool : POOLS)
		{
			if (pool.contains(player))
			{
				return;
			}
		}
		
		for (Set<Player> pool : POOLS)
		{
			if (pool.size() < POOL_SIZE)
			{
				pool.add(player);
				return;
			}
		}
		
		final Set<Player> pool = ConcurrentHashMap.newKeySet(POOL_SIZE);
		pool.add(player);
		ThreadPool.schedulePriorityTaskAtFixedRate(new AutoUse(pool), TASK_DELAY, TASK_DELAY);
		POOLS.add(pool);
	}
	
	public void stopAutoUseTask(Player player)
	{
		player.getAutoUseSettings().resetSkillOrder();
		if (player.getAutoUseSettings().isEmpty() || !player.isOnline() || (player.isInOfflineMode() && !player.isOfflinePlay()))
		{
			for (Set<Player> pool : POOLS)
			{
				if (pool.remove(player))
				{
					return;
				}
			}
		}
	}
	
	public void addAutoSupplyItem(Player player, int itemId)
	{
		player.getAutoUseSettings().getAutoSupplyItems().add(itemId);
		startAutoUseTask(player);
	}
	
	public void removeAutoSupplyItem(Player player, int itemId)
	{
		player.getAutoUseSettings().getAutoSupplyItems().remove(itemId);
		stopAutoUseTask(player);
	}
	
	public void setAutoPotionItem(Player player, int itemId)
	{
		player.getAutoUseSettings().setAutoPotionItem(itemId);
		startAutoUseTask(player);
	}
	
	public void removeAutoPotionItem(Player player)
	{
		player.getAutoUseSettings().setAutoPotionItem(0);
		stopAutoUseTask(player);
	}
	
	public void addAutoBuff(Player player, int skillId)
	{
		player.getAutoUseSettings().getAutoBuffs().add(skillId);
		startAutoUseTask(player);
	}
	
	public void removeAutoBuff(Player player, int skillId)
	{
		player.getAutoUseSettings().getAutoBuffs().remove(skillId);
		stopAutoUseTask(player);
	}
	
	public void addAutoSkill(Player player, Integer skillId)
	{
		player.getAutoUseSettings().getAutoSkills().add(skillId);
		startAutoUseTask(player);
	}
	
	public void removeAutoSkill(Player player, Integer skillId)
	{
		player.getAutoUseSettings().getAutoSkills().remove(skillId);
		stopAutoUseTask(player);
	}
	
	public void addAutoAction(Player player, int actionId)
	{
		player.getAutoUseSettings().getAutoActions().add(actionId);
		startAutoUseTask(player);
	}
	
	public void removeAutoAction(Player player, int actionId)
	{
		player.getAutoUseSettings().getAutoActions().remove(actionId);
		stopAutoUseTask(player);
	}
	
	public static AutoUseTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AutoUseTaskManager INSTANCE = new AutoUseTaskManager();
	}
}
