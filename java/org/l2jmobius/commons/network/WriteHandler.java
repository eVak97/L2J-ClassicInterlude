/*
 * Copyright © 2019-2021 Async-mmocore
 *
 * This file is part of the Async-mmocore project.
 *
 * Async-mmocore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Async-mmocore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.commons.network;

import java.nio.channels.CompletionHandler;

/**
 * Handles the completion of write operations for network clients.<br>
 * This class implements {@link CompletionHandler} to process the results of data writing to the client, ensuring proper handling of the write operation's conclusion.
 * @param <T> The type of Client associated with this write handler.
 * @author JoeAlisson
 */
public class WriteHandler<T extends Client<Connection<T>>> implements CompletionHandler<Long, T>
{
	@Override
	public void completed(Long result, T client)
	{
		// If client is null, there's nothing to handle, possibly due to disconnection.
		if (client == null)
		{
			return;
		}
		
		// Negative result indicates failure to send data, possibly due to client disconnection.
		if (result < 0)
		{
			if (client.isConnected())
			{
				client.disconnect();
			}
			return;
		}
		
		// If there is still data remaining to send, resume sending with the remaining data.
		if ((result > 0) && (result < client.getDataSentSize()))
		{
			client.resumeSend(result);
		}
		else // All data sent, finish the writing process.
		{
			client.finishWriting();
		}
	}
	
	@Override
	public void failed(Throwable e, T client)
	{
		// Handle failures, disconnecting the client if an error occurs.
		client.disconnect();
	}
}
