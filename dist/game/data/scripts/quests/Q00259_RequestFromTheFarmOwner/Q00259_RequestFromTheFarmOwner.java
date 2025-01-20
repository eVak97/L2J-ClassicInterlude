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
package quests.Q00259_RequestFromTheFarmOwner;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00259_RequestFromTheFarmOwner extends Quest
{
	// NPCs
	private static final int EDMOND = 30497;
	private static final int MARIUS = 30405;
	// Monsters
	private static final int GIANT_SPIDER = 20103;
	private static final int TALON_SPIDER = 20106;
	private static final int BLADE_SPIDER = 20108;
	// Items
	private static final int GIANT_SPIDER_SKIN = 1495;
	// Rewards
	private static final int ADENA = 57;
	private static final int HEALING_POTION = 1061;
	private static final int WOODEN_ARROW = 17;
	
	public Q00259_RequestFromTheFarmOwner()
	{
		super(259);
		registerQuestItems(GIANT_SPIDER_SKIN);
		addStartNpc(EDMOND);
		addTalkId(EDMOND, MARIUS);
		addKillId(GIANT_SPIDER, TALON_SPIDER, BLADE_SPIDER);
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
			case "30497-03.htm":
			{
				st.startQuest();
				break;
			}
			case "30497-06.htm":
			{
				st.exitQuest(true, true);
				break;
			}
			case "30405-04.htm":
			{
				if (getQuestItemsCount(player, GIANT_SPIDER_SKIN) >= 10)
				{
					takeItems(player, GIANT_SPIDER_SKIN, 10);
					rewardItems(player, HEALING_POTION, 1);
				}
				else
				{
					htmltext = "<html><body>Incorrect item count</body></html>";
				}
				break;
			}
			case "30405-05.htm":
			{
				if (getQuestItemsCount(player, GIANT_SPIDER_SKIN) >= 10)
				{
					takeItems(player, GIANT_SPIDER_SKIN, 10);
					rewardItems(player, WOODEN_ARROW, 50);
				}
				else
				{
					htmltext = "<html><body>Incorrect item count</body></html>";
				}
				break;
			}
			case "30405-07.htm":
			{
				if (getQuestItemsCount(player, GIANT_SPIDER_SKIN) >= 10)
				{
					htmltext = "30405-06.htm";
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
				htmltext = (player.getLevel() < 15) ? "30497-01.htm" : "30497-02.htm";
				break;
			}
			case State.STARTED:
			{
				final int count = (int) getQuestItemsCount(player, GIANT_SPIDER_SKIN);
				switch (npc.getId())
				{
					case EDMOND:
					{
						if (count == 0)
						{
							htmltext = "30497-04.htm";
						}
						else
						{
							htmltext = "30497-05.htm";
							takeItems(player, GIANT_SPIDER_SKIN, -1);
							int reward = count * 25;
							if (!Config.ALT_VILLAGES_REPEATABLE_QUEST_REWARD && (count >= 10))
							{
								reward += 250;
							}
							rewardItems(player, ADENA, reward);
						}
						break;
					}
					case MARIUS:
					{
						htmltext = (count < 10) ? "30405-01.htm" : "30405-02.htm";
						break;
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
		if ((st == null) || !st.isCond(1))
		{
			return null;
		}
		
		giveItems(player, GIANT_SPIDER_SKIN, 1);
		playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		
		return null;
	}
}
