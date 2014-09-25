package net.sevenscales.editor.api;

import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent.FreehandModeType;
import net.sevenscales.editor.api.event.FreehandModeChangedEventHandler;
import net.sevenscales.editor.api.event.CommentModeEvent;
import net.sevenscales.editor.api.event.CommentModeEventHandler;
import net.sevenscales.editor.api.event.SuperQuickModeEvent;
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

	private Tools(ISurfaceHandler surface, Boolean superQuickMode) {
		this.surface = surface;
		_setQuickMode(superQuickMode);
		// default is curved arrows
		_setCurvedArrow(true);
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

	public static Tools create(ISurfaceHandler surface, Boolean superQuickMode) {
		if (instance == null) {
			instance = new Tools(surface, superQuickMode);
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

	public static void toggleQuickMode() {
		instance._toggleQuickMode();
	}
	private void _toggleQuickMode() {
		setQuickMode(!isQuickMode());
    surface.getEditorContext().getEventBus().fireEvent(new SuperQuickModeEvent(isQuickMode()));
	}
	public static void setQuickMode(boolean enabled) {
		instance._setQuickMode(enabled);
	}
	public void _setQuickMode(boolean enabled) {
		if (enabled) {
			currentTools |= Tool.QUICK_MODE.getValue();
		} else {
			currentTools &= ~Tool.QUICK_MODE.getValue();
		}
	}
	public static boolean isQuickMode() {
		return instance._isQuickMode();
	}
	private boolean _isQuickMode() {
		if (confluence(surface)) {
			return false;
		} else {
			return (currentTools & Tool.QUICK_MODE.getValue()) == Tool.QUICK_MODE.getValue();
		}
	}
	private boolean confluence(ISurfaceHandler surface) {
		return surface.getEditorContext().isTrue(EditorProperty.CONFLUENCE_MODE);
	}	


	public static boolean isCommentMode() {
		return instance._isCommentMode();
	}

	private boolean _isCommentMode() {
		return (currentTools & Tool.COMMENT_TOOL.getValue()) == Tool.COMMENT_TOOL.getValue();
	}

	private boolean _isCurvedArrow() {
		return (currentTools & Tool.CURVED_ARROW.getValue()) == Tool.CURVED_ARROW.getValue();
	}

	public static boolean isCurvedArrow() {
		return instance._isCurvedArrow();
	}

	private void _setCurvedArrow(boolean enabled) {
		currentTools = enabled ? (currentTools | Tool.CURVED_ARROW.getValue()) : currentTools & ~Tool.CURVED_ARROW.getValue();
	}

	public static void enableCurvedArrow() {
		instance._setCurvedArrow(true);
	}
	public static void disableCurvedArrow() {
		instance._setCurvedArrow(false);
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
		if (commentMode) {
			// include all
			return true;
		}
		// do not show annotated and comments
		return !diagram.isAnnotation();
	}

}