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
package quests.Q00382_KailsMagicCoin;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00382_KailsMagicCoin extends Quest
{
	// Monsters
	private static final int FALLEN_ORC = 21017;
	private static final int FALLEN_ORC_ARCHER = 21019;
	private static final int FALLEN_ORC_SHAMAN = 21020;
	private static final int FALLEN_ORC_CAPTAIN = 21022;
	// Items
	private static final int ROYAL_MEMBERSHIP = 5898;
	private static final int SILVER_BASILISK = 5961;
	private static final int GOLD_GOLEM = 5962;
	private static final int BLOOD_DRAGON = 5963;
	
	public Q00382_KailsMagicCoin()
	{
		super(382);
		registerQuestItems(SILVER_BASILISK, GOLD_GOLEM, BLOOD_DRAGON);
		addStartNpc(30687); // Vergara
		addTalkId(30687);
		addKillId(FALLEN_ORC, FALLEN_ORC_ARCHER, FALLEN_ORC_SHAMAN, FALLEN_ORC_CAPTAIN);
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
		
		if (event.equals("30687-03.htm"))
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
				htmltext = ((player.getLevel() < 55) || !hasQuestItems(player, ROYAL_MEMBERSHIP)) ? "30687-01.htm" : "30687-02.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = "30687-04.htm";
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
		
		switch (npc.getId())
		{
			case FALLEN_ORC:
			{
				if (getRandom(10) < 1)
				{
					giveItems(player, SILVER_BASILISK, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				break;
			}
			case FALLEN_ORC_ARCHER:
			{
				if (getRandom(10) < 1)
				{
					giveItems(player, GOLD_GOLEM, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				break;
			}
			case FALLEN_ORC_SHAMAN:
			{
				if (getRandom(10) < 1)
				{
					giveItems(player, BLOOD_DRAGON, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				break;
			}
			case FALLEN_ORC_CAPTAIN:
			{
				if (getRandom(10) < 1)
				{
					giveItems(player, 5961 + getRandom(3), 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				break;
			}
		}
		
		return null;
	}
}
