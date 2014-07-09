/**
 * 
 */
package vordeka.util.list;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;

import vordeka.util.list.ArrayIterator.MutableArrayIterator;

/**
 * Simply wraps an existing array. This class won't allow modification
 * of existing elements at all. Use a ModifiableArrayWrapper to allow elements
 * to be set.
 * <p>
 * The primary purpose of this class is to reduce the overhead involved
 * in a class that needs to be able to process arrays and lists interchangeably.
 * This class allows arrays to be used without needing to be copied.
 * @author Vordeka
 *
 */
public class ArrayWrapper<E> extends AbstractList<E> {

	protected final E[] elements;
	/**
	 * 
	 */
	public ArrayWrapper(E[] elements) {
		this.elements = elements;
	}

	/**
	 * Unsupported - Arrays cannot change size.
	 */
	@Override
	public boolean add(E e) {
		throw new UnsupportedOperationException("The size of a wrapped array may not be altered");
	}

	/**
	 * Unsupported - Arrays cannot change size.
	 */
	@Override
	public void add(int index, E element) {
		throw new UnsupportedOperationException("The size of a wrapped array may not be altered");
		}

	/**
	 * Unsupported - Arrays cannot change size.
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException("The size of a wrapped array may not be altered");
	}

	/**
	 * Unsupported - Arrays cannot change size.
	 */
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException("The size of a wrapped array may not be altered");
	}

	/**
	 * Unsupported - Arrays cannot change size.
	 */
	@Override
	public void clear() {
		throw new UnsupportedOperationException("The size of a wrapped array may not be altered");
	}

	/* (non-Javadoc)
	 * @see java.util.List#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		if(o == null){
			for(E e : elements){
				if(e == null)
					return true;
			}
		} else {
			for(E e : elements){
				if(o.equals(e))
					return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		for(Object o : c){
			if(!contains(o))
				return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.util.List#get(int)
	 */
	@Override
	public E get(int index) {
		rangeCheck(index);
		return elements[index];
	}

	protected void rangeCheck(int index) {
		if(index < 0 || index >= elements.length) throw new IndexOutOfBoundsException("Index: " + index +"   Size: " + elements.length);
	}

//	private void rangeCheckUpToSize(int index) {
//		if(index < 0 || index > elements.length) throw new IndexOutOfBoundsException("Index: " + index +"   Size: " + elements.length);
//	}

	/* (non-Javadoc)
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	@Override
	public int indexOf(Object o) {
		if(o == null){
			for(int i=0; i<elements.length; ++i){
				if(elements[i] == null)
					return i;
			}
		} else {
			for(int i=0; i<elements.length; ++i){
				if(o.equals(elements[i]))
					return i;
			}
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see java.util.List#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return elements.length == 0;
	}

	/* (non-Javadoc)
	 * @see java.util.List#iterator()
	 */
	@Override
	public Iterator<E> iterator() {
		return new MutableArrayIterator<E>(elements);
	}

	/* (non-Javadoc)
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	@Override
	public int lastIndexOf(Object o) {
		if(o == null){
			for(int i = elements.length-1; i>=0; --i){
				if(elements[i] == null)
					return i;
			}
		} else {
			for(int i = elements.length-1; i>=0; --i){
				if(o.equals(elements[i]))
					return i;
			}
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see java.util.List#listIterator()
	 */
	@Override
	public ListIteratorPlus<E> listIterator() {
		return new MutableArrayIterator<E>(elements);
	}

	/* (non-Javadoc)
	 * @see java.util.List#listIterator(int)
	 */
	@Override
	public ListIteratorPlus<E> listIterator(int index) {
		return new MutableArrayIterator<E>(elements, index);
	}

	/* (non-Javadoc)
	 * @see java.util.List#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("The size of a wrapped array may not be altered");
	}

	/* (non-Javadoc)
	 * @see java.util.List#remove(int)
	 */
	@Override
	public E remove(int index) {
		throw new UnsupportedOperationException("The size of a wrapped array may not be altered");
	}

	/* (non-Javadoc)
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("The size of a wrapped array may not be altered");
	}

	/* (non-Javadoc)
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("The size of a wrapped array may not be altered");
	}

	/* (non-Javadoc)
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	@Override
	public E set(int index, E element) {
		throw new UnsupportedOperationException("This array wrapper may not be modified");
	}

	/* (non-Javadoc)
	 * @see java.util.List#size()
	 */
	@Override
	public int size() {
		return elements.length;
	}

	/**
	 * Returns the array wrapped by this ArrayWrapper, not
	 * a copy. Use the generic version if you need a copy
	 * (or make one yourself).
	 */
	@Override
	public E[] toArray() {
		return elements;
	}

	
	/*private class ArrayIterator implements ListIterator<E>{
		*//**
		 * @param index
		 *//*
		public ArrayIterator(int index) {
			rangeCheckUpToSize(index);
			this.index = index - 1;
			this.lastIndex = -1;
		}

		*//**
		 * 
		 *//*
		public ArrayIterator() {
			this(0);
		}

		 (non-Javadoc)
		 * @see java.util.ListIterator#add(java.lang.Object)
		 
		@Override
		public void add(E e) {
			throw new UnsupportedOperationException("The size of a wrapped array may not be altered");
		}


		int index;
		int lastIndex;
		final int endIndex = elements.length - 1;
		
		@Override
		public boolean hasNext() {
			return index < endIndex;
		}

		@Override
		public E next() {
			if(index >= endIndex) throw new NoSuchElementException("End of array reached");
			lastIndex = ++index;
			return elements[index];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("The size of a wrapped array may not be altered");
		}
		

		 (non-Javadoc)
		 * @see java.util.ListIterator#hasPrevious()
		 
		@Override
		public boolean hasPrevious() {
			return index >= 0;
		}


		 (non-Javadoc)
		 * @see java.util.ListIterator#nextIndex()
		 
		@Override
		public int nextIndex() {
			return index + 1;
		}

		 (non-Javadoc)
		 * @see java.util.ListIterator#previous()
		 
		@Override
		public E previous() {
			if(index < 0) throw new NoSuchElementException("Beginning of array reached");
			lastIndex = index;
			return elements[index--];
		}

		 (non-Javadoc)
		 * @see java.util.ListIterator#previousIndex()
		 
		@Override
		public int previousIndex() {
			return index;
		}

		 (non-Javadoc)
		 * @see java.util.ListIterator#set(java.lang.Object)
		 
		@Override
		public void set(E e) {
			ArrayWrapper.this.set(lastIndex, e);
		}
		
	}*/


	/**
	 * @param commands
	 * @return
	 */
	public static <T> ArrayWrapper<T> wrap(T[] elements) {
		return new ArrayWrapper<T>(elements);
	}
	
	public static <T> ArrayWrapper<T> wrap(T[] elements, boolean modifiable) {		
		return modifiable ? new ArrayWrapperModifiable<T>(elements) : new ArrayWrapper<T>(elements);
	}
	
	/**
	 * Simply wraps an existing array. Allows
	 * modification of existing elements but won't allow
	 * adding or removing of elements (like an array).
	 * This is used to reduce the overhead of a class
	 * that can dynamically refer to either an array
	 * or a collection.
	 * @author Vordeka
	 *
	 * @param <E>
	 */
	public static class ArrayWrapperModifiable<E> extends ArrayWrapper<E> {

		public ArrayWrapperModifiable(E[] elements) {
			super(elements);
		}

		@Override
		public E set(int index, E element) {
			rangeCheck(index);
			return elements[index] = element;
		}
	}
}
