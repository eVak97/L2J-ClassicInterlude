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
package quests.Q00377_ExplorationOfTheGiantsCavePart2;

import java.util.HashMap;
import java.util.Map;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

/**
 * Exploration of the Giants' Cave Part 2 (377)<br>
 * Original Jython script by Gnacik.
 * @author nonom
 */
public class Q00377_ExplorationOfTheGiantsCavePart2 extends Quest
{
	// NPC
	private static final int SOBLING = 31147;
	// Items
	private static final int ANCIENT_BOOK = 5955;
	private static final int DICTIONARY_INTERMEDIATE = 5892;
	private static final int[][] BOOKS =
	{
		// @formatter:off
		// science & technology -> majestic leather, leather armor of nightmare
		{5945, 5946, 5947, 5948, 5949},
		// culture -> armor of nightmare, majestic plate
		{5950, 5951, 5952, 5953, 5954}
		// @formatter:on
	};
	// Rewards
	private static final int[][] RECIPES =
	{
		// @formatter:off
		// science & technology -> majestic leather, leather armor of nightmare
		{5338, 5336},
		// culture -> armor of nightmare, majestic plate
		{5420, 5422}
		// @formatter:on
	};
	// Mobs
	private static final Map<Integer, Integer> MOBS1 = new HashMap<>();
	static
	{
		MOBS1.put(20653, 366); // lesser_giant_re
		MOBS1.put(20654, 424); // lesser_giant_soldier_re
		MOBS1.put(20655, 304); // lesser_giant_shooter_re
		MOBS1.put(20656, 304); // lesser_giant_scout_re
		MOBS1.put(20657, 354); // lesser_giant_mage_re
		MOBS1.put(20658, 324); // lesser_giant_elder_re
		MOBS1.put(20771, 366); // barif_re
		MOBS1.put(20772, 366); // barif_pet_re
		MOBS1.put(20651, 366); // gamlin_re
		MOBS1.put(20652, 366); // leogul_re
	}
	
	public Q00377_ExplorationOfTheGiantsCavePart2()
	{
		super(377);
		addStartNpc(SOBLING);
		addTalkId(SOBLING);
		addKillId(MOBS1.keySet());
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
				break;
			}
			case "31147-04.htm":
			{
				htmltext = checkItems(player);
				break;
			}
			case "31147-07.htm":
			{
				qs.exitQuest(true, true);
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		final Player partyMember = getRandomPartyMemberState(player, State.STARTED);
		if (partyMember != null)
		{
			giveItemRandomly(partyMember, npc, ANCIENT_BOOK, 1, 0, 0.8, true);
			return null;
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
				htmltext = ((player.getLevel() < 57) || !hasQuestItems(player, DICTIONARY_INTERMEDIATE)) ? "31147-01.htm" : "31147-02.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = checkItems(player);
				break;
			}
		}
		
		return htmltext;
	}
	
	private static String checkItems(Player player)
	{
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
