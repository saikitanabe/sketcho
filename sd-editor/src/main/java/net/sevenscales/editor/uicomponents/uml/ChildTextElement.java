package net.sevenscales.editor.uicomponents.uml;

import com.google.gwt.core.client.Scheduler;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.TextShape;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.HasTextElement;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.gfx.domain.IParentElement;
import net.sevenscales.editor.gfx.domain.IChildElement;


public class ChildTextElement extends TextElement implements IChildElement {
	private IParentElement parent;
	private double rleft;
	private double rtop;
	private double fixedLeft;
	private double fixedTop;
  private net.sevenscales.editor.gfx.domain.ICircle tempC1;


	public ChildTextElement(ISurfaceHandler surface, TextShape newShape,
			Color backgroundColor, Color borderColor, Color textColor, String text, boolean editable, IDiagramItemRO item, IParentElement parent) {
		super(surface, newShape, backgroundColor, borderColor, textColor, text, editable, item);
		this.parent = parent;
		parent.addChild(this);

		tempC1 = net.sevenscales.editor.gfx.domain.IShapeFactory.Util.factory(editable).createCircle(getGroup());

		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				updateFixedDistance();
			}
		});
    super.constructorDone();
	}

	@Override
	public Info getInfo() {
		// TODO add parent client id
		return super.getInfo();
	}

	@Override
	public Diagram getParent() {
		return parent.asDiagram();
	}

	@Override
	public Diagram asDiagram() {
		return this;
	}

	@Override
  public void saveRelativeDistance(double rleft, double rtop) {
  	this.rleft = rleft;
  	this.rtop = rtop;
  	// cachedWidth = getWidth();
  	// cachedHeight = getHei
  }
	@Override
  public double getRelativeDistanceLeft() {
  	return rleft;
  }
	@Override
  public double getRelativeDistanceTop() {
  	return rtop;
  }
	@Override
  public void setPosition(double left, double top) {
    tempC1.setShape(left, top, 5);
    tempC1.setStroke(150, 150, 150, 1);
    tempC1.setFill(150, 150, 150, 1);
    setShape(new int[]{(int) left, (int) top, getWidth(), getHeight()});
  	// // setShape((int) left, (int) top, getWidth(), getTop());
  	// select();
  }

  @Override
  public double getFixedLeft() {
  	return fixedLeft;
  }
  @Override
  public double getFixedTop() {
  	return fixedTop;
  }

  @Override
	public void saveLastTransform(int dx, int dy) {
		super.saveLastTransform(dx, dy);
		updateFixedDistance();
	}

  // @Override
  public void updateFixedDistance() {
  	double midx = parent.asDiagram().getLeft() + parent.asDiagram().getWidth() / 2.0;
		fixedLeft = getLeft() - midx;
		double midy = parent.asDiagram().getTop() + parent.asDiagram().getHeight() / 2.0;
		fixedTop =  getTop() - midy;
  }

}
