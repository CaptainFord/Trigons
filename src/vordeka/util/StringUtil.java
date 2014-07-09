/**
 * 
 */
package vordeka.util;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.swing.table.TableModel;

import vordeka.util.string.RepeatCharSequence;
import vordeka.util.string.SubSequence;

/**
 * @author Alex
 *
 */
public class StringUtil {
	public static String ARRAY_OPEN = "{"; 
	public static String ARRAY_CLOSE = "}";
	public static String EMPTY_ARRAY = "{}";
	public static String ARRAY_COMMA = ", ";
	
	
	/**
	 * Capitalizes the first letter of each word. Does not otherwise affect the case of the string.
	 * @param input
	 * 		the string to be capitalized
	 * @return
	 * 		the string with the first letter of each word made upper case
	 */
	public static String capitalize(String input){
		//	Is it faster to turn the input into a character array?
		//	YES, if the input is long
		//	NO, if the input is short
		//	However, if I use a character array I can avoid using a
		//	StringBuilder and making repeated calls to append().
		//	So a character array is probably faster
		
		//	Obviously, this method is not locale-friendly. However,
		//	there is no locale-friendly method to capitalize words.
		char[] sentence = input.toCharArray();
		boolean isLeading = true;
		char c;
		for(int i=0; i<sentence.length; ++i){
			c = sentence[i];
			if(Character.isWhitespace(c)){
				isLeading = true;
			} else if(isLeading){
				sentence[i] = Character.toUpperCase(c);
				isLeading = false;
			}
		}
		return String.valueOf(sentence);
	}
	
	/**
	 * Capitalizes all words in the specified string.
	 * @param string
	 * 		The string to capitalize
	 * @return
	 * 		The capitalized string
	 */
	public static String capitalizeAllWords(final String string){
		char[] letters = string.toCharArray();
		boolean priorWhitespace = true;
		for(int i=0; i<letters.length; ++i){
			char c = letters[i];
			if(priorWhitespace && Character.isLowerCase(c)){
				letters[i] = Character.toUpperCase(c);
				priorWhitespace = false;
			} else if(!priorWhitespace && Character.isUpperCase(c)){
				letters[i] = Character.toLowerCase(c);
				priorWhitespace = false;
			} else {
				priorWhitespace = Character.isWhitespace(c);
			}
		}
		return new String(letters);
	}

	public static String arrayToString(Object[] elements, int fromIndex, int toIndex, 
			String separator) {
		if(fromIndex == toIndex) return "";
		if(toIndex < fromIndex) return arrayToString(elements, toIndex, fromIndex, separator);
		StringBuilder builder = new StringBuilder();
		
		for(int i=fromIndex; i<toIndex; ++i){
			builder.append(elements[i]).append(separator);
		}
		builder.setLength(builder.length() - separator.length());
		return builder.toString();
	}
	
	public static String arrayToString(Object[] elements, int fromIndex, int toIndex) {
		return arrayToString(elements, fromIndex, toIndex, ", ");
	}

	public static String arrayToString(Object[] elements, String separator) {
		return arrayToString(elements, 0, elements.length, separator);
	}
	
	public static String arrayToString(Object[] elements) {
		return arrayToString(elements, 0, elements.length);
	}
	
	public static String arrayToString(long[] elements, int fromIndex, int toIndex, String separator) {
		if(fromIndex == toIndex) return "";
		if(toIndex < fromIndex) return arrayToString(elements, toIndex, fromIndex, separator);
		StringBuilder builder = new StringBuilder();
		
		for(int i=fromIndex; i<toIndex; ++i){
			builder.append(elements[i]).append(separator);
		}
		builder.setLength(builder.length() - separator.length());
		return builder.toString();
	}
	
	public static String arrayToString(long[] elements, int fromIndex, int toIndex) {
		return arrayToString(elements, fromIndex, toIndex, ", ");
	}

	public static String arrayToString(long[] elements, String separator) {
		return arrayToString(elements, 0, elements.length, separator);
	}
	
	public static String arrayToString(long[] elements) {
		return arrayToString(elements, 0, elements.length);
	}
	
