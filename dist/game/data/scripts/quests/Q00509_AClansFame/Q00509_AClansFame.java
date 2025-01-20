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
package quests.Q00509_AClansFame;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import org.l2jmobius.gameserver.util.Util;

public class Q00509_AClansFame extends Quest
{
	// NPCs
	private static final int VALDIS = 31331;
	// Raid Bosses
	private static final int DAIMON_THE_WHITE_EYED = 25290;
	private static final int HESTIA_GUARDIAN_DEITY = 25293;
	private static final int PLAGUE_GOLEM = 25523;
	private static final int DEMONS_AGENT_FALSTON = 25322;
	private static final int QUEEN_SHYEED = 25514;
	// Items
	private static final int DAIMONS_EYES = 8489;
	private static final int HESTIAS_FAIRY_STONE = 8490;
	private static final int NUCLEUS_OF_LESSER_GOLEM = 8491;
	private static final int FALSTON_FANG = 8492;
	private static final int SHAIDS_TALON = 8493;
	// Reward list (itemId, minClanPoints, maxClanPoints)
	private static final int[][] REWARD_LIST =
	{
		// @formatter:off
		{DAIMON_THE_WHITE_EYED, DAIMONS_EYES, 180, 215},
		{HESTIA_GUARDIAN_DEITY, HESTIAS_FAIRY_STONE, 430, 465},
		{PLAGUE_GOLEM, NUCLEUS_OF_LESSER_GOLEM, 380, 415},
		{DEMONS_AGENT_FALSTON, FALSTON_FANG, 220, 255},
		{QUEEN_SHYEED, SHAIDS_TALON, 130, 165}
	};
	// Radar
	private static final int[][] radar =
	{
		{186320, -43904, -3175},
		{134672, -115600, -1216},
		{170000, -59900, -3848},
		{93296, -75104, -1824},
		{79635, -55612, -5980}
		// @formatter:on
	};
	
	public Q00509_AClansFame()
	{
		super(509);
		registerQuestItems(DAIMONS_EYES, HESTIAS_FAIRY_STONE, NUCLEUS_OF_LESSER_GOLEM, FALSTON_FANG, SHAIDS_TALON);
		addStartNpc(VALDIS);
		addTalkId(VALDIS);
		addKillId(DAIMON_THE_WHITE_EYED, HESTIA_GUARDIAN_DEITY, PLAGUE_GOLEM, DEMONS_AGENT_FALSTON, QUEEN_SHYEED);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return htmltext;
		}
		
		if (Util.isDigit(event))
		{
			final int evt = Integer.parseInt(event);
			st.set("raid", event);
			htmltext = "31331-" + event + ".htm";
			
			final int x = radar[evt - 1][0];
			final int y = radar[evt - 1][1];
			final int z = radar[evt - 1][2];
			if ((x + y + z) > 0)
			{
				addRadar(player, x, y, z);
			}
			
			st.startQuest();
		}
		else if (event.equals("31331-6.htm"))
		{
			st.exitQuest(true, true);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		
		final Clan clan = player.getClan();
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				if (!player.isClanLeader())
				{
					st.exitQuest(true, true);
					htmltext = "31331-0a.htm";
				}
				else if (clan.getLevel() < 6)
				{
					st.exitQuest(true, true);
					htmltext = "31331-0b.htm";
				}
				else
				{
					htmltext = "31331-0c.htm";
				}
				break;
			}
			case State.STARTED:
			{
				final int raid = st.getInt("raid");
				if (st.isCond(1))
				{
					final int item = REWARD_LIST[raid - 1][1];
					final int count = (int) getQuestItemsCount(player, item);
					final int reward = getRandom(REWARD_LIST[raid - 1][2], REWARD_LIST[raid - 1][3]);
					if (count == 0)
					{
						htmltext = "31331-" + raid + "a.htm";
					}
					else if (count == 1)
					{
						htmltext = "31331-" + raid + "b.htm";
						takeItems(player, item, 1);
						clan.addReputationScore(reward);
						player.sendPacket(new SystemMessage(SystemMessageId.YOU_HAVE_SUCCESSFULLY_COMPLETED_A_CLAN_QUEST_S1_POINT_S_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION).addInt(reward));
						clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
					}
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		// Retrieve the qs of the clan leader.
		final QuestState st = getClanLeaderQuestState(player, npc);
		if ((st == null) || !st.isStarted())
		{
			return null;
		}
		
		// Reward only if quest is setup on good index.
		final int raid = st.getInt("raid");
		if (REWARD_LIST[raid - 1][0] == npc.getId())
		{
			final int item = REWARD_LIST[raid - 1][1];
			if (!hasQuestItems(player, item))
			{
				giveItems(player, item, 1);
				playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
			}
		}
		
		return null;
	}
}