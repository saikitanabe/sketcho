/*
 * Copyright 2009 Fred Sauer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sevenscales.sketcho.client.app.utils;

import net.sevenscales.domain.api.IPage;
import net.sevenscales.plugin.api.SketchUiList;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.IndexedDropController;
import com.allen_sauer.gwt.dnd.client.util.CoordinateLocation;
import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * IndexedDropController that disallows dropping after the last child, which is
 * assumed to be dummy spacer widget preventing parent collapse.
 */
public class NoInsertAtEndIndexedDropController extends IndexedDropController {

  private PageOrganizeHandle handle;
  private IPage page;

  public NoInsertAtEndIndexedDropController(IPage page, PageOrganizeHandle dropHandle) {
    super(dropHandle.getDropTarget());
    this.handle = dropHandle;
    this.page = page;
  }

  @Override
  protected void insert(Widget widget, int beforeIndex) {
    // if trying to put as first move after 0 index
    beforeIndex = beforeIndex == 0 ? 1 : beforeIndex;
    if (beforeIndex == handle.getDropTarget().getWidgetCount()) {
      beforeIndex--;
    }
    super.insert(widget, beforeIndex);
  }
  
  @Override
  protected Widget newPositioner(DragContext context) {
    SimplePanel pos = new SimplePanel();
    Widget positioner = super.newPositioner(context);
    pos.addStyleName("sd-app-NoInsertAtEndIndexedDropController-Positioner");
    pos.setWidget(positioner);
    return pos;
  }
  
  @Override
  public void onEnter(DragContext context) {
    super.onEnter(context);
    handle.getPageName().addStyleName("sd-app-NoInsertAtEndIndexedDropController-onEnter");
  }
  
  @Override
  public void onLeave(DragContext context) {
    super.onLeave(context);
    handle.getPageName().removeStyleName("sd-app-NoInsertAtEndIndexedDropController-onEnter");
  }
  
  @Override
  public void onPreviewDrop(DragContext context) throws VetoDragException {
    super.onPreviewDrop(context);
  }
  
  public IPage getPage() {
    return page;
  }
  
  public IndexedPanel getTarget() {
    return handle.getDropTarget();
  }
}

