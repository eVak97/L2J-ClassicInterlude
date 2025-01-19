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
package quests.Q00257_TheGuardIsBusy;

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

public class Q00257_TheGuardIsBusy extends Quest
{
	// Items
	private static final int GLUDIO_LORD_MARK = 1084;
	private static final int ORC_AMULET = 752;
	private static final int ORC_NECKLACE = 1085;
	private static final int WEREWOLF_FANG = 1086;
	// Newbie Items
	private static final int SPIRITSHOT_FOR_BEGINNERS = 5790;
	private static final int SOULSHOT_FOR_BEGINNERS = 5789;
	
	public Q00257_TheGuardIsBusy()
	{
		super(257);
		registerQuestItems(ORC_AMULET, ORC_NECKLACE, WEREWOLF_FANG, GLUDIO_LORD_MARK);
		addStartNpc(30039); // Gilbert
		addTalkId(30039);
		addKillId(20006, 20093, 20096, 20098, 20130, 20131, 20132, 20342, 20343);
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
		
		if (event.equals("30039-03.htm"))
		{
			st.startQuest();
			giveItems(player, GLUDIO_LORD_MARK, 1);
		}
		else if (event.equals("30039-05.htm"))
		{
			takeItems(player, GLUDIO_LORD_MARK, 1);
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
				htmltext = (player.getLevel() < 6) ? "30039-01.htm" : "30039-02.htm";
				break;
			}
			case State.STARTED:
			{
				final int amulets = (int) getQuestItemsCount(player, ORC_AMULET);
				final int necklaces = (int) getQuestItemsCount(player, ORC_NECKLACE);
				final int fangs = (int) getQuestItemsCount(player, WEREWOLF_FANG);
				
				if ((amulets + necklaces + fangs) == 0)
				{
					htmltext = "30039-04.htm";
				}
				else
				{
					htmltext = "30039-07.htm";
					
					int reward = (5 * amulets) + (10 * fangs) + (15 * necklaces);
					if (!Config.ALT_VILLAGES_REPEATABLE_QUEST_REWARD && ((amulets + necklaces + fangs) >= 10))
					{
						reward += 1000;
					}
					
					takeItems(player, ORC_AMULET, -1);
					takeItems(player, ORC_NECKLACE, -1);
					takeItems(player, WEREWOLF_FANG, -1);
					giveAdena(player, reward, true);
					
					final PlayerVariables vars = player.getVariables();
					// Give newbie reward if player is eligible.
					if ((player.getLevel() < 25) && (st.getInt("Reward") == 0))
					{
						if (!vars.getBoolean("NEWBIE_SHOTS", false))
						{
							st.showQuestionMark(26);
							st.set("Reward", "1");
							
							if (player.isMageClass() && (player.getRace() != Race.ORC))
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
			case 20006:
			case 20130:
			case 20131:
			{
				if (Rnd.get(10) < 5)
				{
					giveItems(player, ORC_AMULET, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				break;
			}
			case 20093:
			case 20096:
			case 20098:
			{
				if (Rnd.get(10) < 5)
				{
					giveItems(player, ORC_NECKLACE, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				break;
			}
			case 20342:
			{
				if (Rnd.get(10) < 2)
				{
					giveItems(player, WEREWOLF_FANG, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				break;
			}
			case 20343:
			{
				if (Rnd.get(10) < 4)
				{
					giveItems(player, WEREWOLF_FANG, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				break;
			}
			case 20132:
			{
				if (Rnd.get(10) < 5)
				{
					giveItems(player, WEREWOLF_FANG, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				break;
			}
		}
		
		return super.onKill(npc, player, isPet);
	}
}
