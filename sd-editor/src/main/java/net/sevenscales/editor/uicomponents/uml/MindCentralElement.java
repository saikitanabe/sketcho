package net.sevenscales.editor.uicomponents.uml;


import net.sevenscales.editor.api.SurfaceHandler;
import net.sevenscales.editor.content.ui.UMLDiagramSelections.UMLDiagramType;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.MindCentralShape;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;

public class MindCentralElement extends ActivityElement implements SupportsRectangleShape {
	public MindCentralElement(SurfaceHandler surface, MindCentralShape newShape, String text, 
			Color backgroundColor, Color borderColor, Color textColor, boolean editable) {
		super(surface, newShape, text, backgroundColor, borderColor, textColor, editable);
		
		textUtil.setMarginTop(18);
		textUtil.setMargin(80);
		textUtil.setMarginBottom(27);
		textUtil.setFontSize(15);
		
		// set text again to format shape correctly
		textUtil.setText(text, editable);
		
    super.constructorDone();
	}
	
	protected ResizeHelpers createResizeHelpers() {
		return ResizeHelpers.createResizeHelpers(surface);
	}
	
	@Override
	public int getResizeIndentX() {
		return 3;
	}
	
	protected double getStrokeWidth() {
		return 3.0;
	}

  public Diagram duplicate(SurfaceHandler surface, int x, int y) {
  	MindCentralShape newShape = new MindCentralShape(x, y, getWidth(), getHeight());
    Diagram result = createDiagram(surface, newShape, getText(), getEditable());
    return result;
  }

  protected Diagram createDiagram(SurfaceHandler surface, MindCentralShape newShape,
      String text, boolean editable) {
    return new MindCentralElement(surface, newShape, text, 
    		new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable);
  }
	
  public String getDefaultRelationship() {
    return "-";
  }
  
  @Override
  public UMLDiagramType getDiagramType() {
  	return UMLDiagramType.MIND_CENTRAL_TOPIC;
  }
  
  @Override
  public String getTextAreaAlign() {
  	return "center";
  }

}
