package net.sevenscales.plugin.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sevenscales.appFrame.impl.EventRegistry;
import net.sevenscales.domain.DiagramContentDTO;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.api.IContent;
import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.api.ITextContent;
import net.sevenscales.domain.dto.TextContentDTO;
import net.sevenscales.plugin.constants.SdRegistryEvents;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class Clipboard {
	private List<IContent> clipboardContent = new ArrayList<IContent>();
  private EventRegistry eventRegistry;

	public IContent pop() {
		int size = clipboardContent.size();
		IContent result = null;
		if (size > 0) {
			result = clipboardContent.get(size-1);
			clipboardContent.remove(size-1);
		}
		
		// notify needs to be out of the loop
		DeferredCommand.addCommand(new Command() {
      public void execute() {
        eventRegistry.handleEvent(SdRegistryEvents.CLIPBOARD_UPDATE, null);
      }
    });
		return result;
	}
	
	public IContent pop(Class contentClass) {
	  if (contentClass == null) {
	    return null;
	  }
	  
    for (IContent c : clipboardContent) {
      Class clazz = c.getClass();
      while (clazz != null) {
        GWT.log(clazz + " " + contentClass, null);
        if (clazz.equals(contentClass)) {
          return c;
        }
        clazz = clazz.getSuperclass();
      }
    }
	  return null;
	}
	
	public List<IContent> popAll() {
		List<IContent> result = new ArrayList<IContent>(clipboardContent);
		clipboardContent.clear();
		GWT.log("pop: " + result, null);
    eventRegistry.handleEvent(SdRegistryEvents.CLIPBOARD_UPDATE, null);
		return result;
	}
	
	public void push(IContent contentCopy) {
	  clipboardContent.add(contentCopy);
		GWT.log("push: " + contentCopy, null);
		eventRegistry.handleEvent(SdRegistryEvents.CLIPBOARD_UPDATE, null);
	}
	
  public void setEventRegistry(EventRegistry eventRegistry) {
    this.eventRegistry = eventRegistry;
  }

  public int size() {
    return clipboardContent.size();
  }
  
  public int size(Class contentClass) {
    if (contentClass == null) {
      return 0;
    }
    int result = 0;
    for (IContent c : clipboardContent) {
      Class clazz = c.getClass(); 
      while (clazz != null) {
        GWT.log(clazz + " " + contentClass, null);
        if (clazz.equals(contentClass)) {
          ++result;
          break;
        }
        clazz = clazz.getSuperclass();
      }
    }
    return result;
  }
	
}
