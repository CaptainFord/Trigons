package vordeka.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains methods that assist in throwing or creating exceptions.
 * @author V
 *
 */
public class ExceptionUtil {

	/**
	 * Verifies that the specified values are not null, if they are, an 
	 * IllegalArgumentException is thrown indicating which fields failed
	 * violation.
	 * @param fieldNames
	 * 		The names of the fields, separated by commas (optional)
	 * @param values
	 * 		The values to check
	 */
	public static void notNull(String fieldNames, Object ... values) {
		int lastIndex = -1;
		int nextIndex = 0;
		if(fieldNames == null){
			fieldNames = "";
			nextIndex = -1;
			lastIndex = 0;
		}
		List<String> failedFields = null;
		int unknownFailures = 0;
		for(int i=0; i<values.length; ++i){
			if(nextIndex != -1)
				nextIndex = fieldNames.indexOf(',', lastIndex);
			if(values[i] == null){
				if(failedFields == null) failedFields = new ArrayList<String>();
				if(nextIndex == -1 && lastIndex == 0){
					++unknownFailures;
				} else {
					String f = (nextIndex == -1 ? 
							fieldNames.substring(lastIndex) : 
							fieldNames.substring(lastIndex, nextIndex)).trim();
					if(f.isEmpty())
						++unknownFailures;
					else
						failedFields.add(f);
				}
			}
			lastIndex = nextIndex + 1;
		}
		
		if(failedFields != null){
			if(unknownFailures > 0){
				failedFields.add("(" + unknownFailures + " unlabeled)");
			}
			StringBuilder b = new StringBuilder();
			int end = failedFields.size();
			for(int i=0; i<end; ++i){
				if(i == end - 2){
					b.append(" and ");
				} else if(i < end - 2){
					b.append(", ");
				}
				b.append(failedFields.get(i));
			}
			b.append(" may not be null");
			throw new IllegalArgumentException(b.toString());
		}
	}

	public static void checkIndexLessOrEqual(int index, int size) {
		if(index < 0 || index > size)
			throw new IndexOutOfBoundsException("Index: " + index + "    Size: " + size);
	}
	
	public static void checkIndexLess(int index, int size) {
		if(index < 0 || index >= size)
			throw new IndexOutOfBoundsException("Index: " + index + "    Size: " + size);
	}
	
	public static void checkRangeLess(int start, int end, int size) {
		if(start < 0 || start >= size || end < 0 || end >= size || end < start)
			throw new IndexOutOfBoundsException("Start: " + start + "    End: " + end  + "    Size: " + size);
	}
	
	public static void checkRangeLessOrEqual(int start, int end, int size) {
		if(start < 0 || start > size || end < 0 || end > size || end < start)
			throw new IndexOutOfBoundsException("Start: " + start + "    End: " + end  + "    Size: " + size);
	}
	
	public static IndexOutOfBoundsException indexOutOfBounds(int index, int size){
		return new IndexOutOfBoundsException("Index: " + index + "    Size: " + size);
	}
	
	public static IndexOutOfBoundsException indexOutOfBounds(int index, int min, int max){
		return new IndexOutOfBoundsException("Index: " + index + 
				"    Min: " + min + "    Max: " + max);
	}

	

	
}
