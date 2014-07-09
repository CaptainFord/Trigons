package trigons.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JComponent;

import trigons.puzzle.TSide;
import vordeka.util.model.GridPoint;

public class JTrigon extends JComponent {
	private static final long serialVersionUID = -368950055413952840L;
	static final int H_PADDING = 2, V_PADDING = 1;
	static final int SELECTION_PADDING = 2;
	static final int HOVER_HIGHLIGHT_THICKNESS = 4;
	
	protected List<TrigonSelectionListener> selListeners = new CopyOnWriteArrayList<TrigonSelectionListener>(); 
	protected Handler handler = new Handler();
	protected InputHandler inputHandler = new InputHandler();

	protected TrigonModel model;
	protected GridPoint hoveredTriangle;
	protected GridPoint selectedTriangle;
	protected GridBorder selectedBorder;
	protected GridBorder hoveredBorder;
	
	protected int triWidth = 60, triHeight = 52;
	
	protected boolean canSelectTriangles;
	protected boolean canSelectBorders;
	protected boolean highlightEmptyTriangles = true;
	
	protected TrigonRenderer renderer = new DefaultTrigonRenderer();
	
	protected int borderValWidth, borderValHeight;
	
	protected Color 
			selectionBackground = Color.lightGray, 
			focusBackground = Color.cyan,
			gridColor = Color.black, 
			selectionGridColor = Color.black, 
			hoverHighlightColor = Color.lightGray,
			borderValColor = new Color(0,0xBF,0),
			borderEraseColor = new Color(255, 255, 255, 192);
	private String selectedText;
	
	
	public JTrigon(){
		this.addMouseListener(inputHandler);
		this.addMouseMotionListener(inputHandler);
		this.setBackground(Color.white);
		this.setFocusable(true);
	}
	
	public JTrigon(TrigonModel model){
		this();
		setModel(model);
	}
	
	public void addSelectionListener(TrigonSelectionListener l){
		this.selListeners.add(l);
	}
	public void removeSelectionListener(TrigonSelectionListener l){
		this.selListeners.remove(l);
	}

	public Triangle getTriangle(int x, int y){
		return getTriangle(x, y, 0);
	}
	
	public Triangle getTriangle(int x, int y, Triangle t){
		return getTriangle(x, y, 0, t);
	}
	
	public Triangle getTriangle(int x, int y, int padding){
		boolean pointsUp = TrigonUtil.doesTrianglePointUp(x, y);
		x += H_PADDING - model.getMinX();
		y += V_PADDING - model.getMinY();
		return Triangle.getTri((x * triWidth) / 2 - padding, 
				y * triHeight - padding, 
				triWidth + padding + padding, 
				triHeight + padding + padding, pointsUp);
	}
	
	public Triangle getTriangle(int x, int y, int padding, Triangle t){
		if(t == null) t = new Triangle();
		boolean pointsUp = TrigonUtil.doesTrianglePointUp(x, y);
		x += H_PADDING - model.getMinX();
		y += V_PADDING - model.getMinY();
		t.setTri((x * triWidth) / 2 - padding, 
				y * triHeight - padding + (pointsUp ? -padding : padding) / 2, 
				triWidth + padding + padding, 
				triHeight + padding + padding, 
				pointsUp);
		return t;
	}
	
	public Rectangle getTrigonBounds(int x, int y, Rectangle r) {
		if(r == null) r = new Rectangle();
		x += H_PADDING - model.getMinX();
		y += V_PADDING - model.getMinY();
		r.setBounds((x * triWidth) / 2 - SELECTION_PADDING, 
				y * triHeight - SELECTION_PADDING, 
				triWidth + SELECTION_PADDING + SELECTION_PADDING, 
				triHeight + SELECTION_PADDING + SELECTION_PADDING);
		return r;
	}
	
	public Rectangle getTrigonBounds(int x, int y) {
		x += H_PADDING - model.getMinX();
		y += V_PADDING - model.getMinY();
		return new Rectangle((x * triWidth) / 2 - SELECTION_PADDING, 
				y * triHeight - SELECTION_PADDING, 
				triWidth + SELECTION_PADDING + SELECTION_PADDING, 
				triHeight + SELECTION_PADDING + SELECTION_PADDING);
	}
	
	public int getTrigonYAt(int y){
		return y / triHeight + model.getMinX() - V_PADDING;
	}
	
