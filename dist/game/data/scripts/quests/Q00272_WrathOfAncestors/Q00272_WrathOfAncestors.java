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
package quests.Q00272_WrathOfAncestors;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00272_WrathOfAncestors extends Quest
{
	// Item
	private static final int GRAVE_ROBBERS_HEAD = 1474;
	
	public Q00272_WrathOfAncestors()
	{
		super(272);
		registerQuestItems(GRAVE_ROBBERS_HEAD);
		addStartNpc(30572); // Livina
		addTalkId(30572);
		addKillId(20319, 20320); // Goblin Grave Robber, Goblin Tomb Raider Leader
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
		
		if (event.equals("30572-03.htm"))
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
				if (player.getRace() != Race.ORC)
				{
					htmltext = "30572-00.htm";
				}
				else if (player.getLevel() < 5)
				{
					htmltext = "30572-01.htm";
				}
				else
				{
					htmltext = "30572-02.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (st.isCond(1))
				{
					htmltext = "30572-04.htm";
				}
				else
				{
					htmltext = "30572-05.htm";
					takeItems(player, GRAVE_ROBBERS_HEAD, -1);
					giveAdena(player, 1500, true);
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
		
		giveItems(player, GRAVE_ROBBERS_HEAD, 1);
		if (getQuestItemsCount(player, GRAVE_ROBBERS_HEAD) < 50)
		{
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		else
		{
			st.setCond(2, true);
		}
		
		return null;
	}
}
