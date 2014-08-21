package core;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.openrdf.rio.RDFFormat;

import core.CreateProv.ModelType;
import util.ISO8601;

public class ReadPageXML {
	
	
	//Holds an String version of the latest prov extraction - a bit ugly to store and access in this way 
	private static String rdfString;
	private static RDFFormat rdfFormat = RDFFormat.TURTLE;
	private static boolean generatePROV = false;
	private static HashSet<String> users = new HashSet<String>();
	
	protected static boolean isGeneratingPROV() {
		return generatePROV;
	}

	protected static void setGeneratePROV(boolean generatePROV) {
		ReadPageXML.generatePROV = generatePROV;
	}

	//String may be null
	public static String getRDFString(){
		//Will only be populated if generating prov
		if(ReadXML.isGeneratingPROV()){
			return rdfString;
		}else
		{
			return null;
		}
		
		
	}
	
	public static void setRDFFormat(RDFFormat format) {
		rdfFormat = format;
		
	}

	//@Change - add "content" to the request so that we can generate a diff 
	//@Change - add arguments for RDF generation and diff generation
	//TODO toggle content request based on diff boolean 
	public static void startWithPage(String title, String rvlimit, int depth, String uclimit) throws Exception{
		
		
		//Seems to be a fairly random and limited percent encoding going on ...? 
				title = title.replaceAll(" ", "%20");
				title = title.replaceAll("=", "%3D");
				title = title.replaceAll("[+]", "%2B");
				System.err.println("Proccessing:" +  title);
				if (depth > 0)
				readXMLbyURL("http://en.wikipedia.org/w/api.php?action=query&titles=" + title + "&prop=revisions&rvlimit=" + rvlimit + "&rvprop=ids|flags|user|timestamp|comment|content|size&format=xml",  depth, title, uclimit, rvlimit, "", "");
				
		
	}
	
	public static void startWithPage(String title, String rvlimit,String rvstartid, String rvstart, int depth, String uclimit) throws Exception{
		
		String rvlimiturl = "";
		String rvstartidurl = "";
		String rvstarturl = "";
		
		if(!rvlimit.equalsIgnoreCase(""))
			rvlimiturl = "&rvlimit=" + rvlimit;
		if(rvlimit.equalsIgnoreCase("all")) // Special case use 500 (max allowed) and then pickup rvcontinue until done (below)
			rvlimiturl = "&rvlimit=500";
		if(!rvstartid.equalsIgnoreCase(""))
			rvstartidurl = "&rvstartid=" + rvstartid;
		if(!rvstart.equalsIgnoreCase(""))
			rvstarturl = "&rvstart=" + rvstart;
		
		//Seems to be a fairly random and limited percent encoding going on ...? 
				title = title.replaceAll(" ", "%20");
				title = title.replaceAll("=", "%3D");
				title = title.replaceAll("[+]", "%2B");
				System.err.println("Proccessing:" +  title);
				
				if(ReadXML.isGeneratingPROV()){
					CreateProv.setModelType(ModelType.PROV_RDF); //quick hack to reset the model
					CreateProv.createProvBundle(title);
				}
				
				if (depth > 0)
					readXMLbyURL("http://en.wikipedia.org/w/api.php?action=query&titles=" + title + "&prop=revisions" + rvlimiturl +  rvstarturl +  rvstartidurl + "&rvprop=ids|flags|user|timestamp|comment|content|size&format=xml",  depth,title, uclimit, rvlimit, rvstartid, rvstart);
				
				if(ReadXML.isGeneratingPROV()){
					CreateProv.createProvBundleStatistics();
					CreateProv.resetStats();
					rdfString = CreateProv.getRDFModel(rdfFormat);
				}
					
		
	}


	public static void readXMLbyFileName(String fileName, String totalNodeNumber, int depth, String uclimit, String rvlimit) throws Exception {
		SAXBuilder builder = new SAXBuilder();

		File xmlFile = new File(fileName);
		Document doc = (Document) builder.build(xmlFile);
		readXML(doc, depth, "", uclimit, rvlimit, "" , "");
	}

	public static void readXMLbyURL(String fileName, int depth, String title, String uclimit, String rvlimit, String rvstartid, String rvstart) throws Exception {
		SAXBuilder builder = new SAXBuilder();

		Document doc = builder.build(fileName);
		readXML(doc, depth,title, uclimit, rvlimit, rvstartid, rvstart);
		
	}