	public GridPoint getTrigonAt(int x, int y){
		//	Well, the row is straightforward, at least.
		int triX = 2 * x / triWidth + model.getMinX() - H_PADDING;
		int triY = y / triHeight + model.getMinY() - V_PADDING;
		Triangle t = getTriangle(triX, triY);
		if(!t.contains(x, y)){
			if(x > t.xpoints[0]){
				++triX;
			} else if(x < t.xpoints[0]){
				--triX;
			}
		}
		return new GridPoint(triX, triY);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Rectangle clip = g.getClipBounds();
		if(clip == null) clip = this.getBounds();
		boolean hasFocus = this.isFocusOwner();
		
		GridPoint p = getTrigonAt(clip.x, clip.y);
		int minTrigonX = p.x - 1;
		int minTrigonY = p.y - 1;
		p = getTrigonAt(clip.x + clip.width - 1, clip.y + clip.height - 1);
		int maxTrigonX = p.x + 1;
		int maxTrigonY = p.y + 1;
		
		g.setColor(getBackground());
		g.fillRect(clip.x, clip.y, clip.width, clip.height);
		g.setColor(gridColor);
		
		Triangle t = null;
		FontMetrics fm = g.getFontMetrics();
		double fontHeight = fm.getStringBounds("18", g).getHeight();
		int upBaseline = (int)((triHeight * 2 + fontHeight) / 3.0);
		int downBaseline = (int)((triHeight + fontHeight) / 3.0) - triHeight;
		
		if(hoveredTriangle != null){
			int triX = hoveredTriangle.x, triY = hoveredTriangle.y;
			t = this.getTriangle(triX, triY, t);
			g.setColor(hoverHighlightColor);
			g.fillPolygon(t);
			t = this.getTriangle(triX, triY, - HOVER_HIGHLIGHT_THICKNESS, t);
			g.setColor(getBackground());
			g.fillPolygon(t);
			g.setColor(gridColor);
		}
		
		//	I believe this paints the triangles
		for(int triX=minTrigonX; triX <= maxTrigonX; ++triX){
			for(int triY=minTrigonY; triY <= maxTrigonY; ++triY){
				int value = model.getTriValueAt(triX, triY);
				boolean selected = this.selectedTriangle != null && 
						triX == this.selectedTriangle.x && triY == this.selectedTriangle.y;
				boolean hovered = this.hoveredTriangle != null && 
						triX == this.hoveredTriangle.x && triY == this.hoveredTriangle.y;
				boolean noTriangle = !model.hasTriangleAt(triX, triY);
				if(noTriangle && !selected)
					continue;

				if(selected){
					t = this.getTriangle(triX, triY, SELECTION_PADDING, t);
					g.setColor(selectionGridColor);
					g.fillPolygon(t);
					t = this.getTriangle(triX, triY, - SELECTION_PADDING - 2, t);
					g.setColor(hasFocus ? focusBackground : selectionBackground);
					g.fillPolygon(t);
					g.setColor(gridColor);
					t = this.getTriangle(triX, triY, t);
				} else if(hovered){
					t = this.getTriangle(triX, triY, t);
					g.drawPolygon(t);
				} else {
					t = this.getTriangle(triX, triY, t);
					g.drawPolygon(t);
				}
				
				if(value < 0 && !(selected && selectedText != null)){
					
				} else {
					String str = (selected && selectedText != null) ? selectedText : Integer.toString(value);
					double strWidth = fm.getStringBounds(str, g).getWidth();
					g.drawString(str, (int)(t.xpoints[0] - 0.5 * strWidth), 
							t.ypoints[0] + (TrigonUtil.doesTrianglePointUp(triX, triY) 
									? upBaseline : downBaseline));
				}
			}
		}
		TSide[] sides = TSide.values();
		Rectangle r = new Rectangle();
		//	Now draw the borders. Somehow, I need to make sure that each border is only drawn once. (Do I?)
		{
			Rectangle2D strBounds = fm.getStringBounds("0", g);
			borderValWidth = (int)Math.ceil(strBounds.getWidth()) + 4;
			borderValHeight = (int)Math.ceil(strBounds.getHeight()) + 4;
		}
		
		//	And I believe this draws the borders (yep)
		for(int triX=minTrigonX; triX <= maxTrigonX; ++triX){
			for(int triY=minTrigonY; triY <= maxTrigonY; ++triY){
				for(TSide side : sides){
					int value = model.getBorderValueAt(triX, triY, side);
					boolean pointsUp = TrigonUtil.doesTrianglePointUp(triX, triY);
					

					if(value == -2){
						//	This border doesn't exist and should be skipped
						//	Selected/Hovered doesn't matter. You can't create borders the
						//	way you create trigons, so nonexistant borders don't matter.
						continue;
					}
						
					//	These two blocks prevent a border from being drawn more than once
					if(side.isLeft() && model.getTriValueAt(triX, triY) >= 0){
						continue;
					}
					if(side.isVertical() && !pointsUp && model.getTriValueAt(triX, triY-1) >= 0){
						continue;
					}
					
					boolean selected = this.selectedBorder != null && selectedBorder.sameAs(triX, triY, side);
					boolean hovered = this.hoveredBorder != null && hoveredBorder.sameAs(triX, triY, side);
					
					
					t = this.getTriangle(triX, triY, t);
					
					int baseX, baseY;
					switch(side){
					case LEFT:
						baseX = (t.getLeft() * 3 + t.getRight()) / 4;
						baseY = t.getMiddleY();
						break;
					case RIGHT:
						baseX = (t.getLeft() + 3 * t.getRight()) / 4;
						baseY = t.getMiddleY();
						break;
					case VERTICAL:
						baseX = t.getMiddleX();
						baseY = pointsUp ? t.getMaxY() : t.getMinY();
						break;
					default:
						throw new IllegalStateException("Unknown side type: " + side);
					}
					if(selected && selectedText != null){
						Rectangle2D strBounds = fm.getStringBounds(selectedText, g);
						int tempWidth = (int)Math.ceil(strBounds.getWidth()) + 4;
						int tempHeight = (int)Math.ceil(strBounds.getHeight()) + 4;
						r.setBounds(baseX - tempWidth / 2, baseY - tempHeight / 2, tempWidth, tempHeight);
					} else {
						r.setBounds(baseX - borderValWidth / 2, baseY - borderValHeight / 2, borderValWidth, borderValHeight);
					}
					
					Color backColor = borderEraseColor;
					Color borderColor = null;
					if(selected){
						backColor  = hasFocus ? focusBackground : selectionBackground;
						borderColor = Color.black;
					} else if(hovered) {
						borderColor = hoverHighlightColor;
					}
					g.setColor(backColor);
					g.fillRect(r.x, r.y, r.width, r.height);
					if(borderColor != null){
						g.setColor(borderColor);
						g.drawRect(r.x, r.y, r.width, r.height);
					}
					g.setColor(selected ? Color.black : borderValColor);
					
					
					if(value >= 0){
						String str = (selected && selectedText != null) ? selectedText : Integer.toString(value);
						double strWidth = fm.getStringBounds(str, g).getWidth();
						g.drawString(str, (int)(baseX - 0.333 * strWidth), 
								 (int) (baseY + fontHeight * 0.333));
					}
				}
			}
		}
	}

