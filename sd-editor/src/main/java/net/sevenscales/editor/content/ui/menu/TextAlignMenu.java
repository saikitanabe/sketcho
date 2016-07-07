package net.sevenscales.editor.content.ui.menu;

import java.util.List;
import java.util.ArrayList;

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
import net.sevenscales.editor.api.event.PotentialOnChangedEvent;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.ShapeProperty;
import net.sevenscales.editor.uicomponents.uml.Relationship2;
import net.sevenscales.editor.uicomponents.uml.GenericFreehandElement;
import net.sevenscales.editor.api.impl.FastElementButton;
import net.sevenscales.editor.content.ui.ContextMenuItem;


public class TextAlignMenu extends Composite {
	// private static final SLogger logger = SLogger.createLogger(TextAlignMenu.class);
	
	interface TextAlignMenuUiBinder extends UiBinder<Widget, TextAlignMenu> {
	}
	private static TextAlignMenuUiBinder uiBinder = GWT.create(TextAlignMenuUiBinder.class);

	@UiField AnchorElement alignLeft;
	@UiField AnchorElement alignCenter;
	@UiField AnchorElement alignRight;

	private ISurfaceHandler surface;
	private PopupPanel parent;

	public TextAlignMenu(ISurfaceHandler surface, PopupPanel parent) {
		this.surface = surface;
		this.parent = parent;

		initWidget(uiBinder.createAndBindUi(this));

		new FastElementButton(alignLeft).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				textAlignLeft();
			}
		});

		new FastElementButton(alignCenter).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				textAlignCenter();
			}
		});

		new FastElementButton(alignRight).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				textAlignRight();
			}
		});

	}

	private void setTextAlign(ShapeProperty shapeProperty) {
		List<Diagram> changed = new ArrayList<Diagram>();
		for (Diagram d : surface.getSelectionHandler().getSelectedItems()) {
			boolean set = false;

			if (ContextMenuItem.supported(d.supportedMenuItems(), ContextMenuItem.TEXT_ALIGN)) {
				d.setTextAlign(shapeProperty);
				set = true;
			}

			if (set) {
				changed.add(d);
			}
		}

		if (changed.size() > 0) {
			surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(changed));
		// } else {
			// not changing anything just notify selected weight
			// notifyLineWeight(weight);
		}
	}

	// private native void notifyLineWeight(int weight)/*-{
	// 	$wnd.globalStreams.updatedFreehandLineWeightStream.push(weight)
	// }-*/;

	private void textAlignLeft() {
		setTextAlign(null);
		parent.hide();
	}

	private void textAlignCenter() {
		setTextAlign(ShapeProperty.TXT_ALIGN_CENTER);
		parent.hide();
	}

	private void textAlignRight() {
		setTextAlign(ShapeProperty.TXT_ALIGN_RIGHT);
		parent.hide();
	}

	// private void weight3() {
	// 	setTextAlign(3);
	// 	parent.hide();
	// }

	// private void weight4() {
	// 	setTextAlign(6);
	// 	parent.hide();
	// }

	// private void weight5() {
	// 	setTextAlign(12);
	// 	parent.hide();
	// }

	// private void weight6() {
	// 	setTextAlign(24);
	// 	parent.hide();
	// }

	// private void moveToForward() {
	// 	// surface.moveSelectedToForward();
	// 	// let's not hide menu if user wants to click more than once
	// }

	private void stopEvent(ClickEvent event) {
		event.stopPropagation();
		event.preventDefault();
	}

}
