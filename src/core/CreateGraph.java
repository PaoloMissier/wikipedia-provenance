package core;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import core.neo4j.Neo4jIndex;



public class CreateGraph {
	
	public static final String SERVER_ROOT_URI="http://localhost:7474/db/data/";
		
	public static void getData(String title, String revid, String parentid, String user, String time, String comment, String size, String pageid) throws URISyntaxException, ClientHandlerException, UniformInterfaceException, JSONException{
		
		checkDatabaseIsRunning();
		
		//Check if article node exists - else create it and add to Neo
		String specialArticleNodeUri = Neo4jIndex.queryNodeOrRelationship("node", "articleNodeIndex", "revid", revid);
		URI articleNode = new URI("");
		if(specialArticleNodeUri == null){
			articleNode = createNode();
			Neo4jIndex.addNodeOrRelationshipToIndex("node", "articleNodeIndex", "revid", revid, articleNode.toString());
		}else{
			articleNode = new URI(specialArticleNodeUri);
		}	
		//Add the appropriate properties for the article
		addProperty(articleNode, "id", revid);
		addProperty(articleNode, "revid", revid);
		addProperty(articleNode, "title", title);
		addProperty(articleNode, "prov:type", "article");
		addProperty(articleNode, "type", "entity");
		addProperty(articleNode, "pageid", pageid);
		addProperty(articleNode, "comment", comment);
		addProperty(articleNode, "time", time);
		addProperty(articleNode, "size", size);
		addProperty(articleNode, "parentid", parentid);
	
		
		String specialActivityNodeUri = Neo4jIndex.queryNodeOrRelationship("node", "activityNodeIndex", "revid", revid);
		URI activityNode = new URI("");
		String commentId = "comment" + revid;
		if(specialActivityNodeUri == null){
			activityNode = createNode();
			Neo4jIndex.addNodeOrRelationshipToIndex("node", "activityNodeIndex", "revid", revid, activityNode.toString());
			
			
			addProperty(activityNode, "prov:type", "edit");
			addProperty(activityNode, "type", "activity");
			addProperty(activityNode, "comment", comment);
			addProperty(activityNode, "starttime", "null");
			addProperty(activityNode, "endtime", time);
			addProperty(activityNode, "id", commentId);
			addProperty(activityNode, "revid", revid);
		}else{
			activityNode = new URI(specialActivityNodeUri);
		}
		

		
		String specialUserNodeUri = Neo4jIndex.queryNodeOrRelationship("node", "userNodeIndex", "username", user);
		URI userNode = new URI("");
		if (specialUserNodeUri == null){
			userNode = createNode();
			Neo4jIndex.addNodeOrRelationshipToIndex("node", "userNodeIndex", "username", user, userNode.toString());
			
			addProperty(userNode, "prov:type", "editor");
			addProperty(userNode, "type", "agent");
			addProperty(userNode, "user_name", user);
			addProperty(userNode, "id", user);
		}else{
			userNode = new URI(specialUserNodeUri);
		}


		Map<String,String> property=new HashMap<String,String>();
		
		String relationshipArticleActivityName = revid + "comment";
		String specialRelationshipArticleActivityUri = Neo4jIndex.queryNodeOrRelationship("relationship", "wasGeneratedBy", "relationshipName", relationshipArticleActivityName);
		URI relationshipArticleActivityUri = new URI("");
		if (specialRelationshipArticleActivityUri == null){
			relationshipArticleActivityUri = addRelationship(articleNode, activityNode, "wasGeneratedBy", "{}");
			Neo4jIndex.addNodeOrRelationshipToIndex("relationship", "wasGeneratedBy", "relationshipName", relationshipArticleActivityName, relationshipArticleActivityUri.toString());
			property.clear();
			property.put("time", time);
			property.put("relationshipName", relationshipArticleActivityName);
			property.put("id", relationshipArticleActivityName);
			property.put("entity", revid);
			property.put("activity", commentId);
			addMetadataToProperty(relationshipArticleActivityUri, property);
		}//else{
//			relationshipArticleActivityUri = new URI(specialRelationshipArticleActivityUri);
//		}
		

		
		String relationshipActivityUserName = "comment" + revid + user;
		String specialRelationshipActivityUserUri = Neo4jIndex.queryNodeOrRelationship("relationship", "wasAssociatedWith", "relationshipName", relationshipActivityUserName);
		URI relationshipActivityUserUri = new URI("");
		if (specialRelationshipActivityUserUri == null){
		    relationshipActivityUserUri = addRelationship(activityNode, userNode, "wasAssociatedWith", "{}");
		    Neo4jIndex.addNodeOrRelationshipToIndex("relationship", "wasAssociatedWith", "relationshipName", relationshipActivityUserName, relationshipActivityUserUri.toString());
		    property.clear();
			property.put("user_name", user);
			property.put("activity", commentId);
			property.put("agent", user);
			property.put("publicationpolicy", "null");
			property.put("relationshipName", relationshipActivityUserName);
			property.put("id", relationshipActivityUserName);
			addMetadataToProperty(relationshipActivityUserUri, property);
		}//else{
//			relationshipActivityUserUri = new URI(specialRelationshipActivityUserUri);
//		}
		

		
		if(parentid != "0") 
		{
			specialArticleNodeUri = Neo4jIndex.queryNodeOrRelationship("node", "articleNodeIndex", "revid", parentid);
			URI parentNode = new URI("");
			if(specialArticleNodeUri == null){
				parentNode = createNode();
				Neo4jIndex.addNodeOrRelationshipToIndex("node", "articleNodeIndex", "revid", parentid, parentNode.toString());
				addProperty(parentNode, "revid", parentid);
				addProperty(parentNode, "id", parentid);
				addProperty(parentNode, "title", title);
				addProperty(parentNode, "type", "entity");
				addProperty(parentNode, "prov:type", "article");
				addProperty(parentNode, "pageid", pageid);
			}else{
				parentNode = new URI(specialArticleNodeUri);			
			}
			
	
			
			String relationshipRevisionParentName = revid + parentid;
			String specialRelationshipRevisionParentUri = Neo4jIndex.queryNodeOrRelationship("relationship", "wasRevisionOf", "relationshipName", relationshipRevisionParentName);
			URI relationshipRevParentUri = new URI("");
			if (specialRelationshipRevisionParentUri == null){
				relationshipRevParentUri = addRelationship(articleNode, parentNode, "wasRevisionOf", "{}");
				Neo4jIndex.addNodeOrRelationshipToIndex("relationship", "wasRevisionOf", "relationshipName", relationshipRevisionParentName, relationshipRevParentUri.toString());
				
				property.clear();
				property.put("parentid", parentid);
				property.put("entity1", parentid);
				property.put("revid", revid);
				property.put("entity2", revid);
				property.put("agent", user);
				property.put("relationshipName", relationshipRevisionParentName);
				property.put("id", relationshipRevisionParentName);
				addMetadataToProperty(relationshipRevParentUri, property);
			}//else{
//				relationshipRevParentUri = new URI(specialRelationshipRevisionParentUri);
//			}
			
		}
	}
	
public static void getUserData(String title, String revid, String user, String time, String comment, String size, String pageid) throws URISyntaxException, ClientHandlerException, UniformInterfaceException, JSONException{
		
		checkDatabaseIsRunning();
		
		
		String specialArticleNodeUri = Neo4jIndex.queryNodeOrRelationship("node", "articleNodeIndex", "revid", revid);
		URI articleNode = new URI("");
		if(specialArticleNodeUri == null){
			articleNode = createNode();
			Neo4jIndex.addNodeOrRelationshipToIndex("node", "articleNodeIndex", "revid", revid, articleNode.toString());
		}else{
			articleNode = new URI(specialArticleNodeUri);
		}	
		addProperty(articleNode, "id", revid);
		addProperty(articleNode, "revid", revid);
		addProperty(articleNode, "title", title);
		addProperty(articleNode, "prov:type", "article");
		addProperty(articleNode, "type", "entity");
		addProperty(articleNode, "pageid", pageid);
		addProperty(articleNode, "comment", comment);
		addProperty(articleNode, "time", time);
		addProperty(articleNode, "size", size);
		
		
		String specialActivityNodeUri = Neo4jIndex.queryNodeOrRelationship("node", "activityNodeIndex", "revid", revid);
		URI activityNode = new URI("");
		String commentId = "comment" + revid;
		if(specialActivityNodeUri == null){
			activityNode = createNode();
			Neo4jIndex.addNodeOrRelationshipToIndex("node", "activityNodeIndex", "revid", revid, activityNode.toString());
			addProperty(activityNode, "prov:type", "edit");
			addProperty(activityNode, "type", "activity");
			addProperty(activityNode, "id", commentId);
			addProperty(activityNode, "starttime", "null");
			addProperty(activityNode, "endtime", time);
			addProperty(activityNode, "comment", comment);
			addProperty(activityNode, "revid", revid);
		}else{
			activityNode = new URI(specialActivityNodeUri);
		}
		
		

		
		String specialUserNodeUri = Neo4jIndex.queryNodeOrRelationship("node", "userNodeIndex", "username", user);
		URI userNode = new URI("");
		if (specialUserNodeUri == null){
			userNode = createNode();
			Neo4jIndex.addNodeOrRelationshipToIndex("node", "userNodeIndex", "username", user, userNode.toString());			
			addProperty(userNode, "prov:type", "editor");	
			addProperty(userNode, "type", "agent");
			addProperty(userNode, "user_name", user);
			addProperty(userNode, "id", user);
		}else{
			userNode = new URI(specialUserNodeUri);
		}
		
		
		Map<String,String> property=new HashMap<String,String>();		
		String relationshipArticleActivityName = revid + "comment";
		String specialRelationshipArticleActivityUri = Neo4jIndex.queryNodeOrRelationship("relationship", "wasGeneratedBy", "relationshipName", relationshipArticleActivityName);
		URI relationshipArticleActivityUri = new URI("");
		if (specialRelationshipArticleActivityUri == null){
			relationshipArticleActivityUri = addRelationship(articleNode, activityNode, "wasGeneratedBy", "{}");
			Neo4jIndex.addNodeOrRelationshipToIndex("relationship", "wasGeneratedBy", "relationshipName", relationshipArticleActivityName, relationshipArticleActivityUri.toString());
			
			property.clear();
			property.put("time", time);
			property.put("relationshipName", relationshipArticleActivityName);
			property.put("id", relationshipArticleActivityName);
			property.put("entity", revid);
			property.put("activity", commentId);
			addMetadataToProperty(relationshipArticleActivityUri, property);
		}//else{
//			relationshipArticleActivityUri = new URI(specialRelationshipArticleActivityUri);
//		}	
		
		
	
		String relationshipActivityUserName = "comment" + revid + user;
		String specialRelationshipActivityUserUri = Neo4jIndex.queryNodeOrRelationship("relationship", "wasAssociatedWith", "relationshipName", relationshipActivityUserName);
		URI relationshipActivityUserUri = new URI("");
		if (specialRelationshipActivityUserUri == null){
		    relationshipActivityUserUri = addRelationship(activityNode, userNode, "wasAssociatedWith", "{}");
		    Neo4jIndex.addNodeOrRelationshipToIndex("relationship", "wasAssociatedWith", "relationshipName", relationshipActivityUserName, relationshipActivityUserUri.toString());
		    
		    property.clear();
			property.put("user_name", user);
			property.put("activity", commentId);
			property.put("agent", user);
			property.put("publicationpolicy", "null");
			property.put("relationshipName", relationshipActivityUserName);
			property.put("id", relationshipActivityUserName);
			addMetadataToProperty(relationshipActivityUserUri, property);
		}//else{
//			relationshipActivityUserUri = new URI(specialRelationshipActivityUserUri);
//		}
		
	}
	
