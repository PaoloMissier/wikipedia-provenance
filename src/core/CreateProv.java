package core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MediaType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.codehaus.jettison.json.JSONException;
import org.openprovenance.prov.json.JSONConstructor;
import org.openprovenance.prov.notation.NotationConstructor;
import org.openprovenance.prov.rdf.Ontology;
import org.openprovenance.prov.rdf.RdfConstructor;
import org.openprovenance.prov.rdf.RepositoryHelper;
import org.openprovenance.prov.rdf.SesameGraphBuilder;
import org.openprovenance.prov.model.Attribute;
import org.openprovenance.prov.model.Namespace;
import org.openprovenance.prov.xml.ProvFactory;
import org.openprovenance.prov.xml.QualifiedName;
import org.openprovenance.prov.xml.Type;
import org.openprovenance.prov.model.Attribute.*;
import org.openprovenance.prov.model.ModelConstructor;
import org.openprovenance.prov.xml.NamespacePrefixMapper;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.contextaware.ContextAwareRepository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.n3.N3Writer;
import org.openrdf.rio.ntriples.NTriplesWriter;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import org.openrdf.rio.trig.TriGWriter;
import org.openrdf.sail.memory.MemoryStore;

import util.Diff;
import util.ISO8601;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;



public class CreateProv {

	protected static ModelConstructor model;
	protected static ProvFactory factory = ProvFactory.getFactory();
	protected static Ontology ontology = new Ontology(factory);

	//The Set of Supported Provenance Serializations Supported
	//PROV_RDF is the only one actually implemented at the moment (May 2013)
	public enum ModelType{
	PROV_RDF,
    PROV_N,
	PROV_JSON
	}


	//Running Stats
	//At the moment we will count the same author twice ...
	private static int users_count = 0;
	private static int revisions_count = 0;
	private static int administrator_count = 0;
	private static int registered_count = 0;
	private static int unregistered_count = 0;
	private static int blocked_count = 0;
	private static int finalsize = 0;
	private static double averageInfluence = 0;

	public static void resetStats(){
		 users_count = 0;
		 revisions_count = 0;
		 administrator_count = 0;
		 registered_count = 0;
		 unregistered_count = 0;
		 blocked_count = 0;
		 finalsize = 0;
		 averageInfluence = 0;
	}

	public static final String XSD_NS = "http://www.w3.org/2001/XMLSchema#";
	public static final String XSD_PREFIX = "xsd";
	public static final String WIKI_PROV_NS = "http://purl.org/net/wikiprov#";
	public static final String WIKI_PROV_PREFIX = "wikiprov";
	public static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String RDFS_PREFIX = "rdfs";


	private static QualifiedName currentBundle;
	private static ContextAwareRepository rep;
	private static HashSet<String> users = new HashSet<String>();
	private static HashSet<String> blockedusers = new HashSet<String>();

