package testInte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

import findVandalism.QuerySpecialNode;

public class QueryNodeNumber {
	
	public static String findNodeNumberByUser(String user){
		String specialUserNodeUri = Neo4jIndex.queryNodeOrRelationship("node", "userNodeIndex", "username", user);
		return splitNodeUri(specialUserNodeUri);		
	}
	
	public static ArrayList<String> findUserInfoByRevid(String revid) throws ClientHandlerException, UniformInterfaceException, JSONException{
		String specialArticleNodeUri = Neo4jIndex.queryNodeOrRelationship("node", "articleNodeIndex", "revid", revid);
		String revidNodeNumber=splitNodeUri(specialArticleNodeUri);
		String user = QuerySpecialNode.queryUserNode(revidNodeNumber);
		ArrayList<String> list = new ArrayList<String>();
		if(user != null){
		String specialUserNodeUri = Neo4jIndex.queryNodeOrRelationship("node", "userNodeIndex", "username", user);
		String userNodeNumber = splitNodeUri(specialUserNodeUri);		
		list.add(userNodeNumber);
		list.add(user);
		}
		return list;
	
	}
	
	public static String splitNodeUri(String specialNodeUri){
		String nodeNumber = new String();
		if(specialNodeUri != null){
			String[] uriArray=specialNodeUri.split("/");
			nodeNumber=uriArray[6];
			}
		return nodeNumber;				
	}
	
	public static String getUserLatestEdit(String user) throws ClientHandlerException, UniformInterfaceException, JSONException, IOException{
		String specialUserNodeUri = Neo4jIndex.queryNodeOrRelationship("node", "userNodeIndex", "username", user);
		String userNodeNumber=splitNodeUri(specialUserNodeUri);
		String userLatesetEditInfo = new String();
		int revid = 0;
		int count = 0;
		List<ArrayList> ArticlesInfo = QuerySpecialNode.getArticlesInfoByUserNode(userNodeNumber);
		for(int i = 0 ; i < ArticlesInfo.size(); i++){
			String tmp = (String) ArticlesInfo.get(i).get(1);
			int tmpRevid = Integer.parseInt(tmp);
			if(tmpRevid > revid){
				revid = tmpRevid;
				count = i;
			}
		}
		if(revid != 0){
			userLatesetEditInfo =  "title:" + ArticlesInfo.get(count).get(0) + " " +
								   "revid:" + ArticlesInfo.get(count).get(1) + " " +
			                       "time:" + ArticlesInfo.get(count).get(2) + " " +
			                       "comment:" + ArticlesInfo.get(count).get(3) + " " ;
			String articleNumberUri = (String) ArticlesInfo.get(count).get(4);
			String articleNodeNumber=splitNodeUri(articleNumberUri);
			userLatesetEditInfo = userLatesetEditInfo + "articleNodeNumber:"+ articleNodeNumber;
			}
			//userLatesetEditInfo = title + articleRevid + time + comment + nodeNumber;
		return userLatesetEditInfo;
		}
	
	public static String getArticleLatestEdit(String title) throws ClientHandlerException, UniformInterfaceException, JSONException{
		Set<String> revisionArray = Statics.getTitles(title);
		Iterator<String> it = revisionArray.iterator();
		String revisonLatesetEditInfo = new String();
		int revid = 0;
		String nodeNumber = new String();
		while (it.hasNext()) {
			  String articleNodeNumber = it.next();
			  List<String> revisionsInfo = QuerySpecialNode.queryArticleNode(articleNodeNumber);
			  int tmpRevid =  Integer.parseInt(revisionsInfo.get(1));
			  if(tmpRevid > revid){
					revid = tmpRevid;
					nodeNumber = articleNodeNumber;
				}
			}
		if(!nodeNumber.equals("")){
			List<String> latestEditRevisionInfo = QuerySpecialNode.queryArticleNode(nodeNumber);			
			revisonLatesetEditInfo =  "title:" + latestEditRevisionInfo.get(0) + " " +
					   				  "revid:" + latestEditRevisionInfo.get(1) + " " +
					   				  "time:" + latestEditRevisionInfo.get(2) + " " +
					   				  "comment:" + latestEditRevisionInfo.get(3) + " " + 
					   				  "articleNodeNumber:"+ nodeNumber;	
		}
		return revisonLatesetEditInfo;		
	}
	
	public static String getRevisonInfoByRevid(String revid) throws ClientHandlerException, UniformInterfaceException, JSONException{
		String specialArticleNodeUri = Neo4jIndex.queryNodeOrRelationship("node", "articleNodeIndex", "revid", revid);
		String revidNodeNumber=splitNodeUri(specialArticleNodeUri);
		String revisonInfo = new String();
		if(!revidNodeNumber.equals("")){
		List<String> revisionArticleInfo = QuerySpecialNode.queryArticleNode(revidNodeNumber);			
		revisonInfo =  "title:" + revisionArticleInfo.get(0) + " " +
				   				  "revid:" + revisionArticleInfo.get(1) + " " +
				   				  "time:" + revisionArticleInfo.get(2) + " " +
				   				  "comment:" + revisionArticleInfo.get(3) + " " + 
				   				  "articleNodeNumber:"+ revidNodeNumber;	
		}
		return revisonInfo;
	}
	
	public static String getRevisonByTitleAndUser(String title, String user) throws ClientHandlerException, UniformInterfaceException, JSONException{
		String specialUserNodeUri = Neo4jIndex.queryNodeOrRelationship("node", "userNodeIndex", "username", user);
		String userNodeNumber=splitNodeUri(specialUserNodeUri);
		List<ArrayList> ArticlesInfo = QuerySpecialNode.getArticlesInfoByUserNode(userNodeNumber);
		String revisonByTitleAndUserInfo = new String();
		for(int i = 0 ; i < ArticlesInfo.size(); i++){
			String tmpTitle = (String) ArticlesInfo.get(i).get(0);
			if(tmpTitle.equals(title)){
				revisonByTitleAndUserInfo = "title:" + ArticlesInfo.get(i).get(0) + " " +
						   					"revid:" + ArticlesInfo.get(i).get(1) + " " +
						   					"time:" + ArticlesInfo.get(i).get(2) + " " +
						   					"comment:" + ArticlesInfo.get(i).get(3) + " " ;
				String articleNumberUri = (String) ArticlesInfo.get(i).get(4);
				String articleNodeNumber=splitNodeUri(articleNumberUri);
				revisonByTitleAndUserInfo = revisonByTitleAndUserInfo + "articleNodeNumber:"+ articleNodeNumber + "\n";
			}
		}
		return revisonByTitleAndUserInfo;
	}
	
	

	

}
