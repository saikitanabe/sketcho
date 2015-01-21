package net.sevenscales.editor.content;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.dom.client.Element;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.BoardRemoveFailureItemsEvent;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.utils.SLogger;


class FailureItem extends Composite {
	private static SLogger logger = SLogger.createLogger(FailureItem.class);
	static {
		SLogger.addFilter(FailureItem.class);
	}

	private static FailureItemUiBinder uiBinder = GWT.create(FailureItemUiBinder.class);

	interface FailureItemUiBinder extends UiBinder<Widget, FailureItem> {
	}

	@UiField Element text;
	@UiField Element type;
	@UiField Element delete;

	private IDiagramItemRO item;
	private ISurfaceHandler surface;
	private boolean deleted;

	FailureItem(IDiagramItemRO item, ISurfaceHandler surface) {
		this.surface = surface;
		this.item = item;
		initWidget(uiBinder.createAndBindUi(this));
		text.setInnerText(item.getText());
		type.setInnerText(item.getType());

		handleDelete(this, delete);
	}

	private native void handleDelete(FailureItem me, Element e)/*-{
		if (typeof $wnd.Hammer != 'undefined') {
			$wnd.Hammer(e, {preventDefault: true}).on('tap', function() {
				me.@net.sevenscales.editor.content.FailureItem::onDelete()();
			})
		}
	}-*/;

	private void onDelete() {
		if (!deleted) {
			logger.debug("onDelete...");
	    surface.getEditorContext().getEventBus().fireEvent(new BoardRemoveFailureItemsEvent(item));
	    delete.setClassName("btn btn-primary disabled");
	    delete.setInnerText("Deleted");
	    deleted = true;
	  }
	}
}