	public static void setModelType(ModelType type){
		switch( type ) {
		case  PROV_RDF:
			Repository myRepository = new SailRepository(new MemoryStore());
			try {
				myRepository.initialize();
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
			rep=new ContextAwareRepository(myRepository);
			model = new RdfConstructor(new SesameGraphBuilder(rep,factory),factory);
    		RdfConstructor rdfmodel = (RdfConstructor) model;
    		Namespace ns = rdfmodel.getNamespace();
    		if(ns == null){
    			ns = new Namespace();
    			rdfmodel.setNamespace(ns);
    		}
    		ns.register("xsd", "http://www.w3.org/2001/XMLSchema#");
    		ns.register("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
    		ns.register("wikiprov", "http://purl.org/net/wikiprov#");
    		
    		
		//TODO setup for Prov-n and Prov-JSON
    	//case  PROV_N: model = new NotationConstructor(null, null);
		//case  PROV_JSON: model = new JSONConstructor();
		}

	}

	public static String getRDFModel(RDFFormat format) throws Exception{
		RdfConstructor rdfmodel = (RdfConstructor) model;
		//TODO check it is acutally an rdfModelConstrutor
		RepositoryHelper rHelper = new RepositoryHelper();

		 StringWriter writer = new StringWriter();
	        RDFHandler serialiser=null;
	        if (format.equals(RDFFormat.N3)) {
	            serialiser=new N3Writer(writer);
	        } else if (format.equals(RDFFormat.RDFXML)) {
	            serialiser=new RDFXMLWriter(writer);
	        } else if  (format.equals(RDFFormat.NTRIPLES)) {
	            serialiser=new NTriplesWriter (writer);
	        } else if  (format.equals(RDFFormat.TRIG)) {
	            serialiser=new TriGWriter (writer);
	        } else if (format.equals(RDFFormat.TURTLE)) {
	            serialiser=new org.openrdf.rio.turtle.TurtleWriter(writer);
	        }
	        rHelper.setPrefixes(serialiser,rdfmodel.getNamespace().getPrefixes());
	        rep.getConnection().export(serialiser);
	        String out = writer.toString();
	       // System.out.println(out);
	        writer.close();

	        return out;



	}

	public static Attribute createAttribute(String namespace, String prefix, String name, Object value, String xsdType){

		Attribute a = factory.newAttribute(new QualifiedName(namespace,name,prefix), value, new QualifiedName(XSD_NS,xsdType,XSD_PREFIX));

		return a;
	}




	public static void getData(String title, String revid, String parentid, String user, String time, String comment, String size, String pageid, String content, String parentContent) throws URISyntaxException, ClientHandlerException, UniformInterfaceException, JSONException, DatatypeConfigurationException{
		revisions_count++;
		title = title.replaceAll(" ", "_");
		if(model == null){
			//Defaults to PROV_RDF if not already set by setModelType(ModelType)
			setModelType(ModelType.PROV_RDF);

		}

		user = sanitizeUserStringForURI(user);

		//Check if article node exists - else create it and add to Model
		String uriString = "_" + title + "_" + revid;
		QualifiedName articleNodeURI = new QualifiedName(WIKI_PROV_NS,uriString,WIKI_PROV_PREFIX);
		ArrayList<Attribute> properties = new ArrayList<Attribute>();

		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"id",revid,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "revid", revid, "string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"title", title,"string"));
	//	properties.add(createProvAttribute(AttributeKind.PROV_TYPE, "article","string"));
		properties.add( factory.newAttribute(AttributeKind.PROV_TYPE,new QualifiedName(WIKI_PROV_NS,"article",WIKI_PROV_PREFIX),new QualifiedName(XSD_NS,"anyURI",XSD_PREFIX)));
		properties.add( factory.newAttribute(AttributeKind.PROV_TYPE,ontology.QNAME_PROVO_Entity,new QualifiedName(XSD_NS,"anyURI",XSD_PREFIX)));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"pageid", pageid,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"comment", comment,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "time", time,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "size", size,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "parentid", parentid,"string"));

		//properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "content", content,"string"));
		//Add the appropriate properties for the article
		model.newEntity(articleNodeURI, properties);


		String activityNodeString = "activity" + "_" + revid;
		QualifiedName activityNodeURI = new QualifiedName(WIKI_PROV_NS,activityNodeString,WIKI_PROV_PREFIX);


		String commentId = "comment" + revid;
		properties = new ArrayList<Attribute>();

		properties.add(factory.newAttribute(AttributeKind.PROV_TYPE, new QualifiedName(WIKI_PROV_NS,"edit",WIKI_PROV_PREFIX),new QualifiedName(XSD_NS,"anyURI",XSD_PREFIX)));
		properties.add( factory.newAttribute(AttributeKind.PROV_TYPE,ontology.QNAME_PROVO_Activity,new QualifiedName(XSD_NS,"anyURI",XSD_PREFIX)));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "comment", comment,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "starttime", "null","string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "endtime", time,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "id", commentId,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "revid", revid,"string"));

		model.newActivity(activityNodeURI, null, DatatypeFactory.newInstance().newXMLGregorianCalendar(time), properties);

		String userNodeString = user;
		QualifiedName userNodeURI = new QualifiedName(WIKI_PROV_NS,userNodeString,WIKI_PROV_PREFIX);
		properties = new ArrayList<Attribute>();

	//	properties.add(createProvAttribute(AttributeKind.PROV_TYPE, "editor","string"));
		properties.add( factory.newAttribute(AttributeKind.PROV_TYPE,new QualifiedName(WIKI_PROV_NS,"editor",WIKI_PROV_PREFIX),new QualifiedName(XSD_NS,"anyURI",XSD_PREFIX)));
		properties.add( factory.newAttribute(AttributeKind.PROV_TYPE,ontology.QNAME_PROVO_Agent,new QualifiedName(XSD_NS,"anyURI",XSD_PREFIX)));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "user_name", user,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "id", user,"string"));

		model.newAgent(userNodeURI, properties);



		String userArticleAttributionString = user + "_" + revid;
		QualifiedName userArticleAttributionNodeURI = new QualifiedName(WIKI_PROV_NS,userArticleAttributionString,WIKI_PROV_PREFIX);
		ArrayList<Attribute> attributedproperties = new ArrayList<Attribute>();



		String relationshipArticleActivityName = "generation" + revid;
		QualifiedName relationshipArticleActivityNodeURI = new QualifiedName(WIKI_PROV_NS,relationshipArticleActivityName,WIKI_PROV_PREFIX);

		properties = new ArrayList<Attribute>();

		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"time", time,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"relationshipName", relationshipArticleActivityName,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"id", relationshipArticleActivityName,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"entity", revid,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"activity", commentId,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"comment", comment,"string"));
		model.newWasGeneratedBy(relationshipArticleActivityNodeURI, articleNodeURI, activityNodeURI, DatatypeFactory.newInstance().newXMLGregorianCalendar(time), properties);



		String relationshipActivityUserName = "comment" + revid + user;
	    QualifiedName relationshipActivityUserNodeURI = new QualifiedName(WIKI_PROV_NS,relationshipActivityUserName,WIKI_PROV_PREFIX);
	    properties = new ArrayList<Attribute>();


	    properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"user_name", user,"string"));
	    properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"activity", commentId,"string"));
	    properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"agent", user,"string"));
	    properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"publicationpolicy", "null","string"));
	    properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"relationshipName", relationshipActivityUserName,"string"));
	    properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"id", relationshipActivityUserName,"string"));

			model.newWasAssociatedWith(relationshipActivityUserNodeURI, activityNodeURI,userNodeURI, null, properties);


		if(parentid != "0")
		{
			String parentNodeString = "_" + title + "_" + parentid;
			 QualifiedName parentNodeURI = new QualifiedName(WIKI_PROV_NS,parentNodeString,WIKI_PROV_PREFIX);

			 properties = new ArrayList<Attribute>();
			 properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "revid", parentid,"string"));
			 properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "id", parentid,"string"));
			 properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "title", title,"string"));
			 properties.add( factory.newAttribute(AttributeKind.PROV_TYPE,ontology.QNAME_PROVO_Entity,new QualifiedName(XSD_NS,"anyURI",XSD_PREFIX)));
			 properties.add(factory.newAttribute(AttributeKind.PROV_TYPE, new QualifiedName(WIKI_PROV_NS,"article",WIKI_PROV_PREFIX),new QualifiedName(XSD_NS,"anyURI",XSD_PREFIX)));
			 properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "pageid", pageid,"string"));

			 model.newEntity(parentNodeURI, properties);



			String relationshipRevisionParentName = "revision_" + title + "_" + revid + "_" + parentid;
			QualifiedName relationshipRevisionParentURI =new QualifiedName(WIKI_PROV_NS,relationshipRevisionParentName,WIKI_PROV_PREFIX);

			//Ontology.QualifiedName_PROVO_Revision
		     properties = new ArrayList<Attribute>();
			 properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "parentid", parentid,"string"));
			 properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "entity1", parentid,"string"));
			 properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "revid", revid,"string"));
			 properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "entity2", revid,"string"));
			 properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "agent", user,"string"));
			 properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "relationshipName", relationshipRevisionParentName,"string"));
			 properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "id", relationshipRevisionParentName,"string"));
			 properties.add( factory.newAttribute(AttributeKind.PROV_TYPE,ontology.QNAME_PROVO_Revision,new QualifiedName(XSD_NS,"anyURI",XSD_PREFIX)));

			 if(ReadXML.isGeneratingDiff() & (parentContent != null)){

				Diff d = Diff.generateDiff(parentContent, content);
				properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "words", d.getWords() ,"integer"));
				properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "common",d.getCommon() ,"integer"));
				properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "deleted",d.getDeleted() ,"integer"));
				properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "changed",d.getChanged() ,"integer"));
				double influence = 1 - ((double)d.getCommon()/(double)d.getWords());
				if(influence != Double.NaN && influence != Double.POSITIVE_INFINITY){
					properties.add(createAttribute(NamespacePrefixMapper.PROV_NS,NamespacePrefixMapper.PROV_PREFIX, "influenceFactor",1 - influence,"double"));
					attributedproperties.add(createAttribute(NamespacePrefixMapper.PROV_NS,NamespacePrefixMapper.PROV_PREFIX, "influenceFactor",influence,"double"));
					finalsize = d.getWords();
					averageInfluence = ((averageInfluence * (revisions_count -1)) + influence) / revisions_count;
				}
			 }

			model.newWasDerivedFrom(relationshipRevisionParentURI, articleNodeURI, parentNodeURI, activityNodeURI,relationshipArticleActivityNodeURI , null, properties);

