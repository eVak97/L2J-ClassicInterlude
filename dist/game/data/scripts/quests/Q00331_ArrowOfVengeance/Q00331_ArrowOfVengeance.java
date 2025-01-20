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
package quests.Q00331_ArrowOfVengeance;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00331_ArrowOfVengeance extends Quest
{
	// Items
	private static final int HARPY_FEATHER = 1452;
	private static final int MEDUSA_VENOM = 1453;
	private static final int WYRM_TOOTH = 1454;
	
	public Q00331_ArrowOfVengeance()
	{
		super(331);
		registerQuestItems(HARPY_FEATHER, MEDUSA_VENOM, WYRM_TOOTH);
		addStartNpc(30125); // Belton
		addTalkId(30125);
		addKillId(20145, 20158, 20176);
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
		
		if (event.equals("30125-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("30125-06.htm"))
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
				htmltext = (player.getLevel() < 32) ? "30125-01.htm" : "30125-02.htm";
				break;
			}
			case State.STARTED:
			{
				final int harpyFeather = (int) getQuestItemsCount(player, HARPY_FEATHER);
				final int medusaVenom = (int) getQuestItemsCount(player, MEDUSA_VENOM);
				final int wyrmTooth = (int) getQuestItemsCount(player, WYRM_TOOTH);
				
				if ((harpyFeather + medusaVenom + wyrmTooth) > 0)
				{
					htmltext = "30125-05.htm";
					takeItems(player, HARPY_FEATHER, -1);
					takeItems(player, MEDUSA_VENOM, -1);
					takeItems(player, WYRM_TOOTH, -1);
					
					int reward = (harpyFeather * 80) + (medusaVenom * 90) + (wyrmTooth * 100);
					if ((harpyFeather + medusaVenom + wyrmTooth) > 10)
					{
						reward += 3100;
					}
					
					giveAdena(player, reward, true);
				}
				else
				{
					htmltext = "30125-04.htm";
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
		
		if (getRandomBoolean())
		{
			switch (npc.getId())
			{
				case 20145:
				{
					giveItems(player, HARPY_FEATHER, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					break;
				}
				case 20158:
				{
					giveItems(player, MEDUSA_VENOM, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					break;
				}
				case 20176:
				{
					giveItems(player, WYRM_TOOTH, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					break;
				}
			}
		}
		
		return null;
	}
}
