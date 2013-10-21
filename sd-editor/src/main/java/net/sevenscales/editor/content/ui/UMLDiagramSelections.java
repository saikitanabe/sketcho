package net.sevenscales.editor.content.ui;

import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.LibrarySelections.Library;
import net.sevenscales.editor.api.event.CreateElementEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class UMLDiagramSelections extends Composite {

	private static UMLDiagramSelectionsUiBinder uiBinder = GWT
			.create(UMLDiagramSelectionsUiBinder.class);
	
	public enum UMLDiagramGroup {
		CLASS_DIAGRAM("class-diagram"),
		USE_CASE_DIAGRAM("use-case-diagram"),
		ACTIVITY_DIAGRAM("activity-diagram"),
		SEQUENCE_DIAGRAM("sequence-diagram"),
		MINDMAP("mindmap-diagram"),
		NONE("");
		
		private String value;
		
		private UMLDiagramGroup(String value) {
			this.value = value;
		}
		public String getValue() {return value;}
	}
	
	public enum UMLDiagramType {
		CLASS("SimpleClass", UMLDiagramGroup.CLASS_DIAGRAM), 
		USE_CASE("Use Case", UMLDiagramGroup.USE_CASE_DIAGRAM),
		SEQUENCE("object", UMLDiagramGroup.SEQUENCE_DIAGRAM),
		ACTOR("Actor", UMLDiagramGroup.USE_CASE_DIAGRAM), 
		NOTE("Note", UMLDiagramGroup.CLASS_DIAGRAM), 
		CHOICE("", UMLDiagramGroup.ACTIVITY_DIAGRAM), 
		START("", UMLDiagramGroup.ACTIVITY_DIAGRAM),
		END("", UMLDiagramGroup.ACTIVITY_DIAGRAM),
		ACTIVITY("My Activity", UMLDiagramGroup.ACTIVITY_DIAGRAM),
		FORK("", UMLDiagramGroup.ACTIVITY_DIAGRAM),
		VFORK("", UMLDiagramGroup.ACTIVITY_DIAGRAM),
		TEXT("Text", UMLDiagramGroup.SEQUENCE_DIAGRAM),
		PACKAGE("package", UMLDiagramGroup.CLASS_DIAGRAM),
		DB("Db", UMLDiagramGroup.SEQUENCE_DIAGRAM),
		MIND_CENTRAL_TOPIC("Central Topic", UMLDiagramGroup.MINDMAP),
		MIND_MAIN_TOPIC("Main Topic", UMLDiagramGroup.MINDMAP),
		MIND_SUB_TOPIC("Sub Topic", UMLDiagramGroup.MINDMAP),
		FREE_HAND("", UMLDiagramGroup.MINDMAP),
		COMMENT_THREAD("", UMLDiagramGroup.CLASS_DIAGRAM),
		COMMENT("", UMLDiagramGroup.CLASS_DIAGRAM),
		NONE("", UMLDiagramGroup.NONE);
		
		private String value;
		private UMLDiagramGroup group;

		UMLDiagramType(String value, UMLDiagramGroup group) {
			this.value = value;
			this.group = group;
		}
		
		public String getValue() {return value;}
		public UMLDiagramGroup getGroup() {
			return group;
		}
	}
	
	public interface SelectionHandler {
		void hidePopup();
	}

	interface UMLDiagramSelectionsUiBinder extends UiBinder<Widget, UMLDiagramSelections> {
	}

	private SelectionHandler selectionHandler;
	private EditorContext editorContext;
	
	@UiField HorizontalPanel diagramGroups;
//	@UiField ButtonElement freehandBtn;

	public UMLDiagramSelections(EditorContext editorContext) {
		this.editorContext = editorContext;
		initWidget(uiBinder.createAndBindUi(this));
		
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
		
		filterByGroup(Library.SOFTWARE);
	}

	public void setSelectionHandler(SelectionHandler selectionHandler) {
		this.selectionHandler = selectionHandler;
	}
	
	private void fire(UMLDiagramType elementType) {
		editorContext.getEventBus().fireEvent(new CreateElementEvent(elementType, 0, 0));
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
		fire(UMLDiagramType.COMMENT_THREAD);
	}
	@UiHandler("_comments")
	public void oncommentsMind(ClickEvent event) {
		fire(UMLDiagramType.COMMENT_THREAD);
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
	
//	@UiHandler("freehand")
//	public void onFreehand(ClickEvent event) {
//		editorContext.getEventBus().fireEvent(new FreehandModeChangedEvent(!editorContext.isTrue(EditorProperty.FREEHAND_MODE)));
//		selectionHandler.hidePopup();
//	}

	public void setGroup(UMLDiagramGroup group) {
		int index = findGroup(group);
		// swap group as second (2 due to centering selection => mouse is closer to correct element)
//		if (index > 0) { // do not swap if already first
			Widget groupWidget = diagramGroups.getWidget(index);
			diagramGroups.remove(index);
			if (diagramGroups.getWidgetCount() > 0) {
				diagramGroups.insert(groupWidget, 1);
			}
//		}
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

}
