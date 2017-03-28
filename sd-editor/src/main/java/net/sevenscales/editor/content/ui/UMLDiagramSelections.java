package net.sevenscales.editor.content.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
// import com.google.gwt.event.dom.client.MouseWheelEvent;
// import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.Library;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.api.event.CreateElementEvent;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent;
import net.sevenscales.editor.api.event.StartSelectToolEvent;
import net.sevenscales.editor.api.impl.FastButton;
import net.sevenscales.editor.content.ui.image.ImageSelection;


public class UMLDiagramSelections extends Composite {
	private static final SLogger logger = SLogger.createLogger(UMLDiagramSelections.class);
	static {
		logger.addFilter(UMLDiagramSelections.class);
	}

	private static UMLDiagramSelectionsUiBinder uiBinder = GWT
			.create(UMLDiagramSelectionsUiBinder.class);
		
	interface UMLDiagramSelectionsUiBinder extends UiBinder<Widget, UMLDiagramSelections> {
	}

	private DiagramSelectionHandler selectionHandler;
	private ISurfaceHandler surface;
	
	@UiField VerticalPanel diagramGroups;
	@UiField FastButton comments;
	// @UiField FastButton _comments;
	@UiField HTMLPanel contextMenuArea;
	@UiField FastButton showDiagrams;
	@UiField FastButton modifyImages;
	@UiField FastButton myimages;
	@UiField FastButton commentMode;
	@UiField SimplePanel imagesArea;
	@UiField FastButton uploadFile;
	@UiField ScrollPanel scrollPanel;

	private ImageSelection imageSelection;
	private Set<DiagramSelectionHandler.WhenScrolledHandler> scrollHandlers = new HashSet<DiagramSelectionHandler.WhenScrolledHandler>();
	
//	@UiField ButtonElement freehandBtn;

	public UMLDiagramSelections(ISurfaceHandler surface, DiagramSelectionHandler selectionHandler) {
		this.surface = surface;
		this.selectionHandler = selectionHandler;

		initWidget(uiBinder.createAndBindUi(this));

		// >>>>>>>>>>> Commented out 11.11.2014
		if (!notConfluence()) {
			// hide comments on confluence
			// on board comments are deprecated, and cannot be added any longer
			comments.setVisible(false);
		}
		// <<<<<<<<<<< Commented out 11.11.2014

		
		imagesArea.setVisible(false);
		modifyImages.setVisible(false);
		showDiagrams.setVisible(false);

		scrollPanel.addScrollHandler(new ScrollHandler() {
			public void onScroll(ScrollEvent event) {
				if (scrollPanel.getScrollPosition() + scrollPanel.getOffsetHeight() >= scrollPanel.getElement().getScrollHeight()) {
					for (DiagramSelectionHandler.WhenScrolledHandler w : scrollHandlers) {
						w.whenScrolled();
					}
				}
			}
		});

		handleCancel(this);
		configureConfluence();
//		editorContext.getEventBus().addHandler(FreehandModeChangedEvent.TYPE, new FreehandModeChangedEventHandler() {
//			@Override
//			public void on(FreehandModeChangedEvent event) {
//				if (event.isEnabled()) {
//					freehandBtn.addClassName("btn-success");
//					freehandBtn.setInnerHTML("<i class='icon-pen-white'></i>Freehand ON");
//				} else {
//					freehandBtn.removeClassName("btn-danger");
//					freehandBtn.removeClassName("btn-success");
//					freehandBtn.setInnerHTML("<i class='icon-pen'></i>Freehand");
//				}
//			}
//		});
		
		// filterByGroup(Library.SOFTWARE);
		// addMouseWheelHandler(new MouseWheelHandler() {
		// 	public void onMouseWheel(MouseWheelEvent event) {

		// 	}
		// });
	}

	private void configureConfluence() {
		if (surface.getEditorContext().isTrue(EditorProperty.CONFLUENCE_MODE)) {
			myimages.setVisible(false);
			uploadFile.setVisible(false);
			commentMode.setVisible(false);
		}
	}

	private native void handleCancel(UMLDiagramSelections me)/*-{
		$wnd.cancelStream.onValue(function() {
			me.@net.sevenscales.editor.content.ui.UMLDiagramSelections::cancel()();
		})
	}-*/;

	private void cancel() {
		selectionHandler.hidePopup();
	}

	@UiHandler("freehand")
	public void onFreeHand(ClickEvent event) {
		stopEvent(event);
		surface.getEditorContext().getEventBus().fireEvent(new FreehandModeChangedEvent(!surface.getEditorContext().isTrue(EditorProperty.FREEHAND_MODE)));
		selectionHandler.hidePopup();
	}

