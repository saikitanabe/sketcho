package net.sevenscales.confluence.plugins;

import java.util.Map;

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;

/**
 * This very simple macro shows you the very basic use-case of displaying *something* on the Confluence page where it is used.
 * Use this example macro to toy around, and then quickly move on to the next example - this macro doesn't
 * really show you all the fun stuff you can do with Confluence.
 */
public class ExampleMacro extends BaseMacro
{

    // We just have to define the variables and the setters, then Spring injects the correct objects for us to use. Simple and efficient.
    // You just need to know *what* you want to inject and use.

    private final PageManager pageManager;
    private final SpaceManager spaceManager;
    private static final String MACRO_BODY_TEMPLATE = "templates/test-macro.vm";

    public ExampleMacro(PageManager pageManager, SpaceManager spaceManager)
    {
        this.pageManager = pageManager;
        this.spaceManager = spaceManager;
    }

    public boolean isInline()
    {
        return false;
    }

    public boolean hasBody()
    {
        return false;
    }

    public RenderMode getBodyRenderMode()
    {
        return RenderMode.NO_RENDER;
    }

    /**
     * This method returns XHTML to be displayed on the page that uses this macro
     * we just do random stuff here, trying to show how you can access the most basic
     * managers and model objects. No emphasis is put on beauty of code nor on
     * doing actually useful things :-)
     */
    public String execute(Map params, String body, RenderContext renderContext)
            throws MacroException {
      String contextPath = renderContext.getSiteRoot();
      String pluginPath = contextPath+"/download/resources/net.sevenscales.confluence.plugins.test-macro:my-macro";

      Map<String, Object> context = MacroUtils.defaultVelocityContext();
  
      context.put("contextPath", contextPath);
      context.put("pluginPath", pluginPath);
      
      return VelocityUtils.getRenderedTemplate(MACRO_BODY_TEMPLATE, context);
    }

}