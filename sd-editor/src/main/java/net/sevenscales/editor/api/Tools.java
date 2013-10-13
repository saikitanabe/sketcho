package net.sevenscales.editor.api;

import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent.FreehandModeType;
import net.sevenscales.editor.api.event.FreehandModeChangedEventHandler;
import net.sevenscales.editor.api.event.CommentModeEvent;
import net.sevenscales.editor.api.event.CommentModeEventHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.domain.utils.SLogger;


public class Tools {
	private static final SLogger logger = SLogger.createLogger(Tools.class);
	private static Tools instance;
	private static final int ONES = 0xffffffff;

	static {
		SLogger.addFilter(Tools.class);
	}


	private ISurfaceHandler surface;
	private int currentTools;

	private Tools(ISurfaceHandler surface) {
		this.surface = surface;

		// surface.getEditorContext().getEventBus().addHandler(FreehandModeChangedEvent.TYPE, new FreehandModeChangedEventHandler() {
		// 	@Override
		// 	public void on(FreehandModeChangedEvent event) {
		// 		setFreehandTool(event.isEnabled());
		// 	}
		// });

		// surface.getEditorContext().getEventBus().addHandler(CommentModeEvent.TYPE, new CommentModeEventHandler() {
		// 	@Override
		// 	public void on(CommentModeEvent event) {
		// 		if (eventsOn) {
		// 			_setCommentTool(event.isEnabled());
		// 		}
		// 	}
		// });
	}

	public static Tools create(ISurfaceHandler surface) {
		if (instance == null) {
			instance = new Tools(surface);
		}
		return instance;
	}

	public static void setAtLeastOneAnnotation(boolean atLeastOneAnnotation) {
		instance._setCommentTool(atLeastOneAnnotation);
	}

	public static void setCommentTool(boolean enabled) {
		instance._setCommentTool(enabled);
	}

	private void _setCommentTool(boolean enabled) {
		if (isCommentMode() != enabled) {
			currentTools = enabled ? (currentTools | Tool.COMMENT_TOOL.getValue()) : currentTools & ~Tool.COMMENT_TOOL.getValue();
			logger.debug("TOOLS {}", currentTools);
			surface.getEditorContext().getEventBus().fireEvent(new CommentModeEvent(isCommentMode()));
		}
	}

	public static void toggleCommentMode() {
		instance._toggleCommentMode();
	}
	private void _toggleCommentMode() {
    setCommentTool(!isCommentMode());
    surface.getEditorContext().getEventBus().fireEvent(new CommentModeEvent(isCommentMode()));
	}

	public static boolean isCommentMode() {
		return instance._isCommentMode();
	}

	private boolean _isCommentMode() {
		return (currentTools & Tool.COMMENT_TOOL.getValue()) == Tool.COMMENT_TOOL.getValue();
	}

	public static boolean filterDiagramByCurrentTool(Diagram diagram) {
		return instance._filterDiagramByCurrentTool(diagram);
	}

	private boolean _filterDiagramByCurrentTool(Diagram diagram) {
		return _filterDiagramByTool(diagram, currentTools);
	}

	public static boolean filterDiagramByCommentTool(Diagram diagram) {
		return instance._filterDiagramByTool(diagram, Tool.COMMENT_TOOL.getValue());
	}

	public static boolean filterDiagramByTool(Diagram diagram, Tool tool) {
		return instance._filterDiagramByTool(diagram, tool.getValue());
	}

	private boolean _filterDiagramByTool(Diagram diagram, int tools) {
		boolean commentMode = isCommentMode();
		Tool tool = Tool.getEnum(tools);
		switch (tool) {
			case COMMENT_TOOL: {
				return true;
			}
			case NO_TOOL: {
				return !diagram.isAnnotation();
			}
		}
		return true;
		
	}

}