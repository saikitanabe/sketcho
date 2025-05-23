package net.sevenscales.editor.diagram;

import java.util.Collection;
import java.util.List;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.api.ActionType;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.ShapeProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.Theme.ElementColorScheme;
import net.sevenscales.editor.content.ui.UMLDiagramType;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.Promise;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.gfx.base.GraphicsEvent;
// import net.sevenscales.editor.uicomponents.TextElementFormatUtil;

import com.google.gwt.core.client.JavaScriptObject;

public interface Diagram extends SourcesMouseDiagramEvents {
  public void applyTransform(int dx, int dy);
  void setTransform(int dx, int dy);
  public void resetTransform();
  public void saveLastTransform(int dx, int dy);
  int getTransformX();
  int getTransformY();
  public void accept(ISurfaceHandler surface);
  public void select();
  public boolean isSelected();
  public void unselect();
  public void addDiagramSelectionHandler(DiagramSelectionHandler selectionHandler);
  void removeFromParent();
  void removeFromParentWithoutConnections();
  boolean isRemoved();
  void release();
  /**
  * Not deleting whole element, but part of it, so it is actually a modify.
  * E.g. relationship point deletion is relationship modify not delete actual relationship.
  */
  boolean changeRemoveToModify();
  List<IShape> getElements();
  // text helper elements are not included in getElements
  List<List<IShape>> getTextElements();
  public void setHighlight(boolean highlight);
  public AnchorElement onAttachArea(Anchor anchor, int x, int y);
  
  /**
   * If needed to move item based on this diagram movement e.g. while
   * dragging this diagram some other dependant diagram needs to be
   * moved exactly same amount.
   * @return
   */
  public Point getDiffFromMouseDownLocation();
  Diagram getOwnerComponent();
  /**
  * Makes possible for subclasses to specialize returned owner component
  * related to certain action. E.g. comment element is dragged
  * and moved element that is fired to server is comment thread.
  * actionType would contain DRAGGING and comment element can 
  * specialize owner component to be parent comment thread.
  */
  Diagram getOwnerComponent(ActionType actionType);
  public void setOwnerComponent(DiagramProxy ownerComponent);
  /**
   * Provided to enable auto resize of diagram element. Element
   * either supports or doesn't auto resize.
   * @return
   */
  public boolean isAutoResize();
  public void setAutoResize(boolean autoresize);

  boolean isSketchiness();
  
  /**
  * Called for element that should show text editor.
  * Parent element can create + switch to child element.
  * relationship creates child element and that should be edited after that
  *
  */
  Diagram showEditorForDiagram(int screenX, int screenY);
  public String getText();
  // get text from certain area of diagram
  public String getText(int x, int y);
  void setText(String text);
  // set text to certain area of diagram
  void setText(String text, int x, int y);
  boolean isForceTextRendering();
  // If true forces diagram text rendering even if property editor is open
	void setForceTextRendering(boolean forceTextRendering);

  public boolean onResizeArea(int x, int y);
  public JavaScriptObject getResizeElement();
  public void resizeStart();
  
  /**
   * Returns true if diagram has been resized. Component should
   * have minimum size which prevents resizing.
   * 
   * @param diff
   * @return
   */
  public boolean resize(Point diff);
  public void resizeEnd();

  Diagram duplicate();

  /**
   * Duplicates this diagram on current surface.
   * @param partOfMultiple if duplicate is part of multiple element duplication at once
   * @return
   */
  public Diagram duplicate(boolean partOfMultiple);
  /**
   * Duplicates this diagram on provided surface.
   * @param surface
   * @param x coordinate x
   * @param y coordinate y
   * @return
   */
  public Diagram duplicate(ISurfaceHandler surface, boolean partOfMultiple);
  public Diagram duplicate(ISurfaceHandler surface, int x, int y);
  
  public void setDragState(DragState dragState);
  
  /**
   * External data can be stored and mapped with this diagram.
   * @param data
   */
  public void setDiagramItem(IDiagramItem data);
  /**
   * Returns data associated to this diagram.
   * @return
   */
  public IDiagramItem getDiagramItem();
  public String getClientId();
  // public String getId();
//  void copyAppliedDiagramItem(IDiagramItemRO apply);
//  IDiagramItem getAppliedDiagramItem();
  
  void setLink(String link);
  String getLink();
  boolean hasLink();

  void setGroupId(String groupId);
  
  /**
  * These are needed if element needs to do something differently
  * when export to svg is starged.
  */
  void toSvgStart();
  /**
  * This can be used to change state back to as it was.
  */
  void toSvgEnd();
  public Info getInfo();
  
  public void setReadOnly(boolean value);
  public void setVisible(boolean visible);
  public boolean isVisible();
//  public boolean okToBend();
//  public void bendStart(int x, int y);
//  public void bend(int dx, int dy);
  public void removeAnchor(Anchor anchor);
  void clearAnchorMap();
  public Collection<AnchorElement> getAnchors();
  public ISurfaceHandler getSurfaceHandler();
  public String getDefaultRelationship();
  
