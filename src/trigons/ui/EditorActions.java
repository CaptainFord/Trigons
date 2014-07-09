package trigons.ui;

import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import trigons.AppTrigons;
import trigons.generator.TrigonFiller;
import trigons.puzzle.TriPuzzle;
import trigons.puzzle.Trigon;
import trigons.puzzle.TrigonBorder;
import trigons.puzzle.TrigonPuzzle;
import trigons.solver.TrigonSolver;
import vordeka.util.ErrUtil;
import vordeka.util.swing.AbstractActionPlus;

public class EditorActions {
	public static final String SETTING_LAST_PUZZLE_PATH = "LastPuzzleFolder";
	
	public final AppFramework framework;
	public NewAction a;
	public SolveAction aSolve;
	public BlankAction aBlank;
	public ClearAction aClear;
	public FillAction aFill;
	public NewAction aNew;
	public SaveAction aSave;
	public LoadAction aLoad;

	public EditorActions(AppFramework framework) {
		this.framework = framework;
		aSolve = new SolveAction();
		aBlank = new BlankAction();
		aClear = new ClearAction();
		aFill = new FillAction();
		aNew = new NewAction();
		aSave = new SaveAction();
		aLoad = new LoadAction();
	}
	
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	public class SolveAction extends EditorAction {
		private static final long serialVersionUID = 5756435048221444573L;

		public SolveAction(){
			super("Solve", "Solves the current puzzle");
		}
		
		@Override
		public void execute(ActionEvent e) {
			TrigonSolver solver = new TrigonSolver(framework.getPuzzle());
			solver.solutionLimit = 2;
			List<TrigonPuzzle> solutions = solver.solve();
			copyCommonBorderValuesTo(solutions, framework.getPuzzle());
			framework.mainWindow.solver = solver;
			
		}

		
		
		
	}
	
	public class BlankAction extends EditorAction {
		private static final long serialVersionUID = 5756435048221444573L;

		public BlankAction(){
			super("Blank", "Blanks out all border values, returning to an unsolved state");
		}
		
		@Override
		public void execute(ActionEvent e) {
			AppTrigons.blankOutBorders(framework.getPuzzle());
		}
	}
	
	public class ClearAction extends EditorAction {
		private static final long serialVersionUID = 5756435048221444573L;

		public ClearAction(){
			super("Clear", "Clears out all trigon and border values, creating a blank puzzle template");
		}
		
		@Override
		public void execute(ActionEvent e) {
			int result = JOptionPane.showConfirmDialog(framework.mainWindow, 
					"Are you sure you want to clear all trigon values?", "Trigons?", 
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(result == JOptionPane.YES_OPTION){
				AppTrigons.blankOut(framework.getPuzzle());
			}
		}
	}
	
	public class FillAction extends EditorAction {
		private static final long serialVersionUID = 5756435048221444573L;

		public FillAction(){
			super("Fill", "Fills in trigon values to create a solvable puzzle");
		}
		
		@Override
		public void execute(ActionEvent e) {
			TrigonFiller filler = new TrigonFiller(new TriPuzzle(framework.getPuzzle()), new Random());
			framework.mainWindow.solver = filler;
			List<TrigonPuzzle> solutions = filler.solve();
			if(solutions.size() == 1){
				copyValuesTo(solutions.get(0), framework.getPuzzle(), true, false);
			}
		}
	}
	
	public class NewAction extends EditorAction {
		private static final long serialVersionUID = 5756435048221444573L;

		public NewAction(){
			super("New", "Creates a new, blank trigon canvas");
		}
		
		@Override
		public void execute(ActionEvent e) {
			int result = JOptionPane.showConfirmDialog(framework.mainWindow, 
					"Are you sure you want to wipe the current puzzle?", "Trigons?", 
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(result == JOptionPane.YES_OPTION){
				framework.getPuzzle().clear();
			}
		}
	}
	
	public class SaveAction extends EditorAction {
		private static final long serialVersionUID = 5756435048221444573L;

		public SaveAction(){
			super("Save", "Saves the current puzzle to a file");
		}
		
		@Override
		public void execute(ActionEvent e) {
			JFileChooser chooser = new JFileChooser(AppTrigons.settings.getFileSetting(SETTING_LAST_PUZZLE_PATH, (String)null));
			int result = chooser.showSaveDialog(framework.mainWindow);
			if(result == JFileChooser.APPROVE_OPTION){
				saveCurrentPuzzle(chooser.getSelectedFile());
				AppTrigons.settings.saveSetting(SETTING_LAST_PUZZLE_PATH, chooser.getCurrentDirectory());
				AppTrigons.settings.saveSettings();
			}
		}

