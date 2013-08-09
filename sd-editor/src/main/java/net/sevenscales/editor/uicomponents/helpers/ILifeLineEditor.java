package net.sevenscales.editor.uicomponents.helpers;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.CircleShape;
import net.sevenscales.editor.uicomponents.uml.SequenceElement;

public interface ILifeLineEditor extends IGlobalElement {
  void setShape(CircleShape circleShape);
  void applyTransform(int dx, int dy);
  void show(SequenceElement parent);
  void setShape(SequenceElement parent);
  void hide(Diagram candidate);
  void forceHide();
  void saveLastTransform(int dx, int dy);
  Diagram getStartSelection();
}
