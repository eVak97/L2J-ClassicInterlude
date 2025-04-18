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
package handlers.effecthandlers;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.effects.AbstractEffect;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.stats.TraitType;

/**
 * Attack Trait effect implementation.
 * @author NosBit
 */
public class AttackTrait extends AbstractEffect
{
	private final Map<TraitType, Float> _attackTraits = new EnumMap<>(TraitType.class);
	
	public AttackTrait(StatSet params)
	{
		if (params.isEmpty())
		{
			throw new IllegalArgumentException(getClass().getSimpleName() + ": this effect must have parameters!");
		}
		
		for (Entry<String, Object> param : params.getSet().entrySet())
		{
			_attackTraits.put(TraitType.valueOf(param.getKey()), Float.parseFloat((String) param.getValue()) / 100);
		}
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item)
	{
		for (Entry<TraitType, Float> trait : _attackTraits.entrySet())
		{
			effected.getStat().mergeAttackTrait(trait.getKey(), trait.getValue().floatValue());
		}
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		for (Entry<TraitType, Float> trait : _attackTraits.entrySet())
		{
			effected.getStat().removeAttackTrait(trait.getKey(), trait.getValue().floatValue());
		}
	}
}
