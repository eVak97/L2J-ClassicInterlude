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
package quests.Q00167_DwarvenKinship;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00167_DwarvenKinship extends Quest
{
	// NPCs
	private static final int CARLON = 30350;
	private static final int NORMAN = 30210;
	private static final int HAPROCK = 30255;
	// Items
	private static final int CARLON_LETTER = 1076;
	private static final int NORMAN_LETTER = 1106;
	
	public Q00167_DwarvenKinship()
	{
		super(167);
		registerQuestItems(CARLON_LETTER, NORMAN_LETTER);
		addStartNpc(CARLON);
		addTalkId(CARLON, HAPROCK, NORMAN);
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
			case "30350-04.htm":
			{
				st.startQuest();
				giveItems(player, CARLON_LETTER, 1);
				break;
			}
			case "30255-03.htm":
			{
				st.setCond(2);
				takeItems(player, CARLON_LETTER, 1);
				giveItems(player, NORMAN_LETTER, 1);
				giveAdena(player, 2000, true);
				break;
			}
			case "30255-04.htm":
			{
				takeItems(player, CARLON_LETTER, 1);
				giveAdena(player, 3000, true);
				st.exitQuest(false, true);
				break;
			}
			case "30210-02.htm":
			{
				takeItems(player, NORMAN_LETTER, 1);
				giveAdena(player, 20000, true);
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
				htmltext = (player.getLevel() < 15) ? "30350-02.htm" : "30350-03.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case CARLON:
					{
						if (cond == 1)
						{
							htmltext = "30350-05.htm";
						}
						break;
					}
					case HAPROCK:
					{
						if (cond == 1)
						{
							htmltext = "30255-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "30255-05.htm";
						}
						break;
					}
					case NORMAN:
					{
						if (cond == 2)
						{
							htmltext = "30210-01.htm";
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
