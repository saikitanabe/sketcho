package net.sevenscales.confluence.plugins;

import java.util.Map;

import net.sevenscales.sketchoconfluenceapp.server.utils.AttachmentStore;
import net.sevenscales.sketchoconfluenceapp.server.utils.IStore;
import net.sevenscales.sketchoconfluenceapp.server.utils.SvgUtil;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.confluence.velocity.htmlsafe.HtmlFragment;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.license.storage.lib.PluginLicenseStoragePluginUnresolvedException;
import com.atlassian.upm.license.storage.lib.ThirdPartyPluginLicenseStorageManager;

/**
 * This very simple macro shows you the very basic use-case of displaying
 * *something* on the Confluence page where it is used. Use this example macro
 * to toy around, and then quickly move on to the next example - this macro
 * doesn't really show you all the fun stuff you can do with Confluence.
 */
public class SketchoMacro extends BaseMacro {

	// We just have to define the variables and the setters, then Spring injects
	// the correct objects for us to use. Simple and efficient.
	// You just need to know *what* you want to inject and use.

	private final PageManager pageManager;
	private final SpaceManager spaceManager;
	private BandanaManager bandanaManager;
	private static final String MACRO_BODY_TEMPLATE = "templates/sketcho-macro.vm";
	private IStore storeHandler;
	// private PageContext markPageContext;

	// used for unique div identifier
	private int index;
	private AttachmentManager attachmentManager;
	private PermissionManager permissionManager;
	private ThirdPartyPluginLicenseStorageManager licenseManager;
	private SettingsManager settingsManager;
	private BootstrapManager bootstrapManager;

	public SketchoMacro(PageManager pageManager, SpaceManager spaceManager,
			AttachmentManager attachmentManager, PermissionManager permissionManager,
			IStore storeHandler, ThirdPartyPluginLicenseStorageManager licenseManager) {
		this.pageManager = pageManager;
		this.spaceManager = spaceManager;
		this.attachmentManager = attachmentManager;
		this.storeHandler = storeHandler;
		this.permissionManager = permissionManager;
		this.licenseManager = licenseManager;
		this.settingsManager = (SettingsManager) ContainerManager.getComponent("settingsManager");
	}

	public void setBandanaManager(BandanaManager bandanaManager) {
		this.bandanaManager = bandanaManager;
	}
	
	public void setSettingsManager(SettingsManager settingsManager) {
		this.settingsManager = settingsManager;
	}
	
	public void setBootstrapManager(BootstrapManager bootstrapManager) {
		this.bootstrapManager = bootstrapManager;
	}

	public void setStoreHandler(IStore storeHandler) {
		this.storeHandler = storeHandler;
	}

	public void setPermissionManager(PermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}

	public boolean isInline() {
		return false;
	}

	public boolean hasBody() {
		return false;
	}

	public RenderMode getBodyRenderMode() {
		return RenderMode.ALL;
	}

	/**
	 * This method returns XHTML to be displayed on the page that uses this macro
	 * we just do random stuff here, trying to show how you can access the most
	 * basic managers and model objects. No emphasis is put on beauty of code nor
	 * on doing actually useful things :-)
	 */
	@Override
	public String execute(Map params, String body, RenderContext renderContext)
			throws MacroException {
		Map<String, Object> context = initContext(params, body, renderContext);
		PageContext pageContext = (PageContext) renderContext;
		return renderMacro(params, context, (PageContext) renderContext);
	}
	
	protected Map<String, Object> initContext(Map params, String body,
			RenderContext renderContext) {
		PageContext pageContext = (PageContext) renderContext;
		Map<String, Object> context = MacroUtils.defaultVelocityContext();
		if (pageContext.getEntity() instanceof Page) {
			Page page = (Page) pageContext.getEntity();
			// String content = page.getContent();

			// boolean writeScript = false;
			// if (markPageContext == null || markPageContext == pageContext) {
			// this.markPageContext = pageContext;
			// writeScript = true;
			// }
			// webResourceManager.requireResource("net.sevenscales.confluence.plugins.sketcho-confluence:sketcho_confluence_app.nocache");

			String contextPath = renderContext.getSiteRoot();
			storeHandler.setContextPath(contextPath);

			String pluginPath = contextPath
					+ "/download/resources/net.sevenscales.confluence.plugins.sketcho-confluence:sketcho-macro";
			String resourcesPath = pluginPath + "/";
			String modelName = (String) params.get("name");
			if (modelName != null) {
//				if (editable) {
//					editable = sketchoManager.editableByUser();
//				}
				
				context.put("restServicePath", restServicePath(contextPath));
				context.put("contextPath", contextPath);
				context.put("pluginPath", pluginPath);
				context.put("resourcesPath", resourcesPath);
				context.put("modelName", modelName);
				// context.put("writeScript", writeScript);
				context.put("servletPath", contextPath + "/plugins/servlet");
				String spaceId = storeHandler.versionKey(page.getId(), modelName);
				context.put("modelingSpace", spaceId);
				context.put("export",
						!pageContext.getOutputType().equals(RenderContext.DISPLAY));
				context.put("pageId", page.getIdAsString());
				context.put("svgUrl", svgUrl(params, context, pageContext));

				// NOTE this is potential attack vector since could put any javascript to the page
				// cannot be used until proper svg validator exists, that doesn't allow javascript
				// context.put("svgContent", svgContent(params, context, pageContext));
				context.put("imgUrl", imgUrl(params, context, pageContext));
				context.put("classname", spaceId.replaceAll(":", "-").replaceAll("\\.", "_").replaceAll("\\s", "_"));
				// System.out.println("context: " + context);
//				context.put("trialLicense", sketchoManager.isTrialLicense());
//				context.put("termsViolation", !sketchoManager.validUserCount());

				applyPermissions(pageContext, context);
			}
		}
		return context;
	}

