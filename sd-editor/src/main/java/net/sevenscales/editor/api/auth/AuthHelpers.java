package net.sevenscales.editor.api.auth;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.domain.CommentDTO;
import net.sevenscales.editor.uicomponents.uml.CommentElement;
import net.sevenscales.editor.uicomponents.uml.CommentThreadElement;

/**
* Provides authorization utilities to prevent user doing wrong actions.
* Server is having a real authorization check.
*/
public class AuthHelpers {

	public static boolean allowedToDelete(Diagram[] diagrams) {
		for (Diagram d : diagrams) {
			if (!allowedToDelete(d)) {
				// if even one fails do not allow to delete
				return false;
			}
		}
		return true;
	}

	public static boolean allowedToDelete(Diagram diagram) {
		if (diagram instanceof CommentThreadElement) {
			// comment thread doesn't support delete at this moment
			return false;
		}

		// same rule as with edit, comment owner needs to match
		return allowedToEdit(diagram);
	}

	public static boolean allowedToEdit(Diagram diagram) {
		boolean result = true;
		if (diagram instanceof CommentElement) {
			result = commentUsernameMatches(diagram);
		}
		return result;
	}

	private static boolean commentUsernameMatches(Diagram diagram) {
		boolean result = false;
		if (diagram.getDiagramItem() instanceof CommentDTO) {
			CommentDTO c = (CommentDTO) diagram.getDiagramItem();
			String username = diagram.getSurfaceHandler().getEditorContext().getCurrentUser();
			if (username != null && username.equals(c.getUsername())) {
				result = true;
			}
		}
		return result;
	}
}