	public static String arrayToString(int[] elements, int fromIndex, int toIndex, String separator) {
		if(fromIndex == toIndex) return "";
		if(toIndex < fromIndex) return arrayToString(elements, toIndex, fromIndex, separator);
		StringBuilder builder = new StringBuilder();
		
		for(int i=fromIndex; i<toIndex; ++i){
			builder.append(elements[i]).append(separator);
		}
		builder.setLength(builder.length() - separator.length());
		return builder.toString();
	}
	
	public static String arrayToString(int[] elements, int fromIndex, int toIndex) {
		return arrayToString(elements, fromIndex, toIndex, ", ");
	}

	public static String arrayToString(int[] elements, String separator) {
		return arrayToString(elements, 0, elements.length, separator);
	}
	
	public static String arrayToString(int[] elements) {
		return arrayToString(elements, 0, elements.length);
	}
	
	public static String arrayToString(short[] elements, int fromIndex, int toIndex, String separator) {
		if(fromIndex == toIndex) return "";
		if(toIndex < fromIndex) return arrayToString(elements, toIndex, fromIndex, separator);
		StringBuilder builder = new StringBuilder();
		
		for(int i=fromIndex; i<toIndex; ++i){
			builder.append(elements[i]).append(separator);
		}
		builder.setLength(builder.length() - separator.length());
		return builder.toString();
	}
	
	public static String arrayToString(short[] elements, int fromIndex, int toIndex) {
		return arrayToString(elements, fromIndex, toIndex, ", ");
	}

	public static String arrayToString(short[] elements, String separator) {
		return arrayToString(elements, 0, elements.length, separator);
	}
	
	public static String arrayToString(short[] elements) {
		return arrayToString(elements, 0, elements.length);
	}
	
	public static String arrayToString(byte[] elements, int fromIndex, int toIndex, String separator) {
		if(fromIndex == toIndex) return "";
		if(toIndex < fromIndex) return arrayToString(elements, toIndex, fromIndex, separator);
		StringBuilder builder = new StringBuilder();
		
		for(int i=fromIndex; i<toIndex; ++i){
			builder.append(elements[i]).append(separator);
		}
		builder.setLength(builder.length() - separator.length());
		return builder.toString();
	}
	
	public static String arrayToString(byte[] elements, int fromIndex, int toIndex) {
		return arrayToString(elements, fromIndex, toIndex, ", ");
	}

	public static String arrayToString(byte[] elements, String separator) {
		return arrayToString(elements, 0, elements.length, separator);
	}
	
	public static String arrayToString(byte[] elements) {
		return arrayToString(elements, 0, elements.length);
	}

	public static String padLeft(String string, int length) {
		return padLeft(string, length, ' ');
	}
	
	public static String padRight(String string, int length) {
		return padRight(string, length, ' ');
	}
	
	public static String padLeft(String string, int count, char padChar) {
		if(count <= 0)
			return string;
		StringBuilder retval = new StringBuilder();
		while(--count >= 0){
			retval.append(padChar);
		}
		return retval.append(string).toString();
	}
	
	public static String padRight(String string, int count, char padChar) {
		if(count <= 0)
			return string;
		StringBuilder retval = new StringBuilder(string);		
		while(--count >= 0){
			retval.append(padChar);
		}
		
		return retval.toString();
	}

	public static String collectionToString(Collection<?> c, String separator, 
			String startBrace, String endBrace){
		if(c == null) return null;
		StringBuilder b = new StringBuilder(startBrace);
		for(Object o : c){
			b.append(o).append(separator);
		}
		b.setLength(b.length() - separator.length());
		return b.append(endBrace).toString();
	}
	
	public static String collectionToString(Collection<?> c, String separator){
		return collectionToString(c, separator, "{","}");
	}
	
	public static String collectionToString(Collection<?> c) {
		return collectionToString(c, ",");
	}

	public static String mapToString(Map<?, ?> map) {
		return "[<" + mapToString(map, " => ", ">,<") + ">]";
	}

