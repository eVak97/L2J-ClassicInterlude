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
package quests.Q00274_SkirmishWithTheWerewolves;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00274_SkirmishWithTheWerewolves extends Quest
{
	// Needed items
	private static final int NECKLACE_OF_VALOR = 1507;
	private static final int NECKLACE_OF_COURAGE = 1506;
	// Items
	private static final int MARAKU_WEREWOLF_HEAD = 1477;
	private static final int MARAKU_WOLFMEN_TOTEM = 1501;
	
	public Q00274_SkirmishWithTheWerewolves()
	{
		super(274);
		registerQuestItems(MARAKU_WEREWOLF_HEAD, MARAKU_WOLFMEN_TOTEM);
		addStartNpc(30569);
		addTalkId(30569);
		addKillId(20363, 20364);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final QuestState st = player.getQuestState(getName());
		final String htmltext = event;
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equals("30569-03.htm"))
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
					htmltext = "30569-00.htm";
				}
				else if (player.getLevel() < 9)
				{
					htmltext = "30569-01.htm";
				}
				else if (hasAtLeastOneQuestItem(player, NECKLACE_OF_COURAGE, NECKLACE_OF_VALOR))
				{
					htmltext = "30569-02.htm";
				}
				else
				{
					htmltext = "30569-07.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (st.isCond(1))
				{
					htmltext = "30569-04.htm";
				}
				else
				{
					htmltext = "30569-05.htm";
					
					final int amount = (int) (3500 + (getQuestItemsCount(player, MARAKU_WOLFMEN_TOTEM) * 600));
					takeItems(player, MARAKU_WEREWOLF_HEAD, -1);
					takeItems(player, MARAKU_WOLFMEN_TOTEM, -1);
					giveAdena(player, amount, true);
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
		
		giveItems(player, MARAKU_WEREWOLF_HEAD, 1);
		if (getQuestItemsCount(player, MARAKU_WEREWOLF_HEAD) < 40)
		{
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		else
		{
			st.setCond(2, true);
		}
		
		if (getRandom(100) < 6)
		{
			giveItems(player, MARAKU_WOLFMEN_TOTEM, 1);
		}
		
		return null;
	}
}
