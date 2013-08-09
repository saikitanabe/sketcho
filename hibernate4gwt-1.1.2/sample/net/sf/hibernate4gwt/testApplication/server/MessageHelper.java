package net.sf.hibernate4gwt.testApplication.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.hibernate4gwt.testApplication.domain.IMessage;

/**
 * Message helper
 * Implements a basic keyword computation
 * @author bruno.marchesson
 *
 */
public class MessageHelper
{
	/**
	 * Compute keywords for message
	 * @param message the message to save
	 */
	public static void computeKeywords(IMessage message)
	{
	//	Create keywords map if needed
	//
		Map<String, Integer> keywords = message.getKeywords();
		if (keywords == null)
		{
			keywords = new HashMap<String, Integer>();
		}
		
	//	Computation of keywords (fake, of course)
	//
		String text = message.getMessage();
		// Remove old keywords
		List<String> keyToRemove = new ArrayList<String>();
		for (String keyword : keywords.keySet())
		{
			if (text.contains(keyword) == false)
			{
				keyToRemove.add(keyword);
			}
		}
		for (String keyword : keyToRemove)
		{
			keywords.remove(keyword);
		}
		
		// Keywords update
		StringTokenizer tokenizer = new StringTokenizer(text);
		while (tokenizer.hasMoreTokens())
		{
			String token = tokenizer.nextToken();
			if ((token.length() > 3) &&
				(keywords.containsKey(token) == false))
			{
				keywords.put(token, token.length());
			}
		}
		
	//	Set keywords
	//
		if (keywords.isEmpty() == false)
		{
			message.setKeywords(keywords);
		}
	}
}
