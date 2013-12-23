package net.sevenscales.editor.diagram.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.IDiagramItemRO;

import net.sevenscales.domain.CommentDTO;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramSearch;
import net.sevenscales.editor.api.event.CommentThreadModifiedOutsideEvent;
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

	public Diagram createCommentInOT(IDiagramItemRO diro, DiagramSearch diagramSearch) {
		Diagram result = createComment(diro, diagramSearch);

		// notify that comment has been created outside this editor
  	CommentElement ce = (CommentElement) result;
  	CommentThreadElement cte = (CommentThreadElement) ce.getParentThread();
  	if (cte != null) {
  		surface.getEditorContext().getEventBus().fireEvent(new CommentThreadModifiedOutsideEvent(cte));
  	}
  	return result;
  }

	private Diagram createComment(IDiagramItemRO diro, DiagramSearch diagramSearch) {
		CommentElement result = null;
		if (diro.isComment()) {
			CommentDTO commentData = (CommentDTO) diro;
			Diagram parent = diagramSearch.findByClientId(commentData.getParentThreadId());
			if (parent != null) {
				CommentThreadElement thread = (CommentThreadElement) parent;
				result = _createComment(commentData, thread);
			}
		}
		return result;
	}

	private CommentElement createComment(IDiagramItemRO item) {
		CommentElement result = null;
		if (item.isComment()) {
			CommentDTO citem = (CommentDTO) item;
			if (commentThreadMapping.containsKey(citem.getParentThreadId())) {
				CommentThreadElement thread = commentThreadMapping.get(citem.getParentThreadId());
				result = _createComment(citem, thread);
			}
		}
		return result;
	}

	private CommentElement _createComment(CommentDTO item, CommentThreadElement thread) {
		String[] s = item.getShape().split(",");
    int x = DiagramItemFactory.parseInt(s[0]);
    int y = DiagramItemFactory.parseInt(s[1]);
    int width = DiagramItemFactory.parseInt(s[2]);
    int height = DiagramItemFactory.parseInt(s[3]);

    CommentElement result = new CommentElement(surface,
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
        item,
        new DiagramItemDTO());
    return (CommentElement) DiagramItemFactory.applyDiagramItem(result, item);
	}
}