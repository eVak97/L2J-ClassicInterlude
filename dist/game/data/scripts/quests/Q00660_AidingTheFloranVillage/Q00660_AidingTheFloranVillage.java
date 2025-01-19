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
package quests.Q00660_AidingTheFloranVillage;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00660_AidingTheFloranVillage extends Quest
{
	// NPCs
	private static final int MARIA = 30608;
	private static final int ALEX = 30291;
	// Monsters
	private static final int PLAIN_WATCHMAN = 21102;
	private static final int ROCK_GOLEM = 21103;
	private static final int LIZARDMEN_SUPPLIER = 21104;
	private static final int LIZARDMEN_AGENT = 21105;
	private static final int CURSED_SEER = 21106;
	private static final int LIZARDMEN_COMMANDER = 21107;
	private static final int LIZARDMEN_SHAMAN = 20781;
	// Items
	private static final int WATCHING_EYES = 8074;
	private static final int GOLEM_SHARD = 8075;
	private static final int LIZARDMEN_SCALE = 8076;
	// Rewards
	private static final int ADENA = 57;
	private static final int ENCHANT_WEAPON_D = 955;
	private static final int ENCHANT_ARMOR_D = 956;
	
	public Q00660_AidingTheFloranVillage()
	{
		super(660);
		registerQuestItems(WATCHING_EYES, LIZARDMEN_SCALE, GOLEM_SHARD);
		addStartNpc(MARIA, ALEX);
		addTalkId(MARIA, ALEX);
		addKillId(CURSED_SEER, PLAIN_WATCHMAN, ROCK_GOLEM, LIZARDMEN_SHAMAN, LIZARDMEN_SUPPLIER, LIZARDMEN_COMMANDER, LIZARDMEN_AGENT);
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
			case "30608-04.htm":
			{
				st.startQuest();
				break;
			}
			case "30291-02.htm":
			{
				if (player.getLevel() < 30)
				{
					htmltext = "30291-02a.htm";
				}
				else
				{
					st.startQuest();
					st.setCond(2);
				}
				break;
			}
			case "30291-05.htm":
			{
				final long count = getQuestItemsCount(player, WATCHING_EYES) + getQuestItemsCount(player, LIZARDMEN_SCALE) + getQuestItemsCount(player, GOLEM_SHARD);
				if (count == 0)
				{
					htmltext = "30291-05a.htm";
				}
				else
				{
					takeItems(player, GOLEM_SHARD, -1);
					takeItems(player, LIZARDMEN_SCALE, -1);
					takeItems(player, WATCHING_EYES, -1);
					rewardItems(player, ADENA, (count * 100) + ((count >= 45) ? 9000 : 0));
				}
				break;
			}
			case "30291-06.htm":
			{
				st.exitQuest(true, true);
				break;
			}
			case "30291-11.htm":
			{
				if (!verifyAndRemoveItems(player, 100))
				{
					htmltext = "30291-11a.htm";
				}
				else
				{
					if (getRandom(10) < 8)
					{
						rewardItems(player, ADENA, 1000);
					}
					else
					{
						rewardItems(player, ADENA, 13000);
						rewardItems(player, ENCHANT_ARMOR_D, 1);
					}
				}
				break;
			}
			case "30291-12.htm":
			{
				if (!verifyAndRemoveItems(player, 200))
				{
					htmltext = "30291-12a.htm";
				}
				else
				{
					final int luck = getRandom(15);
					if (luck < 8)
					{
						rewardItems(player, ADENA, 2000);
					}
					else if (luck < 12)
					{
						rewardItems(player, ADENA, 20000);
						rewardItems(player, ENCHANT_ARMOR_D, 1);
					}
					else
					{
						rewardItems(player, ENCHANT_WEAPON_D, 1);
					}
				}
				break;
			}
			case "30291-13.htm":
			{
				if (!verifyAndRemoveItems(player, 500))
				{
					htmltext = "30291-13a.htm";
				}
				else
				{
					if (getRandom(10) < 8)
					{
						rewardItems(player, ADENA, 5000);
					}
					else
					{
						rewardItems(player, ADENA, 45000);
						rewardItems(player, ENCHANT_WEAPON_D, 1);
					}
				}
				break;
			}
			case "30291-17.htm":
			{
				final long count = getQuestItemsCount(player, WATCHING_EYES) + getQuestItemsCount(player, LIZARDMEN_SCALE) + getQuestItemsCount(player, GOLEM_SHARD);
				if (count != 0)
				{
					htmltext = "30291-17a.htm";
					takeItems(player, WATCHING_EYES, -1);
					takeItems(player, LIZARDMEN_SCALE, -1);
					takeItems(player, GOLEM_SHARD, -1);
					rewardItems(player, ADENA, (count * 100) + ((count >= 45) ? 9000 : 0));
				}
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
				switch (npc.getId())
				{
					case MARIA:
						htmltext = (player.getLevel() < 30) ? "30608-01.htm" : "30608-02.htm";
						break;
					
					case ALEX:
						htmltext = "30291-01.htm";
						break;
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case MARIA:
					{
						htmltext = "30608-06.htm";
						break;
					}
					case ALEX:
					{
						final int cond = st.getCond();
						if (cond == 1)
						{
							htmltext = "30291-03.htm";
							st.setCond(2, true);
						}
						else if (cond == 2)
						{
							htmltext = (hasAtLeastOneQuestItem(player, WATCHING_EYES, LIZARDMEN_SCALE, GOLEM_SHARD)) ? "30291-04.htm" : "30291-05a.htm";
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
		final QuestState qs = getRandomPartyMemberState(player, 2, 3, npc);
		if (qs == null)
		{
			return null;
		}
		final Player partyMember = qs.getPlayer();
		
		final QuestState st = getQuestState(partyMember, false);
		if (st == null)
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case PLAIN_WATCHMAN:
			case CURSED_SEER:
			{
				if (getRandom(100) < 79)
				{
					giveItems(partyMember, WATCHING_EYES, 1);
					playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				break;
			}
			case ROCK_GOLEM:
			{
				if (getRandom(100) < 75)
				{
					giveItems(partyMember, GOLEM_SHARD, 1);
					playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				break;
			}
			case LIZARDMEN_SHAMAN:
			case LIZARDMEN_SUPPLIER:
			case LIZARDMEN_AGENT:
			case LIZARDMEN_COMMANDER:
			{
				if (getRandom(100) < 67)
				{
					giveItems(partyMember, LIZARDMEN_SCALE, 1);
					playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				break;
			}
		}
		
		return null;
	}
	
	/**
	 * This method drops items following current counts.
	 * @param player The Player to affect.
	 * @param numberToVerify The count of qItems to drop from the different categories.
	 * @return false when counter isn't reached, true otherwise.
	 */
	private static boolean verifyAndRemoveItems(Player player, int numberToVerify)
	{
		final int eyes = (int) getQuestItemsCount(player, WATCHING_EYES);
		final int scale = (int) getQuestItemsCount(player, LIZARDMEN_SCALE);
		final int shard = (int) getQuestItemsCount(player, GOLEM_SHARD);
		if ((eyes + scale + shard) < numberToVerify)
		{
			return false;
		}
		
		if (eyes >= numberToVerify)
		{
			takeItems(player, WATCHING_EYES, numberToVerify);
		}
		else
		{
			int currentNumber = numberToVerify - eyes;
			takeItems(player, WATCHING_EYES, -1);
			if (scale >= currentNumber)
			{
				takeItems(player, LIZARDMEN_SCALE, currentNumber);
			}
			else
			{
				currentNumber -= scale;
				takeItems(player, LIZARDMEN_SCALE, -1);
				takeItems(player, GOLEM_SHARD, currentNumber);
			}
		}
		return true;
	}
}
