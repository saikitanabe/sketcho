package net.sevenscales.editor.content.ui;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.LibrarySelections.Library;
import net.sevenscales.editor.api.event.CreateElementEvent;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.impl.FastButton;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent.FreehandModeType;
import net.sevenscales.editor.api.event.StartSelectToolEvent;
import net.sevenscales.editor.content.ui.image.ImageSelection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
// import com.google.gwt.event.dom.client.MouseWheelEvent;
// import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.event.shared.HandlerRegistration;


public class UMLDiagramSelections extends Composite {
	private static final SLogger logger = SLogger.createLogger(UMLDiagramSelections.class);
	static {
		logger.addFilter(UMLDiagramSelections.class);
	}

	private static UMLDiagramSelectionsUiBinder uiBinder = GWT
			.create(UMLDiagramSelectionsUiBinder.class);
	
	public enum UMLDiagramGroup {
		CLASS_DIAGRAM("class-diagram", Library.SOFTWARE),
		USE_CASE_DIAGRAM("use-case-diagram", Library.SOFTWARE),
		ACTIVITY_DIAGRAM("activity-diagram", Library.SOFTWARE),
		SEQUENCE_DIAGRAM("sequence-diagram", Library.SOFTWARE),
		MINDMAP("mindmap-diagram", Library.MINDMAP),
		NONE("", Library.SOFTWARE);
		
		private String value;
		private Library library;
		
		private UMLDiagramGroup(String value, Library library) {
			this.value = value;
			this.library = library;
		}
		public String getValue() {return value;}
		public Library getLibrary() {
			return library;
		}
	}
	
	public enum UMLDiagramType {
		CLASS("SimpleClass", ElementType.CLASS, UMLDiagramGroup.CLASS_DIAGRAM), 
		USE_CASE("Use Case", ElementType.ELLIPSE, UMLDiagramGroup.USE_CASE_DIAGRAM),
		SEQUENCE("object", ElementType.SEQUENCE,  UMLDiagramGroup.SEQUENCE_DIAGRAM),
		ACTOR("Actor", ElementType.ACTOR,  UMLDiagramGroup.USE_CASE_DIAGRAM), 
		NOTE("Note", ElementType.NOTE,  UMLDiagramGroup.CLASS_DIAGRAM), 
		CHOICE("", ElementType.CHOICE, UMLDiagramGroup.ACTIVITY_DIAGRAM), 
		START("", ElementType.ACTIVITY_START, UMLDiagramGroup.ACTIVITY_DIAGRAM),
		END("", ElementType.ACTIVITY_END, UMLDiagramGroup.ACTIVITY_DIAGRAM),
		ACTIVITY("My Activity", ElementType.ACTIVITY, UMLDiagramGroup.ACTIVITY_DIAGRAM),
		FORK("", ElementType.FORK, UMLDiagramGroup.ACTIVITY_DIAGRAM),
		VFORK("", ElementType.FORK, UMLDiagramGroup.ACTIVITY_DIAGRAM),
		TEXT("Text", ElementType.TEXT_ITEM,  UMLDiagramGroup.SEQUENCE_DIAGRAM),
		PACKAGE("package", ElementType.PACKAGE, UMLDiagramGroup.CLASS_DIAGRAM),
		DB("Db", ElementType.STORAGE,  UMLDiagramGroup.SEQUENCE_DIAGRAM),
		MIND_CENTRAL_TOPIC("Central Topic", ElementType.MIND_CENTRAL, UMLDiagramGroup.MINDMAP),
		MIND_MAIN_TOPIC("Main Topic", ElementType.ACTIVITY, UMLDiagramGroup.MINDMAP),
		MIND_SUB_TOPIC("Sub Topic", ElementType.TEXT_ITEM, UMLDiagramGroup.MINDMAP),
		FREE_HAND("", ElementType.FREEHAND, UMLDiagramGroup.MINDMAP),
		COMMENT_THREAD("",  ElementType.COMMENT_THREAD, UMLDiagramGroup.CLASS_DIAGRAM),
		COMMENT("",  ElementType.CLASS, UMLDiagramGroup.CLASS_DIAGRAM),
		NONE("",  null, UMLDiagramGroup.NONE),
		COMPONENT("", ElementType.COMPONENT, UMLDiagramGroup.CLASS_DIAGRAM),
		SERVER("", ElementType.SERVER, UMLDiagramGroup.CLASS_DIAGRAM),
		SMILEY("", ElementType.SMILEY, UMLDiagramGroup.CLASS_DIAGRAM),
		FIREWALL("", ElementType.FIREWALL, UMLDiagramGroup.CLASS_DIAGRAM),
		POLYGON4("", ElementType.POLYGON4, UMLDiagramGroup.CLASS_DIAGRAM),
		POLYGON8("", ElementType.POLYGON8, UMLDiagramGroup.CLASS_DIAGRAM),
		RECT("", ElementType.RECT, UMLDiagramGroup.CLASS_DIAGRAM),
		TRIANGLE("", ElementType.TRIANGLE, UMLDiagramGroup.CLASS_DIAGRAM),
		CIRCLE("", ElementType.CIRCLE, UMLDiagramGroup.CLASS_DIAGRAM),
		CLOUD("", ElementType.CLOUD, UMLDiagramGroup.CLASS_DIAGRAM),
		WBROWSER("", ElementType.WEB_BROWSER, UMLDiagramGroup.CLASS_DIAGRAM),
		IPHONE("", ElementType.IPHONE, UMLDiagramGroup.CLASS_DIAGRAM),
		STAR5("", ElementType.STAR5, UMLDiagramGroup.CLASS_DIAGRAM),
		STAR4("", ElementType.STAR4, UMLDiagramGroup.CLASS_DIAGRAM),
		ARROW_DOWN("", ElementType.ARROW_DOWN, UMLDiagramGroup.CLASS_DIAGRAM),
		ARROW_RIGHT("", ElementType.ARROW_RIGHT, UMLDiagramGroup.CLASS_DIAGRAM),
		ARROW_UP("", ElementType.ARROW_UP, UMLDiagramGroup.CLASS_DIAGRAM),
		ARROW_LEFT("", ElementType.ARROW_LEFT, UMLDiagramGroup.CLASS_DIAGRAM),
		BUBBLE_LEFT("", ElementType.BUBBLE, UMLDiagramGroup.CLASS_DIAGRAM),
		BUBBLE_RIGHT("", ElementType.BUBBLE_R, UMLDiagramGroup.CLASS_DIAGRAM),
		ENVELOPE("", ElementType.ENVELOPE, UMLDiagramGroup.CLASS_DIAGRAM),
		IMAGE("", ElementType.IMAGE, UMLDiagramGroup.CLASS_DIAGRAM);
		
