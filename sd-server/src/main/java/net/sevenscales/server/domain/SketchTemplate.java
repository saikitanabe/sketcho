package net.sevenscales.server.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sevenscales.domain.api.IContent;
import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.api.IListContent;
import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IProperty;
import net.sevenscales.domain.api.ITemplate;
import net.sevenscales.domain.api.ITextContent;
import net.sevenscales.domain.constants.Constants;
import net.sevenscales.domain.dto.ContentPropertyDTO;

public class SketchTemplate implements Serializable, ITemplate {
  private static final long serialVersionUID = -5703261465849383992L;
  private List<IContent> templateFields;
  private Map<String, IProperty> pageProperties = new HashMap<String, IProperty>(); 
  
  /**
   * At some point this will be a database entity, but for now only a runtime configuration is
   * possible.
   */
  public SketchTemplate() {
    PageProperty p = new PageProperty();
    p.setType(ITemplate.BOOLEAN);
    p.setValue(Boolean.FALSE.toString());
    pageProperties.put("nameVisible", p);
    
    templateFields = new ArrayList<IContent>();
    
//    IContent title = new TextLineContent();
//    title.setName("Title");
//    ContentProperty cp = new ContentProperty();
//    cp.setType(ITemplate.BOOLEAN);
//    cp.setValue(Boolean.TRUE.toString());
//    title.getProperties().put(ContentPropertyDTO.NAME_AS_TITLE, cp);
//    templateFields.add(title);

    ContentProperty cp = new ContentProperty();

    IContent link = new LinkContent();
    link.setName("Link");
    cp = new ContentProperty();
    cp.setType(ITemplate.BOOLEAN);
    cp.setValue(Boolean.TRUE.toString());
    link.getProperties().put(ContentPropertyDTO.NAME_AS_TITLE, cp);

    cp = new ContentProperty();
    cp.setType(ITemplate.BOOLEAN);
    cp.setValue(Boolean.FALSE.toString());
    link.getProperties().put(ContentPropertyDTO.DELETABLE, cp);
    templateFields.add(link);

    IListContent state = new ListContent();
    state.setItems("New,Accepted,Started,Designed,Future,Discarded");
    state.setValue("New");
    state.setName("Status");
    cp = new ContentProperty();
    cp.setType(ITemplate.BOOLEAN);
    cp.setValue(Boolean.TRUE.toString());
    state.getProperties().put(ContentPropertyDTO.NAME_AS_TITLE, cp);
    
    cp = new ContentProperty();
    cp.setType(ITemplate.BOOLEAN);
    cp.setValue(Boolean.FALSE.toString());
    state.getProperties().put(ContentPropertyDTO.DELETABLE, cp);
    templateFields.add(state);
    
    ITextContent description = new TextContent();
    cp = new ContentProperty();
    cp.setType(ITemplate.BOOLEAN);
    cp.setValue(Boolean.TRUE.toString());
    description.getProperties().put(ContentPropertyDTO.NAME_AS_TITLE, cp);
    description.setName("Description");

    cp = new ContentProperty();
    cp.setType(ITemplate.BOOLEAN);
    cp.setValue(Boolean.FALSE.toString());
    description.getProperties().put(ContentPropertyDTO.DELETABLE, cp);
    templateFields.add(description);
    
    IDiagramContent model = new DiagramContent();
    cp = new ContentProperty();
    cp.setType(ITemplate.BOOLEAN);
    cp.setValue(Boolean.TRUE.toString());
    model.getProperties().put(ContentPropertyDTO.NAME_AS_TITLE, cp);
    model.setName("Diagram");

    cp = new ContentProperty();
    cp.setType(ITemplate.BOOLEAN);
    cp.setValue(Boolean.FALSE.toString());
    model.getProperties().put(ContentPropertyDTO.DELETABLE, cp);
    templateFields.add(model);

//    new BooleanProperty(ContentPropertyDTO.NAME_AS_TITLE, true);
  }
  
//  private void addProperty(IProperty p) {
//    
//  }
  
  public List<IContent> getTemplateFields() {
    return templateFields;
  }
  public void setTemplateFields(List<IContent> templateFields) {
    this.templateFields = templateFields;
  }
  
  public String generateName(IPage page) {
    return "";
  }
  
  public Integer getPageType() {
    return Constants.PAGE_TYPE_SKETCH;
  }

  public Map<String, IProperty> getPageProperties() {
    return pageProperties;
  }
  public void setPageProperties(Map<String, IProperty> pageProperties) {
    this.pageProperties = pageProperties;
  }
}
