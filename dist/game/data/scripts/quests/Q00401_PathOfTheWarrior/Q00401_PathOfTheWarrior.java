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
package quests.Q00401_PathOfTheWarrior;

import org.l2jmobius.gameserver.enums.ClassId;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.itemcontainer.Inventory;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.network.serverpackets.SocialAction;

public class Q00401_PathOfTheWarrior extends Quest
{
	// Items
	private static final int AURON_LETTER = 1138;
	private static final int WARRIOR_GUILD_MARK = 1139;
	private static final int RUSTED_BRONZE_SWORD_1 = 1140;
	private static final int RUSTED_BRONZE_SWORD_2 = 1141;
	private static final int RUSTED_BRONZE_SWORD_3 = 1142;
	private static final int SIMPLON_LETTER = 1143;
	private static final int POISON_SPIDER_LEG = 1144;
	private static final int MEDALLION_OF_WARRIOR = 1145;
	// NPCs
	private static final int AURON = 30010;
	private static final int SIMPLON = 30253;
	
	public Q00401_PathOfTheWarrior()
	{
		super(401);
		registerQuestItems(AURON_LETTER, WARRIOR_GUILD_MARK, RUSTED_BRONZE_SWORD_1, RUSTED_BRONZE_SWORD_2, RUSTED_BRONZE_SWORD_3, SIMPLON_LETTER, POISON_SPIDER_LEG);
		addStartNpc(AURON);
		addTalkId(AURON, SIMPLON);
		addKillId(20035, 20038, 20042, 20043);
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
		
		if (event.equals("30010-05.htm"))
		{
			if (player.getClassId() != ClassId.FIGHTER)
			{
				htmltext = (player.getClassId() == ClassId.WARRIOR) ? "30010-03.htm" : "30010-02b.htm";
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "30010-02.htm";
			}
			else if (hasQuestItems(player, MEDALLION_OF_WARRIOR))
			{
				htmltext = "30010-04.htm";
			}
		}
		else if (event.equals("30010-06.htm"))
		{
			st.startQuest();
			giveItems(player, AURON_LETTER, 1);
		}
		else if (event.equals("30253-02.htm"))
		{
			st.setCond(2, true);
			takeItems(player, AURON_LETTER, 1);
			giveItems(player, WARRIOR_GUILD_MARK, 1);
		}
		else if (event.equals("30010-11.htm"))
		{
			st.setCond(5, true);
			takeItems(player, RUSTED_BRONZE_SWORD_2, 1);
			takeItems(player, SIMPLON_LETTER, 1);
			giveItems(player, RUSTED_BRONZE_SWORD_3, 1);
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
				htmltext = "30010-01.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case AURON:
					{
						if (cond == 1)
						{
							htmltext = "30010-07.htm";
						}
						else if ((cond == 2) || (cond == 3))
						{
							htmltext = "30010-08.htm";
						}
						else if (cond == 4)
						{
							htmltext = "30010-09.htm";
						}
						else if (cond == 5)
						{
							htmltext = "30010-12.htm";
						}
						else if (cond == 6)
						{
							htmltext = "30010-13.htm";
							takeItems(player, POISON_SPIDER_LEG, -1);
							takeItems(player, RUSTED_BRONZE_SWORD_3, 1);
							giveItems(player, MEDALLION_OF_WARRIOR, 1);
							addExpAndSp(player, 3200, 1500);
							player.broadcastPacket(new SocialAction(player.getObjectId(), 3));
							st.exitQuest(true, true);
						}
						break;
					}
					case SIMPLON:
					{
						if (cond == 1)
						{
							htmltext = "30253-01.htm";
						}
						else if (cond == 2)
						{
							if (!hasQuestItems(player, RUSTED_BRONZE_SWORD_1))
							{
								htmltext = "30253-03.htm";
							}
							else if (getQuestItemsCount(player, RUSTED_BRONZE_SWORD_1) <= 9)
							{
								htmltext = "30253-03b.htm";
							}
						}
						else if (cond == 3)
						{
							htmltext = "30253-04.htm";
							st.setCond(4, true);
							takeItems(player, RUSTED_BRONZE_SWORD_1, 10);
							takeItems(player, WARRIOR_GUILD_MARK, 1);
							giveItems(player, RUSTED_BRONZE_SWORD_2, 1);
							giveItems(player, SIMPLON_LETTER, 1);
						}
						else if (cond == 4)
						{
							htmltext = "30253-05.htm";
						}
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
		if ((st == null) || !st.isStarted())
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case 20035:
			case 20042:
			{
				if (st.isCond(2) && (getRandom(10) < 4))
				{
					giveItems(player, RUSTED_BRONZE_SWORD_1, 1);
					if (getQuestItemsCount(player, RUSTED_BRONZE_SWORD_1) < 10)
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					else
					{
						st.setCond(3, true);
					}
				}
				break;
			}
			case 20038:
			case 20043:
			{
				if (st.isCond(5) && (player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND) == RUSTED_BRONZE_SWORD_3))
				{
					giveItems(player, POISON_SPIDER_LEG, 1);
					if (getQuestItemsCount(player, POISON_SPIDER_LEG) < 20)
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					else
					{
						st.setCond(6, true);
					}
				}
				break;
			}
		}
		
		return null;
	}
}
