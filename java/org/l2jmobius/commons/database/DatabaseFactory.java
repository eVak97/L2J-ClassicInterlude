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
package org.l2jmobius.commons.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.l2jmobius.Config;

/**
 * DatabaseFactory class using HikariCP for connection pooling.<br>
 * Configured for high-load environments with 2000-3000 players.<br>
 * Singleton implementation to ensure a single pool instance.
 * @author Mobius
 * @since November 10th 2018
 * @version October 16th 2024
 */
public class DatabaseFactory
{
	private static final Logger LOGGER = Logger.getLogger(DatabaseFactory.class.getName());
	
	private static HikariDataSource DATABASE_POOL;
	
	private DatabaseFactory()
	{
	}
	
	/**
	 * Initializes the HikariCP connection pool with optimized settings.<br>
	 * Ensures that the pool is initialized only once.
	 */
	public static synchronized void init()
	{
		if ((DATABASE_POOL != null) && !DATABASE_POOL.isClosed())
		{
			LOGGER.warning("Database: Connection pool is already initialized.");
			return;
		}
		
		try
		{
			final HikariConfig config = new HikariConfig();
			config.setDriverClassName(Config.DATABASE_DRIVER);
			config.setJdbcUrl(Config.DATABASE_URL);
			config.setUsername(Config.DATABASE_LOGIN);
			config.setPassword(Config.DATABASE_PASSWORD);
			
			// Pool Size Configuration.
			config.setMaximumPoolSize(determineMaxPoolSize(Config.DATABASE_MAX_CONNECTIONS)); // 100
			config.setMinimumIdle(determineMinimumIdle(Config.DATABASE_MAX_CONNECTIONS)); // e.g., 20
			
			// Timeout Settings.
			config.setConnectionTimeout(60000); // 1 minute.
			config.setIdleTimeout(300000); // 5 minutes.
			config.setMaxLifetime(600000); // 10 minutes.
			
			// Leak Detection.
			config.setLeakDetectionThreshold(60000); // 1 minute.
			
			// Pool Name for Identification.
			config.setPoolName("L2JMobiusPool");
			
			// Register MBeans for Monitoring.
			config.setRegisterMbeans(true);
			
			// Additional Optimizations.
			config.setInitializationFailTimeout(-1);
			config.setValidationTimeout(5000); // 5 seconds.
			
			// Initialize HikariDataSource.
			DATABASE_POOL = new HikariDataSource(config);
			
			LOGGER.info("Database: HikariCP pool initialized successfully.");
			
			if (Config.DATABASE_TEST_CONNECTIONS)
			{
				testDatabaseConnections();
			}
			else
			{
				testSingleConnection();
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Database: Failed to initialize HikariCP pool.", e);
		}
	}
	
	/**
	 * Determines the appropriate maximum pool size based on configuration and server capacity. Caps the pool size to prevent database overload.
	 * @param configuredMax The configured maximum pool size from Config.
	 * @return Adjusted maximum pool size.
	 */
	private static int determineMaxPoolSize(int configuredMax)
	{
		// For high-load servers, a maxPoolSize between 100-200 is reasonable.
		// Adjust based on actual database server capacity and application behavior.
		int maxPoolSize = Math.min(configuredMax, 200); // Cap at 200 to prevent DB overload.
		if (maxPoolSize < 10)
		{
			maxPoolSize = 10;
		}
		return maxPoolSize;
	}
	
	/**
	 * Determines the appropriate minimum idle connections based on configuration.
	 * @param configuredMax The configured maximum pool size from Config.
	 * @return Adjusted minimum idle connections.
	 */
	private static int determineMinimumIdle(int configuredMax)
	{
		// Set minimumIdle to 10% of maxPoolSize or a minimum of 2.
		final int calculatedMinIdle = Math.max(configuredMax / 10, 2);
		return calculatedMinIdle;
	}
	
	/**
	 * Tests the database connections by attempting to open the maximum number of connections.<br>
	 * Adjusts the pool size if necessary based on successful connections.
	 */
	private static void testDatabaseConnections()
	{
		final List<Connection> connections = new LinkedList<>();
		int successfulConnections = 0;
		
		try
		{
			LOGGER.info("Database: Testing database connections...");
			
			for (int i = 0; i < DATABASE_POOL.getMaximumPoolSize(); i++)
			{
				Connection connection = null;
				try
				{
					connection = DATABASE_POOL.getConnection();
					connections.add(connection);
					successfulConnections++;
					LOGGER.info("Database: Successfully opened connection " + connection.toString() + ".");
				}
				catch (SQLException e)
				{
					LOGGER.log(Level.SEVERE, "Database: Failed to open connection " + (i + 1) + "!", e);
					break;
				}
			}
			
			if (successfulConnections == DATABASE_POOL.getMaximumPoolSize())
			{
				LOGGER.info("Database: Initialized with a total of " + successfulConnections + " connections.");
			}
			else
			{
				LOGGER.warning("Database: Only " + successfulConnections + " out of " + DATABASE_POOL.getMaximumPoolSize() + " connections were successful.");
				adjustPoolSize(successfulConnections);
			}
		}
		finally // Close all opened connections.
		{
			for (Connection connection : connections)
			{
				if (connection != null)
				{
					try
					{
						connection.close();
					}
					catch (SQLException e)
					{
						LOGGER.log(Level.SEVERE, "Database: Error closing connection.", e);
					}
				}
			}
		}
	}
	
	/**
	 * Adjusts the pool size based on the number of successful connections.
	 * @param successfulConnections Number of connections that were successfully opened.
	 */
	private static void adjustPoolSize(int successfulConnections)
	{
		LOGGER.warning("Database: Adjusting pool size based on successful connections.");
		
		// Calculate new pool size, reducing in steps to find a stable number.
		int newConnectionCount = successfulConnections;
		
		if (successfulConnections > 100)
		{
			newConnectionCount = (successfulConnections / 100) * 100;
		}
		else if (successfulConnections > 50)
		{
			newConnectionCount = (successfulConnections / 50) * 50;
		}
		
		// Ensure a minimum pool size of 20.
		newConnectionCount = Math.max(newConnectionCount, 20);
		
		// Update pool configuration.
		try
		{
			DATABASE_POOL.setMaximumPoolSize(newConnectionCount);
			DATABASE_POOL.setMinimumIdle(determineMinimumIdle(newConnectionCount));
			LOGGER.info("Database: Reinitialized pool size to " + newConnectionCount + ".");
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Database: Failed to adjust pool size.", e);
		}
	}
	
	/**
	 * Tests a single connection to verify database connectivity.
	 */
	private static void testSingleConnection()
	{
		try (Connection connection = DATABASE_POOL.getConnection())
		{
			if (connection.isValid(5))
			{
				LOGGER.info("Database: Initialized with a valid connection.");
			}
			else
			{
				LOGGER.warning("Database: Connection is not valid.");
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.SEVERE, "Database: Problem initializing connection pool.", e);
		}
	}
	
	/**
	 * Retrieves a connection from the pool.
	 * @return A valid database connection.
	 */
	public static Connection getConnection()
	{
		try
		{
			return DATABASE_POOL.getConnection();
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.SEVERE, "Database: Could not get a connection.", e);
			throw new RuntimeException("Unable to obtain a database connection.", e);
		}
	}
	
	/**
	 * Closes the HikariCP connection pool gracefully.
	 */
	public static synchronized void close()
	{
		if ((DATABASE_POOL != null) && !DATABASE_POOL.isClosed())
		{
			try
			{
				DATABASE_POOL.close();
				LOGGER.info("Database: HikariCP pool closed successfully.");
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "Database: There was a problem closing the data source.", e);
			}
		}
	}
}
