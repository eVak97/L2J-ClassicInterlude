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
package quests.Q00661_MakingTheHarvestGroundsSafe;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00661_MakingTheHarvestGroundsSafe extends Quest
{
	// NPC
	private static final int NORMAN = 30210;
	// Monsters
	private static final int GIANT_POISON_BEE = 21095;
	private static final int CLOUDY_BEAST = 21096;
	private static final int YOUNG_ARANEID = 21097;
	// Items
	private static final int STING_OF_GIANT_POISON_BEE = 8283;
	private static final int CLOUDY_GEM = 8284;
	private static final int TALON_OF_YOUNG_ARANEID = 8285;
	// Reward
	private static final int ADENA = 57;
	
	public Q00661_MakingTheHarvestGroundsSafe()
	{
		super(661);
		registerQuestItems(STING_OF_GIANT_POISON_BEE, CLOUDY_GEM, TALON_OF_YOUNG_ARANEID);
		addStartNpc(NORMAN);
		addTalkId(NORMAN);
		addKillId(GIANT_POISON_BEE, CLOUDY_BEAST, YOUNG_ARANEID);
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
			case "30210-02.htm":
			{
				st.startQuest();
				break;
			}
			case "30210-04.htm":
			{
				final int item1 = (int) getQuestItemsCount(player, STING_OF_GIANT_POISON_BEE);
				final int item2 = (int) getQuestItemsCount(player, CLOUDY_GEM);
				final int item3 = (int) getQuestItemsCount(player, TALON_OF_YOUNG_ARANEID);
				int sum = 0;
				sum = (item1 * 57) + (item2 * 56) + (item3 * 60);
				if ((item1 + item2 + item3) >= 10)
				{
					sum += 2871;
				}
				takeItems(player, STING_OF_GIANT_POISON_BEE, item1);
				takeItems(player, CLOUDY_GEM, item2);
				takeItems(player, TALON_OF_YOUNG_ARANEID, item3);
				rewardItems(player, ADENA, sum);
				break;
			}
			case "30210-06.htm":
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
				htmltext = (player.getLevel() < 21) ? "30210-01a.htm" : "30210-01.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (hasAtLeastOneQuestItem(player, STING_OF_GIANT_POISON_BEE, CLOUDY_GEM, TALON_OF_YOUNG_ARANEID)) ? "30210-03.htm" : "30210-05.htm";
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		final QuestState st = getQuestState(player, false);
		if ((st == null) || !st.isStarted())
		{
			return null;
		}
		
		if (getRandomBoolean())
		{
			giveItems(player, npc.getId() - 12812, 1);
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		
		return null;
	}
}
