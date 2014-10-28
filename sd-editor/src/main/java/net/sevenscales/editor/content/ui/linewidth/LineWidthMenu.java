package net.sevenscales.editor.content.ui.linewidth;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.ui.PopupPanel;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.uicomponents.uml.Relationship2;
import net.sevenscales.editor.api.impl.FastElementButton;


public class LineWidthMenu extends Composite {
	// private static final SLogger logger = SLogger.createLogger(LineWidthMenu.class);
	
	interface LineWidthMenuUiBinder extends UiBinder<Widget, LineWidthMenu> {
	}
	private static LineWidthMenuUiBinder uiBinder = GWT.create(LineWidthMenuUiBinder.class);

	@UiField AnchorElement weight1;
	@UiField AnchorElement weight2;
	@UiField AnchorElement weight3;
	@UiField AnchorElement weight4;
	@UiField AnchorElement weight5;
	// @UiField AnchorElement moveForward;

	private ISurfaceHandler surface;
	private PopupPanel parent;

	public LineWidthMenu(ISurfaceHandler surface, PopupPanel parent) {
		this.surface = surface;
		this.parent = parent;

		initWidget(uiBinder.createAndBindUi(this));

		new FastElementButton(weight1).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				weight1();
			}
		});

		new FastElementButton(weight2).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				weight2();
			}
		});

		new FastElementButton(weight3).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				weight3();
			}
		});

		new FastElementButton(weight4).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				weight4();
			}
		});
		new FastElementButton(weight5).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				weight5();
			}
		});
		// new FastElementButton(moveForward).addClickHandler(new ClickHandler() {
		// 	@Override
		// 	public void onClick(ClickEvent event) {
		// 		stopEvent(event);
		// 		moveToForward();
		// 	}
		// });

	}

	private void setWeight(int weight) {
		for (Diagram d : surface.getSelectionHandler().getSelectedItems()) {
			if (d instanceof Relationship2) {
				Relationship2 r = (Relationship2) d;
				r.setWeight(weight);
			}
		}
	}

	private void weight1() {
		setWeight(1);
		parent.hide();
	}

	private void weight2() {
		setWeight(3);
		parent.hide();
	}

	private void weight3() {
		setWeight(6);
		parent.hide();
	}

	private void weight4() {
		setWeight(12);
		parent.hide();
	}

	private void weight5() {
		setWeight(24);
		parent.hide();
	}

	private void moveToForward() {
		// surface.moveSelectedToForward();
		// let's not hide menu if user wants to click more than once
	}

	private void stopEvent(ClickEvent event) {
		event.stopPropagation();
		event.preventDefault();
	}

}