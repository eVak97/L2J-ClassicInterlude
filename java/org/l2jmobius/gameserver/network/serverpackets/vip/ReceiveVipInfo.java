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
package org.l2jmobius.gameserver.network.serverpackets.vip;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.l2jmobius.Config;
import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.vip.VipManager;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ReceiveVipInfo extends ServerPacket
{
	private final byte _vipTier;
	private final int _vipDuration;
	private final long _vipPoints;
	private final long _nextLevelPoints;
	private final long _currentLevelPoints;
	private final long _previousLevelPoints;
	
	public ReceiveVipInfo(Player player)
	{
		final VipManager vipManager = VipManager.getInstance();
		_vipTier = player.getVipTier();
		_vipPoints = player.getVipPoints();
		_vipDuration = (int) ChronoUnit.SECONDS.between(Instant.now(), Instant.ofEpochMilli(player.getVipTierExpiration()));
		_nextLevelPoints = vipManager.getPointsToLevel((byte) (_vipTier + 1));
		_currentLevelPoints = vipManager.getPointsToLevel(_vipTier);
		_previousLevelPoints = vipManager.getPointsDepreciatedOnLevel(_vipTier);
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		if (!Config.VIP_SYSTEM_ENABLED)
		{
			return;
		}
		
		ServerPackets.RECIVE_VIP_INFO.writeId(this, buffer);
		buffer.writeByte(_vipTier);
		buffer.writeLong(_vipPoints);
		buffer.writeInt(_vipDuration);
		buffer.writeLong(_nextLevelPoints);
		buffer.writeLong(_previousLevelPoints);
		buffer.writeByte(_vipTier);
		buffer.writeLong(_currentLevelPoints);
	}
}
