package net.st.shareddesign.editor.uicomponents.impl;

import junit.framework.TestCase;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.RelationshipShape2;
import net.sevenscales.editor.uicomponents.impl.RelationshipTextUtil2;

public class RelationshipTextUtil2Test extends TestCase {
  public void testAssociation() {
    RelationshipTextUtil2 su = new RelationshipTextUtil2();
    su.setText("-");
    Info s = su.parseShape();
    assertTrue(s instanceof RelationshipShape2);
    RelationshipShape2 rs = (RelationshipShape2) s;
    assertTrue(!rs.isDirected());
    assertTrue(!rs.isAggregate());
    assertTrue(!rs.isDependancy());    
    assertTrue(!rs.isInheritance());
    
    assertEquals("", su.parseLeftText());
    assertEquals("", su.parseLabel());
  }

  public void testAssociationLabel() {
    RelationshipTextUtil2 su = new RelationshipTextUtil2();
    su.setText("pallo\nkala-");
    Info s = su.parseShape();
    assertTrue(s instanceof RelationshipShape2);
    RelationshipShape2 rs = (RelationshipShape2) s;
    assertTrue(!rs.isDirected());
    assertTrue(!rs.isAggregate());
    assertTrue(!rs.isDependancy());    
    assertTrue(!rs.isInheritance());
    
    assertEquals("kala", su.parseLeftText());
    assertEquals("pallo", su.parseLabel());
  }

  public void testAssociationLabelWindows() {
	    RelationshipTextUtil2 su = new RelationshipTextUtil2();
	    su.setText("pallo\r\nkala-");
	    Info s = su.parseShape();
	    assertTrue(s instanceof RelationshipShape2);
	    RelationshipShape2 rs = (RelationshipShape2) s;
	    assertTrue(!rs.isDirected());
	    assertTrue(!rs.isAggregate());
	    assertTrue(!rs.isDependancy());    
	    assertTrue(!rs.isInheritance());
	    
	    assertEquals("kala", su.parseLeftText());
	    assertEquals("pallo\r", su.parseLabel());
	  }

  public void testAssociationLabelLeftRight() {
    RelationshipTextUtil2 su = new RelationshipTextUtil2();
    su.setText("label\nleft-right");
    Info s = su.parseShape();
    assertTrue(s instanceof RelationshipShape2);
    RelationshipShape2 rs = (RelationshipShape2) s;
    assertTrue(!rs.isDirected());
    assertTrue(!rs.isAggregate());
    assertTrue(!rs.isDependancy());    
    assertTrue(!rs.isInheritance());
    
    assertEquals("left", su.parseLeftText());
    assertEquals("label", su.parseLabel());
    assertEquals("right", su.parseRightText());
  }
  
  public void testRigtAssociationText() {
    RelationshipTextUtil2 su = new RelationshipTextUtil2();
    su.setText("-*");
    Info s = su.parseShape();
    assertTrue(s instanceof RelationshipShape2);
    RelationshipShape2 rs = (RelationshipShape2) s;
    assertTrue(!rs.isDirected());
    assertTrue(!rs.isAggregate());
    assertTrue(!rs.isDependancy());    
    assertTrue(!rs.isInheritance());
    
    String right = su.parseRightText();
    assertEquals("*", right);
  }
  
  public void testLeftAssociationText() {
    RelationshipTextUtil2 su = new RelationshipTextUtil2();
    su.setText("*-");
    Info s = su.parseShape();
    assertTrue(s instanceof RelationshipShape2);
    RelationshipShape2 rs = (RelationshipShape2) s;
    assertTrue(!rs.isDirected());
    assertTrue(!rs.isAggregate());
    assertTrue(!rs.isDependancy());    
    assertTrue(!rs.isInheritance());
    
    String left = su.parseLeftText();
    assertEquals("*", left);
  }
  
  public void testAssociationTexts0() {
    RelationshipTextUtil2 su = new RelationshipTextUtil2();
    su.setText("left-right");
    Info s = su.parseShape();
    assertTrue(s instanceof RelationshipShape2);
    RelationshipShape2 rs = (RelationshipShape2) s;
    assertTrue(!rs.isDirected());
    assertTrue(!rs.isAggregate());
    assertTrue(!rs.isDependancy());
    assertTrue(!rs.isInheritance());
    
    String left = su.parseLeftText();
    assertEquals("left", left);
    String right = su.parseRightText();
    assertEquals("right", right);
  }

