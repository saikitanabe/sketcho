package net.sevenscales.editor.diagram.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.IDiagramItemRO;
import  net.sevenscales.domain.JsComment;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.uicomponents.uml.CommentElement;
import net.sevenscales.editor.uicomponents.uml.CommentThreadElement;
import net.sevenscales.editor.content.utils.DiagramItemFactory;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.diagram.shape.CommentShape;


public class CommentFactory {
	public interface Factory {
		void addDiagram(Diagram diagram);
	}

	private Set<IDiagramItemRO> comments = new HashSet<IDiagramItemRO>();
	private Map<String, CommentThreadElement> commentThreadMapping = new HashMap<String, CommentThreadElement>();
	private ISurfaceHandler surface;
	private boolean editable;

	public CommentFactory(ISurfaceHandler surface, boolean editable) {
		this.surface = surface;
		this.editable = editable;
	}

	public void add(IDiagramItemRO comment) {
		comments.add(comment);
	}
	
	public void process(Diagram diagram) {
		if (diagram instanceof CommentThreadElement) {
			commentThreadMapping.put(diagram.getDiagramItem().getClientId(), (CommentThreadElement) diagram);
		}
	}

	public void lazyInit(Factory factory) {
		for (IDiagramItemRO item : comments) {
			CommentElement comment = createComment(item);
			if (comment != null) {
				factory.addDiagram(comment);
			}
		}
	}

	private CommentElement createComment(IDiagramItemRO item) {
		CommentElement result = null;
		JsComment jsComment = CommentElement.parseCommentJson(item.getCustomData());
		if (commentThreadMapping.containsKey(jsComment.getParentThread())) {
			CommentThreadElement thread = commentThreadMapping.get(jsComment.getParentThread());
			result = _createComment(item, jsComment, thread);
		}
		return result;
	}

	private CommentElement _createComment(IDiagramItemRO item, JsComment jsComment, CommentThreadElement thread) {
		String[] s = item.getShape().split(",");
    int x = DiagramItemFactory.parseInt(s[0]);
    int y = DiagramItemFactory.parseInt(s[1]);
    int width = DiagramItemFactory.parseInt(s[2]);
    int height = DiagramItemFactory.parseInt(s[3]);

    return new CommentElement(surface,
        new CommentShape(x, 
            y,
            width,
            height),
            item.getText(),
            DiagramItemFactory.parseBackgroundColor(item),
            DiagramItemFactory.parseBorderColor(item),
            DiagramItemFactory.parseTextColor(item),
        editable,
        thread, 
        jsComment);
	}
}