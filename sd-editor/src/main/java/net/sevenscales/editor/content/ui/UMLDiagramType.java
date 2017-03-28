package net.sevenscales.editor.content.ui;

import net.sevenscales.domain.ElementType;

public enum UMLDiagramType {
	CLASS("SimpleClass", ElementType.CLASS, UMLDiagramGroup.CLASS_DIAGRAM), 
	USE_CASE("Use Case", ElementType.USE_CASE, UMLDiagramGroup.USE_CASE_DIAGRAM),
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
	COMPONENT("Component", ElementType.COMPONENT, UMLDiagramGroup.CLASS_DIAGRAM),
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