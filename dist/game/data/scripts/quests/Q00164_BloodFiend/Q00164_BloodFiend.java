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
package quests.Q00164_BloodFiend;

import org.l2jmobius.gameserver.enums.ChatType;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.network.NpcStringId;

public class Q00164_BloodFiend extends Quest
{
	// Item
	private static final int KIRUNAK_SKULL = 1044;
	
	public Q00164_BloodFiend()
	{
		super(164);
		registerQuestItems(KIRUNAK_SKULL);
		addStartNpc(30149);
		addTalkId(30149);
		addKillId(27021);
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
		
		if (event.equals("30149-04.htm"))
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
				if (player.getRace() == Race.DARK_ELF)
				{
					htmltext = "30149-00.htm";
				}
				else if (player.getLevel() < 21)
				{
					htmltext = "30149-02.htm";
				}
				else
				{
					htmltext = "30149-03.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (hasQuestItems(player, KIRUNAK_SKULL))
				{
					htmltext = "30149-06.htm";
					takeItems(player, KIRUNAK_SKULL, 1);
					giveAdena(player, 42130, true);
					st.exitQuest(false, true);
				}
				else
				{
					htmltext = "30149-05.htm";
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
		if ((qs != null) && qs.isCond(1))
		{
			npc.broadcastSay(ChatType.GENERAL, NpcStringId.I_HAVE_FULFILLED_MY_CONTRACT_WITH_TRADER_CREAMEES);
			giveItems(killer, KIRUNAK_SKULL, 1);
			qs.setCond(2, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
}
