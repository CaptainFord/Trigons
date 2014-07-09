/**
 * 
 */
package vordeka.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Alex
 *
 */
public class GeneralUtil {
	public static final Object[] EMPTY_ARRAY = new Object[0];
	
	@SuppressWarnings("unchecked")
	public static <T> T[] nullArray(){
		return (T[]) EMPTY_ARRAY;
	}

	/**
	 * Convenience method which tests if two values are equal,
	 * and accounts for null values
	 * @param source
	 * @param source2
	 * @return
	 */
	public static boolean equal(Object o1, Object o2) {
		return o1 == o2 || (o1 != null && o1.equals(o2));
	}
	
	/**
	 * Convenience method which tests if two values are not equal,
	 * and accounts for null values
	 * @param source
	 * @param source2
	 * @return
	 */
	public static boolean notEqual(Object o1, Object o2) {
		return (o1 != o2) && (o1 == null || !o1.equals(o2));
	}

	/**
	 * Attempts to cast an object to a particular class. If it can't,
	 * it throws a ClassCastException with a message detailing the attempt.
	 * @param object
	 * 		The object to cast
	 * @param toClass
	 * 		The class to cast it to
	 * @return
	 * 		the class cast to the desired type
	 * @throws NullPointerException
	 * 		if <code>toClass</code> is null
	 * @throws ClassCastException
	 * 		if the cast fails
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static <T> T checkedCast(Object object, Class<T> toClass){
		if(object == null || toClass.isInstance(object)){
			return (T) object;
		}
		throw new ClassCastException("Can't cast " + object.getClass() + " to " + toClass);
	}

	public static UnsupportedOperationException notImplemented() {
		return new UnsupportedOperationException("Not implemented yet");	
	}
	
	public static <T extends Comparable<? super T>> void sortReverse(List<T> list) {
		Collections.sort(list, new Comparator<T>(){
			@Override
			public int compare(T o1, T o2) {
				return o2.compareTo(o1);
			}});
	}
}
