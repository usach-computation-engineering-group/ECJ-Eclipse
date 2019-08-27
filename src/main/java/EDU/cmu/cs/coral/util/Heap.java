package EDU.cmu.cs.coral.util;

// uncomment next line if you are using JDK 1.1 
//import com.sun.java.util.collections.*;

// uncomment next line if you are using JDK 1.2 or later
import java.util.*;

/**
 * This is a heap implementation of a Priority Queue.
 * <P>
 * Copyright (c)2000 William Uther
 *
 * @author William Uther (will@cs.cmu.edu)
 * @version $Revision: 1.3 $
 */

public final class Heap extends AbstractCollection implements PriorityQueue {

final static class location {
	int loc;
	
	location(int loc) {
		this.loc = loc;
	}
	
	int getParentLoc() {
		return getParentLoc(loc);
	}

	static int getParentLoc(int loc) {
		return (loc-1)/2;
	}

	int getLeftChildLoc() {
		return getLeftChildLoc(loc);
	}

	static int getLeftChildLoc(int loc) {
		return loc*2 + 1;
	}
	
	public String toString() {
		return "Loc: " + Integer.toString(loc);
	}
}

protected static final boolean checking = true;

protected Object[] elements;
protected int elementCount;

protected Map EntryMap;

protected Comparator comparator;

protected int changeCount;

public Heap(Comparator comparator) {
	this(comparator, 16);
}

public Heap(Comparator comparator, int initialCapacity) {
	this(comparator, initialCapacity, true);
}

public Heap(Comparator comparator, int initialCapacity, boolean useFastLookup) {
	elements = new Object[initialCapacity];
	elementCount = 0;
	this.comparator = comparator;
	changeCount = 0;
	if (useFastLookup) {
		EntryMap = new HashMap(initialCapacity);
	} else {
		EntryMap = null;
	}
}

public int size() {
	return elementCount;
}

public boolean isEmpty() {
	return (elementCount == 0);
}

// Note: This is slightly different to vector in that if capacity is already enough nothing happens
public void ensureCapacity(int minCapacity) {
	if (elements.length < minCapacity+1) {
		int newCapacity = 2*elements.length;
		
		if (newCapacity < minCapacity) {	// if we're still too small use minCapacity
			newCapacity = minCapacity+1;
		}
	
		Object[] newElements = new Object[newCapacity];
		System.arraycopy(elements, 0, newElements, 0, elementCount);

		elements = newElements;
	}
}

public void trimToSize() {
	if (elements.length > size()+2) {
		int newCapacity = size()+2;
		Object[] newElements = new Object[newCapacity];

		System.arraycopy(elements, 0, newElements, 0, elementCount);

		elements = newElements;
	}
}

public Comparator comparator() {
	return comparator;
}

protected boolean eq(Object a, Object b) {
	if ((a == null) && (b == null))
		return true;
	if (a == null)
		return false;
	if (b == null)
		return false;
	return a.equals(b);
}

protected location getLocation(Object ob) {
	return getLocation(ob, -1);
}

protected location getLocation(int loc) {
	return getLocation(elements[loc], loc);
}

protected location getLocation(Object ob, int hint) {
	if (EntryMap != null) {
		return (location)EntryMap.get(ob);
	}
	if ((hint > 0) && (hint < elementCount) && (eq(elements[hint], ob)))
		return new location(hint);
	for (int i=0; i<elementCount; i++) {
		if (eq(elements[i],ob))
			return new location(i);
	}
	return null;
}

public boolean contains(Object elem) {
	if (EntryMap != null) {
		return EntryMap.containsKey(elem);
	}
	for (int i=0; i<elementCount; i++) {
		if (eq(elements[i],elem))
			return true;
	}
	return false;
}

public Iterator iterator() {
	return new Iterator() {
		int location = 0;
		int changeCount = Heap.this.changeCount;
		
		public boolean hasNext() {
			if (Heap.this.changeCount != changeCount) {
				throw new ConcurrentModificationException();
			}
			return location < elementCount;
		}
		
		public Object next() {
			if (!hasNext())
				throw new NoSuchElementException();
			return elements[location];
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
	};
}

public boolean add(Object o) {
	if (contains(o))	// don't add something twice
		return false;
	changeCount++;
	ensureCapacity(elementCount+1);
	location l = new location(elementCount);
	if (EntryMap != null) {
		EntryMap.put(o, l);
	}
	elements[elementCount] = o;
	elementCount++;
	upHeap(l);
	return true;
}

public boolean remove(Object o) {
	location l = getLocation(o);
	if (l == null)
		return false;
	changeCount++;
	if (EntryMap != null) {
		EntryMap.remove(o);
	}
	if (elementCount != 1) {
		Object lastOb = elements[elementCount-1];
		location ol = getLocation(lastOb, elementCount-1);
		elements[l.loc] = lastOb;
		ol.loc = l.loc;
		elementCount--;
		largeDownHeap(ol);
	} else {
		elementCount--;
	}
	elements[elementCount] = null;
	
	if (checking)
		Check();
	
	return true;
}

public void clear() {
	if (elementCount == 0)
		return;
	changeCount++;
	for (int i=0; i<elementCount; i++) {
		elements[i] = null;
	}
	if (EntryMap != null) {
		EntryMap.clear();
	}
	elementCount = 0;
}

public Object peekMin() {
	if (elementCount > 0) {
		return elements[0];
	} else
		return null;
}

public Object removeMin() {
	if (elementCount > 0) {
		Object result = elements[0];
		remove(result);
		return result;
	} else
		return null;
}

public boolean alteredKey(Object o) {
	location l = getLocation(o);
	if (l == null)
		return false;
	return alteredKey(l);
}

protected boolean alteredKey(location l) {
	changeCount++;
	if (l.loc == 0) {
		smallDownHeap(l);
	} else {
		int parent = l.getParentLoc();
		int diff = comparator.compare(elements[parent], elements[l.loc]);
		if (diff > 0) {
			upHeap(l);
		} else if (diff < 0) {
			smallDownHeap(l);
		}
	}
	return true;
}

protected void upHeap(location hole) {
	if (elementCount > 1) {
		Object origElement = elements[hole.loc];	// store this elt away - create the hole
		int parentLoc = hole.getParentLoc();

		while ((hole.loc > 0) && (comparator.compare(elements[parentLoc], origElement) > 0)) {

			// System.out.println("swapping: " + parentLoc + " and " + hole.loc);

			elements[hole.loc] = elements[parentLoc];	// move the item
			
			location parent = getLocation(elements[parentLoc], parentLoc);
			parent.loc = hole.loc;
			hole.loc = parentLoc;

			parentLoc = hole.getParentLoc();
		}
/*
		System.out.println("hole at: " + hole.loc + " parent: " + parentLoc);
		System.out.println("comparison: " + comparator.compare(elements[parentLoc], origElement));
*/
		elements[hole.loc] = origElement;	// put the elt back in the hole
	}

	if (checking)
		Check();
	
	return;
}

/**
 * This method moves an element down the heap to where it fits.  It does this by moving it
 * down to the bottom of the heap then calling upHeap.  This is efficient for removals where
 * the thing is being moved down the heap came from the bottom of the heap.  For small increases
 * of key this will be inefficient.
 */

protected void largeDownHeap(location hole) {
	if (elementCount <= 1)
		return;

	Object origElement = elements[hole.loc];	// store this elt away - create the hole
	int heapBottomParentLoc = location.getParentLoc(elementCount-1);

	// move hole down till it's at the bottom, or parent of the last element
	// this way we don't need to check if the right child exists all the way down
	while (hole.loc < heapBottomParentLoc) {
		int childLoc = hole.getLeftChildLoc();
		if (comparator.compare(elements[childLoc], elements[childLoc+1]) > 0) {
			childLoc++;
		}
		
		elements[hole.loc] = elements[childLoc];
		
		location child = getLocation(elements[childLoc], childLoc);
		child.loc = hole.loc;
		hole.loc = childLoc;
	}
	
	// handle case of it being parent of last element seperately
	if (hole.loc == heapBottomParentLoc) {
		int childLoc = hole.getLeftChildLoc();
		if ((childLoc+1 < elementCount) &&
			(comparator.compare(elements[childLoc], elements[childLoc+1]) > 0)) {
			childLoc++;
		}
		
		elements[hole.loc] = elements[childLoc];
		
		location child = getLocation(elements[childLoc], childLoc);
		child.loc = hole.loc;
		hole.loc = childLoc;
	}
	
	// put the element back into the hole
	elements[hole.loc] = origElement;
	
	upHeap(hole);

	return;
}

/**
 * This method moves an element down the heap to where it fits.
 * It does this by bubbling the element down the heap till it is in the correct spot.
 */

protected void smallDownHeap(location hole) {
	if (elementCount <= 1)
		return;

	Object origElement = elements[hole.loc];	// store this elt away - create the hole
	int heapBottomParentLoc = location.getParentLoc(elementCount-1);

	// move hole down till it's at the bottom, or parent of the last element
	// this way we don't need to check if the right child exists all the way down
	while (hole.loc < heapBottomParentLoc) {
		int childLoc = hole.getLeftChildLoc();
		if (comparator.compare(elements[childLoc], elements[childLoc+1]) > 0) {
			childLoc++;
		}
		
		if (comparator.compare(origElement, elements[childLoc]) <= 0)
			break;

		elements[hole.loc] = elements[childLoc];
		
		location child = getLocation(elements[childLoc], childLoc);
		child.loc = hole.loc;
		hole.loc = childLoc;
	}
	
	// handle case of it being parent of last element seperately
	if (hole.loc == heapBottomParentLoc) {
		int childLoc = hole.getLeftChildLoc();
		if ((childLoc+1 < elementCount) &&
			(comparator.compare(elements[childLoc], elements[childLoc+1]) > 0)) {
			childLoc++;
		}
		
		if (comparator.compare(origElement, elements[childLoc]) > 0) {
			elements[hole.loc] = elements[childLoc];
			
			location child = getLocation(elements[childLoc], childLoc);
			child.loc = hole.loc;
			hole.loc = childLoc;
		}
	}
	
	// put the element back into the hole
	elements[hole.loc] = origElement;
	
	if (checking)
		Check();
	
	return;
}

protected void Check() {
	if (elementCount > elements.length) {
		throw new UnexpectedException("bad element count: " + this);
	}
	
	if (EntryMap != null) {
		if (EntryMap.size() != elementCount) {
			throw new UnexpectedException("Incorrect Entry Map Size:\nHeap:" + this + "\n\nEntryMap: " + EntryMap + "\n");
		}
	}
	
	for (int i=0; i<elementCount; i++) {
		if ((i > 0) && (comparator.compare(elements[i], elements[location.getParentLoc(i)]) < 0)) {	// check heap condition on priorities
			throw new UnexpectedException("not heap ordered: " + this);
		}
		if (EntryMap != null) {
			location thisLoc = getLocation(i);
			if (thisLoc.loc != i) {
				throw new UnexpectedException("Incorrect Location Map Entry: " + this + "\n\nEntryMap: " + EntryMap + "\n");
			}
		}
	}
}

public String toString() {
	StringBuffer result = new StringBuffer("Heap: ");
	
	result.append("ID: ");
	result.append(System.identityHashCode(this));
	result.append(" elementCount: ");
	result.append(elementCount);
	result.append("\n");
	
	for (int i=0; i<elementCount; i++) {
		result.append("Elt ");
		result.append(i);
		result.append(": ");
		result.append(elements[i]);
		result.append("\n");
	}
	
	return result.toString();
}

}
