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
package quests.Q00156_MillenniumLove;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00156_MillenniumLove extends Quest
{
	// NPCs
	private static final int LILITH = 30368;
	private static final int BAENEDES = 30369;
	// Items
	private static final int LILITH_LETTER = 1022;
	private static final int THEON_DIARY = 1023;
	
	public Q00156_MillenniumLove()
	{
		super(156);
		registerQuestItems(LILITH_LETTER, THEON_DIARY);
		addStartNpc(LILITH);
		addTalkId(LILITH, BAENEDES);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final String htmltext = event;
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "30368-04.htm":
			{
				st.startQuest();
				giveItems(player, LILITH_LETTER, 1);
				break;
			}
			case "30369-02.htm":
			{
				st.setCond(2, true);
				takeItems(player, LILITH_LETTER, 1);
				giveItems(player, THEON_DIARY, 1);
				break;
			}
			case "30369-03.htm":
			{
				takeItems(player, LILITH_LETTER, 1);
				addExpAndSp(player, 3000, 0);
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
				htmltext = (player.getLevel() < 15) ? "30368-00.htm" : "30368-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case LILITH:
					{
						if (hasQuestItems(player, LILITH_LETTER))
						{
							htmltext = "30368-05.htm";
						}
						else if (hasQuestItems(player, THEON_DIARY))
						{
							htmltext = "30368-06.htm";
							takeItems(player, THEON_DIARY, 1);
							giveItems(player, 5250, 1);
							addExpAndSp(player, 3000, 0);
							st.exitQuest(false, true);
						}
						break;
					}
					case BAENEDES:
					{
						if (hasQuestItems(player, LILITH_LETTER))
						{
							htmltext = "30369-01.htm";
						}
						else if (hasQuestItems(player, THEON_DIARY))
						{
							htmltext = "30369-04.htm";
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
