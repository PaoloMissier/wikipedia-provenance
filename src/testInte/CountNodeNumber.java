package testInte;

import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class CountNodeNumber {
	
private static final String SERVER_ROOT_URI = "http://localhost:7474/db/data/";
	
	public static String countAllNodeNumber(){
		final String cypherUri = SERVER_ROOT_URI + "cypher";

		String cypherPayload = "{\"query\": \"start a=node(*) return count(a)\", \"params\":{}}";
		String number = "";

		WebResource resource = Client.create().resource(cypherUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).entity(cypherPayload)
				.post(ClientResponse.class);
		try {
			JSONObject json = new JSONObject(response.getEntity(String.class));
			JSONArray getData = json.getJSONArray("data");
			JSONArray test = getData.getJSONArray(0);
			number = test.getString(0);
			return number;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		response.close();
		return number;
	}


}
