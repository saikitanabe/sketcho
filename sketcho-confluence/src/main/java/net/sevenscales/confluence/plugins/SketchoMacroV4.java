package net.sevenscales.confluence.plugins;

import java.util.Map;

import net.sevenscales.sketchoconfluenceapp.server.utils.IStore;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.upm.license.storage.lib.ThirdPartyPluginLicenseStorageManager;

public class SketchoMacroV4 extends SketchoMacro implements Macro {
//	private static final Logger logger = LoggerFactory.getLogger(SketchoMacroV4.class);

	public SketchoMacroV4(PageManager pageManager, SpaceManager spaceManager,
			AttachmentManager attachmentManager, PermissionManager permissionManager,
			IStore storeHandler, ThirdPartyPluginLicenseStorageManager licenseManager) {
		super(pageManager, spaceManager, attachmentManager, permissionManager,
				storeHandler, licenseManager);
	}

	@Override
  public String execute(Map<String, String> params, String body,
      ConversionContext conversionContext) throws MacroExecutionException {
    try {
    	if ("mobile".equals(conversionContext.getPropertyAsString("output-device-type"))) {
	    	Map<String, Object> context = initContext(params, body, (RenderContext) conversionContext.getPageContext());
    		return renderMacroAsImage(params, context, conversionContext.getPageContext());
    	}
      return execute(params, body, (RenderContext) conversionContext.getPageContext());
    } catch (MacroException e) {
      throw new MacroExecutionException(e);
    }
  }

  @Override
  public BodyType getBodyType() {
    return BodyType.NONE;
  }

  @Override
  public OutputType getOutputType() {
    return OutputType.BLOCK;
  }
}
