package trigons;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.UIManager;

import trigons.generator.TrigonFiller;
import trigons.puzzle.TriPuzzle;
import trigons.puzzle.Trigon;
import trigons.puzzle.TrigonBorder;
import trigons.puzzle.TrigonLoader;
import trigons.puzzle.TrigonPuzzle;
import trigons.ui.MainWindow;
import vordeka.util.ErrUtil;
import vordeka.util.SettingsSaver;
import vordeka.util.io.WriterSink;

public class AppTrigons {
	public static final String DEFAULT_PUZZLE_FILENAME = "Raw Puzzle.txt";
	public static final String DEFAULT_PUZZLE = "Max 6;\n\nRow 0:11,17,7,9,9,5,12,9,5;\nRow -1:7,6,13,3,12,18,14,11,10,8,10;\nRow -2:10,15,12,9,8,13, ,16,10,9,6,7,13;\nRow -3:11,9,4,14,12, , , , , ,13,5,2,8,7;\nRow -3:10,6,2,7,8, , , , , ,11,12,14,8,4;\nRow -2:5,8,12,4,9,8, ,11,16,13,10,3,1;\nRow -1:6,6,5,14,3,10,15,15,4,12,11;\nRow 0:9,7,6,6,8,7,11,0,10";
	
	public static SettingsSaver settings;

	public static void main(String[] args){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		settings = SettingsSaver.initSaver(".trigons");
		Random rand = new Random("Your eyes are like pools of cheese in the rain".hashCode());
		TrigonPuzzle puzzle = loadDefaultPuzzle();
		
//		blankOut(puzzle);
//		puzzle = new TrigonGenerator(new Random("Gee, willy, two weiners!".hashCode()), 6).generatePuzzle(true);
		
		/*for(TrigonBorder b : puzzle.getBorders()){
			if(b.isHorizontal()){
				if(b.getFirstTrigon() != null && b.getSecondTrigon() != null){
					if(b.getFirstTrigon().getX() >= b.getSecondTrigon().getX()){
						ErrUtil.debugMsg("Trigon border between " + b.getFirstTrigon() + " and " + b.getSecondTrigon() + " runs right to left!");
					}
				}
			}
		}*/
		/*JTrigon trigons = new JTrigon();
		trigons.setModel(model);
		
		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		frame.add(new JScrollPane(trigons));
		
		frame.pack();
		frame.setVisible(true);*/
		
//		puzzle = generateNewPuzzleFromTemplate(puzzle, rand);
		
//		puzzle = solver.getRefinedPuzzle();
		MainWindow mw = new MainWindow();
		mw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mw.setTitle("Trigons!");
		mw.setPuzzle(puzzle);
//		mw.solver = solver;
		mw.pack();
		mw.setVisible(true);
	}
	
	private static TrigonPuzzle loadDefaultPuzzle() {
		File defaultFile = new File(DEFAULT_PUZZLE_FILENAME);
		if(defaultFile.exists()){
			try {
				return TrigonLoader.loadPuzzle(defaultFile);
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		try {
			return TrigonLoader.loadPuzzle(DEFAULT_PUZZLE);
		} catch(Exception e){
			e.printStackTrace();
		}
		return new TriPuzzle();
	}

	private static TrigonPuzzle generateNewPuzzleFromTemplate(TrigonPuzzle puzzle,
			Random rand) {
		TrigonFiller solver = new TrigonFiller(puzzle, rand);
		List<TrigonPuzzle> solutions = solver.solve();
		ErrUtil.debugMsg("Solutions Found: " + solutions.size());
		if(solutions.size() == 1){
			puzzle = solutions.get(0);
		} else {
			ErrUtil.debugMsg("Solutions Found: " + solutions.size());
		}
		ErrUtil.debugMsg("Total Arbitrations: " + solver.arbitrationCount + ":" + solver.subSolversCount + 
				"   Perms in First Layer: " + solver.getRemainingTrigonPermCount());
		ErrUtil.debugMsg("Process Counts:\n" + solver.dumpProcessCounts());
		return puzzle;
	}

	public static void blankOutBorders(TrigonPuzzle puzzle) {
		for(TrigonBorder b : puzzle.getBorders()){
			b.setValue(-1);
		}
	}
	
	public static void blankOut(TrigonPuzzle puzzle) {
		for(Trigon tri : puzzle.getTrigons()){
			tri.setValue(-1);
		}
		for(TrigonBorder b : puzzle.getBorders()){
			b.setValue(-1);
		}
	}

	private static FileWriter fw;
	private static Writer debugWriter;

	public static Writer getDebugWriter() throws IOException {
		
//		if(fw == null){
//			String filename = "debug." + System.currentTimeMillis() + ".txt";
//			fw = new FileWriter(filename);
//		}
		if(debugWriter == null){
			debugWriter = new WriterSink();
//			debugWriter = new BufferedWriter(fw);
		}
		return debugWriter;
	}
	public static void flushDebugWriter() throws IOException{
		if(debugWriter != null)	debugWriter.flush();
		if(fw != null)	fw.flush();
	}
}
