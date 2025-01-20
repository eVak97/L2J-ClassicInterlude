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
package quests.Q00027_ChestCaughtWithABaitOfWind;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

import quests.Q00050_LanoscosSpecialBait.Q00050_LanoscosSpecialBait;

public class Q00027_ChestCaughtWithABaitOfWind extends Quest
{
	// NPCs
	private static final int LANOSCO = 31570;
	private static final int SHALING = 31434;
	
	// Items
	private static final int LARGE_BLUE_TREASURE_CHEST = 6500;
	private static final int STRANGE_BLUEPRINT = 7625;
	private static final int BLACK_PEARL_RING = 880;
	
	public Q00027_ChestCaughtWithABaitOfWind()
	{
		super(27);
		
		registerQuestItems(STRANGE_BLUEPRINT);
		
		addStartNpc(LANOSCO);
		addTalkId(LANOSCO, SHALING);
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
			case "31570-04.htm":
			{
				st.startQuest();
				break;
			}
			case "31570-07.htm":
			{
				if (hasQuestItems(player, LARGE_BLUE_TREASURE_CHEST))
				{
					st.setCond(2);
					takeItems(player, LARGE_BLUE_TREASURE_CHEST, 1);
					giveItems(player, STRANGE_BLUEPRINT, 1);
				}
				else
				{
					htmltext = "31570-08.htm";
				}
				break;
			}
			case "31434-02.htm":
			{
				if (hasQuestItems(player, STRANGE_BLUEPRINT))
				{
					htmltext = "31434-02.htm";
					takeItems(player, STRANGE_BLUEPRINT, 1);
					giveItems(player, BLACK_PEARL_RING, 1);
					st.exitQuest(false, true);
				}
				else
				{
					htmltext = "31434-03.htm";
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
				if (player.getLevel() < 27)
				{
					htmltext = "31570-02.htm";
				}
				else
				{
					final QuestState st2 = player.getQuestState(Q00050_LanoscosSpecialBait.class.getSimpleName());
					if ((st2 != null) && st2.isCompleted())
					{
						htmltext = "31570-01.htm";
					}
					else
					{
						htmltext = "31570-03.htm";
					}
				}
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case LANOSCO:
					{
						if (cond == 1)
						{
							htmltext = (!hasQuestItems(player, LARGE_BLUE_TREASURE_CHEST)) ? "31570-06.htm" : "31570-05.htm";
						}
						else if (cond == 2)
						{
							htmltext = "31570-09.htm";
						}
						break;
					}
					case SHALING:
					{
						if (cond == 2)
						{
							htmltext = "31434-01.htm";
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
