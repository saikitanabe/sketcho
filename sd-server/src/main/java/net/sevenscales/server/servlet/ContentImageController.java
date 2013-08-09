/*
 * Copyright 2005-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sevenscales.server.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class ContentImageController extends AbstractController {

//    private BookService bookService;

    private static final String CONTENT_TYPE = "image/png";

	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws IllegalStateException, IOException {

//	  String svg = request.getHeader("content-svg");
		String id = null;
	  try {
		id = ServletRequestUtils.getStringParameter(request, "content");
		if (id == null)
			throw new IllegalStateException("must specify the content");
	} catch (ServletRequestBindingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  downloadImage(response, id);
	  
		// set the content type
//		response.setContentType(CONTENT_TYPE);
//
//		// get the ID of the book -- set "bad request" if its not a valid integer
//		Long id;
//		try {
//			id = ServletRequestUtils.getLongParameter(request,"content");
//			if (id == null)
//				throw new IllegalStateException("must specify the content id");
//		} catch (Exception e) {
//			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid content");
//			return null;
//		}

//		// get the book from the service
//		Book book = bookService.getBook(id);
//
//		// if the book doesn't exist then set "not found"
//		if (book == null) {
//			response.sendError(HttpServletResponse.SC_NOT_FOUND, "no such book");
//			return null;
//		}
//
//		// if the book doesn't have a picture, set "not found"
//		if (book.getCoverPng() == null) {
//			response.sendError(HttpServletResponse.SC_NOT_FOUND, "book has no image");
//			return null;
//		}
//
//		if (logger.isDebugEnabled())
//			logger.debug("returning cover for book "
//					+ book.getKey() + " '" + book.getTitle() + "'" +
//					" size: " + book.getCoverPng().length + " bytes");
//
//		// send the image
//		response.setContentLength(book.getCoverPng().length);
//		response.getOutputStream().write(book.getCoverPng());
//		response.getOutputStream().flush();

		// we already handled the response
		return null;
	}
	
  public void downloadImage(HttpServletResponse response, String id) {
    
    try {
//      png = SvgUtil.createPng(svg);
      File png = new File("./tmp/"+id+".png"); 
      int length = (int) png.length();
    
      ServletOutputStream os = response.getOutputStream();
//      String mimeType = getServletContext().getMimeType(png.getName());
//      response.setContentType(mimeType);
      response.setContentType("application/octet-stream");
//      response.setContentType("application/download");
      response.setContentLength(length);
      response.setHeader("Content-Disposition","attachment; filename=\"" +
          png.getName() + "\"");
      
//      FileInputStream fis = new FileInputStream(png);
      byte[] bbuf = new byte[1024];
      DataInputStream in = new DataInputStream(new FileInputStream(png)); 
      while ((in != null) && ((length = in.read(bbuf)) != -1)) { 
          os.write(bbuf, 0, length); 
      } 

//      FileCopyUtils.copy(fis, os);
      in.close();
//      fis.close();
      os.flush();
//      os.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }


//	@Required
//	public void setBookService(BookService bookService) {
//	    this.bookService = bookService;
//	}
}
