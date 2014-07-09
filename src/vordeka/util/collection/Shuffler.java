/**
 * 
 */
package vordeka.util.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * @author Alex
 *
 */
public class Shuffler {

	/**
	 * @param boundComponent
	 * @return
	 */
	public static <T> List<T> createShuffledCopy(Collection<T> collection) {
		Random random = new Random();
		List<T> retval = new ArrayList<T>();
		int size = 0; 
		int index;
		for(T t : collection){
			index = random.nextInt(++size);
			if(index < size -1)
				t = retval.set(index, t);
			retval.add(t);
		}
		return retval;
	}
	
	/**
	 * @param boundComponent
	 * @return
	 */
	public static <T> List<T> createShuffledCopy(Collection<? extends T> ... collections) {
		Random random = new Random();
		List<T> retval = new ArrayList<T>();
		int size = 0; 
		int index;
		for(Collection<? extends T> c : collections ){
			for(T t : c){
				index = random.nextInt(++size);
				if(index < size -1)
					t = retval.set(index, t);
				retval.add(t);
			}
		}
		return retval;
	}

	/**
	 * @param list
	 * @return
	 */
	public static <T> List<T> shuffle(List<T> list) {
		Random random = new Random();
		int size = list.size();
		for(int i = size - 1; i > 0; --i){
			list.set(i, list.set(random.nextInt(i + 1), list.get(i)));
		}
		return list;
	}

	public static void shuffle(int[] list, Random rand) {
		int size = list.length;
		for(int i = size - 1; i > 0; --i){
			int x = rand.nextInt(i + 1);
			int val = list[i];
			list[i] = list[x];
			list[x] = val;
		}
	}

	public static int[] shuffledIndices(int length, Random rand) {
		int[] list = new int[length];
		for(int i=0; i<length; ++i){
			list[i] = i;
		}
		shuffle(list, rand);
		return list;
	}



}
