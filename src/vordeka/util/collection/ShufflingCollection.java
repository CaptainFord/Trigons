package vordeka.util.collection;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * <p>This implementation of Collection not only doesn't guarantee
 * that it's elements are in order, it actually guarantees that
 * its elements are in a different order with every traversal.</p>
 * 
 * <p>
 * This collection implementation was designed for a specific
 * application for which it is particularly well-suited. I've built
 * a turn-based game in which game objects execute events. However,
 * the selection of targets and the execution of events depends entirely
 * on the order in which the objects are presented. In order to be
 * fair to all of them, the order has to be different on every
 * iteration, to give every card a fair chance to be chosen first. 
 * </p>
 * 
 * <p>
 * The other alternative was to produce a new, shuffled list on every
 * event invocation. However, many of the target types don't even use
 * the list of all possible targets, so the effort spent shuffling the
 * list is wasted in that case. A second advantage of this implementation
 * is that no time is spent if the list isn't used. A third, related benefit
 * is that this instance can be re-used, unlike a shuffled deck, which
 * needs a new instance with every invocation (this isn't a large savings,
 * considering that it still needs to perform the shuffle operation when
 * the iterator is created, but at least no list instance is created).
 * </p>
 * 
 * <p>
 * This class is a view into another collection, and doesn't allow
 * modification of the original. A subclass could allow modification by
 * delegating the calls to the protected source field.
 * </p>
 * 
 * <p>
 * One last note about this class's iterators. Each Iterator puts the 
 * elements in a different order by creating a copy of the data in the
 * source collection, as a result, modification of the underlying source
 * collection after the iterator has been created will have no effect on
 * the iteration (although concurrent modification can still occur while a
 * {@link ShuffledIterator} is being constructed). In addition, all of its 
 * iterators implement {@link ListIterator} as well as the method 
 * {@link ShuffledIterator#size() size()} (since it could differ from the 
 * size of the underlying source during its execution).
 * </p>
 * 
 * @author V
 *
 * @param <E>
 */
public class ShufflingCollection<E> extends AbstractCollection<E> {

	/**
	 * Even if I made this field private, a subclass could still capture it
	 * from the constructor anyway.
	 */
	protected Collection<? extends E> source;
	private Random random;

	public ShufflingCollection(Collection<? extends E> source){
		this(source, new Random());
	}
	
	public ShufflingCollection(Collection<? extends E> source,
			Random rand) {
		//	Fail fast and early.
		if(source == null) throw new IllegalArgumentException("null source is not allowed");
		this.source = source;
		this.random = rand;
	}

	public class ShuffledIterator implements ListIterator<E> {

		private final Object[] elements;
		private int index;

		public ShuffledIterator(){
			Iterator<? extends E> itr = source.iterator();
			elements = new Object[source.size()];
			
			if(itr.hasNext()){
				elements[0] = itr.next();
				int size = 1;
				int index;
				while(itr.hasNext()){
					index = random.nextInt(++size);
					elements[size-1] = elements[index];
					elements[index] = itr.next();
				}
			}
			index = 0;
		}
		
		public ShuffledIterator(int size) {
			if(source.isEmpty()){
				elements = new Object[0];
				index = 0;
				return;
			}
				
			Object[] src = source.toArray();
			int srcSize = src.length;
			
			elements = new Object[size];
			for(int i=0; i<size; ++i){
				elements[i] = src[random.nextInt(srcSize)];
			}
			index = 0;
		}

		public void add(E e) {
			throw new UnsupportedOperationException("Cannot modify a shuffled view; It has no connection to the source");
		}

		public boolean hasNext() {
			return index < elements.length;
		}

		public boolean hasPrevious() {
			return index > 0;
		}

		@SuppressWarnings("unchecked")
		public E next() {
			if(index >= elements.length)
				throw new NoSuchElementException("Exceeding list length: " + elements.length + " (index=" + index + ")");
			return (E) elements[index++];
		}

		public int nextIndex() {
			return index;
		}
		
		@SuppressWarnings("unchecked")
		public E previous() {
			if(index >= elements.length)
				throw new NoSuchElementException("Already at beginning (index=" + index + ")");
			return (E) elements[--index];
		}

		public int previousIndex() {
			return index - 1;
		}

		public void remove() {
			throw new UnsupportedOperationException("Cannot modify a shuffled view; It has no connection to the source");
		}

		public void set(E e) {
			throw new UnsupportedOperationException("Cannot modify a shuffled view; It has no connection to the source");
		}
		
		public int size(){
			return elements.length;
		}
		
	}
	
	/**
	 * Creates a ListIterator containing all the elements in the
	 * underlying collection, but in a randomized order. Once
	 * created, the order of the elements is fixed for that iterator.
	 * Different iterators contain the same elements in a different
	 * order. Changes to the underlying collection only affect iterators
	 * created after those changes have occurred.  
	 */
	@Override
	public ListIterator<E> iterator() {
		return new ShuffledIterator();
	}
	
	/**
	 * Creates a ListIterator containing the specified number of elements,
	 * drawn from the underlying collection. The resulting list of elements
	 * may contain duplicates.
	 */
	public ListIterator<E> iterator(int size) {		
		return new ShuffledIterator(size);
	}

	@Override
	public boolean contains(Object o) {
		return source.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return source.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return source.isEmpty();
	}

	@Override
	public int size() {
		return source.size();
	}

	public static <T> ShufflingCollection<T> wrap(Collection<? extends T> c) {
		return new ShufflingCollection<T>(c);
	}

	public static <T> ShufflingCollection<T> wrap(Collection<? extends T> c, Random rand) {
		return new ShufflingCollection<T>(c, rand);
	}
}