		private String value;
		private ElementType elementType;
		private UMLDiagramGroup group;

		UMLDiagramType(String value, ElementType elementType, UMLDiagramGroup group) {
			this.value = value;
			this.elementType = elementType;
			this.group = group;
		}
		
		public String getValue() {
			return value;
		}
		public ElementType getElementType() {
			return elementType;
		}
		public UMLDiagramGroup getGroup() {
			return group;
		}
	}
	
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

		if (!notConfluence()) {
			// hide comments on confluence
			comments.setVisible(false);
			// _comments.setVisible(false);
		}
		
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

	private void fire(UMLDiagramType elementType) {
		surface.getEditorContext().getEventBus().fireEvent(new CreateElementEvent(elementType, null, 0, 0));
	}
	
	@UiHandler("classf")
	public void onclass(ClickEvent event) {
		fire(UMLDiagramType.CLASS);
	}

	@UiHandler("usecase")
	public void onusecase(ClickEvent event) {
		fire(UMLDiagramType.USE_CASE);
	}
	@UiHandler("sequence")
	public void onsequence(ClickEvent event) {
		fire(UMLDiagramType.SEQUENCE);
	}
	@UiHandler("actor")
	public void onactor(ClickEvent event) {
		fire(UMLDiagramType.ACTOR);
	}
	@UiHandler("note")
	public void onnote(ClickEvent event) {
		fire(UMLDiagramType.NOTE);
	}
	@UiHandler("comments")
	public void oncomments(ClickEvent event) {
		fireCommentThread();
	}
	// @UiHandler("_comments")
	// public void oncommentsMind(ClickEvent event) {
	// 	fireCommentThread();
	// }
	
	private void fireCommentThread() {
		if (notConfluence()) {
			fire(UMLDiagramType.COMMENT_THREAD);
		}
	}

	@UiHandler("choice")
	public void onchoice(ClickEvent event) {
		fire(UMLDiagramType.CHOICE);
	}

	@UiHandler("start")
	public void onstart(ClickEvent event) {
		fire(UMLDiagramType.START);
	}

	@UiHandler("hfork")
	public void onfork(ClickEvent event) {
		fire(UMLDiagramType.FORK);
	}

	@UiHandler("vfork")
	public void onvfork(ClickEvent event) {
		fire(UMLDiagramType.VFORK);
	}

	@UiHandler("end")
	public void onend(ClickEvent event) {
		fire(UMLDiagramType.END);
	}

