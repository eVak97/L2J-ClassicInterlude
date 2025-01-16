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
package org.l2jmobius.gameserver.network.clientpackets;

import java.util.Arrays;
import java.util.List;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.ai.CtrlIntention;
import org.l2jmobius.gameserver.data.xml.DoorData;
import org.l2jmobius.gameserver.enums.AdminTeleportType;
import org.l2jmobius.gameserver.enums.FlyType;
import org.l2jmobius.gameserver.enums.SayuneType;
import org.l2jmobius.gameserver.geoengine.GeoEngine;
import org.l2jmobius.gameserver.geoengine.pathfinding.AbstractNodeLoc;
import org.l2jmobius.gameserver.geoengine.pathfinding.PathFinding;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.SayuneEntry;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventDispatcher;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerMoveRequest;
import org.l2jmobius.gameserver.model.events.returns.TerminateReturn;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ActionFailed;
import org.l2jmobius.gameserver.network.serverpackets.FlyToLocation;
import org.l2jmobius.gameserver.network.serverpackets.MagicSkillLaunched;
import org.l2jmobius.gameserver.network.serverpackets.MagicSkillUse;
import org.l2jmobius.gameserver.network.serverpackets.sayune.ExFlyMove;
import org.l2jmobius.gameserver.network.serverpackets.sayune.ExFlyMoveBroadcast;
import org.l2jmobius.gameserver.util.Broadcast;
import org.l2jmobius.gameserver.util.Util;

public class MoveBackwardToLocation extends ClientPacket
{
	private int _targetX;
	private int _targetY;
	private int _targetZ;
	private int _originX;
	private int _originY;
	private int _originZ;
	private int _movementMode;
	
