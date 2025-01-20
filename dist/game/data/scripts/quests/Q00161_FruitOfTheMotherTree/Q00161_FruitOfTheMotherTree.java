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
package quests.Q00161_FruitOfTheMotherTree;

import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00161_FruitOfTheMotherTree extends Quest
{
	// NPCs
	private static final int ANDELLIA = 30362;
	private static final int THALIA = 30371;
	// Items
	private static final int ANDELLIA_LETTER = 1036;
	private static final int MOTHERTREE_FRUIT = 1037;
	
	public Q00161_FruitOfTheMotherTree()
	{
		super(161);
		registerQuestItems(ANDELLIA_LETTER, MOTHERTREE_FRUIT);
		addStartNpc(ANDELLIA);
		addTalkId(ANDELLIA, THALIA);
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
		
		if (event.equals("30362-04.htm"))
		{
			st.startQuest();
			giveItems(player, ANDELLIA_LETTER, 1);
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
					htmltext = "30362-00.htm";
				}
				else if (player.getLevel() < 3)
				{
					htmltext = "30362-02.htm";
				}
				else
				{
					htmltext = "30362-03.htm";
				}
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case ANDELLIA:
					{
						if (cond == 1)
						{
							htmltext = "30362-05.htm";
						}
						else if (cond == 2)
						{
							htmltext = "30362-06.htm";
							takeItems(player, MOTHERTREE_FRUIT, 1);
							giveAdena(player, 1000, true);
							addExpAndSp(player, 1000, 0);
							st.exitQuest(false, true);
						}
						break;
					}
					case THALIA:
					{
						if (cond == 1)
						{
							htmltext = "30371-01.htm";
							st.setCond(2, true);
							takeItems(player, ANDELLIA_LETTER, 1);
							giveItems(player, MOTHERTREE_FRUIT, 1);
						}
						else if (cond == 2)
						{
							htmltext = "30371-02.htm";
						}
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
