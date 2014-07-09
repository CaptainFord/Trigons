package vordeka.util.string;

import vordeka.util.ExceptionUtil;
import vordeka.util.StringUtil;

public class RepeatCharSequence implements CharSequence {
	private char c;
	private int count;

	public RepeatCharSequence(char c, int count) {
		this.c = c;
		this.count = count;
	}

	@Override
	public int length() {
		return count;
	}

	@Override
	public char charAt(int index) {
		ExceptionUtil.checkIndexLess(index, count);
		return c;
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		ExceptionUtil.checkRangeLess(start, end, length());
		return new RepeatCharSequence(c, end-start);
	}
	
	public String toString() {
		return StringUtil.repeat(c, count);
	}

}
