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
package quests.Q00407_PathOfTheElvenScout;

import org.l2jmobius.gameserver.enums.ClassId;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.network.serverpackets.SocialAction;

public class Q00407_PathOfTheElvenScout extends Quest
{
	// NPCs
	private static final int REISA = 30328;
	private static final int BABENCO = 30334;
	private static final int MORETTI = 30337;
	private static final int PRIAS = 30426;
	// Items
	private static final int REISA_LETTER = 1207;
	private static final int PRIAS_TORN_LETTER_1 = 1208;
	private static final int PRIAS_TORN_LETTER_2 = 1209;
	private static final int PRIAS_TORN_LETTER_3 = 1210;
	private static final int PRIAS_TORN_LETTER_4 = 1211;
	private static final int MORETTI_HERB = 1212;
	private static final int MORETTI_LETTER = 1214;
	private static final int PRIAS_LETTER = 1215;
	private static final int HONORARY_GUARD = 1216;
	private static final int REISA_RECOMMENDATION = 1217;
	private static final int RUSTED_KEY = 1293;
	
	public Q00407_PathOfTheElvenScout()
	{
		super(407);
		registerQuestItems(REISA_LETTER, PRIAS_TORN_LETTER_1, PRIAS_TORN_LETTER_2, PRIAS_TORN_LETTER_3, PRIAS_TORN_LETTER_4, MORETTI_HERB, MORETTI_LETTER, PRIAS_LETTER, HONORARY_GUARD, RUSTED_KEY);
		addStartNpc(REISA);
		addTalkId(REISA, MORETTI, BABENCO, PRIAS);
		addKillId(20053, 27031);
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
		
		if (event.equals("30328-05.htm"))
		{
			if (player.getClassId() != ClassId.ELVEN_FIGHTER)
			{
				htmltext = (player.getClassId() == ClassId.ELVEN_SCOUT) ? "30328-02a.htm" : "30328-02.htm";
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "30328-03.htm";
			}
			else if (hasQuestItems(player, REISA_RECOMMENDATION))
			{
				htmltext = "30328-04.htm";
			}
			else
			{
				st.startQuest();
				giveItems(player, REISA_LETTER, 1);
			}
		}
		else if (event.equals("30337-03.htm"))
		{
			st.setCond(2, true);
			takeItems(player, REISA_LETTER, -1);
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
				htmltext = "30328-01.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case REISA:
					{
						if (cond == 1)
						{
							htmltext = "30328-06.htm";
						}
						else if ((cond > 1) && (cond < 8))
						{
							htmltext = "30328-08.htm";
						}
						else if (cond == 8)
						{
							htmltext = "30328-07.htm";
							takeItems(player, HONORARY_GUARD, -1);
							giveItems(player, REISA_RECOMMENDATION, 1);
							addExpAndSp(player, 3200, 1000);
							player.broadcastPacket(new SocialAction(player.getObjectId(), 3));
							st.exitQuest(true, true);
						}
						break;
					}
					case MORETTI:
					{
						if (cond == 1)
						{
							htmltext = "30337-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = (!hasQuestItems(player, PRIAS_TORN_LETTER_1)) ? "30337-04.htm" : "30337-05.htm";
						}
						else if (cond == 3)
						{
							htmltext = "30337-06.htm";
							st.setCond(4, true);
							takeItems(player, PRIAS_TORN_LETTER_1, -1);
							takeItems(player, PRIAS_TORN_LETTER_2, -1);
							takeItems(player, PRIAS_TORN_LETTER_3, -1);
							takeItems(player, PRIAS_TORN_LETTER_4, -1);
							giveItems(player, MORETTI_HERB, 1);
							giveItems(player, MORETTI_LETTER, 1);
						}
						else if ((cond > 3) && (cond < 7))
						{
							htmltext = "30337-09.htm";
						}
						else if ((cond == 7) && hasQuestItems(player, PRIAS_LETTER))
						{
							htmltext = "30337-07.htm";
							st.setCond(8, true);
							takeItems(player, PRIAS_LETTER, -1);
							giveItems(player, HONORARY_GUARD, 1);
						}
						else if (cond == 8)
						{
							htmltext = "30337-08.htm";
						}
						break;
					}
					case BABENCO:
					{
						if (cond == 2)
						{
							htmltext = "30334-01.htm";
						}
						break;
					}
					case PRIAS:
					{
						if (cond == 4)
						{
							htmltext = "30426-01.htm";
							st.setCond(5, true);
						}
						else if (cond == 5)
						{
							htmltext = "30426-01.htm";
						}
						else if (cond == 6)
						{
							htmltext = "30426-02.htm";
							st.setCond(7, true);
							takeItems(player, RUSTED_KEY, -1);
							takeItems(player, MORETTI_HERB, -1);
							takeItems(player, MORETTI_LETTER, -1);
							giveItems(player, PRIAS_LETTER, 1);
						}
						else if (cond == 7)
						{
							htmltext = "30426-04.htm";
						}
						break;
					}
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		final QuestState st = getQuestState(player, false);
		if ((st == null) || !st.isStarted())
		{
			return null;
		}
		
		final int cond = st.getCond();
		if (npc.getId() == 20053)
		{
			if (cond == 2)
			{
				if (!hasQuestItems(player, PRIAS_TORN_LETTER_1))
				{
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					giveItems(player, PRIAS_TORN_LETTER_1, 1);
				}
				else if (!hasQuestItems(player, PRIAS_TORN_LETTER_2))
				{
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					giveItems(player, PRIAS_TORN_LETTER_2, 1);
				}
				else if (!hasQuestItems(player, PRIAS_TORN_LETTER_3))
				{
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					giveItems(player, PRIAS_TORN_LETTER_3, 1);
				}
				else if (!hasQuestItems(player, PRIAS_TORN_LETTER_4))
				{
					st.setCond(3, true);
					giveItems(player, PRIAS_TORN_LETTER_4, 1);
				}
			}
		}
		else if (((cond == 4) || (cond == 5)) && (getRandom(10) < 6))
		{
			giveItems(player, RUSTED_KEY, 1);
			st.setCond(6, true);
		}
		
		return null;
	}
}
