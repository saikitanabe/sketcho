package net.sevenscales.editor.content.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Collection;
import java.util.Comparator;

import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.utils.Debug;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.DiagramSearchImpl;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.ot.BoardDocument;
import net.sevenscales.editor.api.ot.BoardDocumentHelpers;
import net.sevenscales.editor.content.BoardColorHelper;
import net.sevenscales.editor.content.ClientIdHelpers;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramSearch;
import net.sevenscales.editor.diagram.SelectionHandler;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.utils.CommentFactory;
import net.sevenscales.editor.diagram.utils.ReattachHelpers;
import net.sevenscales.editor.uicomponents.uml.Relationship2;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.diagram.drag.Anchor;

public class DuplicateHelpers {
	private static final SLogger logger = SLogger.createLogger(DuplicateHelpers.class);
  private static final int MIN_DISTANCE = 100;

	private ISurfaceHandler surface;
	private SelectionHandler selectionHandler;

	static {
		SLogger.addFilter(DuplicateHelpers.class);
	}

	public DuplicateHelpers(ISurfaceHandler surface, SelectionHandler selectionHandler) {
		this.surface = surface;
		this.selectionHandler = selectionHandler;
	}

	private static class State {
		public Map<String, String> clientIdMapping = new HashMap<String, String>();
		public Map<String, String> mappedGroupIds = new HashMap<String, String>();
		public CommentFactory commentFactory;
		public ReattachHelpers reattachHelpers = new ReattachHelpers();
		public Set<Relationship2> relationships = new HashSet<Relationship2>();
		public List<Diagram> newItems = new ArrayList<Diagram>();

		public State(ISurfaceHandler surface) {
 			commentFactory = new CommentFactory(surface, /*editable*/true);
		}

		public void addRelationshipIfAny(Diagram diagram) {
			if (diagram instanceof Relationship2) {
				relationships.add((Relationship2) diagram);
			}
		}

		public void add(Diagram copied) {
			newItems.add(copied);
			reattachHelpers.processDiagram(copied);
			addRelationshipIfAny(copied);

			// Apply theme colors
			BoardColorHelper.applyThemeToDiagram(copied, Theme.getColorScheme(Theme.ThemeName.PAPER), Theme.getCurrentColorScheme());
		}

	}
	
	public void duplicate(BoardDocument boardDocument) {
		// iterate all items and duplicate + generate client id; map original client id to new duplicated id
		// reconnect relationships based on mapped client IDs

		DiagramSearch search = surface.createDiagramSearch();
		List<Diagram> toduplicate = BoardDocumentHelpers.resolveAlsoParents(selectionHandler.getSelectedItems(), search);

		if (toduplicate.size() > 1) {
			int left = Integer.MAX_VALUE;
			int top = Integer.MAX_VALUE;
			for (Diagram d : toduplicate) {
				left = Math.min(left, d.getLeft());
				top = Math.min(top, d.getTop());
			}
			List<? extends IDiagramItemRO> items = BoardDocumentHelpers.diagramsToItems(toduplicate);
			// missing functional languages!
			// need to create new instances of diagram items, or it will be a mess, due to shared model
			List<IDiagramItem> copies = new ArrayList<IDiagramItem>();
			for (IDiagramItemRO diro : items) {
				copies.add(diro.copy());
			}
			paste(left + 20, top + 35, copies, boardDocument, true, true, false);
		} else if (toduplicate.size() == 1) {
			// special one item duplicate, e.g. sequence or horizontal bar
			Diagram duplicated = toduplicate.get(0).duplicate();
			surface.addAsSelected(duplicated, true, true);
		}
	}

	private void addItemsToTheBoard(
    State state,
    boolean asSelected,
    boolean autoResizeAndAlign
  ) {
		// check if something was really duplicated; item might not support duplication like comment element.
		if (state.newItems.size() > 0) {
			// replace old connections with new client ids
			replaceOldConnections(state.relationships, state.clientIdMapping);

      if (autoResizeAndAlign) {
        state.reattachHelpers.reattachRelationshipsAndDrawClosestPath();
      } else {
        state.reattachHelpers.reattachRelationshipsAndDraw();
      }

			if (asSelected) {
				surface.addAsSelected(state.newItems, true, true);
			} else {
				surface.add(state.newItems, true, true);
			}
		}
	}

