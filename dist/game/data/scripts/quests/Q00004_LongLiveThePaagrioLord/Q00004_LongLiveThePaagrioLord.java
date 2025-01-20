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
package quests.Q00004_LongLiveThePaagrioLord;

import java.util.HashMap;
import java.util.Map;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00004_LongLiveThePaagrioLord extends Quest
{
	private static final int NAKUSIN = 30578;
	private static final Map<Integer, Integer> NPC_GIFTS = new HashMap<>();
	static
	{
		NPC_GIFTS.put(30585, 1542);
		NPC_GIFTS.put(30566, 1541);
		NPC_GIFTS.put(30562, 1543);
		NPC_GIFTS.put(30560, 1544);
		NPC_GIFTS.put(30559, 1545);
		NPC_GIFTS.put(30587, 1546);
	}
	
	public Q00004_LongLiveThePaagrioLord()
	{
		super(4);
		registerQuestItems(1541, 1542, 1543, 1544, 1545, 1546);
		addStartNpc(30578); // Nakusin
		addTalkId(30578, 30585, 30566, 30562, 30560, 30559, 30587);
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
		
		if (event.equals("30578-03.htm"))
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
				if (player.getRace() != Race.ORC)
				{
					htmltext = "30578-00.htm";
				}
				else if (player.getLevel() < 2)
				{
					htmltext = "30578-01.htm";
				}
				else
				{
					htmltext = "30578-02.htm";
				}
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				final int npcId = npc.getId();
				if (npcId == NAKUSIN)
				{
					if (cond == 1)
					{
						htmltext = "30578-04.htm";
					}
					else if (cond == 2)
					{
						htmltext = "30578-06.htm";
						giveItems(player, 4, 1);
						for (int item : NPC_GIFTS.values())
						{
							takeItems(player, item, -1);
						}
						
						st.exitQuest(false, true);
					}
				}
				else
				{
					final int i = NPC_GIFTS.get(npcId);
					if (hasQuestItems(player, i))
					{
						htmltext = "30585-02.htm";
					}
					else
					{
						giveItems(player, i, 1);
						htmltext = "30585-01.htm";
						
						int count = 0;
						for (int item : NPC_GIFTS.values())
						{
							count += getQuestItemsCount(player, item);
						}
						
						if (count == 6)
						{
							st.setCond(2, true);
						}
						else
						{
							playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
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
