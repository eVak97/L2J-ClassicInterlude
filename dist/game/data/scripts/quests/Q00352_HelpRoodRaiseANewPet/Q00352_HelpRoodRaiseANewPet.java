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
package quests.Q00352_HelpRoodRaiseANewPet;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00352_HelpRoodRaiseANewPet extends Quest
{
	// Items
	private static final int LIENRIK_EGG_1 = 5860;
	private static final int LIENRIK_EGG_2 = 5861;
	
	public Q00352_HelpRoodRaiseANewPet()
	{
		super(352);
		registerQuestItems(LIENRIK_EGG_1, LIENRIK_EGG_2);
		addStartNpc(31067); // Rood
		addTalkId(31067);
		addKillId(20786, 20787, 21644, 21645);
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
		
		if (event.equals("31067-04.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("31067-09.htm"))
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
				htmltext = (player.getLevel() < 39) ? "31067-00.htm" : "31067-01.htm";
				break;
			}
			case State.STARTED:
			{
				final int eggs1 = (int) getQuestItemsCount(player, LIENRIK_EGG_1);
				final int eggs2 = (int) getQuestItemsCount(player, LIENRIK_EGG_2);
				if ((eggs1 + eggs2) == 0)
				{
					htmltext = "31067-05.htm";
				}
				else
				{
					int reward = 2000;
					if ((eggs1 > 0) && (eggs2 == 0))
					{
						htmltext = "31067-06.htm";
						reward += eggs1 * 34;
						takeItems(player, LIENRIK_EGG_1, -1);
						giveAdena(player, reward, true);
					}
					else if ((eggs1 == 0) && (eggs2 > 0))
					{
						htmltext = "31067-08.htm";
						reward += eggs2 * 1025;
						takeItems(player, LIENRIK_EGG_2, -1);
						giveAdena(player, reward, true);
					}
					else if ((eggs1 > 0) && (eggs2 > 0))
					{
						htmltext = "31067-08.htm";
						reward += (eggs1 * 34) + (eggs2 * 1025) + 2000;
						takeItems(player, LIENRIK_EGG_1, -1);
						takeItems(player, LIENRIK_EGG_2, -1);
						giveAdena(player, reward, true);
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
		
		final int npcId = npc.getId();
		final int random = getRandom(100);
		final int chance = ((npcId == 20786) || (npcId == 21644)) ? 44 : 58;
		if (random < chance)
		{
			giveItems(player, LIENRIK_EGG_1, 1);
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		else if (random < (chance + 4))
		{
			giveItems(player, LIENRIK_EGG_2, 1);
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		
		return null;
	}
}
