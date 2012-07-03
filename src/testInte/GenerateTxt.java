package testInte;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

import findVandalism.FindVandalism;
import findVandalism.QuerySpecialNode;

public class GenerateTxt {
	
//	public static void main(String[] args) throws Exception {
//		String allVandalism = new String(); 
//		List<Object[]> vandalismAndCount = showVandalism("all", null);
//		//allVandalism = vandalismAndCount.get("vandalismText");
//		System.out.println(vandalismAndCount);
//	}
	
	public static void getUserContribsTxt(String user, String folderPath) throws Exception{
		String totalNodeNumber = "300";
		int depth = 1;
		String uclimit = "max";
		String rvlimit = "1";
		String userFileName = "/" + user + ".txt ";
		String src = folderPath + userFileName;
		generateUserContribsTxt(user, totalNodeNumber, depth, uclimit, rvlimit, src);
	}
	
	public static void generateUserContribsTxt(String user, String totalNodeNumber, int depth, String uclimit, String rvlimit, String src) throws Exception {
		
		 List<String> userTxt = ReadUserXML.startWithUser(user, uclimit, totalNodeNumber, depth, rvlimit);
		
		 File f = new File(src);
		 
		 BufferedWriter bw = null;
		 bw = new BufferedWriter(new FileWriter(f));
		 Iterator<String> getUserTxt = userTxt.iterator();
		 while(getUserTxt.hasNext()){
		 bw.append(getUserTxt.next());
		 }
		 bw.flush();
		 bw.close();
	}
	
	public static void getUserContribsTxtOffline(String user, String folderPath) throws ClientHandlerException, UniformInterfaceException, JSONException, IOException{
		String specialUserNodeUri = Neo4jIndex.queryNodeOrRelationship("node", "userNodeIndex", "username", user);
		String[] uriArray=specialUserNodeUri.split("/");
		String userNodeNumber=uriArray[6];
		List<ArrayList> ArticlesInfo = QuerySpecialNode.getArticlesInfoByUserNode(userNodeNumber);
		String src =folderPath +  "/" + user + ".txt ";
		
		generateUserContribsTxtOffline(src, ArticlesInfo);		
	}
	
	public static void generateUserContribsTxtOffline(String src, List<ArrayList> ArticlesInfo) throws IOException{
		File f = new File(src);	 
		BufferedWriter bw = null;
		bw = new BufferedWriter(new FileWriter(f));
		for(int i = 0 ; i < ArticlesInfo.size(); i++){
				bw.append("title:" + ArticlesInfo.get(i).get(0) + " ");
				bw.append("revid:" + ArticlesInfo.get(i).get(1) + " ");
				bw.append("time:" + ArticlesInfo.get(i).get(2) + " ");
				String articleNumberUri = (String) ArticlesInfo.get(i).get(4);
				String[] uriArray=articleNumberUri.split("/");
				String NodeNumber=uriArray[6];
				bw.append("nodeNumber:"+ NodeNumber + "\n");
		}
		bw.flush();
		bw.close();
		
	}
	
	public static List<Object[]> showContrisByUser(String user) throws ClientHandlerException, UniformInterfaceException, JSONException{
		String specialUserNodeUri = Neo4jIndex.queryNodeOrRelationship("node", "userNodeIndex", "username", user);
		String[] uriArray=specialUserNodeUri.split("/");
		String userNodeNumber=uriArray[6];
		List<ArrayList> ArticlesInfo = QuerySpecialNode.getArticlesInfoByUserNode(userNodeNumber);
		
//		Map<String, String> userContribsAndCount = new HashMap<String, String>();
//		String userContris = new String();
//		String count = Integer.toString(ArticlesInfo.size());
		
		
		List<Object[]> resultList=new ArrayList<Object[]>();
		for(int i = 0 ; i < ArticlesInfo.size(); i++){
//			userContris = userContris + "title:" + ArticlesInfo.get(i).get(0) + " ";
//			userContris = userContris +"revid:" + ArticlesInfo.get(i).get(1) + " ";
//			userContris = userContris +"time:" + ArticlesInfo.get(i).get(2) + " ";
			String articleNumberUri = (String) ArticlesInfo.get(i).get(4);
			String[] articleUriArray=articleNumberUri.split("/");
			String NodeNumber=articleUriArray[6];
//			userContris = userContris +"nodeNumber:"+ NodeNumber + "\n";
			
			String title=(String) ArticlesInfo.get(i).get(0);
			String revid=(String) ArticlesInfo.get(i).get(1);
			String time=(String) ArticlesInfo.get(i).get(2);
			Object[] tmpArray=new Object[]{title,revid,time,NodeNumber};
			resultList.add(tmpArray);
		}
//		userContribsAndCount.put("userContribs", userContris);
//		userContribsAndCount.put("userCount", count);
//		return userContribsAndCount;
		
		return resultList;
	}
	
