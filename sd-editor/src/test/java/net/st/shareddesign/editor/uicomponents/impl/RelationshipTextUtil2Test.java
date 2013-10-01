package net.st.shareddesign.editor.uicomponents.impl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.After;

import com.google.gson.Gson;

import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.RelationshipShape2;
import net.sevenscales.editor.uicomponents.impl.RelationshipTextUtil2;
import net.sevenscales.domain.DiagramItemDTO;


public class RelationshipTextUtil2Test extends TestCase {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    // private static class JsonFactoryImpl implements RelationshipParser.JsonFactory {
    //   public RelationshipType fromJson(String jsonStr) {
    //     Gson gson = new Gson();
    //     return gson.fromJson(jsonStr, RelationshipType.class);
    //   }
    // }

    // private JsonFactoryImpl jsonFactoryImpl = new JsonFactoryImpl();

    @Before
    public void setUpStreams() {
        System.out.println("setUpStreams...");
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
    }    

  public void testLegacyAssociation() {
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

  public void testLegacyAssociationWithArrowText() {
    RelationshipTextUtil2 su = new RelationshipTextUtil2();
    su.setText("-\\>");
    Info s = su.parseShape();
    assertTrue(s instanceof RelationshipShape2);
    RelationshipShape2 rs = (RelationshipShape2) s;
    assertTrue(!rs.isDirected());
    assertTrue(!rs.isAggregate());
    assertTrue(!rs.isDependancy());
    assertTrue(!rs.isInheritance());
    
    assertEquals("", su.parseLeftText());
    assertEquals("", su.parseLabel());
    assertEquals("\\>", su.parseRightText());
  }

  public void testLegacyAssociationLabel() {
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

  public void testLegacyAssociationLabelWindows() {
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

  public void testLegacyAssociationLabelLeftRight() {
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
  
  public void testLegacyRigtAssociationText() {
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
  
  public void testLegacyLeftAssociationText() {
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
  
  public void testLegacyAssociationTexts0() {
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

  public void testLegacyAssociationTexts1() {
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
  
  public void testLegacyAssociationTexts2() {
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

  public void testLegacyAssociationTexts3() {
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

  public void testLegacyDependancy() {
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

  public void testLegacyDirectedDependancy() {
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

  public void testLegacyInheritance() {
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
  
  public void testLegacyOwns() {
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
  
  public void testLegacyOwnsDirected() {
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
