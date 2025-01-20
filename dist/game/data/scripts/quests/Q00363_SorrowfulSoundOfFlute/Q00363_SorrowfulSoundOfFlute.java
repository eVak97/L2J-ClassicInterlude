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
package quests.Q00363_SorrowfulSoundOfFlute;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00363_SorrowfulSoundOfFlute extends Quest
{
	// NPCs
	private static final int NANARIN = 30956;
	private static final int OPIX = 30595;
	private static final int ALDO = 30057;
	private static final int RANSPO = 30594;
	private static final int HOLVAS = 30058;
	private static final int BARBADO = 30959;
	private static final int POITAN = 30458;
	// Items
	private static final int NANARIN_FLUTE = 4319;
	private static final int BLACK_BEER = 4320;
	private static final int CLOTHES = 4318;
	// Reward
	private static final int THEME_OF_SOLITUDE = 4420;
	
	public Q00363_SorrowfulSoundOfFlute()
	{
		super(363);
		registerQuestItems(NANARIN_FLUTE, BLACK_BEER, CLOTHES);
		addStartNpc(NANARIN);
		addTalkId(NANARIN, OPIX, ALDO, RANSPO, HOLVAS, BARBADO, POITAN);
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
			case "30956-02.htm":
			{
				st.startQuest();
				break;
			}
			case "30956-05.htm":
			{
				st.setCond(3, true);
				giveItems(player, CLOTHES, 1);
				break;
			}
			case "30956-06.htm":
			{
				st.setCond(3, true);
				giveItems(player, NANARIN_FLUTE, 1);
				break;
			}
			case "30956-07.htm":
			{
				st.setCond(3, true);
				giveItems(player, BLACK_BEER, 1);
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
				htmltext = (player.getLevel() < 15) ? "30956-03.htm" : "30956-01.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case NANARIN:
					{
						if (cond == 1)
						{
							htmltext = "30956-02.htm";
						}
						else if (cond == 2)
						{
							htmltext = "30956-04.htm";
						}
						else if (cond == 3)
						{
							htmltext = "30956-08.htm";
						}
						else if (cond == 4)
						{
							if (st.getInt("success") == 1)
							{
								htmltext = "30956-09.htm";
								giveItems(player, THEME_OF_SOLITUDE, 1);
								playSound(player, QuestSound.ITEMSOUND_QUEST_FINISH);
							}
							else
							{
								htmltext = "30956-10.htm";
								playSound(player, QuestSound.ITEMSOUND_QUEST_GIVEUP);
							}
							st.exitQuest(true, true);
						}
						break;
					}
					case OPIX:
					case POITAN:
					case ALDO:
					case RANSPO:
					case HOLVAS:
					{
						htmltext = npc.getId() + "-01.htm";
						if (cond == 1)
						{
							st.setCond(2, true);
						}
						break;
					}
					case BARBADO:
					{
						if (cond == 3)
						{
							st.setCond(4, true);
							
							if (hasQuestItems(player, NANARIN_FLUTE))
							{
								htmltext = "30959-02.htm";
								st.set("success", "1");
							}
							else
							{
								htmltext = "30959-01.htm";
							}
							
							takeItems(player, BLACK_BEER, -1);
							takeItems(player, CLOTHES, -1);
							takeItems(player, NANARIN_FLUTE, -1);
						}
						else if (cond == 4)
						{
							htmltext = "30959-03.htm";
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
