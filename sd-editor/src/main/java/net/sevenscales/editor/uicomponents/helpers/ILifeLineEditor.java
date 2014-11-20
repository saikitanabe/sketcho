package net.sevenscales.editor.uicomponents.helpers;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.ISequenceElement;
import net.sevenscales.editor.diagram.shape.CircleShape;

public interface ILifeLineEditor extends IGlobalElement {
  void setShape(CircleShape circleShape);
  void applyTransform(int dx, int dy);
  void show(ISequenceElement parent);
  void setShape(ISequenceElement parent);
  void hide(Diagram candidate);
  void forceHide();
  void saveLastTransform(int dx, int dy);
  Diagram getStartSelection();
}
