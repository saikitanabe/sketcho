package net.sevenscales.server.gwt;


import java.io.File;
import java.io.IOException;
import java.util.List;

import net.sevenscales.domain.api.IContent;
import net.sevenscales.server.Configuration;
import net.sevenscales.server.GwtController;
import net.sevenscales.server.dao.IContentDAO;
import net.sevenscales.server.image.SvgUtil;
import net.sevenscales.serverAPI.remote.ContentRemote;
import net.sf.hibernate4gwt.core.HibernateBeanManager;

import org.springframework.beans.factory.annotation.Required;

public class ContentRemoteImpl extends GwtController implements ContentRemote {
  private IContentDAO contentDAO;
  private HibernateBeanManager beanManager;
  
  public ContentRemoteImpl() {
//    ApplicationContext.getInstance().loadConfiguration(getConfiguration());
//    contentDAO = (IContentDAO) ApplicationContext.getInstance().getBean(IContentDAO.NAME);
  }

  protected Configuration getConfiguration() {
    return new Configuration("applicationContext.xml");
  }
  
  public IContentDAO getContentDAO() {
    return contentDAO;
  }

  public void setContentDAO(IContentDAO contentDAO) {
    this.contentDAO = contentDAO;
  }
  
  @Required
  public void setBeanManager(HibernateBeanManager beanManager) {
	this.beanManager = beanManager;
  }

//  @Override
  public List<IContent> findAll() {
    List<IContent> result = contentDAO.findAll();
    result = (List<IContent>) beanManager.clone(result);
    return result;
  }
//  @Override
  public IContent open(Long id) {
    return (IContent) beanManager.clone(contentDAO.open(id));
  }
//  @Override
  public IContent save(IContent content) {
    content = (IContent) beanManager.merge(content);
    return (IContent) beanManager.clone(contentDAO.save(content));
  }
//  @Override
  public IContent update(IContent content) {
    content = (IContent) beanManager.merge(content);
    return (IContent) beanManager.clone(contentDAO.update(content));
  }

//  @Override
  public void remove(IContent content) {
    content = (IContent) beanManager.merge(content);
    contentDAO.remove(content);
  }

//  @Override
  public void removeAll(List<IContent> contents) {
    contents = (List<IContent>) beanManager.merge(contents);
    contentDAO.removeAll(contents);
  }
  
  public String downloadImage(String svg, Long contentId) {
	    IContent content = contentDAO.open(contentId);
	    Long time = content.getModifiedTime();
	    String name = contentId+"_"+time;
    try {
      SvgUtil.createPng(svg, name);
      SvgUtil.cleanHouse();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return "?content="+name;
//    int length = 0;
//    
//    File png;
//    try {
//      png = SvgUtil.createPng(svg);
//    
//      ServletOutputStream os = getThreadLocalResponse().getOutputStream();
////      String mimeType = getServletContext().getMimeType(png.getName());
////      getThreadLocalResponse().setContentType(mimeType);
//      getThreadLocalResponse().setContentType("application/octet-stream"); 
//      getThreadLocalResponse().setContentLength( (int) png.length());
//      getThreadLocalResponse().setHeader("Content-Disposition","attachment; filename=\"" +
//          png.getName() + "\"");
//      
//      FileInputStream fis = new FileInputStream(png);
//      FileCopyUtils.copy(fis, os);
//      fis.close();
//      os.flush();
//      os.close();
      
//      String text = "<abc ddd> <123></123></abc>"; 
//      HttpServletResponse response = getThreadLocalResponse();
//      response.setContentLength(text.getBytes().length);
//      FileCopyUtils.copy(text.getBytes(), response.getOutputStream());
//
//      //non-spring  
//      response.setContentLength(text.getBytes().length);
//      //.. ur code
//      ServletOutputStream sos = response.getOutputStream();
//      sos.write(text.getBytes());
//      sos.flush();
//      sos.close(); 
      
//      getThreadLocalResponse().setContentType("application/octet-stream"); 
//      getThreadLocalResponse().setContentLength((int) png.length()); 
//      getThreadLocalResponse().setHeader("Content-Disposition", "attachment; filename*=\"utf-8''" + png.getName()); 
//      byte[] bbuf = new byte[1024];
//      DataInputStream in = new DataInputStream(new FileInputStream(png)); 
//      while ((in != null) && ((length = in.read(bbuf)) != -1)) { 
//          os.write(bbuf, 0, length); 
//      } 
//      in.close(); 
//      fis.close();
//      os.flush(); 
//      os.close();
//    } catch (IOException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
  }
}
