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
package org.l2jmobius.gameserver.data.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.gameserver.model.matching.MatchingRoomHistory;

/**
 * @author Mobius
 */
public class PartyMatchingHistoryTable
{
	private static final Logger LOGGER = Logger.getLogger(PartyMatchingHistoryTable.class.getName());
	
	private static final String RESTORE_PARTY_HISTORY = "SELECT title, leader FROM party_matching_history ORDER BY id DESC LIMIT 100"; // Maximum size according to retail is 100.
	private static final String DELETE_PARTY_HISTORY = "DELETE FROM party_matching_history";
	private static final String INSERT_PARTY_HISTORY = "INSERT INTO party_matching_history (title,leader) values (?,?)";
	
	private static final LinkedList<MatchingRoomHistory> HISTORY = new LinkedList<>();
	
	protected PartyMatchingHistoryTable()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(RESTORE_PARTY_HISTORY);
			ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				addRoomHistory(rset.getString("title"), rset.getString("leader"));
			}
		}
		catch (Exception e)
		{
			LOGGER.warning(getClass().getSimpleName() + ": Could not load data: " + e.getMessage());
		}
		
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + HISTORY.size() + " party matching history data.");
	}
	
	public void addRoomHistory(String title, String leaderName)
	{
		synchronized (HISTORY)
		{
			HISTORY.add(new MatchingRoomHistory(title, leaderName));
			if (HISTORY.size() > 100) // Maximum size according to retail is 100.
			{
				HISTORY.removeFirst();
			}
		}
	}
	
	public Collection<MatchingRoomHistory> getHistory()
	{
		return HISTORY;
	}
	
	public void storeMe()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps1 = con.prepareStatement(DELETE_PARTY_HISTORY);
			PreparedStatement ps2 = con.prepareStatement(INSERT_PARTY_HISTORY))
		{
			ps1.execute();
			
			for (MatchingRoomHistory history : HISTORY)
			{
				ps2.setString(1, history.getTitle());
				ps2.setString(2, history.getLeader());
				ps2.addBatch();
			}
			ps2.executeBatch();
		}
		catch (Exception e)
		{
			LOGGER.warning(getClass().getSimpleName() + ": PartyMatchingHistoryTable: Problem inserting room history!");
		}
	}
	
	public static PartyMatchingHistoryTable getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final PartyMatchingHistoryTable INSTANCE = new PartyMatchingHistoryTable();
	}
}
