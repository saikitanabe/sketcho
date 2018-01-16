package net.sevenscales.editor.api;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.event.CommentModeEvent;
import net.sevenscales.editor.api.event.SketchModeEvent;
import net.sevenscales.editor.api.event.SuperQuickModeEvent;
import net.sevenscales.editor.diagram.Diagram;


public class Tools {
	private static final SLogger logger = SLogger.createLogger(Tools.class);
	private static Tools instance;
	private static final int ONES = 0xffffffff;

	static {
		SLogger.addFilter(Tools.class);
	}


	private EditorContext editorContext;
	private int currentTools;
	private boolean exportMode;

	private Tools(EditorContext editorContext, Boolean superQuickMode) {
		this.editorContext = editorContext;
		_setQuickMode(superQuickMode);
		// default is curved arrows
		_setCurvedArrow(true);
		// editorContext.getEditorContext().getEventBus().addHandler(FreehandModeChangedEvent.TYPE, new FreehandModeChangedEventHandler() {
		// 	@Override
		// 	public void on(FreehandModeChangedEvent event) {
		// 		setFreehandTool(event.isEnabled());
		// 	}
		// });

		// editorContext.getEditorContext().getEventBus().addHandler(CommentModeEvent.TYPE, new CommentModeEventHandler() {
		// 	@Override
		// 	public void on(CommentModeEvent event) {
		// 		if (eventsOn) {
		// 			_setCommentTool(event.isEnabled());
		// 		}
		// 	}
		// });

		init(this);
	}

	private native void init(Tools me)/*-{
		if (typeof $wnd.globalStreams !== 'undefined') {
			// preview doesn't have globalStreams
			$wnd.globalStreams.contextMenuStream.filter(function(e) {
				return e && e.type === 'switch-mode'
			}).onValue(function(v) {
				me.@net.sevenscales.editor.api.Tools::_toggleSketchMode()()
			})

			$wnd.globalStreams.handToolShortcutStream.onValue(function() {
				me.@net.sevenscales.editor.api.Tools::_toggleHandTool()()
			})

	    $wnd.cancelStream.onValue(function(v) {
	      me.@net.sevenscales.editor.api.Tools::_setHandTool(Z)(false)
	    })

	    $wnd.globalStreams.addSlideStream.onValue(function() {
	    	me.@net.sevenscales.editor.api.Tools::_setHandTool(Z)(false)
	    })

		}
	}-*/;

	public static Tools create(EditorContext editorContext, Boolean superQuickMode) {
		if (instance == null) {
			instance = new Tools(editorContext, superQuickMode);
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
			editorContext.getEventBus().fireEvent(new CommentModeEvent(isCommentMode()));
		}
	}

	public static void toggleCommentMode() {
		instance._toggleCommentMode();
	}
	private void _toggleCommentMode() {
    setCommentTool(!isCommentMode());
    editorContext.getEventBus().fireEvent(new CommentModeEvent(isCommentMode()));
	}

	public static void toggleQuickMode() {
		instance._toggleQuickMode();
	}
	private void _toggleQuickMode() {
		setQuickMode(!isQuickMode());
    editorContext.getEventBus().fireEvent(new SuperQuickModeEvent(isQuickMode()));
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
		if (confluence()) {
			return false;
		} else {
			return (currentTools & Tool.QUICK_MODE.getValue()) == Tool.QUICK_MODE.getValue();
		}
	}
	public static void setHandTool(boolean enabled) {
		instance._setHandTool(enabled);
	}
	public void _setHandTool(boolean enabled) {
		// check that state is changed
		if (enabled && isHandTool()) {
			return;
		}
		if (!enabled && !isHandTool()) {
			return;
		}

		if (enabled) {
			currentTools |= Tool.HAND_TOOL.getValue();
		} else {
			currentTools &= ~Tool.HAND_TOOL.getValue();
		}

    _fireHandTool(isHandTool());
	}
	public static boolean isHandTool() {
		return instance._isHandTool();
	}
	private boolean _isHandTool() {
		if (confluence()) {
			return false;
		} else {
			return (currentTools & Tool.HAND_TOOL.getValue()) == Tool.HAND_TOOL.getValue();
		}
	}
	public static void toggleHandTool() {
		instance._toggleHandTool();
	}
	private void _toggleHandTool() {
		boolean prevValue = isHandTool();
    setHandTool(!isHandTool());
	}
	private native void _fireHandTool(boolean enabled)/*-{
		if (typeof $wnd.globalStreams.handToolStream !== 'undefined') {
			$wnd.globalStreams.handToolStream.push(enabled);
		}
	}-*/;


	private boolean confluence() {
		return editorContext.isTrue(EditorProperty.CONFLUENCE_MODE);
	}	


	public static void setDiagramProperties(Long diagramProperties) {
		instance._setDiagramProperties(diagramProperties);
	}

	private void _setDiagramProperties(Long diagramProperties) {
		boolean sketchMode = (diagramProperties & DiagramProperty.SKETCH_MODE.getValue()) == DiagramProperty.SKETCH_MODE.getValue();
		setSketchMode(sketchMode);
	}

	public static native int getCurrentSketchMode()/*-{
		return $wnd.tsCurrentSketchMode();
	}-*/;

	public static boolean isSketchMode() {
		return instance._isSketchMode();
	}
	private boolean _isSketchMode() {
		return (currentTools & Tool.SKETCH_MODE.getValue()) == Tool.SKETCH_MODE.getValue();
	}
	public static void setSketchMode(boolean enabled) {
		instance._setSketchMode(enabled);
	}
	public void _setSketchMode(boolean enabled) {
		if (enabled) {
			currentTools |= Tool.SKETCH_MODE.getValue();
		} else {
			currentTools &= ~Tool.SKETCH_MODE.getValue();
		}
	}
	public static void toggleSketchMode() {
		instance._toggleSketchMode();
	}
	private void _toggleSketchMode() {
		setSketchMode(!isSketchMode());
    editorContext.getEventBus().fireEvent(new SketchModeEvent(isSketchMode()));
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

	public static boolean isExport() {
		return instance.exportMode;
	}
	public static void setExportMode(boolean export) {
		instance.exportMode = export;
	}

}