	/**
	 * Creates a string representation for a map object by converting all of 
	 * its keys and values to strings.
	 * @param map
	 * 		The map to print entries from
	 * @param keySep
	 * 		The separator to use within an entry to separate the key and value
	 * @param entrySep
	 * 		The separator to use between entries
	 * @return
	 * 		The entries of the map converted to strings
	 */
	public static String mapToString(Map<?, ?> map, String keySep,
			String entrySep) {
		StringBuilder b = new StringBuilder();
		for(Entry<?,?> entry : map.entrySet()){
			b.append(entry.getKey()).append(keySep).append(entry.getValue()).append(entrySep);
		}
		if(b.length() > 0)
			b.setLength(b.length() - entrySep.length());
		return b.toString();
	}

	/**
	 * Breaks up a long message into multiple lines. It tries to break messages
	 * at white space, but it will prioritize filling at least 1/3 of the columns
	 * on each line.
	 * <p>
	 * This method assumes that tab stops are placed every 8 columns.
	 * @param msg
	 * 		The message to output
	 * @param columns
	 * 		The maximum number of columns per line
	 * @return
	 * 		The formatted message
	 */
	public static String breakUpIntoLines(String msg, int columns) {
		return breakUpIntoLines(msg, columns, "\n");
	}
	
	/**
	 * Breaks up a long message into multiple lines. It tries to break messages
	 * at white space, but it will prioritize filling at least 1/3 of the columns
	 * on each line.
	 * <p>
	 * This method assumes that tab stops are placed every 8 columns.
	 * @param msg
	 * 		The message to output
	 * @param columns
	 * 		The maximum number of columns per line
	 * @param lineBreak
	 * 		The string is used to break up lines, and should include a '\n' character.
	 * It can be used to apply an indent to newly created lines.
	 * @return
	 * 		The formatted message
	 */
	public static String breakUpIntoLines(String msg, int columns, String lineBreak) {
		return breakUpIntoLines(msg, columns, lineBreak, 8);
	}
	

	/**
	 * Breaks up a long message into multiple lines. It tries to break messages
	 * at white space, but it will prioritize filling at least 1/3 of the columns
	 * on each line.
	 * <p>
	 * This method assumes that tab stops are placed every 8 columns.
	 * @param msg
	 * 		The message to output
	 * @param columns
	 * 		The maximum number of columns per line
	 * @param lineBreak
	 * 		The string is used to break up lines, and should include a '\n' character.
	 * It can be used to apply an indent to newly created lines.
	 * @param tabStopGap
	 * 		The width of the gap between tab stops in columns (default is 8)
	 * @return
	 * 		The formatted message
	 */
	public static String breakUpIntoLines(String msg, int columns, 
			String lineBreak, int tabStopGap) {
		//	To deal with tab stops properly, I would create an interface,
		//	probably TabStop or something, with the method getTabLength(int column);
		//	But that would only work for monospaced text, so why bother going to the 
		//	effort when I'm probably only going to use it for the console anyway?
		//	I'll leave it the way it is for now.
		
		StringBuilder b = new StringBuilder();
		int indentLength;
		{
			int lineBreakIndex = lineBreak.lastIndexOf('\n');
			if(lineBreakIndex == -1){
				throw new IllegalArgumentException("lineBreak does not actually contain a line break");
			}
			CharSequence indent = lineBreak.subSequence(lineBreakIndex+1, lineBreak.length());
			indentLength = getLengthInColumns(indent, 0, tabStopGap);
		}
		int lineLength = 0;
		int lineStart = 0;
		int wStart = -1;
		int wEnd = -1;
		int end = msg.length();
		for(int i=0; i<end; ++i){
			char c = msg.charAt(i);
			if(c == '\t'){
				int tabLen = tabStopGap - (lineLength % tabStopGap);
				lineLength += tabLen;
				if(wEnd == i){
					++wEnd;
				} else {
					wStart = i;
					wEnd = i+1;
				}
			} else if(c == '\n'){
				b.append(msg, lineStart, lineStart + lineLength + 1);
				lineLength = 0;
				lineStart = i+1;
				wStart = -1;
				wEnd = -1;
				continue;
			} else if(Character.isWhitespace(c)){
				++lineLength;
				if(wEnd == i){
					++wEnd;
				} else {
					wStart = i;
					wEnd = i+1;
				}
			} else {
				if(++lineLength > columns){
					//	This character can't fit into this line.
					if(wEnd == -1 || (wEnd - lineStart)*3 < columns){
						//	Break it here, right before this character
						b.append(msg,lineStart,i).append(lineBreak);
						lineStart = i;
					} else {
						b.append(msg,lineStart,wStart).append(lineBreak);
						lineStart = wEnd;
					}
					lineLength = indentLength;
					wStart = -1;
					wEnd = -1;
				}
			}
		}
		if(wStart == -1){
			b.append(msg, lineStart, msg.length());
		} else {
			b.append(msg, lineStart, wStart);
		}
		return b.toString();
	}
	
