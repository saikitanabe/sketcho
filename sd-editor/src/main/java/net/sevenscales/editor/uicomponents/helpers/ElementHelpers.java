package net.sevenscales.editor.uicomponents.helpers;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;

import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Event;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

public class ElementHelpers {
	private static final SLogger logger = SLogger.createLogger(ElementHelpers.class);

	public static void hide(IGlobalElement globalElement, AbstractDiagramItem candidate) {
		if (globalElement.getParent() != null && globalElement.getParent().isSelected()) {
//			logger.debug("hide selected({}), parent {} => NOT HIDING since selected...", 
//					globalElement.getParent().isSelected(), globalElement.getParent());
		} else if (candidate == globalElement.getParent() || globalElement.getParent() == null) {
			// allow to hide only from the parent or when there are no parent, e.g. unselect all
			globalElement.hideGlobalElement();
		}
	}


	public static void addEventListener(Element element, EventListener eventListener) {
		DOM.sinkEvents((com.google.gwt.user.client.Element) element.cast(),	Event.ONCLICK);
		DOM.setEventListener(
				(com.google.gwt.user.client.Element) element.cast(), eventListener);
	}


}
