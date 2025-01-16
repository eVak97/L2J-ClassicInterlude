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
package org.l2jmobius.gameserver.geoengine.pathfinding.utils;

import org.l2jmobius.gameserver.geoengine.pathfinding.geonodes.GeoNode;

/**
 * @author -Nemesiss-, Mobius
 */
public class BinaryNodeHeap
{
	private final GeoNode[] _list;
	private int _size;
	
	public BinaryNodeHeap(int size)
	{
		_list = new GeoNode[size + 1];
		_size = 0;
	}
	
	public void add(GeoNode n)
	{
		_size++;
		int pos = _size;
		while ((pos > 1) && (n.getCost() < _list[pos / 2].getCost()))
		{
			_list[pos] = _list[pos / 2];
			pos /= 2;
		}
		_list[pos] = n;
	}
	
	public GeoNode removeFirst()
	{
		if (_size == 0)
		{
			return null;
		}
		
		final GeoNode first = _list[1]; // The node to return.
		final GeoNode last = _list[_size]; // The last node in the heap.
		_list[_size--] = null; // Remove the last node and decrease the size.
		
		int pos = 1;
		int child;
		
		// "Bubbling down" the last node to its correct position.
		while ((pos * 2) <= _size)
		{
			child = pos * 2;
			if ((child != _size) && (_list[child + 1].getCost() < _list[child].getCost()))
			{
				child++;
			}
			
			if (last.getCost() <= _list[child].getCost())
			{
				break;
			}
			
			_list[pos] = _list[child];
			pos = child;
		}
		
		_list[pos] = last;
		return first;
	}
	
	public boolean contains(GeoNode n)
	{
		for (int i = 1; i <= _size; i++)
		{
			if (_list[i].equals(n))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean isEmpty()
	{
		return _size == 0;
	}
}
