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
package org.l2jmobius.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.data.sql.ClanTable;
import org.l2jmobius.gameserver.data.xml.ClanHallData;
import org.l2jmobius.gameserver.model.residences.ClanHall;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author KenM
 */
public class ExShowAgitInfo extends ServerPacket
{
	public static final ExShowAgitInfo STATIC_PACKET = new ExShowAgitInfo();
	
	private final List<ClanHallHolder> _clanHalls;
	
	private ExShowAgitInfo()
	{
		final Collection<ClanHall> clanHalls = ClanHallData.getInstance().getClanHalls();
		_clanHalls = new ArrayList<>(clanHalls.size());
		for (ClanHall clanHall : clanHalls)
		{
			_clanHalls.add(new ClanHallHolder(clanHall.getResidenceId(), clanHall.getOwnerId() <= 0 ? "" : ClanTable.getInstance().getClan(clanHall.getOwnerId()).getName(), clanHall.getOwnerId() <= 0 ? "" : ClanTable.getInstance().getClan(clanHall.getOwnerId()).getLeaderName(), clanHall.getType().getClientVal()));
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_SHOW_AGIT_INFO.writeId(this, buffer);
		buffer.writeInt(_clanHalls.size());
		for (ClanHallHolder holder : _clanHalls)
		{
			buffer.writeInt(holder.getResidenceId());
			buffer.writeString(holder.getOwnerClanName());
			buffer.writeString(holder.getLeaderName());
			buffer.writeInt(holder.getClanHallType());
		}
	}
	
	public class ClanHallHolder
	{
		private final int _residenceId;
		private final String _ownerClanName;
		private final String _leaderName;
		private final int _clanHallType;
		
		public ClanHallHolder(int residenceId, String ownerClanName, String leaderName, int clanHallType)
		{
			_residenceId = residenceId;
			_ownerClanName = ownerClanName;
			_leaderName = leaderName;
			_clanHallType = clanHallType;
		}
		
		public int getResidenceId()
		{
			return _residenceId;
		}
		
		public String getOwnerClanName()
		{
			return _ownerClanName;
		}
		
		public String getLeaderName()
		{
			return _leaderName;
		}
		
		public int getClanHallType()
		{
			return _clanHallType;
		}
	}
}
