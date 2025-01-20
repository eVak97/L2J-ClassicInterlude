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
package quests.Q00029_ChestCaughtWithABaitOfEarth;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

import quests.Q00052_WilliesSpecialBait.Q00052_WilliesSpecialBait;

public class Q00029_ChestCaughtWithABaitOfEarth extends Quest
{
	// NPCs
	private static final int WILLIE = 31574;
	private static final int ANABEL = 30909;
	// Items
	private static final int SMALL_PURPLE_TREASURE_CHEST = 6507;
	private static final int SMALL_GLASS_BOX = 7627;
	private static final int PLATED_LEATHER_GLOVES = 2455;
	
	public Q00029_ChestCaughtWithABaitOfEarth()
	{
		super(29);
		registerQuestItems(SMALL_GLASS_BOX);
		addStartNpc(WILLIE);
		addTalkId(WILLIE, ANABEL);
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
		
		switch (event)
		{
			case "31574-04.htm":
			{
				st.startQuest();
				break;
			}
			case "31574-07.htm":
			{
				if (hasQuestItems(player, SMALL_PURPLE_TREASURE_CHEST))
				{
					st.setCond(2);
					takeItems(player, SMALL_PURPLE_TREASURE_CHEST, 1);
					giveItems(player, SMALL_GLASS_BOX, 1);
				}
				else
				{
					htmltext = "31574-08.htm";
				}
				break;
			}
			case "30909-02.htm":
			{
				if (hasQuestItems(player, SMALL_GLASS_BOX))
				{
					htmltext = "30909-02.htm";
					takeItems(player, SMALL_GLASS_BOX, 1);
					giveItems(player, PLATED_LEATHER_GLOVES, 1);
					st.exitQuest(false, true);
				}
				else
				{
					htmltext = "30909-03.htm";
				}
				break;
			}
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
				if (player.getLevel() < 48)
				{
					htmltext = "31574-02.htm";
				}
				else
				{
					final QuestState st2 = player.getQuestState(Q00052_WilliesSpecialBait.class.getSimpleName());
					if ((st2 != null) && st2.isCompleted())
					{
						htmltext = "31574-01.htm";
					}
					else
					{
						htmltext = "31574-03.htm";
					}
				}
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case WILLIE:
					{
						if (cond == 1)
						{
							htmltext = (!hasQuestItems(player, SMALL_PURPLE_TREASURE_CHEST)) ? "31574-06.htm" : "31574-05.htm";
						}
						else if (cond == 2)
						{
							htmltext = "31574-09.htm";
						}
						break;
					}
					case ANABEL:
					{
						if (cond == 2)
						{
							htmltext = "30909-01.htm";
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
