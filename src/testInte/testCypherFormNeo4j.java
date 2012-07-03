package testInte;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class testCypherFormNeo4j {

	private static final String SERVER_ROOT_URI = "http://localhost:7474/db/data/";

	public static void main(String[] args) throws ClientHandlerException,
			UniformInterfaceException, JSONException {
		// TODO Auto-generated method stub
		run();

	}

	private static void run() throws ClientHandlerException,
			UniformInterfaceException, JSONException {
		final String cypherUri = SERVER_ROOT_URI + "cypher";

		String cypherPayload = "{\"query\": \"start a=node(*) return count(a)\", \"params\":{}}";

		WebResource resource = Client.create().resource(cypherUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).entity(cypherPayload)
				.post(ClientResponse.class);

//		 System.out.println( String.format(
//		 "POST [%s] to [%s], status code [%d], returned data: "
//		 + System.getProperty( "line.separator" ) + "%s",
//		 cypherPayload, cypherUri, response.getStatus(),
//		 response.getEntity( String.class ) ));
		try {
			JSONObject json = new JSONObject(response.getEntity(String.class));
//			String in = response.getEntity(String.class);
//			System.out.println(in);
//			System.out.println("show the characters: ");
//			for (int i = 0; i < 10; i++) {
//				System.out.println("the nu: " + i + " is: " + in.charAt(i));
//			}
//			System.out.println(response.getType());
//			System.out.println(response.getEntityTag());
//			JSONObject json = new JSONObject(in);
			JSONArray getData = json.getJSONArray("data");
			JSONArray test = getData.getJSONArray(0);
			String getDataObject = test.getString(0);
			//String nodeUrl = getDataObject.getString("self");
		
			System.out.println(getDataObject);
		} catch (Exception e) {
			if(e.getMessage().equals("JSONArray[0] not found.")){
				System.out.println("done!!!");
			}
			System.err.println(e.getMessage());
		}
		response.close();
	}

}
