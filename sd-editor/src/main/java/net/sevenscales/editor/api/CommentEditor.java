package net.sevenscales.editor.api;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.dom.client.Style.*;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.EditorCommon;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.FastElementButton;
import net.sevenscales.editor.api.event.SelectionMouseUpEvent;
import net.sevenscales.editor.api.event.SelectionMouseUpEventHandler;
import net.sevenscales.editor.api.event.CommentDeletedEvent;
import net.sevenscales.editor.api.event.CommentDeletedEventHandler;
import net.sevenscales.editor.api.event.CommentThreadModifiedOutsideEvent;
import net.sevenscales.editor.api.event.CommentThreadModifiedOutsideEventHandler;

import net.sevenscales.editor.content.ui.CustomPopupPanel;

import net.sevenscales.editor.diagram.Diagram;

import net.sevenscales.editor.uicomponents.uml.CommentThreadElement;
import net.sevenscales.editor.uicomponents.uml.CommentElement;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;

import net.sevenscales.domain.utils.SLogger;


class CommentEditor  extends Composite {
	private static final SLogger logger = SLogger.createLogger(CommentEditor.class);
	private static final String PROPERTIES_EDITOR_STYLE = "properties-TextArea2";
	private static final int EDITOR_INCREMENT = 90;
	private static final int EDITOR_INCREMENT_FIRST = 45;
	private static final int HINT_INCREMENT = 40;

  private static CommentEditorUiBinder uiBinder = GWT.create(CommentEditorUiBinder.class);
	interface CommentEditorUiBinder extends UiBinder<Widget, CommentEditor> {
	}


	private ISurfaceHandler surface;
	@UiField Label writeComment;
	@UiField DivElement commentArea;
	@UiField TextArea textArea;
	@UiField AnchorElement comment;
	private CustomPopupPanel popup;
	private EditorCommon editorCommon;
	private CommentThreadElement commentThread;

	CommentEditor(ISurfaceHandler surface) {
		this.surface = surface;

		initWidget(uiBinder.createAndBindUi(this));

		// this.textArea = new TextArea();
		this.textArea.setStyleName(PROPERTIES_EDITOR_STYLE);

		this.popup = new CustomPopupPanel(textArea);
		// popup.setStyleName("propertyPopup");
		// autohide is not enabled since property editor is closed manually and autohide causes problems
		this.popup.setAutoHideEnabled(false);
		this.popup.setAutoHideOnHistoryEventsEnabled(false);

		this.popup.setWidget(this);

		editorCommon = new EditorCommon(surface, new EditorCommon.HideEditor() {
			public void hide() {
				CommentEditor.this.hide();
			}
		});

		writeComment.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showEditor(commentThread);
			}
		});

		new FastElementButton(comment).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				logger.debug("Comment {}...", textArea.getText());
				createComment();
				clearAndHide();
			}
		});

		surface.getEditorContext().getEventBus().addHandler(SelectionMouseUpEvent.TYPE, new SelectionMouseUpEventHandler() {
			public void onSelection(SelectionMouseUpEvent event) {
				if (event.isOnlyOne()) {
					Diagram selected = event.getFirst();
					if (selected instanceof CommentElement) {
						selected = ((CommentElement) selected).getParentThread();
					}
					showWriteComment(selected);
				}
			}
		});

		surface.getEditorContext().getEventBus().addHandler(CommentDeletedEvent.TYPE, new CommentDeletedEventHandler() {
			public void on(CommentDeletedEvent event) {
				hide();
			}
		});

		surface.getEditorContext().getEventBus().addHandler(CommentThreadModifiedOutsideEvent.TYPE, new CommentThreadModifiedOutsideEventHandler() {
			public void on(final CommentThreadModifiedOutsideEvent event) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						positionPopupBy(event.getCommentThreadElement());
					}
				});
			}
		});
	}

	private void clearAndHide() {
		textArea.setText("");
		hide();
	}

	private void createComment() {
		commentThread.createComment(textArea.getText());
	}

	void showEditor(Diagram diagram) {
		show(diagram, calcEditorIncrement(diagram, false));
		editorCommon.fireEditorOpen();
	}

	private int calcEditorIncrement(Diagram diagram, boolean calcHintSize) {
 		int result = calcHintSize ? 0 : EDITOR_INCREMENT_FIRST;
		if (diagram instanceof CommentThreadElement) {
			if (((CommentThreadElement) diagram).getChildElements().size() > 0) {
				result = calcHintSize ? HINT_INCREMENT : EDITOR_INCREMENT;
			}
		}
		return result;
	}

	private void show(Diagram diagram, int incrementSize) {
		// TODO get parent from CommentElement; store also comment element
		// if need to update comment

		if (popup.isShowing()) {
			// reset height state
			hide();
		}

		if (diagram instanceof CommentThreadElement) {
			this.commentThread = (CommentThreadElement) diagram;
			logger.debug("show CommentEditor...");
			surface.getMouseDiagramManager().getDragHandler().releaseDrag();

			positionPopupBy(this.commentThread);

			textArea.getElement().getStyle().setBackgroundColor("#" + commentThread.getBackgroundColor());
			textArea.getElement().getStyle().setColor("#" + diagram.getTextColor());
			textArea.getElement().getStyle().setWidth(diagram.getTextAreaWidth(), Unit.PX);
			textArea.getElement().getStyle().setHeight(50, Unit.PX);
			commentArea.getStyle().setVisibility(Visibility.VISIBLE);

			writeComment.setVisible(false);

			commentThread.setIncrementHeight(incrementSize);

			popup.show();
		}
	}

	private void positionPopupBy(Diagram commentThreadCandidate) {
		if (this.commentThread == commentThreadCandidate) {
			MatrixPointJS point = MatrixPointJS.createUnscaledPoint(this.commentThread.getTextAreaLeft(), this.commentThread.getTextAreaTop(), surface.getScaleFactor());
			int x = point.getX() + surface.getRootLayer().getTransformX() + surface.getAbsoluteLeft();
			int y = point.getY() + surface.getRootLayer().getTransformY() + surface.getAbsoluteTop() + 5;
	
			if (commentThread.getChildElements().size() > 0) {
				// not first
				y += this.commentThread.getHeight();
			}
			popup.setPopupPosition(x, y);
		}
	}

	private void showWriteComment(Diagram diagram) {
		if (diagram instanceof CommentThreadElement) {
			this.commentThread = (CommentThreadElement) diagram;
			commentThread.showResizeHandles();
			writeComment.getElement().getStyle().setWidth(diagram.getTextAreaWidth(), Unit.PX);
			show(commentThread, calcEditorIncrement(commentThread, true));
			writeComment.setVisible(true);
			commentArea.getStyle().setVisibility(Visibility.HIDDEN);
		}		
	}

	private void hide() {
		if (popup.isShowing() && commentThread != null) {
			commentThread.hideResizeHandles();
			commentThread.restoreSize();
		}
		commentThread = null;
		popup.hide();
		CommentEditor.this.editorCommon.fireEditorClosed();
	}

}