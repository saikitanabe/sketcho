package net.sevenscales.editor.uicomponents.uml;

import java.util.List;
import java.util.ArrayList;

import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.safehtml.shared.UriUtils;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.content.ui.UMLDiagramSelections.UMLDiagramType;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.content.utils.ContainerAttachHelpers;
import net.sevenscales.editor.content.utils.DiagramHelpers;
import net.sevenscales.editor.content.utils.IntegerHelpers;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.ImageShape;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.utils.UiUtils;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.ILine;
import net.sevenscales.editor.gfx.domain.IImage;
import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.editor.silver.SilverUtils;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementVerticalFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.HasTextElement;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.ShapeProperty;
import net.sevenscales.domain.IPathRO;
import net.sevenscales.domain.ExtensionDTO;
import net.sevenscales.domain.js.ImageInfo;


public class ImageElement extends AbstractDiagramItem implements SupportsRectangleShape {
	private static SLogger logger = SLogger.createLogger(ImageElement.class);

	public static double FREEHAND_STROKE_WIDTH = 2;
	public static int ACTIVITY_START_RADIUS = 10;
	public static int FREEHAND_TOUCH_WIDTH = 20;

	private ImageShape shape;
	private Point coords = new Point();
  private IRectangle background;
  private IGroup group;
  private IImage image;
  private Image imageLoader;
  private boolean loaded;

	public ImageElement(ISurfaceHandler surface, ImageShape newShape, Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
		super(editable, surface, backgroundColor, borderColor, textColor, item);

		this.shape = newShape;

    fetchSignedUrlIfMissing();

		group = IShapeFactory.Util.factory(editable).createGroup(surface.getElementLayer());
    // group.setAttribute("cursor", "default");

    createImage();

    background = IShapeFactory.Util.factory(editable).createRectangle(group);
    background.setFill(0, 0 , 0, 0); // transparent
    // background.setStroke("#363636");

		addEvents(background);
		
		addMouseDiagramHandler(this);

    shapes.add(image);

    resizeHelpers = ResizeHelpers.createResizeHelpers(surface);

    setReadOnly(!editable);

    setShape(shape.rectShape.left, shape.rectShape.top, shape.rectShape.width, shape.rectShape.height);

    setBorderColor(borderColor);

    super.constructorDone();
	}

  private void createImage() {
    image = IShapeFactory.Util.factory(true).createImage(group, 
      0, 
      0,
      // cannot center loader icon since it might not be visible for the user
      // when loading a big image which doesn't fit the screen
      // left, top should be always visible for the user
      // shape.rectShape.left + shape.rectShape.width / 2 - 10, 
      // shape.rectShape.top + shape.rectShape.height / 2 - 10,
      20,
      20,
      "/static/images/ajax-loader.gif");

    startLoader(shape.getUrl());
  }

  private void startLoader(final String url) {
    if (imageLoader == null && !"*".equals(url)) {
      // loader is not started for empty image, then this is already handled through
      // fetch
      imageLoader = new Image(UriUtils.fromString(url));
      // imageLoader.hide();
      imageLoader.addLoadHandler(new LoadHandler() {
        public void onLoad(LoadEvent event) {
          applyImageShape(url);
          imageLoader.removeFromParent();
        }
      });

      RootPanel.get().add(imageLoader);
    }
  }

  private void applyImageShape(String url) {
    image.setShape(0, 
                   0, 
                   getWidth(), 
                   getHeight(),
                   url);
    loaded = true;
  }

  private void fetchSignedUrlIfMissing() {
    if (isNotFetchedAwsUrl()) {
      fetchSignedUrl(this, shape.getFilename());
    }
  }

  private void updateImageInfo(String signedUrl) {
    startLoader(signedUrl);
  }

  private native void fetchSignedUrl(ImageElement me, String filename)/*-{
    // need to fetch directly and not through angular, since hander
    // needs to be this instance; this certainly will not work
    // on Confluence! :)
    if (typeof $wnd.backendProfileService !== 'undefined') {
      $wnd.backendProfileService.getSignedUrl({boardId: $wnd.currentBoard().boardId, filename: filename}).then(function(signedUrl) {
        if (signedUrl) {
          me.@net.sevenscales.editor.uicomponents.uml.ImageElement::updateImageInfo(Ljava/lang/String;)(signedUrl);
        }
      })
    }
  }-*/;

