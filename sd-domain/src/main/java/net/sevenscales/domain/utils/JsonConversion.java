package net.sevenscales.domain.utils;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.domain.IDiagramItemRO;

public class JsonConversion {
  public static JsonConversion EMPTY;
  
  static {
    EMPTY = new JsonConversion("", JsonFormat.SEND_FORMAT, new ArrayList<IDiagramItemRO>());
  }
  
  private String json;
  private JsonFormat jsonFormatType;
  private List<? extends IDiagramItemRO> presentation;
  
  public JsonConversion(String json, JsonFormat jsonFormatType, List<? extends IDiagramItemRO> presentation) {
    this.json = json;
    this.jsonFormatType = jsonFormatType;
    this.presentation = presentation;
  }
  
  public String getJson() {
    return json;
  }
  public JsonFormat getJsonFormatType() {
    return jsonFormatType;
  }
  public List<? extends IDiagramItemRO> getPresentation() {
    return presentation;
  }
}
