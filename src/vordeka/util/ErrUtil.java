package vordeka.util;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;


public class ErrUtil {
	private static Set<String> debugOnceKeys, warningOnceKeys;
	
	public static PrintStream out, err;
	
	public static PrintStream out(){
		return ErrUtil.out == null ? System.out : ErrUtil.out;
	}
	
	public static PrintStream err(){
		return ErrUtil.err == null ? System.err : ErrUtil.err;
	}
	
	
	public static void msg(String label, String message){
		out().println("[" + label + "] (" + sourceLocation(1) + ") - " + message);
	}
	
	public static void msg(String label, String message, int traceOffset){
		out().println("[" + label + "] (" + sourceLocation(traceOffset + 1) + ") - " + message);
	}
	
	public static void alert(String label, String message){
		err().println("[" + label + "] (" + sourceLocation(1) + ") - " + message);
	}
	
	public static void alert(String label, String message, int traceOffset){
		err().println("[" + label + "] (" + sourceLocation(traceOffset + 1) + ") - " + message);
	}
	
	public static void debugMsg(String message){
		out().println("[Dbg] (" + sourceLocation(1) + ") - " + message);
	}
	
	public static void debugMsg(String message, int traceOffset) {
		out().println("[Dbg] (" + sourceLocation(traceOffset + 1) + ") - " + message);
	}
	
	public static void printStackTrace(){
		out().println("[Dbg+Trace] (" + sourceLocation(1) + ") - Printing Stack Trace:");
		new Exception("Printing Stack Trace ").printStackTrace(System.out);
	}
	
	public static void printStackTrace(String message){
		out().println("[Dbg+Trace] (" + sourceLocation(1) + ") - " + message);
		new Exception("Printing Stack Trace ").printStackTrace(System.out);
	}
	
	/**
	 * Gets a string identifying the location <code>count</code>
	 * places above the call to this method.
	 * @param count
	 * 		the number of stack elements above this method call to offset
	 * @return
	 * 		a string identifying a location leading to this method invocation
	 */
	public static String sourceLocation(int count) {
		StackTraceElement e = Thread.currentThread().getStackTrace()[2 + count];
		return e.getFileName() + ":" + e.getLineNumber();
	}
	
	/**
	 * Gets a string identifying the location where this
	 * method was invoked from.
	 * @return
	 * 		a string identifying a location leading to this method invocation
	 */
	public static String sourceLocation() {
		StackTraceElement e = Thread.currentThread().getStackTrace()[2];
		return e.getFileName() + ":" + e.getLineNumber();
	}

	public static void warningMsg(String message){
		out().println("[Warning] (" + sourceLocation(1) + ") - " + message);
	}

	public static void warningMsg(String message, int traceOffset) {
		out().println("[Warning] (" + sourceLocation(traceOffset + 1) + ") - " + message);
	}
	
	public static void warningWithTrace(String message){
		out().println("[Warning] (" + sourceLocation(1) + ") - " + message + 
				shortStackTrace(4, 1));
	}
	
	public static void warningWithTrace(String message, int traceLength){
		out().println("[Warning] (" + sourceLocation(1) + ") - " + message + 
				shortStackTrace(traceLength, 1));
	}
	
	public static void warningWithTrace(String message, int traceLength, int traceOffset){
		out().println("[Warning] (" + sourceLocation(traceOffset + 1) + ") - " + message + 
				shortStackTrace(traceLength, traceOffset + 1));
	}
	
	public static void errorMsg(String message){
		err().println("[Warning] (" + sourceLocation(1) + ") - " + message);
	}

	public static void errorMsg(String message, int traceOffset) {
		err().println("[Warning] (" + sourceLocation(traceOffset + 1) + ") - " + message);
	}
	
	public static void errorMsgWithTrace(String message){
		err().println("[Warning] (" + sourceLocation(1) + ") - " + message + 
				shortStackTrace(4, 1));
	}
	
	public static void errorMsgWithTrace(String message, int traceLength){
		err().println("[Warning] (" + sourceLocation(1) + ") - " + message + 
				shortStackTrace(traceLength, 1));
	}
	
	public static void errorMsgWithTrace(String message, int traceLength, int traceOffset){
		err().println("[Warning] (" + sourceLocation(traceOffset + 1) + ") - " + message + 
				shortStackTrace(traceLength, traceOffset + 1));
	}
	
	public static void errMsg(Throwable e, String whatYouWereDoing) {
		err().println("Error caught (" + sourceLocation(1) + ") while " + whatYouWereDoing);
		e.printStackTrace(System.err);
	}
	
	public static void errMsg(Throwable e, String whatYouWereDoing, int traceOffset) {
		err().println("Error caught (" + sourceLocation(traceOffset + 1) + ") while " + whatYouWereDoing);
		e.printStackTrace(System.err);
	}

	public static void markMethod() {
		StackTraceElement e = Thread.currentThread().getStackTrace()[2];
		out().println("[Dbg] (" + sourceLocation(1) + ") - " + e.getClassName() + "." + e.getMethodName() + " called");
	}

	public static String getCurrentMethod() {
		return Thread.currentThread().getStackTrace()[2].getMethodName();
	}

	/**
	 * Identical to {@link #getCurrentMethod()}, but looks further up the stack.
	 * An offset of 0 operates identically to {@link #getCurrentMethod()}.
	 * @param stackOffset
	 * 		The number of stack positions to offset by
	 * @return
	 * 		The name of the method at the specified relative stack position
	 */
	public static String getCurrentMethod(int stackOffset) {
		return Thread.currentThread().getStackTrace()[2 + stackOffset].getMethodName();
	}

