package vordeka.util.io;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

/**
 * Reads entire lines from a Reader.
 * @author Alex
 *
 */
public class LineReader {
	
	protected Reader r;
	protected String[] terminators;
	protected int[] matches;
	protected String lastTerminator;
	protected StringBuilder b = new StringBuilder();

	public LineReader(Reader r){
		this(r, "\n");
	}
	
	public LineReader(Reader r, String ... lineTerminators){
		if(lineTerminators.length == 0)
			throw new IllegalArgumentException("Must have at least one terminator");
		for(String terminator : lineTerminators){
			if(terminator == null)
				throw new IllegalArgumentException("Terminators may not be null");
			if(terminator.length() == 0)
				throw new IllegalArgumentException("Terminators may not be zero-length");
		}
		this.r = r;
		this.terminators = Arrays.copyOf(lineTerminators, lineTerminators.length);
		this.matches = new int[terminators.length];
	}
	
	/**
	 * Tests if the last line returned was terminated by a terminator
	 * string, or if it was terminated by the end of the stream.
	 * @return
	 * 		true if the line was terminated by a terminator string, false
	 * if it was terminated by the end of the stream
	 */
	public boolean lastLineWasTerminated(){
		return getLastTerminator() != null;
	}
	
	public String getLastTerminator() {
		return lastTerminator;
	}

	/**
	 * Reads the next line from the stream, returns null
	 * if the end of the stream has been reached. 
	 * @return
	 * @throws IOException 
	 */
	public String nextLine() throws IOException{
		lastTerminator = null;
		b.setLength(0);
		Arrays.fill(matches, 0);
		
		int v;
		char c;
loop:	while(true) {
			v = r.read();
			if(v == -1){
				if(b.length() == 0)
					return null;
				break;
			}
			c = (char) v;
			b.append(c);
			for(int i=0; i<terminators.length; ++i){
				if(c == terminators[i].charAt(matches[i])){
					if(++matches[i] == terminators[i].length()){
						lastTerminator = terminators[i];
						break loop;
					}
				}
			}
		}
		if(lastTerminator != null){
			b.setLength(b.length() - lastTerminator.length());
		}
		return b.toString();
	}

}