	private void applyPermissions(PageContext pageContext, Map<String, Object> context) {
		boolean editable = false; // default for anonymous user
		if (com.atlassian.confluence.user.AuthenticatedUserThreadLocal
		.getUser() != null) {
			editable = permissionManager.hasPermission(
					com.atlassian.confluence.user.AuthenticatedUserThreadLocal
							.getUser(), Permission.EDIT, pageContext.getEntity());
		}
		
		boolean licenseAllowsEditing = configureLisence(context);
		if (editable) {
			context.put("editable", licenseAllowsEditing);
		}
	}

	protected String renderMacro(Map<String, String> params, Map<String, Object> context, PageContext pageContext) {
		if (pageContext.getOutputType().equals(RenderContext.DISPLAY)) {
			return VelocityUtils
					.getRenderedTemplate(MACRO_BODY_TEMPLATE, context);
		}

		return renderMacroAsImage(params, context, pageContext);
	}

	protected String renderMacroAsImage(Map<String, String> params, Map<String, Object> context, PageContext pageContext) {
		if (pageContext.getEntity() instanceof Page) {
			String url = imgUrl(params, context, pageContext);
			return new StringBuilder("<div><img src='").append(url).append("'></div>").toString();
		}
		return "";
	}
	
	private String imgUrl(Map<String, String> params, Map<String, Object> context, PageContext pageContext) {
		Page page = (Page) pageContext.getEntity();
		Attachment a = attachmentManager.getAttachment(page, AttachmentStore.PRE + (String) params.get("name") + ".png");
		if (a == null) {
			return "";
		}
		return new StringBuilder().append(context.get("contextPath")).append(a.getDownloadPath()).toString();
	}

	private String svgUrl(Map<String, String> params, Map<String, Object> context, PageContext pageContext) {
		Page page = (Page) pageContext.getEntity();
		Attachment a = attachmentManager.getAttachment(page, AttachmentStore.PRE + (String) params.get("name") + ".svg");
		if (a == null) {
			return "";
		}
		return new StringBuilder().append(context.get("contextPath")).append(a.getDownloadPath()).toString();
	}

	private HtmlFragment svgContent(Map<String, String> params, Map<String, Object> context, PageContext pageContext) {
		Page page = (Page) pageContext.getEntity();
		Attachment a = attachmentManager.getAttachment(page, AttachmentStore.PRE + (String) params.get("name") + ".svg");
		if (a == null) {
			return new HtmlFragment("");
		}
		// return new StringBuilder().append(context.get("contextPath")).append(a.getDownloadPath()).toString();
		String svg = storeHandler.loadContent(page.getId(), params.get("name"), ".svg");
		String safeSvg = SvgUtil.validatedSvg(svg);
		System.out.println("safeSvg: " + safeSvg);
		return new HtmlFragment(safeSvg);
	}

	private String  restServicePath(String contextPath) {
		String restPath = "/rest/storerestservice/1.0/sketch/";
//		if (settingsManager != null) {
////			// newer versions of Confluence
//			return settingsManager.getGlobalSettings().get + restPath;
//		}
//		// older versions of Confluence
//		String baseUrl = bootstrapManager.getBaseUrl();
//		String contextPath = bootstrapManager.getWebAppContextPath();
		return contextPath + restPath;
	}

	private boolean configureLisence(Map<String, Object> context) {
		boolean editable = false;
    try
    {
        //Check and see if a license is currently stored.
        //This accessor method can be used whether or not a licensing-aware UPM is present.
        if (licenseManager.getLicense().isDefined())
        {
            PluginLicense pluginLicense = licenseManager.getLicense().get();
            //Check and see if the stored license has an error. If not, it is currently valid.
            if (pluginLicense.getError().isDefined())
            {
                //A license is currently stored, however, it is invalid (e.g. expired or user count mismatch)
            	context.put("notValidLicense", true);
//                resp.getWriter().write("I'd love to say hello, but cannot do so because your license has an error: " 
//                        + pluginLicense.getError().get().name());
            }
            else
            {
                //A license is currently stored and it is valid.
//            	context.put("notValidLicense", false);
            	editable = true;
//                resp.getWriter().write("Hello, world! You are licensed!");
            }
        }
        else
        {
            //No license (valid or invalid) is stored.
        	context.put("noLicense", true);
//            resp.getWriter().write("I'd love to say hello, but cannot do so because you don't have a license.");
        }
    }
    catch (PluginLicenseStoragePluginUnresolvedException e)
    {
        //The current license status cannot be retrieved because the Plugin License Storage plugin is unavailable.
    	context.put("licenseResourceProblem", true);
//        resp.getWriter().write("I'd love to say hello, but cannot find my required resources. Please speak to a system administrator.");
    }
    return editable;
	}

}
