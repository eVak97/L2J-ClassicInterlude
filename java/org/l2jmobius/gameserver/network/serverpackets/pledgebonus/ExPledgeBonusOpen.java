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
package org.l2jmobius.gameserver.network.serverpackets.pledgebonus;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.data.xml.ClanRewardData;
import org.l2jmobius.gameserver.enums.ClanRewardType;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.clan.ClanRewardBonus;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExPledgeBonusOpen extends ServerPacket
{
	private Clan _clan;
	private ClanRewardBonus _highestMembersOnlineBonus;
	private ClanRewardBonus _highestHuntingBonus;
	private ClanRewardBonus _membersOnlineBonus;
	private ClanRewardBonus _huntingBonus;
	private boolean _canClaimMemberReward;
	private boolean _canClaimHuntingReward;
	
	public ExPledgeBonusOpen(Player player)
	{
		_clan = player.getClan();
		if (_clan == null)
		{
			PacketLogger.warning("Player: " + player + " attempting to write to a null clan!");
			return;
		}
		
		final ClanRewardData data = ClanRewardData.getInstance();
		_highestMembersOnlineBonus = data.getHighestReward(ClanRewardType.MEMBERS_ONLINE);
		_highestHuntingBonus = data.getHighestReward(ClanRewardType.HUNTING_MONSTERS);
		_membersOnlineBonus = ClanRewardType.MEMBERS_ONLINE.getAvailableBonus(_clan);
		_huntingBonus = ClanRewardType.HUNTING_MONSTERS.getAvailableBonus(_clan);
		if (_highestMembersOnlineBonus == null)
		{
			PacketLogger.warning("Couldn't find highest available clan members online bonus!!");
			_clan = null;
			return;
		}
		else if (_highestHuntingBonus == null)
		{
			PacketLogger.warning("Couldn't find highest available clan hunting bonus!!");
			_clan = null;
			return;
		}
		else if (_highestMembersOnlineBonus.getSkillReward() == null)
		{
			PacketLogger.warning("Couldn't find skill reward for highest available members online bonus!!");
			_clan = null;
			return;
		}
		else if (_highestHuntingBonus.getItemReward() == null)
		{
			PacketLogger.warning("Couldn't find item reward for highest available hunting bonus!!");
			_clan = null;
			return;
		}
		
		_canClaimMemberReward = _clan.canClaimBonusReward(player, ClanRewardType.MEMBERS_ONLINE);
		_canClaimHuntingReward = _clan.canClaimBonusReward(player, ClanRewardType.HUNTING_MONSTERS);
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		if (_clan == null)
		{
			return;
		}
		
		ServerPackets.EX_PLEDGE_BONUS_OPEN.writeId(this, buffer);
		
		// Members online bonus.
		buffer.writeInt(_highestMembersOnlineBonus.getRequiredAmount());
		buffer.writeInt(_clan.getMaxOnlineMembers());
		buffer.writeByte(0); // 140
		buffer.writeInt(_membersOnlineBonus != null ? _highestMembersOnlineBonus.getSkillReward().getSkillId() : 0);
		buffer.writeByte(_membersOnlineBonus != null ? _membersOnlineBonus.getLevel() : 0);
		buffer.writeByte(_canClaimMemberReward);
		
		// Hunting bonus.
		buffer.writeInt(_highestHuntingBonus.getRequiredAmount());
		buffer.writeInt(_clan.getHuntingPoints());
		buffer.writeByte(0); // 140
		buffer.writeInt(_huntingBonus != null ? _highestHuntingBonus.getItemReward().getId() : 0);
		buffer.writeByte(_huntingBonus != null ? _huntingBonus.getLevel() : 0);
		buffer.writeByte(_canClaimHuntingReward);
	}
}
