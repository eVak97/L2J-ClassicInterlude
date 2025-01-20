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
package quests.Q00303_CollectArrowheads;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00303_CollectArrowheads extends Quest
{
	// Item
	private static final int ORCISH_ARROWHEAD = 963;
	
	public Q00303_CollectArrowheads()
	{
		super(303);
		registerQuestItems(ORCISH_ARROWHEAD);
		addStartNpc(30029); // Minia
		addTalkId(30029);
		addKillId(20361);
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
		
		if (event.equals("30029-03.htm"))
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
				htmltext = (player.getLevel() < 10) ? "30029-01.htm" : "30029-02.htm";
				break;
			}
			case State.STARTED:
			{
				if (st.isCond(1))
				{
					htmltext = "30029-04.htm";
				}
				else
				{
					htmltext = "30029-05.htm";
					takeItems(player, ORCISH_ARROWHEAD, -1);
					giveAdena(player, 1000, true);
					addExpAndSp(player, 2000, 0);
					st.exitQuest(true, true);
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
		if ((st == null) || !st.isCond(1))
		{
			return null;
		}
		
		if (getRandom(10) < 4)
		{
			giveItems(player, ORCISH_ARROWHEAD, 1);
			if (getQuestItemsCount(player, ORCISH_ARROWHEAD) < 10)
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			else
			{
				st.setCond(2, true);
			}
		}
		
		return null;
	}
}