	@Override
	protected void readImpl()
	{
		_targetX = readInt();
		_targetY = readInt();
		_targetZ = readInt();
		_originX = readInt();
		_originY = readInt();
		_originZ = readInt();
		_movementMode = readInt(); // is 0 if cursor keys are used 1 if mouse is used
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (player.isOverloaded())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_MOVE_DUE_TO_THE_WEIGHT_OF_YOUR_INVENTORY);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if ((Config.PLAYER_MOVEMENT_BLOCK_TIME > 0) && !player.isGM() && (player.getNotMoveUntil() > System.currentTimeMillis()))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_MOVE_WHILE_SPEAKING_TO_AN_NPC_ONE_MOMENT_PLEASE);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if ((_targetX == _originX) && (_targetY == _originY) && (_targetZ == _originZ))
		{
			player.stopMove(player.getLocation());
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Check for possible door logout and move over exploit. Also checked at ValidatePosition.
		if (DoorData.getInstance().checkIfDoorsBetween(player.getLastServerPosition(), player.getLocation(), player.getInstanceWorld()))
		{
			player.stopMove(player.getLastServerPosition());
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (_movementMode == 1)
		{
			player.setCursorKeyMovement(false);
			
			// If movement was suspended, do not move when no path, or too complex path found. Tested at retail on October 21st 2024.
			if (player.isMovementSuspended())
			{
				final List<AbstractNodeLoc> path = PathFinding.getInstance().findPath(player.getX(), player.getY(), player.getZ(), _targetX, _targetY, _targetZ, player.getInstanceWorld(), true);
				if ((path == null) || (path.size() > 4))
				{
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
			}
			
			if (EventDispatcher.getInstance().hasListener(EventType.ON_PLAYER_MOVE_REQUEST, player))
			{
				final TerminateReturn terminate = EventDispatcher.getInstance().notifyEvent(new OnPlayerMoveRequest(player, new Location(_targetX, _targetY, _targetZ)), player, TerminateReturn.class);
				if ((terminate != null) && terminate.terminate())
				{
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
			}
		}
		else // 0
		{
			if (!Config.ENABLE_KEYBOARD_MOVEMENT)
			{
				return;
			}
			
			// Check is heading is already blocked.
			final int heading = Util.calculateHeadingFrom(_originX, _originY, _targetX, _targetY);
			if (player.isHeadingBlocked(heading))
			{
				player.blockMovementToHeading(heading);
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			final int playerX = player.getX();
			final int playerY = player.getY();
			final int playerZ = player.getZ();
			final double angle = Util.convertHeadingToDegree(heading);
			final double radian = Math.toRadians(angle);
			final double course = Math.toRadians(180);
			final double frontDistance = 10 * (player.getMoveSpeed() / 100);
			final int x1 = (int) (Math.cos(Math.PI + radian + course) * frontDistance);
			final int y1 = (int) (Math.sin(Math.PI + radian + course) * frontDistance);
			final int x = _originX + x1;
			final int y = _originY + y1;
			if (!GeoEngine.getInstance().canSeeTarget(playerX, playerY, playerZ, x, y, playerZ, player.getInstanceWorld()))
			{
				player.blockMovementToHeading(heading);
				player.stopMove(player.getLocation());
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			final Location destination = GeoEngine.getInstance().getValidLocation(playerX, playerY, playerZ, x, y, playerZ, player.getInstanceWorld());
			_targetX = destination.getX();
			_targetY = destination.getY();
			_targetZ = destination.getZ();
			
			player.setCursorKeyMovement(true);
			player.setLastServerPosition(playerX, playerY, playerZ);
		}
		
		// Release existing heading block.
		player.unblockMovementToHeading();
		
		// Correcting targetZ from floor level to head level.
		// Client is giving floor level as targetZ, but that floor level doesn't match our current geodata and teleport coordinates as good as head level!
		// L2J uses floor, not head level as char coordinates. This is some sort of incompatibility fix. Validate position packets sends head level.
		_targetZ += player.getTemplate().getCollisionHeight();
		
		final AdminTeleportType teleMode = player.getTeleMode();
		switch (teleMode)
		{
			case DEMONIC:
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				player.teleToLocation(new Location(_targetX, _targetY, _targetZ));
				player.setTeleMode(AdminTeleportType.NORMAL);
				break;
			}
			case SAYUNE:
			{
				player.sendPacket(new ExFlyMove(player, SayuneType.ONE_WAY_LOC, -1, Arrays.asList(new SayuneEntry(false, -1, _targetX, _targetY, _targetZ))));
				player.setXYZ(_targetX, _targetY, _targetZ);
				Broadcast.toKnownPlayers(player, new ExFlyMoveBroadcast(player, SayuneType.ONE_WAY_LOC, -1, new Location(_targetX, _targetY, _targetZ)));
				player.setTeleMode(AdminTeleportType.NORMAL);
				break;
			}
			case CHARGE:
			{
				player.setXYZ(_targetX, _targetY, _targetZ);
				Broadcast.toSelfAndKnownPlayers(player, new MagicSkillUse(player, 30012, 10, 500, 0));
				Broadcast.toSelfAndKnownPlayers(player, new FlyToLocation(player, _targetX, _targetY, _targetZ, FlyType.CHARGE));
				Broadcast.toSelfAndKnownPlayers(player, new MagicSkillLaunched(player, 30012, 10));
				player.sendPacket(ActionFailed.STATIC_PACKET);
				break;
			}
			default:
			{
				// Can't move if character is confused.
				if (player.isControlBlocked())
				{
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				
				// Can't move if character is trying to move a huge distance.
				final double dx = _targetX - player.getX();
				final double dy = _targetY - player.getY();
				if (((dx * dx) + (dy * dy)) > 98010000) // 9900*9900
				{
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				
				// Prevent moving to same location. Geodata cell size is 16.
				if (player.isMoving() && (Util.calculateDistance(player.getXdestination(), player.getYdestination(), player.getZdestination(), _targetX, _targetY, _targetZ, true, false) < 33))
				{
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(_targetX, _targetY, _targetZ));
				break;
			}
		}
		
		// Remove queued skill upon move request.
		if (player.getQueuedSkill() != null)
		{
			player.setQueuedSkill(null, null, false, false);
		}
		
		// Mobius: Check spawn protections.
		player.onActionRequest();
	}
}
