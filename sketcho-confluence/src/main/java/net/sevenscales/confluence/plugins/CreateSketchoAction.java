package net.sevenscales.confluence.plugins;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.actions.PageAware;
import com.opensymphony.util.TextUtils;
 
/**
* This Confluence action adds the label 'draft' to the page or blog post when a user selects it from the
* 'Tools' menu in Confluence. Refer to the 'atlassian-plugin.xml' file for details on how this action is
* implemented in Confluence.
*/
public class CreateSketchoAction extends ConfluenceActionSupport implements PageAware {
  private AbstractPage page;
//private LabelManager labelManager;
  private PageManager pageManager;
  private String diagramName;
  
  private static class PrivateUpdate extends DefaultSaveContext {
  	@Override
  	public boolean isEventSuppressed() {
  		return true;
  	}
  }
  
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
  
  public void setPageManager(PageManager pageManager) {
    this.pageManager = pageManager;
  }
  
  public void setDiagramName(String diagramName) {
    this.diagramName = diagramName;
  }
  
//  public void setContentEntityManager(ContentEntityManager contentEntityManager) {
//    this.contentEntityManager = contentEntityManager;
//  }
  
  @Override
  public void validate() {
    if (!TextUtils.stringSet(diagramName)) {
      addFieldError("Diagram name", getText("user.macro.name.empty", new Object[]{"{sketcho}"}));
    }
  }
 
  public String execute() {
    if (!TextUtils.stringSet(diagramName)) {
      addFieldError("Diagram name empty", getText("user.macro.name.empty", new Object[]{"{sketcho}"}));
      return ERROR;
    }

//    page.setContent(page.getContent()+"\n{sketcho:name="+diagramName+"}");
//    
//    SaveContext saveContext = new DefaultSaveContext();
//    pageManager.saveContentEntity(page, saveContext);
    
    try {
	    page.setBodyAsString(page.getBodyAsString() + "<p>" +
	        "<ac:macro ac:name='sketcho'><ac:parameter ac:name='name'>"+diagramName+"</ac:parameter></ac:macro>"
	          + "</p>");
    } catch (java.lang.NoSuchMethodError e) {
    	// Confluence 3 is in use... let's try getContent method
    	String content;
			try {
				content = page.getClass().getMethod("getContent", null).invoke(page, null).toString();
			} catch (Exception e1) {
				// throw if something is wrong
				throw new RuntimeException(e1);
			}
    	
    	//page.setContent(content+"\n{sketcho:name="+diagramName+"}");
    }
    
//    SaveContext saveContext = new DefaultSaveContext();
    pageManager.saveContentEntity(page, new PrivateUpdate());

    return "success";
  }
}
