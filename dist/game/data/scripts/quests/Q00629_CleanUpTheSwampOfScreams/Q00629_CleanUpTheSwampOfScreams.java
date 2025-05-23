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
package quests.Q00629_CleanUpTheSwampOfScreams;

import java.util.HashMap;
import java.util.Map;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00629_CleanUpTheSwampOfScreams extends Quest
{
	// NPC
	private static final int PIERCE = 31553;
	// Items
	private static final int TALON_OF_STAKATO = 7250;
	private static final int GOLDEN_RAM_COIN = 7251;
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	static
	{
		CHANCES.put(21508, 500000);
		CHANCES.put(21509, 431000);
		CHANCES.put(21510, 521000);
		CHANCES.put(21511, 576000);
		CHANCES.put(21512, 746000);
		CHANCES.put(21513, 530000);
		CHANCES.put(21514, 538000);
		CHANCES.put(21515, 545000);
		CHANCES.put(21516, 553000);
		CHANCES.put(21517, 560000);
	}
	
	public Q00629_CleanUpTheSwampOfScreams()
	{
		super(629);
		registerQuestItems(TALON_OF_STAKATO, GOLDEN_RAM_COIN);
		addStartNpc(PIERCE);
		addTalkId(PIERCE);
		addKillId(CHANCES.keySet());
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
		
		switch (event)
		{
			case "31553-1.htm":
			{
				if (player.getLevel() >= 66)
				{
					st.startQuest();
				}
				else
				{
					htmltext = "31553-0a.htm";
					st.exitQuest(true, true);
				}
				break;
			}
			case "31553-3.htm":
			{
				if (getQuestItemsCount(player, TALON_OF_STAKATO) >= 100)
				{
					takeItems(player, TALON_OF_STAKATO, 100);
					giveItems(player, GOLDEN_RAM_COIN, 20);
				}
				else
				{
					htmltext = "31553-3a.htm";
				}
				break;
			}
			case "31553-5.htm":
			{
				st.exitQuest(true, true);
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		
		if (!hasAtLeastOneQuestItem(player, 7246, 7247))
		{
			return "31553-6.htm";
		}
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				htmltext = (player.getLevel() < 66) ? "31553-0a.htm" : "31553-0.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (getQuestItemsCount(player, TALON_OF_STAKATO) >= 100) ? "31553-2.htm" : "31553-1a.htm";
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		final QuestState st = getRandomPartyMemberState(player, -1, 3, npc);
		if ((st == null) || !st.isStarted())
		{
			return null;
		}
		final Player partyMember = st.getPlayer();
		
		if (getRandom(1000000) < CHANCES.get(npc.getId()))
		{
			giveItems(partyMember, TALON_OF_STAKATO, 1);
			if (getQuestItemsCount(partyMember, TALON_OF_STAKATO) < 100)
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
