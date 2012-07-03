package findVandalism;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import testInte.Neo4jIndex;
import testInte.Statics;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

public class FindVandalism {
	
	public static void main(String[] args) throws ClientHandlerException, UniformInterfaceException, JSONException, URISyntaxException {
		System.out.println("start...");
		Set<String> nodeNumberArray = getVandalismArticleNumber();
		
//		String user = "Green Tree 1000";
//		Set<String> nodeNumberArray = getVandalismArticleNumberByUser(user);
		
//		String title = "God";
//		Set<String> nodeNumberArray = getVandalismArticleNumberByTitle(title);
//		
//		Set<String> nodeNumberArray = getVandalismArticleNumber();
//		System.out.println(nodeNumberArray.size());
		
		for(String nodeNumber:nodeNumberArray){
			List<String> articleInfoArray = QuerySpecialNode.queryArticleNode(nodeNumber);
			for(int i=0; i<3; i++){
				System.out.print(articleInfoArray.get(i) + " ");
			}			
			System.out.print("nodeNumber:" + nodeNumber+ " ");
			System.out.println("userName:" + QuerySpecialNode.queryUserNode(nodeNumber));
		}
		System.out.println("finish...");
		
		
	}
	
	public static Set<String> getVandalismArticleNumberByUser(String user) throws ClientHandlerException, UniformInterfaceException, JSONException, URISyntaxException{
		String specialUserNodeUri = Neo4jIndex.queryNodeOrRelationship("node", "userNodeIndex", "username", user);
		String flagKind = "user";
		Set<String> nodeNumberArray = new HashSet<String>();
		if (specialUserNodeUri != null){
		String[] uriArray=specialUserNodeUri.split("/");
		String userNodeNumber=uriArray[6];	
		ClientResponse response = QuerySpecialNode.getAllArticlesInfoByUserNode(userNodeNumber);
		//System.out.println(response.getEntity(String.class));
		nodeNumberArray = runVandalismArticleNumber(response, flagKind);	
		//System.out.println(nodeNumberArray.toString());
		}
		return nodeNumberArray;		
	}
	
	
	public static Set<String> getVandalismArticleNumber(){	
		String flagKind = "all";
		ClientResponse response = QuerySpecialNode.getAllArticlesInfo();
		Set<String> nodeNumberArray = runVandalismArticleNumber(response,flagKind);
		return nodeNumberArray;	
	}
	
	public static Set<String> getVandalismArticleNumberByTitle(String title) throws ClientHandlerException, UniformInterfaceException, JSONException{	
		List<List> articlesInfo= new ArrayList<List>();
		Set<String> revisionArray = Statics.getTitles(title);
		Iterator<String> it = revisionArray.iterator();
		while (it.hasNext()) {
			  String articleNodeNumber = it.next();
			  //System.out.println(articleNodeNumber);
			  List<String> revisionsInfo = QuerySpecialNode.queryArticleNode(articleNodeNumber);
			  articlesInfo.add(revisionsInfo);
			}
		Set<String> nodeNumberArray = runVandalismByTitle(articlesInfo);
		return nodeNumberArray;
	}
	
	public static Set<String> runVandalismByTitle(List<List> articlesInfo) throws ClientHandlerException, UniformInterfaceException, JSONException{
		Set<String> nodeNumberArray = new HashSet<String>();
		
		for(int i = 0 ; i < articlesInfo.size(); i++){
			String size = null;
			String articleNumberUri = (String) articlesInfo.get(i).get(4);
			String[] uriArray=articleNumberUri.split("/");
			String nodeNumber=uriArray[6];
			size = (String) articlesInfo.get(i).get(5);
					
			if(size != null) {
				boolean vandalism = compareSize(nodeNumber, size);
				if (vandalism){
					nodeNumberArray.add(nodeNumber);
				}else{
					String parentNodeNumber = null;
					String childNode = QuerySpecialNode.findChildNodeNumber(nodeNumber);
					if(childNode != null){
					
						parentNodeNumber = checkSpecialString(childNode);
						if(parentNodeNumber != null) nodeNumberArray.add(parentNodeNumber);		
					}		
				}					
			}
		}
		return nodeNumberArray;	
	}
	
	public static Set<String> runVandalismArticleNumber(ClientResponse response, String flagKind){		
		Set<String> nodeNumberArray = new HashSet<String>();
		//List<String> nodeNumberArray = new ArrayList<String>();
		try {
			JSONObject json = new JSONObject(response.getEntity(String.class));
			JSONArray getData = json.getJSONArray("data");
			//System.err.println(getData.length());
					
			for(int count = 0; count < getData.length(); count++){
				String getNodesize = null;
				JSONArray getNode = getData.getJSONArray(count);
				JSONObject getNodeInfo = getNode.getJSONObject(0);
				JSONObject getNodeData = getNodeInfo.getJSONObject("data");
				if(getNodeData.has("size"))
					getNodesize = getNodeData.getString("size");			
				String getNodeUri = getNodeInfo.getString("self");
				
				String[] uriArray=getNodeUri.split("/");
				String nodeNumber=uriArray[6];
				//System.out.println("===================================");
				//System.out.println("    size:" + getNodesize+ "    nodeNumber:" + nodeNumber);
				if(getNodesize != null) {
					boolean vandalism = compareSize(nodeNumber, getNodesize);
					if (vandalism){
					//	System.out.println(nodeNumber);
						nodeNumberArray.add(nodeNumber);
					}else{
						String parentNodeNumber = null;
						//if (flagKind == "user"){
						String childNodeNumber = QuerySpecialNode.findChildNodeNumber(nodeNumber);
						//}
						if(childNodeNumber != null) parentNodeNumber = checkSpecialString(childNodeNumber);
						if(parentNodeNumber != null) nodeNumberArray.add(nodeNumber);
					}					
				}
			}
			
		} catch (Exception e) {
				System.err.println(e.getMessage());			// these two lines should be commented , otherwise " Index: 0, Size: 0 "
				System.err.println(e.getStackTrace());		
				e.printStackTrace();
		}
		response.close();
		return nodeNumberArray;
	}
	
	
	public static boolean compareSize(String nodeNumber, String size){
		int countSize = 0;
		boolean vandalism = false;
		String parentSize = QuerySpecialNode.findParent(nodeNumber);
		if (parentSize != null){
			countSize = Integer.parseInt(parentSize) - Integer.parseInt(size);
			if(countSize > 2000){
				String childSize = QuerySpecialNode.findChild(nodeNumber);
				if (childSize != null){
					countSize = Integer.parseInt(childSize) - Integer.parseInt(size);
					if (countSize>2000) vandalism = true;
				}
			}
		}
		return vandalism;		
	}
	
	
	public static String checkSpecialString(String nodeNumber) throws ClientHandlerException, UniformInterfaceException, JSONException{
		boolean vandalism = false;
		boolean rvv = false;
		String parentNodeNumber = null;
		String articleComment = new String();
		List<String> articleInfoArray = QuerySpecialNode.queryArticleNode(nodeNumber);
		String articleTitle = articleInfoArray.get(0);
		try{
		articleComment = articleInfoArray.get(3);
		}catch (Exception e) {
			articleComment = "null";
		}
		if(articleTitle != "Vandalism"){
			try{
			vandalism = articleComment.contains("vandalism");
			rvv = articleComment.contains("rvv");
			}catch (Exception e) {
				//e.printStackTrace();
			}
			if(vandalism||rvv){
				parentNodeNumber = QuerySpecialNode.findParentNodeNumber(nodeNumber);
			}
		}		
		return parentNodeNumber;
	}
	
	
	
}
