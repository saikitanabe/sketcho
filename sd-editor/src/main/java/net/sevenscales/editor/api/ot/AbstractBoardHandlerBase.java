package net.sevenscales.editor.api.ot;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Window;
import com.google.gwt.json.client.JSONArray;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.event.BoardRemoveDiagramsEvent;
import net.sevenscales.editor.api.event.BoardRemoveDiagramsEventHandler;
import net.sevenscales.editor.api.event.DiagramElementAddedEvent;
import net.sevenscales.editor.api.event.DiagramElementAddedEventHandler;
import net.sevenscales.editor.api.event.PotentialOnChangedEvent;
import net.sevenscales.editor.api.event.PotentialOnChangedEventHandler;
import net.sevenscales.editor.api.event.RedoEvent;
import net.sevenscales.editor.api.event.RedoEventHandler;
import net.sevenscales.editor.api.event.UndoEvent;
import net.sevenscales.editor.api.event.UndoEventHandler;
import net.sevenscales.editor.api.ot.ApplyHelpers.ApplyOperation;
import net.sevenscales.editor.api.ot.ApplyHelpers.DiagramApplyOperation;
import net.sevenscales.editor.api.ot.OperationQueue.Acknowledged;
import net.sevenscales.editor.content.ClientIdHelpers;
import net.sevenscales.editor.content.Context;
import net.sevenscales.editor.content.UiSketchoBoardEditContent;
import net.sevenscales.editor.content.utils.DiagramItemFactory;
import net.sevenscales.editor.content.utils.JsonHelpers;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.editor.uicomponents.helpers.ConnectionHelpers;
import net.sevenscales.editor.uicomponents.helpers.IGlobalElement;
import net.sevenscales.editor.uicomponents.helpers.LifeLineEditorHelper;
import net.sevenscales.editor.uicomponents.helpers.RelationshipHandleHelpers;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;
import net.sevenscales.editor.utils.GoogleAnalyticsHelper;
import net.sevenscales.editor.gfx.domain.Promise;

public abstract class AbstractBoardHandlerBase implements Acknowledged, OperationTransaction {
	private static final SLogger logger = SLogger.createLogger(AbstractBoardHandlerBase.class);
	
	private String boardName;
	private UiSketchoBoardEditContent editorContent;
	private Context context;
	private EditorContext editorContext;
	private String clientIdentifier = "no-clientIdentifier";
	private BoardOTHelpers boardHelpers;
	private OTBuffer otBuffer;
	private JsonHelpers jsonHelpers;
	private OTCompensationTransformer compensationTransformer;
	// used to get previous item state for undo operation json
	private BoardDocument graphicalDocumentCache;
	private static final List<ApplyOperation> EMPTY_LIST;
	private static final List<IDiagramItemRO> EMPTY_ITEM_LIST;
	private boolean transaction;
	private List<CompensationModel> transactionModels;
	// private IGoogleAnalyticsHelper analytics;
	
	static {
		EMPTY_LIST = new ArrayList<ApplyOperation>();
		EMPTY_ITEM_LIST = new ArrayList<IDiagramItemRO>();

    SLogger.addFilter(AbstractBoardHandlerBase.class);
	}
	
	public AbstractBoardHandlerBase(String boardName, Context context, EditorContext editorContext) {
		this.boardName = boardName;
		this.context = context;
		this.editorContext = editorContext;
		
		otBuffer = new OTBuffer();
		compensationTransformer = new OTCompensationTransformer();
		transactionModels = new ArrayList<CompensationModel>();
	}
	
	private UndoEventHandler undoEventHandler = new UndoEventHandler() {
		public void on(UndoEvent event) {
			applyLocalSendOperation(OTOperation.UNDO.getValue(), EMPTY_ITEM_LIST);
		}
	};
	
	private RedoEventHandler redoEventHandler = new RedoEventHandler() {
		public void on(RedoEvent event) {
			applyLocalSendOperation(OTOperation.REDO.getValue(), EMPTY_ITEM_LIST);
		}
	};
	
