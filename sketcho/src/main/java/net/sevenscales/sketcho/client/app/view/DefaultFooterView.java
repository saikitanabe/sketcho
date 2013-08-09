package net.sevenscales.sketcho.client.app.view;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.IController;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.plugin.constants.TileId;

import com.google.gwt.user.client.ui.HorizontalPanel;

public class DefaultFooterView extends View {
	private HorizontalPanel footer;

	public DefaultFooterView(IController controller) {
		super(controller);
		// construct footer
		footer = new HorizontalPanel();
//		footer.add(ActionFactory.
//				createLinkAction("Home", ActionId.PROJECTS, controller));
	}

	public void activate(ITilesEngine tilesEngine, 
						 DynamicParams params, IContributor contributor) {
		tilesEngine.setTile(TileId.FOOTER, footer);
	}
}
