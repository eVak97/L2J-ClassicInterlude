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
package quests.Q00354_ConquestOfAlligatorIsland;

import java.util.HashMap;
import java.util.Map;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00354_ConquestOfAlligatorIsland extends Quest
{
	// Items
	private static final int ALLIGATOR_TOOTH = 5863;
	private static final int TORN_MAP_FRAGMENT = 5864;
	private static final int PIRATE_TREASURE_MAP = 5915;
	// Drops
	private static final Map<Integer, Double> MOB1 = new HashMap<>();
	private static final Map<Integer, Integer> MOB2 = new HashMap<>();
	static
	{
		MOB1.put(20804, 0.84); // crokian_lad
		MOB1.put(20805, 0.91); // dailaon_lad
		MOB1.put(20806, 0.88); // crokian_lad_warrior
		MOB1.put(20807, 0.92); // farhite_lad
		MOB2.put(20808, 14); // nos_lad
		MOB2.put(20991, 69); // tribe_of_swamp
	}
	private static final int[][] ADDITIONAL_REWARDS =
	{
		// @formatter:off
		{736, 15},	// SoE
		{1061, 20},	// Healing Potion
		{734, 15},	// Haste Potion
		{735, 15},	// Alacrity Potion
		{1878, 35},	// Braided Hemp
		{1875, 15},	// Stone of Purity
		{1879, 15},	// Cokes
		{1880, 15},	// Steel
		{956, 1},	// Enchant Armor D
		{955, 1},	// Enchant Weapon D
		// @formatter:on
	};
	
	public Q00354_ConquestOfAlligatorIsland()
	{
		super(354);
		registerQuestItems(ALLIGATOR_TOOTH, TORN_MAP_FRAGMENT);
		addStartNpc(30895); // Kluck
		addTalkId(30895);
		addKillId(MOB1.keySet());
		addKillId(MOB2.keySet());
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
			case "30895-02.htm":
			{
				st.startQuest();
				break;
			}
			case "30895-03.htm":
			{
				if (hasQuestItems(player, TORN_MAP_FRAGMENT))
				{
					htmltext = "30895-03a.htm";
				}
				break;
			}
			case "30895-05.htm":
			{
				final int amount = (int) getQuestItemsCount(player, ALLIGATOR_TOOTH);
				if (amount > 0)
				{
					int reward = amount * 300;
					if (amount >= 100)
					{
						final int[] add_reward = ADDITIONAL_REWARDS[Integer.parseInt(event)];
						rewardItems(player, add_reward[0], add_reward[1]);
						htmltext = "30895-05b.htm";
					}
					else
					{
						htmltext = "30895-05a.htm";
					}
					
					takeItems(player, ALLIGATOR_TOOTH, -1);
					giveAdena(player, reward, true);
				}
				break;
			}
			case "30895-07.htm":
			{
				if (getQuestItemsCount(player, TORN_MAP_FRAGMENT) >= 10)
				{
					htmltext = "30895-08.htm";
					takeItems(player, TORN_MAP_FRAGMENT, 10);
					giveItems(player, PIRATE_TREASURE_MAP, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				break;
			}
			case "30895-09.htm":
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
				htmltext = (player.getLevel() < 38) ? "30895-00.htm" : "30895-01.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (hasQuestItems(player, TORN_MAP_FRAGMENT)) ? "30895-03a.htm" : "30895-03.htm";
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(player, -1, 3, npc);
		if (qs != null)
		{
			final int npcId = npc.getId();
			if (MOB1.containsKey(npcId))
			{
				giveItemRandomly(player, npc, ALLIGATOR_TOOTH, 1, 0, MOB1.get(npcId), true);
			}
			else
			{
				final int itemCount = ((getRandom(100) < MOB2.get(npcId)) ? 2 : 1);
				giveItemRandomly(player, npc, ALLIGATOR_TOOTH, itemCount, 0, 1, true);
			}
			
			giveItemRandomly(player, npc, TORN_MAP_FRAGMENT, 1, 0, 0.1, false);
		}
		return super.onKill(npc, player, isSummon);
	}
}