	private PotentialOnChangedEventHandler operationHandler = new PotentialOnChangedEventHandler() {
		@Override
		public void on(PotentialOnChangedEvent event) {
//			Diagram diagram = event.getDiagrams()[0];
//			System.out.println("PotentialOnChangedEvent number of elements: " + event.getDiagrams().length + " " + event.getDiagrams());
			removeDeletedDiagrams(event.getDiagrams());

			if (event.getDiagrams().size() > 0) {
				// check that diagrams are not null
				sendOperation(boardName, clientIdentifier, "modify", event.getDiagrams());
			}
		}
	};

	/**
	* Some shapes might have been removed on realtime and this is the last
	* block before going to make any changes on client model.
	* To prevent crash and client reload.
	*/
	private void removeDeletedDiagrams(List<Diagram> diagrams) {
		for (int i = diagrams.size() - 1; i >= 0; --i) {
			Diagram d = diagrams.get(i);

			if (d.isRemoved()) {
				// remove from end so doesn't affect next items
				diagrams.remove(i);
			}
		}
	}
	
	private DiagramElementAddedEventHandler addDiagramElementHandler = new DiagramElementAddedEventHandler() {
		@Override
		public void onAdded(DiagramElementAddedEvent event) {
			if (editorContent.getEditorContext().isTrue(EditorProperty.ON_CHANGE_ENABLED)) {
				int i = 0;
				
				boolean addedAnyRealDiagrams = false;
				for (Diagram diagram : event.getDiagrams()) {
					if (!(diagram instanceof CircleElement)) {
						DiagramItemDTO di = (DiagramItemDTO) DiagramItemFactory.createOrUpdate(diagram);
						// generate client id if it is missing; 
						// case duplicate has already client id to have correct references
						// case redo has already inserted client id, and now we are using the same id to calculate prev state correctly
						ClientIdHelpers.generateClientIdIfNotSet(di, ++i, graphicalDocumentCache, editorContext);
						addedAnyRealDiagrams = true;
					}
				}
				
				if (addedAnyRealDiagrams) {
					trackInsertEvent(event.getDiagrams());
					sendOperation(boardName, clientIdentifier, OTOperation.INSERT.getValue(), event.getDiagrams());
				}
				
//				// now generate json with connections
//				for (Diagram diagram : event.getDiagrams()) {
//					if (!(diagram instanceof CircleElement)) {
//						DiagramItemDTO di = (DiagramItemDTO) DiagramItemFactory.createOrUpdate(diagram, false);
//						getBoardHelpers().checkItem(di);
//						String json = DiagramItemJS.asJson(di).toString();
//	 					jsons += json + ",";
//					}
//				}
//
//				if (jsons.length() > 0) {
//					// remove last comma
//					jsons = jsons.substring(0, jsons.length() - 1);
//					sendOperation(boardName, clientIdentifier, "insert", "[" + jsons + "]");
//				}
			}
		}
	};

	private void trackInsertEvent(Iterable<Diagram> diagrams) {
		// >>>> ST 8.10.2015 element types are no longer checked
		// it is enough to know if shapes are inserted
		// String elements = mkString(diagrams);
		// <<<< ST 8.10.2015

		GoogleAnalyticsHelper.trackEvent("Board", "INSERT", "");
	}

	private String mkString(Iterable<Diagram> diagrams) {
		String result = "";
		boolean first = true;
		for (Diagram d : diagrams) {
			if (!(d instanceof CircleElement)) {
				if (first) {
					first = false;
				} else {
					result += ",";
				}
				result += d.getDiagramItem().getType();
			}
		}
		return result;
	}

	private BoardRemoveDiagramsEventHandler removeHandler = new BoardRemoveDiagramsEventHandler() {
		@Override
		public void on(BoardRemoveDiagramsEvent event) {
			sendOperation(boardName, clientIdentifier, "delete", event.getRemoved());
		}
	};
	
