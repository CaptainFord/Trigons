package trigons.puzzle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrigonImpl extends Trigon {

	protected final transient TrigonPuzzle puzzle;
	protected final TriBorder borders[] = new TriBorder[3];
	protected final int x, y;
	protected int value;

	public TrigonImpl(TrigonPuzzle puzzle, int x, int y, int value) {
		this.puzzle = puzzle;
		this.x = x;
		this.y = y;
		this.value = value;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public void setValue(int value) {
		if(value < -1) value = -1;
		if(this.value == value)
			return;
		this.value = value;
		puzzle.fireTrigonChanged(this);
	}

	@Override
	public TriBorder getBorder(TSide side) {
		return borders[side.ordinal()];
	}

	public void writeBorderValues(DataOutputStream out) throws IOException {
		out.writeInt(borders[0].value);
		out.writeInt(borders[1].value);
		out.writeInt(borders[2].value);
	}

	public void readBorderValues(DataInputStream in) throws IOException {
		borders[0].value = in.readInt();
		borders[1].value = in.readInt();
		borders[2].value = in.readInt();
	}

}
