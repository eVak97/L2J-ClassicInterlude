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
package org.l2jmobius.commons.threads;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.l2jmobius.Config;
import org.l2jmobius.commons.util.CommonUtil;

/**
 * This class provides methods to schedule tasks with a delay or at a fixed rate, as well as immediate execution.
 * @author Mobius
 */
public class ThreadPool
{
	private static final Logger LOGGER = Logger.getLogger(ThreadPool.class.getName());
	
	private static final ScheduledThreadPoolExecutor SCHEDULED_POOL = new ScheduledThreadPoolExecutor(Config.SCHEDULED_THREAD_POOL_SIZE, new ThreadProvider("L2jMobius ScheduledThread"), new ThreadPoolExecutor.CallerRunsPolicy());
	private static final ThreadPoolExecutor INSTANT_POOL = new ThreadPoolExecutor(Config.INSTANT_THREAD_POOL_SIZE, Integer.MAX_VALUE, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(), new ThreadProvider("L2jMobius Thread"));
	private static final long MAX_DELAY = 3155695200000L; // One hundred years.
	private static final long MIN_DELAY = 0L;
	
	private static ScheduledThreadPoolExecutor HIGH_PRIORITY_SCHEDULED_POOL;
	
	public static void init()
	{
		LOGGER.info("ThreadPool: Initialized");
		
		// Configure High Priority ScheduledThreadPoolExecutor.
		if (Config.HIGH_PRIORITY_SCHEDULED_THREAD_POOL_SIZE > 0)
		{
			HIGH_PRIORITY_SCHEDULED_POOL = new ScheduledThreadPoolExecutor(Config.HIGH_PRIORITY_SCHEDULED_THREAD_POOL_SIZE, new ThreadProvider("L2jMobius High Priority ScheduledThread", ThreadPriority.PRIORITY_8), new ThreadPoolExecutor.CallerRunsPolicy());
			LOGGER.info("...scheduled pool executor with " + Config.HIGH_PRIORITY_SCHEDULED_THREAD_POOL_SIZE + " high priority threads.");
		}
		
		// Configure ScheduledThreadPoolExecutor.
		SCHEDULED_POOL.setRejectedExecutionHandler(new RejectedExecutionHandlerImpl());
		SCHEDULED_POOL.setRemoveOnCancelPolicy(true);
		SCHEDULED_POOL.prestartAllCoreThreads();
		
		// Configure ThreadPoolExecutor.
		INSTANT_POOL.setRejectedExecutionHandler(new RejectedExecutionHandlerImpl());
		INSTANT_POOL.prestartAllCoreThreads();
		
		// Schedule the purge task.
		scheduleAtFixedRate(ThreadPool::purge, 60000, 60000);
		
		// Log information.
		LOGGER.info("...scheduled pool executor with " + Config.SCHEDULED_THREAD_POOL_SIZE + " total threads.");
		LOGGER.info("...instant pool executor with " + Config.INSTANT_THREAD_POOL_SIZE + " total threads.");
	}
	
	public static void purge()
	{
		SCHEDULED_POOL.purge();
		INSTANT_POOL.purge();
	}
	
	/**
	 * Creates and executes a one-shot action that becomes enabled after the given delay.
	 * @param runnable : the task to execute.
	 * @param delay : the time from now to delay execution.
	 * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion.
	 */
	public static ScheduledFuture<?> schedule(Runnable runnable, long delay)
	{
		try
		{
			return SCHEDULED_POOL.schedule(new RunnableWrapper(runnable), validate(delay), TimeUnit.MILLISECONDS);
		}
		catch (Exception e)
		{
			LOGGER.warning(runnable.getClass().getSimpleName() + System.lineSeparator() + e.getMessage() + System.lineSeparator() + e.getStackTrace());
			return null;
		}
	}
	
