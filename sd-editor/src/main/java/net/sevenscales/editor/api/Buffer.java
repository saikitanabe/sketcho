package net.sevenscales.editor.api;

import net.sevenscales.editor.diagram.Diagram;

class Buffer {
  private String text = "";
  private Diagram diagram;
  private boolean dirty;

  Diagram getDiagram() {
    return this.diagram;
  }

  void setDiagram(Diagram diagram) {
    this.diagram = diagram;
  }

  void setText(String text) {
    if (!this.text.equals(text)) {
      this.text = text;
      // mark dirty
      this.dirty = false;
    }
  }

	boolean isSent() {
    // return buffer.text.equals(lastSentText);
    return dirty;
  }
  
  void markSent() {
    this.dirty = true;
  }

}
