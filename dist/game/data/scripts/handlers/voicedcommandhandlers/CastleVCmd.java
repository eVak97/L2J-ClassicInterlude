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
package handlers.voicedcommandhandlers;

import org.l2jmobius.gameserver.handler.IVoicedCommandHandler;
import org.l2jmobius.gameserver.instancemanager.CastleManager;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.instance.Door;
import org.l2jmobius.gameserver.model.siege.Castle;
import org.l2jmobius.gameserver.network.SystemMessageId;

/**
 * @author Zoey76
 */
public class CastleVCmd implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"opendoors",
		"closedoors",
		"ridewyvern"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String params)
	{
		switch (command)
		{
			case "opendoors":
			{
				if (!params.equals("castle"))
				{
					activeChar.sendMessage("Only Castle doors can be open.");
					return false;
				}
				
				if (!activeChar.isClanLeader())
				{
					activeChar.sendPacket(SystemMessageId.ONLY_THE_CLAN_LEADER_MAY_ISSUE_COMMANDS);
					return false;
				}
				
				final Door door = activeChar.getTarget().asDoor();
				if (door == null)
				{
					activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
					return false;
				}
				
				final Castle castle = CastleManager.getInstance().getCastleById(activeChar.getClan().getCastleId());
				if (castle == null)
				{
					activeChar.sendMessage("Your clan does not own a castle.");
					return false;
				}
				
				if (castle.getSiege().isInProgress())
				{
					activeChar.sendPacket(SystemMessageId.THE_CASTLE_GATES_CANNOT_BE_OPENED_AND_CLOSED_DURING_A_SIEGE);
					return false;
				}
				
				if (castle.checkIfInZone(door.getX(), door.getY(), door.getZ()))
				{
					activeChar.sendPacket(SystemMessageId.THE_GATE_IS_BEING_OPENED);
					door.openMe();
				}
				break;
			}
			case "closedoors":
			{
				if (!params.equals("castle"))
				{
					activeChar.sendMessage("Only Castle doors can be closed.");
					return false;
				}
				if (!activeChar.isClanLeader())
				{
					activeChar.sendPacket(SystemMessageId.ONLY_THE_CLAN_LEADER_MAY_ISSUE_COMMANDS);
					return false;
				}
				final Door door2 = activeChar.getTarget().asDoor();
				if (door2 == null)
				{
					activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
					return false;
				}
				final Castle castle2 = CastleManager.getInstance().getCastleById(activeChar.getClan().getCastleId());
				if (castle2 == null)
				{
					activeChar.sendMessage("Your clan does not own a castle.");
					return false;
				}
				
				if (castle2.getSiege().isInProgress())
				{
					activeChar.sendPacket(SystemMessageId.THE_CASTLE_GATES_CANNOT_BE_OPENED_AND_CLOSED_DURING_A_SIEGE);
					return false;
				}
				
				if (castle2.checkIfInZone(door2.getX(), door2.getY(), door2.getZ()))
				{
					activeChar.sendMessage("The gate is being closed.");
					door2.closeMe();
				}
				break;
			}
			case "ridewyvern":
			{
				if (activeChar.isClanLeader() && (activeChar.getClan().getCastleId() > 0))
				{
					activeChar.mount(12621, 0, true);
				}
				break;
			}
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
