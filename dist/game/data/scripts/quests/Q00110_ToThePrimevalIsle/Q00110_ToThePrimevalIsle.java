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
package quests.Q00110_ToThePrimevalIsle;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00110_ToThePrimevalIsle extends Quest
{
	// NPCs
	private static final int ANTON = 31338;
	private static final int MARQUEZ = 32113;
	// Item
	private static final int ANCIENT_BOOK = 8777;
	
	public Q00110_ToThePrimevalIsle()
	{
		super(110);
		registerQuestItems(ANCIENT_BOOK);
		addStartNpc(ANTON);
		addTalkId(ANTON, MARQUEZ);
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
		
		if (event.equals("31338-02.htm"))
		{
			st.startQuest();
			giveItems(player, ANCIENT_BOOK, 1);
		}
		else if (event.equals("32113-03.htm") && hasQuestItems(player, ANCIENT_BOOK))
		{
			takeItems(player, ANCIENT_BOOK, 1);
			giveAdena(player, 169380, true);
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
				htmltext = (player.getLevel() < 75) ? "31338-00.htm" : "31338-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case ANTON:
					{
						htmltext = "31338-01c.htm";
						break;
					}
					case MARQUEZ:
					{
						htmltext = "32113-01.htm";
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
