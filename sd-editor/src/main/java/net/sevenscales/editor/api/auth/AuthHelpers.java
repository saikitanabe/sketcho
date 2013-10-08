package net.sevenscales.editor.api.auth;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.domain.CommentDTO;
import net.sevenscales.editor.uicomponents.uml.CommentElement;

/**
* Provides authorization utilities to prevent user doing wrong actions.
* Server is having a real authorization check.
*/
public class AuthHelpers {

	public static boolean allowedToEdit(Diagram diagram) {
		boolean result = true;
		if (diagram instanceof CommentElement) {
			result = false;
			if (diagram.getDiagramItem() instanceof CommentDTO) {
				CommentDTO c = (CommentDTO) diagram.getDiagramItem();
				String username = diagram.getSurfaceHandler().getEditorContext().getCurrentUser();
				if (username != null && username.equals(c.getUsername())) {
					result = true;
				}
			}
		}
		return result;
	}
}