	@Override
	public Dimension getPreferredSize() {
		//	Adds additional padding for 1 extra row on each side. That means
		//	two triangles horizontally, and 1 triangle vertically.
		//	Also adds in the extra 1/2 for the width, 
		//	caused by the overlap of triangles in the same row.
		int w, h;
		if(model == null){
			w = H_PADDING + H_PADDING + 14;
			h = V_PADDING + V_PADDING + 13;
		} else {
			w = model.getMaxX() - model.getMinX() + H_PADDING + H_PADDING + 2;
			h = model.getMaxY() - model.getMinY() + V_PADDING + V_PADDING + 1;
		}
		return new Dimension((w * triWidth) / 2, h * triHeight);
	}

	public void repaintBorder(GridBorder border){
		repaintBorder(border.x, border.y, border.side);
	}
	public void repaintBorder(int x, int y, TSide side){
		repaint(getTrigonBorderBounds(x, y, side));
	}
	public void repaintTriangle(GridPoint p){
		repaintTriangle(p.x, p.y);
	}
	public void repaintTriangle(int x, int y){
		repaint(getTrigonBounds(x, y));
	}
	
	class Handler implements TrigonModelListener {

		@Override
		public void trigonBorderValueChanged(TrigonBorderEvent evt) {
			Rectangle r0 = getTrigonBounds(evt.getFirstX(), evt.getFirstY());
			Rectangle r1 = getTrigonBounds(evt.getSecondX(), evt.getSecondY());
			r0.add(r1);
			repaint(r0);
		}

