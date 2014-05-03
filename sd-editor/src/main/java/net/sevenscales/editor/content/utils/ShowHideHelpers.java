package net.sevenscales.editor.content.utils;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.Widget;

import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.LibrarySelections;
import net.sevenscales.domain.utils.SLogger;


public class ShowHideHelpers {
	private static final SLogger logger = SLogger.createLogger(ShowHideHelpers.class);
	
	private boolean onOuterElement = false;
	private Widget inner;
	private Widget outer;
	private EditorContext editorContext;

	public ShowHideHelpers(Widget outer, final Widget inner, boolean editable, EditorContext editorContext) {
		this(outer, inner, 6000, editable, editorContext);
	}
	
	public ShowHideHelpers(Widget outer, final Widget inner, int delay, boolean editable, EditorContext editorContext) {
		this.outer = outer;
		this.inner = inner;
		this.editorContext = editorContext;
		final RepeatingCommand hideslider = new RepeatingCommand() {
			@Override
			public boolean execute() {
				if (!onOuterElement) {
					hide();
				}
				return false;
			}
		};

		if (!editable) {
			// read only boards doesn't need to show library
			Scheduler.get().scheduleFixedDelay(hideslider, delay);
		}

		listenTouch();
		outer.addDomHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				show();
			}
		}, MouseOverEvent.getType());
		outer.addDomHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				System.out.println(Element.as(event.getNativeEvent().getEventTarget()).getId());
//				if (!Element.as(event.getNativeEvent().getEventTarget()).isOrHasChild(inner.getElement())) {
					onOuterElement = false;
					// Scheduler.get().scheduleFixedDelay(hideslider, 2000);
//				}
			}
		}, MouseOutEvent.getType());
		
		outer.addDomHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				show();
//				if (!Element.as(event.getNativeEvent().getEventTarget()).isOrHasChild(inner.getElement())) {
//					Scheduler.get().scheduleFixedDelay(hideslider, 2000);
//				}
			}
		}, MouseDownEvent.getType());

	}

	private void listenTouch() {
		outer.addDomHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				show();
			}
		}, TouchStartEvent.getType());
	}

	private void show() {
		// logger.debug("show...");
		Object value = editorContext.get(EditorProperty.CURRENT_LIBRARY);
		if (value != null && value instanceof LibrarySelections.Library && value == LibrarySelections.Library.IMAGES && notConfluence()) {
			trigger("library-show");
		}

		onOuterElement = true;
		EffectHelpers.fadeIn(inner.getElement());
	}

	public void forceFadeOut() {
		hide();
	}
	
	private void hide() {
		// logger.debug("hide...");
		if (notConfluence()) {
			trigger("library-hide");
		}
		EffectHelpers.fadeOut(inner.getElement());
	}

	private boolean notConfluence() {
		return !editorContext.isTrue(EditorProperty.CONFLUENCE_MODE);
	}

	private native void trigger(String event)/*-{
		$wnd.$($doc).trigger(event)
	}-*/;
}
