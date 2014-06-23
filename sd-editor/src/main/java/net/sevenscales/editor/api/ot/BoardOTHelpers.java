package net.sevenscales.editor.api.ot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.CommentDTO;
import net.sevenscales.domain.utils.Debug;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.DiagramItemField;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.Theme.ThemeName;
import net.sevenscales.editor.api.ot.ApplyHelpers.DiagramApplyOperation;
import net.sevenscales.editor.content.utils.DiagramItemFactory;
import net.sevenscales.editor.diagram.utils.CommentFactory;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramSearch;
import net.sevenscales.editor.diagram.utils.ReattachHelpers;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.editor.uicomponents.uml.CommentElement;
import net.sevenscales.editor.gfx.domain.IParentElement;
import net.sevenscales.editor.gfx.domain.IChildElement;
import net.sevenscales.editor.gfx.domain.IRelationship;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;

public class BoardOTHelpers {
	public static final String HIGHLIGHT_COLOR = "00E000";
	private ISurfaceHandler surface;
	private DiagramSearch diagramSearch;
	private CommentFactory commentFactory;
	private String clientIdentifier;
	private static final SLogger logger = SLogger.createLogger(BoardOTHelpers.class);

	public BoardOTHelpers(ISurfaceHandler surface, String clientIdentifier) {
		this.surface = surface;
		this.clientIdentifier = clientIdentifier;
		this.diagramSearch = surface.createDiagramSearch();
		this.commentFactory = new CommentFactory(surface, true);
	}
	
	public void applyOperationsToGraphicalView(String originator, List<DiagramApplyOperation> operations) throws MappingNotFoundException {
		for (DiagramApplyOperation ap : operations) {
			applyOperationToGraphicalView(originator, ap.getOperation(), ap.getItems());
		}
	}
	
	private void applyOperationToGraphicalView(String originator, OTOperation operation, List<IDiagramItemRO> items) throws MappingNotFoundException {
		// logger.debug2("applyOperationToGraphicalView: originator({}), clientIdentifier({})", originator, clientIdentifier);
		surface.getEditorContext().set(EditorProperty.ON_CHANGE_ENABLED, false);
		surface.getEditorContext().set(EditorProperty.ON_OT_OPERATION, true);
		
		switch (operation) {
		case MODIFY:
			safeModifyOT(originator, items);
			break;
		case INSERT:
			safeInsertOT(originator, items);
			break;
		case DELETE:
			safeDeleteOT(originator, items);
			break;
		case UNDO_DELETE:
		case REDO_DELETE:
			boolean immidiate = true;
			deleteOT(originator, items, immidiate);
			break;
		case UNDO_INSERT:
		case REDO_INSERT:
			insertOT(originator, items);
			break;
		case UNDO_MODIFY:
		case REDO_MODIFY:
			modifyOT(originator, items);
			break;
		}
		
		surface.getEditorContext().set(EditorProperty.ON_CHANGE_ENABLED, true);
		surface.getEditorContext().set(EditorProperty.ON_OT_OPERATION, false);
	}

	private void safeDeleteOT(String originator, List<IDiagramItemRO> items) throws MappingNotFoundException {
		if (originator.equals(clientIdentifier)) {
			// do not start delete operation on originator
			return;
		}
		boolean immidiate = false;
		deleteOT(originator, items, immidiate);
	}
	
	private class DeleteOTRepeating implements RepeatingCommand {
		private Set<Diagram> diagrams;
		
		DeleteOTRepeating(Set<Diagram> diagrams) {
			this.diagrams = diagrams;
		}
		
		@Override
		public boolean execute() {
			deleteDiagrams(diagrams);
			return false;
		}
	}
	
	private void deleteDiagrams(Set<Diagram> diagrams) {
		for (Diagram d : diagrams) {
			d.removeFromParent();
		}
	}
	
