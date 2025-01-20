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
package quests.Q00170_DangerousSeduction;

import org.l2jmobius.gameserver.enums.ChatType;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.network.NpcStringId;

public class Q00170_DangerousSeduction extends Quest
{
	// Item
	private static final int NIGHTMARE_CRYSTAL = 1046;
	
	public Q00170_DangerousSeduction()
	{
		super(170);
		registerQuestItems(NIGHTMARE_CRYSTAL);
		addStartNpc(30305); // Vellior
		addTalkId(30305);
		addKillId(27022); // Merkenis
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
		
		if (event.equals("30305-04.htm"))
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
				if (player.getRace() != Race.DARK_ELF)
				{
					htmltext = "30305-00.htm";
				}
				else if (player.getLevel() < 21)
				{
					htmltext = "30305-02.htm";
				}
				else
				{
					htmltext = "30305-03.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (hasQuestItems(player, NIGHTMARE_CRYSTAL))
				{
					htmltext = "30305-06.htm";
					takeItems(player, NIGHTMARE_CRYSTAL, -1);
					giveAdena(player, 102680, true);
					st.exitQuest(false, true);
				}
				else
				{
					htmltext = "30305-05.htm";
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
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1))
		{
			qs.setCond(2, true);
			giveItems(player, NIGHTMARE_CRYSTAL, 1);
			npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.SEND_MY_SOUL_TO_LICH_KING_ICARUS);
		}
		return super.onKill(npc, player, isSummon);
	}
}