	public BoardDocument getGraphicalViewDocument() {
		return graphicalDocumentCache;
	}
	
	protected void initHandlers() {
		editorContext.getEventBus().addHandler(PotentialOnChangedEvent.TYPE, operationHandler);
		editorContext.getEventBus().addHandler(DiagramElementAddedEvent.TYPE, addDiagramElementHandler);
		editorContext.getEventBus().addHandler(BoardRemoveDiagramsEvent.TYPE, removeHandler);
  	editorContext.getEventBus().addHandler(UndoEvent.TYPE, undoEventHandler); 
  	editorContext.getEventBus().addHandler(RedoEvent.TYPE, redoEventHandler); 
	}
	protected void initHelpers() {
		if (boardHelpers == null) {
			boardHelpers = new BoardOTHelpers(getEditorContent().getSurface(), clientIdentifier);
		}
		
		if (jsonHelpers == null) {
			jsonHelpers = new JsonHelpers(getEditorContent().getSurface());
		}
	}
	protected UiSketchoBoardEditContent getEditorContent() {
		if (editorContent == null) {
			throw new RuntimeException("editorContent not set");
		}

		return editorContent;
	}
	protected boolean isEditorContentSet() {
		return editorContent != null;
	}
	protected void setEditorContent(UiSketchoBoardEditContent editorContent) {
		this.editorContent = editorContent;
		IDiagramContent diagramContent = null;
		if (editorContent.getContent() instanceof IDiagramContent) {
			diagramContent = (IDiagramContent) editorContent.getContent();
		}

		if (graphicalDocumentCache == null) {
				graphicalDocumentCache = new BoardDocument(diagramContent.getDiagramItems(), getBoardName() + "-graphic", "Graphical Document Cache");
				editorContext.setGraphicalDocumentCache(graphicalDocumentCache);
		} else {
			graphicalDocumentCache.reset(diagramContent.getDiagramItems());
		}
	}
	protected String getClientIdentifier() {
		return clientIdentifier;
	}
	protected void setClientIdentifier(String clientIdentifier) {
		this.clientIdentifier = clientIdentifier;
	}
	protected String getBoardName() {
		return boardName;
	}
	protected Context getContext() {
		return context;
	}
	protected EditorContext getEditorContext() {
		return editorContext;
	}
	protected BoardOTHelpers getBoardHelpers() {
		if (boardHelpers == null) {
			throw new RuntimeException("boardHelpers not set");
		}
		return boardHelpers;
	}
	protected JsonHelpers getJsonHelpers() {
		if (jsonHelpers == null) {
			throw new RuntimeException("jsonHelpers not set");
		}
		return jsonHelpers;
	}
	
//	protected final void sendOperation(String name, String originator, String operation, String jsonContent) {
//		applyLocalSendOperationAndUpdateGraphicalView(operation, jsonContent);
//		if (notUndoOrRedo(operation)) {
//			sendLocalOperation(name, originator, operation, jsonContent);
//		}
//	}

