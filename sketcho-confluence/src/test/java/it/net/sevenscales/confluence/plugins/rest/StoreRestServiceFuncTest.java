package it.net.sevenscales.confluence.plugins.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.sevenscales.confluence.plugins.rest.DiagramContentJson;
import net.sevenscales.confluence.plugins.rest.StoreRestServiceModel;

import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StoreRestServiceFuncTest {
	private static final String BASE_URL = "http://localhost:1990/confluence";

	@Before
	public void setup() {

	}

	@After
	public void tearDown() {

	}

	@Test
	public void messageIsValid() {
		// String baseUrl = System.getProperty("baseurl");
		String resourceUrl = BASE_URL + "/rest/storerestservice/1.0/message";

		RestClient client = new RestClient();
		Resource resource = client.resource(resourceUrl);

		try {
			StoreRestServiceModel message = resource.get(StoreRestServiceModel.class);

			assertEquals("wrong message", "Hello World", message.getMessage());
		} catch (ClientWebException e) {
			System.out.println("ClientWebException: " + e.getResponse().getStatusCode() + 
					" " + e.getResponse().getMessage());
			assertTrue(false);
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	@Test
	public void messagePost() {
		String resourceUrl = BASE_URL + "/rest/storerestservice/1.0/message";

		RestClient client = new RestClient();
		Resource resource = client.resource(resourceUrl);

//		StoreRestServiceModel model = new StoreRestServiceModel("kukkakaali");
//		ClientResponse resp = resource.post(model);
//		assertEquals("wrong status", 200, resp.getStatusCode());
//		Resource resource = client.resource(resourceUrl);
		DiagramContentJson dj = new DiagramContentJson();
		dj.setName("Habula");
//		resource.post(dj);
		
//    ClientResponse response = resource.post(ClientResponse.class, dj);
//    System.out.println("Form response " + response.getEntity(String.class));
	}

}
