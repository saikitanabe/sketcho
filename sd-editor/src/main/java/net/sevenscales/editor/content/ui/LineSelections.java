package net.sevenscales.editor.content.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import net.sevenscales.editor.content.RelationShipType;

public class LineSelections extends Composite {

	private static LineSelectionsUiBinder uiBinder = GWT
			.create(LineSelectionsUiBinder.class);
		
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
	@UiHandler("dlineboth")
	public void onDLineBoth(ClickEvent event) {
		selectionHandler.itemSelected(RelationShipType.DIRECTED_BOTH);
	}
	@UiHandler("depline")
	public void onDepLine(ClickEvent event) {
		selectionHandler.itemSelected(RelationShipType.DEPENDANCY_DIRECTED);
	}
	@UiHandler("deplineboth")
	public void onDepLineBoth(ClickEvent event) {
		selectionHandler.itemSelected(RelationShipType.DEPENDANCY_DIRECTED_BOTH);
	}
	@UiHandler("dashed")
	public void onDashedLine(ClickEvent event) {
		selectionHandler.itemSelected(RelationShipType.DEPENDANCY);
	}
	@UiHandler("realize")
	public void onRealize(ClickEvent event) {
		selectionHandler.itemSelected(RelationShipType.REALIZE);
	}
	@UiHandler("iline")
	public void onILine(ClickEvent event) {
		selectionHandler.itemSelected(RelationShipType.INHERITANCE);
	}
	@UiHandler("aline")
	public void onALine(ClickEvent event) {
		selectionHandler.itemSelected(RelationShipType.AGGREGATION_DIRECTED);
	}
	@UiHandler("alinefilled")
	public void onALineFilled(ClickEvent event) {
		selectionHandler.itemSelected(RelationShipType.AGGREGATION_DIRECTED_FILLED);
	}
	@UiHandler("aggrboth")
	public void onAggregationBoth(ClickEvent event) {
		selectionHandler.itemSelected(RelationShipType.AGGREGATION);
	}
	@UiHandler("aggrbothfilled")
	public void onAggregationBothFilled(ClickEvent event) {
		selectionHandler.itemSelected(RelationShipType.AGGREGATION_FILLED);
	}
	@UiHandler("synchline")
	public void onReverse(ClickEvent event) {
		selectionHandler.itemSelected(RelationShipType.SYNCHRONIZED);
	}

}
