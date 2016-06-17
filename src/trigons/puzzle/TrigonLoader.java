package trigons.puzzle;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import vordeka.util.exception.ImpossibleException;
import vordeka.util.io.LineReader;

public class TrigonLoader {
	
	public static final int FORMAT_BINARY = 1;
	public static final int FORMAT_TEXT = 2;
	
	public static TrigonPuzzle loadPuzzle(InputStream is, int format) throws IOException {
		switch(format){
		case FORMAT_BINARY:
			return loadBinary(new DataInputStream(is));
		case FORMAT_TEXT:
			try {
				return loadRawText(new InputStreamReader(is,"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new ImpossibleException(e);
			}
		default:
			throw new IllegalArgumentException("Unknown format: " + format);
		}
	}


	private static TrigonPuzzle loadBinary(DataInputStream in) throws IOException  {
		// TODO Auto-generated method stub
		return null;
	}
	

	private static TrigonPuzzle loadRawText(Reader r) throws IOException {
		TriPuzzle puzzle = new TriPuzzle();
		LineReader lr = new LineReader(r, ";");
		int y = 0;
		while(true){
			String line = lr.nextLine();
			if(line == null)
				break;
			line = line.trim().toLowerCase();
			if(line.isEmpty()) 
				continue;
			if(line.startsWith("max")){
				puzzle.maxBorderValue = Integer.parseInt(line.substring(3).trim());
			} else if(line.startsWith("row")){
				line = line.substring(3).trim();
				String[] parts = line.split(":");
				int x = 0;
				if(parts.length > 1){
					x = Integer.parseInt(parts[0].trim());
					parts = parts[1].split(",");
				} else {
					parts = parts[0].split(",");
				}
				for(String part : parts){
					part = part.trim();
					if(!part.isEmpty()){
						puzzle.addTrigon(x, y, Integer.parseInt(part));
					}
					++x;
				}
				++y;
			}
		}
		return puzzle;
	}


	public static TrigonPuzzle loadPuzzle(File file) throws IOException {
		FileInputStream fin = new FileInputStream(file);
		BufferedInputStream bin = new BufferedInputStream(fin);
		
		TrigonPuzzle puzzle = loadPuzzle(bin, FORMAT_TEXT);
		
		bin.close();
		fin.close();
		
		return puzzle;
	}


	public static TrigonPuzzle loadPuzzle(String source) throws IOException {
		Reader r = new StringReader(source);
		return loadRawText(r);
	}
}