		private void saveCurrentPuzzle(File file) {
			FileOutputStream fout = null;
			BufferedOutputStream bout = null;
			DataOutputStream dout = null;
			try {
				fout = new FileOutputStream(file);
				bout = new BufferedOutputStream(fout);
				dout = new DataOutputStream(bout);
				
				((TriPuzzle)framework.getPuzzle()).writeData(dout);
			} catch (IOException e) {
				ErrUtil.errMsg(e, "trying to save puzzle to: " + file);
			} finally {
				if(fout != null){
					try {
						dout.flush();
						bout.flush();
						fout.flush();
						dout.close();
						bout.close();
					} catch(Exception e){
						e.printStackTrace();
					}
					try {
						fout.close();
					} catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public class LoadAction extends EditorAction {
		private static final long serialVersionUID = 5756435048221444573L;

		public LoadAction(){
			super("Load", "Loads a saved puzzle from a file");
		}
		
		@Override
		public void execute(ActionEvent e) {
			JFileChooser chooser = new JFileChooser(AppTrigons.settings.getFileSetting(SETTING_LAST_PUZZLE_PATH, (String)null));
			int result = chooser.showSaveDialog(framework.mainWindow);
			if(result == JFileChooser.APPROVE_OPTION){
				loadPuzzle(chooser.getSelectedFile());
				AppTrigons.settings.saveSetting(SETTING_LAST_PUZZLE_PATH, chooser.getCurrentDirectory());
				AppTrigons.settings.saveSettings();
			}
		}
		
		private void loadPuzzle(File file){
			FileInputStream fin = null;
			try {
				fin = new FileInputStream(file);
				DataInputStream din = new DataInputStream(new BufferedInputStream(fin));
				
				TriPuzzle puzzle = new TriPuzzle();
				puzzle.readData(din);
				copyValuesTo(puzzle, framework.getPuzzle(), true, true);
			} catch (IOException e) {
				ErrUtil.errMsg(e, "trying to save puzzle to: " + file);
			} finally {
				if(fin != null){
					try {
						fin.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public abstract class EditorAction extends AbstractActionPlus {
		private static final long serialVersionUID = -2059559201387749688L;
		
		public EditorAction() {
			super();
			// TODO Auto-generated constructor stub
		}

		public EditorAction(String name, String shortDescription,
				String longDescription, Icon icon) {
			super(name, shortDescription, longDescription, icon);
			// TODO Auto-generated constructor stub
		}

		public EditorAction(String name, String actionCommand,
				String shortDescription, String longDescription,
				Icon smallIcon, Icon largeIcon, KeyStroke accelerator,
				int mnemonic, int displayedMnemonicIndex) {
			super(name, actionCommand, shortDescription, longDescription, smallIcon,
					largeIcon, accelerator, mnemonic, displayedMnemonicIndex);
			// TODO Auto-generated constructor stub
		}

		public EditorAction(String name, String shortDescription,
				String longDescription) {
			super(name, shortDescription, longDescription);
		}

		public EditorAction(String name, String description) {
			super(name, description);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(framework.isEditModeOn()){
				execute(e);
			} else {
				ErrUtil.warningMsg("EditorAction '" + this.getClass().getSimpleName() + "' attempted to execute while editing mode was off");
			}
		}
		
		public abstract void execute(ActionEvent e);
	}
	
	private void copyCommonBorderValuesTo(List<TrigonPuzzle> solutions,
			TrigonPuzzle destination) {
		
		
		for(TrigonBorder destBorder : destination.getBorders()){
			putCommonValueOfBorder(destBorder, solutions);
		}
		
	}
	private void putCommonValueOfBorder(TrigonBorder destBorder,
			List<TrigonPuzzle> solutions) {
		int value = Integer.MIN_VALUE;
		for(TrigonPuzzle solution : solutions){
			TrigonBorder srcBorder = solution.getBorder(destBorder.getFirstTrigonX(), 
					destBorder.getFirstTrigonY(), 
					destBorder.getFirstTrigonSide());
			if(srcBorder == null){
				return;
			} else if(value == Integer.MIN_VALUE){
				value = srcBorder.getValue();
			} else if(value != srcBorder.getValue()){
				return;
			}
		}
		destBorder.setValue(value);
	}

	static void copyValuesTo(TrigonPuzzle source, TrigonPuzzle destination, boolean trigons, boolean borders) {
		//	To avoid firing unnecessary events, only differences are made
		if(trigons){
			for(Trigon srcTri : source.getTrigons()){
				Trigon destTri = destination.getTrigon(srcTri.getX(), srcTri.getY());
				if(destTri == null){
					destination.addTrigon(srcTri.getX(), srcTri.getY(), srcTri.getValue());
				} else if(destTri.getValue() != srcTri.getValue()){
					destTri.setValue(srcTri.getValue());
				}
			}

			List<Trigon> existingTrigons = new ArrayList<Trigon>(destination.getTrigons());
			for(Trigon destTrigon : existingTrigons){
				if(source.getTrigon(destTrigon.getX(), destTrigon.getY()) == null){
					destination.removeTrigon(destTrigon);
				}
			}
			
		}
		if(borders){
			for(TrigonBorder srcBorder : source.getBorders()){
				TrigonBorder destBorder = destination.getBorder(srcBorder.getFirstTrigonX(), 
						srcBorder.getFirstTrigonY(), 
						srcBorder.getFirstTrigonSide());
				if(destBorder.getValue() != srcBorder.getValue()){
					destBorder.setValue(srcBorder.getValue());
				}
			}
		}
		
	}
}
