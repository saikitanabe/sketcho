package net.sevenscales.editor.api.ot;

public enum OTOperation {
	MODIFY("modify"), INSERT("insert"), DELETE("delete"),
	UNDO("undo"), REDO("redo"),
	UNDO_DELETE("undo.delete"), REDO_DELETE("redo.delete"), 
	UNDO_INSERT("undo.insert"), REDO_INSERT("redo.insert"),
	UNDO_MODIFY("undo.modify"), REDO_MODIFY("redo.modify"), 
	USER_JOIN("user.join"), USER_LEFT("user.left"), USER_MOVE("user.move"),
	REOPEN_COMMENT_THREAD("reopen");

	private String value;
	private OTOperation(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	
	@Override
  public String toString() {
		return this.getValue();
  }
	
	public static OTOperation getEnum(String operation) {
		if (operation == null) {
      throw new IllegalArgumentException();
		}
		
		for (OTOperation v : values()) {
      if (operation.equalsIgnoreCase(v.getValue())) return v;
		}
		throw new IllegalArgumentException();
	}
}
