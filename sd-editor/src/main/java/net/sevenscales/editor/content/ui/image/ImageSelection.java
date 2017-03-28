package net.sevenscales.editor.content.ui.image;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
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
import net.sevenscales.editor.api.event.CreateElementEvent;
import net.sevenscales.domain.js.ImageInfo;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.content.ui.UMLDiagramType;


public class ImageSelection extends Composite {
	private static final SLogger logger = SLogger.createLogger(ImageSelection.class);
	private static final int IMAGES_PER_ROW = 6;

	static {
		logger.addFilter(ImageSelection.class);
	}
	
	interface ImageSelectionUiBinder extends UiBinder<Widget, ImageSelection> {
	}
	private static ImageSelectionUiBinder uiBinder = GWT.create(ImageSelectionUiBinder.class);

	@UiField VerticalPanel images;
	@UiField Widget emptyMessage;

	private ISurfaceHandler surface;
	private DiagramSelectionHandler parent;
	private HorizontalPanel currentRow;
	private int imageCount;
	private double offset = 0;
	private double max = 20;

	public ImageSelection(ISurfaceHandler surface, DiagramSelectionHandler parent) {
		this.surface = surface;
		this.parent = parent;

		init(this);
		initWidget(uiBinder.createAndBindUi(this));
		loadThumbnails();

		emptyMessage.setVisible(false);

  	surface.getEditorContext().getEventBus().addHandler(ImageAddedEvent.TYPE, new ImageAddedEventHandler() {
  		public void on(ImageAddedEvent event) {
  			addThumbnail(event.getImageInfo());
		  	showOrHideEmptyMessage();
  		}
  	});

  	parent.addScrollHandler(new DiagramSelectionHandler.WhenScrolledHandler() {
			public void whenScrolled() {
				offset += max;
				loadThumbnails();
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

		$wnd.gwtLoadedImagesReset = function(images) {
			imageSelection.@net.sevenscales.editor.content.ui.image.ImageSelection::gwtLoadedImagesReset(Lcom/google/gwt/core/client/JsArray;)(images);
		}

		$wnd.gwtReceiveFileInfo = function(fileinfo) {
			imageSelection.@net.sevenscales.editor.content.ui.image.ImageSelection::gwtReceiveFileInfo(Lnet/sevenscales/domain/js/ImageInfo;)(fileinfo);
		}

	}-*/;

	public void loadImages() {
		offset = imageCount;
		loadThumbnails();
	}

	private void loadThumbnails() {
		_ngLoadThumbnails(offset, max);
	}
	private native void _ngLoadThumbnails(double offset, double max)/*-{
		// images are loaded through angular app code
		// that loads images from server
		$wnd.ngLoadThumbnails(offset, max);
	}-*/;

  private void gwtLoadedImagesReset(JsArray<ImageInfo> images) {
  	clear();
  	gwtLoadedImages(images);
  	// add images to offset, so loading next time from this position
  	offset = images.length();
	}
  private void gwtLoadedImages(JsArray<ImageInfo> images) {
  	logger.debug("images json");
  	for (int i = 0; i < images.length(); ++i) {
  		ImageInfo img = images.get(i);
  		addThumbnail(img);
  	}

  	showOrHideEmptyMessage();
  }

  private void showOrHideEmptyMessage() {
  	if (imageCount == 0) {
			emptyMessage.setVisible(true);
  	} else {
  		emptyMessage.setVisible(false);
  	}
  }

  private void clear() {
  	offset = 0;
  	imageCount = 0;
  	images.clear();
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
  	parent.hidePopup();
  	copyLibraryFileToBoard(imageInfo.getHash());
  }

  private native void copyLibraryFileToBoard(String hash)/*-{
  	$wnd.ngCopyLibraryFileToBoard(hash);
  }-*/;

  private void gwtReceiveFileInfo(ImageInfo imageInfo) {
		surface.getEditorContext().getEventBus().fireEvent(new CreateElementEvent(UMLDiagramType.IMAGE.getElementType().getValue(), imageInfo, 0, 0));
  }

}