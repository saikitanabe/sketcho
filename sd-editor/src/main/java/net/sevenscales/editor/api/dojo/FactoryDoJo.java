package net.sevenscales.editor.api.dojo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.UIObject;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.IModelingPanel;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.ot.OTBuffer;
import net.sevenscales.editor.api.ot.OperationTransaction;
import net.sevenscales.editor.content.ui.IModeManager;

public class FactoryDoJo {
	private static final SLogger logger = SLogger.createLogger(FactoryDoJo.class);

	public static IModelingPanel createModelingPanel(UIObject parent, int width, int height, boolean editable, IModeManager modeManager, EditorContext editorContext, OTBuffer otBuffer, OperationTransaction operationTransaction, Boolean superQuickMode) {
		try {
			return new ModelingPanel(parent, width, height, editable, modeManager, editorContext, false, true, otBuffer, operationTransaction, superQuickMode);
		} catch (Exception e) {
			logger.error("createModelingPanel", e);
			throw new RuntimeException(e);
		}
	}

	public static ISurfaceHandler createSurfaceHandler() {
		return GWT.create(SurfaceHandlerImplFirefox.class);
	}
}