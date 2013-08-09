package net.sevenscales.sketchoconfluenceapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ContentService {
  public static class Util {
    public static String service_url = GWT.getHostPageBaseURL()+"resources/contentImage"; 
  }
//  public static String content_service_url = GWT.getHostPageBaseURL()+"resources/content"; 
  
  private static void loadDiagramContent(String name, final AsyncCallback<String> callback) {
//    Map<String,String> reqs = new HashMap<String, String>();
//    reqs.put("name", name);
//    String formattedReqs = Location.formatRequests(reqs); 
//    String url = content_service_url+formattedReqs;
//    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));
//    try {
//      Request request = builder.sendRequest(null, new RequestCallback() {
//        public void onError(Request request, Throwable exception) {
//          callback.onFailure(exception);
//        }
//
//        public void onResponseReceived(Request request, Response response) {
//          if (200 == response.getStatusCode()) {
//            String contentXml = response.getText();
//            callback.onSuccess(contentXml);
//          } else {
//            callback.onFailure(new Throwable("Request not successful"));
//          }
//        }       
//      });
//    } catch (RequestException e) {
//      callback.onFailure(new Throwable("Couldn't connect to server"));
//    }
  }

}
