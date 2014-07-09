/**
 * 
 */
package vordeka.util.list;

import java.util.AbstractList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * @author Alex
 *
 */
public class SingleElementList<E> extends AbstractList<E> {

	private E element;

	public SingleElementList(E element){
		this.element = element;
	}
	
	@Override
	public Iterator<E> iterator() {
		return new SingleElementIterator<E>(element);
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public E get(int index) {
		if(index != 0) throw new IndexOutOfBoundsException("Index: " + index +"   Size: 1");
		return element;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
		    return true;
		if (!(o instanceof List))
		    return false;
		List<?> other = (List<?>) o;
		if(other.size() != 1) return false;
		return element == null ? other.get(0) == null : element.equals(o);
	}

	@Override
	public int hashCode() {
		return element == null ? super.hashCode() : element.hashCode();
	}

	@Override
	public int indexOf(Object o) {
		if(element == null ? o == null : element.equals(o))
			return 0;
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		if(element == null ? o == null : element.equals(o))
			return 0;
		return -1;
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return new SingleElementIterator<E>(element, index);
	}

	@Override
	public E set(int index, E element) {
		if(index != 0) throw new IndexOutOfBoundsException("Index: " + index +"   Size: 1");
		E temp = this.element;
		this.element = element;
		return temp;
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		if(fromIndex != 0) throw new IndexOutOfBoundsException("Index: " + fromIndex +"   Size: 1");
		if(fromIndex == toIndex) return Collections.emptyList();
		if(toIndex != 1) throw new IndexOutOfBoundsException("Index: " + toIndex +"   Size: 1");
		return this;
	}

	public static class SingleElementIterator<E> implements ListIterator<E>{
		E element;
		boolean hasNext;
		public SingleElementIterator(E element, boolean hasNext){
			this.element = element;
			this.hasNext = hasNext;
		}
		public SingleElementIterator(E element, int index){
			if(index < 0 || index > 1)
				throw new IndexOutOfBoundsException("Index: " + index + "   Size: 1");
			this.element = element;
			hasNext = index == 0;
		}
		
		public SingleElementIterator(E element) {
			this.element = element;
			hasNext = true;
		}
		
		@Override
		public void add(E e) {
			throw new UnsupportedOperationException("Cannot modify this list");
		}

		@Override
		public boolean hasNext() {
			return hasNext;
		}

		@Override
		public boolean hasPrevious() {
			return !hasNext;
		}

		@Override
		public E next() {
			if(!hasNext) throw new NoSuchElementException("No remaining elements");
			hasNext = false;
			return element;
		}

		@Override
		public int nextIndex() {
			return hasNext ? 0 : 1;
		}

		@Override
		public E previous() {
			if(hasNext) throw new NoSuchElementException("No prior elements");
			hasNext = true;
			return element;
		}

		@Override
		public int previousIndex() {
			return hasNext ? -1 : 0;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Cannot modify this list");
		}

		@Override
		public void set(E e) {
			element = e;
		}
		
	}

	public static <T> SingleElementList<T> wrap(T element) {
		return new SingleElementList<T>(element);
	}
	
}
