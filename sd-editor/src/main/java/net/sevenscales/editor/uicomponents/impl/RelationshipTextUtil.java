package net.sevenscales.editor.uicomponents.impl;

import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.silver.RelationshipShape;

public class RelationshipTextUtil {
  
  private static final int begin = 0;
  private static final int association = 0x00000001;
  private static final int dependancy = 0x00000002;
  private static final int inheritance = 0x00000004;
  private static final int startaggregation = 0x00000008;
  private static final int aggregation = 0x00000010;
  private static final int directed = 0x00000020;
  private static final int failed = -1;
  int index = 0;
  int leftEnd = 0;
  
  private String text;
  private RelationshipShape relationshipShape = new RelationshipShape();
  private int leftStart = -1;
  
  public RelationshipTextUtil() {
  }

  public Info parseShape() {
    boolean stop = false;
    int state = begin;
    int result = 0;
    index = 0;
    while (!stop && index < text.length()) {
      char ch = text.charAt(index);
      switch (state) {
        case begin:
          if (ch == '-') {
            leftEnd = index;
            state = association;
          } else if (ch == '<'){
            leftEnd = index;
            state = startaggregation;
          } else {
            state = begin;
          }
          break;
        case association:
          if (ch == '-') { 
            state |= dependancy;
            result |= RelationshipShape.DEPENDANCY;
          } else if (ch == '>'){
            result |= RelationshipShape.DIRECTED;
            stop = true;
          } else if (ch == '|') {
            state |= inheritance;
          } else {
            --index;
            stop = true;
          }
          break;
        case startaggregation:
          if (ch == '>') {
            state = aggregation;
          }
          break;
        case aggregation:
          if (ch == '-') {
            result |= RelationshipShape.AGGREGATE;
            state = aggregation | directed;
          } else {
            --index;
            state = failed;
            stop = true;
          }
          break;
        case aggregation | directed:
          if (ch == '>') {
            result |= RelationshipShape.DIRECTED;
            stop = true;
          } else {
            --index;
            stop = true;
          }
          break;
        case association | dependancy:
          if (ch == '>') {
            result |= RelationshipShape.DIRECTED;
            stop = true;
          } else {
            --index;
            stop = true;
          }
          break;
        case association | inheritance: {
          if (ch == '>') {
            result |= RelationshipShape.INHERITANCE;
            stop = true;
          } else {
            --index;
            stop = true;
          }
          
        }
//        case dependancy:
//          break;
      }
      ++index;
    }
    relationshipShape.caps = result;
    return relationshipShape;
  }

  public String parseLeftText() {
    int i = leftEnd;
    leftStart = -1;
    while (--i >= 0) {
      if (text.charAt(i) == '\n') {
        break;
      }
    }
    
    leftStart = i + 1;
    
    return text.substring(leftStart, leftEnd);
  }

  public String parseRightText() {
    return text.substring(index);
  }

  public void setText(String text) {
    this.text = text;
  }

  public String parseLabel() {
    int labelend = leftStart > 0 ? leftStart - 1 : 0;  
    return text.substring(0, labelend);
  }

}
