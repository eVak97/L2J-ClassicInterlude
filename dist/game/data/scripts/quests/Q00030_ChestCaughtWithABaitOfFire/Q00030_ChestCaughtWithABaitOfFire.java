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
package quests.Q00030_ChestCaughtWithABaitOfFire;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

import quests.Q00053_LinnaeusSpecialBait.Q00053_LinnaeusSpecialBait;

public class Q00030_ChestCaughtWithABaitOfFire extends Quest
{
	// NPCs
	private static final int LINNAEUS = 31577;
	private static final int RUKAL = 30629;
	// Items
	private static final int RED_TREASURE_BOX = 6511;
	private static final int MUSICAL_SCORE = 7628;
	private static final int NECKLACE_OF_PROTECTION = 916;
	
	public Q00030_ChestCaughtWithABaitOfFire()
	{
		super(30);
		registerQuestItems(MUSICAL_SCORE);
		addStartNpc(LINNAEUS);
		addTalkId(LINNAEUS, RUKAL);
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
			case "31577-04.htm":
			{
				st.startQuest();
				break;
			}
			case "31577-07.htm":
			{
				if (hasQuestItems(player, RED_TREASURE_BOX))
				{
					st.setCond(2);
					takeItems(player, RED_TREASURE_BOX, 1);
					giveItems(player, MUSICAL_SCORE, 1);
				}
				else
				{
					htmltext = "31577-08.htm";
				}
				break;
			}
			case "30629-02.htm":
			{
				if (hasQuestItems(player, MUSICAL_SCORE))
				{
					htmltext = "30629-02.htm";
					takeItems(player, MUSICAL_SCORE, 1);
					giveItems(player, NECKLACE_OF_PROTECTION, 1);
					st.exitQuest(false, true);
				}
				else
				{
					htmltext = "30629-03.htm";
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
				if (player.getLevel() < 60)
				{
					htmltext = "31577-02.htm";
				}
				else
				{
					final QuestState st2 = player.getQuestState(Q00053_LinnaeusSpecialBait.class.getSimpleName());
					if ((st2 != null) && st2.isCompleted())
					{
						htmltext = "31577-01.htm";
					}
					else
					{
						htmltext = "31577-03.htm";
					}
				}
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case LINNAEUS:
					{
						if (cond == 1)
						{
							htmltext = (!hasQuestItems(player, RED_TREASURE_BOX)) ? "31577-06.htm" : "31577-05.htm";
						}
						else if (cond == 2)
						{
							htmltext = "31577-09.htm";
						}
						break;
					}
					case RUKAL:
					{
						if (cond == 2)
						{
							htmltext = "30629-01.htm";
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
