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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.safehtml.shared.UriUtils;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.FastElementButton;
import net.sevenscales.editor.content.ui.DiagramSelectionHandler;
import net.sevenscales.editor.content.utils.DiagramElementFactory;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.api.event.ImageAddedEvent;
import net.sevenscales.editor.api.event.ImageAddedEventHandler;
import net.sevenscales.domain.js.ImageInfo;
import net.sevenscales.domain.utils.SLogger;


public class ImageSelection extends Composite {
	private static final SLogger logger = SLogger.createLogger(ImageSelection.class);
	private static final int IMAGES_PER_ROW = 5;

	static {
		logger.addFilter(ImageSelection.class);
	}
	
	interface ImageSelectionUiBinder extends UiBinder<Widget, ImageSelection> {
	}
	private static ImageSelectionUiBinder uiBinder = GWT.create(ImageSelectionUiBinder.class);

	@UiField VerticalPanel images;

	private ISurfaceHandler surface;
	private DiagramSelectionHandler parent;
	private HorizontalPanel currentRow;
	private int imageCount;

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

		$wnd.gwtReceiveFileInfo = function(fileinfo) {
			imageSelection.@net.sevenscales.editor.content.ui.image.ImageSelection::gwtReceiveFileInfo(Lnet/sevenscales/domain/js/ImageInfo;)(fileinfo);
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

  private void addThumbnail(final ImageInfo imageInfo) {
  	++imageCount;
  	if (imageCount % IMAGES_PER_ROW == 1) {
  		// 4 images/row, if modulus (jakojaannos) is 1 then it would be first new on a row
	  	createNewRow();
  	}

  	final Image img = new Image(UriUtils.fromString(imageInfo.getThumbnailUrl()));
		img.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// stopEvent(event);
				addImage(imageInfo);
			}
		});

  	img.setStyleName("img-select-thumb");
  	currentRow.add(img);
  }

  private void createNewRow() {
  	currentRow = new HorizontalPanel();
  	images.add(currentRow);
  }

  private void addImage(ImageInfo imageInfo) {
  	logger.debug("addImage... {}", imageInfo.getHash());
  	getSignedUrl(imageInfo.getHash());
  }

  private native void getSignedUrl(String hash)/*-{
  	$wnd.ngGetFileInfo(hash);
  }-*/;

  private void gwtReceiveFileInfo(ImageInfo imageInfo) {
  	Diagram d = DiagramElementFactory.createImageElement(surface, imageInfo.getFilename(), imageInfo.getUrl(), 0, 0, imageInfo.getWidth(), imageInfo.getHeight());
		surface.addAsSelected(d, true);
  }

}