		@Override
		public void trigonValueChanged(TrigonModel model, int x, int y) {
			repaintTriangle(x, y);
//			repaint(getTrigonBounds(x, y));
		}

		@Override
		public void trigonPuzzleBoundsChanged(TrigonModel model) {
			JTrigon.this.revalidate();
			JTrigon.this.repaint();
		}
		
	}
	
	class InputHandler extends MouseAdapter {		
		@Override
		public void mousePressed(MouseEvent e) {
			requestFocus();
			GridBorder b = getTrigonBorderAt(e.getX(), e.getY());
			if(b != null && model.getBorderValueAt(b.x, b.y, b.side) != -2){
				setSelectedBorder(b);
				setSelectedTrigon(null);
			} else {
				GridPoint g = getTrigonAt(e.getX(), e.getY());
				setSelectedTrigon(g);
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			mouseMoved(e);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			setHoveredTrigon(null);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			GridBorder b = getTrigonBorderAt(e.getX(), e.getY());
			if(b != null && model.getBorderValueAt(b.x, b.y, b.side) != -2){
				setHoveredBorder(b);
				setHoveredTrigon(null);
			} else {
				GridPoint g = getTrigonAt(e.getX(), e.getY());
				if(highlightEmptyTriangles || model.hasTriangleAt(g.x, g.y)){
					setHoveredTrigon(g);
				} else {
					setHoveredTrigon(null);
				}
			}
		}
	}

	public void setHoveredTrigon(GridPoint g) {
		if(this.hoveredTriangle == g || (g != null && g.equals(this.hoveredTriangle)))
			return;
		GridPoint old = this.hoveredTriangle;
		GridBorder oldBorder = this.hoveredBorder;
		this.hoveredTriangle = g;
		this.hoveredBorder = null;
		this.firePropertyChange("hoveredTrigon", old, g);
		this.firePropertyChange("hoveredBorder", oldBorder, null);
		if(old != null){
			repaint(getTrigonBounds(old.x, old.y));
		}
		if(oldBorder != null){
			repaint(getTrigonBorderBounds(oldBorder.x, oldBorder.y, oldBorder.side));
		}
		if(g != null){
			repaint(getTrigonBounds(g.x, g.y));
		}
	}
	
	public void setHoveredBorder(GridBorder b) {
		if(this.hoveredBorder == b || (b != null && b.sameAs(this.hoveredBorder)))
			return;
		GridPoint old = this.hoveredTriangle;
		GridBorder oldBorder = this.hoveredBorder;
		this.hoveredTriangle = null;
		this.hoveredBorder = b;
		this.firePropertyChange("hoveredTrigon", old, null);
		this.firePropertyChange("hoveredBorder", oldBorder, b);
		if(old != null){
			repaint(getTrigonBounds(old.x, old.y));
		}
		if(oldBorder != null){
			repaint(getTrigonBorderBounds(oldBorder.x, oldBorder.y, oldBorder.side));
		}
		if(b != null){
			repaint(getTrigonBorderBounds(b.x, b.y, b.side));
		}
	}
	

	public GridBorder getTrigonBorderAt(int x, int y) {
		GridPoint p = this.getTrigonAt(x, y);
		if(getTrigonBorderBounds(p.x, p.y, TSide.LEFT).contains(x, y))
			return new GridBorder(p.x, p.y, TSide.LEFT);
		if(getTrigonBorderBounds(p.x, p.y, TSide.RIGHT).contains(x, y))
			return new GridBorder(p.x, p.y, TSide.RIGHT);
		if(getTrigonBorderBounds(p.x, p.y, TSide.VERT).contains(x, y))
			return new GridBorder(p.x, p.y, TSide.VERT);
		return null;
	}
	
