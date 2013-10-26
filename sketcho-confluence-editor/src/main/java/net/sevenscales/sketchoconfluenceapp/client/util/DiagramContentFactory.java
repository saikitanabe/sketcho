package net.sevenscales.sketchoconfluenceapp.client.util;

import net.sevenscales.domain.DiagramContentDTO;
import net.sevenscales.domain.JSONContentParser;
import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.utils.JsonFormat;
import net.sevenscales.domain.json.JsonExtraction;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.content.Context;
import net.sevenscales.editor.gfx.svg.converter.SvgData;
import net.sevenscales.sketchoconfluenceapp.client.ContentService;
import net.sevenscales.sketchoconfluenceapp.client.Sketcho_confluence_app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DiagramContentFactory {
	/**
	 * Convert the string of JSON into JavaScript object.
	 */
	// private static final JsArray<DiagramItemJS> asArrayOfDiagramItemJS(String
	// json) {
	// return JsonUtils.safeEval(json);
	// }

	public static void create(Long pageId, String name, final Context context,
			final AsyncCallback<IDiagramContent> callback, final boolean inDialog,
			final EditorContext editorContext) {
		// TODO: check if json service is used
		if (!Sketcho_confluence_app.isJsonService) {
//			RequestBuilder rb = StoreService.Util.service.load(pageId, name,
//					new AsyncCallback<IDiagramContent>() {
//						public void onSuccess(IDiagramContent result) {
//							// IDiagramContent content =
//							// DiagramContentParser.toDiagram(result);
//							callback.onSuccess(result);
//						}
//
//						public void onFailure(Throwable caught) {
//							System.out.println("DiagramContentFactory error: " + caught);
//							callback.onFailure(caught);
//						}
//					});
//			rb.setTimeoutMillis(6000);
//			try {
//				rb.send();
//			} catch (RequestException e) {
//				Window.alert("Ups, unexpected error, cannot load your sketch. Please reload the page and try again.");
//			}
	 } else {
		 // use json Service
		 jsonService(pageId, name, context, callback, editorContext);
	 }
		// ContentService.loadDiagramContent(name, new AsyncCallback<String>() {
		// public void onSuccess(String result) {
		// IDiagramContent content = DiagramContentParser.toDiagram(result);
		// UiDiagramEditContent editContent = new UiDiagramEditContent(content,
		// context);
		// callback.onSuccess(editContent);
		// }
		// public void onFailure(Throwable caught) {
		// callback.onFailure(caught);
		// }
		// });
	}

	private static String jsonBaseUrl() {
		return GWT.getModuleBaseURL().replace("/" + GWT.getModuleName(), "");
	}
	
	private static String restService() {
//		return Window.Location.getProtocol() + "//" + Window.Location.getHost() + Sketcho_confluence_app.restServicePath;
		return Sketcho_confluence_app.restServicePath;
	}

	private static void jsonService(Long pageId, final String name, final Context context,
			final AsyncCallback<IDiagramContent> callback, final EditorContext editorContext) {
		// Send request to server and catch any errors.
		String url = restService() + pageId + "%3A" + name + ".json";
		System.out.println("url: " + url);
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		builder.setHeader("Accept", "application/json");

		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					System.out.println("Couldn't retrieve JSON");
				}

				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						processResponse(name, response.getText(), context, callback, editorContext);
					} else {
						System.out.println("Couldn't retrieve JSON ("
								+ response.getStatusCode() + ", " + response.getText() + ")");
					}
				}
			});
		} catch (RequestException e) {
			System.out.println("Couldn't retrieve JSON");
		}
	}

	private static void processResponse(String name, String json,
			Context context, AsyncCallback<IDiagramContent> callback, EditorContext editorContext) {
		System.out.println("json: " + json);
//		DiagramContentJS contentJs = JsonUtils.safeEval(json);
		JSONContentParser parser = new JSONContentParser(json);
		DiagramContentDTO result = parser.toDTO();
		callback.onSuccess(result);
	}

	public static void store(Long pageId, String name, IDiagramContent content, SvgData svg,
			AsyncCallback<String> asyncCallback) {
		if (!Sketcho_confluence_app.isJsonService) {
//			gwtRpcServiceStore(pageId, name, content, svg, asyncCallback);
		} else {
			jsonServiceStore(pageId, name, content, svg, asyncCallback);
		}
	}

	private static void jsonServiceStore(Long pageId, String name, IDiagramContent content,
			SvgData svg, final AsyncCallback<String> asyncCallback) {
		DiagramContentDTO dto = (DiagramContentDTO) content;
		dto.setName(name);
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, restService());
		builder.setHeader("Content-Type", "application/json");
		// System.out.println("JSON: " + dto.asJson().toString());
		JSONValue json = JsonExtraction.decompose(dto, JsonFormat.PRESENTATION_FORMAT); // DiagramContentJS.asJson2(dto, JsonFormat.PRESENTATION_FORMAT);
		JSONObject obj = (JSONObject) json;
		obj.put("svg", new JSONString(svg.svg)); // add SVG separately; not in DTO
		obj.put("pageId", new JSONNumber(pageId)); // add SVG separately; not
