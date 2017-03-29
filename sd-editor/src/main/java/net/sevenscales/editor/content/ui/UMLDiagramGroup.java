package net.sevenscales.editor.content.ui;

import net.sevenscales.editor.api.Library;

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