	// @UiHandler("freehandLines")
	// public void onFreeHandLines(ClickEvent event) {
	// 	stopEvent(event);
	// 	if (!surface.getEditorContext().isTrue(EditorProperty.FREEHAND_MODE)) {
	// 		surface.getEditorContext().getEventBus().fireEvent(new FreehandModeChangedEvent(!surface.getEditorContext().isTrue(EditorProperty.FREEHAND_MODE), FreehandModeChangedEvent.FreehandModeType.FREEHAND_LINES));
	// 	} else {
	// 		surface.getEditorContext().getEventBus().fireEvent(new FreehandModeChangedEvent(FreehandModeChangedEvent.FreehandModeType.FREEHAND_LINES));
	// 	}

	// 	selectionHandler.hidePopup();
	// }
	
	@UiHandler("select")
	public void onSelect(ClickEvent event) {
		stopEvent(event);
		surface.getEditorContext().getEventBus().fireEvent(new StartSelectToolEvent());
		selectionHandler.hidePopup();
	}

	@UiHandler("commentMode")
	public void onCommentMode(ClickEvent event) {
		stopEvent(event);
		Tools.toggleCommentMode();
		selectionHandler.hidePopup();
	}

	@UiHandler("sketchMode")
  public void onSketchMode(ClickEvent event) {
		stopEvent(event);
		Tools.toggleSketchMode();
		selectionHandler.hidePopup();
	}

	@UiHandler("myimages")
	public void onMyImages(ClickEvent event) {
		stopEvent(event);
		showMyImages();
	}

	@UiHandler("showDiagrams")
	public void onShowDiagrams(ClickEvent event) {
		stopEvent(event);
		showDiagrams();
	}

	@UiHandler("uploadFile")
	public void onUploadFile(ClickEvent event) {
		stopEvent(event);
		startUploadFile(event.getClientX(), event.getClientY());
	}

	private native void startUploadFile(int x, int y)/*-{
		$wnd.ngStartUploadFile(x, y);
	}-*/;

	private void showDiagrams() {
		diagramGroups.setVisible(true);
		showDiagrams.setVisible(false);
		myimages.setVisible(true);
		imagesArea.setVisible(false);
		modifyImages.setVisible(false);
	}

	private void showMyImages() {
		diagramGroups.setVisible(false);
		showDiagrams.setVisible(true);
		myimages.setVisible(false);
		modifyImages.setVisible(true);
		loadAndShowImages();
	}

	private void loadAndShowImages() {
		if (imageSelection == null) {
			imageSelection = new ImageSelection(surface, selectionHandler);
			imagesArea.setWidget(imageSelection);
		} else {
			imageSelection.loadImages();
		}
		imagesArea.setVisible(true);
	}

	private void stopEvent(ClickEvent event) {
		event.stopPropagation();
		event.preventDefault();
	}

  // public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
  //   return contextMenuArea.addDomHandler(handler, MouseWheelEvent.getType());
  // }

	private void fire(String elementType) {
		surface.getEditorContext().getEventBus().fireEvent(new CreateElementEvent(elementType, null, 0, 0));
	}
	
	@UiHandler("classf")
	public void onclass(ClickEvent event) {
		fire(UMLDiagramType.CLASS.getElementType().getValue());
	}

	@UiHandler("usecase")
	public void onusecase(ClickEvent event) {
		fire(UMLDiagramType.USE_CASE.getElementType().getValue());
	}
	@UiHandler("sequence")
	public void onsequence(ClickEvent event) {
		fire(UMLDiagramType.SEQUENCE.getElementType().getValue());
	}
	@UiHandler("actor")
	public void onactor(ClickEvent event) {
		fire(UMLDiagramType.ACTOR.getElementType().getValue());
	}
	@UiHandler("note")
	public void onnote(ClickEvent event) {
		fire(UMLDiagramType.NOTE.getElementType().getValue());
	}

	// >>>>>>>>>>> Commented out 11.11.2014
	@UiHandler("comments")
	public void oncomments(ClickEvent event) {
		fireCommentThread();
	}
	// <<<<<<<<<<< Commented out 11.11.2014

	// @UiHandler("_comments")
	// public void oncommentsMind(ClickEvent event) {
	// 	fireCommentThread();
	// }
	
	private void fireCommentThread() {
		if (notConfluence()) {
			fire(UMLDiagramType.COMMENT_THREAD.getElementType().getValue());
		}
	}

	@UiHandler("choice")
	public void onchoice(ClickEvent event) {
		fire(UMLDiagramType.CHOICE.getElementType().getValue());
	}

	@UiHandler("start")
	public void onstart(ClickEvent event) {
		fire(UMLDiagramType.START.getElementType().getValue());
	}

