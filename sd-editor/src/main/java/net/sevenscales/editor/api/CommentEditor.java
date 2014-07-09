package net.sevenscales.editor.api;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FocusPanel;
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
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.EditorCommon;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.FastElementButton;
import net.sevenscales.editor.api.event.SelectionMouseUpEvent;
import net.sevenscales.editor.api.event.SelectionMouseUpEventHandler;
import net.sevenscales.editor.api.event.CommentDeletedEvent;
import net.sevenscales.editor.api.event.CommentDeletedEventHandler;
import net.sevenscales.editor.api.event.CommentThreadDeletedEvent;
import net.sevenscales.editor.api.event.CommentThreadDeletedEventHandler;
import net.sevenscales.editor.api.event.CommentThreadModifiedOutsideEvent;
import net.sevenscales.editor.api.event.CommentThreadModifiedOutsideEventHandler;
import net.sevenscales.editor.api.event.BoardRemoveDiagramsEvent;
import net.sevenscales.editor.api.event.BoardRemoveDiagramsEventHandler;
import net.sevenscales.editor.api.event.EditDiagramPropertiesStartedEvent;
import net.sevenscales.editor.api.event.EditDiagramPropertiesStartedEventHandler;
import net.sevenscales.editor.content.utils.ScaleHelpers;
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
	private static final int HINT_INCREMENT = 50;

  private static CommentEditorUiBinder uiBinder = GWT.create(CommentEditorUiBinder.class);
	interface CommentEditorUiBinder extends UiBinder<Widget, CommentEditor> {
	}


	private ISurfaceHandler surface;
	@UiField Widget commentEditorContainer;
	@UiField DivElement commentHintBox;
	@UiField Label writeComment;
	@UiField DivElement commentArea;
	@UiField TextArea textArea;
	@UiField AnchorElement comment;
	private CustomPopupPanel popup;
	private PopupPanel donePopup;
	private EditorCommon editorCommon;
	private CommentThreadElement commentThread;
	private boolean dontCreateComment;

	CommentEditor(ISurfaceHandler surface) {
		this.surface = surface;

		initWidget(uiBinder.createAndBindUi(this));

		// this.textArea = new TextArea();
		this.textArea.setStyleName(PROPERTIES_EDITOR_STYLE);

		this.popup = new CustomPopupPanel(textArea);
		this.popup.setStyleName("comment-editor-popup");
		// popup.setStyleName("propertyPopup");
		// autohide is not enabled since property editor is closed manually and autohide causes problems
		this.popup.setAutoHideEnabled(false);
		this.popup.setAutoHideOnHistoryEventsEnabled(false);

		this.popup.setWidget(this);

		donePopup = new PopupPanel();
		HTML doneButton = new HTML(SafeHtmlUtils.fromSafeConstant("<button class='btn btn-mini'>Done</button>"));
		donePopup.setWidget(doneButton);

		doneButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				markDone();
			}
		});

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

		popup.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				// dontCreateComment: comment creation if thread is deleted in OT
				// editor open: dont' delete comment thread when write comment hint is closed
				if (!dontCreateComment && 
						CommentEditor.this.surface.getEditorContext().isTrue(EditorProperty.PROPERTY_EDITOR_IS_OPEN)) {
					createComment();
				}
			}
		});

		new FastElementButton(comment).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				logger.debug("Comment {}...", textArea.getText());
				hide();
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

		surface.getEditorContext().getEventBus().addHandler(EditDiagramPropertiesStartedEvent.TYPE, new EditDiagramPropertiesStartedEventHandler() {
			public void on(EditDiagramPropertiesStartedEvent event) {
				donePopup.hide();
			}
		});

		surface.getEditorContext().getEventBus().addHandler(CommentThreadDeletedEvent.TYPE, removeHandler);
	}

	/**
	* Remove handler is needed if comment thread is removed in OT and current user
	* has not send the comment. Then comment would not have parent any longer
	* since it is removed. So lets hide editor and message is kept in comment
	* editor and user can resend in new comment thread that comment.
	*/
	private CommentThreadDeletedEventHandler removeHandler = new CommentThreadDeletedEventHandler() {
		@Override
		public void on(CommentThreadDeletedEvent event) {
			if (commentThread != null && event.getCommentThreadElement().getDiagramItem().getClientId().equals(commentThread.getDiagramItem().getClientId())) {
				dontCreateComment = true;
				hide();
				dontCreateComment = false;
			}
		}
	};

	private void markDone() {
		if (commentThread != null) {
			commentThread.markDone();
			commentThread.hideResizeHandles();
			commentThread.hideConnectionHelpers();
			commentThread = null;
			hide();
		}
	}

	private void createComment() {
		if (isEditorNotEmpty()) {
			commentThread.createComment(textArea.getText());
			textArea.setText("");
		} else {
			if (commentThread.getChildElements().size() == 0) {
				commentThread.remove();
			}
		}
	}

	private boolean isEditorNotEmpty() {
		return !"".equals(textArea.getText());
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

			// commentHintBox.getStyle().setBackgroundColor("#" + commentThread.getBackgroundColor());
			// commentHintBox.getStyle().setProperty("background", commentThread.getBackgroundColorAsColor().toRgbaCss());
			// commentHintBox.getStyle().setOpacity(commentThread.getBackgroundColorAsColor().opacity);
			hideCommentHintBox();

			// commentArea.getStyle().setBackgroundColor("#" + commentThread.getBackgroundColor());
			if (commentThread.getChildElements().size() == 0) {
				commentHintBox.getStyle().setProperty("background", commentThread.getBackgroundColorAsColor().toRgbaCss());
				commentArea.getStyle().setProperty("background", commentThread.getBackgroundColorAsColor().toRgbaCss());
				commentThread.setVisible(false);
			} else {
				commentHintBox.getStyle().setProperty("background", "transparent");
				commentArea.getStyle().setProperty("background", "transparent");
			}
			// commentArea.getStyle().setOpacity(commentThread.getBackgroundColorAsColor().opacity);
			commentArea.getStyle().setColor("#" + diagram.getTextColor());
			commentArea.getStyle().setWidth(diagram.getWidth(), Unit.PX);
			textArea.getElement().getStyle().setWidth(diagram.getWidth() - 30, Unit.PX);
			textArea.getElement().getStyle().setHeight(50, Unit.PX);
			textArea.getElement().getStyle().setColor("#" + diagram.getTextColor().toHexString());
			showCommentArea();

			// if comment thread should be increased dyncamically this is a working
			// method!
			commentThread.setIncrementHeight(incrementSize);

			popup.show();
		}
	}

	private void positionPopupBy(Diagram commentThreadCandidate) {
		if (this.commentThread == commentThreadCandidate) {
			MatrixPointJS point = MatrixPointJS.createUnscaledPoint(this.commentThread.getLeft(), this.commentThread.getTop(), surface.getScaleFactor());
			int x = point.getX() + surface.getRootLayer().getTransformX() + surface.getAbsoluteLeft() + 1;
			int y = point.getY() + surface.getRootLayer().getTransformY() + surface.getAbsoluteTop() - 15;

			if (commentThread.getChildElements().size() == 0) {
				y += 15;
			}
	
			if (commentThread.getChildElements().size() > 0) {
				// not first
				y += ScaleHelpers.unscaleValue(this.commentThread.getHeight(), surface.getScaleFactor());
			}
			popup.setPopupPosition(x, y);

			if (commentThread.getChildElements().size() > 0) {
				// Diagram firstChild = commentThread.getChildElements().get(0);
				// MatrixPointJS childPoint = MatrixPointJS.createUnscaledPoint(firstChild.getLeft(), firstChild.getTop(), surface.getScaleFactor());
				// int top = ScaleHelpers.unscaleValue(childPoint.getY(), surface.getScaleFactor()) + surface.getRootLayer().getTransformY() + surface.getAbsoluteTop() + 3;
				int top = point.getY() + surface.getRootLayer().getTransformY() + surface.getAbsoluteTop() + 20;
				int donex = + ScaleHelpers.unscaleValue(commentThread.getWidth() - 52, surface.getScaleFactor());
				donePopup.setPopupPosition(x + donex, top);
			}
		}
	}

	private void showWriteComment(Diagram diagram) {
		if (diagram instanceof CommentThreadElement) {
			this.commentThread = (CommentThreadElement) diagram;
			commentThread.showResizeHandles();
			writeComment.getElement().getStyle().setWidth(diagram.getWidth() - 30, Unit.PX);
			commentHintBox.getStyle().setWidth(diagram.getWidth() - 2, Unit.PX);
			show(commentThread, calcEditorIncrement(commentThread, true));
			showCommentHintBox();
			hideCommentArea();
		}		
	}

	private void showCommentArea() {
		commentArea.getStyle().setDisplay(Display.BLOCK);
	}
	private void hideCommentArea() {
		commentArea.getStyle().setDisplay(Display.NONE);
	}

	private void showCommentHintBox() {
		commentHintBox.getStyle().setDisplay(Display.BLOCK);
		if (commentThread.getChildElements().size() > 0) {
			donePopup.show();
		}
	}

	private void hideCommentHintBox() {
		commentHintBox.getStyle().setDisplay(Display.NONE);
	}

	private void hide() {
		if (popup.isShowing() && commentThread != null) {
			commentThread.hideResizeHandles();
			commentThread.restoreSize();
			commentThread.setVisible(true);
		}
		popup.hide();
		donePopup.hide();
		commentThread = null;
		CommentEditor.this.editorCommon.fireEditorClosed();
	}

}