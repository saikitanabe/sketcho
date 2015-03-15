package net.sevenscales.editor.content.ui;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.uibinder.client.UiField;

import net.sevenscales.editor.content.RelationShipType;

public class LineSelections extends Composite {

	private static LineSelectionsUiBinder uiBinder = GWT
			.create(LineSelectionsUiBinder.class);
		
	public interface SelectionHandler {
		void itemSelected(RelationShipType type);
	}

	interface LineSelectionsUiBinder extends UiBinder<Widget, LineSelections> {
	}

	interface IParent {
		boolean isShowing();
	}

	private SelectionHandler selectionHandler;
	private int currentIndex = 0;
	@UiField SimplePanel dline;
	@UiField SimplePanel dlineboth;
	@UiField SimplePanel iline;
	@UiField SimplePanel synchline;
	@UiField SimplePanel rline;
	@UiField SimplePanel depline;
	@UiField SimplePanel deplineboth;
	@UiField SimplePanel dashed;
	@UiField SimplePanel realize;
	@UiField SimplePanel aline;
	@UiField SimplePanel alinefilled;
	@UiField SimplePanel aggrboth;
	@UiField SimplePanel aggrbothfilled;
	private IParent parent;
	private Line[] lines;

	private static class Line {
		RelationShipType type;
		SimplePanel div;

		Line(RelationShipType type, SimplePanel div) {
			this.type = type;
			this.div = div;
		}
	}

	public LineSelections(IParent parent) {
		this.parent = parent;
		initWidget(uiBinder.createAndBindUi(this));

		lines = new Line[] {
			new Line(RelationShipType.DIRECTED, dline),
			new Line(RelationShipType.DEPENDANCY_DIRECTED, depline),
			new Line(RelationShipType.AGGREGATION_DIRECTED, aline),
			new Line(RelationShipType.DIRECTED_BOTH, dlineboth),
			new Line(RelationShipType.DEPENDANCY_DIRECTED_BOTH, deplineboth),
			new Line(RelationShipType.AGGREGATION_DIRECTED_FILLED, alinefilled),
			new Line(RelationShipType.INHERITANCE, iline),
			new Line(RelationShipType.DEPENDANCY, dashed),
			new Line(RelationShipType.AGGREGATION, aggrboth),
			new Line(RelationShipType.SYNCHRONIZED, synchline),
			new Line(RelationShipType.REALIZE, realize),
			new Line(RelationShipType.AGGREGATION_FILLED, aggrbothfilled),
			new Line(RelationShipType.LINE, rline)
		};

		// get arrow stream only when shown
		// could cancel subscription when hidden
		init(this);

		currentSelection();
	}

	private void currentSelection() {
		if (currentIndex >= 0 && currentIndex < lines.length) {
			lines[currentIndex].div.addStyleName("line-selected");
		}
	}

	private boolean clearSelection(int change) {
		if (currentIndex + change >= 0 && currentIndex + change < lines.length) {
			lines[currentIndex].div.removeStyleName("line-selected");
			return true;
		}
		return false;
	}

	private void selectLine() {
		if (currentIndex >= 0 && currentIndex < lines.length) {
			selectionHandler.itemSelected(lines[currentIndex].type);
		}
	}

	public void show() {
		// TODO subscribe stream
		currentIndex = 0;
	}

	public void hide() {
		// TODO cancel stream
	}

	private boolean isShowing() {
		return parent.isShowing();
	}

	private native void init(LineSelections me)/*-{
		$wnd.globalStreams.arrowsStream.onValue(function(keyCode) {
			me.@net.sevenscales.editor.content.ui.LineSelections::arrowKey(I)(keyCode)
		})

		$wnd.spaceKeyManager.stream.onValue(function() {
			if (me.@net.sevenscales.editor.content.ui.LineSelections::isShowing()()) {
				$wnd.spaceKeyManager.handled()
				me.@net.sevenscales.editor.content.ui.LineSelections::selectLine()()
			}
		})
	}-*/;

	private void arrowKey(int keyCode) {
		switch (keyCode) {
			case KeyCodes.KEY_LEFT:
				if (clearSelection(-1)) {
					--currentIndex;
				}
				break;
			case KeyCodes.KEY_UP:
				if (clearSelection(-3)) {
					currentIndex -= 3;
				}
				break;
			case KeyCodes.KEY_RIGHT:
				if (clearSelection(1)) {
					++currentIndex;
				}
				break;
			case KeyCodes.KEY_DOWN:
				if (clearSelection(3)) {
					currentIndex += 3;
				}
				break;
		}
		currentSelection();
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