	@UiHandler("activity")
	public void onactivity(ClickEvent event) {
		fire(UMLDiagramType.ACTIVITY);
	}
	
	@UiHandler("storage")
	public void onstorage(ClickEvent event) {
		fire(UMLDiagramType.DB);
	}

	@UiHandler("text")
	public void ontext(ClickEvent event) {
		fire(UMLDiagramType.TEXT);
	}
	
	@UiHandler("umlpackagef")
	public void onpackage(ClickEvent event) {
		fire(UMLDiagramType.PACKAGE);
	}

	@UiHandler("_centtopic")
	public void onCentTopic(ClickEvent event) {
		fire(UMLDiagramType.MIND_CENTRAL_TOPIC);
	}
	@UiHandler("_maintopic")
	public void onMainTopic(ClickEvent event) {
		fire(UMLDiagramType.MIND_MAIN_TOPIC);
	}
	@UiHandler("_subtopic")
	public void onSubTopic(ClickEvent event) {
		fire(UMLDiagramType.MIND_SUB_TOPIC);
	}
	@UiHandler("_mindnote")
	public void onmindnote(ClickEvent event) {
		fire(UMLDiagramType.NOTE);
	}

	@UiHandler("umliconcomp")
	public void onumliconcomp(ClickEvent event) {
		fire(UMLDiagramType.COMPONENT);
	}
	@UiHandler("umliconserver")
	public void onumliconserver(ClickEvent event) {
		fire(UMLDiagramType.SERVER);
	}
	@UiHandler("umliconsmiley")
	public void onumliconsmiley(ClickEvent event) {
		fire(UMLDiagramType.SMILEY);
	}
	@UiHandler("umliconfirewall")
	public void onumliconfirewall(ClickEvent event) {
		fire(UMLDiagramType.FIREWALL);
	}
	@UiHandler("umliconpolygon5")
	public void onumliconpolygon5(ClickEvent event) {
		fire(UMLDiagramType.POLYGON4);
	}
	@UiHandler("umliconpolygon8")
	public void onumliconpolygon8(ClickEvent event) {
		fire(UMLDiagramType.POLYGON8);
	}
	@UiHandler("umliconrect")
	public void onumliconrect(ClickEvent event) {
		fire(UMLDiagramType.RECT);
	}
	@UiHandler("umlicontriangle")
	public void onumlicontriangle(ClickEvent event) {
		fire(UMLDiagramType.TRIANGLE);
	}
	@UiHandler("umliconcircle")
	public void onumliconcircle(ClickEvent event) {
		fire(UMLDiagramType.CIRCLE);
	}
	@UiHandler("umliconcloud")
	public void onumliconcloud(ClickEvent event) {
		fire(UMLDiagramType.CLOUD);
	}
	@UiHandler("umliconwbrowser")
	public void onumliconwbrowser(ClickEvent event) {
		fire(UMLDiagramType.WBROWSER);
	}
	@UiHandler("umliconiphone")
	public void onumliconiphone(ClickEvent event) {
		fire(UMLDiagramType.IPHONE);
	}
	@UiHandler("umliconstar5")
	public void onumliconstar5(ClickEvent event) {
		fire(UMLDiagramType.STAR5);
	}
	@UiHandler("umliconstar4")
	public void onumliconstar4(ClickEvent event) {
		fire(UMLDiagramType.STAR4);
	}
	@UiHandler("umliconarrowd")
	public void onumliconarrowd(ClickEvent event) {
		fire(UMLDiagramType.ARROW_DOWN);
	}
	@UiHandler("umliconarrowr")
	public void onumliconarrowr(ClickEvent event) {
		fire(UMLDiagramType.ARROW_RIGHT);
	}
	@UiHandler("umliconarrowu")
	public void onumliconarrowu(ClickEvent event) {
		fire(UMLDiagramType.ARROW_UP);
	}
	@UiHandler("umliconarrowl")
	public void onumliconarrowl(ClickEvent event) {
		fire(UMLDiagramType.ARROW_LEFT);
	}
	@UiHandler("umliconbubblel")
	public void onumliconbubblel(ClickEvent event) {
		fire(UMLDiagramType.BUBBLE_LEFT);
	}
	@UiHandler("umliconbubbler")
	public void onumliconbubbler(ClickEvent event) {
		fire(UMLDiagramType.BUBBLE_RIGHT);
	}
	@UiHandler("umliconletter")
	public void onumliconletter(ClickEvent event) {
		fire(UMLDiagramType.ENVELOPE);
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
		comments.setVisible(false);
	}

	public void showCommentElement() {
		if (notConfluence()) {
			comments.setVisible(true);
		}
	}

}