	public Rectangle getTrigonBorderBounds(int x, int y, TSide side) {
		// TODO Auto-generated method stub
		Triangle t = getTriangle(x, y);
		boolean pointsUp = TrigonUtil.doesTrianglePointUp(x, y);
		int baseX, baseY;
		switch(side){
		case LEFT:
			baseX = (t.getLeft() * 3 + t.getRight()) / 4;
			baseY = t.getMiddleY();
			break;
		case RIGHT:
			baseX = (t.getLeft() + 3 * t.getRight()) / 4;
			baseY = t.getMiddleY();
			break;
		case VERTICAL:
			baseX = t.getMiddleX();
			baseY = pointsUp ? t.getMaxY() : t.getMinY();
			break;
		default:
			throw new IllegalStateException("Unknown side type: " + side);
		}
//		int borderValWidth = triWidth / 4;
//		int borderValHeight = triHeight / 3;
		return new Rectangle(baseX - borderValWidth / 2, baseY - borderValHeight / 2, borderValWidth+1, borderValHeight+1);
	}

	public GridPoint getSelectedTrigon() {
		return selectedTriangle == null ? null : new GridPoint(selectedTriangle);
	}
	public GridBorder getSelectedBorder() {
		return selectedBorder == null ? null :  new GridBorder(selectedBorder);
	}
	
	public void setSelectedTrigon(int x, int y) {
		setSelectedTrigon(new GridPoint(x, y));
	}
	public void setSelectedTrigon(GridPoint g) {
		if(this.selectedTriangle == g || (g != null && g.equals(this.selectedTriangle)))
			return;
		GridPoint old = this.selectedTriangle;
		GridBorder oldBorder = this.selectedBorder;
		this.selectedTriangle = g;
		this.selectedBorder = null;
		this.firePropertyChange("selectedTrigon", old, g);
		this.firePropertyChange("selectedBorder", oldBorder, null);
		this.fireSelectionChanged();
		if(old != null){
			repaint(getTrigonBounds(old.x, old.y));
		}
		if(oldBorder != null){
			repaint(getTrigonBorderBounds(oldBorder.x, oldBorder.y, oldBorder.side));
		}
		if(g != null){
			repaint(getTrigonBounds(g.x, g.y));
		}
	}
	
	public void setSelectedBorder(int x, int y, TSide side) {
		if(side == null) throw new IllegalArgumentException("side cannot be null");
		setSelectedBorder(new GridBorder(x, y, side));
	}
	public void setSelectedBorder(GridBorder b) {
		if(this.selectedBorder == b || (b != null && b.sameAs(this.selectedBorder)))
			return;
		GridPoint old = this.selectedTriangle;
		GridBorder oldBorder = this.selectedBorder;
		this.selectedTriangle = null;
		this.selectedBorder = b;
		this.firePropertyChange("selectedTriangle", old, null);
		this.firePropertyChange("selectedBorder", oldBorder, b);
		this.fireSelectionChanged();
		if(old != null){
			repaint(getTrigonBounds(old.x, old.y));
		}
		if(oldBorder != null){
			repaint(getTrigonBorderBounds(oldBorder.x, oldBorder.y, oldBorder.side));
		}
		if(b != null){
			repaint(getTrigonBorderBounds(b.x, b.y, b.side));
		}
	}

	//	*****************
	//	Simple Properties
	//	*****************
	
	private void fireSelectionChanged() {
		for(TrigonSelectionListener l : selListeners){
			l.selectionChanged(this);
		}
	}

	public boolean canSelectTriangles() {
		return canSelectTriangles;
	}
	public boolean canSelectBorders() {
		return canSelectBorders;
	}
	
	public boolean isCanSelectTriangles() {
		return canSelectTriangles;
	}
	public boolean isCanSelectBorders() {
		return canSelectBorders;
	}

	public void setCanSelectTriangles(boolean canSelectTriangles) {
		if(this.canSelectTriangles == canSelectTriangles) return;
		this.canSelectTriangles = canSelectTriangles;
		this.firePropertyChange("canSelectTriangles", !canSelectTriangles, canSelectTriangles);
	}	
	public void setCanSelectBorders(boolean canSelectBorders) {
		if(this.canSelectBorders == canSelectBorders) return;
		this.canSelectBorders = canSelectBorders;
		this.firePropertyChange("canSelectBorders", !canSelectBorders, canSelectBorders);
	}

