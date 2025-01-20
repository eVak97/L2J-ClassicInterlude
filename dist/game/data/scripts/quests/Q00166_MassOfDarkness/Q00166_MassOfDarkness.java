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
package quests.Q00166_MassOfDarkness;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00166_MassOfDarkness extends Quest
{
	// NPCs
	private static final int UNDRIAS = 30130;
	private static final int IRIA = 30135;
	private static final int DORANKUS = 30139;
	private static final int TRUDY = 30143;
	// Items
	private static final int UNDRIAS_LETTER = 1088;
	private static final int CEREMONIAL_DAGGER = 1089;
	private static final int DREVIANT_WINE = 1090;
	private static final int GARMIEL_SCRIPTURE = 1091;
	
	public Q00166_MassOfDarkness()
	{
		super(166);
		registerQuestItems(UNDRIAS_LETTER, CEREMONIAL_DAGGER, DREVIANT_WINE, GARMIEL_SCRIPTURE);
		addStartNpc(UNDRIAS);
		addTalkId(UNDRIAS, IRIA, DORANKUS, TRUDY);
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
		
		if (event.equals("30130-04.htm"))
		{
			st.startQuest();
			giveItems(player, UNDRIAS_LETTER, 1);
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
				if (player.getRace() != Race.DARK_ELF)
				{
					htmltext = "30130-00.htm";
				}
				else if (player.getLevel() < 2)
				{
					htmltext = "30130-02.htm";
				}
				else
				{
					htmltext = "30130-03.htm";
				}
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case UNDRIAS:
					{
						if (cond == 1)
						{
							htmltext = "30130-05.htm";
						}
						else if (cond == 2)
						{
							htmltext = "30130-06.htm";
							takeItems(player, CEREMONIAL_DAGGER, 1);
							takeItems(player, DREVIANT_WINE, 1);
							takeItems(player, GARMIEL_SCRIPTURE, 1);
							takeItems(player, UNDRIAS_LETTER, 1);
							giveAdena(player, 500, true);
							addExpAndSp(player, 500, 0);
							st.exitQuest(false, true);
						}
						break;
					}
					case IRIA:
					{
						if ((cond == 1) && !hasQuestItems(player, CEREMONIAL_DAGGER))
						{
							htmltext = "30135-01.htm";
							giveItems(player, CEREMONIAL_DAGGER, 1);
							if (hasQuestItems(player, DREVIANT_WINE, GARMIEL_SCRIPTURE))
							{
								st.setCond(2, true);
							}
							else
							{
								playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							}
						}
						else if (cond == 2)
						{
							htmltext = "30135-02.htm";
						}
						break;
					}
					case DORANKUS:
					{
						if ((cond == 1) && !hasQuestItems(player, DREVIANT_WINE))
						{
							htmltext = "30139-01.htm";
							giveItems(player, DREVIANT_WINE, 1);
							if (hasQuestItems(player, CEREMONIAL_DAGGER, GARMIEL_SCRIPTURE))
							{
								st.setCond(2, true);
							}
							else
							{
								playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							}
						}
						else if (cond == 2)
						{
							htmltext = "30139-02.htm";
						}
						break;
					}
					case TRUDY:
					{
						if ((cond == 1) && !hasQuestItems(player, GARMIEL_SCRIPTURE))
						{
							htmltext = "30143-01.htm";
							giveItems(player, GARMIEL_SCRIPTURE, 1);
							if (hasQuestItems(player, CEREMONIAL_DAGGER, DREVIANT_WINE))
							{
								st.setCond(2, true);
							}
							else
							{
								playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							}
						}
						else if (cond == 2)
						{
							htmltext = "30143-02.htm";
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
