package trigons.ui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import trigons.puzzle.TrigonPuzzle;
import trigons.solver.TrigonSolverOrFiller;
import trigons.solver.TrigonValueSet.TrigonValuePermutation;
import trigons.swing.DefaultTrigonModel;
import trigons.swing.GridBorder;
import trigons.swing.JTrigon;
import vordeka.util.model.GridPoint;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1820982639612164714L;
	
	public final AppFramework framework;
	protected JToolBar toolbar = new JToolBar();
	protected JTrigon trigon;
	protected TrigonPuzzle puzzle;
	protected JLabel statusBar;

	public TrigonSolverOrFiller solver;
	
	public MainWindow() {
		this.framework = new AppFramework(this);
		initComponents();
		this.framework.finishInit();
	}

	private void initComponents() {
		toolbar = new JToolBar();
		toolbar.add(framework.editorActions.aNew);
		toolbar.add(framework.editorActions.aSave);
		toolbar.add(framework.editorActions.aLoad);
		toolbar.addSeparator();
		toolbar.add(framework.editorActions.aClear);
		toolbar.add(framework.editorActions.aFill);
		toolbar.addSeparator();
		toolbar.add(framework.editorActions.aBlank);
		toolbar.add(framework.editorActions.aSolve);
		trigon = new JTrigon();
		statusBar = new JLabel("Stuff goes here");
		statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
		addStatusBarListeners();
		
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(trigon));
		this.add(toolbar, BorderLayout.NORTH);
		this.add(statusBar, BorderLayout.SOUTH);
		
		
	}

	public TrigonPuzzle getPuzzle() {
		return puzzle;
	}

	public void setPuzzle(TrigonPuzzle puzzle) {
		if(this.puzzle == puzzle) return;
//		if(this.puzzle != null){
//			
//		}
		this.puzzle = puzzle;
		trigon.setModel(new DefaultTrigonModel(puzzle));
//		if(this.puzzle != null){
//			
//		}
	}

	private void addStatusBarListeners() {
		PropertyChangeListener l = new PropertyChangeListener(){
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				StringBuilder b = new StringBuilder();
				GridPoint st = trigon.getSelectedTrigon();
				GridBorder sb = trigon.getSelectedBorder();
				GridPoint ht = trigon.getHoveredTrigon();
				GridBorder hb = trigon.getHoveredBorder();
//				if(st != null) b.append("    Selected: " + st.x + "," + st.y);
//				if(sb != null) b.append("    Selected: " + sb.x + "," + sb.y + ":" + sb.side);
//				if(ht != null) b.append("    Hovered: " + ht.x + "," + ht.y);
//				if(hb != null) b.append("    Hovered: " + hb.x + "," + hb.y + ":" + hb.side);
				if(ht != null || hb != null){
					int triX, triY;
					if(ht != null){
						triX = ht.x;
						triY = ht.y;
					} else {
						triX = hb.x;
						triY = hb.y;
					}
					if(solver != null){
						Collection<TrigonValuePermutation> perms = solver.getPerms(triX, triY);
						b.append('(').append(triX).append(',').append(triY).append(')');
						if(perms != null){
							b.append(' ').append(perms.size()).append("-{");
							if(!perms.isEmpty()){
							for(TrigonValuePermutation perm : perms){
								b.append(perm.left).append(perm.right).append(perm.vertical);
								b.append(',');
							}
							b.setLength(b.length()-1);
							}
							b.append('}');
						}
					}
					
				}
				statusBar.setText(b.toString().trim());
			}
			
		};
		trigon.addPropertyChangeListener("hoveredTrigon", l);
		trigon.addPropertyChangeListener("selectedTrigon", l);
		trigon.addPropertyChangeListener("hoveredBorder", l);
		trigon.addPropertyChangeListener("selectedBorder", l);
	}
}
