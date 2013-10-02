package net.sevenscales.editor.diagram.utils;

import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.LibrarySelections.Library;
import net.sevenscales.editor.content.RelationShipType;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.uicomponents.uml.NoteElement;
import net.sevenscales.editor.uicomponents.uml.CommentThreadElement;

public class RelationshipHelpers {
	public static String relationship(Diagram sender, EditorContext editorContext) {
		return relationship(sender, editorContext, null);
	}
	
  public static String relationship(Diagram sender, EditorContext editorContext, Diagram theOtherEnd) {
  	// it is possible change default relationship type from toolbar
//    Diagram d = toolSelection.selectedElement();
//    if (d instanceof Relationship2) {
//      return d.getText();
//    }
  	
  	if (editorContext.get(EditorProperty.CURRENT_LIBRARY).equals(Library.MINDMAP)) {
  		// MINDMAP LIBRARY overrides default connection type
  		return RelationShipType.LINE.getValue();
  	}
  	
    // TODO should be diagram property, it would know wheather or not always have certain type of connection
  	if (sender instanceof NoteElement || (theOtherEnd != null && theOtherEnd instanceof NoteElement)) {
  		// note element has always default type
  		return RelationShipType.DEPENDANCY.getValue();
  	}

    if (sender instanceof CommentThreadElement || (theOtherEnd != null && theOtherEnd instanceof CommentThreadElement)) {
      // comments element has always default type
      return RelationShipType.DEPENDANCY.getValue();
    }
    
		Object type = editorContext.get(EditorProperty.CURRENT_RELATIONSHIP_TYPE);
		if (type != null && type instanceof RelationShipType) {
			return ((RelationShipType) type).getValue();
		}

    return sender.getDefaultRelationship();
  }
}
