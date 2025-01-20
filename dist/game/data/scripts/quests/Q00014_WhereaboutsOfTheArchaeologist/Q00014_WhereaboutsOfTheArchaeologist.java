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
package quests.Q00014_WhereaboutsOfTheArchaeologist;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00014_WhereaboutsOfTheArchaeologist extends Quest
{
	// NPCs
	private static final int LIESEL = 31263;
	private static final int GHOST_OF_ADVENTURER = 31538;
	// Items
	private static final int LETTER = 7253;
	
	public Q00014_WhereaboutsOfTheArchaeologist()
	{
		super(14);
		registerQuestItems(LETTER);
		addStartNpc(LIESEL);
		addTalkId(LIESEL, GHOST_OF_ADVENTURER);
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
		
		if (event.equals("31263-2.htm"))
		{
			st.startQuest();
			giveItems(player, LETTER, 1);
		}
		else if (event.equals("31538-1.htm"))
		{
			takeItems(player, LETTER, 1);
			giveAdena(player, 113228, true);
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
				htmltext = (player.getLevel() < 74) ? "31263-1.htm" : "31263-0.htm";
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case LIESEL:
					{
						htmltext = "31263-2.htm";
						break;
					}
					case GHOST_OF_ADVENTURER:
					{
						htmltext = "31538-0.htm";
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