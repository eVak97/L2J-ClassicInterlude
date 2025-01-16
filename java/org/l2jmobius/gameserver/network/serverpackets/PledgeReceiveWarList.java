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

import java.util.Collection;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.clan.ClanWar;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Mobius
 */
public class PledgeReceiveWarList extends ServerPacket
{
	private final Clan _clan;
	private final int _tab;
	private final Collection<ClanWar> _clanList;
	
	public PledgeReceiveWarList(Clan clan, int tab)
	{
		_clan = clan;
		_tab = tab;
		_clanList = clan.getWarList().values();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.PLEDGE_RECEIVE_WAR_LIST.writeId(this, buffer);
		buffer.writeInt(_tab); // page
		buffer.writeInt(_clanList.size());
		for (ClanWar clanWar : _clanList)
		{
			final Clan clan = clanWar.getOpposingClan(_clan);
			if (clan == null)
			{
				continue;
			}
			buffer.writeString(clan.getName());
			buffer.writeInt(clanWar.getState().ordinal()); // type: 0 = Declaration, 1 = Blood Declaration, 2 = In War, 3 = Victory, 4 = Defeat, 5 = Tie, 6 = Error
			buffer.writeInt(clanWar.getRemainingTime()); // Time if friends to start remaining
			buffer.writeInt(clanWar.getKillDifference(_clan)); // Score
			buffer.writeInt(clanWar.calculateWarProgress(_clan).ordinal());
			buffer.writeInt(clanWar.getKillToStart()); // Friends to start war left
		}
	}
}
