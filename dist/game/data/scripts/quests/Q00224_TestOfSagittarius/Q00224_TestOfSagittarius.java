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
package quests.Q00224_TestOfSagittarius;

import org.l2jmobius.gameserver.enums.ClassId;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.itemcontainer.Inventory;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.network.serverpackets.SocialAction;

public class Q00224_TestOfSagittarius extends Quest
{
	// NPCs
	private static final int BERNARD = 30702;
	private static final int HAMIL = 30626;
	private static final int SIR_ARON_TANFORD = 30653;
	private static final int VOKIAN = 30514;
	private static final int GAUEN = 30717;
	// Monsters
	private static final int ANT = 20079;
	private static final int ANT_CAPTAIN = 20080;
	private static final int ANT_OVERSEER = 20081;
	private static final int ANT_RECRUIT = 20082;
	private static final int ANT_PATROL = 20084;
	private static final int ANT_GUARD = 20086;
	private static final int NOBLE_ANT = 20089;
	private static final int NOBLE_ANT_LEADER = 20090;
	private static final int BREKA_ORC_SHAMAN = 20269;
	private static final int BREKA_ORC_OVERLORD = 20270;
	private static final int MARSH_STAKATO_WORKER = 20230;
	private static final int MARSH_STAKATO_SOLDIER = 20232;
	private static final int MARSH_STAKATO_DRONE = 20234;
	private static final int MARSH_SPIDER = 20233;
	private static final int ROAD_SCAVENGER = 20551;
	private static final int MANASHEN_GARGOYLE = 20563;
	private static final int LETO_LIZARDMAN = 20577;
	private static final int LETO_LIZARDMAN_ARCHER = 20578;
	private static final int LETO_LIZARDMAN_SOLDIER = 20579;
	private static final int LETO_LIZARDMAN_WARRIOR = 20580;
	private static final int LETO_LIZARDMAN_SHAMAN = 20581;
	private static final int LETO_LIZARDMAN_OVERLORD = 20582;
	private static final int SERPENT_DEMON_KADESH = 27090;
	// Items
	private static final int BERNARD_INTRODUCTION = 3294;
	private static final int HAMIL_LETTER_1 = 3295;
	private static final int HAMIL_LETTER_2 = 3296;
	private static final int HAMIL_LETTER_3 = 3297;
	private static final int HUNTER_RUNE_1 = 3298;
	private static final int HUNTER_RUNE_2 = 3299;
	private static final int TALISMAN_OF_KADESH = 3300;
	private static final int TALISMAN_OF_SNAKE = 3301;
	private static final int MITHRIL_CLIP = 3302;
	private static final int STAKATO_CHITIN = 3303;
	private static final int REINFORCED_BOWSTRING = 3304;
	private static final int MANASHEN_HORN = 3305;
	private static final int BLOOD_OF_LIZARDMAN = 3306;
	private static final int CRESCENT_MOON_BOW = 3028;
	private static final int WOODEN_ARROW = 17;
	// Rewards
	private static final int MARK_OF_SAGITTARIUS = 3293;
	private static final int DIMENSIONAL_DIAMOND = 7562;
	
