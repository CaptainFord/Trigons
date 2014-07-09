package trigons.swing;

public interface TrigonModelListener {
	void trigonBorderValueChanged(TrigonBorderEvent evt);
	void trigonValueChanged(TrigonModel model, int x, int y);
	
	void trigonPuzzleBoundsChanged(TrigonModel model);
}
