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

import java.util.List;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.clan.entry.PledgeRecruitInfo;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Sdw
 */
public class ExPledgeRecruitBoardSearch extends ServerPacket
{
	final List<PledgeRecruitInfo> _clanList;
	private final int _currentPage;
	private final int _totalNumberOfPage;
	private final int _clanOnCurrentPage;
	private final int _startIndex;
	private final int _endIndex;
	static final int CLAN_PER_PAGE = 12;
	
	public ExPledgeRecruitBoardSearch(List<PledgeRecruitInfo> clanList, int currentPage)
	{
		_clanList = clanList;
		_currentPage = currentPage;
		_totalNumberOfPage = (int) Math.ceil((double) _clanList.size() / CLAN_PER_PAGE);
		_startIndex = Math.max(0, (_currentPage - 1) * CLAN_PER_PAGE); // Ensure startIndex is non-negative.
		_endIndex = Math.min(_startIndex + CLAN_PER_PAGE, _clanList.size()); // Ensure endIndex is within bounds.
		_clanOnCurrentPage = _endIndex - _startIndex;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_PLEDGE_RECRUIT_BOARD_SEARCH.writeId(this, buffer);
		buffer.writeInt(_currentPage);
		buffer.writeInt(_totalNumberOfPage);
		buffer.writeInt(_clanOnCurrentPage);
		
		// Write clan ids and ally ids.
		for (int i = _startIndex; i < _endIndex; i++)
		{
			final PledgeRecruitInfo recruitInfo = _clanList.get(i);
			buffer.writeInt(recruitInfo.getClanId());
			buffer.writeInt(recruitInfo.getClan().getAllyId());
		}
		
		// Write clan details.
		for (int i = _startIndex; i < _endIndex; i++)
		{
			final PledgeRecruitInfo recruitInfo = _clanList.get(i);
			Clan clan = recruitInfo.getClan();
			buffer.writeInt(clan.getCrestId());
			buffer.writeInt(clan.getAllyCrestId());
			buffer.writeString(clan.getName());
			buffer.writeString(clan.getLeaderName());
			buffer.writeInt(clan.getLevel());
			buffer.writeInt(clan.getMembersCount());
			buffer.writeInt(recruitInfo.getKarma());
			buffer.writeString(recruitInfo.getInformation());
			buffer.writeInt(recruitInfo.getApplicationType()); // Helios
			buffer.writeInt(recruitInfo.getRecruitType()); // Helios
		}
	}
}