	protected final void sendOperation(String boardName, String originator, String operation, Iterable<Diagram> diagrams)  {

    // ST 23.02.2022: defer sending for added diagrams to have a correct size for elements
    // This is due to asynchronous size because of React foreign object that is
    // rendered asynchronosely. Proper implementation is to run getTextSize
    // for each diagram and send only after all Promises have returned.

    int size = 0;
    if (diagrams instanceof List) {
      size = ((List)diagrams).size();
    } else if (diagrams instanceof Set) {
      size = ((Set) diagrams).size();
    } else {
      // finally calculate the size if not available
      for (Diagram d : diagrams) {
        ++size;
      }
    }

    Promise[] promises = new Promise[size];
    int index = 0;
    for (Diagram diagram : diagrams) {
      promises[index++] = diagram.getTextSize();
    }

    Promise.all(promises).then(p -> {
      List<? extends IDiagramItemRO> operationItems = BoardDocumentHelpers.diagramsToItems(diagrams);

      // packJson to get only modified fields of each object, client id is preserved.
      JSONArray diff = null;
      if (operation.equals(OTOperation.MODIFY.getValue())) {
        diff = JSONPack.diff(operationItems, graphicalDocumentCache.getDocument());
      }

      if (diff != null && diff.size() == 0) {
        logger.debug("modify operation no diff => don't change anything");
        return;
      }

      applyLocalSendOperation(operation, operationItems);
      if (notUndoOrRedo(operation)) {
        // undo and redo are always translated to change operation (insert, move, delete...)
        // client is responsible to do undo/redo operations, for server those are just normal
        // change operations
        sendLocalOperation(boardName, originator, operation, operationItems, diff);
      }
    });
	}
	protected abstract void sendLocalOperation(String name, String originator, String operation, List<? extends IDiagramItemRO> operationItems, JSONArray diff);

//	protected abstract void sendLocalOperation(String boardName, String originator, String operation, List<Diagram> diagrams);
	
	private boolean notUndoOrRedo(String operation) {
		boolean undoOrRedo = OTOperation.UNDO.getValue().equals(operation) || OTOperation.REDO.getValue().equals(operation);
		return !undoOrRedo;
	}
	
	/**
	 * Generic apply or calculation for local send operation, what is generic to support local undo/redo.
	 * @param operation
	 * @param operationJson
	 * @param prevState 
	 */
	private final void applyLocalSendOperation(String operation, List<? extends IDiagramItemRO> operationItems) {
		// logger.debug("APPLYOPERATION operation {}, operationJson {}", operation, jsonConversion.getJson());
		OTOperation op = OTOperation.getEnum(operation);
		switch (op) {
		case MODIFY:
		case INSERT:
		case DELETE:
			compensateLocalChangeOperation(op, operationItems);
			break;
		case UNDO:
			applyLocalUndo();
			break;
		case REDO:
			applyLocalRedo();
			break;
		}
	}
	
	protected void compensateLocalChangeOperation(OTOperation op, List<? extends IDiagramItemRO> operationItems) {
		// runtime action by the user, resolve what compensates this operation and remember that
//		List<IDiagramItemRO> changes = BoardDocumentHelpers.fromJson(operationJson);
		CompensationModel model = null;
    try {
      model = compensationTransformer.compensate(op, graphicalDocumentCache.getDocument(), operationItems);
      // push operation to OTBuffer
      if (model != null && transaction) {
      	// handle multiple models as one single transaction => one undo/redo
      	transactionModels.add(model);
      } else if (model != null) {
	      otBuffer.pushToUndoBufferAndResetRedo(model);
      }
      // apply changes to graphical view document after compensation calculation, to have correct
      // previous state.
      graphicalDocumentCache.apply(op, operationItems);
    } catch (MappingNotFoundException e) {
      // something is seriously wrong if this happens during local operations; what should be done...
      // save the board (on confluence and reload...) now just ignored :)
      net.sevenscales.domain.utils.Error.reload("compensateLocalChangeOperation", e);
    }
	}

	@Override
	public void beginTransaction() {
		transactionModels.clear();
		transaction = true;
	}

	@Override
	public void commitTransaction() {
		// create new not to handle same array
		List<CompensationModel> topush = new ArrayList<CompensationModel>();
		for (CompensationModel cm : transactionModels) {
			topush.add(cm);
		}
		otBuffer.pushToUndoBufferAndResetRedo(topush);
		transactionModels.clear();
		transaction = false;
	}
	
	protected void clearOTBuffer() {
		otBuffer.clear();
	}

