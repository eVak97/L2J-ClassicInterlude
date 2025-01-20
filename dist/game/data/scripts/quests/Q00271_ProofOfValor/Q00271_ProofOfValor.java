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
package quests.Q00271_ProofOfValor;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00271_ProofOfValor extends Quest
{
	// Item
	private static final int KASHA_WOLF_FANG = 1473;
	// Rewards
	private static final int NECKLACE_OF_VALOR = 1507;
	private static final int NECKLACE_OF_COURAGE = 1506;
	
	public Q00271_ProofOfValor()
	{
		super(271);
		registerQuestItems(KASHA_WOLF_FANG);
		addStartNpc(30577); // Rukain
		addTalkId(30577);
		addKillId(20475); // Kasha Wolf
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
		
		if (event.equals("30577-03.htm"))
		{
			st.startQuest();
			if (hasAtLeastOneQuestItem(player, NECKLACE_OF_COURAGE, NECKLACE_OF_VALOR))
			{
				htmltext = "30577-07.htm";
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
				if (player.getRace() != Race.ORC)
				{
					htmltext = "30577-00.htm";
				}
				else if (player.getLevel() < 4)
				{
					htmltext = "30577-01.htm";
				}
				else
				{
					htmltext = (hasAtLeastOneQuestItem(player, NECKLACE_OF_COURAGE, NECKLACE_OF_VALOR)) ? "30577-06.htm" : "30577-02.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (st.isCond(1))
				{
					htmltext = (hasAtLeastOneQuestItem(player, NECKLACE_OF_COURAGE, NECKLACE_OF_VALOR)) ? "30577-07.htm" : "30577-04.htm";
				}
				else
				{
					htmltext = "30577-05.htm";
					takeItems(player, KASHA_WOLF_FANG, -1);
					giveItems(player, (getRandom(100) < 10) ? NECKLACE_OF_VALOR : NECKLACE_OF_COURAGE, 1);
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
		
		giveItems(player, KASHA_WOLF_FANG, getRandom(4) == 0 ? 2 : 1);
		if (getQuestItemsCount(player, KASHA_WOLF_FANG) < 50)
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
