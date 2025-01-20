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
package quests.Q00007_ATripBegins;

import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00007_ATripBegins extends Quest
{
	// NPCs
	private static final int MIRABEL = 30146;
	private static final int ARIEL = 30148;
	private static final int ASTERIOS = 30154;
	// Items
	private static final int ARIEL_RECO = 7572;
	// Rewards
	private static final int MARK_TRAVELER = 7570;
	private static final int SOE_GIRAN = 7559;
	
	public Q00007_ATripBegins()
	{
		super(7);
		registerQuestItems(ARIEL_RECO);
		addStartNpc(MIRABEL);
		addTalkId(MIRABEL, ARIEL, ASTERIOS);
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
			case "30146-03.htm":
			{
				st.startQuest();
				break;
			}
			case "30148-02.htm":
			{
				st.setCond(2, true);
				giveItems(player, ARIEL_RECO, 1);
				break;
			}
			case "30154-02.htm":
			{
				st.setCond(3, true);
				takeItems(player, ARIEL_RECO, 1);
				break;
			}
			case "30146-06.htm":
			{
				giveItems(player, MARK_TRAVELER, 1);
				rewardItems(player, SOE_GIRAN, 1);
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
				if (player.getRace() != Race.ELF)
				{
					htmltext = "30146-01.htm";
				}
				else if (player.getLevel() < 3)
				{
					htmltext = "30146-01a.htm";
				}
				else
				{
					htmltext = "30146-02.htm";
				}
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case MIRABEL:
					{
						if ((cond == 1) || (cond == 2))
						{
							htmltext = "30146-04.htm";
						}
						else if (cond == 3)
						{
							htmltext = "30146-05.htm";
						}
						break;
					}
					case ARIEL:
					{
						if (cond == 1)
						{
							htmltext = "30148-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "30148-03.htm";
						}
						break;
					}
					case ASTERIOS:
					{
						if (cond == 2)
						{
							htmltext = "30154-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = "30154-03.htm";
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
