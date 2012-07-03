package testInte;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.client.ClientResponse;

import findVandalism.QuerySpecialNode;

public class Delete {
	
	public static void deleteAllIndex(){
		String deleteArticleNodeIndexUri = "index/node/articleNodeIndex";
		Neo4jIndex.delete(deleteArticleNodeIndexUri);		
		String deleteActivityNodeIndexUri = "index/node/activityNodeIndex";
		Neo4jIndex.delete(deleteActivityNodeIndexUri);		
		String deleteUserNodeIndexUri = "index/node/userNodeIndex";
		Neo4jIndex.delete(deleteUserNodeIndexUri);
		
		String deleteWasRevisionOfIndexUri = "index/relationship/wasRevisionOf";
		Neo4jIndex.delete(deleteWasRevisionOfIndexUri);
		String deleteWasGeneratedByIndexUri = "index/relationship/wasGeneratedBy";
		Neo4jIndex.delete(deleteWasGeneratedByIndexUri);
		String deleteWasAssociatedWithIndexUri = "index/relationship/wasAssociatedWith";
		Neo4jIndex.delete(deleteWasAssociatedWithIndexUri);
	}
	
	
	
	public static void deleteRelationship(){
		String type = "relationship";
		ClientResponse responseWasAssociatedWith = QuerySpecialNode.getWasAssociatedWith();	
		deleteAllNodeOrRelationship(responseWasAssociatedWith, type);
		System.out.println("delete relationship wasAssociatedWith successfully");
		ClientResponse responseWasGeneratedBy = QuerySpecialNode.getWasGeneratedBy();		
		deleteAllNodeOrRelationship(responseWasGeneratedBy, type);
		System.out.println("delete relationship wasGeneratedBy successfully");
		ClientResponse responseWasRevisionOf = QuerySpecialNode.getWasRevisionOf();
		deleteAllNodeOrRelationship(responseWasRevisionOf, type);
		System.out.println("delete relationship wasRevisionOf successfully");
	}
	
	public static void deleteNode(){
		String type = "node";
		ClientResponse responseArticlesresponse = QuerySpecialNode.getAllArticlesInfo();
		deleteAllNodeOrRelationship(responseArticlesresponse, type);
		System.out.println("delete node article successfully");
		ClientResponse responseActivity = QuerySpecialNode.getAllActivityInfo();
		deleteAllNodeOrRelationship(responseActivity, type);
		System.out.println("delete node activity successfully");
		ClientResponse responseUser = QuerySpecialNode.getAllUserInfo();
		deleteAllNodeOrRelationship(responseUser, type);
		System.out.println("delete node user successfully");
	}
	
	public static void deleteAllNodeOrRelationship(ClientResponse response, String type){
		
		try {
			JSONObject json = new JSONObject(response.getEntity(String.class));
			JSONArray getData = json.getJSONArray("data");
					
			for(int count = 0; count < getData.length(); count++){
				JSONArray getNode = getData.getJSONArray(count);
				JSONObject getNodeInfo = getNode.getJSONObject(0);	
				String getNodeUri = getNodeInfo.getString("self");
				
				String[] uriArray=getNodeUri.split("/");
				String nodeNumber=uriArray[6];
				QuerySpecialNode.deleteRelationshipOrNode(type, nodeNumber);
			}
		} catch (Exception e) {
//			System.err.println(e.getMessage());			// these two lines should be commented , otherwise " Index: 0, Size: 0 "
//			System.err.println(e.getStackTrace());		
	}			
				
	}

}
