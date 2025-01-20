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
package quests.Q00412_PathOfTheDarkWizard;

import org.l2jmobius.gameserver.enums.ClassId;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.network.serverpackets.SocialAction;

public class Q00412_PathOfTheDarkWizard extends Quest
{
	// NPCs
	private static final int VARIKA = 30421;
	private static final int CHARKEREN = 30415;
	private static final int ANNIKA = 30418;
	private static final int ARKENIA = 30419;
	// Items
	private static final int SEED_OF_ANGER = 1253;
	private static final int SEED_OF_DESPAIR = 1254;
	private static final int SEED_OF_HORROR = 1255;
	private static final int SEED_OF_LUNACY = 1256;
	private static final int FAMILY_REMAINS = 1257;
	private static final int VARIKA_LIQUOR = 1258;
	private static final int KNEE_BONE = 1259;
	private static final int HEART_OF_LUNACY = 1260;
	private static final int JEWEL_OF_DARKNESS = 1261;
	private static final int LUCKY_KEY = 1277;
	private static final int CANDLE = 1278;
	private static final int HUB_SCENT = 1279;
	
	public Q00412_PathOfTheDarkWizard()
	{
		super(412);
		registerQuestItems(SEED_OF_ANGER, SEED_OF_DESPAIR, SEED_OF_HORROR, SEED_OF_LUNACY, FAMILY_REMAINS, VARIKA_LIQUOR, KNEE_BONE, HEART_OF_LUNACY, LUCKY_KEY, CANDLE, HUB_SCENT);
		addStartNpc(VARIKA);
		addTalkId(VARIKA, CHARKEREN, ANNIKA, ARKENIA);
		addKillId(20015, 20022, 20045, 20517, 20518);
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
			case "30421-05.htm":
			{
				if (player.getClassId() != ClassId.DARK_MAGE)
				{
					htmltext = (player.getClassId() == ClassId.DARK_WIZARD) ? "30421-02a.htm" : "30421-03.htm";
				}
				else if (player.getLevel() < 19)
				{
					htmltext = "30421-02.htm";
				}
				else if (hasQuestItems(player, JEWEL_OF_DARKNESS))
				{
					htmltext = "30421-04.htm";
				}
				else
				{
					st.startQuest();
					giveItems(player, SEED_OF_DESPAIR, 1);
				}
				break;
			}
			case "30421-07.htm":
			{
				if (hasQuestItems(player, SEED_OF_ANGER))
				{
					htmltext = "30421-06.htm";
				}
				else if (hasQuestItems(player, LUCKY_KEY))
				{
					htmltext = "30421-08.htm";
				}
				else if (getQuestItemsCount(player, FAMILY_REMAINS) == 3)
				{
					htmltext = "30421-18.htm";
				}
				break;
			}
			case "30421-10.htm":
			{
				if (hasQuestItems(player, SEED_OF_HORROR))
				{
					htmltext = "30421-09.htm";
				}
				else if (getQuestItemsCount(player, KNEE_BONE) == 2)
				{
					htmltext = "30421-19.htm";
				}
				break;
			}
			case "30421-13.htm":
			{
				if (hasQuestItems(player, SEED_OF_LUNACY))
				{
					htmltext = "30421-12.htm";
				}
				break;
			}
			case "30415-03.htm":
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
				giveItems(player, LUCKY_KEY, 1);
				break;
			}
			case "30418-02.htm":
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
				giveItems(player, CANDLE, 1);
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
				htmltext = "30421-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case VARIKA:
					{
						if (hasQuestItems(player, SEED_OF_ANGER, SEED_OF_HORROR, SEED_OF_LUNACY))
						{
							htmltext = "30421-16.htm";
							takeItems(player, SEED_OF_ANGER, 1);
							takeItems(player, SEED_OF_DESPAIR, 1);
							takeItems(player, SEED_OF_HORROR, 1);
							takeItems(player, SEED_OF_LUNACY, 1);
							giveItems(player, JEWEL_OF_DARKNESS, 1);
							addExpAndSp(player, 3200, 1650);
							player.broadcastPacket(new SocialAction(player.getObjectId(), 3));
							st.exitQuest(true, true);
						}
						else
						{
							htmltext = "30421-17.htm";
						}
						break;
					}
					case CHARKEREN:
					{
						if (hasQuestItems(player, SEED_OF_ANGER))
						{
							htmltext = "30415-06.htm";
						}
						else if (!hasQuestItems(player, LUCKY_KEY))
						{
							htmltext = "30415-01.htm";
						}
						else if (getQuestItemsCount(player, FAMILY_REMAINS) == 3)
						{
							htmltext = "30415-05.htm";
							playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							takeItems(player, FAMILY_REMAINS, -1);
							takeItems(player, LUCKY_KEY, 1);
							giveItems(player, SEED_OF_ANGER, 1);
						}
						else
						{
							htmltext = "30415-04.htm";
						}
						break;
					}
					case ANNIKA:
					{
						if (hasQuestItems(player, SEED_OF_HORROR))
						{
							htmltext = "30418-04.htm";
						}
						else if (!hasQuestItems(player, CANDLE))
						{
							htmltext = "30418-01.htm";
						}
						else if (getQuestItemsCount(player, KNEE_BONE) == 2)
						{
							htmltext = "30418-04.htm";
							playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							takeItems(player, CANDLE, 1);
							takeItems(player, KNEE_BONE, -1);
							giveItems(player, SEED_OF_HORROR, 1);
						}
						else
						{
							htmltext = "30418-03.htm";
						}
						break;
					}
					case ARKENIA:
					{
						if (hasQuestItems(player, SEED_OF_LUNACY))
						{
							htmltext = "30419-03.htm";
						}
						else if (!hasQuestItems(player, HUB_SCENT))
						{
							htmltext = "30419-01.htm";
							playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							giveItems(player, HUB_SCENT, 1);
						}
						else if (getQuestItemsCount(player, HEART_OF_LUNACY) == 3)
						{
							htmltext = "30419-03.htm";
							playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							takeItems(player, HEART_OF_LUNACY, -1);
							takeItems(player, HUB_SCENT, 1);
							giveItems(player, SEED_OF_LUNACY, 1);
						}
						else
						{
							htmltext = "30419-02.htm";
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
			case 20015:
			{
				if (hasQuestItems(player, LUCKY_KEY) && (getQuestItemsCount(player, FAMILY_REMAINS) < 3) && (getRandom(10) < 5))
				{
					giveItems(player, FAMILY_REMAINS, 1);
					if (getQuestItemsCount(player, FAMILY_REMAINS) < 3)
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
			case 20022:
			case 20517:
			case 20518:
			{
				if (hasQuestItems(player, CANDLE) && (getQuestItemsCount(player, KNEE_BONE) < 2) && (getRandom(10) < 5))
				{
					giveItems(player, KNEE_BONE, 1);
					if (getQuestItemsCount(player, KNEE_BONE) < 2)
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
			case 20045:
			{
				if (hasQuestItems(player, HUB_SCENT) && (getQuestItemsCount(player, HEART_OF_LUNACY) < 3) && (getRandom(10) < 5))
				{
					giveItems(player, HEART_OF_LUNACY, 1);
					if (getQuestItemsCount(player, HEART_OF_LUNACY) < 3)
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
