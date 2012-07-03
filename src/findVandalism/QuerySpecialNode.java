package findVandalism;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class QuerySpecialNode {
	
	private static final String SERVER_ROOT_URI = "http://localhost:7474/db/data/";


//	public static void main(String[] args)  {
//		ClientResponse response = getAllArticlesInfo();
//		System.out.println(response.getEntity(String.class));
//	}
//	public static String queryArticleNode(String revid, String totalNodeNumber) throws ClientHandlerException, UniformInterfaceException, JSONException {
//
//		String cypherPayload = "{\"query\": \"START a=node(" + totalNodeNumber + ") MATCH a-[r:`wasRevisionOf`*0..500]->b where b.revid='" + revid + "' RETURN b\", \"params\":{}}";
//		String articleNodeUri = run(cypherPayload);
//		return articleNodeUri;
//
//	}
	
	public static void deleteRelationshipOrNode(String type, String nodeOrRelationshipNumber){
		final String cypherUri = SERVER_ROOT_URI + "" + type + "/" + nodeOrRelationshipNumber + "";
		WebResource resource = Client.create().resource(cypherUri);
		resource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).delete(ClientResponse.class);		
	}
	
	
	public static List<String> queryArticleNode(String nodeNumber) throws ClientHandlerException, UniformInterfaceException, JSONException {

		String cypherPayload = "{\"query\": \"START a=node(" + nodeNumber + ") RETURN a\", \"params\":{}}";
		List<String> articleInfoArray = getArticleInfo(cypherPayload).get(0);
		return articleInfoArray;

	}
	
//	public static String queryActivityNode(String revid, String totalNodeNumber) throws ClientHandlerException, UniformInterfaceException, JSONException{
//		String cypherPayload = "{\"query\": \"START a=node(" + totalNodeNumber + ") MATCH a-[r:`wasRevisionOf`*0..500]->b-[:`wasGeneratedBy`]->c where c.revid='" + revid + "' RETURN c\", \"params\":{}}";
//		String activityNodeUri = run(cypherPayload);
//		return activityNodeUri;
//	}
	
