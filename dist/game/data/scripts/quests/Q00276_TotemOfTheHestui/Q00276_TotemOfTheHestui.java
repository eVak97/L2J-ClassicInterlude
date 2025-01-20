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
package quests.Q00276_TotemOfTheHestui;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00276_TotemOfTheHestui extends Quest
{
	// Items
	private static final int KASHA_PARASITE = 1480;
	private static final int KASHA_CRYSTAL = 1481;
	// Rewards
	private static final int HESTUI_TOTEM = 1500;
	private static final int LEATHER_PANTS = 29;
	
	public Q00276_TotemOfTheHestui()
	{
		super(276);
		registerQuestItems(KASHA_PARASITE, KASHA_CRYSTAL);
		addStartNpc(30571); // Tanapi
		addTalkId(30571);
		addKillId(20479, 27044);
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
		
		if (event.equals("30571-03.htm"))
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
					htmltext = "30571-00.htm";
				}
				else if (player.getLevel() < 15)
				{
					htmltext = "30571-01.htm";
				}
				else
				{
					htmltext = "30571-02.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (st.isCond(1))
				{
					htmltext = "30571-04.htm";
				}
				else
				{
					htmltext = "30571-05.htm";
					takeItems(player, KASHA_CRYSTAL, -1);
					takeItems(player, KASHA_PARASITE, -1);
					giveItems(player, HESTUI_TOTEM, 1);
					giveItems(player, LEATHER_PANTS, 1);
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
		
		if (!hasQuestItems(player, KASHA_CRYSTAL))
		{
			switch (npc.getId())
			{
				case 20479:
				{
					final int count = (int) getQuestItemsCount(player, KASHA_PARASITE);
					final int random = getRandom(100);
					if ((count >= 79) || ((count >= 69) && (random <= 20)) || ((count >= 59) && (random <= 15)) || ((count >= 49) && (random <= 10)) || ((count >= 39) && (random < 2)))
					{
						addSpawn(27044, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true, 0);
						takeItems(player, KASHA_PARASITE, count);
					}
					else
					{
						giveItems(player, KASHA_PARASITE, 1);
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case 27044:
				{
					st.setCond(2, true);
					giveItems(player, KASHA_CRYSTAL, 1);
					break;
				}
			}
		}
		
		return null;
	}
}
