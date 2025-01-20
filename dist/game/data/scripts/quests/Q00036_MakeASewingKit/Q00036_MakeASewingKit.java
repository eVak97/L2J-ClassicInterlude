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
package quests.Q00036_MakeASewingKit;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

import quests.Q00037_MakeFormalWear.Q00037_MakeFormalWear;

public class Q00036_MakeASewingKit extends Quest
{
	// Items
	private static final int REINFORCED_STEEL = 7163;
	private static final int ARTISANS_FRAME = 1891;
	private static final int ORIHARUKON = 1893;
	// Reward
	private static final int SEWING_KIT = 7078;
	
	public Q00036_MakeASewingKit()
	{
		super(36);
		registerQuestItems(REINFORCED_STEEL);
		addStartNpc(30847); // Ferris
		addTalkId(30847);
		addKillId(20566); // Iron Golem
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
			case "30847-1.htm":
			{
				st.startQuest();
				break;
			}
			case "30847-3.htm":
			{
				st.setCond(3, true);
				takeItems(player, REINFORCED_STEEL, 5);
				break;
			}
			case "30847-5.htm":
			{
				if ((getQuestItemsCount(player, ORIHARUKON) >= 10) && (getQuestItemsCount(player, ARTISANS_FRAME) >= 10))
				{
					takeItems(player, ARTISANS_FRAME, 10);
					takeItems(player, ORIHARUKON, 10);
					giveItems(player, SEWING_KIT, 1);
					st.exitQuest(false, true);
				}
				else
				{
					htmltext = "30847-4a.htm";
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
				if (player.getLevel() >= 60)
				{
					final QuestState fwear = player.getQuestState(Q00037_MakeFormalWear.class.getSimpleName());
					if ((fwear != null) && fwear.isCond(6))
					{
						htmltext = "30847-0.htm";
					}
					else
					{
						htmltext = "30847-0a.htm";
					}
				}
				else
				{
					htmltext = "30847-0b.htm";
				}
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				if (cond == 1)
				{
					htmltext = "30847-1a.htm";
				}
				else if (cond == 2)
				{
					htmltext = "30847-2.htm";
				}
				else if (cond == 3)
				{
					htmltext = ((getQuestItemsCount(player, ORIHARUKON) < 10) || (getQuestItemsCount(player, ARTISANS_FRAME) < 10)) ? "30847-4a.htm" : "30847-4.htm";
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		final Player member = getRandomPartyMember(player, 1);
		if ((member != null) && getRandomBoolean())
		{
			giveItems(member, REINFORCED_STEEL, 1);
			if (getQuestItemsCount(member, REINFORCED_STEEL) >= 5)
			{
				getQuestState(member, false).setCond(2, true);
			}
			else
			{
				playSound(member, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		return super.onKill(npc, player, isSummon);
	}
}
