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
package quests.Q00628_HuntGoldenRam;

import java.util.HashMap;
import java.util.Map;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00628_HuntGoldenRam extends Quest
{
	// NPCs
	private static final int KAHMAN = 31554;
	// Items
	private static final int SPLINTER_STAKATO_CHITIN = 7248;
	private static final int NEEDLE_STAKATO_CHITIN = 7249;
	private static final int GOLDEN_RAM_BADGE_RECRUIT = 7246;
	private static final int GOLDEN_RAM_BADGE_SOLDIER = 7247;
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	static
	{
		CHANCES.put(21508, 500000);
		CHANCES.put(21509, 430000);
		CHANCES.put(21510, 521000);
		CHANCES.put(21511, 575000);
		CHANCES.put(21512, 746000);
		CHANCES.put(21513, 500000);
		CHANCES.put(21514, 430000);
		CHANCES.put(21515, 520000);
		CHANCES.put(21516, 531000);
		CHANCES.put(21517, 744000);
	}
	
	public Q00628_HuntGoldenRam()
	{
		super(628);
		registerQuestItems(SPLINTER_STAKATO_CHITIN, NEEDLE_STAKATO_CHITIN, GOLDEN_RAM_BADGE_RECRUIT, GOLDEN_RAM_BADGE_SOLDIER);
		addStartNpc(KAHMAN);
		addTalkId(KAHMAN);
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
			case "31554-02.htm":
			{
				st.startQuest();
				break;
			}
			case "31554-03a.htm":
			{
				if ((getQuestItemsCount(player, SPLINTER_STAKATO_CHITIN) >= 100) && st.isCond(1)) // Giving GOLDEN_RAM_BADGE_RECRUIT Medals
				{
					htmltext = "31554-04.htm";
					st.setCond(2, true);
					takeItems(player, SPLINTER_STAKATO_CHITIN, -1);
					giveItems(player, GOLDEN_RAM_BADGE_RECRUIT, 1);
				}
				break;
			}
			case "31554-07.htm":
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
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				htmltext = (player.getLevel() < 66) ? "31554-01a.htm" : "31554-01.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				if (cond == 1)
				{
					if (getQuestItemsCount(player, SPLINTER_STAKATO_CHITIN) >= 100)
					{
						htmltext = "31554-03.htm";
					}
					else
					{
						htmltext = "31554-03a.htm";
					}
				}
				else if (cond == 2)
				{
					if ((getQuestItemsCount(player, SPLINTER_STAKATO_CHITIN) >= 100) && (getQuestItemsCount(player, NEEDLE_STAKATO_CHITIN) >= 100))
					{
						htmltext = "31554-05.htm";
						st.setCond(3);
						playSound(player, QuestSound.ITEMSOUND_QUEST_FINISH);
						takeItems(player, SPLINTER_STAKATO_CHITIN, -1);
						takeItems(player, NEEDLE_STAKATO_CHITIN, -1);
						takeItems(player, GOLDEN_RAM_BADGE_RECRUIT, 1);
						giveItems(player, GOLDEN_RAM_BADGE_SOLDIER, 1);
					}
					else if (!hasQuestItems(player, SPLINTER_STAKATO_CHITIN) && !hasQuestItems(player, NEEDLE_STAKATO_CHITIN))
					{
						htmltext = "31554-04b.htm";
					}
					else
					{
						htmltext = "31554-04a.htm";
					}
				}
				else if (cond == 3)
				{
					htmltext = "31554-05a.htm";
				}
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
		
		final int cond = st.getCond();
		final int npcId = npc.getId();
		switch (npcId)
		{
			case 21508:
			case 21509:
			case 21510:
			case 21511:
			case 21512:
			{
				if (((cond == 1) || (cond == 2)) && (getQuestItemsCount(partyMember, SPLINTER_STAKATO_CHITIN) < 100) && (getRandom(1000000) < CHANCES.get(npcId)))
				{
					giveItems(partyMember, SPLINTER_STAKATO_CHITIN, 1);
					if (getQuestItemsCount(partyMember, SPLINTER_STAKATO_CHITIN) < 100)
					{
						playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					else
					{
						playSound(partyMember, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					}
				}
				break;
			}
			case 21513:
			case 21514:
			case 21515:
			case 21516:
			case 21517:
			{
				if ((cond == 2) && (getQuestItemsCount(partyMember, NEEDLE_STAKATO_CHITIN) < 100) && (getRandom(1000000) < CHANCES.get(npcId)))
				{
					giveItems(partyMember, NEEDLE_STAKATO_CHITIN, 1);
					if (getQuestItemsCount(partyMember, NEEDLE_STAKATO_CHITIN) < 100)
					{
						playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					else
					{
						playSound(partyMember, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					}
				}
				break;
			}
		}
		
		return null;
	}
}