	/**
	 * Returns a string containing a short stack trace based on the current location 
	 * in the code.
	 * @param numElements
	 * @param stackOffset
	 * @return
	 */
	public static String shortStackTrace(int numElements, int stackOffset) {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		StringBuilder b = new StringBuilder();
		int start = 2 + stackOffset;
		int end = 2 + numElements + stackOffset;
		for(int i=start; i<end; ++i){
			if(i > start) b.append('\n');
			b.append("\tat ").append(trace[i].toString());
		}
		return b.toString();
	}
	
	
	/**
	 * Returns a string containing a subsegment of an exception's stack trace.
	 * @param t
	 * @param numElements
	 * @return
	 */
	public static String shortStackTrace(Throwable t, int numElements) {
		return shortStackTrace(t, numElements, 0, true);
	}
	

	/**
	 * Returns a string containing a subsegment of an exception's stack trace.
	 * @param t
	 * @param numElements
	 * @param showRemainingCount
	 * 		if true, a line will be added at the end indicating the number of elements 
	 * 	not printed
	 * @return
	 */
	public static String shortStackTrace(Throwable t, int numElements, boolean showRemainingCount) {
		return shortStackTrace(t, numElements, 0, showRemainingCount);
	}
	
	/**
	 * Returns a string containing a subsegment of an exception's stack trace.
	 * @param t
	 * @param numElements
	 * @param stackOffset
	 * @return
	 */
	public static String shortStackTrace(Throwable t, int numElements, int stackOffset) {
		return shortStackTrace(t, numElements, stackOffset, true);
	}
	
	/**
	 * Returns a string containing a subsegment of an exception's stack trace.
	 * @param t
	 * 		the {@link Throwable} to print the trace from 
	 * @param numElements
	 * 		the number of trace elements to show
	 * @param stackOffset
	 * 		the number of trace elements to skip over from the top of the trace
	 * @param showRemainingCount
	 * 		if true, a line will be added at the end indicating the number of elements 
	 * 	not printed
	 * @return
	 * 		the requested subset of the exception's stack trace
	 */
	public static String shortStackTrace(Throwable t, int numElements, int stackOffset, 
			boolean showRemainingCount) {
		StackTraceElement[] trace = t.getStackTrace();
		if(stackOffset >= trace.length){
			return "";
		}
		StringBuilder b = new StringBuilder();
		int start = stackOffset < 0 ? 0 : stackOffset;
		int end = stackOffset + numElements;
		if(end > trace.length) end = trace.length;
		for(int i=start; i<end; ++i){
			if(i > start) b.append('\n');
			b.append("\tat ").append(trace[i].toString());
		}	
		if(showRemainingCount && end < trace.length){
			b.append("\n\t[").append(trace.length - end).append(" More]");
		}
		return b.toString();
	}

	public static void debugOnce(String key, String message) {
		debugOnce(key, message, 1);
	}
	public static void debugOnce(String key, String message, int traceOffset) {
		if(debugOnceKeys == null){
			debugOnceKeys = new HashSet<String>();
		}
		if(debugOnceKeys.add(key)){
			debugMsg(message, traceOffset + 1);
		}
	}

	public static String info(Object aboutMe) {
		if(aboutMe == null) return "null";
		return "'" + aboutMe + "' (" + aboutMe.getClass() + ")";
	}
	
	public static void warningOnce(String message, int traceOffset) {
		warningOnce(message, message, traceOffset + 1);
	}
	public static void warningOnce(String key, String message) {
		warningOnce(key, message, 1);
	}
	public static void warningOnce(String key, String message, int traceOffset) {
		if(warningOnceKeys == null){
			warningOnceKeys = new HashSet<String>();
		}
		if(warningOnceKeys.add(key)){
			warningMsg(message, traceOffset + 1);
		}
	}

	/**
	 * Generates a message that lists the parameters of a method that was just called.
	 * @param params
	 * 		The parameters to the method call
	 */
	public static void invokeMsg(Object[] params) {
		invokeMsg(1, params);
	}	
	
	public static void invokeMsg(int stackOffset, Object[] params) {
		StackTraceElement e = Thread.currentThread().getStackTrace()[2 + stackOffset];
		String simpleClassName = e.getClassName();
		int lastDotIndex = simpleClassName.lastIndexOf('.');
		simpleClassName = lastDotIndex >= 0 ? simpleClassName.substring(lastDotIndex+1) : simpleClassName;
		StringBuilder msg = new StringBuilder(simpleClassName + "." + e.getMethodName() + "(");
		for(Object o : params){
			msg.append(ToString.paramStr(o, 0)).append(", ");
		}
		if(params.length > 0)
			msg.setLength(msg.length() - 2);
		msg.append(")");
		msg("Called", msg.toString(), 1 + stackOffset);
	}
	
	public static void invokeMsg(int stackOffset, int traceLength, Object ... params) {
		StackTraceElement e = Thread.currentThread().getStackTrace()[2 + stackOffset];
		String simpleClassName = e.getClassName();
		int lastDotIndex = simpleClassName.lastIndexOf('.');
		simpleClassName = lastDotIndex >= 0 ? simpleClassName.substring(lastDotIndex+1) : simpleClassName;
		StringBuilder msg = new StringBuilder(simpleClassName + "." + e.getMethodName() + "(");
		for(Object o : params){
			msg.append(ToString.paramStr(o, 0)).append(", ");
		}
		if(params.length > 0)
			msg.setLength(msg.length() - 2);
		msg.append(")");
		msg.append("\n").append(shortStackTrace(traceLength, 2 + stackOffset));
		msg("Called", msg.toString(), 1 + stackOffset);
	}

	public static void debugOnce(String message) {
		debugOnce(message, message, 1);
	}

	public static void warningOnce(String message) {
		warningOnce(message, message, 1);
	}

	
}