  public boolean isNotFetchedAwsUrl() {
    return "*".equals(shape.getUrl());
  }

	@Override
	public int getRelativeLeft() {
		return background.getX();
	}
	@Override
	public int getRelativeTop() {
		return background.getY();
	}
	@Override
	public int getWidth() {
		return background.getWidth();
	}
	@Override
	public int getHeight() {
		return background.getHeight();
	}

	public Point getDiffFromMouseDownLocation() {
		return new Point(diffFromMouseDownX, diffFromMouseDownY);
	}
	
	public void accept(ISurfaceHandler surface) {
	  super.accept(surface);
		surface.makeDraggable(this);
	}
	
  @Override
	public Diagram duplicate(boolean partOfMultiple) {
		return duplicate(surface, partOfMultiple);
	}
	
  @Override
	public Diagram duplicate(ISurfaceHandler surface, boolean partOfMultiple) {
		Point p = getCoords();
		return duplicate(surface, p.x + 20, p.y + 20);
	}
	
  @Override
  public Diagram duplicate(ISurfaceHandler surface, int x, int y) {
    ImageShape newShape = new ImageShape(x, y, getWidth() * factorX, getHeight() * factorY, shape.getUrl(), shape.getFilename());
    Diagram result = new ImageElement(surface, newShape, new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, new DiagramItemDTO());
    return result;
  }
	
  public boolean resize(Point diff) {
    return resize(getRelativeLeft(), getRelativeTop(), getWidth() + diff.x, getHeight() + diff.y);
  }

  protected boolean resize(int left, int top, int width, int height) {
    setShape(getRelativeLeft(), getRelativeTop(), width, height);
    dispatchAndRecalculateAnchorPositions();
    return true;
  }

	public void resizeEnd() {
	}

  public Info getInfo() {
    super.fillInfo(shape);
    return this.shape;
  }

	public void setShape(Info shape) {
	}

  public String getDefaultRelationship() {
    return "--";
  }
  
  @Override
  public UMLDiagramType getDiagramType() {
  	return UMLDiagramType.FREE_HAND;
  }
  
  @Override
  protected void doSetShape(int[] shape) {
    setShape(shape[0], shape[1], shape[2], shape[3]);
  }

  @Override
  public void setShape(int left, int top, int width, int height) {
  	if (width >= 10 && height >= 10) {
      if (loaded) {
        // cannot set real size until image is fully loaded
        // place holder is smaller
        image.setShape(0, 0, width, height);
      }

      group.setTransform(left, top);
      background.setShape(0, 0, width, height, 0);
			super.applyHelpersShape();
  	}
  }

  public void setHighlightColor(Color color) {
		background.setStroke(color);
  }
  
	@Override
	public IGroup getGroup() {
		return group;
	}

	@Override
  public boolean supportsTextEditing() {
  	return false;
  }
  
  @Override
  public int supportedMenuItems() {
    return ContextMenuItem.NO_MENU.getValue() | 
           ContextMenuItem.DUPLICATE.getValue() |
           ContextMenuItem.URL_LINK.getValue() | 
           ContextMenuItem.LAYERS.getValue() |
           ContextMenuItem.DELETE.getValue();
  }

  @Override
  public String getCustomData() {
  	if (shape.getFilename() != null && !"".equals(shape.getFilename())) {
  		// do not store/send to server AWS signed url; it is temporary
	  	return "*" + "," + shape.getFilename();
  	} else {
  		return shape.getUrl();
  	}
  }

  /**
  * Returns either absolute url or relative file name for aws images.
  */
  public String getImageUrl() {
  	if (shape.getFilename() != null && !"".equals(shape.getFilename())) {
  		return shape.getFilename();
  	}
  	return shape.getUrl();
  }

  public String getImageAbsoluteUrl() {
    return shape.getUrl();
  }

  /**
  * Returns runtime image url.
  */
  public String getUrl() {
    return shape.getUrl();
  }

}