	private void applyLocalUndo() {
		// actions from the buffer to get to prev or next stage
		List<CompensationModel> undo = otBuffer.undoBuffer();
		logger.debug("applyLocalUndo {}", undo);
		if (undo != null) {
			for (CompensationModel cm : undo) {
				OTOperation undoop = OTOperation.getEnum(OTOperation.UNDO.getValue() + "." + cm.undoOperation);
				applyUndoOrRedoToGraphicalView(undoop, cm.undoJson);
				graphicalDocumentCache.apply(undoop, cm.undoJson);
				extendApplyLocalUndo(undoop, cm.undoOperation, cm.undoJson);
			}
		}
	}
	protected abstract void extendApplyLocalUndo(OTOperation undoop, OTOperation undoOperation, List<IDiagramItemRO> undoJson);
	
	private void applyLocalRedo() {
		List<CompensationModel> redo = otBuffer.redoBuffer();
		logger.debug("applyLocalRedo {}", redo);
		if (redo != null) {
			for (CompensationModel cm : redo) {
				OTOperation redoop = OTOperation.getEnum(OTOperation.REDO.getValue() + "." + cm.redoOperation);
				applyUndoOrRedoToGraphicalView(redoop, cm.redoJson);
				graphicalDocumentCache.apply(redoop, cm.redoJson);
				extendApplyLocalRedo(redoop, cm.redoOperation, cm.redoJson);
			}
		}
	}
	protected abstract void extendApplyLocalRedo(OTOperation redoop, OTOperation redoOperation, List<IDiagramItemRO> redoJson);

	protected void applyUndoOrRedoToGraphicalView(OTOperation ope, List<IDiagramItemRO> diagramItems) {
		DiagramApplyOperation o = new DiagramApplyOperation(ope, BoardDocumentHelpers.copyDiagramItems(diagramItems), null);
		logger.debug("applyUndoOrRedoToGraphicalView op {}, items.length() {}", o.getOperation(), o.getItems().size());
		List<DiagramApplyOperation> ops = new ArrayList<DiagramApplyOperation>();
		ops.add(o);
		try {
      getBoardHelpers().applyOperationsToGraphicalView(getClientIdentifier(), ops);
    } catch (MappingNotFoundException e) {
      String msg = SLogger.format("MappingNotFoundException msg {}", e.getMessage());
      logger.error(msg);
      if (LogConfiguration.loggingIsEnabled(Level.FINEST)) {
        Window.alert(msg);
      }
    }
	}
	
	protected OTCompensationTransformer getCompensationTransformer() {
		return compensationTransformer;
	}
	
	public OTBuffer getOtBuffer() {
		return otBuffer;
	}
	
	@Override
	public boolean acknowledgedFromServerOrShouldRetry() {
		return true;
	}

 public void setContent(IDiagramContent result) {
    clear();
    getEditorContent().setContent(result);
    getGraphicalViewDocument().reset(result.getDiagramItems());
  }

  public void clear() {
    // TODO how to clear whole surface...
//	    getEditorContent().getSurface().removeAll();
    clearOTBuffer();
    getGraphicalViewDocument().clear();
  }
	  
  // NOTE all global elements need to release all listeners
  public void closeGlobalElements() {
    List<IGlobalElement> globalElements = new ArrayList<IGlobalElement>();
    globalElements.add(ConnectionHelpers.getIfAny(getEditorContent().getModelingPanel().getSurface()));
    globalElements.add(ResizeHelpers.getIfAny(getEditorContent().getModelingPanel().getSurface()));
    globalElements.add(RelationshipHandleHelpers.getIfAny(getEditorContent().getModelingPanel().getSurface()));
    globalElements.add(LifeLineEditorHelper.getIfAny(getEditorContent().getModelingPanel().getSurface()));
    
    release(globalElements);
//	    ConnectionHelpers.createConnectionHelpers(currentEditContent.getModelingPanel().getSurface(), 
//	        currentEditContent.getModelingPanel().getSurface().getModeManager()).removeExtraConnectionHandles();
    
  }

  private void release(List<IGlobalElement> globalElements) {
    for (IGlobalElement g : globalElements) {
      if (g != null) {
        g.release(getEditorContent().getModelingPanel().getSurface());
      }
    }
  }


}