	public static void readXML(Document doc, int depth, String title, String uclimit, String rvlimit, String rvstartid, String rvstart) throws Exception {

		Element root = doc.getRootElement();
		//System.out.println(doc.toString());
		List queryNo = root.getChildren("query");
		String firstTimestamp = null;

		depth--;
		
		for (int i = 0; i < queryNo.size(); i++) {
			// I know stupid i,j,k,a,b,c....can chang to iterator, but not it is
			// not easy for understanding
			Element queryElenemt = (Element) queryNo.get(i);

			List pagesNo = queryElenemt.getChildren("pages");
			for (int j = 0; j < pagesNo.size(); j++) {
				Element pagesElement = (Element) pagesNo.get(j);

				List pageNo = pagesElement.getChildren("page");
				for (int k = 0; k < pageNo.size(); k++) {
					Element pageElement = (Element) pageNo.get(k);
					String pageid = pageElement.getAttribute("pageid")
							.getValue();
					
					if(title.equalsIgnoreCase(""))
					  title = pageElement.getAttribute("title").getValue();
					//System.out.println("pageid:" + pageid + "  title:" + title);

					List revisionNo = pageElement.getChildren("revisions");
					String user = "";

					HashMap<String, Element> revisions = new HashMap<String, Element>();
					
					for (int a = 0; a < revisionNo.size(); a++) {
						Element revisionElement = (Element) revisionNo.get(a);

						List revNo = revisionElement.getChildren("rev");
						//Loop through first and create Hash of children indexed by revid
						//This is so we can quickly find the previous (parent) revision for diff proccessing
						for (int b = 0; b < revNo.size(); b++) {
							Element revElement = (Element) revNo.get(b);
							String revid = revElement.getAttribute("revid")
									.getValue();
							String time = revElement.getAttributeValue("timestamp");
							if(firstTimestamp == null)
								firstTimestamp = time;
							
							revisions.put(revid, revElement);
						}
						
						//Now loop through revisions and create entries
						for(Element revision : revisions.values()){
							
							String revid = revision.getAttribute("revid")
									.getValue();
							
							String parentid = revision.getAttribute(
									"parentid").getValue();
							// String minor =
							// revElement.getAttribute("minor").getValue();
							user = revision.getAttribute("user")
									.getValue();
							
							String time = revision.getAttribute(
									"timestamp").getValue();
							
							
							String comment = new String();
							try{
							comment = revision.getAttribute("comment")
									.getValue();
							}catch (Exception e1) {
								comment = "null";									
							}
							String size = new String();
							try{
							size = revision.getAttribute("size")
									.getValue();
							}catch (Exception e1) {
								size = "0";									
							}
							String content = new String();
							try{
							content = revision.getText();
							}catch (Exception e1) {
								comment = "null";									
							}
							
							//See if we have the parent to do a diff with
							String parentContent = null;
							if(parentid != null){
								Element parent = revisions.get(parentid);
								if(parent != null){
									parentContent = parent.getText();
								}
							}
							
							
							if(ReadXML.usingNeo4j())
								CreateGraph.getData(title, revid, parentid, user, time, comment, size, pageid);
							
							if(ReadXML.isGeneratingPROV())
							    CreateProv.getData(title, revid, parentid, user, time, comment, size, pageid,content,parentContent);
//							System.out.println("revid:" + revid + "  parentid:"
//									+ parentid + "  user:" + user
//									+ "  time:" + time + " comment:"
//									+ comment+ " size:" + size);
							//if(!users.contains(user)){
							ReadUserContribXML.startWithUser(user, uclimit, depth, rvlimit);
							ReadUserPageXML.startWithUser(user);
							ReadUserBlocksXML.startWithUser(user,ISO8601.toCalendar(firstTimestamp));
						//	}
							
						//	users.add(user);
							

							
						}
						
					}
				}
			}

			if(rvlimit != "" & Integer.parseInt(rvlimit) > 500){
			List query_continueNo = root.getChildren("query-continue");
			for (int c = 0; c < query_continueNo.size(); c++) {
				Element qc_revisionsElement = (Element) query_continueNo.get(c);
				List qc_revisionsNo = qc_revisionsElement
						.getChildren("revisions");
				for (int d = 0; d < qc_revisionsNo.size(); d++) {
					Element rvstartidElement = (Element) qc_revisionsNo.get(d);
					//System.out.println(rvstartidElement);
					Attribute rvstartidAttribute = rvstartidElement.getAttribute(
							"rvcontinue"); 
					if(rvstartidAttribute != null){
						String rvlimiturl = "";
						String rvstartidurl = "";
						String rvstarturl = "";
						String rvcontinueurl = "";
						String rvcontinue = rvstartidAttribute.getValue();
						
						if(!rvlimit.equalsIgnoreCase(""))
							rvlimiturl = "&rvlimit=" + rvlimit;
						if(rvlimit.equalsIgnoreCase("all")) // Special case use 500 (max allowed) and then pickup rvcontinue until done (below)
						{	rvlimiturl = "&rvlimit=500";
							rvstartid = rvcontinue;
						}else{
							rvcontinueurl = "&rvcontinue=" + rvcontinue;
						}
						if(!rvstartid.equalsIgnoreCase(""))
							rvstartidurl = "&rvstartid=" + rvstartid;
						if(!rvstart.equalsIgnoreCase(""))
							rvstarturl = "&rvstart=" + rvstart;
					
						
					
					readXMLbyURL("http://en.wikipedia.org/w/api.php?action=query&titles=" + title + "&prop=revisions" + rvlimiturl +  rvstarturl +  rvstartidurl + "&rvprop=ids|flags|user|timestamp|comment|content|size&format=xml"+ rvcontinueurl,  depth, title, uclimit, rvlimit, rvstartid, rvstart);
										}
					
				}
			 }
			}
		}
		
		
		
	}

	

}
