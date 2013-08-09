package net.st.shareddesign.editor.content.utils;

import java.util.List;

import junit.framework.TestCase;
import net.sevenscales.editor.content.utils.TokenParser;
import net.sevenscales.editor.content.utils.TokenParser.StringToken;

public class TokenParserTest extends TestCase {
	public void testOneWordBold() {
		String text = "*bold*";
		List<StringToken> tokens = TokenParser.parseEntities(text);
		assertEquals("bold", tokens.get(0).text);
		assertEquals(true, tokens.get(0).fontWeight);
	}
	
	public void testTwoWordBold() {
		String text = "*bold1* *bold2*";
		List<StringToken> tokens = TokenParser.parseEntities(text);
		
		assertEquals(2, tokens.size());
		
		assertEquals("bold1", tokens.get(0).text);
		assertEquals(true, tokens.get(0).fontWeight);
		
		assertEquals("bold2", tokens.get(1).text);
		assertEquals(true, tokens.get(1).fontWeight);
	}
	
	// TODO
//	public void testOneWordBoldPlusSpecial() {
//		String text = "*title*: text";
//		List<StringToken> tokens = TokenParser.parseEntities(text);
//		
//		assertEquals(3, tokens.size());
//		
//		assertEquals("title", tokens.get(0).text);
//		assertEquals(true, tokens.get(0).fontWeight);
//
//		assertEquals(":", tokens.get(1).text);
//		assertEquals(false, tokens.get(1).fontWeight);
//
//		assertEquals("text", tokens.get(2).text);
//		assertEquals(false, tokens.get(2).fontWeight);
//	}
	
	public void testTwoWorksNotBold() {
		String text = "bold1 *bold2";
		List<StringToken> tokens = TokenParser.parseEntities(text);
		
		assertEquals(2, tokens.size());
		
		assertEquals("bold1", tokens.get(0).text);
		assertEquals(false, tokens.get(0).fontWeight);
		
		assertEquals("*bold2", tokens.get(1).text);
		assertEquals(false, tokens.get(1).fontWeight);
	}
	
	public void testNotCompletedBold() {
		String text = "*bold1* *not bold *bold2*";
		List<StringToken> tokens = TokenParser.parseEntities(text);
		
		assertEquals(4, tokens.size());
		
		assertEquals("bold1", tokens.get(0).text);
		assertEquals(true, tokens.get(0).fontWeight);

		assertEquals("*not", tokens.get(1).text);
		assertEquals(false, tokens.get(1).fontWeight);

		assertEquals("bold", tokens.get(2).text);
		assertEquals(false, tokens.get(2).fontWeight);

		assertEquals("bold2", tokens.get(3).text);
		assertEquals(true, tokens.get(3).fontWeight);
	}
	
	public void testSentenceBold() {
		String text = "not *bold1 my sub* and *bold2*";
		List<StringToken> tokens = TokenParser.parseEntities(text);
		
		assertEquals(6, tokens.size());
		assertEquals("not", tokens.get(0).text);
		assertEquals(false, tokens.get(0).fontWeight);
		
		assertEquals("bold1", tokens.get(1).text);
		assertEquals(true, tokens.get(1).fontWeight);

		assertEquals("my", tokens.get(2).text);
		assertEquals(true, tokens.get(2).fontWeight);

		assertEquals("sub", tokens.get(3).text);
		assertEquals(true, tokens.get(3).fontWeight);
		
		assertEquals("and", tokens.get(4).text);
		assertEquals(false, tokens.get(4).fontWeight);

		assertEquals("bold2", tokens.get(5).text);
		assertEquals(true, tokens.get(5).fontWeight);
	}
	
	public void testWholeLineBold() {
		String text = "*bold1 bold2*";
		List<StringToken> tokens = TokenParser.parseEntities(text);
		
		assertEquals(2, tokens.size());
		
		assertEquals("bold1", tokens.get(0).text);
		assertEquals(true, tokens.get(0).fontWeight);
		
		assertEquals("bold2", tokens.get(1).text);
		assertEquals(true, tokens.get(1).fontWeight);
	}
	
	public void testNotBoldedLine() {
		String text = "*bold1 bold2";
		List<StringToken> tokens = TokenParser.parseEntities(text);
		
		assertEquals(2, tokens.size());
		
		assertEquals("*bold1", tokens.get(0).text);
		assertEquals(false, tokens.get(0).fontWeight);
		
		assertEquals("bold2", tokens.get(1).text);
		assertEquals(false, tokens.get(1).fontWeight);
	}
	
	public void testSameThingOnWithMultipleLines() {
		String text = "*bold1 bold2*\ntoka rivi";
		List<StringToken> tokens = TokenParser.parse(text);
		
		assertEquals(5, tokens.size());
		
		assertEquals("bold1", tokens.get(0).text);
		assertEquals(true, tokens.get(0).fontWeight);
		
		assertEquals("bold2", tokens.get(1).text);
		assertEquals(true, tokens.get(1).fontWeight);

		assertEquals("\n", tokens.get(2).text);
		assertEquals(false, tokens.get(2).fontWeight);

		assertEquals("toka", tokens.get(3).text);
		assertEquals(false, tokens.get(3).fontWeight);
		
		assertEquals("rivi", tokens.get(4).text);
		assertEquals(false, tokens.get(4).fontWeight);
	}

}
