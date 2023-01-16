package net.sevenscales.editor.api.ot;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.JSONBoardUserParser;
import net.sevenscales.domain.JSONDiagramParser;
import net.sevenscales.domain.JSONParserHelpers;

public class ApplyHelpers {

	public static ApplyOperations toApplyOperations(String operations) {
		ApplyOperations result = new ApplyOperations();
		result.diagramOperations = new ArrayList<DiagramApplyOperation>();
		result.userOperations = new ArrayList<BoardUserApplyOperation>();

		// JsArray<OperationJS> ops = JsonUtils.safeEval(operations);
    JSONValue jvalue = JSONParser.parseStrict(operations);
    if (jvalue.isArray() != null) {
      // comes from the server, and doesn't contain tab id
      parseOperations(jvalue.isArray(), result);
    } else if (jvalue.isObject() != null) {
      // comes from the localstorage (from a different tab) and contains also tab_id

      JSONValue jTabId = jvalue.isObject().get("tab_id");
      if (jTabId.isString() != null) {
        result.tabId = jTabId.isString().stringValue();
      }

      JSONValue jops = jvalue.isObject().get("operations");
      if (jops.isArray() != null) {
        parseOperations(jops.isArray(), result);
      }
      
    }

		// for (int i = 0; i < ops.length(); ++i) {
		// 	OperationJS opjs = ops.get(i);
		// 	OTOperation operation = OTOperation.getEnum(opjs.getOperation());
		// 	if (opjs.getOperation().startsWith("user.")) {
		// 		BoardUserApplyOperation applyOperation = createBoardUserApplyOperation(operation, opjs.getUsers());
		// 		if (applyOperation != null) {
		// 			result.userOperations.add(applyOperation);
		// 		}
		// 	} else {
		// 		DiagramApplyOperation applyOperation = createDiagramApplyOperation(operation, opjs.getItems());
		// 		result.diagramOperations.add(applyOperation);
		// 	}
		// }
		return result;
	}

  // SketchBoardServer.Update
  public static List<BoardUpdate> toUpdateOperations(String updates) {
    List<BoardUpdate> result = new ArrayList<BoardUpdate>();

    JSONValue jvalue = JSONParser.parseStrict(updates);
    JSONArray array = jvalue.isArray();

    if (array != null) {
      for (int i = 0; i < array.size(); ++i) {
        JSONObject update = array.get(i).isObject();
        if (update != null) {
          BoardUpdate bu = new BoardUpdate();
          bu.name = JSONParserHelpers.getString(update.get("name"));
          bu.originator = JSONParserHelpers.getString(update.get("originator"));
          bu.siteId = JSONParserHelpers.getString(update.get("siteId"));
          bu.version = JSONParserHelpers.getInt(update.get("version"));
          bu.checksum = JSONParserHelpers.getString(update.get("checksum"));
          // bu.guids = TODO

          JSONArray dops = update.get("doperations").isArray();
          if (dops != null) {
            for (int x = 0; x < dops.size(); ++x) {
              JSONObject dop = dops.get(x).isObject();
              if (dop != null) {
                JSONArray items = dop.get("items").isArray();
                JSONString guid = dop.get("guid").isString();
                JSONValue o = dop.get("operation");
                if (o.isString() != null && items != null && guid != null) {
                  OTOperation operation = OTOperation.getEnum(o.isString().stringValue());
                  DiagramApplyOperation applyOperation = createDiagramApplyOperation(operation, items.isArray(), guid.stringValue());
                  bu.operations.add(applyOperation);
                }
              }
            }
          }

          result.add(bu);
        }
      }
    }

    return result;
  }

	private static void parseOperations(JSONArray operations, ApplyOperations applyOperations) {
		for (int i = 0; i < operations.size(); ++i) {
			JSONValue operation = operations.get(i);
			if (operation.isObject() != null) {
				parseOperation(operation.isObject(), applyOperations);
			}
		}
	}

	private static void parseOperation(JSONObject operation, ApplyOperations applyOperations) {
		JSONValue o = operation.get("operation");
		if (o.isString() != null) {
			parseOperation(OTOperation.getEnum(o.isString().stringValue()), operation, applyOperations);
		}
	}

