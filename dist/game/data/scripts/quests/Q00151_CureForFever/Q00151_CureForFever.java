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
package quests.Q00151_CureForFever;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00151_CureForFever extends Quest
{
	// NPCs
	private static final int ELIAS = 30050;
	private static final int YOHANES = 30032;
	// Items
	private static final int POISON_SAC = 703;
	private static final int FEVER_MEDICINE = 704;
	
	public Q00151_CureForFever()
	{
		super(151);
		registerQuestItems(FEVER_MEDICINE, POISON_SAC);
		addStartNpc(ELIAS);
		addTalkId(ELIAS, YOHANES);
		addKillId(20103, 20106, 20108);
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
		
		if (event.equals("30050-03.htm"))
		{
			st.startQuest();
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
				htmltext = (player.getLevel() < 15) ? "30050-01.htm" : "30050-02.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case ELIAS:
					{
						if (cond == 1)
						{
							htmltext = "30050-04.htm";
						}
						else if (cond == 2)
						{
							htmltext = "30050-05.htm";
						}
						else if (cond == 3)
						{
							htmltext = "30050-06.htm";
							takeItems(player, FEVER_MEDICINE, 1);
							giveItems(player, 102, 1);
							st.exitQuest(false, true);
						}
						break;
					}
					case YOHANES:
					{
						if (cond == 2)
						{
							htmltext = "30032-01.htm";
							st.setCond(3, true);
							takeItems(player, POISON_SAC, 1);
							giveItems(player, FEVER_MEDICINE, 1);
						}
						else if (cond == 3)
						{
							htmltext = "30032-02.htm";
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
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1) && (getRandom(10) < 2))
		{
			giveItems(killer, POISON_SAC, 1);
			qs.setCond(2, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
}
