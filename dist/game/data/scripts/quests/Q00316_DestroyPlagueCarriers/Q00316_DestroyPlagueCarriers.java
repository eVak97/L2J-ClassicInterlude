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
package quests.Q00316_DestroyPlagueCarriers;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00316_DestroyPlagueCarriers extends Quest
{
	// Monsters
	private static final int SUKAR_WERERAT = 20040;
	private static final int SUKAR_WERERAT_LEADER = 20047;
	private static final int VAROOL_FOULCLAW = 27020;
	// Items
	private static final int WERERAT_FANG = 1042;
	private static final int VAROOL_FOULCLAW_FANG = 1043;
	
	public Q00316_DestroyPlagueCarriers()
	{
		super(316);
		registerQuestItems(WERERAT_FANG, VAROOL_FOULCLAW_FANG);
		addStartNpc(30155); // Ellenia
		addTalkId(30155);
		addKillId(SUKAR_WERERAT, SUKAR_WERERAT_LEADER, VAROOL_FOULCLAW);
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
		
		if (event.equals("30155-04.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("30155-08.htm"))
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
				if (player.getRace() != Race.ELF)
				{
					htmltext = "30155-00.htm";
				}
				else if (player.getLevel() < 18)
				{
					htmltext = "30155-02.htm";
				}
				else
				{
					htmltext = "30155-03.htm";
				}
				break;
			}
			case State.STARTED:
			{
				final int ratFangs = (int) getQuestItemsCount(player, WERERAT_FANG);
				final int varoolFangs = (int) getQuestItemsCount(player, VAROOL_FOULCLAW_FANG);
				if ((ratFangs + varoolFangs) == 0)
				{
					htmltext = "30155-05.htm";
				}
				else
				{
					htmltext = "30155-07.htm";
					takeItems(player, WERERAT_FANG, -1);
					takeItems(player, VAROOL_FOULCLAW_FANG, -1);
					
					int reward = (ratFangs * 60) + (varoolFangs * 10000);
					if (!Config.ALT_VILLAGES_REPEATABLE_QUEST_REWARD && (ratFangs >= 10))
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
		
		switch (npc.getId())
		{
			case SUKAR_WERERAT:
			case SUKAR_WERERAT_LEADER:
			{
				if (getRandom(10) < 4)
				{
					giveItems(player, WERERAT_FANG, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				break;
			}
			case VAROOL_FOULCLAW:
			{
				if (!hasQuestItems(player, VAROOL_FOULCLAW_FANG) && (getRandom(10) < 2))
				{
					giveItems(player, VAROOL_FOULCLAW_FANG, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				break;
			}
		}
		
		return null;
	}
}