	private static int getLengthInColumns(CharSequence str, int startColumn, int tabStopGap) {
		int columns = 0;
		int end = str.length();
		for(int i=0; i<end; ++i){
			char c = str.charAt(i);
			if(c == '\t'){
				int tabLen = tabStopGap - (columns + startColumn) % tabStopGap;
				columns += tabLen;
			} else {
				++columns;
			}
		}
		return columns;
	}

	public static String arrayToString(double[] array,
			int fromIndex, int toIndex, String separator, NumberFormat format) {
		if(fromIndex == toIndex) return "";
		if(toIndex < fromIndex) return arrayToString(array, toIndex, 
				fromIndex, separator, format);
		StringBuilder builder = new StringBuilder();
		
		for(int i=fromIndex; i<toIndex; ++i){
			builder.append(format.format(array[i])).append(separator);
		}
		builder.setLength(builder.length() - separator.length());
		return builder.toString();
	}
	
	public static String arrayToString(double[] array, int fromIndex, int toIndex, 
			String separator) {
		return arrayToString(array, fromIndex, toIndex, separator, NumberFormat.getInstance());
	}
	
	public static String arrayToString(double[] array, String separator, NumberFormat format) {
		return arrayToString(array, 0, array.length, separator, format);
	}
	
	public static String arrayToString(double[] array, String separator) {
		return arrayToString(array, 0, array.length, separator, NumberFormat.getInstance());
	}
	
	public static String arrayToString(double[] array, int fromIndex, 
			int toIndex, NumberFormat format) {
		return arrayToString(array, fromIndex, toIndex, ",", format);
	}
	
	public static String arrayToString(double[] array, int fromIndex, int toIndex) {
		return arrayToString(array, fromIndex, toIndex, ",", NumberFormat.getInstance());
	}
	
	public static String arrayToString(double[] array, NumberFormat format) {
		return arrayToString(array, 0, array.length, ",", format);
	}
	
	public static String arrayToString(double[] array) {
		return arrayToString(array, 0, array.length, ",", NumberFormat.getInstance());
	}
	
	public static String dumpTableModelMonospaced(TableModel model){
		return dumpTableModelMonospaced(model, NumberFormat.getInstance());
	}
	public static String dumpTableModelMonospaced(TableModel model, NumberFormat numFormat){
		int rows = model.getRowCount();
		int cols = model.getColumnCount();
		
		int[] colSize = new int[cols];
		
		for(int c=0; c<cols; ++c){
			String name = model.getColumnName(c);
			if(name != null) colSize[c] = name.length();
			for(int r=0; r<rows; ++r){
				Object value = model.getValueAt(r, c);
				if(value != null){
					String str;
					if(value instanceof Double || value instanceof Float){
						str = numFormat.format((Number)value);
					} else {
						str = value.toString();
					}
					colSize[c] = Math.max(colSize[c], str.length());
				}
			}
		}
		
		StringBuilder b = new StringBuilder();
		
		for(int c=0; c<cols; ++c){
			String name = model.getColumnName(c);
			int len;
			if(name == null){
				len = 0;
			} else {
				len = name.length();
				b.append(name);
			}
			if(c+1 < cols){
				b.append(StringUtil.repeatSeq(' ', colSize[c]-len+1));
			}
		}
		for(int r=0; r<rows; ++r){
			b.append('\n');
			for(int c=0; c<cols; ++c){
				if(c>0) 
					b.append(' ');
				Object value = model.getValueAt(r, c);
				boolean leftAlign;
				String str;
				if(value == null){
					str = "";
					leftAlign = true;
				} else if(value instanceof Double || value instanceof Float){
					str = numFormat.format((Number)value);
					leftAlign = false;
				} else {
					str = value.toString();
					leftAlign = !(value instanceof Number);
				}
				if(!leftAlign){
					b.append(StringUtil.repeatSeq(' ', colSize[c] - str.length()));
				}
				b.append(str);
				if(leftAlign && (c+1 < cols)){
					b.append(StringUtil.repeatSeq(' ', colSize[c] - str.length()));
				}
			}
		}
		
		return b.toString();
	}

