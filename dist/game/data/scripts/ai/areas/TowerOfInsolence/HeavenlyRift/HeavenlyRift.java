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
package ai.areas.TowerOfInsolence.HeavenlyRift;

import org.l2jmobius.gameserver.ai.CtrlIntention;
import org.l2jmobius.gameserver.enums.ChatType;
import org.l2jmobius.gameserver.instancemanager.ZoneManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.Party;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Playable;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.instance.FriendlyNpc;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.zone.ZoneType;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ExChangeNpcState;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.util.Util;

import ai.AbstractNpcAI;

/**
 * TODO: Re-enter time for all players is 20 mins after someone finish the dung.
 * @author GolbergSoft
 */
public class HeavenlyRift extends AbstractNpcAI
{
	// NPCs
	private static final int DIMENTIONAL_VORTEX = 30952;
	private static final int ARUSHINAI = 30401;
	// Monsters
	private static final int BOMB = 18003;
	private static final int DIVINE_ANGEL = 20139;
	private static final int TOWER = 18004;
	// Items
	private static final int CELESTIAL_SHARD = 49759;
	private static final int BROKEN_CELESTIAL_SHARD = 49767;
	private static final int[] ITEM_DROP_1 = new int[]
	{
		49756,
		49762,
		49763
	};
	private static final int[] ITEM_DROP_2 = new int[]
	{
		49760,
		49761
	};
	// Reward
	private static final int SP_SCROLL_20000 = 49764;
	private static final int LIFE_CONTROL_TOWER_SCROLL_OF_BLESSING = 49765;
	// Zone
	protected static final ZoneType ZONE = ZoneManager.getInstance().getZoneByName("heavenly_rift");
	// Teleports
	private static final Location ENTER = new Location(112685, 13362, 10966);
	private static final Location EXIT = new Location(114326, 13407, -5096);
	private static final Location CENTER = new Location(112710, 14098, 10984);
	// Time
	private static final int TIME_FOR_PREPARE = 3; // min
	private static final int BATTLE_TIME = 20; // min
	// Misc
	private static final int CHANCE_TO_SPAWN_ANGEL_AFTER_KILL_BOOM = 33; // %
	// Etc - variation 1
	private static int _var1; // Tower and 20x of Angels
	private static int _varAngelCount1;
	private static int _varRewardTaken1; // 0 not taken 1 taken
	// Etc - variation 2
	private static int _var2; // Bombs
	private static int _varBombCount2;
	// Etc - variation 3
	private static int _var3; // 40x of Angels
	private static int _varAngelCount3;
	
