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

import java.util.logging.Level;

import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.data.xml.NpcData;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.Spawn;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.instance.Decoy;
import org.l2jmobius.gameserver.model.actor.instance.EffectPoint;
import org.l2jmobius.gameserver.model.actor.templates.NpcTemplate;
import org.l2jmobius.gameserver.model.effects.AbstractEffect;
import org.l2jmobius.gameserver.model.effects.EffectType;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.targets.TargetType;

/**
 * Summon Npc effect implementation.
 * @author Zoey76
 */
public class SummonNpc extends AbstractEffect
{
	private final int _despawnDelay;
	private final int _npcId;
	private final int _npcCount;
	private final boolean _randomOffset;
	private final boolean _isSummonSpawn;
	private final boolean _singleInstance; // Only one instance of this NPC is allowed.
	private final boolean _isAggressive;
	
	public SummonNpc(StatSet params)
	{
		_despawnDelay = params.getInt("despawnDelay", 0);
		_npcId = params.getInt("npcId", 0);
		_npcCount = params.getInt("npcCount", 1);
		_randomOffset = params.getBoolean("randomOffset", false);
		_isSummonSpawn = params.getBoolean("isSummonSpawn", false);
		_singleInstance = params.getBoolean("singleInstance", false);
		_isAggressive = params.getBoolean("aggressive", true); // Used by Decoy.
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.SUMMON_NPC;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item)
	{
		if ((_npcId <= 0) || (_npcCount <= 0))
		{
			LOGGER.warning(SummonNpc.class.getSimpleName() + ": Invalid NPC ID or count skill ID: " + skill.getId());
			return;
		}
		
		if (!effected.isPlayer() || effected.isAlikeDead())
		{
			return;
		}
		
		final Player player = effected.asPlayer();
		if (player.inObserverMode())
		{
			return;
		}
		
		if (player.isMounted())
		{
			return;
		}
		
		final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(_npcId);
		if (npcTemplate == null)
		{
			LOGGER.warning(SummonNpc.class.getSimpleName() + ": Spawn of the nonexisting NPC ID: " + _npcId + ", skill ID:" + skill.getId());
			return;
		}
		
		int x = player.getX();
		int y = player.getY();
		int z = player.getZ();
		
		if (skill.getTargetType() == TargetType.GROUND)
		{
			final Location wordPosition = player.getCurrentSkillWorldPosition();
			if (wordPosition != null)
			{
				x = wordPosition.getX();
				y = wordPosition.getY();
				z = wordPosition.getZ();
			}
		}
		else
		{
			x = effected.getX();
			y = effected.getY();
			z = effected.getZ();
		}
		
		if (_randomOffset)
		{
			x += (Rnd.nextBoolean() ? Rnd.get(20, 50) : Rnd.get(-50, -20));
			y += (Rnd.nextBoolean() ? Rnd.get(20, 50) : Rnd.get(-50, -20));
		}
		
		// If only single instance is allowed, delete previous NPCs.
		if (_singleInstance)
		{
			for (Npc npc : player.getSummonedNpcs())
			{
				if (npc.getId() == _npcId)
				{
					npc.deleteMe();
				}
			}
		}
		
		switch (npcTemplate.getType())
		{
			case "Decoy":
			{
				final Decoy decoy = new Decoy(npcTemplate, player, _despawnDelay > 0 ? _despawnDelay : 20000, _isAggressive);
				decoy.setCurrentHp(decoy.getMaxHp());
				decoy.setCurrentMp(decoy.getMaxMp());
				decoy.setHeading(player.getHeading());
				decoy.setInstance(player.getInstanceWorld());
				decoy.setSummoner(player);
				decoy.spawnMe(x, y, z);
				break;
			}
			case "EffectPoint":
			{
				final EffectPoint effectPoint = new EffectPoint(npcTemplate, player);
				effectPoint.setCurrentHp(effectPoint.getMaxHp());
				effectPoint.setCurrentMp(effectPoint.getMaxMp());
				effectPoint.setInvul(true);
				effectPoint.setSummoner(player);
				effectPoint.setTitle(player.getName());
				effectPoint.spawnMe(x, y, z);
				// First consider NPC template despawn_time parameter.
				final long despawnTime = (long) (effectPoint.getParameters().getFloat("despawn_time", 0) * 1000);
				if (despawnTime > 0)
				{
					effectPoint.scheduleDespawn(despawnTime);
				}
				else if (_despawnDelay > 0) // Use skill despawnDelay parameter.
				{
					effectPoint.scheduleDespawn(_despawnDelay);
				}
				break;
			}
			default:
			{
				Spawn spawn;
				try
				{
					spawn = new Spawn(npcTemplate);
				}
				catch (Exception e)
				{
					LOGGER.log(Level.WARNING, SummonNpc.class.getSimpleName() + ": Unable to create spawn. " + e.getMessage(), e);
					return;
				}
				
				spawn.setXYZ(x, y, z);
				spawn.setHeading(player.getHeading());
				spawn.stopRespawn();
				
				final Npc npc = spawn.doSpawn(_isSummonSpawn);
				player.addSummonedNpc(npc); // npc.setSummoner(player);
				npc.setName(npcTemplate.getName());
				npc.setTitle(npcTemplate.getName());
				if (_despawnDelay > 0)
				{
					npc.scheduleDespawn(_despawnDelay);
				}
				npc.broadcastInfo();
			}
		}
	}
}
