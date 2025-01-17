/*
 * Copyright (c) 2013 L2jMobius
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package quests.Q00154_SacrificeToTheSea;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;

/**
 * Sacrifice to the Sea (154)
 * @author Mobius
 */
public class Q00154_SacrificeToTheSea extends Quest
{
	// NPCs
	private static final int ROCKSWELL = 30312;
	private static final int CRISTEL = 30051;
	private static final int ROLLFNAN = 30055;
	// Items
	private static final int FOX_FUR = 1032;
	private static final int FOX_FUR_YAM = 1033;
	private static final int MAIDEN_DOLL = 1034;
	// Monsters
	private static final int ELDER_KELTIR = 20544;
	private static final int YOUNG_KELTIR = 20545;
	private static final int KELTIR = 20481;
	// Reward
	private static final int MAGE_EARING = 113;
	// Misc
	private static final int MIN_LEVEL = 2;
	
	public Q00154_SacrificeToTheSea()
	{
		super(154);
		addStartNpc(ROCKSWELL);
		addTalkId(ROCKSWELL, CRISTEL, ROLLFNAN);
		addKillId(ELDER_KELTIR, YOUNG_KELTIR, KELTIR);
		registerQuestItems(FOX_FUR, FOX_FUR_YAM, MAIDEN_DOLL);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && event.equals("30312-03.htm"))
		{
			qs.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public String onTalk(Npc npc, Player talker)
	{
		final QuestState qs = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		
		switch (npc.getId())
		{
			case ROCKSWELL:
			{
				if (qs.isCreated())
				{
					htmltext = ((talker.getLevel() >= MIN_LEVEL) ? "30312-01.htm" : "30312-02.htm");
				}
				else if (qs.isStarted())
				{
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "30312-04.html";
							break;
						}
						case 2:
						{
							htmltext = "30312-07.html";
							break;
						}
						case 3:
						{
							htmltext = "30312-05.html";
							break;
						}
						case 4:
						{
							takeItems(talker, MAIDEN_DOLL, -1);
							rewardItems(talker, MAGE_EARING, 1);
							addExpAndSp(talker, 0, 1000);
							qs.exitQuest(false, true);
							htmltext = "30312-06.html";
							break;
						}
					}
				}
				else
				{
					htmltext = getAlreadyCompletedMsg(talker);
				}
				break;
			}
			case CRISTEL:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						htmltext = "30051-02.html";
						break;
					}
					case 2:
					{
						takeItems(talker, FOX_FUR, -1);
						giveItems(talker, FOX_FUR_YAM, 1);
						qs.setCond(3, true);
						htmltext = "30051-01.html";
						break;
					}
					case 3:
					{
						htmltext = "30051-03.html";
						break;
					}
					case 4:
					{
						htmltext = "30051-04.html";
						break;
					}
				}
				break;
			}
			case ROLLFNAN:
			{
				switch (qs.getCond())
				{
					case 1:
					case 2:
					{
						htmltext = "30055-03.html";
						break;
					}
					case 3:
					{
						takeItems(talker, FOX_FUR_YAM, -1);
						giveItems(talker, MAIDEN_DOLL, 1);
						qs.setCond(4, true);
						htmltext = "30055-01.html";
						break;
					}
					case 4:
					{
						htmltext = "30055-02.html";
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(killer, 1, 3, npc);
		if ((qs != null) && giveItemRandomly(qs.getPlayer(), npc, FOX_FUR, 1, 10, 0.3, true))
		{
			qs.setCond(2);
		}
		return super.onKill(npc, killer, isSummon);
	}
}