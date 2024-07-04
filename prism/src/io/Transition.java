//==============================================================================
//
//	Copyright (c) 2024-
//	Authors:
//	* Dave Parker <david.parker@cs.ox.ac.uk> (University of Oxford)
//
//------------------------------------------------------------------------------
//
//	This file is part of PRISM.
//
//	PRISM is free software; you can redistribute it and/or modify
//	it under the terms of the GNU General Public License as published by
//	the Free Software Foundation; either version 2 of the License, or
//	(at your option) any later version.
//
//	PRISM is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//
//	You should have received a copy of the GNU General Public License
//	along with PRISM; if not, write to the Free Software Foundation,
//	Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//
//==============================================================================

package io;

import java.util.Objects;

/**
 * Storage of info about a model transition, for the purposes of import/export.
 */
class Transition<Value> implements Comparable<Transition<Value>>
{
	protected int target;
	protected Value probability;
	protected Object action;

	public Transition(int target, Value probability, Object action)
	{
		this.target = target;
		this.probability = probability;
		this.action = action;
	}

	@Override
	public int compareTo(Transition<Value> o)
	{
		int compTarget = Integer.compare(target, o.target);
		if (compTarget != 0) {
			return compTarget;
		}
		return 0;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Transition<?> that = (Transition<?>) o;
		return target == that.target && Objects.equals(probability, that.probability) && Objects.equals(action, that.action);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(target, probability, action);
	}

	@Override
	public String toString()
	{
		String ret = target + ", " + probability;
		if (action != null) {
			ret += ", " + action;
		}
		return ret;
	}
}
