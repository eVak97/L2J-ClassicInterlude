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
package quests.Q00263_OrcSubjugation;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00263_OrcSubjugation extends Quest
{
	// Items
	private static final int ORC_AMULET = 1116;
	private static final int ORC_NECKLACE = 1117;
	
	public Q00263_OrcSubjugation()
	{
		super(263);
		registerQuestItems(ORC_AMULET, ORC_NECKLACE);
		addStartNpc(30346); // Kayleen
		addTalkId(30346);
		addKillId(20385, 20386, 20387, 20388);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final String htmltext = event;
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equals("30346-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("30346-06.htm"))
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
				if (player.getRace() != Race.DARK_ELF)
				{
					htmltext = "30346-00.htm";
				}
				else if (player.getLevel() < 8)
				{
					htmltext = "30346-01.htm";
				}
				else
				{
					htmltext = "30346-02.htm";
				}
				break;
			}
			case State.STARTED:
			{
				final int amulet = (int) getQuestItemsCount(player, ORC_AMULET);
				final int necklace = (int) getQuestItemsCount(player, ORC_NECKLACE);
				if ((amulet == 0) && (necklace == 0))
				{
					htmltext = "30346-04.htm";
				}
				else
				{
					htmltext = "30346-05.htm";
					takeItems(player, ORC_AMULET, -1);
					takeItems(player, ORC_NECKLACE, -1);
					int reward = (amulet * 20) + (necklace * 30);
					if (!Config.ALT_VILLAGES_REPEATABLE_QUEST_REWARD && ((amulet + necklace) >= 10))
					{
						reward += 1000;
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
		if ((st == null) || !st.isCond(1))
		{
			return null;
		}
		
		if (getRandomBoolean())
		{
			giveItems(player, (npc.getId() == 20385) ? ORC_AMULET : ORC_NECKLACE, 1);
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		
		return null;
	}
}
