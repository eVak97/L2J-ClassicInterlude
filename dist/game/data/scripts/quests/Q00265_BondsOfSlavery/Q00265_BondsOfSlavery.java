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
package quests.Q00265_BondsOfSlavery;

import org.l2jmobius.Config;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;

public class Q00265_BondsOfSlavery extends Quest
{
	// Item
	private static final int SHACKLE = 1368;
	// Newbie Items
	private static final int SPIRITSHOT_FOR_BEGINNERS = 5790;
	private static final int SOULSHOT_FOR_BEGINNERS = 5789;
	
	public Q00265_BondsOfSlavery()
	{
		super(265);
		registerQuestItems(SHACKLE);
		addStartNpc(30357); // Kristin
		addTalkId(30357);
		addKillId(20004, 20005);
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
		
		if (event.equals("30357-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("30357-06.htm"))
		{
			st.exitQuest(true, true);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				if (player.getRace() != Race.DARK_ELF)
				{
					htmltext = "30357-00.htm";
				}
				else if (player.getLevel() < 6)
				{
					htmltext = "30357-01.htm";
				}
				else
				{
					htmltext = "30357-02.htm";
				}
				break;
			}
			case State.STARTED:
			{
				final long shackles = getQuestItemsCount(player, SHACKLE);
				if (shackles == 0)
				{
					htmltext = "30357-04.htm";
				}
				else
				{
					htmltext = "30357-05.htm";
					
					long reward = 12 * shackles;
					if (!Config.ALT_VILLAGES_REPEATABLE_QUEST_REWARD && (shackles >= 10))
					{
						reward += 500;
					}
					
					takeItems(player, SHACKLE, -1);
					giveAdena(player, reward, true);
					
					final PlayerVariables vars = player.getVariables();
					// Give newbie reward if player is eligible.
					if ((player.getLevel() < 25) && (st.getInt("Reward") == 0))
					{
						if (!vars.getBoolean("NEWBIE_SHOTS", false))
						{
							st.showQuestionMark(26);
							st.set("Reward", "1");
							
							if (player.isMageClass())
							{
								st.playTutorialVoice("tutorial_voice_027");
								giveItems(player, SPIRITSHOT_FOR_BEGINNERS, 3000);
							}
							else
							{
								st.playTutorialVoice("tutorial_voice_026");
								giveItems(player, SOULSHOT_FOR_BEGINNERS, 6000);
							}
						}
					}
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
			return super.onKill(npc, player, isPet);
		}
		
		if (Rnd.get(10) < (npc.getId() == 20004 ? 5 : 6))
		{
			giveItems(player, SHACKLE, 1);
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		
		return super.onKill(npc, player, isPet);
	}
}
