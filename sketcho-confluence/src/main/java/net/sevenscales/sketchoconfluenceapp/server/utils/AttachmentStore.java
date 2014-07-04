package net.sevenscales.sketchoconfluenceapp.server.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;

public class AttachmentStore implements IStore {
//	private static final Logger log = LoggerFactory.getLogger(AttachmentStore.class);

  public static final String PRE = "sketcho_";
  private AttachmentManager attachmentManager;
  private ContentEntityManager contentEntityManager;
  private PageManager pageManager;
	private PermissionManager permissionManager;
  private String contextPath;

  public AttachmentStore() {
    contentEntityManager = (ContentEntityManager) ContainerManager.getComponent("contentEntityManager");
    attachmentManager = (AttachmentManager) ContainerManager.getComponent("attachmentManager");
    pageManager = (PageManager) ContainerManager.getComponent("pageManager");
    permissionManager = (PermissionManager) ContainerManager.getComponent("permissionManager");
    
    assert(pageManager != null);
    assert(permissionManager != null);
  }
  
  public void setContextPath(String contextPath) {
    this.contextPath = contextPath;
  }
  
  public String versionKey(Long pageId, String attachmentFileName) {
    ContentEntityObject e = contentEntityManager.getById(pageId);
    Attachment a = attachmentManager.getAttachment(e, attachmentFileName);
    int version = a == null ? 0 : a.getAttachmentVersion();
    return attachmentFileName+":"+version;
  }
  
  public byte[] load(Long pageId, String versionKey) {
  	checkPagePermissions(pageId, Permission.VIEW);

    String[] name = versionKey.split(":");
    ContentEntityObject e = contentEntityManager.getById(pageId);
    String imageName = PRE+name[0]+".png";
    Attachment a = attachmentManager.getAttachment(e, imageName);
    if (a == null) {
      return null;
    }
    
    return toByteArray(a);
  }
  
  private byte[] toByteArray(Attachment a) {
    InputStream in;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      in = a.getContentsAsStream();
      final int BUF_SIZE = 1 << 8;
      byte[] buffer = new byte[BUF_SIZE];
      int bytesRead = -1;
      while((bytesRead = in.read(buffer)) > -1) {
          out.write(buffer, 0, bytesRead);
      }
      in.close();
    } catch (IOException e1) {
      e1.printStackTrace();
      throw new RuntimeException(e1);
    }
    return out.toByteArray();
  }

  public String loadContent(Long pageId, String name, String ext) {
//  	log.debug("pageId({}) name({})", pageId, name);
  	// user should not have possibility open xml
  	// if doesn't have edit rights
  	checkPagePermissions(pageId, Permission.EDIT);

    ContentEntityObject e = contentEntityManager.getById(pageId);
    Attachment a = attachmentManager.getAttachment(e, PRE+name+ext);
    if (a == null) {
      return null;
    }
    return loadAttachment(a);
  }
  
  public String loadAttachment(Attachment a) {
	byte[] contentBytes = toByteArray(a);
	String result = null;
	try {
	  result = new String(contentBytes, "UTF8");
	} catch (UnsupportedEncodingException e1) {
	  e1.printStackTrace();
	  throw new RuntimeException(e1);
	}
	return result;
  }

  public String store(Long pageId, String name, StoreEntry entry) {
  	checkPagePermissions(pageId, Permission.EDIT);
  	
  	assert(name == null || "".equals(name));

    // XML
    ContentEntityObject e = contentEntityManager.getById(pageId);
    String xmlAttachmentName = PRE+name+".xml";
    String svgAttachmentName = PRE+name+".svg";
    int version;
    try {
      version = addAttachment(e, "text/xml; charset=utf-8", xmlAttachmentName, entry.getDiagramContent().getBytes("UTF8"));
      addAttachment(e, "text/xml; charset=utf-8", svgAttachmentName, entry.getSvg().getBytes("UTF8"));
    } catch (UnsupportedEncodingException e1) {
      e1.printStackTrace();
      throw new RuntimeException(e1);
    }
    
    // PNG
    String pngAttachmentName = PRE+name+".png";
    addAttachment(e, "image/png", pngAttachmentName, entry.getImage());
    return name+":"+version;
  }
  
  private void checkPagePermissions(Long pageId, Permission permission) {
//  	log.debug("pageId: " + pageId);
  	User user = com.atlassian.confluence.user.AuthenticatedUserThreadLocal.getUser();
		boolean editable = permissionManager.hasPermission(user, permission, pageManager.getPage(pageId));
		
		if (user == null || !editable) {
//	  	log.debug("user Not allowed");
			throw new SecurityException("Not allowed");
		}
	}

	private int addAttachment(ContentEntityObject e, String contentType, String attachmentName, byte[] data) {
    Attachment prevversion = attachmentManager.getAttachment(e, attachmentName);
    Attachment attachment = prevversion;
    
    if (prevversion == null) {
      attachment = new Attachment();
    } else {
      // if attachment already exists, do not save new version if content is exactly the same as previous
      byte[] prevData = toByteArray(prevversion);
      if (prevData.equals(data)) {
        return prevversion.getVersion();
      }
      try {
        prevversion = (Attachment) attachment.clone();
      } catch (CloneNotSupportedException e1) {
        e1.printStackTrace();
        throw new RuntimeException(e1);
      }
    }
    attachment.setContentType(contentType);
    attachment.setFileName(attachmentName);
    attachment.setFileSize(data.length);
    e.addAttachment(attachment);
    
    ByteArrayInputStream is = new ByteArrayInputStream(data);
    try {
      attachmentManager.saveAttachment(attachment, prevversion, is);
    } catch (IOException e1) {
      e1.printStackTrace();
      throw new RuntimeException(e1);
    }
    return attachment.getVersion();
  }

}
