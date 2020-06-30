package net.sevenscales.editor.content.utils;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.Widget;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.Library;
import net.sevenscales.editor.api.event.pointer.PointerDownEvent;
import net.sevenscales.editor.api.event.pointer.PointerDownHandler;
import net.sevenscales.editor.api.event.pointer.PointerEventsSupport;
import net.sevenscales.editor.api.event.pointer.PointerOutEvent;
import net.sevenscales.editor.api.event.pointer.PointerOutHandler;
import net.sevenscales.editor.api.event.pointer.PointerOverEvent;
import net.sevenscales.editor.api.event.pointer.PointerOverHandler;


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

		outer.setVisible(false);

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

    if (PointerEventsSupport.isSupported()) {
      supportPointerEvents();
    } else {
      supportMouseTouchEvents();
    }

    handleLibraryStreams(this);
  }
  
  private void supportPointerEvents() {
		outer.addDomHandler(new PointerOverHandler() {
			@Override
			public void onPointerOver(PointerOverEvent event) {
        mouseOver();
			}
    }, PointerOverEvent.getType());

		outer.addDomHandler(new PointerOutHandler() {
			@Override
			public void onPointerOut(PointerOutEvent event) {
        mouseOut();
			}
		}, PointerOutEvent.getType());
		
		outer.addDomHandler(new PointerDownHandler() {
			@Override
			public void onPointerDown(PointerDownEvent event) {
        mouseDown(event);
			}
		}, PointerDownEvent.getType());
  }

  private void supportMouseTouchEvents() {
		listenTouch();
		outer.addDomHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
        mouseOver();
			}
		}, MouseOverEvent.getType());
		outer.addDomHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
        mouseOut();
			}
		}, MouseOutEvent.getType());
		
		outer.addDomHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
        mouseDown(event);
			}
		}, MouseDownEvent.getType());
  }

  private void mouseDown(MouseEvent event) {
    event.preventDefault();
    event.stopPropagation();
    // if (!TouchHelpers.isSupportsTouch()) {
      show();
    // }
//				if (!Element.as(event.getNativeEvent().getEventTarget()).isOrHasChild(inner.getElement())) {
//					Scheduler.get().scheduleFixedDelay(hideslider, 2000);
//				}

  }

  private void mouseOver() {
    if (!ngIsLibraryManualShowHide()) {
      show();
    }
  }

  private void mouseOut() {
    // System.out.println(Element.as(event.getNativeEvent().getEventTarget()).getId());
    //				if (!Element.as(event.getNativeEvent().getEventTarget()).isOrHasChild(inner.getElement())) {
              onOuterElement = false;
              // Scheduler.get().scheduleFixedDelay(hideslider, 2000);
    //				}
  }

	private native void handleLibraryStreams(ShowHideHelpers me)/*-{
		$wnd.globalStreams.closeLibraryStram.onValue(function(value) {
			me.@net.sevenscales.editor.content.utils.ShowHideHelpers::onLibraryStream(Z)(value);
		})

		$wnd.globalStreams.handToolStream.onValue(function(value) {
			if (value) {
				me.@net.sevenscales.editor.content.utils.ShowHideHelpers::onLibraryStream(Z)(value);
			}
		})

		$wnd.cancelStream.onValue(function() {
			me.@net.sevenscales.editor.content.utils.ShowHideHelpers::onLibraryStream(Z)(true);
		})

    $wnd.boardReadyStream.onValue(function() {
      me.@net.sevenscales.editor.content.utils.ShowHideHelpers::autoHideLibrary()();
    })

	}-*/;

  private void autoHideLibrary() {
    if (ngIsLibraryManualShowHide()) {
   		hide();
    }
  }

	private void onLibraryStream(boolean value) {
		if (value) {
			hide();
		}
	}

	private void listenTouch() {
		outer.addDomHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				event.preventDefault();
				event.stopPropagation();
				show();
			}
		}, TouchStartEvent.getType());
	}

	private void show() {
		// logger.debug("show...");
		Object value = editorContext.get(EditorProperty.CURRENT_LIBRARY);
		if (value != null && value instanceof Library && notConfluence()) {
			trigger("library-show-" + value.toString().toLowerCase());
		}

		onOuterElement = true;

		inner.setVisible(true);
		// EffectHelpers.fadeIn(inner.getElement());
		EffectHelpers.fadeOut(outer.getElement());
	}

	public void forceFadeOut() {
		if (!ngIsLibraryManualShowHide()) {
			hide();
		}
	}
	
	private void hide() {
		// logger.debug("hide...");
		Object value = editorContext.get(EditorProperty.CURRENT_LIBRARY);
		if (notConfluence()) {
			trigger("library-hide");
		}

		inner.setVisible(false);
		// EffectHelpers.fadeOut(inner.getElement());
		EffectHelpers.fadeIn(outer.getElement());
	}

	private boolean notConfluence() {
		return !editorContext.isTrue(EditorProperty.CONFLUENCE_MODE);
	}

	private native boolean ngIsLibraryManualShowHide()/*-{
		if (typeof $wnd.ngIsLibraryManualShowHide !== 'undefined') {
			return $wnd.ngIsLibraryManualShowHide()
		}
		return false
	}-*/;

	private native void trigger(String event)/*-{
		$wnd.$($doc).trigger(event)
	}-*/;
}
