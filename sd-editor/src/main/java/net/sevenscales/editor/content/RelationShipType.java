package net.sevenscales.editor.content;

public enum RelationShipType {
	DIRECTED("->"),
	DIRECTED_BOTH("<->"),
	LINE("-"),
	DEPENDANCY("--"),
	DEPENDANCY_DIRECTED("-->"),
	DEPENDANCY_DIRECTED_BOTH("<-->"),
	INHERITANCE("-|>"),
	AGGREGATION_DIRECTED("<>->"),
	AGGREGATION_DIRECTED_FILLED("<*>->"),
	AGGREGATION("<>-"), 
	AGGREGATION_FILLED("<*>-"), 
	SYNCHRONIZED("-|*>"),
	REALIZE("--|>"),
	REVERSE("");
	
	private String value;

	RelationShipType(String value) {
		this.value = value;
	}
	
	public String getValue() {return value;}
}
