package trigons.puzzle;

import java.util.Collection;

public interface TrigonListener {

	void trigonValueChanged(TrigonPuzzle puzzle, Trigon trigon);
	void trigonAdded(TrigonPuzzle puzzle, Trigon trigon);
	void trigonRemoved(TrigonPuzzle puzzle, Trigon trigon);

	void borderValueChanged(TrigonPuzzle puzzle, TrigonBorder border);
	void bordersAdded(TrigonPuzzle puzzle, Collection<? extends TrigonBorder> newBorders);
	void bordersRemoved(TrigonPuzzle puzzle, Collection<? extends TrigonBorder> oldBorders);
	
	void puzzleBoundsChanged(TrigonPuzzle puzzle);
}
