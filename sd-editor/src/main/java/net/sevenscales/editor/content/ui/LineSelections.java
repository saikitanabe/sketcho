package net.sevenscales.editor.content.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class LineSelections extends Composite {

	private static LineSelectionsUiBinder uiBinder = GWT
			.create(LineSelectionsUiBinder.class);
	
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
		REVERSE("");
		
		private String value;

		RelationShipType(String value) {
			this.value = value;
		}
		
		public String getValue() {return value;}
	}
	
	public interface SelectionHandler {
		void itemSelected(RelationShipType type);
	}

	interface LineSelectionsUiBinder extends UiBinder<Widget, LineSelections> {
	}

	private SelectionHandler selectionHandler;

	public LineSelections() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setSelectionHandler(SelectionHandler selectionHandler) {
		this.selectionHandler = selectionHandler;
	}

	@UiHandler("rline")
	public void onRLine(ClickEvent event) {
		selectionHandler.itemSelected(RelationShipType.LINE);
	}
	@UiHandler("dline")
	public void onDLine(ClickEvent event) {
		selectionHandler.itemSelected(RelationShipType.DIRECTED);
	}
	@UiHandler("depline")
	public void onDepLine(ClickEvent event) {
		selectionHandler.itemSelected(RelationShipType.DEPENDANCY_DIRECTED);
	}
	@UiHandler("dashed")
	public void onDashedLine(ClickEvent event) {
		selectionHandler.itemSelected(RelationShipType.DEPENDANCY);
	}
	@UiHandler("iline")
	public void onILine(ClickEvent event) {
		selectionHandler.itemSelected(RelationShipType.INHERITANCE);
	}
	@UiHandler("aline")
	public void onALine(ClickEvent event) {
		selectionHandler.itemSelected(RelationShipType.AGGREGATION_DIRECTED);
	}
	@UiHandler("aggrboth")
	public void onAggregationBoth(ClickEvent event) {
		selectionHandler.itemSelected(RelationShipType.AGGREGATION);
	}

	@UiHandler("reverseline")
	public void onReverse(ClickEvent event) {
		selectionHandler.itemSelected(RelationShipType.REVERSE);
	}

}
