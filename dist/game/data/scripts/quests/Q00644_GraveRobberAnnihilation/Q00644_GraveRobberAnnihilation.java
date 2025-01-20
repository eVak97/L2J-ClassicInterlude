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
package quests.Q00644_GraveRobberAnnihilation;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.util.Util;

public class Q00644_GraveRobberAnnihilation extends Quest
{
	// NPC
	private static final int KARUDA = 32017;
	// Item
	private static final int ORC_GRAVE_GOODS = 8088;
	// Rewards
	private static final int[][] REWARDS =
	{
		// @formatter:off
		{1865, 30},
		{1867, 40},
		{1872, 40},
		{1871, 30},
		{1870, 30},
		{1869, 30}
		// @formatter:on
	};
	
	public Q00644_GraveRobberAnnihilation()
	{
		super(644);
		registerQuestItems(ORC_GRAVE_GOODS);
		addStartNpc(KARUDA);
		addTalkId(KARUDA);
		addKillId(22003, 22004, 22005, 22006, 22008);
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
		
		if (event.equals("32017-02.htm"))
		{
			st.startQuest();
		}
		else if (Util.isDigit(event))
		{
			htmltext = "32017-04.htm";
			takeItems(player, ORC_GRAVE_GOODS, -1);
			
			final int[] reward = REWARDS[Integer.parseInt(event)];
			rewardItems(player, reward[0], reward[1]);
			
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
				htmltext = (player.getLevel() < 20) ? "32017-06.htm" : "32017-01.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				if (cond == 1)
				{
					htmltext = "32017-05.htm";
				}
				else if (cond == 2)
				{
					htmltext = "32017-07.htm";
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		final QuestState qs = getRandomPartyMemberState(player, 1, 3, npc);
		if (qs == null)
		{
			return null;
		}
		final Player partyMember = qs.getPlayer();
		
		final QuestState st = getQuestState(partyMember, false);
		if (st == null)
		{
			return null;
		}
		
		if (getRandomBoolean())
		{
			giveItems(partyMember, ORC_GRAVE_GOODS, 1);
			if (getQuestItemsCount(partyMember, ORC_GRAVE_GOODS) < 120)
			{
				playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			else
			{
				st.setCond(2, true);
			}
		}
		
		return null;
	}
}
