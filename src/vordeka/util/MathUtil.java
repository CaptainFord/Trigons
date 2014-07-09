package vordeka.util;

import java.util.Arrays;

public class MathUtil {
	public static long fact(int n){
		if(n < 0) throw new IllegalArgumentException("Factorial is undefined for negative integers");
		if(n < 2) return 1;
		
		long val = 1;
		while(n > 1){
			val *= n--;
		}
		return val;
	}

	/**
	 * Returns the minimum value found in the array.
	 * @param vals
	 * 		the values to process
	 * @return the minimum value, or <code>Integer.MAX_VALUE</code> if the array was empty.
	 */
	public static int min(int[] vals) {
		int min = Integer.MAX_VALUE;
		for(int v : vals){
			if(v < min)
				min = v;
		}
		return min;
	}
	
	/**
	 * Returns the maximum value found in the array.
	 * @param vals
	 * 		the values to process
	 * @return the maximum value, or <code>Integer.MIN_VALUE</code> if the array was empty.
	 */
	public static int max(int[] vals) {
		int max = Integer.MIN_VALUE;
		for(int v : vals){
			if(v > max)
				max = v;
		}
		return max;
	}

	public static int median(int[] vals) {
		vals = Arrays.copyOf(vals, vals.length);
		Arrays.sort(vals);
		int i = vals.length / 2;
		if(vals.length % 2 == 0){
			return (vals[i] + vals[i + 1]) / 2;
		} else {
			return vals[i];
		}
	}
}