	/**
	 * Creates and executes a periodic action that becomes enabled first after the given initial delay.
	 * @param runnable : the task to execute.
	 * @param initialDelay : the time to delay first execution.
	 * @param period : the period between successive executions.
	 * @return a ScheduledFuture representing pending completion of the task and whose get() method will throw an exception upon cancellation.
	 */
	public static ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initialDelay, long period)
	{
		try
		{
			return SCHEDULED_POOL.scheduleAtFixedRate(new RunnableWrapper(runnable), validate(initialDelay), validate(period), TimeUnit.MILLISECONDS);
		}
		catch (Exception e)
		{
			LOGGER.warning(runnable.getClass().getSimpleName() + System.lineSeparator() + e.getMessage() + System.lineSeparator() + e.getStackTrace());
			return null;
		}
	}
	
	/**
	 * Creates and executes a periodic action that becomes enabled first after the given initial delay, using a high priority scheduled thread pool. This method is similar to scheduleAtFixedRate but is designed for tasks requiring more immediate or high-priority execution.
	 * @param runnable : the task to execute.
	 * @param initialDelay : the time to delay first execution.
	 * @param period : the period between successive executions.
	 * @return a ScheduledFuture representing pending completion of the task and whose get() method will throw an exception upon cancellation.
	 */
	public static ScheduledFuture<?> schedulePriorityTaskAtFixedRate(Runnable runnable, long initialDelay, long period)
	{
		try
		{
			return HIGH_PRIORITY_SCHEDULED_POOL.scheduleAtFixedRate(new RunnableWrapper(runnable), validate(initialDelay), validate(period), TimeUnit.MILLISECONDS);
		}
		catch (Exception e)
		{
			LOGGER.warning(runnable.getClass().getSimpleName() + System.lineSeparator() + e.getMessage() + System.lineSeparator() + e.getStackTrace());
			return null;
		}
	}
	
	/**
	 * Executes the given task sometime in the future.
	 * @param runnable : the task to execute.
	 */
	public static void execute(Runnable runnable)
	{
		try
		{
			INSTANT_POOL.execute(new RunnableWrapper(runnable));
		}
		catch (Exception e)
		{
			LOGGER.warning(runnable.getClass().getSimpleName() + System.lineSeparator() + e.getMessage() + System.lineSeparator() + e.getStackTrace());
		}
	}
	
	/**
	 * @param delay : the delay to validate.
	 * @return a valid value, from MIN_DELAY to MAX_DELAY.
	 */
	private static long validate(long delay)
	{
		if (delay < MIN_DELAY)
		{
			LOGGER.warning("ThreadPool found delay " + delay + "!");
			LOGGER.warning(CommonUtil.getStackTrace(new Exception()));
			return MIN_DELAY;
		}
		if (delay > MAX_DELAY)
		{
			LOGGER.warning("ThreadPool found delay " + delay + "!");
			LOGGER.warning(CommonUtil.getStackTrace(new Exception()));
			return MAX_DELAY;
		}
		return delay;
	}
	
	/**
	 * Shutdown thread pooling system correctly.
	 */
	public static void shutdown()
	{
		try
		{
			LOGGER.info("ThreadPool: Shutting down.");
			SCHEDULED_POOL.shutdownNow();
			INSTANT_POOL.shutdownNow();
		}
		catch (Throwable t)
		{
			LOGGER.info("ThreadPool: Problem while shutting down. " + t.getMessage());
		}
	}
	
	/**
	 * Handles tasks rejected by ThreadPoolExecutor, either running them in a new thread<br>
	 * or in the current thread depending on the thread's priority.
	 */
	private static class RejectedExecutionHandlerImpl implements RejectedExecutionHandler
	{
		private static final Logger LOGGER = Logger.getLogger(RejectedExecutionHandlerImpl.class.getName());
		
		@Override
		public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor)
		{
			if (executor.isShutdown())
			{
				return;
			}
			
			LOGGER.warning(runnable.getClass().getSimpleName() + System.lineSeparator() + runnable + " from " + executor + " " + new RejectedExecutionException());
			
			if (Thread.currentThread().getPriority() > Thread.NORM_PRIORITY)
			{
				new Thread(runnable).start();
			}
			else
			{
				runnable.run();
			}
		}
	}
	
	/**
	 * Wraps a Runnable to handle any uncaught exceptions during its execution<br>
	 * by passing them to the thread's uncaught exception handler.
	 */
	private static class RunnableWrapper implements Runnable
	{
		private final Runnable _runnable;
		
		public RunnableWrapper(Runnable runnable)
		{
			_runnable = runnable;
		}
		
		@Override
		public void run()
		{
			try
			{
				_runnable.run();
			}
			catch (Throwable e)
			{
				final Thread t = Thread.currentThread();
				final UncaughtExceptionHandler h = t.getUncaughtExceptionHandler();
				if (h != null)
				{
					h.uncaughtException(t, e);
				}
			}
		}
	}
}
