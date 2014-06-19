package net.sevenscales.editor.diagram.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.IDiagramItemRO;

import net.sevenscales.domain.CommentDTO;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.ElementType;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramSearch;
import net.sevenscales.editor.api.event.CommentThreadModifiedOutsideEvent;
import net.sevenscales.editor.uicomponents.uml.CommentElement;
import net.sevenscales.editor.uicomponents.uml.CommentThreadElement;
import net.sevenscales.editor.uicomponents.uml.ChildTextElement;
import net.sevenscales.editor.content.utils.DiagramItemFactory;
import net.sevenscales.editor.content.utils.AbstractDiagramFactory;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.diagram.shape.CommentShape;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.gfx.domain.IParentElement;


public class CommentFactory {
	public interface Factory {
		void addDiagram(Diagram diagram);
	}

	private Set<IDiagramItemRO> children = new HashSet<IDiagramItemRO>();
	private Map<String, IParentElement> commentThreadMapping = new HashMap<String, IParentElement>();
	private ISurfaceHandler surface;
	private boolean editable;

	public CommentFactory(ISurfaceHandler surface, boolean editable) {
		this.surface = surface;
		this.editable = editable;
	}

	public Set<IDiagramItemRO> getChildren() {
		return children;
	}

	public void add(IDiagramItemRO child) {
		children.add(child);
	}
	
	public void process(Diagram diagram) {
		if (diagram instanceof IParentElement) {
			commentThreadMapping.put(diagram.getDiagramItem().getClientId(), (IParentElement) diagram);
		}
	}

	public void lazyInit(Factory factory, int moveX, int moveY) {
		for (IDiagramItemRO item : children) {
			CommentElement comment = createComment(item);
			if (comment != null) {
				factory.addDiagram(comment);
			} else if (item.getParentId() != null && ElementType.CHILD_TEXT.getValue().equals(item.getType())) {
				factory.addDiagram(createChildTextItem(item, moveX, moveY));
			}
		}
	}

	public Diagram createChildTextItem(IDiagramItemRO item, int moveX, int moveY) {
		if (commentThreadMapping.containsKey(item.getParentId())) {
			IParentElement p = commentThreadMapping.get(item.getParentId());
			AbstractDiagramFactory factory = new AbstractDiagramFactory.ChildTextItemFactory();
			Info shape = factory.parseShape(item, moveX, moveY);
			return factory.parseDiagram(surface, shape, editable, item, p);
		}
		return null;
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

  public void resizeCommentThreads() {
  	for (IParentElement ct : commentThreadMapping.values()) {
  		if (ct instanceof CommentThreadElement) {
	  		((CommentThreadElement)ct).sort();
  		}
  	}
  }

	private Diagram createComment(IDiagramItemRO diro, DiagramSearch diagramSearch) {
		CommentElement result = null;
		if (diro.isComment()) {
			CommentDTO commentData = (CommentDTO) diro;
			Diagram parent = diagramSearch.findByClientId(commentData.getParentId());
			if (parent != null) {
				CommentThreadElement thread = (CommentThreadElement) parent;
				result = _createComment(commentData, thread);
				thread.sort();
			}
		}
		return result;
	}

	private CommentElement createComment(IDiagramItemRO item) {
		CommentElement result = null;
		if (item.isComment()) {
			CommentDTO citem = (CommentDTO) item;
			if (commentThreadMapping.containsKey(citem.getParentId())) {
				IParentElement p = commentThreadMapping.get(citem.getParentId());
				if (p instanceof CommentThreadElement) {
					CommentThreadElement thread = (CommentThreadElement)p;
					result = _createComment(citem, thread);
				}
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