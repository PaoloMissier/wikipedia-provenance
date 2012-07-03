package testInte;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

public class ReadXML {

	static String totalNodeNumber = CountNodeNumber.countAllNodeNumber();
//	public static void main(String[] args) throws Exception {
////		String totalNodeNumber = CountNodeNumber.countAllNodeNumber();
////		System.out.println(totalNodeNumber);		
////		//String title = "Albert Einstein";		
////		String title = "General relativity";
////		String rvlimit = "9";
//////		String title = "Angry Birds";
//////		String rvlimit = "22";
////		String uclimit = "1";
////		int depth = 1;
////		ReadPageXML.startWithPage(title, rvlimit, totalNodeNumber, depth, uclimit);
////		
//////		String user = "Farpointer";
//////		String uclimit = "3";
//////		String rvlimit = "2";
//////		ReadUserXML.startWithUser(user, uclimit, totalNodeNumber,depth, rvlimit);
////		totalNodeNumber = CountNodeNumber.countAllNodeNumber();
////		System.out.println(totalNodeNumber);
//		
//		String user = "WikHead";
//		String vandalism = showVandalismByUser(user);
//		System.out.println(vandalism);
//		
////		String allVandalism = showAllVandalism();
////		System.out.println(allVandalism);
//		
//		//deleteIndex();
//
//	}
	
	public static void queryByArticle(String title, String rvlimit, int depth, String uclimit) throws Exception{
		Neo4jIndex.createIndex();
		ReadPageXML.startWithPage(title, rvlimit, totalNodeNumber, depth, uclimit);
	}
	
	public static void queryByUser(String user, String uclimit, int depth, String rvlimit) throws Exception{
		Neo4jIndex.createIndex();
		ReadUserXML.startWithUser(user, uclimit, totalNodeNumber,depth, rvlimit);
		//startWithUser(user, uclimit, totalNodeNumber, depth, rvlimit);
	}
	
	public static void GetContributionsByUser(String user, String folderPath) throws Exception{
		Neo4jIndex.createIndex();
		GenerateTxt.getUserContribsTxt(user, folderPath);
	}
	
	public static void GetContributionsByUserOffline(String user, String folderPath) throws Exception{
		GenerateTxt.getUserContribsTxtOffline(user, folderPath);
	}
	
	public static void getArticleInfoByTitleOffline(String title, String folderPath) throws Exception{
		GenerateTxt.getArticleRevisionTxtOffline(title, folderPath);
	}
	
	public static List<Object[]> showContrisByUser(String user) throws ClientHandlerException, UniformInterfaceException, JSONException{
//		Map<String, String> userContribsAndCount = GenerateTxt.showContrisByUser(user);
		List<Object[]> objectArray=GenerateTxt.showContrisByUser(user);
//		return userContribsAndCount;
		return objectArray;
	}
	
	public static List<Object[]> showRevisionInfoByTitle(String title) throws ClientHandlerException, UniformInterfaceException, JSONException{
		List<Object[]> articleInfoAndCount=new ArrayList<Object[]>();
		articleInfoAndCount = GenerateTxt.showRevisionsInfoByTitle(title);
		return articleInfoAndCount;
	}
	
	public static void GetVandalism(String folderPath) throws ClientHandlerException, UniformInterfaceException, JSONException, IOException, URISyntaxException{
		GenerateTxt.getVandalismTxt(folderPath);
	}
	
	public static void getVandalismByUser(String folderPath, String user) throws ClientHandlerException, UniformInterfaceException, IOException, JSONException, URISyntaxException{
		GenerateTxt.getVandalismTxtByUser(user, folderPath);
	}
	
	public static void getVandalismByTitle(String folderPath, String title) throws ClientHandlerException, UniformInterfaceException, IOException, JSONException, URISyntaxException{
		GenerateTxt.getVandalismTxtByTitle(title, folderPath);
	}
	
	public static List<Object[]> showAllVandalism() throws ClientHandlerException, UniformInterfaceException, JSONException, URISyntaxException{
		List<Object[]> vandalismAndCount = new ArrayList<Object[]>();
		vandalismAndCount = GenerateTxt.showVandalism("all", null);
		return vandalismAndCount;
	}
	
	public static List<Object[]> showVandalismByUser(String user) throws ClientHandlerException, UniformInterfaceException, JSONException, URISyntaxException{
		List<Object[]> vandalismAndCount = new ArrayList<Object[]>();
		vandalismAndCount = GenerateTxt.showVandalism("user", user);
		return vandalismAndCount;
	}
	
	public static List<Object[]> showVandalismByTitle(String title) throws ClientHandlerException, UniformInterfaceException, JSONException, URISyntaxException{
		List<Object[]> vandalismAndCount = new ArrayList<Object[]>();
		vandalismAndCount = GenerateTxt.showVandalism("title", title);
		return vandalismAndCount;
	}
	
	public static List<Object[]> listTitle(){
		List<Object[]> title = Statics.listAllTitle();
		return title;
	}
	
	public static String countTitle(){
		String countTitle = Statics.listTitleOrGetNumber("count");
		return countTitle;
	}
	
	public static String getTheNumberOfRevision(){
		return Statics.getNumberOfRevision();
	}
	
	public static Map<String, String> getUserListByTitle(String title) throws ClientHandlerException, UniformInterfaceException, JSONException{
		Map<String, String> userStatistics = Statics.getUserList(title);
		return userStatistics;
	}

	
	public static String findNodeNumberByUserName(String user){
		String nodeNumber = QueryNodeNumber.findNodeNumberByUser(user);
		return nodeNumber;
	}
	
	public static ArrayList<String> findUserByRevid(String revid) throws ClientHandlerException, UniformInterfaceException, JSONException{
		ArrayList<String> userList = QueryNodeNumber.findUserInfoByRevid(revid);
		return userList;
	}
	
	public static String findUserLastestInfo(String user) throws ClientHandlerException, UniformInterfaceException, JSONException, IOException{
		String userLatestEdit = QueryNodeNumber.getUserLatestEdit(user);
		return userLatestEdit;
	}
	
	public static String findArticleLatestInfo(String title) throws ClientHandlerException, UniformInterfaceException, JSONException{
		String revisionLatestEdit = QueryNodeNumber.getArticleLatestEdit(title);
		return revisionLatestEdit;
	}
	
	public static String findRevisionInfoByRevid(String revid) throws ClientHandlerException, UniformInterfaceException, JSONException{
		String revInfo = QueryNodeNumber.getRevisonInfoByRevid(revid);
		return revInfo;
	}
	
	public static String findRevisionByTitleAndUser(String title, String user) throws ClientHandlerException, UniformInterfaceException, JSONException{
		String revInfoByTitleAndUser = QueryNodeNumber.getRevisonByTitleAndUser(title, user);
		return revInfoByTitleAndUser;
	}
	
	public static List<Object[]> countTitleUserNumber() throws ClientHandlerException, UniformInterfaceException, JSONException{
		List<Object[]> countTitleNumber = Count.countByTitleToString();
		return countTitleNumber;
	}
	
	public static String countTitleVandalismNumber() throws ClientHandlerException, UniformInterfaceException, JSONException, URISyntaxException{
		String countVandalismNumber = Count.countByVandalismToString();
		return countVandalismNumber;
	}
	
	public static void deleteIndex(){
		Delete.deleteAllIndex();
	}
	
	public static void deleteRelationshipAndNode(){
		Delete.deleteRelationship();
		Delete.deleteNode();
	}
	
	

}
