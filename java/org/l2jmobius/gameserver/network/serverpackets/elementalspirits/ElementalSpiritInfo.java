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
package org.l2jmobius.gameserver.network.serverpackets.elementalspirits;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.model.ElementalSpirit;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author JoeAlisson
 */
public class ElementalSpiritInfo extends AbstractElementalSpiritPacket
{
	private final Player _player;
	private final byte _spiritType;
	private final byte _type;
	
	public ElementalSpiritInfo(Player player, byte spiritType, byte packetType)
	{
		_player = player;
		_spiritType = spiritType;
		_type = packetType;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_ELEMENTAL_SPIRIT_INFO.writeId(this, buffer);
		final ElementalSpirit[] spirits = _player.getSpirits();
		if (spirits == null)
		{
			buffer.writeByte(0);
			buffer.writeByte(0);
			buffer.writeByte(0);
			return;
		}
		buffer.writeByte(_type); // show spirit info window 1; Change type 2; Only update 0
		buffer.writeByte(_spiritType);
		buffer.writeByte(spirits.length); // spirit count
		for (ElementalSpirit spirit : spirits)
		{
			buffer.writeByte(spirit.getType());
			buffer.writeByte(1); // spirit active ?
			// if active
			writeSpiritInfo(buffer, spirit);
		}
		buffer.writeInt(1); // Reset talent items count
		for (int j = 0; j < 1; j++)
		{
			buffer.writeInt(57);
			buffer.writeLong(50000);
		}
	}
}