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
import trigons.puzzle.Trigon;
import trigons.puzzle.TrigonBorder;
import trigons.puzzle.TrigonLoader;
import trigons.puzzle.TrigonPuzzle;
import trigons.ui.MainWindow;
import vordeka.util.ErrUtil;
import vordeka.util.SettingsSaver;
import vordeka.util.io.WriterSink;

public class AppTrigons {
	
	public static SettingsSaver settings;

	public static void main(String[] args){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		settings = SettingsSaver.initSaver(".trigons");
		Random rand = new Random("Your eyes are like pools of cheese in the rain".hashCode());
		TrigonPuzzle puzzle;
		try {
			puzzle = TrigonLoader.loadPuzzle(new File("Raw Puzzle.txt"));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
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
