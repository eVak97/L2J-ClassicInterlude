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
package quests.Q00642_APowerfulPrimevalCreature;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00642_APowerfulPrimevalCreature extends Quest
{
	// NPC
	private static final int ANCIENT_EGG = 18344;
	// Items
	private static final int DINOSAUR_TISSUE = 8774;
	private static final int DINOSAUR_EGG = 8775;
	// Rewards
	private static final int[] REWARDS =
	{
		8690,
		8692,
		8694,
		8696,
		8698,
		8700,
		8702,
		8704,
		8706,
		8708,
		8710
	};
	
	public Q00642_APowerfulPrimevalCreature()
	{
		super(642);
		registerQuestItems(DINOSAUR_TISSUE, DINOSAUR_EGG);
		addStartNpc(32105); // Dinn
		addTalkId(32105);
		// Dinosaurs + egg
		addKillId(22196, 22197, 22198, 22199, 22200, 22201, 22202, 22203, 22204, 22205, 22218, 22219, 22220, 22223, 22224, 22225, ANCIENT_EGG);
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
		
		if (event.equals("32105-04.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("32105-08.htm"))
		{
			if ((getQuestItemsCount(player, DINOSAUR_TISSUE) >= 150) && hasQuestItems(player, DINOSAUR_EGG))
			{
				htmltext = "32105-06.htm";
			}
		}
		else if (event.equals("32105-07.htm"))
		{
			final int tissues = (int) getQuestItemsCount(player, DINOSAUR_TISSUE);
			if (tissues > 0)
			{
				takeItems(player, DINOSAUR_TISSUE, -1);
				giveAdena(player, tissues * 5000, true);
			}
			else
			{
				htmltext = "32105-08.htm";
			}
		}
		else if (event.contains("event_"))
		{
			if ((getQuestItemsCount(player, DINOSAUR_TISSUE) >= 150) && hasQuestItems(player, DINOSAUR_EGG))
			{
				htmltext = "32105-07.htm";
				takeItems(player, DINOSAUR_TISSUE, 150);
				takeItems(player, DINOSAUR_EGG, 1);
				giveAdena(player, 44000, true);
				giveItems(player, REWARDS[Integer.parseInt(event.split("_")[1])], 1);
			}
			else
			{
				htmltext = "32105-08.htm";
			}
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
				htmltext = (player.getLevel() < 75) ? "32105-00.htm" : "32105-01.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (!hasQuestItems(player, DINOSAUR_TISSUE)) ? "32105-08.htm" : "32105-05.htm";
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
		
		if (npc.getId() == ANCIENT_EGG)
		{
			if (getRandom(100) < 1)
			{
				giveItems(player, DINOSAUR_EGG, 1);
				if (getQuestItemsCount(player, DINOSAUR_TISSUE) >= 150)
				{
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
				}
				else
				{
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
			}
		}
		else if (getRandom(100) < 33)
		{
			rewardItems(player, DINOSAUR_TISSUE, 1);
			if ((getQuestItemsCount(player, DINOSAUR_TISSUE) >= 150) && hasQuestItems(player, DINOSAUR_EGG))
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
			}
			else
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		
		return null;
	}
}