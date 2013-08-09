package net.sevenscales.sketchoconfluenceapp.client.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.sevenscales.domain.DiagramContentDTO;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.api.IDiagramItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class DiagramContentParser {
  interface Resources extends ClientBundle {
    Resources INSTANCE = GWT.create(Resources.class);
    
    @Source("DiagramContentTemplate.txt")
    TextResource getDiagramContentTemplate();

    @Source("DiagramItemTemplate.txt")
    TextResource getDiagramItemTemplate();
  }
  
  protected static String parseToXml(String template, Map<String,String> params) {
    for (Entry<String, String> entry : params.entrySet()) {
      template = template.replaceFirst("%"+entry.getKey(), entry.getValue());
    }
    return template;
  }
  
  protected static Set parseDiagramItems(NodeList nodeList) {
    Set<IDiagramItem> result = new HashSet<IDiagramItem>();
    for (int i = 0; i < nodeList.getLength(); ++i) {
      Node n = nodeList.item(i);
      DiagramItemDTO item = new DiagramItemDTO();
      result.add(item);
      for (int x = 0; x < n.getChildNodes().getLength(); ++x) {
        Node c = n.getChildNodes().item(x);
        short type = c.getNodeType();
        if (type == 1) { // ELEMENT_NODE
          String name = c.getNodeName();
          String value = c.getFirstChild().getNodeValue();
          if (name.equals("text")) {
            item.setText(value);
          } else if (name.equals("type")) {
            item.setType(value);
          } else if (name.equals("shape")) {
            item.setShape(value);
          }
        }
      }
    }
    return result;
  }
  
  private static IDiagramContent toDiagram(String modelXml) {
    Document modelDom = XMLParser.parse(modelXml);
    
//    Node diagramItems = modelDom.getElementsByTagName("diagramItems").item(0);
//    DiagramContentDTO content = new DiagramContentDTO();
//    content.setDiagramItems(parseDiagramItems(
//        modelDom.getElementsByTagName("net.sevenscales.domain.dto.DiagramItemDTO")));
//    
//    Node width = modelDom.getElementsByTagName("width").item(0);
//    content.setWidth(Integer.valueOf(width.getFirstChild().getNodeValue()));
//    Node height = modelDom.getElementsByTagName("height").item(0);
//    content.setHeight(Integer.valueOf(height.getFirstChild().getNodeValue()));
    
//    return content;
    return null;
  }

  // currently disabled
  private static String toXml(IDiagramContent diagramContent) {
    StringBuffer diagramItems = new StringBuffer();
    
    for (IDiagramItemRO di : (Set<IDiagramItem>) diagramContent.getDiagramItems()) {
      String itemTemplate = Resources.INSTANCE.getDiagramItemTemplate().getText();
      Map<String,String> itemContext = new HashMap<String, String>();
      itemContext.put("text", di.getText());
      itemContext.put("type", di.getType());
      itemContext.put("shape", di.getShape());
      diagramItems.append(parseToXml(itemTemplate, itemContext));
    }

    StringBuffer result = new StringBuffer();
    String template = Resources.INSTANCE.getDiagramContentTemplate().getText();
    Map<String,String> context = new HashMap<String, String>();
    context.put("diagramItems", diagramItems.toString());
    context.put("width", diagramContent.getWidth().toString());
    context.put("height", diagramContent.getHeight().toString());
    result.append(parseToXml(template, context));
//    <diagramContent class="net.sevenscales.domain.dto.DiagramContentDTO">
//    <diagramItems>
//      <net.sevenscales.domain.dto.DiagramItemDTO>
//        <text>SimpleClass</text>
//        <type>classitem</type>
//        <shape>350,50,100,30</shape>
//        <diagramContent class="net.sevenscales.domain.dto.DiagramContentDTO" reference="../../.."/>
//        <__lazyProperties/>
//      </net.sevenscales.domain.dto.DiagramItemDTO>
//    </diagramItems>
//    <width>700</width>
//    <height>500</height>
//    <properties/>
//    <__lazyProperties/>
//  </diagramContent>
//  <version>0</version>
//</net.sevenscales.sketchoconfluenceapp.server.utils.StoreEntry>

    return result.toString();
  }
}