	public Q00224_TestOfSagittarius()
	{
		super(224);
		registerQuestItems(BERNARD_INTRODUCTION, HAMIL_LETTER_1, HAMIL_LETTER_2, HAMIL_LETTER_3, HUNTER_RUNE_1, HUNTER_RUNE_2, TALISMAN_OF_KADESH, TALISMAN_OF_SNAKE, MITHRIL_CLIP, STAKATO_CHITIN, REINFORCED_BOWSTRING, MANASHEN_HORN, BLOOD_OF_LIZARDMAN, CRESCENT_MOON_BOW);
		addStartNpc(BERNARD);
		addTalkId(BERNARD, HAMIL, SIR_ARON_TANFORD, VOKIAN, GAUEN);
		addKillId(ANT, ANT_CAPTAIN, ANT_OVERSEER, ANT_RECRUIT, ANT_PATROL, ANT_GUARD, NOBLE_ANT, NOBLE_ANT_LEADER, BREKA_ORC_SHAMAN, BREKA_ORC_OVERLORD, MARSH_STAKATO_WORKER, MARSH_STAKATO_SOLDIER, MARSH_STAKATO_DRONE, MARSH_SPIDER, ROAD_SCAVENGER, MANASHEN_GARGOYLE, LETO_LIZARDMAN, LETO_LIZARDMAN_ARCHER, LETO_LIZARDMAN_SOLDIER, LETO_LIZARDMAN_WARRIOR, LETO_LIZARDMAN_SHAMAN, LETO_LIZARDMAN_OVERLORD, SERPENT_DEMON_KADESH);
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
			case "30702-04.htm":
			{
				st.startQuest();
				giveItems(player, BERNARD_INTRODUCTION, 1);
				if (!player.getVariables().getBoolean("secondClassChange39", false))
				{
					htmltext = "30702-04a.htm";
					giveItems(player, DIMENSIONAL_DIAMOND, DF_REWARD_39.get(player.getClassId().getId()));
					player.getVariables().set("secondClassChange39", true);
				}
				break;
			}
			case "30626-03.htm":
			{
				st.setCond(2, true);
				takeItems(player, BERNARD_INTRODUCTION, 1);
				giveItems(player, HAMIL_LETTER_1, 1);
				break;
			}
			case "30626-07.htm":
			{
				st.setCond(5, true);
				takeItems(player, HUNTER_RUNE_1, 10);
				giveItems(player, HAMIL_LETTER_2, 1);
				break;
			}
			case "30653-02.htm":
			{
				st.setCond(3, true);
				takeItems(player, HAMIL_LETTER_1, 1);
				break;
			}
			case "30514-02.htm":
			{
				st.setCond(6, true);
				takeItems(player, HAMIL_LETTER_2, 1);
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
				if ((player.getClassId() != ClassId.ROGUE) && (player.getClassId() != ClassId.ELVEN_SCOUT) && (player.getClassId() != ClassId.ASSASSIN))
				{
					htmltext = "30702-02.htm";
				}
				else if (player.getLevel() < 39)
				{
					htmltext = "30702-01.htm";
				}
				else
				{
					htmltext = "30702-03.htm";
				}
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case BERNARD:
					{
						htmltext = "30702-05.htm";
						break;
					}
					case HAMIL:
					{
						if (cond == 1)
						{
							htmltext = "30626-01.htm";
						}
						else if ((cond == 2) || (cond == 3))
						{
							htmltext = "30626-04.htm";
						}
						else if (cond == 4)
						{
							htmltext = "30626-05.htm";
						}
						else if ((cond > 4) && (cond < 8))
						{
							htmltext = "30626-08.htm";
						}
						else if (cond == 8)
						{
							htmltext = "30626-09.htm";
							st.setCond(9, true);
							takeItems(player, HUNTER_RUNE_2, 10);
							giveItems(player, HAMIL_LETTER_3, 1);
						}
						else if ((cond > 8) && (cond < 12))
						{
							htmltext = "30626-10.htm";
						}
						else if (cond == 12)
						{
							htmltext = "30626-11.htm";
							st.setCond(13, true);
						}
						else if (cond == 13)
						{
							htmltext = "30626-12.htm";
						}
						else if (cond == 14)
						{
							htmltext = "30626-13.htm";
							takeItems(player, BLOOD_OF_LIZARDMAN, -1);
							takeItems(player, CRESCENT_MOON_BOW, 1);
							takeItems(player, TALISMAN_OF_KADESH, 1);
							giveItems(player, MARK_OF_SAGITTARIUS, 1);
							addExpAndSp(player, 54726, 20250);
							player.broadcastPacket(new SocialAction(player.getObjectId(), 3));
							st.exitQuest(false, true);
						}
						break;
					}
					case SIR_ARON_TANFORD:
					{
						if (cond == 2)
						{
							htmltext = "30653-01.htm";
						}
						else if (cond > 2)
						{
							htmltext = "30653-03.htm";
						}
						break;
					}
					case VOKIAN:
					{
						if (cond == 5)
						{
							htmltext = "30514-01.htm";
						}
						else if (cond == 6)
						{
							htmltext = "30514-03.htm";
						}
						else if (cond == 7)
						{
							htmltext = "30514-04.htm";
							st.setCond(8, true);
							takeItems(player, TALISMAN_OF_SNAKE, 1);
						}
						else if (cond > 7)
						{
							htmltext = "30514-05.htm";
						}
						break;
					}
					case GAUEN:
					{
						if (cond == 9)
						{
							htmltext = "30717-01.htm";
							st.setCond(10, true);
							takeItems(player, HAMIL_LETTER_3, 1);
						}
						else if (cond == 10)
						{
							htmltext = "30717-03.htm";
						}
						else if (cond == 11)
						{
							htmltext = "30717-02.htm";
							st.setCond(12, true);
							takeItems(player, MANASHEN_HORN, 1);
							takeItems(player, MITHRIL_CLIP, 1);
							takeItems(player, REINFORCED_BOWSTRING, 1);
							takeItems(player, STAKATO_CHITIN, 1);
							giveItems(player, CRESCENT_MOON_BOW, 1);
							giveItems(player, WOODEN_ARROW, 10);
						}
						else if (cond > 11)
						{
							htmltext = "30717-04.htm";
						}
						break;
					}
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
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		final QuestState st = getQuestState(player, false);
		if ((st == null) || !st.isStarted())
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case ANT:
			case ANT_CAPTAIN:
			case ANT_OVERSEER:
			case ANT_RECRUIT:
			case ANT_PATROL:
			case ANT_GUARD:
			case NOBLE_ANT:
			case NOBLE_ANT_LEADER:
			{
				if (st.isCond(3) && getRandomBoolean())
				{
					giveItems(player, HUNTER_RUNE_1, 1);
					if ((getQuestItemsCount(player, HUNTER_RUNE_1) >= 10))
					{
						st.setCond(4);
					}
					else
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				break;
			}
			case BREKA_ORC_SHAMAN:
			case BREKA_ORC_OVERLORD:
			{
				if (st.isCond(6) && getRandomBoolean())
				{
					giveItems(player, HUNTER_RUNE_2, 1);
					if ((getQuestItemsCount(player, HUNTER_RUNE_2) >= 10))
					{
						st.setCond(7, true);
						giveItems(player, TALISMAN_OF_SNAKE, 1);
					}
					else
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				break;
			}
			case MARSH_STAKATO_WORKER:
			case MARSH_STAKATO_SOLDIER:
			case MARSH_STAKATO_DRONE:
			{
				if (st.isCond(10) && (getRandom(10) < 1))
				{
					giveItems(player, STAKATO_CHITIN, 1);
					if (hasQuestItems(player, MANASHEN_HORN, MITHRIL_CLIP, REINFORCED_BOWSTRING))
					{
						st.setCond(11, true);
					}
					else
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				break;
			}
			case MARSH_SPIDER:
			{
				if (st.isCond(10) && (getRandom(10) < 1))
				{
					giveItems(player, REINFORCED_BOWSTRING, 1);
					if (hasQuestItems(player, MANASHEN_HORN, MITHRIL_CLIP, STAKATO_CHITIN))
					{
						st.setCond(11, true);
					}
					else
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				break;
			}
			case ROAD_SCAVENGER:
			{
				if (st.isCond(10) && (getRandom(10) < 1))
				{
					giveItems(player, MITHRIL_CLIP, 1);
					if (hasQuestItems(player, MANASHEN_HORN, REINFORCED_BOWSTRING, STAKATO_CHITIN))
					{
						st.setCond(11, true);
					}
					else
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				break;
			}
			case MANASHEN_GARGOYLE:
			{
				if (st.isCond(10) && (getRandom(10) < 1))
				{
					giveItems(player, MANASHEN_HORN, 1);
					if (hasQuestItems(player, REINFORCED_BOWSTRING, MITHRIL_CLIP, STAKATO_CHITIN))
					{
						st.setCond(11, true);
					}
					else
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				break;
			}
			case LETO_LIZARDMAN:
			case LETO_LIZARDMAN_ARCHER:
			case LETO_LIZARDMAN_SOLDIER:
			case LETO_LIZARDMAN_WARRIOR:
			case LETO_LIZARDMAN_SHAMAN:
			case LETO_LIZARDMAN_OVERLORD:
			{
				if (st.isCond(13))
				{
					if (((getQuestItemsCount(player, BLOOD_OF_LIZARDMAN) - 120) * 5) > getRandom(100))
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_BEFORE_BATTLE);
						takeItems(player, BLOOD_OF_LIZARDMAN, -1);
						addSpawn(SERPENT_DEMON_KADESH, player, false, 300000);
					}
					else
					{
						giveItems(player, BLOOD_OF_LIZARDMAN, 1);
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				break;
			}
			case SERPENT_DEMON_KADESH:
			{
				if (st.isCond(13))
				{
					if (player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND) == CRESCENT_MOON_BOW)
					{
						st.setCond(14, true);
						giveItems(player, TALISMAN_OF_KADESH, 1);
					}
					else
					{
						addSpawn(SERPENT_DEMON_KADESH, player, false, 300000);
					}
				}
				break;
			}
		}
		
		return null;
	}
}
