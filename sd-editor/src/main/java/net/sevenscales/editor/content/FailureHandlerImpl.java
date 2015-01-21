package net.sevenscales.editor.content;

import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.dom.client.Element;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.domain.IDiagramItemRO;


class FailureHandlerImpl extends Composite {
	private static FailureHandlerImplUiBinder uiBinder = GWT.create(FailureHandlerImplUiBinder.class);

	interface FailureHandlerImplUiBinder extends UiBinder<Widget, FailureHandlerImpl> {
	}

	@UiField Element close;
	@UiField VerticalPanel items;

	private ISurfaceHandler surface;

	FailureHandlerImpl(List<IDiagramItemRO> failed, ISurfaceHandler surface) {
		this.surface = surface;
		initWidget(uiBinder.createAndBindUi(this));
		RootPanel.get().add(this);
		handleFailed(failed);
		handleClose(this, close);
	}

	private native void handleClose(FailureHandlerImpl me, Element e)/*-{
		if (typeof $wnd.Hammer != 'undefined') {
			$wnd.Hammer(e, {preventDefault: true}).on('tap', function() {
				me.@net.sevenscales.editor.content.FailureHandlerImpl::onClose()();
			})
		}
	}-*/;

	private void onClose() {
		RootPanel.get().remove(this);
	}

	private void handleFailed(List<IDiagramItemRO> failed) {
		for (IDiagramItemRO item : failed) {
			items.add(new FailureItem(item, surface));
		}
	}
}