	private void deleteOT(String originator, List<IDiagramItemRO> items, boolean immidiate) throws MappingNotFoundException {
		Set<Diagram> diagrams = new HashSet<Diagram>();
		for (IDiagramItemRO diro : items) {
			Diagram diagram = findDiagramById(diro);
			if (diagram != null) {
				// in originator client diagram is null 
				diagrams.add(diagram);
			}
		}
		
		// 0 on originator; otherwise should match
		boolean validation = (diagrams.size() == items.size() || diagrams.size() == 0);
		if (!validation) {
		  throw new MappingNotFoundException(SLogger.format("deleteOT failed for {}", items.toString()));
		}
		
		// first highlight that something is gonna happen
		if (!immidiate) {
			highlightChanges(originator, diagrams, new DeleteOTRepeating(diagrams));
		} else {
			// remove immediately
			deleteDiagrams(diagrams);
		}
	}

	private void safeInsertOT(String originator, List<IDiagramItemRO> items) throws MappingNotFoundException {
		if (originator.equals(clientIdentifier)) {
			// Update server id by client id to graphical view to have correct graphical view checksum
			// in case inserted by this client.
			// updateServerIds(items);
			updateCommentsTimeStamps(items);
			return;
		}
		insertOT(originator, items);
	}

	private void updateCommentsTimeStamps(List<IDiagramItemRO> items) {
		for (IDiagramItemRO diro : items) {
			if (CommentElement.TYPE.equals(diro.getType())) {
				CommentDTO sourceComment = (CommentDTO) diro;
				Diagram d = findDiagramByClientId(diro.getClientId());
				if (d instanceof CommentElement) {
					CommentElement ce = (CommentElement) d;
					CommentDTO cdto = (CommentDTO) d.getDiagramItem();
					cdto.setCreatedAt(sourceComment.getCreatedAt());
					cdto.setUpdatedAt(sourceComment.getUpdatedAt());
					ce.setTitle();
				}
			}
		}
	}

	private void updateServerIds(List<IDiagramItemRO> items) {
		for (IDiagramItemRO diro : items) {
			Diagram d = findDiagramByClientId(diro.getClientId());
			// is Long slow?... int to Long could lead into problems
			if (d != null) {
			  // diagram might have been removed and e.g. insert is coming too late
			  d.getDiagramItem().setId(diro.getId());
			}
		}		
	}

	private void insertOT(String originator, List<IDiagramItemRO> items) throws MappingNotFoundException {
		Set<Diagram> diagrams = new HashSet<Diagram>();
    ReattachHelpers reattachHelpers = new ReattachHelpers(surface.createDiagramSearch(), true);

		for (IDiagramItemRO diro: items) {
			// TODO diagram should never be found!?!?
			Diagram diagram = findDiagramByClientId(diro.getClientId());
			if (diagram == null) {
				diagram = createAndAddElement(diro);
			} else {
				// insert could be reopen comment thread and element might be
				// just hidden
				diagram.copyFrom(diro);
			}
			applyThemeColors(diagram);
			reattachHelpers.processDiagram(diagram);
      diagrams.add(diagram);
		}
		
		reattachHelpers.reattachRelationshipsAndDraw();
		
		// 0 on originator
		boolean validation = (diagrams.size() == items.size() || diagrams.size() == 0);
		if (!validation) {
		  throw new MappingNotFoundException(SLogger.format("insertOT failed for\ndiagrams {}\nitems {}", diagrams.toString(), items.toString()));
		}
		
		highlightChanges(originator, diagrams, new OTHighlight(diagrams));
	}

	private Diagram createAndAddElement(IDiagramItemRO diro) {
		Diagram result = null;
  	if (CommentElement.TYPE.equals(diro.getType())) {
  		// comments are created through comment factory
  		// doesn't need to create lazily since comment thread is already
  		// on board or it is not possible to create comment
    	result = commentFactory.createCommentInOT(diro, diagramSearch);
	  } else if (diro.getParentId() != null) {
	  	Diagram parent = diagramSearch.findByClientId(diro.getParentId());
	  	if (parent != null && parent instanceof IParentElement) {
	  		IParentElement p = (IParentElement) parent;
		    result = DiagramItemFactory.create(diro, surface, true, p);
	  	}
	  } else {
	    result = DiagramItemFactory.create(diro, surface, true, /*parent*/ null);
	  }
    surface.add(result, true, false);
    return result;
	}