//			 properties = new ArrayList<Attribute>();
//			 properties.add( new Attribute(AttributeKind.PROV_TYPE,Ontology.QualifiedName_PROVO_Revision,new QualifiedName(XSD_NS,"anyURI",XSD_PREFIX)));
//			 model.newWasDerivedFrom(null, articleNodeURI, parentNodeURI, null, null, null, properties);

		}

		model.newWasAttributedTo(userArticleAttributionNodeURI, articleNodeURI, userNodeURI,attributedproperties);

	}

public static void getUserData(String title, String revid, String user, String time, String comment, String size, String pageid) throws URISyntaxException, ClientHandlerException, UniformInterfaceException, JSONException, DatatypeConfigurationException{

	title = title.replaceAll(" ", "_");
	user = sanitizeUserStringForURI(user);

	//Check if article node exists - else create it and add to Model
	//We prefix with '_' because we can't guarantee that the first character is one of 
	//namestartchar according to the turtle grammar.
	String uriString = "_" + title + "_" + revid;
	QualifiedName articleNodeURI = new QualifiedName(WIKI_PROV_NS,uriString,WIKI_PROV_PREFIX);
	ArrayList<Attribute> properties = new ArrayList<Attribute>();

	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"id",revid,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "revid", revid, "string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"title", title,"string"));
	properties.add(factory.newAttribute(AttributeKind.PROV_TYPE, new QualifiedName(WIKI_PROV_NS,"article",WIKI_PROV_PREFIX),new QualifiedName(XSD_NS,"anyURI",XSD_PREFIX)));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "type", "entity","string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"pageid", pageid,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"comment", comment,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "time", time,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "size", size,"string"));
	//Add the appropriate properties for the article
	model.newEntity(articleNodeURI, properties);



	String activityNodeString = "activity" + "_" + revid;
	QualifiedName activityNodeURI = new QualifiedName(WIKI_PROV_NS,activityNodeString,WIKI_PROV_PREFIX);


	String commentId = "comment" + revid;
	properties = new ArrayList<Attribute>();

	properties.add(factory.newAttribute(AttributeKind.PROV_TYPE, new QualifiedName(WIKI_PROV_NS,"edit",WIKI_PROV_PREFIX),new QualifiedName(XSD_NS,"anyURI",XSD_PREFIX)));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "type", "activity","string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "comment", comment,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "starttime", "null","string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "endtime", time,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "id", commentId,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "revid", revid,"string"));

	model.newActivity(activityNodeURI, null, DatatypeFactory.newInstance().newXMLGregorianCalendar(time), properties);



	String userNodeString = "_" + user;
	QualifiedName userNodeURI = new QualifiedName(WIKI_PROV_NS,userNodeString,WIKI_PROV_PREFIX);
	properties = new ArrayList<Attribute>();

	properties.add(factory.newAttribute(AttributeKind.PROV_TYPE, new QualifiedName(WIKI_PROV_NS,"editor",WIKI_PROV_PREFIX),new QualifiedName(XSD_NS,"anyURI",XSD_PREFIX)));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "type", "agent","string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "user_name", user,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "id", user,"string"));

	model.newAgent(userNodeURI, properties);

	String relationshipArticleActivityName = "generation" + revid;
	QualifiedName relationshipArticleActivityNodeURI = new QualifiedName(WIKI_PROV_NS,relationshipArticleActivityName,WIKI_PROV_PREFIX);

	properties = new ArrayList<Attribute>();

	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"time", time,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"relationshipName", relationshipArticleActivityName,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"id", relationshipArticleActivityName,"string"));
	properties.add(createAttribute(NamespacePrefixMapper.PROV_NS,NamespacePrefixMapper.PROV_PREFIX,"entity", revid,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"activity", commentId,"string"));

	model.newWasGeneratedBy(relationshipArticleActivityNodeURI, articleNodeURI, activityNodeURI, DatatypeFactory.newInstance().newXMLGregorianCalendar(time), properties);



	String relationshipActivityUserName = "association" + revid + user;
    QualifiedName relationshipActivityUserNodeURI = new QualifiedName(WIKI_PROV_NS,relationshipActivityUserName,WIKI_PROV_PREFIX);
    properties = new ArrayList<Attribute>();


    properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"user_name", user,"string"));
    properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"activity", commentId,"string"));
    properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"agent", user,"string"));
    properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"publicationpolicy", "null","string"));
    properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"relationshipName", relationshipActivityUserName,"string"));
    properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"id", relationshipActivityUserName,"string"));

		model.newWasAssociatedWith(relationshipActivityUserNodeURI, activityNodeURI,userNodeURI, null, properties);



	}



