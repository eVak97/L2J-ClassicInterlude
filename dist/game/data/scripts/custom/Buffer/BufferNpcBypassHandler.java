/*
 * This file is part of YANModPack: https://github.com/HorridoJoho/YANModPack
 * Copyright (C) 2015  Christian Buck
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package custom.Buffer;

import java.util.logging.Level;

import org.l2jmobius.gameserver.enums.InstanceType;
import org.l2jmobius.gameserver.handler.IBypassHandler;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;

/**
 * @author HorridoJoho
 */
public class BufferNpcBypassHandler implements IBypassHandler
{
	private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(BufferNpcBypassHandler.class.getName());
	
	private static final class SingletonHolder
	{
		protected static final BufferNpcBypassHandler INSTANCE = new BufferNpcBypassHandler();
	}
	
	public static final String BYPASS = "BufferNpc";
	private static final String[] _BYPASS_LIST = new String[]
	{
		BYPASS
	};
	
	static BufferNpcBypassHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	BufferNpcBypassHandler()
	{
	}
	
	@Override
	public boolean useBypass(String command, Player activeChar, Creature target)
	{
		LOGGER.log(Level.SEVERE, "Error message: " + command, (Object[]) null);
		// If html bypass validation fails
		// Other values assumed as a validation pass for now
		if (activeChar.validateHtmlAction(command) == -1)
		{
			return false;
		}
		
		WorldObject playerTarget = activeChar.getTarget();
		if ((playerTarget == null) || !playerTarget.isInstanceTypes(InstanceType.Npc))
		{
			return false;
		}
		
		Buffer.getInstance().executeCommand(activeChar, (Npc) playerTarget, command.substring(BYPASS.length()).trim());
		
		LOGGER.log(Level.SEVERE, "Error message: " + command, (Object[]) null);
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return _BYPASS_LIST;
	}
}