	private static URI createNode(){
		
		// http://localhost:7474/db/data/node
		final String nodeEntryPointUri = SERVER_ROOT_URI + "node";
		
		WebResource resource = Client.create().resource(nodeEntryPointUri);
		// POST{} to the node entry point URI
		
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).entity("{}").post(ClientResponse.class);
		
		final URI location = response.getLocation();
		System.out.println("Response = " + response.getStatus());
		System.out.println(String.format("POST to [%s], status code [%d], location header [%s]", nodeEntryPointUri, response.getStatus(),location.toString()));		
		response.close();		
		return location;
	}
	
	
	private static void addProperty(URI nodeUri, String propertyName, String propertyValue){
		// http://localhost:7474/db/data/node/{node_id}/properties/{property_name}
		String propertyUri = nodeUri.toString() + "/properties/" + propertyName;		
		WebResource resource = Client.create().resource(propertyUri);		
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).entity("\"" + propertyValue + "\"").put(ClientResponse.class);		
		//System.out.println(String.format("PUT to [%s], status code [%d]", propertyUri, response.getStatus()));
		response.close();
	}
	
	
	private static URI addRelationship(URI startNode, URI endNode, String relationshipType, String jsonAttributes) throws URISyntaxException{
		URI fromUri = new URI(startNode.toString() + "/relationships");
		String relationshipJson = generateJsonRelationship(endNode, relationshipType, jsonAttributes);
		//System.out.println(relationshipJson + "===================================");
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
	

		
	private static void addMetadataToProperty( URI relationshipUri,
            Map<String,String> property) throws URISyntaxException
    {
        URI propertyUri = new URI( relationshipUri.toString() + "/properties" );
        String entity = toJsonNameValuePairCollection( property );
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
	
	private static String toJsonNameValuePairCollection(Map<String,String> property){
		
		Set<String> keys=property.keySet();
		Iterator<String> iter=keys.iterator();
		String outPut="{ ";
		while(iter.hasNext()){
			String key=iter.next();
			String value=property.get(key);
			
			outPut+="\""+key+"\" : \""+value+"\"";
			if(iter.hasNext()){
				outPut+=", ";
			}
		}
		outPut+="}";		
//		return "{ \"time\" : \"20120113\", \"title\" : \"test_title3\"}";		
		return outPut;
	}
	
	private static void checkDatabaseIsRunning(){
		WebResource resource = Client.create().resource(SERVER_ROOT_URI);
		ClientResponse response = resource.get(ClientResponse.class);		
		//System.out.println(String.format("GET on [%s], status code [%d]",SERVER_ROOT_URI, response.getStatus()));
		response.close();
	}
	

}
