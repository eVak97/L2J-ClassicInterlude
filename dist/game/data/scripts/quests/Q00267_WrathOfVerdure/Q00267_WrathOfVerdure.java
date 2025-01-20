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
package quests.Q00267_WrathOfVerdure;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00267_WrathOfVerdure extends Quest
{
	// Items
	private static final int GOBLIN_CLUB = 1335;
	// Reward
	private static final int SILVERY_LEAF = 1340;
	
	public Q00267_WrathOfVerdure()
	{
		super(267);
		registerQuestItems(GOBLIN_CLUB);
		addStartNpc(31853); // Bremec
		addTalkId(31853);
		addKillId(20325); // Goblin
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
		
		if (event.equals("31853-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("31853-06.htm"))
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
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				if (player.getRace() != Race.ELF)
				{
					htmltext = "31853-00.htm";
				}
				else if (player.getLevel() < 4)
				{
					htmltext = "31853-01.htm";
				}
				else
				{
					htmltext = "31853-02.htm";
				}
				break;
			}
			case State.STARTED:
			{
				final int count = (int) getQuestItemsCount(player, GOBLIN_CLUB);
				if (count > 0)
				{
					htmltext = "31853-05.htm";
					takeItems(player, GOBLIN_CLUB, -1);
					rewardItems(player, SILVERY_LEAF, count);
					if (!Config.ALT_VILLAGES_REPEATABLE_QUEST_REWARD && (count >= 10))
					{
						giveAdena(player, 600, true);
					}
				}
				else
				{
					htmltext = "31853-04.htm";
				}
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
			giveItems(player, GOBLIN_CLUB, 1);
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		
		return null;
	}
}
