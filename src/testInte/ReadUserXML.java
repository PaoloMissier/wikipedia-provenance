package testInte;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class ReadUserXML {

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
	
	public static List<String> startWithUser(String user, String uclimit,String totalNodeNumber, int depth, String rvlimit) throws Exception{
		user = user.replaceAll(" ", "%20");
		user = user.replaceAll("=", "%3D");
		user = user.replaceAll("[+]", "%2B");
		List<String> userTxt = new ArrayList<String>();
		if (depth > 0)
			userTxt = readXMLbyURL("http://en.wikipedia.org/w/api.php?action=query&list=usercontribs&ucuser="+ user + "&uclimit=" + uclimit + "&format=xml", totalNodeNumber, depth, uclimit, rvlimit);
		return userTxt;
	}
	
	
	public static List<String> readXMLbyFileName(String fileName, String totalNodeNumber, int depth, String uclimit, String rvlimit) throws Exception {
		SAXBuilder builder = new SAXBuilder();

		File xmlFile = new File(fileName);
		Document doc = (Document) builder.build(xmlFile);
		List<String> userTxt = readXML(doc,totalNodeNumber, depth, uclimit, rvlimit);
		return userTxt;
	}

	public static List<String> readXMLbyURL(String fileName, String totalNodeNumber, int depth, String uclimit, String rvlimit) throws Exception {
		SAXBuilder builder = new SAXBuilder();

		Document doc = builder.build(fileName);
		List<String> userTxt = readXML(doc, totalNodeNumber, depth, uclimit, rvlimit);
		return userTxt;
	}

	public static List<String> readXML(Document doc, String totalNodeNumber, int depth, String uclimit, String rvlimit) throws Exception {

		List<String> userTxt = new ArrayList<String>();
		Element root = doc.getRootElement();
		//System.out.println(root);

		List queryNo = root.getChildren("query");

		depth--;
		
		for (int i = 0; i < queryNo.size(); i++) {
			// I know stupid i,j,k,a,b,c....can change to iterator, but not it is
			// not easy for understanding
			Element queryElenemt = (Element) queryNo.get(i);

			List pagesNo = queryElenemt.getChildren("usercontribs");
			String title = "";
//			depth = depth - 1;
			for (int j = 0; j < pagesNo.size(); j++) {
				Element usercontribsElement = (Element) pagesNo.get(j);

						List revNo = usercontribsElement.getChildren("item");
						
						for (int b = 0; b < revNo.size(); b++) {
							Element revElement = (Element) revNo.get(b);
							String userid = revElement.getAttribute("userid")
									.getValue();						
							String user = revElement.getAttribute(
									"user").getValue();
							String pageid = revElement.getAttribute("pageid")
									.getValue();
							String revid = revElement.getAttribute(
									"revid").getValue();
							title = revElement.getAttribute("title")
									.getValue();
							String time = revElement.getAttribute("timestamp")
									.getValue();
							String comment = new String();
							try{
							
							comment = revElement.getAttribute("comment")
									.getValue();
							}catch (Exception e1) {
								comment = "null";							
							}
							//java.lang.NullPointerException
							String size = new String();							
							try{
								size = revElement.getAttribute("size")
										.getValue();
								}catch (Exception e1) {
									size = "0";
									//System.err.println(e1);									
								}
						
							CreateGraph.getUserData(title, revid, user, time, comment, size, pageid, totalNodeNumber);
							userTxt.add("userid:" + userid + "  user:"
									+ user + "  pageid:" + pageid
									+ "  revid:" + revid + " title:"
									+  title + " time:" + time + " comment:" + comment + " size:" + size + "\n");
							
//							System.out.println("userid:" + userid + "  user:"
//									+ user + "  pageid:" + pageid
//									+ "  revid:" + revid + " title:"
//									+  title + " time:" + time + " comment:" + comment + " size:" + size);
							ReadPageXML.startWithPage(title, rvlimit, totalNodeNumber, depth, uclimit);
						}
						
						
					}
			
			


			List query_continueNo = root.getChildren("query-continue");
			for (int c = 0; c < query_continueNo.size(); c++) {
				Element qc_revisionsElement = (Element) query_continueNo.get(c);
				List qc_revisionsNo = qc_revisionsElement
						.getChildren("usercontribs");
				for (int d = 0; d < qc_revisionsNo.size(); d++) {
					Element rvstartidElement = (Element) qc_revisionsNo.get(d);
					String ucstart = rvstartidElement.getAttribute(
							"ucstart").getValue();
//					System.out.println("ucstart:" + ucstart);
				}
			}
		}

		return userTxt;
	}

}
