package net.sevenscales.editor.api;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.dom.client.Style.*;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.EditorCommon;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.FastElementButton;

import net.sevenscales.editor.content.ui.CustomPopupPanel;

import net.sevenscales.editor.diagram.Diagram;

import net.sevenscales.editor.uicomponents.uml.CommentThreadElement;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;

import net.sevenscales.domain.utils.SLogger;


class CommentEditor  extends Composite {
	private static final SLogger logger = SLogger.createLogger(CommentEditor.class);
	private static final String PROPERTIES_EDITOR_STYLE = "properties-TextArea2";

  private static CommentEditorUiBinder uiBinder = GWT.create(CommentEditorUiBinder.class);
	interface CommentEditorUiBinder extends UiBinder<Widget, CommentEditor> {
	}


	private ISurfaceHandler surface;
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

		editorCommon = new EditorCommon(surface, new EditorCommon.HideEditor() {
			public void hide() {
				CommentEditor.this.hide();
			}
		});

		this.popup.setWidget(this);

		new FastElementButton(comment).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				logger.debug("Comment {}...", textArea.getText());
				createComment();
				clearAndHide();
			}
		});
	}

	private void clearAndHide() {
		textArea.setText("");
		hide();
	}

	private void createComment() {
		commentThread.createComment(textArea.getText());

    // commentThread.addComment(commentElement);

	    // editorCommon.fireChanged(thread);

   	// JSONObject json = new JSONObject();
    // json.put("pthread", new JSONString(commentThread.getDiagramItem().getClientId()));

    // logger.debug("pthread: {}", json.toString());
		// commentElement.getDiagramItem().setCustomData(json.toString());
		// commentElement.setCustomData(json.toString());


		// commentThread.setText(textArea.getText());
		// editorCommon.fireChanged(ne);
	}

	void show(Diagram diagram) {
		// TODO get parent from CommentElement; store also comment element
		// if need to update comment
		if (diagram instanceof CommentThreadElement) {
			this.commentThread = (CommentThreadElement) diagram;
			logger.debug("show CommentEditor...");
			surface.getMouseDiagramManager().getDragHandler().releaseDrag();

			MatrixPointJS point = MatrixPointJS.createUnscaledPoint(diagram.getTextAreaLeft(), diagram.getTextAreaTop(), surface.getScaleFactor());
			int x = point.getX() + surface.getRootLayer().getTransformX() + surface.getAbsoluteLeft();
			int y = point.getY() + surface.getRootLayer().getTransformY() + surface.getAbsoluteTop() + diagram.getHeight();

			popup.setPopupPosition(x, y);

			textArea.getElement().getStyle().setBackgroundColor(Theme.getCurrentThemeName().getBoardBackgroundColor());
			textArea.getElement().getStyle().setColor("#" + diagram.getTextColor());
			textArea.getElement().getStyle().setWidth(diagram.getTextAreaWidth(), Unit.PX);
			textArea.getElement().getStyle().setHeight(50, Unit.PX);
			textArea.setVisible(true);

			popup.show();

			editorCommon.fireEditorOpen();
		}
	}

	private void hide() {
		// Diagram selected = surface.getSelectionHandler().getOnlyOneSelected();
		// if (selected != null && selected instanceof CommentThreadElement) {
		// 	show(selected);
		// } else {
			popup.hide();
			CommentEditor.this.editorCommon.fireEditorClosed();
		// }
	}

}