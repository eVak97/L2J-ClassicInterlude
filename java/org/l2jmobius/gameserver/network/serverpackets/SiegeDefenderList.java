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
package org.l2jmobius.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.data.sql.ClanTable;
import org.l2jmobius.gameserver.enums.SiegeClanType;
import org.l2jmobius.gameserver.model.SiegeClan;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.siege.Castle;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * Populates the Siege Defender List in the SiegeInfo Window<br>
 * <br>
 * c = 0xcb<br>
 * d = CastleID<br>
 * d = unknown (0)<br>
 * d = unknown (1)<br>
 * d = unknown (0)<br>
 * d = Number of Defending Clans?<br>
 * d = Number of Defending Clans<br>
 * { //repeats<br>
 * d = ClanID<br>
 * S = ClanName<br>
 * S = ClanLeaderName<br>
 * d = ClanCrestID<br>
 * d = signed time (seconds)<br>
 * d = Type -> Owner = 0x01 || Waiting = 0x02 || Accepted = 0x03<br>
 * d = AllyID<br>
 * S = AllyName<br>
 * S = AllyLeaderName<br>
 * d = AllyCrestID<br>
 * @author Atronic
 */
public class SiegeDefenderList extends ServerPacket
{
	private final Castle _castle;
	private final Clan _owner;
	final List<Clan> _defenders = new ArrayList<>();
	
	public SiegeDefenderList(Castle castle)
	{
		_castle = castle;
		_owner = castle.getOwner();
		
		// Add owners.
		if (_owner != null)
		{
			_defenders.add(_owner);
		}
		
		// List of confirmed defenders.
		for (SiegeClan siegeClan : _castle.getSiege().getDefenderClans())
		{
			final Clan clan = ClanTable.getInstance().getClan(siegeClan.getClanId());
			if ((clan != null) && (clan != _owner))
			{
				_defenders.add(clan);
			}
		}
		
		// List of not confirmed defenders.
		for (SiegeClan siegeClan : _castle.getSiege().getDefenderWaitingClans())
		{
			final Clan clan = ClanTable.getInstance().getClan(siegeClan.getClanId());
			if (clan != null)
			{
				_defenders.add(clan);
			}
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.CASTLE_SIEGE_DEFENDER_LIST.writeId(this, buffer);
		buffer.writeInt(_castle.getResidenceId());
		buffer.writeInt(0); // Unknown.
		
		buffer.writeInt((_owner != null) && _castle.isTimeRegistrationOver()); // Valid registration.
		buffer.writeInt(0); // Unknown.
		
		final int size = _defenders.size();
		buffer.writeInt(size);
		buffer.writeInt(size);
		for (Clan clan : _defenders)
		{
			buffer.writeInt(clan.getId());
			buffer.writeString(clan.getName());
			buffer.writeString(clan.getLeaderName());
			buffer.writeInt(clan.getCrestId());
			buffer.writeInt(0); // Signed time in seconds.
			if (clan == _owner)
			{
				buffer.writeInt(SiegeClanType.OWNER.ordinal() + 1);
			}
			else if (_castle.getSiege().getDefenderClans().stream().anyMatch(defender -> defender.getClanId() == clan.getId()))
			{
				buffer.writeInt(SiegeClanType.DEFENDER.ordinal() + 1);
			}
			else
			{
				buffer.writeInt(SiegeClanType.DEFENDER_PENDING.ordinal() + 1);
			}
			buffer.writeInt(clan.getAllyId());
			if (clan.getAllyId() != 0)
			{
				final AllianceInfo info = new AllianceInfo(clan.getAllyId());
				buffer.writeString(info.getName());
				buffer.writeString(info.getLeaderP()); // Ally leader name.
				buffer.writeInt(clan.getAllyCrestId());
			}
			else
			{
				buffer.writeString("");
				buffer.writeString(""); // Ally leader name.
				buffer.writeInt(0);
			}
		}
	}
}
