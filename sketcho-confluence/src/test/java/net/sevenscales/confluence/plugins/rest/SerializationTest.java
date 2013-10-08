package net.sevenscales.confluence.plugins.rest;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.sevenscales.domain.DiagramContentDTO;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.api.IDiagramContent;

import org.junit.Test;

import com.thoughtworks.xstream.XStream;

public class SerializationTest {
	private XStream xstream = new XStream();
	
	public SerializationTest() {
		xstream.alias("net.sevenscales.domain.dto.DiagramContentDTO", DiagramContentDTO.class);
		xstream.alias("net.sevenscales.domain.dto.DiagramItemDTO", DiagramItemDTO.class);
		xstream.alias("net.sevenscales.confluence.plugins.rest.ChildDTO", ChildDTO.class);
	}

	@Test
	public void test() {
		DiagramContentDTO content = new DiagramContentDTO();
		content.addItem(new DiagramItemDTO("_sampletext", "_stype", "_sshape", "_sbg", "_stcolor", 0, 0L, "_cliid", "_scd", 0));
		content.addItem(new ChildDTO("sampletext", "child", "sshape", "sbg", "stcolor", 0, 0L, "cliid", "scd", 0, "1234"));
		String xml = xstream.toXML(content);
		System.out.println(xml);
		
		IDiagramContent fromStore = (IDiagramContent) xstream.fromXML(xml);
		DiagramContentJson json = new DiagramContentJson();
		json = DiagramContentJson.fromDTO(fromStore);
//		System.out.println(Response.ok(json).build().toString());
//		System.out.println(Response.ok(json, MediaType.APPLICATION_JSON).build().getEntity().toString());

		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(DiagramContentJson.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//	        OutputStream os = new FileOutputStream( "nosferatu.xml" );
	        ByteArrayOutputStream os = new ByteArrayOutputStream();
//	        aClass.outputStreamMethod(os);
	        jaxbMarshaller.marshal( Response.ok(json, MediaType.APPLICATION_JSON).build().getEntity(), os );
	        String aString = new String(os.toByteArray(),"UTF-8");
	        System.out.println(aString);
	        
//	        jaxbMarshaller.setProperty("eclipselink.media-type", "application/json");
//	        jaxbMarshaller.setProperty("eclipselink.media-type", "application/json");
//	        marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);

//	        jaxbMarshaller.marshal(json, System.out);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//        Unmarshaller u = jc.createUnmarshaller();
//        Object element = u.unmarshal( new File( "foo.xml" ) );
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//	       JAXBContext jaxbContext = JAXBContext.newInstance(MemberDetailsRequest.class);
////		assertThat((jaxbContext instanceof org.eclipse.persistence.jaxb.JAXBContext), is(true));
//		Marshaller marshaller = jaxbContext.createMarshaller();
//		MemberDetailsRequest memberDetailsRequest = new MemberDetailsRequest();
//		memberDetailsRequest.setId(1L);
//		StringWriter writer = new StringWriter();
//		marshaller.marshal(memberDetailsRequest, writer);
//		String marshalledXml = writer.toString();
//		assertThat(marshalledXml, containsString("MemberDetailsRequest"));
	}

}
