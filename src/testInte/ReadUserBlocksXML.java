package testInte;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import util.ISO8601;

public class ReadUserBlocksXML {

	public static List<String> startWithUser(String user) throws Exception{
		return startWithUser(user, GregorianCalendar.getInstance());
	
	}

	public static List<String> startWithUser(String user, Calendar atDate) throws Exception{
		user = user.replaceAll(" ", "%20");
		user = user.replaceAll("=", "%3D");
		user = user.replaceAll("[+]", "%2B");
		List<String> userTxt = new ArrayList<String>();
		userTxt = readXMLbyURL("http://en.wikipedia.org/w/api.php?action=query&list=logevents&leaction=block%2Fblock&leprop=ids|title|type|user|userid|timestamp|comment|parsedcomment|details|tags&letype=block&lestart=" + ISO8601.fromCalendar(atDate) + "&leend=2002-09-01T02%3A13%3A55Z&letitle=User:" + user + "&lelimit=10&format=xml", atDate, user);
		//userTxt = readXMLbyURL("http://en.wikipedia.org/w/api.php?action=query&list=blocks&format=xml&bkip="+ user);
		return userTxt;
	}


	public static List<String> readXMLbyURL(String fileName, Calendar atDate, String user) throws Exception {
		SAXBuilder builder = new SAXBuilder();
		//System.err.println(fileName);
		Document doc = builder.build(fileName);
		List<String> userTxt = readXML(doc,atDate, user);
		return userTxt;
	}



	//@matthew_gamble wrote:
	//Don't like how all of this is done by "side-effect" would prefer to pass a storage object to the method with a defined interface that it
	//would then call .getUserData() on. 


	/**
	 * 
	 * @param doc
	 * @return
	 * @throws Exception
	 * 
	 * 


	 */

//	{
//	    "query": {
//	        "logevents": [
//	            {
//	                "logid": 49716540,
//	                "pageid": 0,
//	                "ns": 2,
//	                "title": "User:Quantqusta",
//	                "type": "block",
//	                "action": "block",
//	                "block": {
//	                    "flags": "nocreate",
//	                    "duration": "1 week",
//	                    "expiry": "2013-07-12T09:58:16Z"
//	                },
//	                "user": "Future Perfect at Sunrise",
//	                "userid": "1224855",
//	                "timestamp": "2013-07-05T09:58:16Z",
//	                "comment": "persistent disruptive editing on [[Comfort women]]",
//	                "parsedcomment": "persistent disruptive editing on <a href=\"/wiki/Comfort_women\" title=\"Comfort women\">Comfort women</a>",
//	                "tags": [
//
//	                ]
//	            },
//	
	
	public static List<String> readXML(Document doc,Calendar atDate, String user) throws Exception {

		List<String> userTxt = new ArrayList<String>();
		Element root = doc.getRootElement();
		//System.out.println(root);

		List<Element> query = root.getChildren("query");
		for (Element q : query ){
			//just check the latest block activity
			//TODO it might be an unblock - check this
			Element le = q.getChildren("logevents").get(0).getChild("item");
			if(le != null){
				List<Attribute> a = le.getAttributes();
				if(!a.isEmpty()){
					//title will be User:<userid>
					if(le.getAttributeValue("action").equalsIgnoreCase("unblock"))
						return userTxt;
					
				//	String user  = le.getAttributeValue("title").substring(5);
					String userid = user;					
					String time = le.getAttribute("timestamp")
							.getValue();
					Element block = le.getChild("block");
					String duration = null;		
					String expiry = null;
					if(block != null){
					duration = block.getAttributeValue("duration");		
					expiry = block.getAttributeValue("expiry");
					}
					
					Calendar expiryDate = null;
					if(expiry != null){
						expiryDate = ISO8601.toCalendar(expiry);
					}
					if(ReadXML.isGeneratingPROV()){
						
						if("indefinite".equalsIgnoreCase(duration) || (expiryDate != null && (expiryDate.after(atDate))))
						{
							CreateProv.getUserBlockData(user, userid, time, expiry, true);
							
						}
						else{
							CreateProv.getUserBlockData(user, userid, null, null, false);
						}
					}	
	

					userTxt.add("userid:" + userid + "  user:"
							+ user);

				}
			}else
			{
				CreateProv.getUserBlockData(user, user, null, null, false);
			}
				

		}


		return userTxt;
	}
	
	
	
	
//	public static List<String> readXML(Document doc) throws Exception {
//
//		List<String> userTxt = new ArrayList<String>();
//		Element root = doc.getRootElement();
//		//System.out.println(root);
//
//		List<Element> query = root.getChildren("query");
//		for (Element q : query ){
//			List<Element> blocks = q.getChildren("blocks");
//			for (Element b : blocks) {
//
//				List<Attribute> a = b.getAttributes();
//				if(!a.isEmpty()){
//					String title = "";
//					String userStatus = "";
//
//					String userid = b.getAttribute("userid")
//							.getValue();						
//					String user = b.getAttribute(
//							"user").getValue();
//					String time = b.getAttribute("timestamp")
//							.getValue();
//					String expiry = b.getAttribute("expiry")
//							.getValue();
//					String comment = new String();
//
//
//					if(ReadXML.isGeneratingPROV()){
//						CreateProv.getUserBlockData(user, userid, time, expiry);
//
//					}	
//
//					userTxt.add("userid:" + userid + "  user:"
//							+ user);
//
//				}
//			}
//
//
//		}
//
//
//		return userTxt;
//	}

}
