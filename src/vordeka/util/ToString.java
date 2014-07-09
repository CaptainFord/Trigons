package vordeka.util;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.tree.TreeModel;

public class ToString {
	public static String str(Object o){
		if(o == null){
			return "null";
		} else if(o instanceof CharSequence || o instanceof Number){
			return o.toString();
		} else if(o instanceof Color){
			return str((Color)o);
		} else if(o instanceof Rectangle){
			return str((Rectangle)o);
		} else if(o instanceof TreeModel){
			return str((TreeModel)o);
		} else {
			return o.toString();
		}
	}
	
	public static String str(Color color){
		if(color == null) return "null";
		return "0x" + StringUtil.padLeft(Integer.toHexString(color.getRGB()), 8, '0');
	}

	public static String str(Rectangle rect) {
		if(rect == null) return "null";
		return rect.x + "," + rect.y + "," + rect.width + "x" + rect.height;
	}
	
	public static String str(TreeModel model) {
		return str(model, model.getRoot(), "    ");
	}
	
	public static String str(TreeModel model, Object rootNode, String indentStr) {
		return new TreeModelDumper(model, indentStr).dump(rootNode);
	}
	
	public static class TreeModelDumper {
		StringBuilder b;
		TreeModel model;
		String indentStr;
		
		public TreeModelDumper(TreeModel model, String indentStr){
			this.model = model;
			this.indentStr = indentStr;
		}
		
		public String dump(Object rootNode){
			b = new StringBuilder();
			dumpNode(rootNode, "");
			b.setLength(b.length()-1);
			return b.toString();
		}

		public void dumpNode(Object node, String indent) {
			b.append(indent).append(node.toString()).append('\n');
			int childCount = model.getChildCount(node);
			for(int i=0; i<childCount; ++i){
				dumpNode(model.getChild(node, i), indent + indentStr);
			}
		}
	}


	public static Object repeat(String str, int count) {
		if(count <= 0) return "";
		if(count == 1) return str;
		StringBuilder b = new StringBuilder();
		while(--count >= 0){
			b.append(str);
		}
		return b.toString();
	}

	/**
	 * Generates a concise version of the object's string representation and type.
	 * @param o
	 * @return
	 */
	public static Object paramStr(Object o, int lenLimit) {
		if(o == null) return "null";
		if(o instanceof Number){
			return o.toString();
		} else if(o instanceof String){
			String s = (String)o;
			if(lenLimit > 0 && s.length() > lenLimit){
				return "\"" + s.substring(0, lenLimit / 2) + 
						"…" + s.substring(s.length() - lenLimit/2) + "\"";
			} else {
				return "\"" + s + "\"";
			}
		} else {
			Class<?> cls = o.getClass();
			String s = str(o);
			if(lenLimit > 0 && s.length() > lenLimit){
				return "(" + cls.getSimpleName() + ") " + s.substring(0, lenLimit / 2) + 
						"…" + s.substring(s.length() - lenLimit/2);
			} else {
				return "(" + cls.getSimpleName() + ") " + s;
			}	
		}
	}
	
	public static Object paramStr(Object o) {
		return paramStr(o, 0);
	}

	public static String str(int[] elements, int fromIndex, int toIndex, String separator) {
		if(fromIndex == toIndex) return "";
		if(toIndex < fromIndex) return str(elements, toIndex + 1, fromIndex + 1, separator);
		StringBuilder builder = new StringBuilder();
		
		for(int i=fromIndex; i<toIndex; ++i){
			builder.append(Integer.toHexString(elements[i])).append(separator);
		}
		builder.setLength(builder.length() - separator.length());
		return builder.toString();
	}
	
	public static String str(int[] elements, int fromIndex, int toIndex) {
		return str(elements, fromIndex, toIndex, ", ");
	}

	public static String str(int[] elements, String separator) {
		return str(elements, 0, elements.length, separator);
	}
	
	public static String str(int[] elements) {
		return str(elements, 0, elements.length);
	}
	
	public static String strHex(int[] elements, int fromIndex, int toIndex, String separator) {
		if(fromIndex == toIndex) return "";
		if(toIndex < fromIndex) return strHex(elements, toIndex, fromIndex, separator);
		StringBuilder builder = new StringBuilder();
		
		for(int i=fromIndex; i<toIndex; ++i){
			builder.append(elements[i]).append(separator);
		}
		builder.setLength(builder.length() - separator.length());
		return builder.toString();
	}
	
	public static String strHex(int[] elements, int fromIndex, int toIndex) {
		return strHex(elements, fromIndex, toIndex, ", ");
	}

	public static String strHex(int[] elements, String separator) {
		return strHex(elements, 0, elements.length, separator);
	}
	
	public static String strHex(int[] elements) {
		return strHex(elements, 0, elements.length);
	}
}