  public UMLDiagramType getDiagramType();
  boolean canSetBackgroundColor();
  boolean hasDefaultColors();
  void restoreDefaultColors();
	// public void setBackgroundColor(int red, int green, int blue, double opacity);
	void setBackgroundColor(Color color);
	String getBackgroundColor();
	String getBackgroundColorRgba();
	Color getBackgroundColorAsColor();
	public void setTextColor(int red, int green, int blue);
	void setTextColor(Color color);
  void setFontSize(Integer fontSize);
  void rotate(Integer rotateDeg, boolean save);
  Integer getRotate();
  void setTextAlign(ShapeProperty textAlign);
  boolean isTextAlignLeft();
  boolean isTextAlignCenter();
  boolean isTextAlignRight();
  Integer getFontSize();
  void setLineWeight(Integer lineWeight);
  Integer getLineWeight();
	Color getTextColor();
	public boolean onArea(int left, int top, int right, int bottom);
	// to check it point hovers this element; element can have magnetic area
	public boolean onArea(int x, int y);
	void moveToBack();
  void moveToFront();

  /**
  * Copies all fields to be shown in this diagram element.
  */
	void copyFrom(IDiagramItemRO diagramItem);
  void merge(IDiagramItemRO item);
  /**
  * Copies all fields except position that has been set elsewhere.
  * NOTE: could modify postion in here and duplicate method could be
  * simpler.
  */
  void duplicateFrom(IDiagramItemRO diagramItem);
  void updateTimestamp(Long createdAt, Long updatedAt);
	
	// each element needs to calculate own values based on these
	void setShape(int[] shape);
	int[] getShape();
  /**
  * Left without transformation.
  */
  int getRelativeLeft();
  /**
  * Top without transformation.
  */
  int getRelativeTop();
	int getLeft();
  int getLeftWithText();
	int getTop();
  int getWidth();
  int getWidthWithText();
	int getHeight();
  int getHeightWithText();
  /**
  * E.g. sequence element has different height on svg export vs. runtime when resizing.
  */
  int getSvgHeightWithText();
	int getCenterX();
	int getCenterY();
  void setWidth(int width);
	void setHeight(int height);
  // void setHeightAccordingToText();
	// special cases for highlight, in case some diagram items don't support
	// simple border color changes
	void setHighlightColor(Color color);
  /**
  * Highlights color if defined.
  * Otherwise restores border color according to diagram shape.
  */
	void restoreHighlighColor(Color color);

  void setHighlightBackgroundBorder(Color color);
  void clearHighlightBackgroundBorder();

	// void setBorderColor(String color);
	void setBorderColor(Color color);
  IGroup getGroup();
	IGroup getSubgroup();
	IGroup getTextGroup();
	
	public interface SizeChangedHandler {
		void onSizeChanged(Diagram diagram, int width, int height);
	}
	void setSizeChangedHandlerByText(SizeChangedHandler handler);
	void hideText();
	void showText();
	String getTextAreaBackgroundColor();
	// int getTextAreaLeft();
	// int getTextAreaTop();
	// Promise getTextAreaSize();
	Promise getTextSize();
	// int getTextAreaHeight();
	String getTextAreaAlign();
	boolean supportsOnlyTextareaDynamicHeight();
	Color getBorderColor();
	boolean supportsTextEditing();
  boolean supportsAlignHighlight();
  /**
  * E.g. use case element doesn't support initial connection drag to be centered.
  * It has fixed points to anchor.
  */
  boolean supportsModifyToCenter();
	int supportedMenuItems();
  boolean supportsMenu(ContextMenuItem menuItem);
	// Promise getMeasurementAreaSize();
	String getCustomData();
	void parseCustomData(String customData);
	AnchorElement anchorWith(Anchor anchor, int x, int y);
	void hideConnectionHelpers();
	Color getDefaultBackgroundColor(ElementColorScheme colorScheme);
	Color getDefaultBorderColor(ElementColorScheme colorScheme);
	Color getDefaultTextColor(ElementColorScheme colorScheme);
	boolean usesSchemeDefaultColors(ElementColorScheme colorScheme);
  boolean usesSchemeDefaultTextColor(ElementColorScheme colorScheme);
  boolean usesSchemeDefaultBorderColor(ElementColorScheme colorScheme);
  boolean usesSchemeDefaultBackgroundColor(ElementColorScheme colorScheme);
	/**
	 * To support theme switching so that text element is visible against background.
	 * @return
	 */
	boolean isTextElementBackgroundTransparent();
	
	boolean isTextColorAccordingToBackgroundColor();

  boolean isMarkdownEditor();

  /**
  * Support for cumulative transformations, since Safari doesn't support applyTransformation.
  */
  void snapshotTransformations();
  int getSnaphsotTransformX();
  int getSnaphsotTransformY();

  /**
  * Support for diagram text editing, and understanding edit states.
  */
  void editingEnded(boolean modified);

  // curve arrow support
  boolean isSequenceElement();

  /**
  * Meant for composite elements like comment thread with comment elements.
  * @return null if not a composite element
  */
  List<? extends Diagram> getChildElements();

  void attachedRelationship(AnchorElement anchorElement);
  void applyThemeBorderColor();
  void applyAnnotationColors();
  boolean isAnnotation();
  boolean isResolved();
  void annotate();
  void unannotate();

  void onMouseDown(GraphicsEvent event, int keys);
  void onMouseMove(GraphicsEvent event);
  void onMouseUp(GraphicsEvent event, int keys);
  void onMouseLeave(GraphicsEvent event);
  void onMouseEnter(GraphicsEvent event);
  void onTouchStart(GraphicsEvent event);
  void onTouchMove(GraphicsEvent event);
  void onTouchEnd(GraphicsEvent event);

  // menu support
  void setDuplicateMultiplySize(int factorX, int factorY);

}
