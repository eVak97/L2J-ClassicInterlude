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
package org.l2jmobius.gameserver.network.clientpackets.elementalspirits;

import java.util.stream.Collectors;

import org.l2jmobius.gameserver.enums.ElementalType;
import org.l2jmobius.gameserver.enums.InventoryBlockType;
import org.l2jmobius.gameserver.enums.UserInfoType;
import org.l2jmobius.gameserver.model.ElementalSpirit;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.itemcontainer.PlayerInventory;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import org.l2jmobius.gameserver.network.serverpackets.UserInfo;
import org.l2jmobius.gameserver.network.serverpackets.elementalspirits.ElementalSpiritEvolution;

/**
 * @author JoeAlisson
 */
public class ExElementalSpiritEvolution extends ClientPacket
{
	private byte _type;
	
	@Override
	protected void readImpl()
	{
		_type = readByte();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final ElementalSpirit spirit = player.getElementalSpirit(ElementalType.of(_type));
		if (spirit == null)
		{
			player.sendPacket(SystemMessageId.NO_SPIRITS_ARE_AVAILABLE);
			return;
		}
		
		final boolean canEvolve = checkConditions(player, spirit);
		if (canEvolve)
		{
			spirit.upgrade();
			player.sendPacket(new SystemMessage(SystemMessageId.S1_EVOLVED_TO_S2_STAR).addElementalSpirit(_type).addInt(spirit.getStage()));
			final UserInfo userInfo = new UserInfo(player);
			userInfo.addComponentType(UserInfoType.ATT_SPIRITS);
			player.sendPacket(userInfo);
		}
		player.sendPacket(new ElementalSpiritEvolution(player, _type, canEvolve));
	}
	
	private boolean checkConditions(Player player, ElementalSpirit spirit)
	{
		if (player.isInStoreMode())
		{
			player.sendPacket(SystemMessageId.CANNOT_EVOLVE_ABSORB_EXTRACT_WHILE_USING_THE_PRIVATE_STORE_WORKSHOP);
			return false;
		}
		if (player.isInBattle())
		{
			player.sendPacket(SystemMessageId.CANNOT_EVOLVE_DURING_BATTLE);
			return false;
		}
		if (!spirit.canEvolve())
		{
			player.sendPacket(SystemMessageId.SPIRIT_CANNOT_BE_EVOLVED);
			return false;
		}
		if (!consumeEvolveItems(player, spirit))
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_MATERIALS_REQUIRED_TO_EVOLVE);
			return false;
		}
		return true;
	}
	
	private boolean consumeEvolveItems(Player player, ElementalSpirit spirit)
	{
		final PlayerInventory inventory = player.getInventory();
		try
		{
			inventory.setInventoryBlock(spirit.getItemsToEvolve().stream().map(ItemHolder::getId).collect(Collectors.toList()), InventoryBlockType.BLACKLIST);
			for (ItemHolder itemHolder : spirit.getItemsToEvolve())
			{
				if (inventory.getInventoryItemCount(itemHolder.getId(), -1) < itemHolder.getCount())
				{
					return false;
				}
			}
			
			for (ItemHolder itemHolder : spirit.getItemsToEvolve())
			{
				player.destroyItemByItemId("Evolve", itemHolder.getId(), itemHolder.getCount(), player, true);
			}
			return true;
		}
		finally
		{
			inventory.unblock();
		}
	}
}
