package net.sevenscales.editor.content.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLPanel;

import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.api.event.CommentModeEvent;
import net.sevenscales.editor.api.event.CommentModeEventHandler;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent;
import net.sevenscales.editor.api.event.FreehandModeChangedEventHandler;
import net.sevenscales.editor.content.utils.EffectHelpers;

public class TopButtons extends Composite {

	private static TopButtonsUiBinder uiBinder = GWT
			.create(TopButtonsUiBinder.class);

	interface TopButtonsUiBinder extends UiBinder<Widget, TopButtons> {
	}

	private EditorContext editorContext;
	
	// @UiField ButtonElement freehandOn;
  @UiField HTMLPanel panel;
	@UiField Element colorize;
	@UiField Element lineWeight;
	// @UiField ButtonElement commentModeOn;

	public TopButtons(EditorContext editorContext) {
		this.editorContext = editorContext;
		initWidget(uiBinder.createAndBindUi(this));
		setVisible(false);

		editorContext.getEventBus().addHandler(FreehandModeChangedEvent.TYPE, new FreehandModeChangedEventHandler() {

			@Override
			public void on(FreehandModeChangedEvent event) {
				// hack due to event order
				setVisible(event);
			}
		});

		editorContext.getEventBus().addHandler(CommentModeEvent.TYPE, new CommentModeEventHandler() {
			@Override
			public void on(CommentModeEvent event) {
				showHideCommentMode(event.isEnabled());
			}
		});
		
		// DOM.sinkEvents((com.google.gwt.user.client.Element) freehandOn.cast(),
		// 		Event.ONCLICK);
		// DOM.setEventListener(
		// 		(com.google.gwt.user.client.Element) freehandOn.cast(),
		// 		new EventListener() {
		// 			@Override
		// 			public void onBrowserEvent(Event event) {
		// 				switch (DOM.eventGetType(event)) {
		// 				case Event.ONCLICK:
		// 					setVisible(false);
		// 					TopButtons.this.editorContext.getEventBus().fireEvent(new FreehandModeChangedEvent(false));
		// 					break;
		// 				}
		// 			}
		// 		});

		tapColor(colorize);
		tapLineWeight(lineWeight);
    init(this);

		// DOM.sinkEvents((com.google.gwt.user.client.Element) commentModeOn.cast(),
		// 		Event.ONCLICK);
		// DOM.setEventListener(
		// 		(com.google.gwt.user.client.Element) commentModeOn.cast(),
		// 		new EventListener() {
		// 			@Override
		// 			public void onBrowserEvent(Event event) {
		// 				switch (DOM.eventGetType(event)) {
		// 				case Event.ONCLICK:
		// 					hideCommentMode();
		// 					break;
		// 				}
		// 			}
		// 		});
	}

  private native int getLeftPosition()/*-{
    var tf = $wnd.document.querySelector("#tip-freehand")
    if (tf) {
      var rect = tf.getBoundingClientRect()

      return rect.left
    }

    return 0;
  }-*/;

	@Override
  protected void onAttach() {
  	super.onAttach();
  	EffectHelpers.tooltipper();
  }

	private native void tapColor(Element e)/*-{
		$wnd.Hammer(e, {preventDefault: true}).on('tap', function() {
			$wnd.$('.tooltip').hide()
			$wnd.$($doc).trigger('showFreehandColorMenu', e)
		})
	}-*/;

	private native void tapLineWeight(Element e)/*-{
		$wnd.Hammer(e, {preventDefault: true}).on('tap', function() {
			$wnd.$('.tooltip').hide()
			$wnd.globalStreams.showFreehandLineWeightStream.push(e)
		})
	}-*/;

  private native void init(TopButtons me)/*-{
    $wnd.globalStreams.noteSizeStrem.onValue(function(width) {
      $wnd.setTimeout(function() {
        // give time to render
        me.@net.sevenscales.editor.content.ui.TopButtons::applyPosition()();
      })
    })
  }-*/;

  private void applyPosition() {
    int left = getLeftPosition();
    panel.getElement().getStyle().setLeft(left, Unit.PX);
  }

	public void setVisible(FreehandModeChangedEvent event) {
		super.setVisible(Tools.isCommentMode());
		// freehandOn.getStyle().setDisplay(Display.NONE);
		colorize.getStyle().setDisplay(Display.NONE);
		if (event.isEnabled()) {
			// do not set visible if freehand mode is not on
			// this is due to initial load
			super.setVisible(event.isEnabled());
			// freehandOn.getStyle().setDisplay(Display.INLINE);
			colorize.getStyle().setDisplay(Display.INLINE_BLOCK);

      applyPosition();
  
			// String text = editorContext.<FreehandModeType>getAs(EditorProperty.FREEHAND_MODE_TYPE).toString();
			// if (event.isModeTypeChanged()) {
			// 	text = event.getModeType().toString();
			// }
			// // freehandOn.setInnerText("Freehand " + text);
			// freehandOn.setInnerText("Freehand ON");
		} 
	}

	private void hideCommentMode() {
		Tools.setCommentTool(false);
	}

	private void showHideCommentMode(boolean show) {
		Tools.setCommentTool(show);

		// if (Tools.isCommentMode()) {
		// 	super.setVisible(true);
		// 	commentModeOn.getStyle().setDisplay(Display.INLINE);
		// } else {
		// 	super.setVisible(editorContext.isTrue(EditorProperty.FREEHAND_MODE));
		// 	commentModeOn.getStyle().setDisplay(Display.NONE);
		// }
	}
	
}
