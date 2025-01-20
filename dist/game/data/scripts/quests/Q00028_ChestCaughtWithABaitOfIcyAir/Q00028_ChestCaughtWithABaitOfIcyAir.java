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
package quests.Q00028_ChestCaughtWithABaitOfIcyAir;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

import quests.Q00051_OFullesSpecialBait.Q00051_OFullesSpecialBait;

public class Q00028_ChestCaughtWithABaitOfIcyAir extends Quest
{
	// NPCs
	private static final int OFULLE = 31572;
	private static final int KIKI = 31442;
	// Items
	private static final int BIG_YELLOW_TREASURE_CHEST = 6503;
	private static final int KIKI_LETTER = 7626;
	private static final int ELVEN_RING = 881;
	
	public Q00028_ChestCaughtWithABaitOfIcyAir()
	{
		super(28);
		registerQuestItems(KIKI_LETTER);
		addStartNpc(OFULLE);
		addTalkId(OFULLE, KIKI);
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
			case "31572-04.htm":
			{
				st.startQuest();
				break;
			}
			case "31572-07.htm":
			{
				if (hasQuestItems(player, BIG_YELLOW_TREASURE_CHEST))
				{
					st.setCond(2);
					takeItems(player, BIG_YELLOW_TREASURE_CHEST, 1);
					giveItems(player, KIKI_LETTER, 1);
				}
				else
				{
					htmltext = "31572-08.htm";
				}
				break;
			}
			case "31442-02.htm":
			{
				if (hasQuestItems(player, KIKI_LETTER))
				{
					htmltext = "31442-02.htm";
					takeItems(player, KIKI_LETTER, 1);
					giveItems(player, ELVEN_RING, 1);
					st.exitQuest(false, true);
				}
				else
				{
					htmltext = "31442-03.htm";
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
				if (player.getLevel() < 36)
				{
					htmltext = "31572-02.htm";
				}
				else
				{
					final QuestState st2 = player.getQuestState(Q00051_OFullesSpecialBait.class.getSimpleName());
					if ((st2 != null) && st2.isCompleted())
					{
						htmltext = "31572-01.htm";
					}
					else
					{
						htmltext = "31572-03.htm";
					}
				}
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case OFULLE:
					{
						if (cond == 1)
						{
							htmltext = (!hasQuestItems(player, BIG_YELLOW_TREASURE_CHEST)) ? "31572-06.htm" : "31572-05.htm";
						}
						else if (cond == 2)
						{
							htmltext = "31572-09.htm";
						}
						break;
					}
					case KIKI:
					{
						if (cond == 2)
						{
							htmltext = "31442-01.htm";
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
