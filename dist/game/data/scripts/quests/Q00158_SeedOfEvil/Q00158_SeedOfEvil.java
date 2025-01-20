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
package quests.Q00158_SeedOfEvil;

import org.l2jmobius.gameserver.enums.ChatType;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.network.NpcStringId;

public class Q00158_SeedOfEvil extends Quest
{
	// Item
	private static final int CLAY_TABLET = 1025;
	// Reward
	private static final int ENCHANT_ARMOR_D = 956;
	
	public Q00158_SeedOfEvil()
	{
		super(158);
		registerQuestItems(CLAY_TABLET);
		addStartNpc(30031); // Biotin
		addTalkId(30031);
		addKillId(27016); // Nerkas
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
		
		if (event.equals("30031-04.htm"))
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
				htmltext = (player.getLevel() < 21) ? "30031-02.htm" : "30031-03.htm";
				break;
			}
			case State.STARTED:
			{
				if (!hasQuestItems(player, CLAY_TABLET))
				{
					htmltext = "30031-05.htm";
				}
				else
				{
					htmltext = "30031-06.htm";
					takeItems(player, CLAY_TABLET, 1);
					giveItems(player, ENCHANT_ARMOR_D, 1);
					st.exitQuest(false, true);
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
		if ((qs != null) && !hasQuestItems(killer, CLAY_TABLET))
		{
			giveItems(killer, CLAY_TABLET, 1);
			qs.setCond(2, true);
		}
		npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THE_POWER_OF_LORD_BELETH_RULES_THE_WHOLE_WORLD);
		return super.onKill(npc, killer, isSummon);
	}
}