	public static void getArticleRevisionTxtOffline(String title, String folderPath) throws ClientHandlerException, UniformInterfaceException, JSONException, IOException{
		List<List> articlesInfo= new ArrayList<List>();
		Set<String> revisionArray = Statics.getTitles(title);
		Iterator<String> it = revisionArray.iterator();
		while (it.hasNext()) {
			  String articleNodeNumber = it.next();
			  List<String> revisionsInfo = QuerySpecialNode.queryArticleNode(articleNodeNumber);
			  articlesInfo.add(revisionsInfo);
			}
		String src =folderPath +  "/" + title + ".txt ";		
		generateArticleRevisionTxtOffline(src, articlesInfo);		
	}
	
	public static void generateArticleRevisionTxtOffline(String src, List<List> ArticlesInfo) throws IOException{
		File f = new File(src);	 
		BufferedWriter bw = null;
		bw = new BufferedWriter(new FileWriter(f));
		for(int i = 0 ; i < ArticlesInfo.size(); i++){
				bw.append("title:" + ArticlesInfo.get(i).get(0) + " ");
				bw.append("revid:" + ArticlesInfo.get(i).get(1) + " ");
				bw.append("time:" + ArticlesInfo.get(i).get(2) + " ");
				bw.append("comment:" + ArticlesInfo.get(i).get(3) + " ");
				String articleNumberUri = (String) ArticlesInfo.get(i).get(4);
				String[] uriArray=articleNumberUri.split("/");
				String NodeNumber=uriArray[6];
				bw.append("nodeNumber:"+ NodeNumber + "\n");
		}
		bw.flush();
		bw.close();
		
	}
	
	public static List<Object[]> showRevisionsInfoByTitle(String title) throws ClientHandlerException, UniformInterfaceException, JSONException{
		List<List> articlesInfo= new ArrayList<List>();
		Set<String> revisionArray = Statics.getTitles(title);
		Iterator<String> it = revisionArray.iterator();
		while (it.hasNext()) {
			  String articleNodeNumber = it.next();
			  List<String> revisionsInfo = QuerySpecialNode.queryArticleNode(articleNodeNumber);
			  articlesInfo.add(revisionsInfo);
			}
		
//		Map<String, String> articleInfoAndCount = new HashMap<String, String>();
//		String articleInfo = new String();
//		String count = Integer.toString(articlesInfo.size());
		
		List<Object[]> resultList=new ArrayList<Object[]>();
		for(int i = 0 ; i < articlesInfo.size(); i++){
			//String title = (String) articlesInfo.get(i).get(0) ;
			String revid = (String) articlesInfo.get(i).get(1);
			String time = (String) articlesInfo.get(i).get(2);
			String comment = (String) articlesInfo.get(i).get(3);
			String articleNumberUri = (String) articlesInfo.get(i).get(4);
			String[] uriArray=articleNumberUri.split("/");
			String nodeNumber=uriArray[6];
			
			Object[] tmpArray=new Object[]{title, revid, time, comment, nodeNumber};
			resultList.add(tmpArray);

		}
		
//		articleInfoAndCount.put("articleInfo", articleInfo);
//		articleInfoAndCount.put("articleCount", count);
		return resultList;
	}
	
	
	
	public static void getVandalismTxt(String folderPath) throws ClientHandlerException, UniformInterfaceException, JSONException, IOException, URISyntaxException{
		String src =folderPath +  "/vandalism.txt ";
		generateVandalismTxt(src, null, "all");		
	}
	
