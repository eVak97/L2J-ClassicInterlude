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
package quests.Q00258_BringWolfPelts;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00258_BringWolfPelts extends Quest
{
	// Item
	private static final int WOLF_PELT = 702;
	// Rewards
	private static final int COTTON_SHIRT = 390;
	private static final int LEATHER_PANTS = 29;
	private static final int LEATHER_SHIRT = 22;
	private static final int SHORT_LEATHER_GLOVES = 1119;
	private static final int TUNIC = 426;
	
	public Q00258_BringWolfPelts()
	{
		super(258);
		registerQuestItems(WOLF_PELT);
		addStartNpc(30001); // Lector
		addTalkId(30001);
		addKillId(20120, 20442); // Wolf, Elder Wolf
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
		
		if (event.equals("30001-03.htm"))
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
				htmltext = (player.getLevel() < 3) ? "30001-01.htm" : "30001-02.htm";
				break;
			}
			case State.STARTED:
			{
				if (getQuestItemsCount(player, WOLF_PELT) < 40)
				{
					htmltext = "30001-05.htm";
				}
				else
				{
					takeItems(player, WOLF_PELT, -1);
					final int randomNumber = getRandom(16);
					
					// Reward is based on a random number (1D16).
					if (randomNumber == 0)
					{
						giveItems(player, COTTON_SHIRT, 1);
					}
					else if (randomNumber < 6)
					{
						giveItems(player, LEATHER_PANTS, 1);
					}
					else if (randomNumber < 9)
					{
						giveItems(player, LEATHER_SHIRT, 1);
					}
					else if (randomNumber < 13)
					{
						giveItems(player, SHORT_LEATHER_GLOVES, 1);
					}
					else
					{
						giveItems(player, TUNIC, 1);
					}
					
					htmltext = "30001-06.htm";
					
					if (randomNumber == 0)
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_JACKPOT);
					}
					else
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_FINISH);
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
		
		giveItems(player, WOLF_PELT, 1);
		if (getQuestItemsCount(player, WOLF_PELT) >= 40)
		{
			st.setCond(2, true);
		}
		else
		{
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		
		return null;
	}
}
