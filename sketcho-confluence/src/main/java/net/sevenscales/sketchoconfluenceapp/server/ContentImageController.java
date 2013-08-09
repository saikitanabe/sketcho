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

package net.sevenscales.sketchoconfluenceapp.server;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sevenscales.sketchoconfluenceapp.server.utils.IStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentImageController extends HttpServlet {
//	private static final Logger log = LoggerFactory.getLogger(ContentImageController.class);

	// private BookService bookService;

	private static final String CONTENT_TYPE = "image/png";
	private IStore store = null;

	public void setStoreHandler(IStore store) {
		this.store = store;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

//		log.debug("id {} pageId {}", request.getParameter("content"), request.getParameter("pageId"));
		
		String id = (String) request.getParameter("content");
		Long pageId = new Long(request.getParameter("pageId"));

		if (id == null)
			throw new IllegalStateException("must specify the content");

		if (pageId == null)
			throw new IllegalStateException("must specify page id");

		if (id.equals("confluence-add-sketcho.png")) {
			// HACK: don't know how to refer plugin custom icons!
			File png = new File(".");
			// File png = new File("./images/"+id);
			// byte[] content = new byte[(int)png.length()];
			// try {
			// FileInputStream fis = new FileInputStream(png);
			// fis.read(content);
			// } catch (FileNotFoundException e) {
			// e.printStackTrace();
			// throw new RuntimeException(e);
			// } catch (IOException e) {
			// e.printStackTrace();
			// throw new RuntimeException(e);
			// }
			// response.setContentLength(content.length);
			// response.getOutputStream().write(content);
			// response.getOutputStream().flush();
			return;
		}
		// set the content type
		// response.setContentType(CONTENT_TYPE);
		//
		// // get the ID of the book -- set "bad request" if its not a valid integer
		// Long id;
		// try {
		// id = ServletRequestUtils.getLongParameter(request,"content");
		// if (id == null)
		// throw new IllegalStateException("must specify the content id");
		// } catch (Exception e) {
		// response.sendError(HttpServletResponse.SC_BAD_REQUEST,
		// "invalid content");
		// return null;
		// }

		// // get the book from the service
		// Book book = bookService.getBook(id);
		//
		// // if the book doesn't exist then set "not found"
		// if (book == null) {
		// response.sendError(HttpServletResponse.SC_NOT_FOUND, "no such book");
		// return null;
		// }
		//
		// // if the book doesn't have a picture, set "not found"
		// if (book.getCoverPng() == null) {
		// response.sendError(HttpServletResponse.SC_NOT_FOUND,
		// "book has no image");
		// return null;
		// }
		//
		// if (logger.isDebugEnabled())
		// logger.debug("returning cover for book "
		// + book.getKey() + " '" + book.getTitle() + "'" +
		// " size: " + book.getCoverPng().length + " bytes");
		//
		// // send the image
		byte[] image = store.load(pageId, id);
		if (image != null) {
			response.setContentLength(image.length);
			response.setContentType(CONTENT_TYPE);
			response.getOutputStream().write(image);
			response.getOutputStream().flush();
		}

		// we already handled the response
		// return null;
	}

	public void downloadImage(HttpServletResponse response, Long pageId, String id) {

		try {
			// png = SvgUtil.createPng(svg);
			byte[] image = store.load(pageId, id);
			int length = image.length;

			ServletOutputStream os = response.getOutputStream();
			// String mimeType = getServletContext().getMimeType(png.getName());
			// response.setContentType(mimeType);
			response.setContentType("application/octet-stream");
			// response.setContentType("application/download");
			response.setContentLength(length);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + id
					+ "\"");

			// FileInputStream fis = new FileInputStream(png);
			byte[] bbuf = new byte[1024];
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(image));
			while ((in != null) && ((length = in.read(bbuf)) != -1)) {
				os.write(bbuf, 0, length);
			}

			// FileCopyUtils.copy(fis, os);
			in.close();
			// fis.close();
			os.flush();
			// os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// @Required
	// public void setBookService(BookService bookService) {
	// this.bookService = bookService;
	// }
}