	private static void parseOperation(OTOperation operation, JSONObject operationObject, ApplyOperations applyOperations) {
		if (operation.getValue().startsWith("user.") && operationObject.get("users").isArray() != null) {
			JSONArray jusers = operationObject.get("users").isArray();
			BoardUserApplyOperation applyOperation = createBoardUserApplyOperation(operation, jusers);
			if (applyOperation != null) {
				applyOperations.userOperations.add(applyOperation);
			}
		} else {
			JSONArray items = operationObject.get("items").isArray();
			JSONString guid = operationObject.get("guid").isString();
			if (items != null && guid != null) {
				DiagramApplyOperation applyOperation = createDiagramApplyOperation(operation, items.isArray(), guid.stringValue());
				applyOperations.diagramOperations.add(applyOperation);
			}
		}

	}

	public static abstract class ApplyOperation {
		private OTOperation operation;

		public ApplyOperation(OTOperation operation) {
			this.operation = operation;
		}

		public OTOperation getOperation() {
			return operation;
		}
	}

	public static class BoardUserApplyOperation extends ApplyOperation {
		private String email;
		private String username;
		private String avatarUrl;
		private boolean sketchboardAvatar;
		private int x;
		private int y;
		private int targetx;
		private int targety;
		private String selectedCids;
		private String clientIdentifier;

		public BoardUserApplyOperation(OTOperation operation, String email, String username, String avatarUrl, boolean sketchboardAvatar, int x, int y, int targetx, int targety, String selectedCids, String clientIdentifier) {
			super(operation);
			this.email = email;
			this.username = username;
			this.avatarUrl = avatarUrl;
			this.sketchboardAvatar = sketchboardAvatar;
			this.x = x;
			this.y = y;
			this.targetx = targetx;
			this.targety = targety;
			this.selectedCids = selectedCids;
			this.clientIdentifier = clientIdentifier;
		}

		public String getEmail() {
			return email;
		}

		public String getUsername() {
			return username;
		}

		public String getAvatarUrl() {
			return avatarUrl;
		}

		public boolean isSketchboardAvatar() {
			return sketchboardAvatar;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getTargetX() {
			return targetx;
		}

		public int getTargetY() {
			return targety;
		}

		public String getSelectedCids() {
			return selectedCids;
		}

		public String getClientIdentifier() {
			return clientIdentifier;
		}
	}

	public static class DiagramApplyOperation extends ApplyOperation {
		private List<IDiagramItemRO> items;
    private String guid;
		
		public DiagramApplyOperation(OTOperation operation, List<IDiagramItemRO> items, String guid) {
			super(operation);
			this.items = items;
      this.guid = guid;
		}
				
		public List<IDiagramItemRO> getItems() {
			return items;
		}

    public String getGuid() {
      return this.guid;
    }
	}

	public static class ApplyOperations {
    public String tabId;
		public List<DiagramApplyOperation> diagramOperations;
		public List<BoardUserApplyOperation> userOperations;
	}
	
	private static BoardUserApplyOperation createBoardUserApplyOperation(OTOperation operation, JSONArray jusers) {
		int length = jusers.size();
		if (length > 0) {
			// read only last
			JSONValue juser = jusers.get(length - 1);
			JSONObject juserobj = juser.isObject();
			if (juserobj != null) {
				JSONBoardUserParser parser = new JSONBoardUserParser(juserobj);
				return new BoardUserApplyOperation(operation, 
																					 parser.getEmail(),
																					 parser.getUsername(), 
																					 parser.getAvatarUrl(), 
																					 parser.isSketchboardAvatar(),
																					 parser.getX(), 
																					 parser.getY(), 
																					 parser.getTargetX(), 
																					 parser.getTargetY(), 
																					 parser.getSelectedCids(), 
																					 parser.getClientIdentifier());
			}
		}
		return null;
	}

	private static DiagramApplyOperation createDiagramApplyOperation(
    OTOperation operation,
    JSONArray jitems,
    String guid
  ) {
		List<IDiagramItemRO> items = new ArrayList<IDiagramItemRO>();
		for (int i = 0; i < jitems.size(); ++i) {
			if (jitems.get(i).isObject() != null) {
				JSONDiagramParser parser = new JSONDiagramParser(jitems.get(i).isObject(), true);
				if (parser.isDiagram() != null) {
					items.add(parser.isDiagram());
				} else if (parser.isComment() != null) {
					items.add(parser.isComment());
				}
			}
		}
		return new DiagramApplyOperation(operation, items, guid);
	}

  public static class BoardUpdate {
    public String name;
    public String originator;
    public String siteId;
    public List<DiagramApplyOperation> operations = new ArrayList<DiagramApplyOperation>();
    public int version;
    public String checksum;
    // Set<String> guids = new HashSet<String>();
  }

}