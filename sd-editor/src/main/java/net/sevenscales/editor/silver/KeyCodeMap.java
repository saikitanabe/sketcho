package net.sevenscales.editor.silver;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

public class KeyCodeMap {
	public static final int KEYNONE = 0;
	public static final int BACKSPACE = 1;
	public static final int TAB = 2;
	public static final int ENTER = 3;
	public static final int CTRL = 5;
	public static final int ESCAPE = 8;
	public static final int SPACE = 9;
	public static final int DELETE = 19;
	public static final int LEFT = 14;
	public static final int UP = 15;
	public static final int RIGHT = 16;
	public static final int DOWN = 17;
	
	public static final int DIGIT0 = 20;
	public static final int DIGIT1 = 21;
	public static final int DIGIT2 = 22;
	public static final int DIGIT3 = 23;
	public static final int DIGIT4 = 24;
	public static final int DIGIT5 = 25;
	public static final int DIGIT6 = 26;
	public static final int DIGIT7 = 27;
	public static final int DIGIT8 = 28;
	public static final int DIGIT9 = 29;
	
	public static final int A = 30;
	public static final int B = 31;
	public static final int C = 32;
	public static final int D = 33;
	public static final int E = 34;
	public static final int F = 35;
	public static final int G = 36;
	public static final int H = 37;
	public static final int I = 38;
	public static final int J = 39;
	public static final int K = 40;
	public static final int L = 41;
	public static final int M = 42;
	public static final int N = 43;
	public static final int O = 44;
	public static final int P = 45;
	public static final int Q = 46;
	public static final int R = 47;
	public static final int S = 48;
	public static final int T = 49;
	public static final int U = 50;
	public static final int V = 51;
	public static final int W = 52;
	public static final int X = 53;
	public static final int Y = 54;
	public static final int Z = 55;
	
	
	public static String modify(String text, int keyCode, boolean shift, boolean ctrl) {
		if (keyCode == BACKSPACE) {
			if (text.length() > 0) {
        int removeLength = 1;
//      text.replaceAll("\r\n$", "");
//      if (text.matches("\r\n")) {
      if (ctrl) {
			  removeLength = text.length();
		  } else if (text.length() > 2 && text.charAt(text.length()-2) == '\r') {
        removeLength = 3;
      }

//      int numChars = text.charAt(text.length()-1) == '\r';
      text = text.substring(0, text.length() - removeLength);
			}
		} else { 
			text += map(keyCode, shift);
		}
		return text;
	}
	
	public static String map(int keyCode, boolean shift) {
		switch (keyCode) {
		  case SPACE:
		    return " ";
		  case ENTER:
		    return "\n";
  		case DIGIT0:
  			return "0";
  		case DIGIT1:
  			return "1";
  		case DIGIT2:
  			return "2";
  		case DIGIT3:
  			return "3";
  		case DIGIT4:
  			return "4";
  		case DIGIT5:
  			return "5";
  		case DIGIT6:
  			return "6";
  		case DIGIT7:
  			return "7";
  		case DIGIT8:
  			return "8";
  		case DIGIT9:
  			return "9";
  			
  		case A:
  			return shift ? "A" : "a";
  		case B:
  			return shift ? "B" : "b";
  		case C:
  			return shift ? "C" : "c";
  		case D:
  			return shift ? "D" : "d";
  		case E:
  			return shift ? "E" : "e";
  		case F:
  			return shift ? "F" : "f";
  		case G:
  			return shift ? "G" : "g";
  		case H:
  			return shift ? "H" : "h";
  		case I:
  			return shift ? "I" : "i";
  		case J:
  			return shift ? "J" : "j";
  		case K:
  			return shift ? "K" : "k";
  		case L:
  			return shift ? "L" : "l";
  		case M:
  			return shift ? "M" : "m";
  		case N:
  			return shift ? "N" : "n";
  		case O:
  			return shift ? "O" : "o";
  		case P:
  			return shift ? "P" : "p";
  		case Q:
  			return shift ? "Q" : "q";
  		case R:
  			return shift ? "R" : "r";
  		case S:
  			return shift ? "S" : "s";
  		case T:
  			return shift ? "T" : "t";
  		case U:
  			return shift ? "U" : "u";
  		case V:
  			return shift ? "V" : "v";
  		case W:
  			return shift ? "W" : "w";
  		case X:
  			return shift ? "X" : "x";
  		case Y:
  			return shift ? "Y" : "y";
  		case Z:
  			return shift ? "Z" : "z";
		}
		return "";
	}
	
	public static String modifyBackspace(String text, Event event) {
    if (text.length() > 0) {
      int removeLength = 1;
//    text.replaceAll("\r\n$", "");
//    if (text.matches("\r\n")) {
    if (DOM.eventGetCtrlKey(event)) {
      removeLength = text.length();
    } else if (text.length() > 2 && text.charAt(text.length()-2) == '\r') {
      removeLength = 3;
    }

//    int numChars = text.charAt(text.length()-1) == '\r';
    text = text.substring(0, text.length() - removeLength);
    }
    return text;
	}

	public static String modify(String text, Event event) {
		int keyCode = DOM.eventGetKeyCode(event);
//	    Debug.print("modify"+DOM.eventGetKeyCode(event));			
		switch (keyCode) {
		case KeyCodes.KEY_BACKSPACE:
      if (text.length() > 0) {
        int removeLength = 1;
//      text.replaceAll("\r\n$", "");
//      if (text.matches("\r\n")) {
      if (DOM.eventGetCtrlKey(event)) {
        removeLength = text.length();
      } else if (text.length() > 2 && text.charAt(text.length()-2) == '\r') {
        removeLength = 3;
      }

//      int numChars = text.charAt(text.length()-1) == '\r';
      text = text.substring(0, text.length() - removeLength);
      }
			break;
		default:
			String t = convert(event);
			text += t;
			DOM.eventPreventDefault(event);
			break;
		}
		return text;
	}
	
	private static native String convert(Event event)/*-{
//		$wnd.debugLog("w:"+event.which + "k:"+ event.keyCode +"c:"+ event.charCode);
		return String.fromCharCode(event.charCode);
	}-*/;
}
