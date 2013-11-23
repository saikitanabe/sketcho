package net.sevenscales.sketchoconfluenceapp.client.util.ot;

import java.util.List;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.ot.AbstractBoardHandlerBase;
import net.sevenscales.editor.api.ot.OTOperation;
import net.sevenscales.editor.content.Context;
import net.sevenscales.editor.content.UiSketchoBoardEditContent;

public class BoardOTConfluenceHandler extends AbstractBoardHandlerBase {
	private static final SLogger logger = SLogger.createLogger(BoardOTConfluenceHandler.class);
	
	public BoardOTConfluenceHandler(String boardName, Context context,
			EditorContext editorContext, UiSketchoBoardEditContent editorContent, 
			IDiagramContent initialContent) {
		super(boardName, context, editorContext);
		setEditorContent(editorContent);
		initHandlers();
		initHelpers();
	}

  @Override
  protected void sendLocalOperation(String name, String originator,
      String operation, List<? extends IDiagramItemRO> operationItems) {
    // TODO Auto-generated method stub
    
  }

	@Override
	protected void extendApplyLocalUndo(OTOperation undoop,
			OTOperation undoOperation, List<IDiagramItemRO> undoJson) {
		// no specific implementation for Confluence	
	}

	@Override
	protected void extendApplyLocalRedo(OTOperation redoop,
			OTOperation redoOperation, List<IDiagramItemRO> redoJson) {
		// no specific implementation for Confluence		
	}

}

