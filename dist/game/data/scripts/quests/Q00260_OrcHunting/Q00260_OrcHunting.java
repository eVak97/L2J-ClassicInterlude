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
package quests.Q00260_OrcHunting;

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

public class Q00260_OrcHunting extends Quest
{
	// NPC
	private static final int RAYEN = 30221;
	// Monsters
	private static final int KABOO_ORC = 20468;
	private static final int KABOO_ORC_ARCHER = 20469;
	private static final int KABOO_ORC_GRUNT = 20470;
	private static final int KABOO_ORC_FIGHTER = 20471;
	private static final int KABOO_ORC_FIGHTER_LEADER = 20472;
	private static final int KABOO_ORC_FIGHTER_LIEUTENANT = 20473;
	// Items
	private static final int ORC_AMULET = 1114;
	private static final int ORC_NECKLACE = 1115;
	// Newbie Items
	private static final int SPIRITSHOT_FOR_BEGINNERS = 5790;
	private static final int SOULSHOT_FOR_BEGINNERS = 5789;
	
	public Q00260_OrcHunting()
	{
		super(260);
		registerQuestItems(ORC_AMULET, ORC_NECKLACE);
		addStartNpc(RAYEN);
		addTalkId(RAYEN);
		addKillId(KABOO_ORC, KABOO_ORC_ARCHER, KABOO_ORC_GRUNT, KABOO_ORC_FIGHTER, KABOO_ORC_FIGHTER_LEADER, KABOO_ORC_FIGHTER_LIEUTENANT);
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
		
		if (event.equals("30221-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("30221-06.htm"))
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
				if (player.getRace() != Race.ELF)
				{
					htmltext = "30221-00.htm";
				}
				else if (player.getLevel() < 6)
				{
					htmltext = "30221-01.htm";
				}
				else
				{
					htmltext = "30221-02.htm";
				}
				break;
			}
			case State.STARTED:
			{
				final long amulet = getQuestItemsCount(player, ORC_AMULET);
				final long necklace = getQuestItemsCount(player, ORC_NECKLACE);
				
				if ((amulet == 0) && (necklace == 0))
				{
					htmltext = "30221-04.htm";
				}
				else
				{
					htmltext = "30221-05.htm";
					
					long reward = (amulet * 5) + (necklace * 15);
					if (!Config.ALT_VILLAGES_REPEATABLE_QUEST_REWARD && ((amulet + necklace) >= 10))
					{
						reward += 1000;
					}
					
					takeItems(player, ORC_AMULET, -1);
					takeItems(player, ORC_NECKLACE, -1);
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
		
		switch (npc.getId())
		{
			case KABOO_ORC:
			case KABOO_ORC_GRUNT:
			case KABOO_ORC_ARCHER:
			{
				if (Rnd.get(10) < 5)
				{
					giveItems(player, ORC_AMULET, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				break;
			}
			case KABOO_ORC_FIGHTER:
			case KABOO_ORC_FIGHTER_LEADER:
			case KABOO_ORC_FIGHTER_LIEUTENANT:
			{
				if (Rnd.get(10) < 5)
				{
					giveItems(player, ORC_NECKLACE, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				break;
			}
		}
		
		return super.onKill(npc, player, isPet);
	}
}
