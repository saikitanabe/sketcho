package net.sevenscales.confluence.plugins;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.actions.PageAware;
 
/**
* This Confluence action adds the label 'draft' to the page or blog post when a user selects it from the
* 'Tools' menu in Confluence. Refer to the 'atlassian-plugin.xml' file for details on how this action is
* implemented in Confluence.
*/
public class AddSketchoAction extends ConfluenceActionSupport implements PageAware {
  private AbstractPage page;
  //private LabelManager labelManager;
   
  /**
  * Implementation of PageAware
  */
  public AbstractPage getPage()
  {
  return page;
  }
   
  /**
  * Implementation of PageAware
  */
  public void setPage(AbstractPage page)
  {
  this.page = page;
  }
   
  /**
  * Implementation of PageAware:
  * Returning 'true' ensures that the
  * page is set before the action commences.
  */
  public boolean isPageRequired()
  {
  return true;
  }
   
  /**
  * Implementation of PageAware:
  * Returning 'true' ensures that the
  * current version of the page is used.
  */
  public boolean isLatestVersionRequired()
  {
  return true;
  }
   
  /**
  * Implementation of PageAware:
  * Returning 'true' ensures that the user
  * requires page view permissions.
  */
  public boolean isViewPermissionRequired()
  {
  return true;
  }
   
  public String execute() {
    // page is already retrieved by Confluence's PageAwareInterceptor
    // labelManager is injected by Confluence -- see setLabelManager() below
//    Label label = new Label("draft");
//    labelManager.addLabel(page, label);
    return "success";
  }
}
