package net.sevenscales.editor.content.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

public interface DiagramHelp extends Constants {
  DiagramHelp INSTANCE = GWT.create(DiagramHelp.class);

  String addelement();
  String quickrelationship();
  String backgrounddrag();
  String relationship();
  String copypaste();
  String boardGuideText();
}