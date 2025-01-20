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
package quests.Q00043_HelpTheSister;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00043_HelpTheSister extends Quest
{
	// NPCs
	private static final int COOPER = 30829;
	private static final int GALLADUCCI = 30097;
	// Monsters
	private static final int SPECTER = 20171;
	private static final int SORROW_MAIDEN = 20197;
	// Items
	private static final int CRAFTED_DAGGER = 220;
	private static final int MAP_PIECE = 7550;
	private static final int MAP = 7551;
	private static final int PET_TICKET = 7584;
	
	public Q00043_HelpTheSister()
	{
		super(43);
		registerQuestItems(MAP_PIECE, MAP);
		addStartNpc(COOPER);
		addTalkId(COOPER, GALLADUCCI);
		addKillId(SPECTER, SORROW_MAIDEN);
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
			case "30829-01.htm":
			{
				st.startQuest();
				break;
			}
			case "30829-03.htm":
			{
				if (hasQuestItems(player, CRAFTED_DAGGER))
				{
					st.setCond(2, true);
					takeItems(player, CRAFTED_DAGGER, 1);
				}
				break;
			}
			case "30829-05.htm":
			{
				st.setCond(4, true);
				takeItems(player, MAP_PIECE, 30);
				giveItems(player, MAP, 1);
				break;
			}
			case "30097-06.htm":
			{
				st.setCond(5, true);
				takeItems(player, MAP, 1);
				break;
			}
			case "30829-07.htm":
			{
				giveItems(player, PET_TICKET, 1);
				st.exitQuest(false, true);
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
				htmltext = (player.getLevel() < 26) ? "30829-00a.htm" : "30829-00.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case COOPER:
					{
						if (cond == 1)
						{
							htmltext = (!hasQuestItems(player, CRAFTED_DAGGER)) ? "30829-01a.htm" : "30829-02.htm";
						}
						else if (cond == 2)
						{
							htmltext = "30829-03a.htm";
						}
						else if (cond == 3)
						{
							htmltext = "30829-04.htm";
						}
						else if (cond == 4)
						{
							htmltext = "30829-05a.htm";
						}
						else if (cond == 5)
						{
							htmltext = "30829-06.htm";
						}
						break;
					}
					case GALLADUCCI:
					{
						if (cond == 4)
						{
							htmltext = "30097-05.htm";
						}
						else if (cond == 5)
						{
							htmltext = "30097-06a.htm";
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
	
	@Override
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(2))
		{
			giveItems(player, MAP_PIECE, 1);
			if (getQuestItemsCount(player, MAP_PIECE) == 30)
			{
				qs.setCond(3, true);
			}
			else
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		return super.onKill(npc, player, isSummon);
	}
}
