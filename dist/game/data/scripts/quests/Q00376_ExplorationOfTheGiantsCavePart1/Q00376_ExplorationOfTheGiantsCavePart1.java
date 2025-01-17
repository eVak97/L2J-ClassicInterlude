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
package quests.Q00376_ExplorationOfTheGiantsCavePart1;

import java.util.HashMap;
import java.util.Map;

import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

/**
 * Exploration of the Giants' Cave Part 1 (376)<br>
 * Original Jython script by Gnacik.
 * @author nonom
 */
public class Q00376_ExplorationOfTheGiantsCavePart1 extends Quest
{
	// NPC
	private static final int SOBLING = 31147;
	private static final int CLIFF = 30182;
	// Items
	private static final int PARCHMENT = 5944;
	private static final int DICTIONARY_BASIC = 5891;
	private static final int MYSTERIOUS_BOOK = 5890;
	private static final int DICTIONARY_INTERMEDIATE = 5892;
	private static final int[][] BOOKS =
	{
		// @formatter:off
		// medical theory -> tallum tunic, tallum stockings
		{5937, 5938, 5939, 5940, 5941},
		// architecture -> dark crystal leather, tallum leather
		{5932, 5933, 5934, 5935, 5936},
		// golem plans -> dark crystal breastplate, tallum plate
		{5922, 5923, 5924, 5925, 5926},
		// basics of magic -> dark crystal gaiters, dark crystal leggings
		{5927, 5928, 5929, 5930, 5931}
		// @formatter:on
	};
	// Rewards
	private static final int[][] RECIPES =
	{
		// @formatter:off
		// medical theory -> tallum tunic, tallum stockings
		{5346, 5354},
		// architecture -> dark crystal leather, tallum leather
		{5332, 5334},
		// golem plans -> dark crystal breastplate, tallum plate
		{5416, 5418},
		// basics of magic -> dark crystal gaiters, dark crystal leggings
		{5424, 5340}
		// @formatter:on
	};
	// Mobs
	private static final Map<Integer, Double> MOBS = new HashMap<>();
	static
	{
		MOBS.put(20646, 0.302); // Halingka
		MOBS.put(21058, 0.300); // Beast Lord
		MOBS.put(20647, 0.258); // yintzu
		MOBS.put(20648, 0.264); // paliote
		MOBS.put(20649, 0.258); // hamrit
		MOBS.put(20650, 0.266); // kranrot
	}
	
	public Q00376_ExplorationOfTheGiantsCavePart1()
	{
		super(376);
		addStartNpc(SOBLING);
		addTalkId(SOBLING, CLIFF);
		addKillId(MOBS.keySet());
		registerQuestItems(DICTIONARY_BASIC, MYSTERIOUS_BOOK);
		registerQuestItems(PARCHMENT);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "31147-03.htm":
			{
				qs.startQuest();
				qs.set("condBook", "1");
				giveItems(player, DICTIONARY_BASIC, 1);
				break;
			}
			case "31147-04.htm":
			{
				htmltext = checkItems(player, qs);
				break;
			}
			case "31147-09.htm":
			{
				qs.exitQuest(true, true);
				break;
			}
			case "30182-02.htm":
			{
				qs.setCond(3, true);
				takeItems(player, MYSTERIOUS_BOOK, -1);
				giveItems(player, DICTIONARY_INTERMEDIATE, 1);
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		// Drop parchment to anyone
		Player partyMember = getRandomPartyMemberState(player, State.STARTED);
		if (partyMember == null)
		{
			return null;
		}
		
		QuestState sq = partyMember.getQuestState(getName());
		if (sq != null)
		{
			giveItemRandomly(sq.getPlayer(), npc, PARCHMENT, 1, 0, MOBS.get(npc.getId()), true);
			// Drop mysterious book to person who still need it
			partyMember = getRandomPartyMember(player, "condBook", "1");
			if (partyMember != null)
			{
				sq = partyMember.getQuestState(getName());
				if (sq != null)
				{
					if (Rnd.get(100) < 10) // 10%
					{
						giveItems(partyMember, MYSTERIOUS_BOOK, 1);
						playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						sq.unset("condBook");
					}
				}
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState qs = getQuestState(player, true);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = (player.getLevel() < 51) ? "31147-01.htm" : "31147-02.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = qs.getCond();
				switch (npc.getId())
				{
					case SOBLING:
					{
						htmltext = checkItems(player, qs);
						break;
					}
					case CLIFF:
					{
						if ((cond == 2) && hasQuestItems(player, MYSTERIOUS_BOOK))
						{
							htmltext = "30182-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = "30182-03.htm";
						}
						break;
					}
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	private static String checkItems(Player player, QuestState qs)
	{
		if (hasQuestItems(player, MYSTERIOUS_BOOK))
		{
			final int cond = qs.getCond();
			if (cond == 1)
			{
				qs.setCond(2, true);
				return "31147-07.htm";
			}
			return "31147-08.htm";
		}
		
		for (int type = 0; type < BOOKS.length; type++)
		{
			boolean complete = true;
			for (int book : BOOKS[type])
			{
				if (!hasQuestItems(player, book))
				{
					complete = false;
				}
			}
			
			if (complete)
			{
				for (int book : BOOKS[type])
				{
					takeItems(player, book, 1);
				}
				
				giveItems(player, RECIPES[type][getRandom(RECIPES[type].length)], 1);
				return "31147-04.htm";
			}
		}
		return "31147-05.htm";
	}
}
