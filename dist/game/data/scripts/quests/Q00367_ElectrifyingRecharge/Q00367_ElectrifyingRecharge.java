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
package quests.Q00367_ElectrifyingRecharge;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.model.skill.Skill;

public class Q00367_ElectrifyingRecharge extends Quest
{
	// NPCs
	private static final int LORAIN = 30673;
	// Monsters
	private static final int CATHEROK = 21035;
	// Item
	private static final int LORAIN_LAMP = 5875;
	private static final int TITAN_LAMP_1 = 5876;
	private static final int TITAN_LAMP_2 = 5877;
	private static final int TITAN_LAMP_3 = 5878;
	private static final int TITAN_LAMP_4 = 5879;
	private static final int TITAN_LAMP_5 = 5880;
	// Reward
	private static final int[] REWARD =
	{
		4553,
		4554,
		4555,
		4556,
		4557,
		4558,
		4559,
		4560,
		4561,
		4562,
		4563,
		4564
	};
	
	public Q00367_ElectrifyingRecharge()
	{
		super(367);
		registerQuestItems(LORAIN_LAMP, TITAN_LAMP_1, TITAN_LAMP_2, TITAN_LAMP_3, TITAN_LAMP_4, TITAN_LAMP_5);
		addStartNpc(LORAIN);
		addTalkId(LORAIN);
		addSpellFinishedId(CATHEROK);
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
			case "30673-03.htm":
			{
				st.startQuest();
				giveItems(player, LORAIN_LAMP, 1);
				break;
			}
			case "30673-09.htm":
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_ACCEPT);
				giveItems(player, LORAIN_LAMP, 1);
				break;
			}
			case "30673-08.htm":
			{
				st.exitQuest(true, true);
				break;
			}
			case "30673-07.htm":
			{
				st.setCond(1);
				playSound(player, QuestSound.ITEMSOUND_QUEST_ACCEPT);
				giveItems(player, LORAIN_LAMP, 1);
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
				htmltext = (player.getLevel() < 37) ? "30673-02.htm" : "30673-01.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				if (cond == 1)
				{
					if (hasQuestItems(player, 5880))
					{
						htmltext = "30673-05.htm";
						playSound(player, QuestSound.ITEMSOUND_QUEST_ACCEPT);
						takeItems(player, 5880, 1);
						giveItems(player, LORAIN_LAMP, 1);
					}
					else if (hasQuestItems(player, 5876))
					{
						htmltext = "30673-04.htm";
						takeItems(player, 5876, 1);
					}
					else if (hasQuestItems(player, 5877))
					{
						htmltext = "30673-04.htm";
						takeItems(player, 5877, 1);
					}
					else if (hasQuestItems(player, 5878))
					{
						htmltext = "30673-04.htm";
						takeItems(player, 5878, 1);
					}
					else
					{
						htmltext = "30673-03.htm";
					}
				}
				else if ((cond == 2) && hasQuestItems(player, 5879))
				{
					htmltext = "30673-06.htm";
					takeItems(player, 5879, 1);
					rewardItems(player, REWARD[getRandom(REWARD.length)], 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_FINISH);
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onSpellFinished(Npc npc, Player player, Skill skill)
	{
		final QuestState st = getQuestState(player, false);
		if ((st == null) || !st.isCond(1))
		{
			return null;
		}
		
		if ((skill.getId() == 4072) && hasQuestItems(player, LORAIN_LAMP))
		{
			final int randomItem = getRandom(5876, 5880);
			takeItems(player, LORAIN_LAMP, 1);
			giveItems(player, randomItem, 1);
			if (randomItem == 5879)
			{
				st.setCond(2, true);
			}
			else
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		
		return null;
	}
}
