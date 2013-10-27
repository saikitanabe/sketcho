package net.sevenscales.confluence.plugins.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.sevenscales.domain.DiagramContentDTO;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.sketchoconfluenceapp.server.utils.IStore;
import net.sevenscales.sketchoconfluenceapp.server.utils.StoreEntry;
import net.sevenscales.sketchoconfluenceapp.server.utils.SvgUtil;

import com.thoughtworks.xstream.XStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A resource of message.
 * NOTE for security reasons anonymous editing is not allowed and there should be mark
 * whenever anonymous user is editing the diagram. 
 * 
 * "By default, access to all resources (using any method) requires the client to be authenticated. 
 * Resources that should be available anonymously MUST be marked as such.
 * The default implementation SHOULD use the AnonymousAllowed annotation."
 */
@Path("/sketch")
public class StoreRestService {
	private static final Logger log = LoggerFactory.getLogger(StoreRestService.class);

	private IStore store = null;
	private XStream xstream = new XStream();
	
	public StoreRestService() {
		xstream.alias("net.sevenscales.domain.dto.DiagramContentDTO", DiagramContentDTO.class);
		xstream.alias("net.sevenscales.domain.dto.DiagramItemDTO", DiagramItemDTO.class);
	}
	
	public void setStoreHandler(IStore store) {
		this.store = store;
	}

	@PUT
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response getSketch(DiagramContentJson diagram) {
//		log.debug("getSketch: pageId(" + diagram.getPageId() + ") name(" + diagram.getName() + ")");
		String modelXML = store.loadContent(diagram.getPageId(), diagram.getName());
		DiagramContentJson json = new DiagramContentJson();
		if (modelXML != null) {
			IDiagramContent fromStore = (IDiagramContent) xstream.fromXML(modelXML);
			json = DiagramContentJson.fromDTO(fromStore);
		}
		return Response.ok(json).build();
	}
	
	@GET
  @Path("{sketch}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getSketch(@PathParam("sketch") String id) {
		int index = id.indexOf(':');
		DiagramContentJson json = new DiagramContentJson();
		if (index > 1 && (index + 1) < id.length()) {
			Long pageId = new Long(id.substring(0, index));
			String name = id.substring(index + 1);
//			log.debug("@GET id: " + id);
//			log.debug("getSketch: pageId(" + pageId + ") name(" + name + ")");

			String modelXML = store.loadContent(pageId, name);
			log.debug("getSketch {0}", modelXML);
			if (modelXML != null) {
				IDiagramContent fromStore = (IDiagramContent) xstream.fromXML(modelXML);
				json = DiagramContentJson.fromDTO(fromStore);
			}
		}
		return Response.ok(json).build();
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.TEXT_PLAIN })
	public Response updateSketch(DiagramContentJson diagram) {
		DiagramContentDTO content = diagram.asDTO();
//		log.debug("diagram pageId {} name {}", diagram.getPageId(), diagram.getName());
		
		byte[] image = SvgUtil.createPng(diagram.getSvg(), diagram.getName());

		StoreEntry entry = new StoreEntry();
		entry.setDiagramContent(xstream.toXML(content));
		entry.setImage(image);
		// String entryXML = xstream.toXML(entry);
		String key = store.store(diagram.getPageId(), diagram.getName(), entry);
		
		String result = "?pageId=" + diagram.getPageId() + "&content=" + key.replace(":", "%3A");
//		log.debug("image identifiers {}", result);
//		store.store(diagram.getPageId(), diagram.getName(), entry);
		
		return Response.ok(result).build();
	}


}