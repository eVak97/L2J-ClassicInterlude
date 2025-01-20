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
package quests.Q00013_ParcelDelivery;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00013_ParcelDelivery extends Quest
{
	// NPCs
	private static final int FUNDIN = 31274;
	private static final int VULCAN = 31539;
	// Item
	private static final int PACKAGE = 7263;
	
	public Q00013_ParcelDelivery()
	{
		super(13);
		registerQuestItems(PACKAGE);
		addStartNpc(FUNDIN);
		addTalkId(FUNDIN, VULCAN);
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
		
		if (event.equals("31274-2.htm"))
		{
			st.startQuest();
			giveItems(player, PACKAGE, 1);
		}
		else if (event.equals("31539-1.htm"))
		{
			takeItems(player, PACKAGE, 1);
			giveAdena(player, 82656, true);
			st.exitQuest(false, true);
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
				htmltext = (player.getLevel() < 74) ? "31274-1.htm" : "31274-0.htm";
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case FUNDIN:
					{
						htmltext = "31274-2.htm";
						break;
					}
					case VULCAN:
					{
						htmltext = "31539-0.htm";
						break;
					}
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
}
