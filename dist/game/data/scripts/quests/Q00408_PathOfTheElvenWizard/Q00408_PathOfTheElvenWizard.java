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
package quests.Q00408_PathOfTheElvenWizard;

import org.l2jmobius.gameserver.enums.ClassId;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.network.serverpackets.SocialAction;

public class Q00408_PathOfTheElvenWizard extends Quest
{
	// NPCs
	private static final int ROSELLA = 30414;
	private static final int GREENIS = 30157;
	private static final int THALIA = 30371;
	private static final int NORTHWIND = 30423;
	// Items
	private static final int ROSELLA_LETTER = 1218;
	private static final int RED_DOWN = 1219;
	private static final int MAGICAL_POWERS_RUBY = 1220;
	private static final int PURE_AQUAMARINE = 1221;
	private static final int APPETIZING_APPLE = 1222;
	private static final int GOLD_LEAVES = 1223;
	private static final int IMMORTAL_LOVE = 1224;
	private static final int AMETHYST = 1225;
	private static final int NOBILITY_AMETHYST = 1226;
	private static final int FERTILITY_PERIDOT = 1229;
	private static final int ETERNITY_DIAMOND = 1230;
	private static final int CHARM_OF_GRAIN = 1272;
	private static final int SAP_OF_THE_MOTHER_TREE = 1273;
	private static final int LUCKY_POTPOURRI = 1274;
	
