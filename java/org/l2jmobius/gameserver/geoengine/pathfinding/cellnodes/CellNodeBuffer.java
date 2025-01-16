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
package org.l2jmobius.gameserver.geoengine.pathfinding.cellnodes;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.l2jmobius.Config;

/**
 * @author DS, Diamond, Mobius
 */
public class CellNodeBuffer
{
	private static final int MAX_ITERATIONS = 3500;
	
	private final ReentrantLock _lock = new ReentrantLock();
	private final int _mapSize;
	private final CellNode[][] _buffer;
	
	private int _baseX = 0;
	private int _baseY = 0;
	
	private int _targetX = 0;
	private int _targetY = 0;
	private int _targetZ = 0;
	
	private long _timeStamp = 0;
	private long _lastElapsedTime = 0;
	
	private CellNode _current = null;
	
	public CellNodeBuffer(int size)
	{
		_mapSize = size;
		_buffer = new CellNode[_mapSize][_mapSize];
	}
	
	public final boolean lock()
	{
		try
		{
			return _lock.tryLock(100, TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
			return false;
		}
	}
	
	public final CellNode findPath(int x, int y, int z, int tx, int ty, int tz)
	{
		_timeStamp = System.currentTimeMillis();
		_baseX = x + ((tx - x - _mapSize) / 2); // Middle of the line (x,y) - (tx,ty).
		_baseY = y + ((ty - y - _mapSize) / 2); // Will be in the center of the buffer.
		_targetX = tx;
		_targetY = ty;
		_targetZ = tz;
		_current = getNode(x, y, z);
		_current.setCost(getCost(x, y, z, Config.HIGH_WEIGHT));
		
		for (int count = 0; count < MAX_ITERATIONS; count++)
		{
			if ((_current.getLoc().getNodeX() == _targetX) && (_current.getLoc().getNodeY() == _targetY) && (Math.abs(_current.getLoc().getZ() - _targetZ) < 64))
			{
				return _current; // Found.
			}
			
			getNeighbors();
			final CellNode nextCellNode = _current.getNext();
			if (nextCellNode == null)
			{
				return null; // No more ways.
			}
			
			_current = nextCellNode;
			
			if (Config.AVOID_ABSTRUCTED_PATH_NODES)
			{
				while ((_current != null) && !_current.getLoc().canGoAll())
				{
					getNeighbors();
					final CellNode nextCell = _current.getNext();
					if (nextCell == null)
					{
						return null; // No more ways.
					}
					
					_current = nextCell;
				}
			}
		}
		return null;
	}
	
	public void free()
	{
		_current = null;
		
		for (int i = 0; i < _mapSize; i++)
		{
			for (int j = 0; j < _mapSize; j++)
			{
				final CellNode node = _buffer[i][j];
				if ((node != null) && node.isInUse())
				{
					node.free();
				}
			}
		}
		
		_lock.unlock();
		_lastElapsedTime = System.currentTimeMillis() - _timeStamp;
	}
	
	public final long getElapsedTime()
	{
		return _lastElapsedTime;
	}
	
	public final List<CellNode> debugPath()
	{
		final List<CellNode> result = new LinkedList<>();
		for (CellNode n = _current; n.getParent() != null; n = (CellNode) n.getParent())
		{
			result.add(n);
			n.setCost(-n.getCost());
		}
		
		for (int i = 0; i < _mapSize; i++)
		{
			for (int j = 0; j < _mapSize; j++)
			{
				final CellNode n = _buffer[i][j];
				if ((n == null) || !n.isInUse() || (n.getCost() <= 0))
				{
					continue;
				}
				
				result.add(n);
			}
		}
		return result;
	}
	
	private void getNeighbors()
	{
		if (_current.getLoc().canGoNone())
		{
			return;
		}
		
		final int x = _current.getLoc().getNodeX();
		final int y = _current.getLoc().getNodeY();
		final int z = _current.getLoc().getZ();
		
		CellNode nodeE = null;
		CellNode nodeS = null;
		CellNode nodeW = null;
		CellNode nodeN = null;
		
		// East
		if (_current.getLoc().canGoEast())
		{
			nodeE = addNode(x + 1, y, z, false);
		}
		
		// South
		if (_current.getLoc().canGoSouth())
		{
			nodeS = addNode(x, y + 1, z, false);
		}
		
		// West
		if (_current.getLoc().canGoWest())
		{
			nodeW = addNode(x - 1, y, z, false);
		}
		
		// North
		if (_current.getLoc().canGoNorth())
		{
			nodeN = addNode(x, y - 1, z, false);
		}
		
		if (!Config.ADVANCED_DIAGONAL_STRATEGY)
		{
			return;
		}
		
		// SouthEast
		if ((nodeE != null) && (nodeS != null) && nodeE.getLoc().canGoSouth() && nodeS.getLoc().canGoEast())
		{
			addNode(x + 1, y + 1, z, true);
		}
		
		// SouthWest
		if ((nodeS != null) && (nodeW != null) && nodeW.getLoc().canGoSouth() && nodeS.getLoc().canGoWest())
		{
			addNode(x - 1, y + 1, z, true);
		}
		
		// NorthEast
		if ((nodeN != null) && (nodeE != null) && nodeE.getLoc().canGoNorth() && nodeN.getLoc().canGoEast())
		{
			addNode(x + 1, y - 1, z, true);
		}
		
		// NorthWest
		if ((nodeN != null) && (nodeW != null) && nodeW.getLoc().canGoNorth() && nodeN.getLoc().canGoWest())
		{
			addNode(x - 1, y - 1, z, true);
		}
	}
	
	private final CellNode getNode(int x, int y, int z)
	{
		final int aX = x - _baseX;
		if ((aX < 0) || (aX >= _mapSize))
		{
			return null;
		}
		
		final int aY = y - _baseY;
		if ((aY < 0) || (aY >= _mapSize))
		{
			return null;
		}
		
		CellNode result = _buffer[aX][aY];
		if (result == null)
		{
			result = new CellNode(new NodeLoc(x, y, z));
			_buffer[aX][aY] = result;
		}
		else if (!result.isInUse())
		{
			result.setInUse();
			// Re-init node if needed.
			if (result.getLoc() != null)
			{
				result.getLoc().set(x, y, z);
			}
			else
			{
				result.setLoc(new NodeLoc(x, y, z));
			}
		}
		
		return result;
	}
	
	private final CellNode addNode(int x, int y, int z, boolean diagonal)
	{
		final CellNode newNode = getNode(x, y, z);
		if (newNode == null)
		{
			return null;
		}
		if (newNode.getCost() >= 0)
		{
			return newNode;
		}
		
		final int geoZ = newNode.getLoc().getZ();
		
		final int stepZ = Math.abs(geoZ - _current.getLoc().getZ());
		float weight = diagonal ? Config.DIAGONAL_WEIGHT : Config.LOW_WEIGHT;
		
		if (!newNode.getLoc().canGoAll() || (stepZ > 16))
		{
			weight = Config.HIGH_WEIGHT;
		}
		else if (isHighWeight(x + 1, y, geoZ))
		{
			weight = Config.MEDIUM_WEIGHT;
		}
		else if (isHighWeight(x - 1, y, geoZ))
		{
			weight = Config.MEDIUM_WEIGHT;
		}
		else if (isHighWeight(x, y + 1, geoZ))
		{
			weight = Config.MEDIUM_WEIGHT;
		}
		else if (isHighWeight(x, y - 1, geoZ))
		{
			weight = Config.MEDIUM_WEIGHT;
		}
		
		newNode.setParent(_current);
		newNode.setCost(getCost(x, y, geoZ, weight));
		
		CellNode node = _current;
		int count = 0;
		while ((node.getNext() != null) && (count < (MAX_ITERATIONS * 4)))
		{
			count++;
			if (node.getNext().getCost() > newNode.getCost())
			{
				// Insert node into a chain.
				newNode.setNext(node.getNext());
				break;
			}
			node = node.getNext();
		}
		if (count == (MAX_ITERATIONS * 4))
		{
			System.err.println("Pathfinding: too long loop detected, cost:" + newNode.getCost());
		}
		
		node.setNext(newNode); // Add last.
		
		return newNode;
	}
	
	private final boolean isHighWeight(int x, int y, int z)
	{
		final CellNode result = getNode(x, y, z);
		return (result == null) || !result.getLoc().canGoAll() || (Math.abs(result.getLoc().getZ() - z) > 16);
	}
	
	private final double getCost(int x, int y, int z, float weight)
	{
		final int dX = x - _targetX;
		final int dY = y - _targetY;
		final int dZ = z - _targetZ;
		// Math.abs(dx) + Math.abs(dy) + Math.abs(dz) / 16
		double result = Math.sqrt((dX * dX) + (dY * dY) + ((dZ * dZ) / 256.0));
		if (result > weight)
		{
			result += weight;
		}
		
		if (result > Float.MAX_VALUE)
		{
			result = Float.MAX_VALUE;
		}
		
		return result;
	}
}