public static void getUserPageData(String user,String userid, String userStatus, List<String> groups) {



	user = sanitizeUserStringForURI(user);

	boolean newuser = users.add(user);

	ArrayList<Attribute> properties = new ArrayList<Attribute>();

	String userNodeString = "_" + user;
	QualifiedName userNodeURI = new QualifiedName(WIKI_PROV_NS,userNodeString,WIKI_PROV_PREFIX);
	properties = new ArrayList<Attribute>();

	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "user_name", user,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "userid", userid,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "status", userStatus,"string"));
	for(String group : groups){
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "memberOfGroup", group,"string"));
	}

	if(newuser){
		users_count++;
	if(userStatus.equalsIgnoreCase("administrator"))
		administrator_count++;
	if(userStatus.equalsIgnoreCase("registered"))
		registered_count++;
	if(userStatus.equalsIgnoreCase("unregistered"))
		unregistered_count++;
	}

	model.newAgent(userNodeURI, properties);


}

public static void getUserBlockData(String user, String userid, String time, String expiry, boolean blocked) {

	ArrayList<Attribute> properties = new ArrayList<Attribute>();
	user = sanitizeUserStringForURI(user);
	boolean newuser = blockedusers.add(user);

	String userNodeString = "_" + user;
	QualifiedName userNodeURI = new QualifiedName(WIKI_PROV_NS,userNodeString,WIKI_PROV_PREFIX);
	properties = new ArrayList<Attribute>();

	if(newuser & blocked)
		blocked_count++;

	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "id", userid,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "blocked", blocked,"boolean"));

	if(time != null)
	    properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "timestamp", time,"string"));

	if(expiry != null)
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "expiry", expiry,"string"));

	model.newAgent(userNodeURI, properties);


}


	public static void createProvBundle(String title){
		//Might be the first thing called for CreateProv
		if(model == null){
			//Defaults to PROV_RDF if not already set by setModelType(ModelType)
			setModelType(ModelType.PROV_RDF);

		}

		String time = sanitizeUserStringForURI(ISO8601.now());

		String uriString = "bundle_" + title + "_" + time;
		currentBundle = new QualifiedName(WIKI_PROV_NS,uriString,WIKI_PROV_PREFIX);
		Hashtable<String,String> namespaceTable = new Hashtable<String,String>();
		namespaceTable.put("xsd", "http://www.w3.org/2001/XMLSchema#");
		namespaceTable.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		namespaceTable.put("wikiprov", "http://sierra-nevada.cs.man.ac.uk/wikiprov/");
		Namespace ns = new Namespace(namespaceTable);
		model.startBundle(currentBundle,ns);

	}

	public static void createProvBundleStatistics(){

	ArrayList<Attribute> properties = new ArrayList<Attribute>();
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "revision_count", revisions_count,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "administrator_count", administrator_count,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "registered_count", registered_count,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "unregistered_count", unregistered_count,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "blocked_count", blocked_count,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "final_size", finalsize,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "revision_count", revisions_count,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "influence_average", averageInfluence,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "users_count", users_count,"string"));


		model.newEntity(currentBundle, properties);

	}



	//User names can have spaces in them at least
	public static String sanitizeUserStringForURI(String user){
		user = user.replaceAll("%20", "_");
		user = user.replaceAll(" ", "_");
		user = user.replaceAll("\\.", "_");
		user = user.replaceAll("-", "_");
		return user;
	}


}
