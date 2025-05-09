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
package quests.Q00329_CuriosityOfADwarf;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00329_CuriosityOfADwarf extends Quest
{
	// Items
	private static final int GOLEM_HEARTSTONE = 1346;
	private static final int BROKEN_HEARTSTONE = 1365;
	
	public Q00329_CuriosityOfADwarf()
	{
		super(329);
		addStartNpc(30437); // Rolento
		addTalkId(30437);
		addKillId(20083, 20085); // Granite golem, Puncher
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
		
		if (event.equals("30437-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("30437-06.htm"))
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
				htmltext = (player.getLevel() < 33) ? "30437-01.htm" : "30437-02.htm";
				break;
			}
			case State.STARTED:
			{
				final int golem = (int) getQuestItemsCount(player, GOLEM_HEARTSTONE);
				final int broken = (int) getQuestItemsCount(player, BROKEN_HEARTSTONE);
				if ((golem + broken) == 0)
				{
					htmltext = "30437-04.htm";
				}
				else
				{
					htmltext = "30437-05.htm";
					takeItems(player, GOLEM_HEARTSTONE, -1);
					takeItems(player, BROKEN_HEARTSTONE, -1);
					giveAdena(player, (broken * 50) + (golem * 1000) + (((golem + broken) > 10) ? 1183 : 0), true);
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
		
		final int chance = getRandom(100);
		if (chance < 2)
		{
			giveItems(player, GOLEM_HEARTSTONE, 1);
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		else if (chance < ((npc.getId() == 20083) ? 44 : 50))
		{
			giveItems(player, BROKEN_HEARTSTONE, 1);
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		
		return null;
	}
}