	public static void getVandalismTxtByUser(String user, String folderPath) throws IOException, ClientHandlerException, UniformInterfaceException, JSONException, URISyntaxException{
		String src = folderPath + "/vandalism" + user + ".txt";
		try {
			generateVandalismTxt(src, user, "user");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void getVandalismTxtByTitle(String title, String folderPath) throws IOException, ClientHandlerException, UniformInterfaceException, JSONException, URISyntaxException{
		String src = folderPath + "/vandalism" + title + ".txt";
		try {
			generateVandalismTxt(src, title, "title");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void generateVandalismTxt(String src, String name, String flag) throws ClientHandlerException, UniformInterfaceException, JSONException, IOException, URISyntaxException{
		File f = new File(src);	 
		BufferedWriter bw = null;
		bw = new BufferedWriter(new FileWriter(f));
		
		Set<String> nodeNumberArray = new HashSet<String>();
		if(flag == "all"){
			 nodeNumberArray = FindVandalism.getVandalismArticleNumber();
		}else if(flag == "user"){
			nodeNumberArray = FindVandalism.getVandalismArticleNumberByUser(name);
		}else{
			nodeNumberArray = FindVandalism.getVandalismArticleNumberByTitle(name);
		}
		for(String nodeNumber:nodeNumberArray){
			List<String> articleInfoArray = QuerySpecialNode.queryArticleNode(nodeNumber);
				for(int i=0; i<3; i++){
					bw.append(articleInfoArray.get(i) + " ");
					System.out.print(articleInfoArray.get(i) + " ");
				}
				bw.append("nodeNumber:" + nodeNumber+ " ");
				bw.append("userName:" + QuerySpecialNode.queryUserNode(nodeNumber) + "\n");
				System.out.print("nodeNumber:" + nodeNumber+ " ");
				System.out.println("userName:" + QuerySpecialNode.queryUserNode(nodeNumber));
		}
		bw.flush();
		bw.close();
	}
	
	public static List<Object[]> showVandalism(String flag, String name) throws ClientHandlerException, UniformInterfaceException, JSONException, URISyntaxException{
		//String vandalismText = new String();
		Set<String> nodeNumberArray = new HashSet<String>();
		if(flag == "all"){
			 nodeNumberArray = FindVandalism.getVandalismArticleNumber();
		}else if(flag == "user"){
			nodeNumberArray = FindVandalism.getVandalismArticleNumberByUser(name);
		}else{
			nodeNumberArray = FindVandalism.getVandalismArticleNumberByTitle(name);
		}
	//	int count = 0;
	//	Map<String, String> vandalismAndCount = new HashMap<String, String>();
		List<Object[]> resultList=new ArrayList<Object[]>();
		for(String nodeNumber:nodeNumberArray){
			
			List<String> articleInfoArray = QuerySpecialNode.queryArticleNode(nodeNumber);
			String title= articleInfoArray.get(0);
			String revid= articleInfoArray.get(1);
			String time= articleInfoArray.get(2);
			//String novandalismText = vandalismText + "nodeNumber:" + nodeNumber+ " ";
			String userName =QuerySpecialNode.queryUserNode(nodeNumber);
		//		count++;
			Object[] tmpArray=new Object[]{title, revid, time,nodeNumber, userName};
			resultList.add(tmpArray);
		}
		
//		vandalismAndCount.put("vandalismText", vandalismText);
//		vandalismAndCount.put("count", Integer.toString(count));
		return resultList;
	}
	
	public static Map<String, String> showVandalismTxt(String flag, String name) throws ClientHandlerException, UniformInterfaceException, JSONException, URISyntaxException{
		String vandalismText = new String();
		Set<String> nodeNumberArray = new HashSet<String>();
		if(flag == "all"){
			 nodeNumberArray = FindVandalism.getVandalismArticleNumber();
		}else if(flag == "user"){
			nodeNumberArray = FindVandalism.getVandalismArticleNumberByUser(name);
		}else{
			nodeNumberArray = FindVandalism.getVandalismArticleNumberByTitle(name);
		}
		int count = 0;
		Map<String, String> vandalismAndCount = new HashMap<String, String>();
		for(String nodeNumber:nodeNumberArray){
			
			List<String> articleInfoArray = QuerySpecialNode.queryArticleNode(nodeNumber);
				for(int i=0; i<3; i++){
					vandalismText = vandalismText + articleInfoArray.get(i) + " ";
				}
				vandalismText = vandalismText + "nodeNumber:" + nodeNumber+ " ";
				vandalismText = vandalismText + "userName:" + QuerySpecialNode.queryUserNode(nodeNumber) + "\n";
				count++;
		}
		
		vandalismAndCount.put("vandalismText", vandalismText);
		vandalismAndCount.put("count", Integer.toString(count));
		return vandalismAndCount;
	}

}
