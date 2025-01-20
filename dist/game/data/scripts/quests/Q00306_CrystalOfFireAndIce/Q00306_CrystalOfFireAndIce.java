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
package quests.Q00306_CrystalOfFireAndIce;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00306_CrystalOfFireAndIce extends Quest
{
	// Items
	private static final int FLAME_SHARD = 1020;
	private static final int ICE_SHARD = 1021;
	
	// Droplist (npcId, itemId, chance)
	private static final int[][] DROPLIST =
	{
		// @formatter:off
		{20109, FLAME_SHARD, 300000},
		{20110, ICE_SHARD, 300000},
		{20112, FLAME_SHARD, 400000},
		{20113, ICE_SHARD, 400000},
		{20114, FLAME_SHARD, 500000},
		{20115, ICE_SHARD, 500000}
		// @formatter:on
	};
	
	public Q00306_CrystalOfFireAndIce()
	{
		super(306);
		registerQuestItems(FLAME_SHARD, ICE_SHARD);
		addStartNpc(30004); // Katerina
		addTalkId(30004);
		addKillId(20109, 20110, 20112, 20113, 20114, 20115);
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
		
		if (event.equals("30004-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("30004-06.htm"))
		{
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
				htmltext = (player.getLevel() < 17) ? "30004-01.htm" : "30004-02.htm";
				break;
			}
			case State.STARTED:
			{
				final int totalItems = (int) (getQuestItemsCount(player, FLAME_SHARD) + getQuestItemsCount(player, ICE_SHARD));
				if (totalItems == 0)
				{
					htmltext = "30004-04.htm";
				}
				else
				{
					htmltext = "30004-05.htm";
					takeItems(player, FLAME_SHARD, -1);
					takeItems(player, ICE_SHARD, -1);
					
					int reward = totalItems * 60;
					if (!Config.ALT_VILLAGES_REPEATABLE_QUEST_REWARD && (totalItems >= 10))
					{
						reward += 5000;
					}
					
					giveAdena(player, reward, true);
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
		
		for (int[] drop : DROPLIST)
		{
			if (npc.getId() == drop[0])
			{
				if (getRandom(1000000) < drop[2])
				{
					giveItems(player, drop[1], 1);
				}
				break;
			}
		}
		
		return null;
	}
}
