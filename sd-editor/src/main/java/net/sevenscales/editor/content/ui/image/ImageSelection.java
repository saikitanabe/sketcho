package net.sevenscales.editor.content.ui.image;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.safehtml.shared.UriUtils;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.FastElementButton;
import net.sevenscales.editor.content.ui.DiagramSelectionHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.api.event.ImageAddedEvent;
import net.sevenscales.editor.api.event.ImageAddedEventHandler;
import net.sevenscales.domain.js.ImageInfo;
import net.sevenscales.domain.utils.SLogger;


public class ImageSelection extends Composite {
	private static final SLogger logger = SLogger.createLogger(ImageSelection.class);

	static {
		logger.addFilter(ImageSelection.class);
	}
	
	interface ImageSelectionUiBinder extends UiBinder<Widget, ImageSelection> {
	}
	private static ImageSelectionUiBinder uiBinder = GWT.create(ImageSelectionUiBinder.class);

	@UiField VerticalPanel images;

	private ISurfaceHandler surface;
	private DiagramSelectionHandler parent;

	public ImageSelection(ISurfaceHandler surface, DiagramSelectionHandler parent) {
		this.surface = surface;
		this.parent = parent;

		init(this);
		initWidget(uiBinder.createAndBindUi(this));
		loadThumbnails();

  	surface.getEditorContext().getEventBus().addHandler(ImageAddedEvent.TYPE, new ImageAddedEventHandler() {
  		public void on(ImageAddedEvent event) {
  			addThumbnail(event.getImageInfo());
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

	private native void init(ImageSelection imageSelection)/*-{
		$wnd.gwtLoadedImages = function(images) {
			imageSelection.@net.sevenscales.editor.content.ui.image.ImageSelection::gwtLoadedImages(Lcom/google/gwt/core/client/JsArray;)(images);
		}
	}-*/;

	private void loadThumbnails() {
		_ngLoadThumbnails();
	}
	private native void _ngLoadThumbnails()/*-{
		// images are loaded through angular app code
		// that loads images from server
		$wnd.ngLoadThumbnails();
	}-*/;

  private void gwtLoadedImages(JsArray<ImageInfo> images) {
  	logger.debug("images json");
  	for (int i = 0; i < images.length(); ++i) {
  		ImageInfo img = images.get(i);
  		addThumbnail(img);
  	}
  }

  private void addThumbnail(ImageInfo imageInfo) {
  	images.add(new Image(UriUtils.fromString(imageInfo.getThumbnailUrl())));
  }

	// private void moveToFront() {
	// 	surface.moveSelectedToFront();
	// 	parent.hidePopup();
	// }

	// private void moveToBack() {
	// 	surface.moveSelectedToBack();
	// 	parent.hidePopup();
	// }

	// private void moveToBackward() {
	// 	surface.moveSelectedToBackward();
	// 	// let's not hide menu if user wants to click more than once
	// }

	// private void moveToForward() {
	// 	surface.moveSelectedToForward();
	// 	// let's not hide menu if user wants to click more than once
	// }

	// private void stopEvent(ClickEvent event) {
	// 	event.stopPropagation();
	// 	event.preventDefault();
	// }

}