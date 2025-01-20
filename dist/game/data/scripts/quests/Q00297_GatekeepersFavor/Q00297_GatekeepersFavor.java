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
package quests.Q00297_GatekeepersFavor;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00297_GatekeepersFavor extends Quest
{
	// Item
	private static final int STARSTONE = 1573;
	// Reward
	private static final int GATEKEEPER_TOKEN = 1659;
	
	public Q00297_GatekeepersFavor()
	{
		super(297);
		registerQuestItems(STARSTONE);
		addStartNpc(30540); // Wirphy
		addTalkId(30540);
		addKillId(20521); // Whinstone Golem
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
		
		if (event.equals("30540-03.htm"))
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
				htmltext = (player.getLevel() < 15) ? "30540-01.htm" : "30540-02.htm";
				break;
			}
			case State.STARTED:
			{
				if (st.isCond(1))
				{
					htmltext = "30540-04.htm";
				}
				else
				{
					htmltext = "30540-05.htm";
					takeItems(player, STARSTONE, -1);
					rewardItems(player, GATEKEEPER_TOKEN, 2);
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
		
		if (getRandomBoolean())
		{
			giveItems(player, STARSTONE, 1);
			if (getQuestItemsCount(player, STARSTONE) < 20)
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
