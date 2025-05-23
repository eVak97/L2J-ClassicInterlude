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

import org.l2jmobius.gameserver.enums.ElementalType;
import org.l2jmobius.gameserver.enums.UserInfoType;
import org.l2jmobius.gameserver.model.ElementalSpirit;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.ElementalSpiritAbsorbItemHolder;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.UserInfo;
import org.l2jmobius.gameserver.network.serverpackets.elementalspirits.ElementalSpiritAbsorb;

/**
 * @author JoeAlisson
 */
public class ExElementalSpiritAbsorb extends ClientPacket
{
	private byte _type;
	private int _itemId;
	private int _amount;
	
	@Override
	protected void readImpl()
	{
		_type = readByte();
		readInt(); // items for now is always 1
		_itemId = readInt();
		_amount = readInt();
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
		
		final ElementalSpiritAbsorbItemHolder absorbItem = spirit.getAbsorbItem(_itemId);
		if (absorbItem == null)
		{
			player.sendPacket(new ElementalSpiritAbsorb(player, _type, false));
			return;
		}
		
		final boolean canAbsorb = checkConditions(player, spirit);
		if (canAbsorb)
		{
			player.sendPacket(SystemMessageId.DRAIN_SUCCESSFUL);
			spirit.addExperience(absorbItem.getExperience() * _amount);
			final UserInfo userInfo = new UserInfo(player);
			userInfo.addComponentType(UserInfoType.ATT_SPIRITS);
			player.sendPacket(userInfo);
		}
		player.sendPacket(new ElementalSpiritAbsorb(player, _type, canAbsorb));
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
			player.sendPacket(SystemMessageId.CANNOT_DRAIN_DURING_BATTLE);
			return false;
		}
		if ((spirit.getLevel() == spirit.getMaxLevel()) && (spirit.getExperience() == spirit.getExperienceToNextLevel()))
		{
			player.sendPacket(SystemMessageId.YOU_HAVE_REACHED_THE_HIGHEST_LEVEL_AND_CANNOT_ABSORB_ANY_FURTHER);
			return false;
		}
		if ((_amount < 1) || !player.destroyItemByItemId("Absorb", _itemId, _amount, player, true))
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_MATERIALS_REQUIRED_TO_ABSORB);
			return false;
		}
		return true;
	}
}