	public HeavenlyRift()
	{
		addStartNpc(DIMENTIONAL_VORTEX);
		addFirstTalkId(DIMENTIONAL_VORTEX, ARUSHINAI, TOWER);
		addKillId(BOMB, DIVINE_ANGEL, TOWER);
		addSpawnId(BOMB, DIVINE_ANGEL);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			// If player leave party, it will deport.
			case "ZONE_CHECKER":
			{
				if (ZONE.getPlayersInside().isEmpty())
				{
					startQuestTimer("CLEAN", 10000, null, null);
				}
				else
				{
					ZONE.getCharactersInside().stream().filter(creature -> creature.isPlayer() && !creature.isInParty()).forEach(creature ->
					{
						creature.teleToLocation(EXIT);
						creature.sendMessage("You are not in a party. You will be deported out.");
					});
					startQuestTimer("ZONE_CHECKER", 10000, null, null);
				}
				break;
			}
			case "CLEAN":
			{
				cancelQuestTimers("ZONE_CHECKER");
				cancelQuestTimers("BATLLE_TIME");
				
				for (Creature creature : ZONE.getCharactersInside())
				{
					if (creature.isPlayer())
					{
						creature.teleToLocation(EXIT);
					}
					else if (creature.isNpc() || creature.isMonster())
					{
						if (creature.getId() != ARUSHINAI)
						{
							creature.deleteMe();
						}
					}
					resetVars();
				}
				break;
			}
			case "FINISH":
			{
				cancelQuestTimers("ZONE_CHECKER");
				if (player.isInParty())
				{
					for (Player partyMember : player.getParty().getMembers())
					{
						partyMember.teleToLocation(EXIT);
					}
				}
				else
				{
					// Prevent to get null if testing with Gm, others single players will be teleported out by ZONE_CHECKER.
					player.teleToLocation(EXIT);
				}
				startQuestTimer("CLEAN", 1000, null, null);
				break;
			}
			case "GET_REWARD":
			{
				if (_varRewardTaken1 == 0)
				{
					player.getParty().getMembers().forEach(partyMember ->
					{
						partyMember.addItem("Heavenly Dung SP", SP_SCROLL_20000, 1, npc, true);
						partyMember.addItem("Heavenly Dung Life C", LIFE_CONTROL_TOWER_SCROLL_OF_BLESSING, 1, npc, true);
					});
					_varRewardTaken1 = 1;
				}
				else
				{
					return "18004-already-get-reward.html";
				}
				break;
			}
			case "EXCHANGE":
			{
				long count = getQuestItemsCount(player, BROKEN_CELESTIAL_SHARD);
				if (count < 10)
				{
					player.sendMessage("For successful exchange you need to have 10x Broken Celestial Shards.");
					return "30952-2.htm";
				}
				
				count -= count % 10;
				long reward = count / 10;
				player.destroyItemByItemId("Rift", BROKEN_CELESTIAL_SHARD, count, npc, true);
				player.addItem("Rift", CELESTIAL_SHARD, reward, npc, true);
				break;
			}
			case "ENTER":
			{
				if (ZONE.getPlayersInside().isEmpty())
				{
					startQuestTimer("ZONE_CHECKER", 10000, null, null);
					
					final Party party = player.getParty();
					if (party == null)
					{
						return "no-party.html";
					}
					
					if (party.getLeader() != player)
					{
						return "no-party-leader.html";
					}
					
					if (party.getMembers().stream().anyMatch(partyMember -> partyMember.getLevel() < 75))
					{
						return "no-level.html";
					}
					final Item item = player.getInventory().getItemByItemId(CELESTIAL_SHARD);
					if (item == null)
					{
						return "no-item.html";
					}
					
					if (party.getMembers().stream().anyMatch(partyMember -> !Util.checkIfInRange(1000, player, partyMember, true)))
					{
						return "no-range.html";
					}
					
					player.destroyItemByItemId("Rift", CELESTIAL_SHARD, 1, npc, true);
					startQuestTimer("BEFORE_START", 60000 * TIME_FOR_PREPARE, null, player);
					
					party.getMembers().forEach(partyMember ->
					{
						partyMember.broadcastPacket(new ExShowScreenMessage("You have " + TIME_FOR_PREPARE + " minutes to start battle.", 2, 5000, 0, false, true));
						partyMember.teleToLocation(ENTER);
					});
					
					resetVars();
				}
				else
				{
					return "30952-no-free.html";
				}
				break;
			}
			case "BEFORE_START":
			{
				ZONE.broadcastPacket(new ExShowScreenMessage("Your party leader did not start the event, therefore they will be deported.", 2, 5000, 0, false, true));
				for (Creature creature : ZONE.getCharactersInside())
				{
					if (creature.isPlayer())
					{
						creature.teleToLocation(EXIT);
					}
					if (creature.isNpc() || creature.isMonster())
					{
						if (creature.getId() != ARUSHINAI)
						{
							creature.deleteMe();
						}
					}
				}
				break;
			}
			case "BATLLE_TIME":
			{
				ZONE.broadcastPacket(new ExShowScreenMessage("Your party has run out of " + BATTLE_TIME + " minutes to complete the task, you will be deported away from the rift.", 2, 5000, 0, false, true));
				for (Creature creature : ZONE.getCharactersInside())
				{
					if (creature.isPlayer())
					{
						creature.teleToLocation(EXIT);
					}
					if (creature.isNpc() || creature.isMonster())
					{
						if (creature.getId() != ARUSHINAI)
						{
							creature.deleteMe();
						}
					}
				}
				break;
			}
			case "START_DUNG":
			{
				cancelQuestTimers("BEFORE_START");
				startQuestTimer("BATLLE_TIME", 60000 * BATTLE_TIME, null, player);
				ZONE.broadcastPacket(new ExShowScreenMessage("Your party has " + BATTLE_TIME + " minutes to complete the tasks.", 2, 5000, 0, false, true));
				
				final int riftLevel = getRandom(1, 3);
				switch (riftLevel)
				{
					case 1:
					{
						_var1 = 1;
						ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.PROTECT_THE_CENTRAL_TOWER_FROM_DIVINE_ANGELS, 2, 5000));
						final FriendlyNpc tower = (FriendlyNpc) addSpawn(TOWER, 112648, 14072, 10986, 1, false, 60000 * BATTLE_TIME);
						for (int i = 0; i < 20; ++i)
						{
							Creature angel = addSpawn(DIVINE_ANGEL, 112696, 13960, 10986, 1, false, 60000 * BATTLE_TIME);
							
							angel.setRunning();
							angel.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, tower);
							angel.asAttackable().addDamageHate(tower, 0, 999999);
							
							_varAngelCount1++;
						}
						break;
					}
					case 2:
					{
						_var2 = 1;
						ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.SET_OFF_BOMBS_AND_GET_TREASURES, 2, 5000));
						addSpawn(BOMB, 113352, 12936, 10986, 0, false, 60000 * BATTLE_TIME);
						addSpawn(BOMB, 113592, 13272, 10986, 0, false, 60000 * BATTLE_TIME);
						addSpawn(BOMB, 113816, 13592, 10986, 0, false, 60000 * BATTLE_TIME);
						addSpawn(BOMB, 113080, 13192, 10986, 0, false, 60000 * BATTLE_TIME);
						addSpawn(BOMB, 113336, 13528, 10986, 0, false, 60000 * BATTLE_TIME);
						addSpawn(BOMB, 113560, 13832, 10986, 0, false, 60000 * BATTLE_TIME);
						addSpawn(BOMB, 112776, 13512, 10986, 0, false, 60000 * BATTLE_TIME);
						addSpawn(BOMB, 113064, 13784, 10986, 0, false, 60000 * BATTLE_TIME);
						addSpawn(BOMB, 112440, 13848, 10986, 0, false, 60000 * BATTLE_TIME);
						addSpawn(BOMB, 112728, 14104, 10986, 0, false, 60000 * BATTLE_TIME);
						addSpawn(BOMB, 112760, 14600, 10986, 0, false, 60000 * BATTLE_TIME);
						addSpawn(BOMB, 112392, 14456, 10986, 0, false, 60000 * BATTLE_TIME);
						addSpawn(BOMB, 112104, 14184, 10986, 0, false, 60000 * BATTLE_TIME);
						addSpawn(BOMB, 111816, 14488, 10986, 0, false, 60000 * BATTLE_TIME);
						addSpawn(BOMB, 112104, 14760, 10986, 0, false, 60000 * BATTLE_TIME);
						addSpawn(BOMB, 112392, 15032, 10986, 0, false, 60000 * BATTLE_TIME);
						addSpawn(BOMB, 112120, 15288, 10986, 0, false, 60000 * BATTLE_TIME);
						addSpawn(BOMB, 111784, 15064, 10986, 0, false, 60000 * BATTLE_TIME);
						addSpawn(BOMB, 111480, 14824, 10986, 0, false, 60000 * BATTLE_TIME);
						addSpawn(BOMB, 113144, 14216, 10986, 0, false, 60000 * BATTLE_TIME);
						_varBombCount2 = 20;
						break;
					}
					case 3:
					{
						_var3 = 1;
						ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.DESTROY_WEAKENED_DIVINE_ANGELS, 2, 5000));
						
						for (int i = 0; i < 40; ++i)
						{
							addSpawn(DIVINE_ANGEL, ZONE.getZone().getRandomPoint(), false, 60000 * BATTLE_TIME);
						}
						
						_varAngelCount3 = 40;
						break;
					}
				}
				break;
			}
			case "30952.htm":
			case "30952-01.html":
			case "30952-02.html":
			{
				return event;
			}
		}
		
		return super.onEvent(event, npc, player);
	}
	
	private void resetVars()
	{
		_var1 = 0;
		_var1 = 0;
		_var2 = 0;
		_var3 = 0;
		_varAngelCount1 = 0;
		_varRewardTaken1 = 0;
		_varBombCount2 = 0;
		_varAngelCount3 = 0;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		final Party party = player.getParty();
		switch (npc.getId())
		{
			case TOWER:
			{
				if ((party == null) || (party.getLeader() != player))
				{
					return "18004-only-pt-leader.html";
				}
				
				return (_varAngelCount1 == 0) ? (_varRewardTaken1 == 1 ? "18004-already-get-reward.html" : "18004-success.html") : "18004-not-success.html";
			}
			case ARUSHINAI:
			{
				if (party == null)
				{
					player.sendPacket(SystemMessageId.ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER);
					return getNoQuestMsg(player);
				}
				
				if (party.getLeader() != player)
				{
					return "no-party-leader.html";
				}
				
				if ((_var1 == 0) && (_var2 == 0) && (_var3 == 0))
				{
					return "30401.html";
				}
				
				if (((_var1 == 1) && (_varAngelCount1 != 0)) || ((_var2 == 1) && (_varBombCount2 != 0)) || ((_var3 == 1) && (_varAngelCount3 != 0)))
				{
					npc.broadcastSay(ChatType.NPC_WHISPER, "Keep fighting " + player.getName() + " !");
					return "30401-fight.html";
				}
				
				return "30401-finish.html";
			}
		}
		return super.onFirstTalk(npc, player);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		switch (npc.getId())
		{
			case BOMB:
			{
				npc.broadcastPacket(new ExChangeNpcState(npc.getObjectId(), 1));
				break;
			}
			case DIVINE_ANGEL:
			{
				if (_var3 == 1)
				{
					ZONE.getCharactersInside().forEach(player -> npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, CENTER));
				}
			}
		}
		
		return super.onSpawn(npc);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		switch (npc.getId())
		{
			case TOWER:
			{
				ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.YOU_HAVE_FAILED, 2, 5000));
				for (Creature creature : ZONE.getCharactersInside())
				{
					if (creature.isPlayer())
					{
						creature.teleToLocation(EXIT);
					}
					
					if (creature.isMonster() && !creature.isDead() && (creature.getId() == DIVINE_ANGEL))
					{
						creature.deleteMe();
					}
				}
				break;
			}
			case BOMB:
			{
				_varBombCount2--;
				if (getRandom(100) < CHANCE_TO_SPAWN_ANGEL_AFTER_KILL_BOOM)
				{
					addSpawn(DIVINE_ANGEL, npc, false, 60000 * BATTLE_TIME);
				}
				else
				{
					World.getInstance().forEachVisibleObjectInRange((WorldObject) npc, Playable.class, 200, creature ->
					{
						if ((creature != null) && !creature.isDead())
						{
							creature.setTarget(killer);
							creature.reduceCurrentHp(getRandom(100, 400), killer, null);
						}
					});
					if (getRandom(100) < 50)
					{
						if (getRandom(100) < 50)
						{
							npc.dropItem(killer.asPlayer(), getRandom(100) < 90 ? ITEM_DROP_1[getRandom(ITEM_DROP_1.length)] : ITEM_DROP_2[getRandom(ITEM_DROP_2.length)], 1);
						}
					}
				}
				break;
			}
			case DIVINE_ANGEL:
			{
				if (_var1 == 1)
				{
					_varAngelCount1--;
				}
				else if (_var3 == 1)
				{
					_varAngelCount3--;
				}
				break;
			}
		}
		
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new HeavenlyRift();
	}
}
