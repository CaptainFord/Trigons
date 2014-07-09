/**
 * 
 */
package vordeka.util.list;

import java.util.NoSuchElementException;

/**
 * @author Alex
 *
 */
public class ArrayIterator<E> implements ListIteratorPlus<E> {
	
	protected int lastIndex = -1;
	protected int index;
	protected final E[] array;

	public ArrayIterator(E[] array){
		this.array = array;
		index = 0;
	}
	
	public ArrayIterator(E[] array, int index) {
		this.array = array;
		this.index = index;
	}

	@Override
	public boolean hasNext() {
		return array != null && index < array.length;
	}

	@Override
	public E next() {
		if(index >= array.length){
			throw new NoSuchElementException("Exceeded array length: " + array.length);
		}
		return array[lastIndex = index++];
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Cannot remove elements from an array");
	}
	
	public static <T> ArrayIterator<T> wrap(T ... array){
		return new ArrayIterator<T>(array);
	}

	@Override
	public boolean hasPrevious() {
		return index > 0;
	}

	@Override
	public E previous() {
		if(index <= 0){
			throw new NoSuchElementException("Exceeded array start: " + array.length);
		}
		return array[lastIndex = --index];
	}

	@Override
	public int nextIndex() {
		return index;
	}

	@Override
	public int previousIndex() {
		return index-1;
	}

	@Override
	public void set(E e) {
		throw new UnsupportedOperationException("This iterator is immutable");
	}

	@Override
	public void add(E e) {
		throw new UnsupportedOperationException("Cannot add elements to an array");
	}

	@Override
	public E peek() {
		if(index >= array.length){
			throw new NoSuchElementException("Exceeded array length: " + array.length);
		}
		return array[index];
	}

	@Override
	public E peekPrevious() {
		if(index <= 0){
			throw new NoSuchElementException("Exceeded array start: " + array.length);
		}
		return array[index-1];
	}

	@Override
	public E peek(int offset) {
		int retIndex = index + offset;
		if(retIndex < 0 || retIndex >= array.length){
			throw new IndexOutOfBoundsException("Exceeded array bounds (length=" + array.length + 
					"): " + index + " + " + offset + " - 1 = " + retIndex);
		}
		return array[retIndex];
	}

	@Override
	public int size() {
		return array.length;
	}

	@Override
	public boolean isEmpty() {
		return array.length == 0;
	}

	@Override
	public E get(int index) {
		if(index < 0 || index >= array.length){
			throw new IndexOutOfBoundsException("Exceeded array bounds (length=" + array.length + 
					"): " + index);
		}
		return array[index];
	}

	@Override
	public E shift(int offset) {
		if(offset == 0)
			return array[lastIndex];		
		if(offset > 0){
			while(--offset > 0){
				next();
			}
			return next();
		} else {
			while(++offset < 0){
				previous();
			}
			return previous();
		}
	}

	@Override
	public void setPos(int index) {
		if(index < 0 || index > array.length){
			throw new IndexOutOfBoundsException("Exceeded array bounds (length=" + array.length + 
					"): " + index);
		}
		this.index = index;
	}

	@Override
	public void insert(int index, E value) {
		throw new UnsupportedOperationException("Cannot add elements to an array");
	}

	@Override
	public int lastIndex() {
		return lastIndex;
	}

	@Override
	public E remove(int index) {
		throw new UnsupportedOperationException("Cannot remove elements from an array");
	}

	@Override
	public E set(int index, E value) {
		throw new UnsupportedOperationException("This iterator is immutable");
	}
	
	public static class MutableArrayIterator<E> extends ArrayIterator<E> {

		public MutableArrayIterator(E[] array) {
			super(array);
		}
		
		public MutableArrayIterator(E[] array, int index) {
			super(array, index);
		}

		@Override
		public E set(int index, E value) {
			return array[index] = value;
		}
		
		@Override
		public void set(E e) {
			if(lastIndex == -1)
				throw new NoSuchElementException("Must make a move before this method will function");
			array[lastIndex] = e;
		}
	}

	@Override
	public boolean hasLast() {
		return lastIndex != -1;
	}

	@Override
	public E last() {
		if(lastIndex == -1)
			throw new NoSuchElementException("Must make a move before there can be a last element");
		return array[lastIndex];
	}
}
