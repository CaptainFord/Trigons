package trigons.swing;

import java.awt.Graphics;

public interface TrigonRenderer {
	
	public void drawSelectionBackground(Graphics g, JTrigon comp, TrigonModel model, int triX, int triY, Triangle t);
	public void drawHoverBackground(Graphics g, JTrigon comp, TrigonModel model, int triX, int triY, Triangle t);
	
	public void drawGrid(Graphics g, JTrigon comp, TrigonModel model, int triX, int triY, Triangle t, boolean selected, boolean hovered);
	public void drawValue(Graphics g, JTrigon comp, TrigonModel model, int triX, int triY, Triangle t, boolean selected, boolean hovered);
	public void drawBorderValue(Graphics g, JTrigon comp, TrigonModel model, int triX, int triY, Triangle t, boolean selected, boolean hovered);
	
}
