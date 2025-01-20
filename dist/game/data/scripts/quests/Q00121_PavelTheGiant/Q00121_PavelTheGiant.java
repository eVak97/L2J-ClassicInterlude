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
package quests.Q00121_PavelTheGiant;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00121_PavelTheGiant extends Quest
{
	// NPCs
	private static final int NEWYEAR = 31961;
	private static final int YUMI = 32041;
	
	public Q00121_PavelTheGiant()
	{
		super(121);
		addStartNpc(NEWYEAR);
		addTalkId(NEWYEAR, YUMI);
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
		
		if (event.equals("31961-2.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("32041-2.htm"))
		{
			addExpAndSp(player, 10000, 0);
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
				htmltext = (player.getLevel() < 46) ? "31961-1a.htm" : "31961-1.htm";
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case NEWYEAR:
					{
						htmltext = "31961-2a.htm";
						break;
					}
					case YUMI:
					{
						htmltext = "32041-1.htm";
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