	@UiHandler("hfork")
	public void onfork(ClickEvent event) {
		fire(UMLDiagramType.FORK.getElementType().getValue());
	}

	@UiHandler("vfork")
	public void onvfork(ClickEvent event) {
		fire(UMLDiagramType.VFORK.getElementType().getValue());
	}

	@UiHandler("end")
	public void onend(ClickEvent event) {
		fire(UMLDiagramType.END.getElementType().getValue());
	}

	@UiHandler("activity")
	public void onactivity(ClickEvent event) {
		fire(UMLDiagramType.ACTIVITY.getElementType().getValue());
	}
	
	@UiHandler("storage")
	public void onstorage(ClickEvent event) {
		fire(UMLDiagramType.DB.getElementType().getValue());
	}

	@UiHandler("text")
	public void ontext(ClickEvent event) {
		fire(UMLDiagramType.TEXT.getElementType().getValue());
	}
	
	@UiHandler("umlpackagef")
	public void onpackage(ClickEvent event) {
		fire(UMLDiagramType.PACKAGE.getElementType().getValue());
	}

	@UiHandler("_centtopic")
	public void onCentTopic(ClickEvent event) {
		fire(UMLDiagramType.MIND_CENTRAL_TOPIC.getElementType().getValue());
	}
	@UiHandler("_maintopic")
	public void onMainTopic(ClickEvent event) {
		fire(UMLDiagramType.MIND_MAIN_TOPIC.getElementType().getValue());
	}
	@UiHandler("_subtopic")
	public void onSubTopic(ClickEvent event) {
		fire(UMLDiagramType.MIND_SUB_TOPIC.getElementType().getValue());
	}
	@UiHandler("_mindnote")
	public void onmindnote(ClickEvent event) {
		fire(UMLDiagramType.NOTE.getElementType().getValue());
	}

	@UiHandler("umliconcomp")
	public void onumliconcomp(ClickEvent event) {
		fire(UMLDiagramType.COMPONENT.getElementType().getValue());
	}
	@UiHandler("umliconserver")
	public void onumliconserver(ClickEvent event) {
		fire(UMLDiagramType.SERVER.getElementType().getValue());
	}
	@UiHandler("umliconsmiley")
	public void onumliconsmiley(ClickEvent event) {
		fire(UMLDiagramType.SMILEY.getElementType().getValue());
	}
	@UiHandler("umliconfirewall")
	public void onumliconfirewall(ClickEvent event) {
		fire(UMLDiagramType.FIREWALL.getElementType().getValue());
	}
	@UiHandler("umliconpolygon5")
	public void onumliconpolygon5(ClickEvent event) {
		fire(UMLDiagramType.POLYGON4.getElementType().getValue());
	}
	@UiHandler("umliconpolygon8")
	public void onumliconpolygon8(ClickEvent event) {
		fire(UMLDiagramType.POLYGON8.getElementType().getValue());
	}
	@UiHandler("umliconrect")
	public void onumliconrect(ClickEvent event) {
		fire(UMLDiagramType.RECT.getElementType().getValue());
	}
	@UiHandler("umlicontriangle")
	public void onumlicontriangle(ClickEvent event) {
		fire(UMLDiagramType.TRIANGLE.getElementType().getValue());
	}
	@UiHandler("umliconcircle")
	public void onumliconcircle(ClickEvent event) {
		fire(UMLDiagramType.CIRCLE.getElementType().getValue());
	}
	@UiHandler("umliconcloud")
	public void onumliconcloud(ClickEvent event) {
		fire(UMLDiagramType.CLOUD.getElementType().getValue());
	}
	@UiHandler("umliconwbrowser")
	public void onumliconwbrowser(ClickEvent event) {
		fire(UMLDiagramType.WBROWSER.getElementType().getValue());
	}
	@UiHandler("umliconiphone")
	public void onumliconiphone(ClickEvent event) {
		fire(UMLDiagramType.IPHONE.getElementType().getValue());
	}
	@UiHandler("umliconstar5")
	public void onumliconstar5(ClickEvent event) {
		fire(UMLDiagramType.STAR5.getElementType().getValue());
	}
	@UiHandler("umliconstar4")
	public void onumliconstar4(ClickEvent event) {
		fire(UMLDiagramType.STAR4.getElementType().getValue());
	}
	@UiHandler("umliconarrowd")
	public void onumliconarrowd(ClickEvent event) {
		fire(UMLDiagramType.ARROW_DOWN.getElementType().getValue());
	}
	@UiHandler("umliconarrowr")
	public void onumliconarrowr(ClickEvent event) {
		fire(UMLDiagramType.ARROW_RIGHT.getElementType().getValue());
	}
	@UiHandler("umliconarrowu")
	public void onumliconarrowu(ClickEvent event) {
		fire(UMLDiagramType.ARROW_UP.getElementType().getValue());
	}
	@UiHandler("umliconarrowl")
	public void onumliconarrowl(ClickEvent event) {
		fire(UMLDiagramType.ARROW_LEFT.getElementType().getValue());
	}
	@UiHandler("umliconbubblel")
	public void onumliconbubblel(ClickEvent event) {
		fire(UMLDiagramType.BUBBLE_LEFT.getElementType().getValue());
	}
	@UiHandler("umliconbubbler")
	public void onumliconbubbler(ClickEvent event) {
		fire(UMLDiagramType.BUBBLE_RIGHT.getElementType().getValue());
	}
	@UiHandler("umliconletter")
	public void onumliconletter(ClickEvent event) {
		fire(UMLDiagramType.ENVELOPE.getElementType().getValue());
	}

