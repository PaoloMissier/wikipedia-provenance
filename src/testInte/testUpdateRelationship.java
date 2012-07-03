package testInte;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class testUpdateRelationship {


	private static final String SERVER_ROOT_URI="http://localhost:7474/db/data/";

	public static void main(String[] args) throws URISyntaxException {
		URI testUpdateRelationshipNode21 = new URI("http://localhost:7474/db/data/node/21");
		URI testUpdateRelationshipNode22 = new URI("http://localhost:7474/db/data/node/22");
		
		URI relationshipUri = addRelationship(testUpdateRelationshipNode21, testUpdateRelationshipNode22, "singer", "{ \"from\" : \"1976\", \"until\" : \"1986\" }");
		
		addMetadataToProperty(relationshipUri, "stars", "5");

	}
	
	private static void addMetadataToProperty( URI relationshipUri,
            String name, String value ) throws URISyntaxException
    {
        URI propertyUri = new URI( relationshipUri.toString() + "/properties" );
        String entity = toJsonNameValuePairCollection( name, value );
        WebResource resource = Client.create()
                .resource( propertyUri );
        ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .entity( entity )
                .put( ClientResponse.class );

        System.out.println( String.format(
                "PUT [%s] to [%s], status code [%d]", entity, propertyUri,
                response.getStatus() ) );
        response.close();
    }
	private static String toJsonNameValuePairCollection(String name, String value){
		//return String.format( "{ \"%s\" : \"%s\" }", name, value);
		return String.format( "{ \"%s\" : \"%s\" }", name, value );
	}
	
	private static URI addRelationship(URI startNode, URI endNode, String relationshipType, String jsonAttributes) throws URISyntaxException{
		URI fromUri = new URI(startNode.toString() + "/relationships");
		String relationshipJson = generateJsonRelationship(endNode, relationshipType, jsonAttributes);
		System.out.println(relationshipJson + "===================================");
		WebResource resource = Client.create().resource(fromUri);
		// POST JSON to the relationships URI
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).entity(relationshipJson).post(ClientResponse.class);
		
		final URI location = response.getLocation();
		System.out.println(String.format("POST to [%s], status code [%d], location header [%s]", fromUri, response.getStatus(), location.toString()));
		response.close();
		return location;
	}
	
	
	private static String generateJsonRelationship(URI endNode, String relationshipType, String... jsonAttributes ){
		StringBuilder sb = new StringBuilder();
		sb.append("{ \"to\" : \"");
		sb.append(endNode.toString());
		sb.append("\", ");
		
		sb.append("\"type\" : \"");
		sb.append(relationshipType);
		if (jsonAttributes == null || jsonAttributes.length<1){
			sb.append("\"");
		}else{
			sb.append("\", \"data\" : ");
			for(int i = 0; i < jsonAttributes.length; i++){
				sb.append(jsonAttributes[i]);
					if(i < jsonAttributes.length - 1){
						sb.append(", ");
					}
				}
			}
		
		sb.append(" }");
		return sb.toString();
		}

}
