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
package quests.Q00261_CollectorsDream;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00261_CollectorsDream extends Quest
{
	// Items
	private static final int GIANT_SPIDER_LEG = 1087;
	
	public Q00261_CollectorsDream()
	{
		super(261);
		registerQuestItems(GIANT_SPIDER_LEG);
		addStartNpc(30222); // Alshupes
		addTalkId(30222);
		addKillId(20308, 20460, 20466);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final String htmltext = event;
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equals("30222-03.htm"))
		{
			st.startQuest();
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
				htmltext = (player.getLevel() < 15) ? "30222-01.htm" : "30222-02.htm";
				break;
			}
			case State.STARTED:
			{
				if (st.isCond(2))
				{
					htmltext = "30222-05.htm";
					takeItems(player, GIANT_SPIDER_LEG, -1);
					giveAdena(player, 1000, true);
					addExpAndSp(player, 2000, 0);
					st.exitQuest(true, true);
				}
				else
				{
					htmltext = "30222-04.htm";
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		final QuestState st = getQuestState(player, false);
		if ((st == null) || !st.isCond(1))
		{
			return null;
		}
		
		giveItems(player, GIANT_SPIDER_LEG, 1);
		if (getQuestItemsCount(player, GIANT_SPIDER_LEG) >= 8)
		{
			st.setCond(2, true);
		}
		else
		{
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		
		return null;
	}
}
