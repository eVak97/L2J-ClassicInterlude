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
package quests.Q00650_ABrokenDream;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

import quests.Q00117_TheOceanOfDistantStars.Q00117_TheOceanOfDistantStars;

public class Q00650_ABrokenDream extends Quest
{
	// NPC
	private static final int GHOST = 32054;
	// Monsters
	private static final int CREWMAN = 22027;
	private static final int VAGABOND = 22028;
	// Item
	private static final int DREAM_FRAGMENT = 8514;
	
	public Q00650_ABrokenDream()
	{
		super(650);
		registerQuestItems(DREAM_FRAGMENT);
		addStartNpc(GHOST);
		addTalkId(GHOST);
		addKillId(CREWMAN, VAGABOND);
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
			case "32054-01a.htm":
			{
				st.startQuest();
				break;
			}
			case "32054-03.htm":
			{
				if (!hasQuestItems(player, DREAM_FRAGMENT))
				{
					htmltext = "32054-04.htm";
				}
				break;
			}
			case "32054-05.htm":
			{
				st.exitQuest(true, true);
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
				final QuestState st2 = player.getQuestState(Q00117_TheOceanOfDistantStars.class.getSimpleName());
				if ((st2 != null) && st2.isCompleted() && (player.getLevel() >= 39))
				{
					htmltext = "32054-01.htm";
				}
				else
				{
					htmltext = "32054-00.htm";
					st.exitQuest(true, true);
				}
				break;
			}
			case State.STARTED:
			{
				htmltext = "32054-02.htm";
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
		
		if (getRandom(100) < 25)
		{
			giveItems(player, DREAM_FRAGMENT, 1);
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		
		return null;
	}
}