	private void safeModifyOT(String originator, List<IDiagramItemRO> items) throws MappingNotFoundException {
		if (originator.equals(clientIdentifier)) {
			return;
		}
		modifyOT(originator, items);
	}
	private void modifyOT(String originator, List<IDiagramItemRO> items) throws MappingNotFoundException {
		logger.debug2("modifyOT items.length() {}", items.size());
		Set<Diagram> diagrams = new HashSet<Diagram>();
    ReattachHelpers reattachHelpers = new ReattachHelpers(diagramSearch, true);

    Map<String,Boolean> dirtyFields = new HashMap<String,Boolean>();
		for (IDiagramItemRO diro : items) {
			logger.debug2("modifyOT {}", diro);
			Diagram diagram = findDiagramById(diro);
			if (diagram != null) {
				diro.compare(diagram.getDiagramItem(), dirtyFields);
				diagram.copyFrom(diro);
				reattachHelpers.processDiagram(diagram);
				
				applyThemeColors(diagram);

				diagrams.add(diagram);
			}
		}
		
		
		if (dirtyFields.get(DiagramItemField.DISPLAY_ORDER.getValue()) != null) {
			surface.applyDisplayOrders(items);
		}

		reattachHelpers.reattachRelationshipsAndDraw();

		if (diagrams.size() != items.size()) {
		  throw new MappingNotFoundException(SLogger.format("modifyOT failed for {}", items.toString()));
		}
  	highlightChanges(originator, diagrams, new OTHighlight(diagrams));
	}

	private void applyThemeColors(Diagram diagram) {
    if (diagram.usesSchemeDefaultTextColor(Theme.getColorScheme(ThemeName.PAPER))) {
      // stored with paper colors
      // apply theme colors
      diagram.setTextColor(diagram.getDefaultTextColor(Theme.getCurrentColorScheme()));
    }
    if (diagram.usesSchemeDefaultBorderColor(Theme.getColorScheme(ThemeName.PAPER))) {
      diagram.setBorderColor(diagram.getDefaultBorderColor(Theme.getCurrentColorScheme()));
    }
    if (diagram.usesSchemeDefaultBackgroundColor(Theme.getColorScheme(ThemeName.PAPER))) {
      diagram.setBackgroundColor(diagram.getDefaultBackgroundColor(Theme.getCurrentColorScheme()));
    }
    // else if (diagram.isTextColorAccordingToBackgroundColor()) {
    //   diagram.setTextColor(diagram.getDefaultTextColor(Theme.getCurrentColorScheme()));
    // }
  }

  private static class OTHighlight implements RepeatingCommand {
		private Set<Diagram> diagrams;
		public OTHighlight(Set<Diagram> diagrams) {
			this.diagrams = diagrams;
		}
		@Override
		public boolean execute() {
			for (Diagram d : diagrams) {
				d.restoreHighlighColor();
			}
			return false;
		}
	}
	
	private void highlightChanges(String originator, Set<Diagram> diagrams, RepeatingCommand repeating) {
  	if (!originator.equals(clientIdentifier)) {
  		forceHighlightChanges(originator, diagrams, repeating);
  	}
	}
	
	private void forceHighlightChanges(String originator, Set<Diagram> diagrams, RepeatingCommand repeating) {
		for (Diagram d : diagrams) {
			d.setHighlightColor(HIGHLIGHT_COLOR);
		}
		Scheduler.get().scheduleFixedDelay(repeating, 300);
	}

	private Diagram findDiagramById(IDiagramItemRO di) {
		for (Diagram d : surface.getDiagrams()) {
			if (!(d instanceof CircleElement)) {
				IDiagramItemRO dto = d.getDiagramItem();
				checkItem(di);
				checkItem(dto);
				if (dto.getClientId().equals(di.getClientId())) {
					return d;
				}
			}
		}
		return null;
	}

	public void checkItem(IDiagramItemRO di) {
		if (di.getClientId() == null) {
			Debug.log("ERROR: client id has not been set!");
			throw new RuntimeException("Client id has not been set for di type: " + di.getType() + " type: " + di.getText());
		}
	}

	private Diagram findDiagramByClientId(String clientId) {
		for (Diagram d : surface.getDiagrams()) {
			IDiagramItemRO dto = d.getDiagramItem();
			// circle items doesn't have dto
			if (dto != null && dto.getClientId() != null && dto.getClientId().equals(clientId)) {
				return d;
			}
		}
		return null;
	}

}
