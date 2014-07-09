package vordeka.util.string;

import vordeka.util.ExceptionUtil;

/**
 * This class is intended purely to implement the subSequence method
 * of CharSequence without having to copy the data.
 * @author Vordeka
 *
 */
public class SubSequence implements CharSequence {

	private CharSequence seq;
	private int start;
	private int end;

	public SubSequence(CharSequence seq, int start, int end) {
		this.seq = seq;
		this.start = start;
		this.end = end;
	}

	@Override
	public int length() {
		return end - start;
	}

	@Override
	public char charAt(int index) {
		return seq.charAt(start + index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		ExceptionUtil.checkRangeLess(start, end, length());
		return new SubSequence(this, start, end);
	}

}
