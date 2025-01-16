/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.gameserver.network.clientpackets.autoplay;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.data.xml.ActionData;
import org.l2jmobius.gameserver.enums.ShortcutType;
import org.l2jmobius.gameserver.handler.IPlayerActionHandler;
import org.l2jmobius.gameserver.handler.PlayerActionHandler;
import org.l2jmobius.gameserver.model.ActionDataHolder;
import org.l2jmobius.gameserver.model.ShortCuts;
import org.l2jmobius.gameserver.model.Shortcut;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.Summon;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.taskmanager.AutoUseTaskManager;

/**
 * @author Mobius
 */
public class ExRequestActivateAutoShortcut extends ClientPacket
{
	private int _slot;
	private int _page;
	private boolean _active;
	
	@Override
	protected void readImpl()
	{
		final int position = readShort();
		_slot = position % ShortCuts.MAX_SHORTCUTS_PER_BAR;
		_page = position / ShortCuts.MAX_SHORTCUTS_PER_BAR;
		_active = readByte() == 1;
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (_active)
		{
			player.addAutoShortcut(_slot, _page);
		}
		else
		{
			player.removeAutoShortcut(_slot, _page);
		}
		
		Item item = null;
		Skill skill = null;
		
		if ((_slot == 3) && (_page == 5461) && _active)
		{
			for (int i = 0; i < 12; i++)
			{
				final Shortcut autoUseAllSupply = player.getShortCut(i, 22);
				if (autoUseAllSupply == null)
				{
					continue;
				}
				
				final Item itemAll = player.getInventory().getItemByObjectId(autoUseAllSupply.getId());
				if (itemAll != null)
				{
					player.addAutoShortcut(i, 22);
					AutoUseTaskManager.getInstance().addAutoSupplyItem(player, itemAll.getId());
				}
			}
			return;
		}
		else if ((_slot == 3) && (_page == 5461) && !_active)
		{
			for (int i = 0; i < 12; i++)
			{
				final Shortcut autoUseAllSupply = player.getShortCut(i, 22);
				if (autoUseAllSupply == null)
				{
					continue;
				}
				
				final Item itemAll = player.getInventory().getItemByObjectId(autoUseAllSupply.getId());
				if (itemAll != null)
				{
					player.removeAutoShortcut(i, 22);
					AutoUseTaskManager.getInstance().removeAutoSupplyItem(player, itemAll.getId());
				}
			}
			return;
		}
		
		final Shortcut shortcut = player.getShortCut(_slot, _page);
		if ((shortcut == null))
		{
			return;
		}
		
		if (shortcut.getType() == ShortcutType.SKILL)
		{
			skill = player.getKnownSkill(shortcut.getId());
			if (skill == null)
			{
				if (player.hasServitors())
				{
					for (Summon summon : player.getServitors().values())
					{
						skill = summon.getKnownSkill(shortcut.getId());
						if (skill != null)
						{
							break;
						}
					}
				}
				if ((skill == null) && player.hasPet())
				{
					skill = player.getPet().getKnownSkill(shortcut.getId());
				}
			}
		}
		else
		{
			item = player.getInventory().getItemByObjectId(shortcut.getId());
		}
		
		// stop
		if (!_active)
		{
			if (item != null)
			{
				// auto supply
				if (!item.isPotion())
				{
					AutoUseTaskManager.getInstance().removeAutoSupplyItem(player, item.getId());
				}
				else // auto potion
				{
					AutoUseTaskManager.getInstance().removeAutoPotionItem(player);
				}
			}
			// auto skill
			if (skill != null)
			{
				if (skill.isBad())
				{
					AutoUseTaskManager.getInstance().removeAutoSkill(player, skill.getId());
				}
				else
				{
					AutoUseTaskManager.getInstance().removeAutoBuff(player, skill.getId());
				}
			}
			else // action
			{
				AutoUseTaskManager.getInstance().removeAutoAction(player, shortcut.getId());
			}
			return;
		}
		
		// start
		if ((item != null) && !item.isPotion())
		{
			// auto supply
			if (Config.ENABLE_AUTO_ITEM)
			{
				AutoUseTaskManager.getInstance().addAutoSupplyItem(player, item.getId());
			}
		}
		else
		{
			// auto potion
			if ((_page == 23) && (_slot == 1))
			{
				if (Config.ENABLE_AUTO_POTION && (item != null) && item.isPotion())
				{
					AutoUseTaskManager.getInstance().setAutoPotionItem(player, item.getId());
					return;
				}
			}
			// auto skill
			if (Config.ENABLE_AUTO_SKILL && (skill != null))
			{
				if (skill.isBad())
				{
					AutoUseTaskManager.getInstance().addAutoSkill(player, skill.getId());
				}
				else
				{
					AutoUseTaskManager.getInstance().addAutoBuff(player, skill.getId());
				}
				return;
			}
			// action
			final ActionDataHolder actionHolder = ActionData.getInstance().getActionData(shortcut.getId());
			if (actionHolder != null)
			{
				final IPlayerActionHandler actionHandler = PlayerActionHandler.getInstance().getHandler(actionHolder.getHandler());
				if (actionHandler != null)
				{
					AutoUseTaskManager.getInstance().addAutoAction(player, shortcut.getId());
				}
			}
		}
	}
}
