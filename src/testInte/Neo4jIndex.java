package testInte;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class Neo4jIndex {
	private static final String SERVER_ROOT_URI = "http://localhost:7474/db/data/";
	
	public static void createIndex(){
		createNodeOrRelationshipIndex("node", "articleNodeIndex");		
		createNodeOrRelationshipIndex("node", "activityNodeIndex");
		createNodeOrRelationshipIndex("node", "userNodeIndex");
		
		createNodeOrRelationshipIndex("relationship", "wasRevisionOf");
		createNodeOrRelationshipIndex("relationship", "wasGeneratedBy");
		createNodeOrRelationshipIndex("relationship", "wasAssociatedWith");
	}
	
	public static void createNodeOrRelationshipIndex(String type, String indexName){
		final String indexUri = SERVER_ROOT_URI + "index/" + type + "/";
		WebResource resource = Client.create().resource(indexUri);		
		String indexJson = "{ \"name\" : \"" + indexName + "\"}";
		
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).entity(indexJson)
				.post(ClientResponse.class);
		response.close();	
	}
	
	public static void addNodeOrRelationshipToIndex(String type, String indexName, String key, String value, String nodeUri){
		indexName = indexName.replaceAll(" ", "%20");
		value = value.replaceAll("\\\\", "\\\\\\\\");
		value = value.replaceAll("\"", "\\\\\"");
		final String indexNodeUri = SERVER_ROOT_URI + "index/" + type + "/" + indexName + "";
		WebResource resource = Client.create().resource(indexNodeUri);
		String indexNodeJson = "{\"key\" : \"" + key + "\", \"value\" : \"" + value + "\", \"uri\" : \"" + nodeUri + "\" }";

		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).entity(indexNodeJson)
				.post(ClientResponse.class);
		response.close();		
	}
	
	public static String queryNodeOrRelationship(String type, String indexName, String key, String value) {
		indexName = indexName.replaceAll(" ", "%20");
		value = value.replaceAll(" ", "%20");
		value = value.replaceAll("\\\\", "%5C");
		value = value.replaceAll("\"", "%22");
		value = value.replaceAll("=", "%3D");
		value = value.replaceAll("[+]", "%2B");
		value = value.replaceAll("\\^", "%5E");
	
		//Illegal character in path at index 71: http://localhost:7474/db/data/index/node/userNodeIndex/username/F=q(E+v^B)
		//F%3Dq(E%2Bv%5EB)
		
		final String queryNodeUri = SERVER_ROOT_URI + "index/" + type + "/" + indexName
				+ "/" + key + "/" + value + "";

		WebResource resource = Client.create().resource(queryNodeUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		String nodeOrRelationshipUri = null;
		try {
			JSONArray test = new JSONArray(response.getEntity(String.class));
			JSONObject json = test.getJSONObject(0);
			nodeOrRelationshipUri = json.getString("self");

		} catch (Exception e) {
			//System.err.println(e.getMessage());
		}
		response.close();
		return nodeOrRelationshipUri;
	}
	
	public static void delete(String deleteIndexUri){
		deleteIndexUri = SERVER_ROOT_URI + deleteIndexUri;
		WebResource resource = Client.create().resource(deleteIndexUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
		System.out.println(response.toString());
	}
	
	

}
