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

import org.l2jmobius.gameserver.handler.IVoicedCommandHandler;
import org.l2jmobius.gameserver.model.actor.Player;

/**
 * @author HorridoJoho
 */
public final class BufferVoicedCommandHandler implements IVoicedCommandHandler
{
	private static final class SingletonHolder
	{
		protected static final BufferVoicedCommandHandler INSTANCE = new BufferVoicedCommandHandler();
	}
	
	public static final String VOICED_COMMAND = "buffer";
	private static final String[] _VOICED_COMMAND_LIST =
	{
		VOICED_COMMAND
	};
	
	static BufferVoicedCommandHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	BufferVoicedCommandHandler()
	{
	}
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String params)
	{
		Buffer.getInstance().executeCommand(activeChar, null, params);
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _VOICED_COMMAND_LIST;
	}
	
}
