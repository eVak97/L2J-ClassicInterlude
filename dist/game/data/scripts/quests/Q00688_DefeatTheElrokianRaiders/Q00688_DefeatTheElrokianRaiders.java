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
package quests.Q00688_DefeatTheElrokianRaiders;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00688_DefeatTheElrokianRaiders extends Quest
{
	// NPC
	private static final int DINN = 32105;
	// Monster
	private static final int ELROKI = 22214;
	// Item
	private static final int DINOSAUR_FANG_NECKLACE = 8785;
	
	public Q00688_DefeatTheElrokianRaiders()
	{
		super(688);
		registerQuestItems(DINOSAUR_FANG_NECKLACE);
		addStartNpc(DINN);
		addTalkId(DINN);
		addKillId(ELROKI);
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
		
		switch (event)
		{
			case "32105-03.htm":
			{
				st.startQuest();
				break;
			}
			case "32105-08.htm":
			{
				final int count = (int) getQuestItemsCount(player, DINOSAUR_FANG_NECKLACE);
				if (count > 0)
				{
					takeItems(player, DINOSAUR_FANG_NECKLACE, -1);
					giveAdena(player, count * 3000, true);
				}
				st.exitQuest(true, true);
				break;
			}
			case "32105-06.htm":
			{
				final int count = (int) getQuestItemsCount(player, DINOSAUR_FANG_NECKLACE);
				takeItems(player, DINOSAUR_FANG_NECKLACE, -1);
				giveAdena(player, count * 3000, true);
				break;
			}
			case "32105-07.htm":
			{
				final int count = (int) getQuestItemsCount(player, DINOSAUR_FANG_NECKLACE);
				if (count >= 100)
				{
					takeItems(player, DINOSAUR_FANG_NECKLACE, 100);
					giveAdena(player, 450000, true);
				}
				else
				{
					htmltext = "32105-04.htm";
				}
				break;
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
				htmltext = (!hasQuestItems(player, DINOSAUR_FANG_NECKLACE)) ? "32105-04.htm" : "32105-05.htm";
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		final QuestState st = getRandomPartyMemberState(player, -1, 3, npc);
		if ((st == null) || !st.isStarted())
		{
			return null;
		}
		final Player partyMember = st.getPlayer();
		
		if (getRandomBoolean())
		{
			giveItems(partyMember, DINOSAUR_FANG_NECKLACE, 1);
			playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		
		return null;
	}
}
