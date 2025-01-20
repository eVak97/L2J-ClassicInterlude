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
package quests.Q00012_SecretMeetingWithVarkaSilenos;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00012_SecretMeetingWithVarkaSilenos extends Quest
{
	// NPCs
	private static final int CADMON = 31296;
	private static final int HELMUT = 31258;
	private static final int NARAN_ASHANUK = 31378;
	// Items
	private static final int MUNITIONS_BOX = 7232;
	
	public Q00012_SecretMeetingWithVarkaSilenos()
	{
		super(12);
		registerQuestItems(MUNITIONS_BOX);
		addStartNpc(CADMON);
		addTalkId(CADMON, HELMUT, NARAN_ASHANUK);
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
			case "31296-03.htm":
			{
				st.startQuest();
				break;
			}
			case "31258-02.htm":
			{
				giveItems(player, MUNITIONS_BOX, 1);
				st.setCond(2, true);
				break;
			}
			case "31378-02.htm":
			{
				takeItems(player, MUNITIONS_BOX, 1);
				addExpAndSp(player, 79761, 0);
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
				htmltext = (player.getLevel() < 74) ? "31296-02.htm" : "31296-01.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case CADMON:
					{
						if (cond == 1)
						{
							htmltext = "31296-04.htm";
						}
						break;
					}
					case HELMUT:
					{
						if (cond == 1)
						{
							htmltext = "31258-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "31258-03.htm";
						}
						break;
					}
					case NARAN_ASHANUK:
					{
						if (cond == 2)
						{
							htmltext = "31378-01.htm";
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