//		obj.put("svgwidth", new JSONNumber(svg.width)); // add SVG separately; not
																										// in DTO
		builder.setRequestData(obj.toString());
		builder.setCallback(new RequestCallback() {
			@Override
			public void onResponseReceived(Request request, Response response) {
				GWT.log("jsonServiceStore response: " + response.getStatusCode());
				if (response.getStatusCode() == 200) {
					GWT.log(response.getText());
//					DiagramContentJS contentJs = JsonUtils.safeEval(response.getText());
//					DiagramContentDTO result = contentJs.asDTO();
					asyncCallback.onSuccess(jsonImage(response.getText()));
				} else {
					asyncCallback.onFailure(new RuntimeException("Status Code: "
							+ response.getStatusCode()));
					Window.alert("Status Code: " + response.getStatusCode());
				}
			}

			@Override
			public void onError(Request request, Throwable exception) {
				asyncCallback.onFailure(exception);
				Window.alert(exception.getMessage());
			}
		});

		try {
			builder.send();
		} catch (RequestException e) {
			e.printStackTrace();
			Window.alert(e.getMessage());
		}
	}

//	private static void gwtRpcServiceStore(Long pageId, String name, IDiagramContent content,
//			SvgData svg, final AsyncCallback<String> asyncCallback) {
//		StoreService.Util.service.store(pageId, name, (IDiagramContent) content, svg.svg,
//				new AsyncCallback<String>() {
//					public void onSuccess(String result) {
//						final String url = ContentService.Util.service_url + result;
//						asyncCallback.onSuccess(url);
//					}
//
//					public void onFailure(Throwable caught) {
//						asyncCallback.onFailure(caught);
//					}
//				});
//	}

	public static String diagramImageUrl(Long pageId, String diagramName, String version,
			boolean thumbnail) {
//		if (Sketcho_confluence_app.isJsonService) {
//			return jsonImage(diagramName, version, thumbnail);
//		}
		return gwtRpcImage(pageId, diagramName, version);
	}

	private static String gwtRpcImage(Long pageId, String diagramName, String version) {
//		return Window.Location.getProtocol() + "//" + Window.Location.getHost() + 
		version = String.valueOf(System.currentTimeMillis());
		return ContentService.Util.service_url + "?pageId="+ pageId + "&content=" + diagramName + "%3A" + version;
	}

	private static String jsonImage(Long pageId, String diagramName, String version) {
//		return Window.Location.getProtocol() + "//" + Window.Location.getHost() + 
		return ContentService.Util.service_url + "?pageId="+ pageId + "&content=" + diagramName + "%3A"
					 + version;
	}
	
	private static String jsonImage(String diagramNameAndVersion) {
//		return Window.Location.getProtocol() + "//" + Window.Location.getHost() + 
			return ContentService.Util.service_url + diagramNameAndVersion;
	}

}