	// @UiHandler("contextMenuArea")
	// public void onContextMenuArea(MouseWheelEvent event) {
	// 	logger.debug("onContextMenuArea...");
	// }

	private boolean notConfluence() {
		return !surface.getEditorContext().isTrue(EditorProperty.CONFLUENCE_MODE);
	}	
	
//	@UiHandler("freehand")
//	public void onFreehand(ClickEvent event) {
//		editorContext.getEventBus().fireEvent(new FreehandModeChangedEvent(!editorContext.isTrue(EditorProperty.FREEHAND_MODE)));
//		selectionHandler.hidePopup();
//	}

	public void setGroup(UMLDiagramGroup group) {
		// NOTE: for now group focus is not supported
		// - becomes too complex, and maybe difficult to use
		
		// int index = findGroup(group);
		// Widget groupWidget = diagramGroups.getWidget(index);
		// diagramGroups.remove(index);
		// if (diagramGroups.getWidgetCount() > 0) {
		// 	diagramGroups.insert(groupWidget, 1);
		// }
	}

	private int findGroup(UMLDiagramGroup group) {
		int count = diagramGroups.getWidgetCount();
		for (int i = 0; i < count; ++i) {
			String classNames = diagramGroups.getWidget(i).getElement().getClassName();
			UMLDiagramGroup g = diagramGroup(classNames);
			if (g.equals(group)) {
				return i;
			}
		}
		return -1;
	}

	private UMLDiagramGroup diagramGroup(String classNames) {
		UMLDiagramGroup[] groups = UMLDiagramGroup.values();
		for (UMLDiagramGroup g : groups) {
			if (classNames.contains(g.getValue())) {
				return g;
			}
		}
		return UMLDiagramGroup.NONE;
	}

	public void sortByGroup(Library library) {
		int count = diagramGroups.getWidgetCount();
		List<Widget> asfirst = new ArrayList<Widget>();
		for (int i = 0; i < count; ++i) {
			Widget group = diagramGroups.getWidget(i);
			String classNames = group.getElement().getClassName();
			UMLDiagramGroup g = diagramGroup(classNames);
			if (library.equals(g.getLibrary())) {
				// reverse order, can be switched just by looping
				// asfirst.add(0, i);
				asfirst.add(0, group);
			}
		}

		for (Widget group : asfirst) {
			diagramGroups.insert(group, 0);
		}
	}

	public void filterByGroup(Library library) {
		int count = diagramGroups.getWidgetCount();
		for (int i = 0; i < count; ++i) {
			String classNames = diagramGroups.getWidget(i).getElement().getClassName();
			UMLDiagramGroup g = diagramGroup(classNames);
			switch (library) {
			case MINDMAP:
				if (!UMLDiagramGroup.MINDMAP.equals(g)) {
					diagramGroups.getWidget(i).setVisible(false);
				} else {
					diagramGroups.getWidget(i).setVisible(true);
				}
				break;
			default:
				if (UMLDiagramGroup.MINDMAP.equals(g)) {
					diagramGroups.getWidget(i).setVisible(false);
				} else {
					diagramGroups.getWidget(i).setVisible(true);
				}
				break;
			}
		}
	}

	public void addScrollHandler(DiagramSelectionHandler.WhenScrolledHandler scrollHandler) {
		scrollHandlers.add(scrollHandler);
	}

	public void hideCommentElement() {
		// >>>>>>>>>>> Commented out 11.11.2014
		// comments.setVisible(false);
		// >>>>>>>>>>> Commented out 11.11.2014
	}

	public void showCommentElement() {
		// >>>>>>>>>>> Commented out 11.11.2014
		// if (notConfluence()) {
		// 	comments.setVisible(true);
		// }
		// >>>>>>>>>>> Commented out 11.11.2014
	}

}