	public boolean isHighlightEmptyTriangles() {
		return highlightEmptyTriangles;
	}
	public void setHighlightEmptyTriangles(boolean highlightEmptyTriangles) {
		if(this.highlightEmptyTriangles == highlightEmptyTriangles) return;
		this.highlightEmptyTriangles = highlightEmptyTriangles;
		this.firePropertyChange("highlightEmptyTriangles", !highlightEmptyTriangles, highlightEmptyTriangles);
	}
	
//	public Color getBackground(){
//		return background;
//	}
//	public void setBackground(Color background){
//		if(this.background == background) return;
//		Color old = this.background;
//		this.background = background;
//		firePropertyChange("background", old, background);
//	}

	public Color getSelectionBackground(){
		return selectionBackground;
	}
	public void setSelectionBackground(Color selectionBackground){
		if(this.selectionBackground == selectionBackground) return;
		Color old = this.selectionBackground;
		this.selectionBackground = selectionBackground;
		firePropertyChange("selectionBackground", old, selectionBackground);
	}

	public Color getGridColor(){
		return gridColor;
	}
	public void setGridColor(Color gridColor){
		if(this.gridColor == gridColor) return;
		Color old = this.gridColor;
		this.gridColor = gridColor;
		firePropertyChange("gridColor", old, gridColor);
	}

	public Color getSelectionGridColor(){
		return selectionGridColor;
	}
	public void setSelectionGridColor(Color selectionGridColor){
		if(this.selectionGridColor == selectionGridColor) return;
		Color old = this.selectionGridColor;
		this.selectionGridColor = selectionGridColor;
		firePropertyChange("selectionGridColor", old, selectionGridColor);
	}

	public Color getHoverHighlightColor(){
		return hoverHighlightColor;
	}
	public void setHoverHighlightColor(Color hoverHighlightColor){
		if(this.hoverHighlightColor == hoverHighlightColor) return;
		Color old = this.hoverHighlightColor;
		this.hoverHighlightColor = hoverHighlightColor;
		firePropertyChange("hoverHighlightColor", old, hoverHighlightColor);
	}

	public TrigonRenderer getRenderer() {
		return renderer;
	}
	public void setRenderer(TrigonRenderer renderer) {
		if(renderer == this.renderer) return;
		if(renderer == null) throw new IllegalArgumentException("renderer may not be null");
		TrigonRenderer old = this.renderer;
		this.renderer = renderer;
		firePropertyChange("renderer", old, renderer);
	}

	
	//	*****************
	//	Less Simple Properties
	//	*****************


	public TrigonModel getModel(){
		return this.model;
	}
	
	public void setModel(TrigonModel model){
		TrigonModel old = this.model;
		if(this.model != null){
			this.model.removeTrigonListener(handler);
		}
		this.model = model;
		if(this.model != null){
			this.model.addTrigonListener(handler);
		}
		this.firePropertyChange("model", old, model);
	}
	
	public int getTriangleWidth(){
		return triWidth;
	}
	
	public int getTriangleHeight(){
		return triHeight;
	}
	
	public void setTriangleWidth(int triWidth) {
		int old = this.triWidth;
		this.triWidth = triWidth;
		this.firePropertyChange("triangleWidth", old, triWidth);
	}

	public void setTriangleHeight(int triHeight) {
		int old = this.triHeight;
		this.triHeight = triHeight;
		this.firePropertyChange("triangleHeight", old, triHeight);
	}
	
	public void setTriangleSize(int width, int height){
		setTriangleWidth(width);
		setTriangleHeight(height);
	}
	
	/**
	 * Sets the width and height of the triangles based upon the idea
	 * that the triangles are equilateral.
	 * @param sideLength
	 * 		the length of a side of one of the triangles. Exactly equals
	 * triangleWidth.
	 */
	public void setTriangleSize(int sideLength){
		setTriangleSize(sideLength, (int)(sideLength * 0.866025404 + 0.5));
	}

	public GridPoint getHoveredTrigon() {
		return hoveredTriangle;
	}
	public GridBorder getHoveredBorder() {
		return hoveredBorder;
	}

	/**
	 * Sets the text that displays instead of the value of the selected item.
	 * @param text
	 */
	public void setSelectedText(String text){
		
		this.selectedText = text;
		if(this.selectedBorder != null){
			repaintBorder(this.selectedBorder);
		}
		if(this.selectedTriangle != null){
			repaintTriangle(this.selectedTriangle);
		}
	}
	public String getSelectedText(){
		return selectedText;
	}

	
	
}
