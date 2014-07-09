package vordeka.util.list;

import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * <p>
 * This was originally created as a generic interface for the additional methods added
 * to ArrayIterator, which had the advantage of not having to track the removal
 * index in case {@link #remove()} was called, since an array's size can't be
 * changed. For other implementations, though, the contract of {@link #remove()}
 * stays unchanged: The last element returned by the iterator is the one to be removed.
 * </p>
 * <p>
 * The original purpose was to provide some transparent access to some useful 
 * methods of an underlying {@link java.util.List List} that were absent in an
 * ArrayIterator, such as {@link #get(int)} and {@link #size()}. It has since grown 
 * to provide numerous additional functions, such as {@link #shift(int)}, 
 * {@link #setPos(int)}, and {@link #peek(int)}, but the most notable is 
 * {@link #insert(int,E)}, which simply allows arbitrary modification of the list
 * without causing a ConcurrentModificationException.
 * </p>
 * <p>
 * Most recently, I added the concept of a 'last element' to this interface, which
 * is accessible using the methods {@link #hasLast()}, {@link #lastIndex()} and 
 * {@link #last()}.
 * </p>
 * <p>
 * Note that the methods that use indexes throw IndexOutOfBoundsException 
 * instead of NoSuchElementException. This creates a distinction between the 
 * iterator running out of elements, and an error in indexing the list.</p>
 * 
 * 
 * @author V
 *
 * @param <E>
 */
public interface ListIteratorPlus<E> extends ListIterator<E> {
	
	/**
	 * Peeks at the next element without advancing the iterator.
	 * @return 
	 * 		The next element in the iteration
	 * @throws NoSuchElementException if the end of the iteration
	 * 		has already been reached
	 */
	E peek();
	
	/**
	 * Peeks at the previous element without rewinding the iterator.
	 * @return 
	 * 		The previous element in the iteration
	 * @throws NoSuchElementException 
	 * 		if there is no previous element to return
	 */
	E peekPrevious();
	
	/**
	 * Returns an element at a position relative to the index of
	 * the next element. <code>peek(-1)</code> returns the same
	 * value as <code>previous()</code>, while <code>peek(0)</code>
	 * returns the same value as <code>next()</code>
	 * @param offset
	 * 		The offset from the index of the previous element
	 * @return 
	 * 		The element at the specified offset
	 * @throws IndexOutOfBoundsException 
	 * 		if the offset is out of bounds
	 */
	E peek(int offset);
	
	/**
	 * Identical to the List method of the same name.
	 * @return
	 * 		The size of the iteration
	 */
	int size();
	
	/**
	 * Might as well implement it, even if it is just a wrapper
	 * around size().
	 * @return
	 * 		true if there are no elements in this iteration, false
	 * otherwise
	 */
	boolean isEmpty();
	
	/**
	 * Identical to the List method of the same name.
	 * @param index
	 * 		The index of the element to retrieve
	 * @return
	 * 		The element at the specified index
	 * @throws IndexOutOfBoundsException
	 * 		If the specified index is out of bounds
	 */
	E get(int index);
	
	/**
	 * Shifts the position of this iterator by the specified
	 * offset, and returns an element at that position. The element
	 * returned is consistent with the result if you had made a corresponding
	 * number of calls to {@link #next()} or {@link #previous())}. This allows
	 * all iterator positions to be achieved using this method.
	 * <p>
	 * The element returned if you call <code>shift(0)</code> is undefined,
	 * and may even be null.
	 * </p>
	 * Think of <code>offset</code> as the number of calls to next() or previous()
	 * to perform, where a positive offset calls next() and a negative offset calls
	 * previous().
	 * @param offset
	 * 		The offset from the current position
	 * @return
	 * 		The element at the specified index
	 * @throws IndexOutOfBoundsException
	 * 		If the specified offset is outside the bounds of the array
	 */
	E shift(int offset);
	
	/**
	 * Sets the position of the iterator so that the next element
	 * has the specified index. This way, <code>setPos(0)</code> rewinds
	 * to the beginning, and <code>setPos(size())</code> is after the last
	 * element. 
	 * @param index
	 * 		The index of the element to retrieve
	 * @throws IndexOutOfBoundsException
	 * 		If the specified offset is outside the bounds of the array
	 */
	void setPos(int index);
	
	/**
	 * Calls add(int, E) on the underlying list, and shifts this ListIterator's
	 * position to adapt to the insertion. Inserting an element through this
	 * method allows this iterator to adapt to the change, and prevents a 
	 * ConcurrentModificationException.
	 * @param index
	 * 		the index to insert the element at
	 * @param value
	 * 		the element to insert
	 */
	void insert(int index, E value);
	
	/**
	 * Identical to the list method of the same name. Removing an element
	 * through this method allows this iterator to adapt to the change,
	 * and prevents a ConcurrentModificationException.
	 * @param index
	 * 		the index of the element to remove
	 * @return
	 * 		the element that was removed
	 */
	E remove(int index);
	
	/**
	 * Identical to the list method of the same name. Setting an element
	 * through this method allows this iterator to adapt to the change,
	 * and prevents a ConcurrentModificationException.
	 * @param index
	 * 		the index of the element to remove
	 * @return
	 * 		the element that was removed
	 */
	E set(int index, E value);
	
	/**
	 * Tests if this iterator has a valid last element that can be investigated by
	 * {@link #last()} or {@link #lastIndex()}. This also tests whether or not 
	 * {@link #add()}, {@link #set(E)}, {@link #remove()} and {@link #last()} 
	 * would throw a NoSuchElementException. 
	 * @return
	 * 		the last element retrieved through iteration on this iterator
	 */
	boolean hasLast();
	
	/**
	 * Returns the index of the last element that was returned by a call
	 * to next() or previous(). Returns -1 if next() or previous() has not 
	 * been called yet, or if this ListIterator doesn't track the last index.
	 * <p>
	 * Note: Implementations that allows negative indices use Integer.MIN_VALUE
	 * instead of -1. Currently, this only includes {@link ArbitraryList}.
	 * @return
	 * 		the index of the last element returned.
	 */
	int lastIndex();
	
	/**
	 * Returns the element that was returned by the last call to next() or
	 * previous(). This is the element that would be affected by a call
	 * to {@link #remove()} or {@link #set(E)}.
	 * <p>
	 * Unlike next() and previous(), this method does not change the iterator's
	 * position. It is purely informational.
	 * @return
	 * 		the last element retrieved through iteration on this iterator
	 * @throws NoSuchElementException
	 * 		if there is no last element
	 */
	E last();
	
}
