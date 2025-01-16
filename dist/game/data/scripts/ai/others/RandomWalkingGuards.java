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
package ai.others;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.geoengine.GeoEngine;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.util.Util;

import ai.AbstractNpcAI;

/**
 * @author Mobius
 */
public class RandomWalkingGuards extends AbstractNpcAI
{
	private static final int[] GUARDS =
	{
		31032, // talking island
		31033, // elf village
		31034, // dark elf village
		31036, // orc village
		31035, // dwarf village
	};
	// Others
	private static final int MIN_WALK_DELAY = 15000;
	private static final int MAX_WALK_DELAY = 45000;
	
	private RandomWalkingGuards()
	{
		addSpawnId(GUARDS);
	}
	
	@Override
	public void onTimerEvent(String event, StatSet params, Npc npc, Player player)
	{
		if ((npc != null) && !npc.isDead() && npc.isSpawned())
		{
			if (!npc.isInCombat())
			{
				final Location randomLoc = Util.getRandomPosition(npc.getSpawn().getLocation(), 0, Config.MAX_DRIFT_RANGE);
				addMoveToDesire(npc, GeoEngine.getInstance().getValidLocation(npc.getX(), npc.getY(), npc.getZ(), randomLoc.getX(), randomLoc.getY(), randomLoc.getZ(), npc.getInstanceWorld()), 23);
			}
			getTimers().addTimer("RANDOM_WALK_" + npc.getObjectId(), null, getRandom(MIN_WALK_DELAY, MAX_WALK_DELAY), npc, null);
		}
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		getTimers().addTimer("RANDOM_WALK_" + npc.getObjectId(), null, getRandom(MIN_WALK_DELAY, MAX_WALK_DELAY), npc, null);
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new RandomWalkingGuards();
	}
}
