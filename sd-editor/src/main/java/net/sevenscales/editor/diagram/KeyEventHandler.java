package net.sevenscales.editor.diagram;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;

public class KeyEventHandler implements KeyEventListener {
	private List<KeyEventListener> keyEventHandlers;
  private boolean editable;

	public KeyEventHandler(boolean editable) {
	  this.editable = editable;
		keyEventHandlers = new ArrayList<KeyEventListener>();
	}

	public boolean onKeyEventDown(int keyCode, boolean shift, boolean ctrl) {
	    for (KeyEventListener l : keyEventHandlers) {
	      l.onKeyEventDown(keyCode, shift, ctrl);
	    }
	    return false;
	}

  public boolean onKeyEventUp(int keyCode, boolean shift, boolean ctrl) {
    for (KeyEventListener l : keyEventHandlers) {
      l.onKeyEventUp(keyCode, shift, ctrl);
    }
    return false;
  }
  
  public void onKeyDown(KeyDownEvent event) {
	for (KeyEventListener l : keyEventHandlers) {
		l.onKeyDown(event);
	}
  }
  
  public void onKeyUp(KeyUpEvent event) {
	for (KeyEventListener l : keyEventHandlers) {
		l.onKeyUp(event);
	}
  }
  
  public void onKeyPress(KeyPressEvent event) {
	  for (KeyEventListener l : keyEventHandlers) {
		  l.onKeyPress(event);
	  }
  }

  public void add(KeyEventListener keyEventHandler) {
    keyEventHandlers.add(keyEventHandler);
  }

  public void remove(KeyEventListener keyEventHandler) {
    keyEventHandlers.remove(keyEventHandler);
  }

//  public void onKeyDown(KeyDownEvent event) {
//    for (KeyEventListener l : keyEventHandlers) {
//      l.onKeyDown(event);
//    }
//  }
//
//  public void onKeyPress(KeyPressEvent event) {
//    for (KeyEventListener l : keyEventHandlers) {
//      l.onKeyPress(event);
//    }
//  }
//
//  public void onKeyUp(KeyUpEvent event) {
//    for (KeyEventListener l : keyEventHandlers) {
//      l.onKeyUp(event);
//    }
//  }

}
