package testInte;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

import findVandalism.QuerySpecialNode;

public class Statics {
	
//	public static void main(String[] args) throws Exception {
//		String count = listTitleOrGetNumber("count");
//		System.out.println(count);
//	}
	
	public static String getNumberOfRevision(){
		ClientResponse response = QuerySpecialNode.countRevisionNumber();
		String numberOfRevision = new String();
		try {
			JSONObject json = new JSONObject(response.getEntity(String.class));
			if(json.has("data")){
			JSONArray getData = json.getJSONArray("data");
			JSONArray test = new JSONArray();
			if(!getData.isNull(0)){
				test = getData.getJSONArray(0);
				numberOfRevision = test.getString(0);
			}else{
				numberOfRevision = "0";
			}
			}else{numberOfRevision = "0";}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		response.close();
		return numberOfRevision;
	}
	
	
	public static String listTitleOrGetNumber(String flagTitle){
		Set<String> titleArray = getAllTitle();
		Iterator<String> itTitle = titleArray.iterator();
		int count = 0;
		String title = new String();
		while (itTitle.hasNext()) {
		  title = title + itTitle.next()+ "\n" ;		  
		  count++;
		  //System.out.println(count);
		}
		if(flagTitle == "title") {
			return title;
			}else{
				return Integer.toString(count);
			}		
	}
	
	
	public static List<Object[]> listAllTitle(){
		Set<String> titleArray = getAllTitle();
		Iterator<String> itTitle = titleArray.iterator();
		String title = new String();
		List<Object[]> resultList=new ArrayList<Object[]>();
		while (itTitle.hasNext()) {
		  title = itTitle.next();	
		  Object[] tmpArray=new Object[]{title};
		  resultList.add(tmpArray);	
		}
		return resultList;
	}
	
	public static Set<String> getAllTitle(){	
		ClientResponse response = QuerySpecialNode.getAllArticlesInfo();
		//System.out.println(response.getEntity( String.class ));
		Set<String> titleArray = findAllTitle(response);
		return titleArray;	
	}
	
	public static Set<String> findAllTitle(ClientResponse response){
		Set<String> titleArray = new HashSet<String>();
		try {
			JSONObject json = new JSONObject(response.getEntity(String.class));
			JSONArray getData = json.getJSONArray("data");
			//System.out.println(getData.length());
					
			for(int count = 0; count < getData.length(); count++){
				JSONArray getNode;
				getNode = getData.getJSONArray(count);
			
				JSONObject getNodeInfo = getNode.getJSONObject(0);
				JSONObject getNodeData = getNodeInfo.getJSONObject("data");
				String getNodeTitle = getNodeData.getString("title");
				titleArray.add(getNodeTitle);
			}
		} catch (Exception e) {
//			System.err.println(e.getMessage());			// these two lines should be commented , otherwise " Index: 0, Size: 0 "
//			System.err.println(e.getStackTrace());		
		}
		response.close();		
		return titleArray;
	}
	
//===========================Find user name list and number by article title========================================================================================	
	public static Map<String, String> getUserList(String title) throws ClientHandlerException, UniformInterfaceException, JSONException{
		String userList = new String();
		ArrayList<Entry<String, Integer>> userOrderList = findUserList(title);	
		int countUserNumber = 0;
		for(Entry<String,Integer> e : userOrderList) { 
			userList = userList + e.getKey() + "    " + e.getValue() + "\n";
			countUserNumber++;
        }
		
		
		
//		Iterator iter = userMap.entrySet().iterator(); 
//		int countUserNumber = 0;
//		while (iter.hasNext()) { 
//			Map.Entry m=(Map.Entry)iter.next();
//			userList = userList + m.getKey() + ":" + m.getValue() + "\n";
//			countUserNumber++;
//		} 
		Map<String, String> statisticsMap = new HashMap<String, String>();
		statisticsMap.put("userList", userList);
		statisticsMap.put("userNumber", Integer.toString(countUserNumber));
		return statisticsMap;
	}
	
	public static ArrayList<Entry<String, Integer>> findUserList(String title) throws ClientHandlerException, UniformInterfaceException, JSONException{
		Set<String> titleArray = getTitles(title);
		Iterator<String> itTitle = titleArray.iterator();
		Map<String, Integer> userMap = new HashMap<String, Integer>();
		while (itTitle.hasNext()) {
		  String nodeNumber = itTitle.next();
		  String user = QuerySpecialNode.queryUserNode(nodeNumber);		
			if(userMap.containsKey(user) && (user!=null) ){
				Integer countNumber = userMap.get(user);
				countNumber++;
				userMap.put(user, countNumber);
			}else if(user!=null){				
				userMap.put(user, 1);
				}	  
		}
		ArrayList<Entry<String,Integer>> l = Count.order(userMap);
		return l;
	}
	
	
	
	public static Set<String> getTitles(String title){	
		ClientResponse response = QuerySpecialNode.getAllArticlesInfo();
		Set<String> titleArray = IteratorAllRevision(response, title);
		return titleArray;	
	}
	
	public static Set<String> IteratorAllRevision(ClientResponse response, String title){
		Set<String> titleRevisionArray = new HashSet<String>();
		try {
			JSONObject json = new JSONObject(response.getEntity(String.class));
			JSONArray getData = json.getJSONArray("data");
			//System.out.println(getData.length());
					
			for(int count = 0; count < getData.length(); count++){
				JSONArray getNode;
				getNode = getData.getJSONArray(count);
			
				JSONObject getNodeInfo = getNode.getJSONObject(0);
				JSONObject getNodeData = getNodeInfo.getJSONObject("data");
				String getNodeUri = getNodeInfo.getString("self");
				String getNodeTitle = getNodeData.getString("title");
				String[] uriArray=getNodeUri.split("/");
				String NodeNumber=uriArray[6];
				if(getNodeTitle.equals(title)) titleRevisionArray.add(NodeNumber);
				
			}
		} catch (Exception e) {
//			System.err.println(e.getMessage());			// these two lines should be commented , otherwise " Index: 0, Size: 0 "
//			System.err.println(e.getStackTrace());		
		}
		response.close();		
		return titleRevisionArray;
	}

}
