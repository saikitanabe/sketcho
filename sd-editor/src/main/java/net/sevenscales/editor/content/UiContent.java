package net.sevenscales.editor.content;

import net.sevenscales.domain.api.IContent;
import net.sevenscales.editor.api.EditorContext;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class UiContent extends SimplePanel {
	private EditorContext editorContext;
	private IContent content;
	private Context context;

	public UiContent(IContent content, Context context, EditorContext editorContext) {
		this.content = content;
		this.editorContext = editorContext;
		
    // default values
    checkDefaults();
    
    setStyleName("no-show-focus");
    addStyleName("UiContent");
    
//    addKeyDownHandler(new KeyDownHandler() {
//			@Override
//			public void onKeyDown(KeyDownEvent event) {
//				// disable Confluence default keys when having focus on
//				// sketcho diagram area; otherwise will move somewhere else
//				ContentEventUtils.hanleKeyEvent(event);
//			}
//		});
//    addKeyPressHandler(new KeyPressHandler() {
//			@Override
//			public void onKeyPress(KeyPressEvent event) {
//				// IE8 needs this or it will handle confluence 
//				// keyboard shortcuts
//				ContentEventUtils.hanleKeyEvent(event);
////				event.stopPropagation();
////				event.preventDefault();
//			}
//		});
	}
	
  protected void checkDefaults() {
    if (getContent().getWidth() == null && getDefaultWidth() > 0) {
      getContent().setWidth(getDefaultWidth());
    }
    
    if (getContent().getHeight() == null && getDefaultHeight() > 0) {
      getContent().setHeight(getDefaultHeight());
    }
  }
  
  protected abstract int getDefaultWidth();
  protected abstract int getDefaultHeight();
	
	public IContent getContent() {
		return this.content;
	}
	
	public void setContent(IContent content) {
	  this.content = content;
	}
	
	public abstract Widget getWidget();
	
	/**
	 * Externalizes ui content to model content.
	 * 
	 */
	public abstract void externalize();

	/**
	 * Internalizes model content to ui content.
	 */
	public abstract void internalize();
	
  public void refresh() {
  }
  
  public Context getContext() {
	return context;
	}
  
  public EditorContext getEditorContext() {
		return editorContext;
	}
  
}