	public static String dumpTableModel(TableModel model, NumberFormat numFormat) {
		StringBuilder b = new StringBuilder();
		int rows = model.getRowCount();
		int cols = model.getColumnCount();
		
		for(int c=0; c<cols; ++c){
			if(c>0) b.append('\t');
			b.append(model.getColumnName(c));
		}
		for(int r=0; r<rows; ++r){
			b.append('\n');
			for(int c=0; c<cols; ++c){
				if(c>0) 
					b.append('\t');
				Object value = model.getValueAt(r, c);
				if(value instanceof Double || value instanceof Float){
					b.append(numFormat.format((Number)value));
				} else {
					b.append(value);
				}
			}
		}
		
		return b.toString();
	}
	public static String dumpTableModel(TableModel model) {		
		return dumpTableModel(model, NumberFormat.getInstance());
	}


	public static String arrayToString(float[] elements) {
		return arrayToString(elements, 0, elements.length);
	}
	
	/**
	 * @param elements
	 * @param i
	 * @param size
	 * @return
	 */
	public static String arrayToString(float[] elements, int fromIndex, int toIndex) {
		if(fromIndex == toIndex) return EMPTY_ARRAY;
		if(toIndex < fromIndex) return arrayToString(elements, toIndex, fromIndex);
		StringBuilder builder = new StringBuilder().append(ARRAY_OPEN);
		
		for(int i=fromIndex; i<toIndex; ++i){
			builder.append(elements[i]).append(ARRAY_COMMA);
		}
		builder.setLength(builder.length()-2);
		builder.append(ARRAY_CLOSE);
		return builder.toString();
	}
	
	/**
	 * Pads the specified string to a specified length by adding spaces
	 * to its left end.
	 * @param string
	 * 		The string to pad
	 * @param length
	 * 		The target length
	 * @return
	 * 		The padded string
	 */
	public static String padToLeft(String string, int length) {
		return padLeft(string, length - string.length());
	}
	

	/**
	 * Pads the specified string to a specified length by adding characters
	 * to its left end.
	 * @param string
	 * 		The string to pad
	 * @param length
	 * 		The target length
	 * @param padChar
	 * 		The character to pad the string with
	 * @return
	 * 		The padded string
	 */
	public static String padToLeft(String string, int length, char padChar) {
		return padLeft(string, length - string.length(), padChar);
	}
	
	/**
	 * Pads the specified string to a specified length by adding spaces
	 * to its right end.
	 * @param string
	 * 		The string to pad
	 * @param length
	 * 		The target length
	 * @return
	 * 		The padded string
	 */
	public static String padToRight(String string, int length) {
		return padRight(string, length - string.length());
	}
	
	/**
	 * Pads the specified string to a specified length by adding characters
	 * to its right end.
	 * @param string
	 * 		The string to pad
	 * @param length
	 * 		The target length
	 * @param padChar
	 * 		The character to pad the string with
	 * @return
	 * 		The padded string
	 */
	public static String padToRight(String string, int length, char padChar) {
		return padRight(string, length - string.length(), padChar);
	}


