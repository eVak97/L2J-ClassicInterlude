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
package quests.Q00119_LastImperialPrince;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00119_LastImperialPrince extends Quest
{
	// NPCs
	private static final int NAMELESS_SPIRIT = 31453;
	private static final int DEVORIN = 32009;
	// Item
	private static final int ANTIQUE_BROOCH = 7262;
	
	public Q00119_LastImperialPrince()
	{
		super(119);
		registerQuestItems(ANTIQUE_BROOCH);
		addStartNpc(NAMELESS_SPIRIT);
		addTalkId(NAMELESS_SPIRIT, DEVORIN);
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
			case "31453-04.htm":
			{
				if (hasQuestItems(player, ANTIQUE_BROOCH))
				{
					st.startQuest();
				}
				else
				{
					htmltext = "31453-04b.htm";
					st.exitQuest(true, true);
				}
				break;
			}
			case "32009-02.htm":
			{
				if (!hasQuestItems(player, ANTIQUE_BROOCH))
				{
					htmltext = "31453-02a.htm";
					st.exitQuest(true, true);
				}
				break;
			}
			case "32009-03.htm":
			{
				st.setCond(2, true);
				break;
			}
			case "31453-07.htm":
			{
				giveAdena(player, 68787, true);
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
				htmltext = (!hasQuestItems(player, ANTIQUE_BROOCH) || (player.getLevel() < 74)) ? "31453-00a.htm" : "31453-01.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case NAMELESS_SPIRIT:
					{
						if (cond == 1)
						{
							htmltext = "31453-04a.htm";
						}
						else if (cond == 2)
						{
							htmltext = "31453-05.htm";
						}
						break;
					}
					case DEVORIN:
					{
						if (cond == 1)
						{
							htmltext = "32009-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "32009-04.htm";
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = "31453-00b.htm";
				break;
			}
		}
		
		return htmltext;
	}
}
