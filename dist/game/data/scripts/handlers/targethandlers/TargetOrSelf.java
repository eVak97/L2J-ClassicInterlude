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
package handlers.targethandlers;

import org.l2jmobius.gameserver.geoengine.GeoEngine;
import org.l2jmobius.gameserver.handler.ITargetTypeHandler;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.targets.TargetType;
import org.l2jmobius.gameserver.network.SystemMessageId;

/**
 * @author Liamxroy
 */
public class TargetOrSelf implements ITargetTypeHandler
{
	@Override
	public Enum<TargetType> getTargetType()
	{
		return TargetType.TARGET_OR_SELF;
	}
	
	@Override
	public WorldObject getTarget(Creature creature, WorldObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
	{
		if (selectedTarget == null)
		{
			return creature;
		}
		
		if (!selectedTarget.isCreature())
		{
			return creature;
		}
		
		if (forceUse)
		{
			return selectedTarget;
		}
		
		// You can always target yourself.
		final Creature target = selectedTarget.asCreature();
		if (creature == target)
		{
			return target;
		}
		
		// Check for cast range if character cannot move. TODO: char will start follow until within castrange, but if his moving is blocked by geodata, this msg will be sent.
		if (dontMove && (creature.calculateDistance2D(target) > skill.getCastRange()))
		{
			if (sendMessage)
			{
				creature.sendPacket(SystemMessageId.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_CANCELLED);
			}
			return creature;
		}
		
		if (skill.isFlyType() && !GeoEngine.getInstance().canMoveToTarget(creature.getX(), creature.getY(), creature.getZ(), target.getX(), target.getY(), target.getZ(), creature.getInstanceWorld()))
		{
			if (sendMessage)
			{
				creature.sendPacket(SystemMessageId.THE_TARGET_IS_LOCATED_WHERE_YOU_CANNOT_CHARGE);
			}
			return creature;
		}
		
		if (selectedTarget.isAutoAttackable(creature))
		{
			return creature;
		}
		
		// Geodata check when character is within range.
		if (!GeoEngine.getInstance().canSeeTarget(creature, target) || (skill.isFlyType() && !GeoEngine.getInstance().canMoveToTarget(creature, target)))
		{
			if (sendMessage)
			{
				creature.sendPacket(SystemMessageId.CANNOT_SEE_TARGET);
			}
			
			return creature;
		}
		
		return target;
	}
}