  public void testAssociationTexts1() {
    RelationshipTextUtil2 su = new RelationshipTextUtil2();
    su.setText("left--right");
    Info s = su.parseShape();
    assertTrue(s instanceof RelationshipShape2);
    RelationshipShape2 rs = (RelationshipShape2) s;
    assertTrue(!rs.isDirected());
    assertTrue(!rs.isAggregate());
    assertTrue(rs.isDependancy());
    assertTrue(!rs.isInheritance());
    
    String left = su.parseLeftText();
    assertEquals("left", left);
    String right = su.parseRightText();
    assertEquals("right", right);
  }
  
  public void testAssociationTexts2() {
    RelationshipTextUtil2 su = new RelationshipTextUtil2();
    su.setText("left-->right");
    Info s = su.parseShape();
    assertTrue(s instanceof RelationshipShape2);
    RelationshipShape2 rs = (RelationshipShape2) s;
    assertTrue(rs.isDirected());
    assertTrue(!rs.isAggregate());
    assertTrue(rs.isDependancy());
    assertTrue(!rs.isInheritance());
    
    String left = su.parseLeftText();
    assertEquals("left", left);
    String right = su.parseRightText();
    assertEquals("right", right);
  }

  public void testAssociationTexts3() {
    RelationshipTextUtil2 su = new RelationshipTextUtil2();
    su.setText("left<>->right");
    Info s = su.parseShape();
    assertTrue(s instanceof RelationshipShape2);
    RelationshipShape2 rs = (RelationshipShape2) s;
    assertTrue(rs.isDirected());
    assertTrue(rs.isAggregate());
    assertTrue(!rs.isDependancy());
    assertTrue(!rs.isInheritance());
    
    String left = su.parseLeftText();
    assertEquals("left", left);
    String right = su.parseRightText();
    assertEquals("right", right);
  }

  public void testDependancy() {
    RelationshipTextUtil2 su = new RelationshipTextUtil2();
    su.setText("--");
    Info s = su.parseShape();
    assertTrue(s instanceof RelationshipShape2);
    RelationshipShape2 rs = (RelationshipShape2) s;
    assertTrue(rs.isDependancy());    
    assertTrue(!rs.isDirected());
    assertTrue(!rs.isAggregate());
    assertTrue(!rs.isInheritance());    
  }

  public void testDirectedDependancy() {
    RelationshipTextUtil2 su = new RelationshipTextUtil2();
    su.setText("-->");
    Info s = su.parseShape();
    assertTrue(s instanceof RelationshipShape2);
    RelationshipShape2 rs = (RelationshipShape2) s;
    assertTrue(rs.isDependancy());    
    assertTrue(rs.isDirected());
    assertTrue(!rs.isAggregate());
    assertTrue(!rs.isInheritance());    
  }

  public void testInheritance() {
    RelationshipTextUtil2 su = new RelationshipTextUtil2();
    su.setText("-|>");
    Info s = su.parseShape();
    assertTrue(s instanceof RelationshipShape2);
    RelationshipShape2 rs = (RelationshipShape2) s;
    assertTrue(!rs.isDependancy());    
    assertTrue(!rs.isDirected());
    assertTrue(!rs.isAggregate());
    assertTrue(rs.isInheritance());    
  }
  
  public void testOwns() {
    RelationshipTextUtil2 su = new RelationshipTextUtil2();
    su.setText("<>-");
    Info s = su.parseShape();
    assertTrue(s instanceof RelationshipShape2);
    RelationshipShape2 rs = (RelationshipShape2) s;
    assertTrue(!rs.isDependancy());    
    assertTrue(!rs.isDirected());
    assertTrue(rs.isAggregate());
    assertTrue(!rs.isInheritance());    
  }
  
  public void testOwnsDirected() {
    RelationshipTextUtil2 su = new RelationshipTextUtil2();
    su.setText("<>->");
    Info s = su.parseShape();
    assertTrue(s instanceof RelationshipShape2);
    RelationshipShape2 rs = (RelationshipShape2) s;
    assertTrue(!rs.isDependancy());    
    assertTrue(rs.isDirected());
    assertTrue(rs.isAggregate());
    assertTrue(!rs.isInheritance());    
  }



}
