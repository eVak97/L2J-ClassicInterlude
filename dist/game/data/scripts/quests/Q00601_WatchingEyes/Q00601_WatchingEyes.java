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
package quests.Q00601_WatchingEyes;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00601_WatchingEyes extends Quest
{
	// Items
	private static final int PROOF_OF_AVENGER = 7188;
	// Rewards
	private static final int[][] REWARDS =
	{
		// @formatter:off
		{6699, 90000, 20},
		{6698, 80000, 40},
		{6700, 40000, 50},
		{0, 230000, 100}
		// @formatter:on
	};
	
	public Q00601_WatchingEyes()
	{
		super(601);
		registerQuestItems(PROOF_OF_AVENGER);
		addStartNpc(31683); // Eye of Argos
		addTalkId(31683);
		addKillId(21306, 21308, 21309, 21310, 21311);
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
		
		if (event.equals("31683-03.htm"))
		{
			if (player.getLevel() < 71)
			{
				htmltext = "31683-02.htm";
			}
			else
			{
				st.startQuest();
			}
		}
		else if (event.equals("31683-07.htm"))
		{
			takeItems(player, PROOF_OF_AVENGER, -1);
			
			final int random = getRandom(100);
			for (int[] element : REWARDS)
			{
				if (random < element[2])
				{
					giveAdena(player, element[1], true);
					if (element[0] != 0)
					{
						giveItems(player, element[0], 5);
						addExpAndSp(player, 120000, 10000);
					}
					break;
				}
			}
			st.exitQuest(true, true);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				htmltext = "31683-01.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				if (cond == 1)
				{
					htmltext = (hasQuestItems(player, PROOF_OF_AVENGER)) ? "31683-05.htm" : "31683-04.htm";
				}
				else if (cond == 2)
				{
					htmltext = "31683-06.htm";
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		final QuestState qs = getRandomPartyMemberState(player, 1, 3, npc);
		if (qs == null)
		{
			return null;
		}
		final Player partyMember = qs.getPlayer();
		
		final QuestState st = getQuestState(partyMember, false);
		if (st == null)
		{
			return null;
		}
		
		if (getRandomBoolean())
		{
			giveItems(partyMember, PROOF_OF_AVENGER, 1);
			if (getQuestItemsCount(partyMember, PROOF_OF_AVENGER) < 100)
			{
				playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			else
			{
				st.setCond(2, true);
			}
		}
		
		return null;
	}
}
