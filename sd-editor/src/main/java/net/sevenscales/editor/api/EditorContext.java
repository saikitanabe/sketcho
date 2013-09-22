package net.sevenscales.editor.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sevenscales.editor.api.event.EditorClosedEvent;
import net.sevenscales.editor.api.ot.BoardDocument;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * EditorContext provides configuration for a one single editor.
 * @author saikitanabe
 *
 */
public class EditorContext {
	// to speed up query, if problems make a public field
	private boolean editable;

//	private static EditorContext instance;
	private HandlerManager eventBus = new HandlerManager(null);
	private Map<EditorProperty, Object> properties = new HashMap<EditorProperty, Object>();
	private Properties propertiesArea;
	private List<Widget> registeredComponents;
	private BoardDocument graphicalDocumentCache;
	
	public EditorContext() {
		registeredComponents = new ArrayList<Widget>();
	}
	
//	public static EditorContext getInstance() {
//		if (instance == null) {
//			instance = new EditorContext();
//		}
//		return instance;
//	}
	
	public BoardDocument getGraphicalDocumentCache() {
		return graphicalDocumentCache;
	}
	public void setGraphicalDocumentCache(BoardDocument graphicalDocumentCache) {
		this.graphicalDocumentCache = graphicalDocumentCache;
	}
	
	public HandlerManager getEventBus() {
		return eventBus;
	}
	
	public Object get(EditorProperty key) {
		if (properties.get(key) != null) {
			return properties.get(key);
		}
		return false;
	}

	public <T> T getAs(EditorProperty key) {
		return (T) get(key);
	}
	
	public boolean isTrue(EditorProperty key) {
		if (properties.get(key) != null) {
			return Boolean.valueOf(properties.get(key).toString());
		}
		return false;
	}
	
	public void set(EditorProperty key, Object value) {
		properties.put(key, value);
	}
	
	public boolean hasProperty(EditorProperty key) {
		return properties.get(key) != null;
	}

	public void setPropertiesArea(Properties propertiesArea) {
		this.propertiesArea = propertiesArea;
	}
	public Properties getPropertiesArea() {
		return propertiesArea;
	}

	public void setEditable(boolean editable) {
		this.editable = editable; 
	}
	public boolean isEditable() {
		return editable;
	}
	
	public void registerAndAddToRootPanel(Widget widget) {
		if (!registeredComponents.contains(widget)) {
			registeredComponents.add(widget);
			RootPanel.get().add(widget);
		}
	}

	public void closeEditor() {
		for (Widget w : registeredComponents) {
			w.setVisible(false);
		}
    Document.get().getElementById("toolbar-background").getStyle().setVisibility(Visibility.HIDDEN);
    getEventBus().fireEvent(new EditorClosedEvent());
//    Document.get().getElementById("sketchboardme-buttonbar").getStyle().setVisibility(Visibility.HIDDEN);
//    Document.get().getElementById("sketchboardme-confluenceMenu").getStyle().setVisibility(Visibility.HIDDEN);
//    Document.get().getElementById("sketchboardme-toolframe").getStyle().setVisibility(Visibility.HIDDEN);
//    Document.get().getElementById("sketchboardme-scaleSlider").getStyle().setVisibility(Visibility.HIDDEN);
	}

	public void showEditor() {
		for (Widget w : registeredComponents) {
			w.setVisible(true);
		}
    Document.get().getElementById("toolbar-background").getStyle().setVisibility(Visibility.VISIBLE);
//    Document.get().getElementById("sketchboardme-buttonbar").getStyle().setVisibility(Visibility.VISIBLE);
//    Document.get().getElementById("sketchboardme-confluenceMenu").getStyle().setVisibility(Visibility.VISIBLE);
//    Document.get().getElementById("sketchboardme-toolframe").getStyle().setVisibility(Visibility.VISIBLE);
//    Document.get().getElementById("sketchboardme-scaleSlider").getStyle().setVisibility(Visibility.VISIBLE);
	}

	public boolean isFreehandMode() {
		return isTrue(EditorProperty.FREEHAND_MODE);
	}

	public native String getCurrentUser()/*-{
		return $wnd.currentUser().displayName;
	}-*/;
}
