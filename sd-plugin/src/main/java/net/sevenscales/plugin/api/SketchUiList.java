package net.sevenscales.plugin.api;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import net.sevenscales.appFrame.impl.RequestUtils;
import net.sevenscales.appFrame.impl.uicomponents.ListUiHelper;
import net.sevenscales.domain.api.IPage;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;

public class SketchUiList extends ListUiHelper<IPage> implements ClickHandler {
  
  public SketchUiList() {
    addClickHandler(this);
    setTitle("Click to open");
  }
  
  public void onClick(ClickEvent event) {
    IPage sketch = getDomainObject(event);
    if (sketch != null) {
      Map<Object, Object> requests = new HashMap<Object, Object>();
      requests.put(RequestId.CONTROLLER, RequestValue.SKETCH_CONTROLLER);
      requests.put(RequestId.PROJECT_ID, sketch.getProject().getId());
      requests.put(RequestId.PAGE_ID, sketch.getId());
      RequestUtils.activate(requests);
    }
  }
}
