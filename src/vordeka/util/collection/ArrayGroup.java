/**
 * 
 */
package vordeka.util.collection;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import vordeka.util.GeneralUtil;

/**
 * This class is unsynchronized. It's biggest difference from an ArrayList
 * is that it does not preserve the order of the elements when one is
 * removed, instead moving the last element in the array into the position
 * of the deleted element. The effect is that removal becomes a constant
 * time operation, as opposed to linear. 
 * <p>
 * It's best application is for situations where you will be doing a lot
 * of removal using the Iterator.remove() method. You are better served using
 * a Set if you will be primarily using the Collection.remove() method.
 * @author Alex
 *
 */
public class ArrayGroup<E> extends AbstractCollection<E> {

	private Object[] array = GeneralUtil.EMPTY_ARRAY;
	private int size;
	
	/**
	 * This value is used to allocate the initial array and
	 * for allocating replacement arrays when this collection is cleared.
	 * The default is 1. Regardless, the initial capacity is 0, with an 
	 * array of the base size being allocated on the first call to add().
	 */
	private int baseSize;
	
	private int modCount;
	
	
	public ArrayGroup(){
		baseSize = 1;
	}
	
	public ArrayGroup(int baseSize){
		this.baseSize = baseSize;
	}
	
	
	
	public ArrayGroup(Collection<? extends E> c) {
		this.baseSize = c.size();
		this.addAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public boolean add(E e) {
		++modCount;
		if(size == array.length){
			if(size == 0){
				//	No need to copy, in this case there are no values stored
				array = new Object[baseSize];
			} else {
				Object[] temp = array;
				array = new Object[size * 2 + 1];
				System.arraycopy(temp, 0, array, 0, size);
			}
		}
		array[size] = e;
		++size;
		return true;
	}


	/**
	 * This implementation simply drops the current array
	 * and resets the size to zero. This is a *very* efficient
	 * operation. The only downside is that the first few add
	 * operations will be relatively inefficient.
	 * @see java.util.Collection#clear()
	 */
	public void clear() {
		++modCount;
		array = GeneralUtil.EMPTY_ARRAY;
		size = 0;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		if(o == null){
			for(int i=0; i<size; ++i){
				if(array[i] == null)
					return true;
			}
			return false;
		} else {
			for(int i=0; i<size; ++i){
				if(o.equals(array[i]))
					return true;
			}
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> c) {
		for(Object o : c){
			if(!contains(o)) return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	public boolean isEmpty() {
		return size == 0;
	}


	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		if(o == null){
			for(int i=0; i<size; ++i){
				if(array[i] == null){
					remove(i);
					return true;
				}
			}
		} else {
			for(int i=0; i<size; ++i){
				if(o.equals(array[i])){
					remove(i);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Removes the item at the specified index.
	 * Similar to the method defined in List, except
	 * that it does not return a value. Furthermore,
	 * the item at the back of the list is moved to
	 * fill in the now blank space, as opposed to
	 * shifting all subsequent elements to the left. 
	 * @param index
	 * 		The index of the value to remove.
	 */
	public void remove(int index) {
		if(index >= size || index < 0) 
			throw new IllegalArgumentException("Index is out of range: index=" + index + "  size=" + size);
		++modCount;
		array[index] = array[--size];
		array[size] = null;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c) {
		int startMod = modCount;
		for(Object o : c){
			remove(o);
		}
		return modCount != startMod;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c) {
		int startMod = modCount;
		for(int i=0; i<size; ++i){
			if(!c.contains(array[i])){
				remove(i--);
			}
		}
		return modCount != startMod;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#size()
	 */
	public int size() {
		return size;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	public Object[] toArray() {
		Object[] retval = new Object[size];
		System.arraycopy(array, 0, retval, 0, size);
		return retval;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray(T[])
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		if(a.length < size){
			a =  (T[])java.lang.reflect.Array
		            .newInstance(a.getClass().getComponentType(), size);
		}
		System.arraycopy(array, 0, a, 0, size);
		return a;
	}

	/**
	 * @param baseSize the baseSize to set
	 */
	public void setBaseSize(int baseSize) {
		this.baseSize = baseSize;
	}

	/**
	 * @return the baseSize
	 */
	public int getBaseSize() {
		return baseSize;
	}
	
	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#iterator()
	 */
	public Iterator<E> iterator() {
		return new ArrayGroupIterator();
	}

	public class ArrayGroupIterator implements Iterator<E>{

		private int index;
		private int expectedModCount;
		
		ArrayGroupIterator(){
			expectedModCount = modCount;
		}
		
		public boolean hasNext() {
			if(modCount != expectedModCount) throw new ConcurrentModificationException();
			return index < size;
		}

		@SuppressWarnings("unchecked")
		public E next() {
			if(modCount != expectedModCount) throw new ConcurrentModificationException();
			return (E) array[index++];
		}

		public void remove() {
			if(modCount != expectedModCount) throw new ConcurrentModificationException();
			if(index == 0) throw new NoSuchElementException("No element present to be removed");
			ArrayGroup.this.remove(--index);
			expectedModCount = modCount;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends E> c) {
		int neededSize = size + c.size();
		int startSize = size;
		++modCount;
		if(neededSize >= array.length){
			Object[] temp = array;
			array = new Object[Math.max(baseSize, neededSize + size)];
			System.arraycopy(temp, 0, array, 0, size);
		}
		for(E e : c){
			array[size++] = e;
		}
		return startSize != size;
	}
}
