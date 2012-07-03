package testInte;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class testUpdateNode {


	private static final String SERVER_ROOT_URI="http://localhost:7474/db/data/";
	
	public static void main(String[] args) throws URISyntaxException {
		URI testUpdateNode12 = new URI("http://localhost:7474/db/data/node/12");
		addProperty(testUpdateNode12,"band","test2");
		addProperty(testUpdateNode12,"band2","test4");
		//addProperty(testUpdateNode12,"band3","test3");

	}
	
	
	private static void addProperty(URI nodeUri, String propertyName, String propertyValue){
		// http://localhost:7474/db/data/node/{node_id}/properties/{property_name}
		String propertyUri = nodeUri.toString() + "/properties/" + propertyName;
		
		WebResource resource = Client.create().resource(propertyUri);
		
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).entity("\"" + propertyValue + "\"").put(ClientResponse.class);
		
		System.out.println(String.format("PUT to [%s], status code [%d]", propertyUri, response.getStatus()));
		response.close();
	}
	

}
