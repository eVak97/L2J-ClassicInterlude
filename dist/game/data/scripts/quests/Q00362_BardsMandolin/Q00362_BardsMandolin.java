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
package quests.Q00362_BardsMandolin;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00362_BardsMandolin extends Quest
{
	// NPCs
	private static final int SWAN = 30957;
	private static final int NANARIN = 30956;
	private static final int GALION = 30958;
	private static final int WOODROW = 30837;
	// Items
	private static final int SWAN_FLUTE = 4316;
	private static final int SWAN_LETTER = 4317;
	
	public Q00362_BardsMandolin()
	{
		super(362);
		registerQuestItems(SWAN_FLUTE, SWAN_LETTER);
		addStartNpc(SWAN);
		addTalkId(SWAN, NANARIN, GALION, WOODROW);
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
		
		if (event.equals("30957-3.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("30957-7.htm") || event.equals("30957-8.htm"))
		{
			giveAdena(player, 10000, true);
			giveItems(player, 4410, 1);
			st.exitQuest(true, true);
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
				htmltext = (player.getLevel() < 15) ? "30957-2.htm" : "30957-1.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case SWAN:
					{
						if ((cond == 1) || (cond == 2))
						{
							htmltext = "30957-4.htm";
						}
						else if (cond == 3)
						{
							htmltext = "30957-5.htm";
							st.setCond(4, true);
							giveItems(player, SWAN_LETTER, 1);
						}
						else if (cond == 4)
						{
							htmltext = "30957-5a.htm";
						}
						else if (cond == 5)
						{
							htmltext = "30957-6.htm";
						}
						break;
					}
					case WOODROW:
					{
						if (cond == 1)
						{
							htmltext = "30837-1.htm";
							st.setCond(2, true);
						}
						else if (cond == 2)
						{
							htmltext = "30837-2.htm";
						}
						else if (cond > 2)
						{
							htmltext = "30837-3.htm";
						}
						break;
					}
					case GALION:
					{
						if (cond == 2)
						{
							htmltext = "30958-1.htm";
							st.setCond(3);
							playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							giveItems(player, SWAN_FLUTE, 1);
						}
						else if (cond > 2)
						{
							htmltext = "30958-2.htm";
						}
						break;
					}
					case NANARIN:
					{
						if (cond == 4)
						{
							htmltext = "30956-1.htm";
							st.setCond(5, true);
							takeItems(player, SWAN_FLUTE, 1);
							takeItems(player, SWAN_LETTER, 1);
						}
						else if (cond == 5)
						{
							htmltext = "30956-2.htm";
						}
						break;
					}
				}
				break;
			}
		}
		
		return htmltext;
	}
}
