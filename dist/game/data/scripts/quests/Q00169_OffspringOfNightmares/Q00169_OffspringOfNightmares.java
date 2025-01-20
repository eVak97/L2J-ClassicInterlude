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
package quests.Q00169_OffspringOfNightmares;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00169_OffspringOfNightmares extends Quest
{
	// Items
	private static final int CRACKED_SKULL = 1030;
	private static final int PERFECT_SKULL = 1031;
	private static final int BONE_GAITERS = 31;
	
	public Q00169_OffspringOfNightmares()
	{
		super(169);
		registerQuestItems(CRACKED_SKULL, PERFECT_SKULL);
		addStartNpc(30145); // Vlasty
		addTalkId(30145);
		addKillId(20105, 20025);
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
		
		if (event.equals("30145-04.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("30145-08.htm"))
		{
			final int reward = (int) (17000 + (getQuestItemsCount(player, CRACKED_SKULL) * 20));
			takeItems(player, PERFECT_SKULL, -1);
			takeItems(player, CRACKED_SKULL, -1);
			giveItems(player, BONE_GAITERS, 1);
			giveAdena(player, reward, true);
			st.exitQuest(false, true);
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
					htmltext = "30145-00.htm";
				}
				else if (player.getLevel() < 15)
				{
					htmltext = "30145-02.htm";
				}
				else
				{
					htmltext = "30145-03.htm";
				}
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				if (cond == 1)
				{
					if (hasQuestItems(player, CRACKED_SKULL))
					{
						htmltext = "30145-06.htm";
					}
					else
					{
						htmltext = "30145-05.htm";
					}
				}
				else if (cond == 2)
				{
					htmltext = "30145-07.htm";
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
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted())
		{
			if ((getRandom(10) > 7) && !hasQuestItems(killer, PERFECT_SKULL))
			{
				giveItems(killer, PERFECT_SKULL, 1);
				qs.setCond(2, true);
			}
			else if (getRandom(10) > 4)
			{
				giveItems(killer, CRACKED_SKULL, 1);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}
