package net.sevenscales.sketchoconfluenceapp.client;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.appFrame.impl.EventRegistry;
import net.sevenscales.editor.api.MeasurementPanel;
import net.sevenscales.editor.content.Context;
import net.sevenscales.sketchoconfluenceapp.client.view.SketchoEditor;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Sketcho_confluence_app implements EntryPoint {
  private List<SketchoEditor> editors = new ArrayList<SketchoEditor>();
  public static native void log(String msg)/*-{
    if (typeof $wnd.console != "undefined") $wnd.console.log("Sketchboard.Me: " + msg);
  }-*/;

  private static Sketcho_confluence_app instance;
  private Context context;
  public static boolean isJsonService = true;
  public static String resourcesPath = "";
  public static String restServicePath = "";
  private SketchoEditor editor;
  
//  private void editSketch(String moduleSpaceId, String sketchName) {
////    Dictionary modelingSpace = Dictionary.getDictionary("modelingSpace");
////    String modelingId = modelingSpace.get("value");
//    log("modelingSpaceId:"+moduleSpaceId);
//    context.setEventRegistry(new EventRegistry());
//  }
  
  private void loadSketch(String moduleSpaceId, String pageId, String sketchName, String editable) {
  	// enable this only when debugging, not so easy to see page id
//    log("loadSketch:" + "moduleSpaceId(" + moduleSpaceId + "), pageId(" + pageId + "), name("+ sketchName + ")");
    
//    SketchoEditor editor = new SketchoEditor(context, moduleSpaceId, new Long(pageId), sketchName, Boolean.valueOf(editable));
//    editors.add(editor);
//    RootPanel.get(moduleSpaceId).add(editor);
  }
  
  private void openSketch(String moduleSpaceId, String pageId, String sketchName, String selector, String editable) {
  	if (Boolean.valueOf(editable)) {
  		editor.openSketch(moduleSpaceId, new Long(pageId), sketchName, selector, Boolean.valueOf(editable));
  	}
  }
  
  private static native void initApp(Sketcho_confluence_app app)/*-{
//    $wnd.editSketch = function(moduleSpaceId, sketchName) {
//      app.@net.sevenscales.sketchoconfluenceapp.client.Sketcho_confluence_app::editSketch(Ljava/lang/String;Ljava/lang/String;)
//        (moduleSpaceId, sketchName);
//    };
    $wnd.loadSketch = function(moduleSpaceId, pageId, sketchName, editable) {
      app.@net.sevenscales.sketchoconfluenceapp.client.Sketcho_confluence_app::loadSketch(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)
        (moduleSpaceId, pageId, sketchName, editable);
    };
    
    $wnd.openSketch = function(moduleSpaceId, pageId, sketchName, selector, editable) {
      app.@net.sevenscales.sketchoconfluenceapp.client.Sketcho_confluence_app::openSketch(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)
        (moduleSpaceId, pageId, sketchName, selector, editable);
    };

    $wnd.svgViewer.loadAll()
    
//    $wnd.sketchoEditorLoaded();
  }-*/;
  
  public void onModuleLoad() {
  	instance = this;
    log("onModuleLoad");
    this.context = new Context();
    context.setEventRegistry(new EventRegistry());
    editor = new SketchoEditor(context);
    RootPanel.get().add(editor);
    
    MeasurementPanel.init();
    
//    try {
//      Dictionary servicePath = Dictionary.getDictionary("storeServicePath");
//      String value = servicePath.get("value");
//      ServiceDefTarget endpoint = (ServiceDefTarget) StoreService.Util.service;  
//      endpoint.setServiceEntryPoint(value);
////      StoreService.Util.service_url = value;
//      log("servicePath changed:"+endpoint.getServiceEntryPoint());
////      log("storeServicePath changed:"+StoreService.Util.service_url);
//    } catch (Exception e) {
//      // don't do anything resource is gwt
//    }

    try {
      Dictionary servicePath = Dictionary.getDictionary("contentServicePath");
      String value = servicePath.get("value");
//      ServiceDefTarget endpoint = (ServiceDefTarget) StoreService.Util.service;  
//      endpoint.setServiceEntryPoint(value);
      ContentService.Util.service_url = value;
//      log("servicePath changed:"+endpoint.getServiceEntryPoint());
      log("contentServicePath changed:"+ContentService.Util.service_url);
    } catch (Exception e) {
      // don't do anything resource is gwt
    }
    
//    try {
//      Dictionary servicePath = Dictionary.getDictionary("jsonService");
//      String value = servicePath.get("value");
//      log("using jsonService:" + value);
//      isJsonService = Boolean.valueOf(value);
//    } catch (Exception e) {
//      // don't do anything => use default value
//    }

    try {
      Dictionary resourcesPathDict = Dictionary.getDictionary("resourcesPath");
      String value = resourcesPathDict.get("value");
      log("resourcesPath:" + value);
      resourcesPath = value;
    } catch (Exception e) {
      // don't do anything => use default value
    }
    
    try {
      Dictionary dict = Dictionary.getDictionary("restServicePath");
      restServicePath = dict.get("value");
      log("restServicePath: " + restServicePath);
    } catch (Exception e) {
      // don't do anything resource is gwt
    }


    initApp(this);
  }
  
  public List<SketchoEditor> getEditors() {
		return editors;
	}
  
  public static Sketcho_confluence_app getInstance() {
		return instance;
	}
  
}