  private native void beginOperationQueueTransaction()/*-{
    $wnd.beginOperationQueueTransaction()
  }-*/;

  private native void commitOperationQueueTransaction()/*-{
    $wnd.commitOperationQueueTransaction()
  }-*/;

	public Map<String, String> paste(
    int x,
    int y,
    List<? extends IDiagramItemRO> items,
    BoardDocument boardDocument,
    boolean editable,
    boolean asSelected,
    boolean autoResizeAndAlign
  ) {
    // TODO check if editable, should be checked already before copy!!
    // TODO what do to with comments!! is it allowed to copy those!!!???

    // start operation queue transaction
    // align makes insert and move
    // and we want those to goes in a single send.
    Map<String, String> result = null;

    try {
      beginOperationQueueTransaction();

      if (editable) {
        logger.debug("paste... {}", items);
        surface.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, true);

        State state = copyAndMapClientIds(x, y, items, boardDocument);

        if (autoResizeAndAlign) {
          // align shapes before reattaching relationships
          // so those utilize updated positions
          align(state);
          // state.reprocess();
        }

        addItemsToTheBoard(state, asSelected, autoResizeAndAlign);
        surface.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, false);

        result = state.clientIdMapping;
      }

      com.google.gwt.core.client.Scheduler.get()
          .scheduleDeferred(new com.google.gwt.core.client.Scheduler.ScheduledCommand() {
            public void execute() {
              // allow to send what ever has been done in here
              commitOperationQueueTransaction();
            }
          });
    } catch (Exception e) {
      commitOperationQueueTransaction();
    }

		return result;
	}

  private void align(State state) {
    for (Diagram d : state.newItems) {
      d.resizeEnd();
    }

    com.google.gwt.core.client.Scheduler.get().scheduleDeferred(new com.google.gwt.core.client.Scheduler.ScheduledCommand() {
      // need to do as scheduled, since otherwise elements are not resized
      public void execute() {
        surface.getSelectionHandler().unselectAll();
        sort(state);
      }
    });
  }

  private void sort(State state) {
    Set<Diagram> shapes = state.newItems.stream()
      .filter(i -> !(i instanceof Relationship2))
      .collect(Collectors.toSet());

    DiagramGrouper dg = new DiagramGrouper(shapes, state.relationships);
    List<Cluster> clusters = dg.groupDiagrams();

    // Sort and center align each connected group
    int yGapBetweenGroups = 100;
    int currentY = 0;

    for (Cluster group : clusters) {
      LayoutAlgorithm la = new LayoutAlgorithm(group, state.relationships, currentY, surface);
      la.layout();

      currentY += group.members.stream()
        .mapToInt(node -> node.item.getHeight())
        .max().orElse(0) + yGapBetweenGroups;
    }

    // LayoutForceDirected la = new LayoutForceDirected(clusters, state.relationships, shapes, surface);
    // la.layout();

    for (Diagram d : state.relationships) {
      if (d instanceof Relationship2) {
        Anchor anchor = ((Relationship2)d).getStartAnchor();
        if (anchor != null) {
          Diagram ad = anchor.getDiagram();
          ad.anchorWith(anchor, ad.getCenterX(), ad.getCenterY());
        }
        Anchor anchor2 = ((Relationship2)d).getEndAnchor();
        if (anchor2 != null) {
          Diagram ad = anchor2.getDiagram();
          ad.anchorWith(anchor2, ad.getCenterX(), ad.getCenterY());
        }
      }
    }

    for (Diagram d : shapes) {
      if (!(d instanceof Relationship2)) {
        moveAnchors(d.getAnchors(), 0, 0, 0);
      }
    }

    ReattachHelpers reattachHelpers = new ReattachHelpers();

    for (Diagram d : state.newItems) {
      reattachHelpers.processDiagram(d);
    }

    reattachHelpers.reattachRelationships(false, true, true);

    surface.getEditorContext().getEventBus().fireEvent(new net.sevenscales.editor.api.event.PotentialOnChangedEvent(state.newItems));
  }

  private void moveAnchors(Collection<AnchorElement> anchors, int dx, int dy, int sequence) {
		for (AnchorElement ae : anchors) {
			ae.dispatch(dx, dy, sequence);
		}
	}

	private State copyAndMapClientIds(int x, int y, List<? extends IDiagramItemRO> items, BoardDocument boardDocument) {
		final State state = new State(surface);
		int i = 0;

		int left = Integer.MAX_VALUE;
		int top = Integer.MAX_VALUE;

		for (IDiagramItemRO di : items) {
			if (DuplicateHelpers.allowedToPasteType(di)) {
				Info shape = ShapeParser.parse(di, 0, 0);
				// Info shape = di.parseShape();
				int l = shape.getLeft();
				int t = shape.getTop();
				int r = l + shape.getWidth();

				left = l < left ? l : left;
				top = t < top ? t : top;
			}
		}

		int moveX = x - left;
		int moveY = y - top;

		for (IDiagramItemRO di : items) {
			// need to modify same di, first set group, not to use old group id
			mapGroup(di, i, state, boardDocument);
  		copyAndMap(moveX, moveY, di, ++i, state, boardDocument);
		}

		mapChildToNewParent(state);

    state.commentFactory.lazyInit(new CommentFactory.Factory() {
    	public void addDiagram(Diagram diagram, IDiagramItemRO item) {
    		state.add(diagram);
    	}
    }, moveX, moveY);

		return state;
	}

	private void mapChildToNewParent(State state) {
		for (IDiagramItemRO child : state.commentFactory.getChildren()) {
			if (child.getParentId() != null && !"".equals(child.getParentId())) {
				String newParentId = state.clientIdMapping.get(child.getParentId());
				if (newParentId != null && !"".equals(newParentId) && child instanceof IDiagramItem) {
					((IDiagramItem) child).setParentId(newParentId);
				}
			}
		}		
	}

	private void copyAndMap(int x, int y, IDiagramItemRO diro, int i, State state, BoardDocument boardDocument) {
		if (allowedToPasteType(diro) && diro instanceof IDiagramItem) {
			IDiagramItem di = (IDiagramItem) diro;
			String newClientId = ClientIdHelpers.generateClientId(i, boardDocument, surface.getEditorContext());

			state.clientIdMapping.put(di.getClientId(), newClientId);
			di.setClientId(newClientId);

			// always editable at this point
			if (diro.getParentId() != null) {
				state.commentFactory.add(di);
			} else {
				Diagram copied = DiagramItemFactory.create(x, y, di, surface, true, /*parent*/ null);
				if (copied != null) {
					state.commentFactory.process(copied);
					state.add(copied);
				}
			}
		}
	}

	private void mapGroup(IDiagramItemRO diro, int i, State state, BoardDocument boardDocument) {
		if (diro.isGroup() && diro instanceof IDiagramItem) {
			String mappedGroupId = state.mappedGroupIds.get(diro.getGroup());
			if (mappedGroupId == null) {
				// generate a new group identifier
				mappedGroupId = ClientIdHelpers.generateClientId(i, boardDocument, surface.getEditorContext());
				state.mappedGroupIds.put(diro.getGroup(), mappedGroupId);
			}

			((IDiagramItem) diro).setGroup(mappedGroupId);
		}
	}

	public static boolean allowedToPasteType(IDiagramItemRO diro) {
		return !ElementType.COMMENT.getValue().equals(diro.getType())	&&
					 !ElementType.COMMENT_THREAD.getValue().equals(diro.getType());
	}

	private void replaceOldConnections(Set<Relationship2> relationships, Map<String, String> clientIdMapping) {
		for (Relationship2 r : relationships) {
			String cd = r.getDiagramItem().getCustomData();
			if (cd.indexOf(":") > 0) {
				String[] connections = cd.split(":");
				String start = getMapping(connections[0], clientIdMapping);
				String end = getMapping(connections[1], clientIdMapping);
				r.getDiagramItem().setCustomData(start + ":" + end);
				r.parseCustomData(r.getDiagramItem().getCustomData());
			}
		}
	}

	private String getMapping(String connection, Map<String, String> clientIdMapping) {
		return clientIdMapping.get(connection) != null ? clientIdMapping.get(connection) : "";
	}
}
