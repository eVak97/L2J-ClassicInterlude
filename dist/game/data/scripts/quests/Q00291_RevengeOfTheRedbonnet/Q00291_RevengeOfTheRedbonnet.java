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
package quests.Q00291_RevengeOfTheRedbonnet;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00291_RevengeOfTheRedbonnet extends Quest
{
	// Quest items
	private static final int BLACK_WOLF_PELT = 1482;
	// Rewards
	private static final int SCROLL_OF_ESCAPE = 736;
	private static final int GRANDMA_PEARL = 1502;
	private static final int GRANDMA_MIRROR = 1503;
	private static final int GRANDMA_NECKLACE = 1504;
	private static final int GRANDMA_HAIRPIN = 1505;
	
	public Q00291_RevengeOfTheRedbonnet()
	{
		super(291);
		registerQuestItems(BLACK_WOLF_PELT);
		addStartNpc(30553); // Maryse Redbonnet
		addTalkId(30553);
		addKillId(20317);
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
		
		if (event.equals("30553-03.htm"))
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
				htmltext = (player.getLevel() < 4) ? "30553-01.htm" : "30553-02.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				if (cond == 1)
				{
					htmltext = "30553-04.htm";
				}
				else if (cond == 2)
				{
					htmltext = "30553-05.htm";
					takeItems(player, BLACK_WOLF_PELT, -1);
					
					final int random = getRandom(100);
					if (random < 3)
					{
						rewardItems(player, GRANDMA_PEARL, 1);
					}
					else if (random < 21)
					{
						rewardItems(player, GRANDMA_MIRROR, 1);
					}
					else if (random < 46)
					{
						rewardItems(player, GRANDMA_NECKLACE, 1);
					}
					else
					{
						rewardItems(player, SCROLL_OF_ESCAPE, 1);
						rewardItems(player, GRANDMA_HAIRPIN, 1);
					}
					
					st.exitQuest(true, true);
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
		if ((st == null) || !st.isCond(1))
		{
			return null;
		}
		
		giveItems(player, BLACK_WOLF_PELT, 1);
		if (getQuestItemsCount(player, BLACK_WOLF_PELT) < 40)
		{
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		else
		{
			st.setCond(2, true);
		}
		
		return null;
	}
}
