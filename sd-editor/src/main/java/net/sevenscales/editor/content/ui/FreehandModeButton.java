package net.sevenscales.editor.content.ui;

import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent.FreehandModeType;
import net.sevenscales.editor.api.event.FreehandModeChangedEventHandler;
import net.sevenscales.editor.api.impl.FastButton;
import net.sevenscales.domain.utils.SLogger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;

public class FreehandModeButton extends HTML {
	private static SLogger logger = SLogger.createLogger(FreehandModeButton.class);

	private EditorContext editorContext;

	public FreehandModeButton(EditorContext editorContext) {
		this.editorContext = editorContext;
		
		editorContext.getEventBus().addHandler(FreehandModeChangedEvent.TYPE, new FreehandModeChangedEventHandler() {
			@Override
			public void on(FreehandModeChangedEvent event) {
				if (event.isEnabled()) {
					enabled(event);
				} else {
					disabled();
				}
			}
		});

		setHTML(SafeHtmlUtils
				.fromSafeConstant("<button class='btn' style='white-space: nowrap;'><i class='context-icon-pen'></i>Freehand</button>"));
		// freehandMode.setStyleName("btn-freehand");
		// freehandMode.addStyleName("btn-freehand-disabled");
		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// class is late at least on firefox
				if (FreehandModeButton.this.editorContext.isTrue(EditorProperty.FREEHAND_MODE)) {
					FreehandModeButton.this.editorContext.getEventBus().fireEvent(new FreehandModeChangedEvent(false));
				} else {
					FreehandModeButton.this.editorContext.getEventBus().fireEvent(new FreehandModeChangedEvent(true));
				}
			}
		});
	}
	
	private void disabled() {
		logger.debug("freehand disabled");
		setHTML(SafeHtmlUtils
				.fromSafeConstant("<button class='btn' style='white-space: nowrap;'><i class='context-icon-pen'></i>Freehand</button>"));
		editorContext.set(EditorProperty.FREEHAND_MODE, false);
	}
	
	private void enabled(FreehandModeChangedEvent event) {
		logger.debug("freehand enabled");
		setHTML(SafeHtmlUtils
						.fromSafeConstant("<button class='btn btn-success' style='white-space: nowrap;'><i class='context-icon-pen-white'></i>Freehand</button>"));
		if (event.isModeTypeChanged()) {
			editorContext.set(EditorProperty.FREEHAND_MODE_TYPE, event.getModeType());
		}
		editorContext.set(EditorProperty.FREEHAND_MODE, true);
	}
}