	public Q00408_PathOfTheElvenWizard()
	{
		super(408);
		registerQuestItems(ROSELLA_LETTER, RED_DOWN, MAGICAL_POWERS_RUBY, PURE_AQUAMARINE, APPETIZING_APPLE, GOLD_LEAVES, IMMORTAL_LOVE, AMETHYST, NOBILITY_AMETHYST, FERTILITY_PERIDOT, CHARM_OF_GRAIN, SAP_OF_THE_MOTHER_TREE, LUCKY_POTPOURRI);
		addStartNpc(ROSELLA);
		addTalkId(ROSELLA, GREENIS, THALIA, NORTHWIND);
		addKillId(20047, 20019, 20466);
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
			case "30414-06.htm":
			{
				if (player.getClassId() != ClassId.ELVEN_MAGE)
				{
					htmltext = (player.getClassId() == ClassId.ELVEN_WIZARD) ? "30414-02a.htm" : "30414-03.htm";
				}
				else if (player.getLevel() < 19)
				{
					htmltext = "30414-04.htm";
				}
				else if (hasQuestItems(player, ETERNITY_DIAMOND))
				{
					htmltext = "30414-05.htm";
				}
				else
				{
					st.startQuest();
					giveItems(player, FERTILITY_PERIDOT, 1);
				}
				break;
			}
			case "30414-07.htm":
			{
				if (!hasQuestItems(player, MAGICAL_POWERS_RUBY))
				{
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					giveItems(player, ROSELLA_LETTER, 1);
				}
				else
				{
					htmltext = "30414-10.htm";
				}
				break;
			}
			case "30414-14.htm":
			{
				if (!hasQuestItems(player, PURE_AQUAMARINE))
				{
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					giveItems(player, APPETIZING_APPLE, 1);
				}
				else
				{
					htmltext = "30414-13.htm";
				}
				break;
			}
			case "30414-18.htm":
			{
				if (!hasQuestItems(player, NOBILITY_AMETHYST))
				{
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					giveItems(player, IMMORTAL_LOVE, 1);
				}
				else
				{
					htmltext = "30414-17.htm";
				}
				break;
			}
			case "30157-02.htm":
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
				takeItems(player, ROSELLA_LETTER, 1);
				giveItems(player, CHARM_OF_GRAIN, 1);
				break;
			}
			case "30371-02.htm":
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
				takeItems(player, APPETIZING_APPLE, 1);
				giveItems(player, SAP_OF_THE_MOTHER_TREE, 1);
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
				htmltext = "30414-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case ROSELLA:
					{
						if (hasQuestItems(player, MAGICAL_POWERS_RUBY, NOBILITY_AMETHYST, PURE_AQUAMARINE))
						{
							htmltext = "30414-24.htm";
							takeItems(player, FERTILITY_PERIDOT, 1);
							takeItems(player, MAGICAL_POWERS_RUBY, 1);
							takeItems(player, NOBILITY_AMETHYST, 1);
							takeItems(player, PURE_AQUAMARINE, 1);
							giveItems(player, ETERNITY_DIAMOND, 1);
							addExpAndSp(player, 3200, 1890);
							player.broadcastPacket(new SocialAction(player.getObjectId(), 3));
							st.exitQuest(true, true);
						}
						else if (hasQuestItems(player, ROSELLA_LETTER))
						{
							htmltext = "30414-08.htm";
						}
						else if (hasQuestItems(player, CHARM_OF_GRAIN))
						{
							if (getQuestItemsCount(player, RED_DOWN) == 5)
							{
								htmltext = "30414-25.htm";
							}
							else
							{
								htmltext = "30414-09.htm";
							}
						}
						else if (hasQuestItems(player, APPETIZING_APPLE))
						{
							htmltext = "30414-15.htm";
						}
						else if (hasQuestItems(player, SAP_OF_THE_MOTHER_TREE))
						{
							if (getQuestItemsCount(player, GOLD_LEAVES) == 5)
							{
								htmltext = "30414-26.htm";
							}
							else
							{
								htmltext = "30414-16.htm";
							}
						}
						else if (hasQuestItems(player, IMMORTAL_LOVE))
						{
							htmltext = "30414-19.htm";
						}
						else if (hasQuestItems(player, LUCKY_POTPOURRI))
						{
							if (getQuestItemsCount(player, AMETHYST) == 2)
							{
								htmltext = "30414-27.htm";
							}
							else
							{
								htmltext = "30414-20.htm";
							}
						}
						else
						{
							htmltext = "30414-11.htm";
						}
						break;
					}
					case GREENIS:
					{
						if (hasQuestItems(player, ROSELLA_LETTER))
						{
							htmltext = "30157-01.htm";
						}
						else if (getQuestItemsCount(player, RED_DOWN) == 5)
						{
							htmltext = "30157-04.htm";
							playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							takeItems(player, CHARM_OF_GRAIN, 1);
							takeItems(player, RED_DOWN, -1);
							giveItems(player, MAGICAL_POWERS_RUBY, 1);
						}
						else if (hasQuestItems(player, CHARM_OF_GRAIN))
						{
							htmltext = "30157-03.htm";
						}
						break;
					}
					case THALIA:
					{
						if (hasQuestItems(player, APPETIZING_APPLE))
						{
							htmltext = "30371-01.htm";
						}
						else if (getQuestItemsCount(player, GOLD_LEAVES) == 5)
						{
							htmltext = "30371-04.htm";
							playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							takeItems(player, GOLD_LEAVES, -1);
							takeItems(player, SAP_OF_THE_MOTHER_TREE, 1);
							giveItems(player, PURE_AQUAMARINE, 1);
						}
						else if (hasQuestItems(player, SAP_OF_THE_MOTHER_TREE))
						{
							htmltext = "30371-03.htm";
						}
						break;
					}
					case NORTHWIND:
					{
						if (hasQuestItems(player, IMMORTAL_LOVE))
						{
							htmltext = "30423-01.htm";
							playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							takeItems(player, IMMORTAL_LOVE, 1);
							giveItems(player, LUCKY_POTPOURRI, 1);
						}
						else if (getQuestItemsCount(player, AMETHYST) == 2)
						{
							htmltext = "30423-03.htm";
							playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							takeItems(player, AMETHYST, -1);
							takeItems(player, LUCKY_POTPOURRI, 1);
							giveItems(player, NOBILITY_AMETHYST, 1);
						}
						else if (hasQuestItems(player, LUCKY_POTPOURRI))
						{
							htmltext = "30423-02.htm";
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
			case 20019:
			{
				if (hasQuestItems(player, SAP_OF_THE_MOTHER_TREE) && (getQuestItemsCount(player, GOLD_LEAVES) < 5) && (getRandom(10) < 4))
				{
					giveItems(player, GOLD_LEAVES, 1);
					if (getQuestItemsCount(player, GOLD_LEAVES) < 5)
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					else
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					}
				}
				break;
			}
			case 20047:
			{
				if (hasQuestItems(player, LUCKY_POTPOURRI) && (getQuestItemsCount(player, AMETHYST) < 2) && (getRandom(10) < 4))
				{
					giveItems(player, AMETHYST, 1);
					if (getQuestItemsCount(player, AMETHYST) < 2)
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					else
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					}
				}
				break;
			}
			case 20466:
			{
				if (hasQuestItems(player, CHARM_OF_GRAIN) && (getQuestItemsCount(player, RED_DOWN) < 5) && (getRandom(10) < 7))
				{
					giveItems(player, RED_DOWN, 1);
					if (getQuestItemsCount(player, RED_DOWN) < 5)
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					else
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					}
				}
				break;
			}
		}
		
		return null;
	}
}