	public static String arrayToString(boolean[] elements) {
		return arrayToString(elements, 0, elements.length);
	}

	public static String arrayToString(boolean[] elements, int fromIndex, int toIndex) {
		return arrayToString(elements, fromIndex, toIndex, "true", "false");
	}
	
	public static String arrayToString(boolean[] elements, String trueStr,
			String falseStr) {
		return arrayToString(elements, 0, elements.length, trueStr, falseStr);
	}
	public static String arrayToString(boolean[] elements, int fromIndex, int toIndex,
			String trueStr, String falseStr) {
		if(fromIndex == toIndex) return EMPTY_ARRAY;
		if(toIndex < fromIndex) return arrayToString(elements, toIndex, fromIndex, trueStr, falseStr);
		StringBuilder builder = new StringBuilder().append(ARRAY_OPEN);
		
		for(int i=fromIndex; i<toIndex; ++i){
			builder.append(elements[i] ? trueStr : falseStr).append(ARRAY_COMMA);
		}
		builder.setLength(builder.length()-2);
		builder.append(ARRAY_CLOSE);
		return builder.toString();
	}

	public static String dumpTableContents(TableModel table,
			NumberFormat doubleFormat) {
		StringBuilder b = new StringBuilder();
		int rows = table.getRowCount();
		int cols = table.getColumnCount();
		for(int c=0; c<cols; ++c){
			b.append(table.getColumnName(c)).append('\t');
		}
		for(int r=0; r<rows; ++r){
			b.append('\n');
			for(int c=0; c<cols; ++c){
				Object value = table.getValueAt(r, c);
				if(value instanceof Double || value instanceof Float){
					b.append(doubleFormat.format(value)).append('\t');
				} else {
					b.append(value).append('\t');
				}
			}
			b.setLength(b.length()-1);
		}
		return b.toString();
	}
	
	public static String dumpTableContents(TableModel table) {
		StringBuilder b = new StringBuilder();
		int rows = table.getRowCount();
		int cols = table.getColumnCount();
		for(int c=0; c<cols; ++c){
			b.append(table.getColumnName(c)).append('\t');
		}
		for(int r=0; r<rows; ++r){
			b.append('\n');
			for(int c=0; c<cols; ++c){
				b.append(table.getValueAt(r, c)).append('\t');
			}
			b.setLength(b.length()-1);
		}
		return b.toString();
	}

	public static String arrayToString(char[] letters) {
		return arrayToString(letters, 0, letters.length);
	}


	/**
	 * @param elements
	 * @param i
	 * @param size
	 * @return
	 */
	public static String arrayToString(char[] elements, int fromIndex, int toIndex) {
		if(fromIndex == toIndex) return "{}";
		if(toIndex < fromIndex) return arrayToString(elements, toIndex, fromIndex);
		StringBuilder builder = new StringBuilder().append('{');
		
		for(int i=fromIndex; i<toIndex; ++i){
			builder.append(elements[i]).append(ARRAY_COMMA);
		}
		builder.setLength(builder.length()-2);
		builder.append('}');
		return builder.toString();
	}
	
	public static String generateGibberish(int numWords, Random rand) {
		return generateGibberish(numWords, rand, false);
	}

	public static String generateGibberish(int numWords, Random rand, boolean punctuation) {
		if(numWords == 0) return "";
		final String[] prefixes = "str th sh r v k s l p kr n pl pr m".split(" ");
		final String[] vowels = "aw ah ee e ih ay ai oh".split(" ");
		final String[] suffixes = "z x n nd d ch ck cks rd rt st tch ng".split(" ");
		StringBuilder b = new StringBuilder();
		while(--numWords >= 0){
			int syllables = rand.nextInt(4) + 1;
			while(--syllables >= 0){
				b.append(prefixes[rand.nextInt(prefixes.length)])
				.append(vowels[rand.nextInt(vowels.length)]);
			}
			b.append(suffixes[rand.nextInt(suffixes.length)]);
			if(punctuation && numWords > 0 && rand.nextInt(9) == 0){
				b.append(", ");
			} else {
				b.append(' ');
			}
		}
		b.setLength(b.length()-1);
		if(punctuation)
			b.append(". ");
		return b.toString();
	}