//	public static String queryUserNode(String user, String totalNodeNumber) throws ClientHandlerException, UniformInterfaceException, JSONException {
//
//		String cypherPayload = "{\"query\": \"START a=node(" + totalNodeNumber + ") MATCH a-[r:`wasRevisionOf`*0..500]->b-[:`wasGeneratedBy`]->c-[:`wasAssociatedWith`]->d where d.user_name='"+ user +"' RETURN d\", \"params\":{}}";
//		String userNodeUri = run(cypherPayload);
//		return userNodeUri;
//
//	}
	
	public static String queryUserNode(String nodeNumber) throws ClientHandlerException, UniformInterfaceException, JSONException {
		
			String cypherPayload = "{\"query\": \"START a=node(" + nodeNumber + ") MATCH a-[:`wasGeneratedBy`]->b-[:`wasAssociatedWith`]->c RETURN c\", \"params\":{}}";
			String user_name = getUserName(cypherPayload);
			return user_name;
	}
	
	public static List<ArrayList> getArticlesInfoByUserNode(String nodeNumber) throws ClientHandlerException, UniformInterfaceException, JSONException {
		
		String cypherPayload = "{\"query\": \"START c=node(" + nodeNumber + ") MATCH a-[:`wasGeneratedBy`]->b-[:`wasAssociatedWith`]->c RETURN a\", \"params\":{}}";
		List<ArrayList> ArticlesInfo = getArticleInfo(cypherPayload);
		return ArticlesInfo;
	}
	
	public static ClientResponse getAllArticlesInfoByUserNode(String nodeNumber) throws ClientHandlerException, UniformInterfaceException, JSONException {		
		String cypherPayload = "{\"query\": \"START c=node(" + nodeNumber + ") MATCH a-[:`wasGeneratedBy`]->b-[:`wasAssociatedWith`]->c RETURN a\", \"params\":{}}";
		ClientResponse response = getGeneralPostResponse(cypherPayload);
		return response;
	}
	
	public static ClientResponse getAllArticlesInfo(){
		//START n=node:favorites('revid : *') RETURN n
		//START n=node:articleNodeIndex('revid : *') RETURN n
		String cypherPayload = "{\"query\": \"START n=node:articleNodeIndex('revid : *') RETURN n\", \"params\":{}}";
		ClientResponse response = getGeneralPostResponse(cypherPayload);
		
		return response;
	}
	
	public static ClientResponse getAllActivityInfo(){
		String cypherPayload = "{\"query\": \"START n=node:activityNodeIndex('revid : *') RETURN n\", \"params\":{}}";
		ClientResponse response = getGeneralPostResponse(cypherPayload);
		return response;
	}
	
	public static ClientResponse getAllUserInfo(){
		String cypherPayload = "{\"query\": \"START n=node:userNodeIndex('username : *') RETURN n\", \"params\":{}}";
		ClientResponse response = getGeneralPostResponse(cypherPayload);
		return response;
	}
	
	public static ClientResponse getWasGeneratedBy(){
		String cypherPayload = "{\"query\": \"START n=relationship:wasGeneratedBy('relationshipName : *') RETURN n\", \"params\":{}}";
		ClientResponse response = getGeneralPostResponse(cypherPayload);
		return response;
	}
	
	public static ClientResponse getWasAssociatedWith(){
		String cypherPayload = "{\"query\": \"START n=relationship:wasAssociatedWith('relationshipName : *') RETURN n\", \"params\":{}}";
		ClientResponse response = getGeneralPostResponse(cypherPayload);
		return response;
	}
	
	public static ClientResponse getWasRevisionOf(){
		String cypherPayload = "{\"query\": \"START n=relationship:wasRevisionOf('relationshipName : *') RETURN n\", \"params\":{}}";
		ClientResponse response = getGeneralPostResponse(cypherPayload);
		return response;
	}
	
	public static String findParentNodeNumber(String nodeNumber){
		String cypherPayload = "{\"query\": \"START a=node(" + nodeNumber + ") MATCH a-[r:`wasRevisionOf`*]->b RETURN b\", \"params\":{}}";
		String parentNodeUri = (String) getArticleInfo(cypherPayload).get(0).get(4);	
		String[] uriArray=parentNodeUri.split("/");
		String parentNodeNumber=uriArray[6];
		return parentNodeNumber;
	}
	
	public static String findChildNodeNumber(String nodeNumber){
		String childNodeNumber = null;
		String cypherPayload = "{\"query\": \"START b=node(" + nodeNumber + ") MATCH a-[r:`wasRevisionOf`*]->b RETURN a\", \"params\":{}}";
		if(!getArticleInfo(cypherPayload).isEmpty()){
			String childNodeUri = (String) getArticleInfo(cypherPayload).get(0).get(4);	
			String[] uriArray=childNodeUri.split("/");
			childNodeNumber=uriArray[6];
		}
		return childNodeNumber;
	}
	
	
	public static String findParent(String nodeNumber){
		//POST http://localhost:7474/db/data/cypher {"query": "START a=node(474) MATCH a-[r:`wasRevisionOf`]->b RETURN b", "params":{}}
		String cypherPayload = "{\"query\": \"START a=node(" + nodeNumber + ") MATCH a-[r:`wasRevisionOf`*]->b RETURN b\", \"params\":{}}";
		String getNodesize = getSpecialNodeSize(cypherPayload);
		return getNodesize;
	}
	
	public static String findChild(String nodeNumber){
		//POST http://localhost:7474/db/data/cypher {"query": "START a=node(474) MATCH b-[r:`wasRevisionOf`]->a RETURN b", "params":{}}
		String cypherPayload = "{\"query\": \"START a=node(" + nodeNumber + ") MATCH b-[r:`wasRevisionOf`*]->a RETURN b\", \"params\":{}}";
		String getNodesize = getSpecialNodeSize(cypherPayload);
		return getNodesize;
	}
	
	public static ClientResponse countRevisionNumber(){
		String cypherPayload = "{\"query\": \"START n=node:articleNodeIndex('revid : *') RETURN count(n)\", \"params\":{}}";
		ClientResponse response = getGeneralPostResponse(cypherPayload);
		return response;
	}
	
	public static ClientResponse getGeneralPostResponse(String cypherPayload){
		final String cypherUri = SERVER_ROOT_URI + "cypher";
		WebResource resource = Client.create().resource(cypherUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).entity(cypherPayload)
				.post(ClientResponse.class);
		return response;
	}
	
	public static String getSpecialNodeSize(String cypherPayload){
		String getNodesize = null;
		ClientResponse response = getGeneralPostResponse(cypherPayload);

		try {
			JSONObject json = new JSONObject(response.getEntity(String.class));
			JSONArray getData = json.getJSONArray("data");
			JSONArray test= new JSONArray();
			if(!getData.isNull(0)){
			test = getData.getJSONArray(0);
			JSONObject  getDataObject = test.getJSONObject(0);
			JSONObject getNodeData = getDataObject.getJSONObject("data");
			if(getNodeData.has("size"))
			getNodesize = getNodeData.getString("size");
			}	
		} catch (Exception e) {
				System.err.println(e.getMessage());
		}
		response.close();		
		return getNodesize;
	}


	private static String getUserName(String cypherPayload) throws ClientHandlerException, UniformInterfaceException, JSONException {
		String user_name = null;
		ClientResponse response = getGeneralPostResponse(cypherPayload);
		try {
			JSONObject json = new JSONObject(response.getEntity(String.class));
			if(json.has("data")){
			JSONArray getData = json.getJSONArray("data");
			if(!getData.isNull(0)){
			JSONArray test = getData.getJSONArray(0);
			JSONObject getDataObject = test.getJSONObject(0);
			JSONObject getNodeData = getDataObject.getJSONObject("data");
			user_name = getNodeData.getString("user_name");
			}
			}
		
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		response.close();
		return user_name;
	}
	
	
	public static List<ArrayList> getArticleInfo(String cypherPayload){
		ClientResponse response = getGeneralPostResponse(cypherPayload);
		
		List<ArrayList> articleArray = new ArrayList<ArrayList>();

		try {
			JSONObject json = new JSONObject(response.getEntity(String.class));
			if(json.has("data")){
			JSONArray getData = json.getJSONArray("data");
			
			for (int i = 0; i < getData.length(); i++){
				
			ArrayList<String> articleInfoArray = new ArrayList<String>();
				
			JSONArray test= new JSONArray();
			if(!getData.isNull(0)){
			test = getData.getJSONArray(i);
			JSONObject  getDataObject = test.getJSONObject(0);
			JSONObject getNodeData = getDataObject.getJSONObject("data");
			String getNodeUri = getDataObject.getString("self");
			
			String getNodeTitle = getNodeData.getString("title");
			String getNodeRevid = getNodeData.getString("revid");
			String getNodeTime = null;
			if(getNodeData.has("time")){
			getNodeTime = getNodeData.getString("time");
			}
			String getNodeComment = null;
			if(getNodeData.has("comment")){
			getNodeComment = getNodeData.getString("comment");
			}
			String getNodeSize = null;
			if(getNodeData.has("size")){
				getNodeSize = getNodeData.getString("size");   //add get size
			}
			
			articleInfoArray.add(getNodeTitle);
			articleInfoArray.add(getNodeRevid);
			articleInfoArray.add(getNodeTime);
			articleInfoArray.add(getNodeComment);
			articleInfoArray.add(getNodeUri);
			articleInfoArray.add(getNodeSize);
			articleArray.add(articleInfoArray);
			}
			}	
			}
		} catch (Exception e) {
				System.err.println(e.getMessage());
		}
		response.close();
		
		
		return articleArray;
	}


}
