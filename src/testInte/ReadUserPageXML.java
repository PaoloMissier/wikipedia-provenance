package testInte;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class ReadUserPageXML {

	/**
	 * @param args
	 * @throws Exception 
	 */

	//	public static void main(String[] args) throws Exception {
	//		String totalNodeNumber = CountNodeNumber.countAllNodeNumber();
	//
	//		//readXMLbyFileName("d:\\user.xml", totalNodeNumber);
	//		
	//		//readXMLbyURL("http://en.wikipedia.org/w/api.php?action=query&list=usercontribs&ucuser=Farpointer&uclimit=5&format=xml", nodeNumber);
	//		String user = "Farpointer";
	//		String uclimit = "5";
	//		String rvlimit = "3";
	//		int depth = 3;
	//		startWithUser(user, uclimit, totalNodeNumber, depth, rvlimit);
	//
	//	}

	public static List<String> startWithUser(String user) throws Exception{
		//System.err.println("Read User Page - startWithUser:" + user);
		user = user.replaceAll(" ", "%20");
		user = user.replaceAll("=", "%3D");
		user = user.replaceAll("[+]", "%2B");
		List<String> userTxt = new ArrayList<String>();
		userTxt = readXMLbyURL("http://en.wikipedia.org/w/api.php?action=query&list=users&usprop=groups&ususers="+ user + "&format=xml");
		return userTxt;
	}


	public static List<String> readXMLbyFileName(String fileName, String totalNodeNumber) throws Exception {
		SAXBuilder builder = new SAXBuilder();

		File xmlFile = new File(fileName);
		Document doc = (Document) builder.build(xmlFile);
		List<String> userTxt = readXML(doc);
		return userTxt;
	}

	public static List<String> readXMLbyURL(String fileName) throws Exception {
		SAXBuilder builder = new SAXBuilder();

		Document doc = builder.build(fileName);
		List<String> userTxt = readXML(doc);
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
<query>
	<users>
		<user userid="3030741" name="28bytes" editcount="25591" registration="2006-12-14T23:17:44Z" emailable="" gender="unknown" userrightstoken="+\">
			<groups>
				<g>abusefilter</g>
				<g>bureaucrat</g>
				<g>sysop</g>
				<g>*</g>
				<g>user</g>
				<g>autoconfirmed</g>
				</groups>
			<implicitgroups>
				<g>*</g>
				<g>user</g>
				<g>autoconfirmed</g>
			</implicitgroups>
			<rights>
				<r>abusefilter-modify</r>
				<r>move-subpages</r>
				<r>suppressredirect</r>
				...
			</rights>	
		</user>
	</users>
</query>
	 */

	public static List<String> readXML(Document doc) throws Exception {
		//System.err.println("Read User Page = readXML");
		List<String> userTxt = new ArrayList<String>();
		Element root = doc.getRootElement();
		//System.out.println(root);

		List<Element> queryNo = root.getChildren("query");
		//Case for if the user is unregistered - look for invalid attribute - userStatus is then unregistered 

		for (Element queryElement : queryNo) {
			//System.err.println(queryElement);

			List<Element> users = queryElement.getChildren("users");
			;
			String title = "";
			String userStatus = "Unregistered";

			//if getAttribute invalid != null 

			for (Element usersElement : users) {

				List<Element> user = usersElement.getChildren("user");
				for (Element u : user){
				//	System.err.println(u.toString());
					List <String>  groups = new ArrayList<String>();
					List <Element> groupsElementList = u.getChildren("groups");
					for (Element groupsElement : groupsElementList){

						List<Element> gList = groupsElement.getChildren("g");
					
						for(Element g : gList)
						{
							groups.add(g.getText());
						//	System.err.println(g.getText());

						}
					}

						String userid = "none";

						if (groups.contains("sysop")){
							userStatus = "Administrator";
							userid = u.getAttribute("userid")
									.getValue();
						}else if (!groups.isEmpty()){
							userStatus = "Registered";
							userid = u.getAttribute("userid")
									.getValue();
						} 


						String username = u.getAttribute(
								"name").getValue();

						if(ReadXML.isGeneratingPROV()){
							CreateProv.getUserPageData(username, userid, userStatus, groups);

						}	

						userTxt.add("userid:" + userid + "  user:"
								+ user);

					
				}
			}
		}

		return userTxt;
	}

}