	/**
	 * Reduces all sequences of whitespace in the string to single spaces.
	 * @param str
	 * 		the string to reduce
	 * @return
	 * 		the reduced string
	 */
	public static String reduceAllWhitespace(String str) {
		char[] buf = str.toCharArray();
		boolean lastWasWhite = false;
		int o=0;
		for(int i=0; i<buf.length; ++i){
			if(Character.isWhitespace(buf[i])){
				if(!lastWasWhite){
					buf[o++] = ' ';
					lastWasWhite = true;
				}
			} else {
				buf[o++] = buf[i];
				lastWasWhite = false;
			}
		}
		return String.valueOf(buf, 0, o);
	}

	public static int count(String str, char c) {
		int count = 0;
		int end = str.length();
		for(int i=0; i<end; ++i){
			if(str.charAt(i) == c)
				++count;
		}
		return count;
	}
	
	public static int count(String str, char c, int start, int end) {
		int count = 0;
		for(int i=start; i<end; ++i){
			if(str.charAt(i) == c)
				++count;
		}
		return count;
	}
	
	public static CharSequence repeatSeq(final char c, final int count) {
		return new RepeatCharSequence(c, count);
		
	}
	
	public static CharSequence repeatSeq(final String str, final int count) {
		return new CharSequence(){

			@Override
			public int length() {
				return str.length() * count;
			}

			@Override
			public char charAt(int index) {
				return str.charAt(index % str.length());
			}

			@Override
			public CharSequence subSequence(int start, int end) {
				ExceptionUtil.checkRangeLess(start, end, length());
				return new SubSequence(this, start, end);
			}
		};
	}
	
	public static String repeat(String str, int count) {
		if(count <= 0) return "";
		if(count == 1) return str;
		StringBuilder b = new StringBuilder();
		while(--count >= 0){
			b.append(str);
		}
		return b.toString();
	}
	
	public static String repeat(char c, int count) {
		if(count <= 0) return "";
		char[] buff = new char[count];
		Arrays.fill(buff, c);
		return String.valueOf(buff);
	}

	public static int indexOfWhitespace(CharSequence str) {
		return indexOfWhitespace(str, 0);
	}

	private static int indexOfWhitespace(CharSequence str, int start) {
		int end = str.length();
		for(int i=start; i<end; ++i){
			if(Character.isWhitespace(str.charAt(i)))
				return i;
		}
		return -1;
	}

	/**
	 * Tests a string to see if it contains no non-whitespace characters.
	 * Will return true for an empty string -- that is to say, no whitespace 
	 * characters are required, only no non-whitespace characters.
	 * @param str
	 * 		the string to test
	 * @return
	 * 		true if the string contains no non-whitespace characters, false
	 * if it contains non-whitespace characters
	 */
	public static boolean containsOnlyWhitespace(String str) {
		int end = str.length();
		for(int i=0; i<end; ++i){
			char c = str.charAt(i);
			if(!Character.isWhitespace(c)){
				return false;
			}
		}
		return true;
	}

	/**
	 * Functions similarly to {@linkplain vordeka.util.GeneralUtil#equal(Object,Object)}
	 * except that it takes in a boolean value that allows case-sensitivity to be
	 * switched on or off.
	 * <p>
	 * In essence, it's simply a convenience method that checks if the arguments are
	 * null before calling methods on them.
	 * @param str1
	 * 		the first string to be compared
	 * @param str2
	 * 		the second string to be compared
	 * @param ignoreCase
	 * 		if true, {@linkplain String#equalsIgnoreCase} is used instead of 
	 * {@linkplain String#equals}
	 * @return
	 * 		true if the arguments compare as equal, false otherwise
	 */
	public static boolean equal(String str1, String str2,
			boolean ignoreCase) {
		if (str1 == null) {
			return str2 == null;
		} else if (ignoreCase) {
			return str1.equalsIgnoreCase(str2);
		} else {
			return str1.equals(str2);
		}
	}

	
}
