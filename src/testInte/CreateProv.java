package testInte;

import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MediaType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.codehaus.jettison.json.JSONException;
import org.openprovenance.prov.json.JSONConstructor;
import org.openprovenance.prov.notation.NotationConstructor;
import org.openprovenance.prov.rdf.Ontology;
import org.openprovenance.prov.rdf.RdfConstructor;
import org.openprovenance.prov.rdf.RepositoryHelper;
import org.openprovenance.prov.rdf.SesameGraphBuilder;
import org.openprovenance.prov.xml.Attribute;
import org.openprovenance.prov.xml.Attribute.*;
import org.openprovenance.prov.xml.ModelConstructor;
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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;



public class CreateProv {
	
	protected static ModelConstructor model;
	
//	public static void resetModel(){
//		model = null;
//	}
	public enum ModelType{	
	PROV_RDF,
    PROV_N,
	PROV_JSON
	}
	
	public static final String XSD_NS = "http://www.w3.org/2001/XMLSchema#";
	public static final String XSD_PREFIX = "xsd";
	public static final String WIKI_PROV_NS = "http://sierra-nevada.cs.man.ac.uk/wikiprov/";
	public static final String WIKI_PROV_PREFIX = "wiki-prov";
	
	private static ContextAwareRepository rep;
	
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
			model = new RdfConstructor(new SesameGraphBuilder(rep));
    		RdfConstructor rdfmodel = (RdfConstructor) model;
    		rdfmodel.getNamespaceTable().put("xsd", "http://www.w3.org/2001/XMLSchema#");
    		rdfmodel.getNamespaceTable().put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
    		rdfmodel.getNamespaceTable().put("wiki-prov", "http://sierra-nevada.cs.man.ac.uk/wikiprov/");
		//TODO setup for Prov-n and Prov-JSON
    	//case  PROV_N: model = new NotationConstructor(null, null);
		//case  PROV_JSON: model = new JSONConstructor();
		}
		
	}
	
	public static String getRDFModel(RDFFormat format) throws Exception{
		RdfConstructor rdfmodel = (RdfConstructor) model;
		//TODO check it is acutally an rdfModelConstrutor
		RepositoryHelper rHelper = new RepositoryHelper();
		//rHelper.dumpToRDF("/tmp/dump.ttl", rep, RDFFormat.TURTLE, rdfmodel.getNamespaceTable());
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
	        rHelper.setPrefixes(serialiser,rdfmodel.getNamespaceTable());
	        rep.getConnection().export(serialiser);
	        String out = writer.toString();
	        System.out.println(out);
	        writer.close();
	        
	        return out;
		
		
		
	}
	
	public static Attribute createAttribute(String namespace, String prefix, String name, String value, String xsdType){
		
		Attribute a = new Attribute(new QName(namespace,name,prefix), value, new QName(XSD_NS,xsdType,XSD_PREFIX));
		
		return a;
	}
	
	public static Attribute createProvAttribute(AttributeKind kind, String value, String xsdType){
		Attribute a = new Attribute(kind,value,new QName(XSD_NS,xsdType,XSD_PREFIX));
		return a;
	}
	
	
	public static void getData(String title, String revid, String parentid, String user, String time, String comment, String size, String pageid) throws URISyntaxException, ClientHandlerException, UniformInterfaceException, JSONException, DatatypeConfigurationException{
		
		if(model == null){
			//Defaults to PROV_RDF if not already set by setModelType(ModelType)
			setModelType(ModelType.PROV_RDF);
		}
		
		//Check if article node exists - else create it and add to Model  
		String uriString = title + "-" + revid;
		QName articleNodeURI = new QName(WIKI_PROV_NS,uriString,WIKI_PROV_PREFIX);
		ArrayList<Attribute> properties = new ArrayList<Attribute>();
		
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"id",revid,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "revid", revid, "string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"title", title,"string"));
		properties.add(createProvAttribute(AttributeKind.PROV_TYPE, "article","string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "type", "entity","string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"pageid", pageid,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"comment", comment,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "time", time,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "size", size,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "parentid", parentid,"string"));
		//Add the appropriate properties for the article
		model.newEntity(articleNodeURI, properties);

		
		String activityNodeString = "activity" + "-" + revid;
		QName activityNodeURI = new QName(WIKI_PROV_NS,activityNodeString,WIKI_PROV_PREFIX);

		
		String commentId = "comment" + revid;
		properties = new ArrayList<Attribute>();	
			
		properties.add(createProvAttribute(AttributeKind.PROV_TYPE, "edit","string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "type", "activity","string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "comment", comment,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "starttime", "null","string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "endtime", time,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "id", commentId,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "revid", revid,"string"));
		
		model.newActivity(activityNodeURI, null, DatatypeFactory.newInstance().newXMLGregorianCalendar(time), properties);
		
		String userNodeString = user;
		QName userNodeURI = new QName(WIKI_PROV_NS,userNodeString,WIKI_PROV_PREFIX);
		properties = new ArrayList<Attribute>();	
			
		properties.add(createProvAttribute(AttributeKind.PROV_TYPE, "editor","string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "type", "agent","string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "user_name", user,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "id", user,"string"));
	
		model.newAgent(userNodeURI, properties);
		
		
		
		String relationshipArticleActivityName = revid + "comment";
		QName relationshipArticleActivityNodeURI = new QName(WIKI_PROV_NS,relationshipArticleActivityName,WIKI_PROV_PREFIX);
	
		properties = new ArrayList<Attribute>();	

		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"time", time,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"relationshipName", relationshipArticleActivityName,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"id", relationshipArticleActivityName,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"entity", revid,"string"));
		properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"activity", commentId,"string"));
				
		model.newWasGeneratedBy(relationshipArticleActivityNodeURI, articleNodeURI, activityNodeURI, DatatypeFactory.newInstance().newXMLGregorianCalendar(time), properties);
		

		
		String relationshipActivityUserName = "comment" + revid + user;
	    QName relationshipActivityUserNodeURI = new QName(WIKI_PROV_NS,relationshipActivityUserName,WIKI_PROV_PREFIX);
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
			String parentNodeString = parentid;
			 QName parentNodeURI = new QName(WIKI_PROV_NS,parentNodeString,WIKI_PROV_PREFIX);
			   	
			 properties = new ArrayList<Attribute>();
			 properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "revid", parentid,"string"));
			 properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "id", parentid,"string"));
			 properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "title", title,"string"));
			 properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"type", "entity","string"));
			 properties.add(createProvAttribute(AttributeKind.PROV_TYPE, "article","string"));
			 properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "pageid", pageid,"string"));
			
			 model.newEntity(parentNodeURI, properties);
			
	
			
			String relationshipRevisionParentName = revid + "-" + parentid;
			QName relationshipRevisionParentURI =new QName(WIKI_PROV_NS,relationshipRevisionParentName,WIKI_PROV_PREFIX);
			  	
			//Ontology.QNAME_PROVO_Revision	
			properties = new ArrayList<Attribute>();
			 properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "parentid", parentid,"string"));
			 properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "entity1", parentid,"string"));
			 properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "revid", revid,"string"));
			 properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "entity2", revid,"string"));
			 properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "agent", user,"string"));
			 properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "relationshipName", relationshipRevisionParentName,"string"));
			 properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "id", relationshipRevisionParentName,"string"));
			properties.add( new Attribute(AttributeKind.PROV_TYPE,Ontology.QNAME_PROVO_Revision,new QName(XSD_NS,"anyURI",XSD_PREFIX)));
			
			model.newWasDerivedFrom(relationshipRevisionParentURI, articleNodeURI, parentNodeURI, null, null, null, properties);
			
		}
	}
	
public static void getUserData(String title, String revid, String user, String time, String comment, String size, String pageid) throws URISyntaxException, ClientHandlerException, UniformInterfaceException, JSONException, DatatypeConfigurationException{
		
		
	//Check if article node exists - else create it and add to Model  
	String uriString = title + "-" + revid;
	QName articleNodeURI = new QName(WIKI_PROV_NS,uriString,WIKI_PROV_PREFIX);
	ArrayList<Attribute> properties = new ArrayList<Attribute>();
	
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"id",revid,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "revid", revid, "string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"title", title,"string"));
	properties.add(createProvAttribute(AttributeKind.PROV_TYPE, "article","string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "type", "entity","string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"pageid", pageid,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"comment", comment,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "time", time,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "size", size,"string"));
	//Add the appropriate properties for the article
	model.newEntity(articleNodeURI, properties);

	
	
	String activityNodeString = "activity" + "-" + revid;
	QName activityNodeURI = new QName(WIKI_PROV_NS,activityNodeString,WIKI_PROV_PREFIX);

	
	String commentId = "comment" + revid;
	properties = new ArrayList<Attribute>();	
		
	properties.add(createProvAttribute(AttributeKind.PROV_TYPE, "edit","string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "type", "activity","string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "comment", comment,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "starttime", "null","string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "endtime", time,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "id", commentId,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "revid", revid,"string"));
	
	model.newActivity(activityNodeURI, null, DatatypeFactory.newInstance().newXMLGregorianCalendar(time), properties);		
		
		
		
	String userNodeString = user;
	QName userNodeURI = new QName(WIKI_PROV_NS,userNodeString,WIKI_PROV_PREFIX);
	properties = new ArrayList<Attribute>();	
		
	properties.add(createProvAttribute(AttributeKind.PROV_TYPE, "editor","string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "type", "agent","string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "user_name", user,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX, "id", user,"string"));

	model.newAgent(userNodeURI, properties);
		
	String relationshipArticleActivityName = revid + "comment";
	QName relationshipArticleActivityNodeURI = new QName(WIKI_PROV_NS,relationshipArticleActivityName,WIKI_PROV_PREFIX);

	properties = new ArrayList<Attribute>();	

	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"time", time,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"relationshipName", relationshipArticleActivityName,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"id", relationshipArticleActivityName,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"entity", revid,"string"));
	properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"activity", commentId,"string"));
			
	model.newWasGeneratedBy(relationshipArticleActivityNodeURI, articleNodeURI, activityNodeURI, DatatypeFactory.newInstance().newXMLGregorianCalendar(time), properties);
	

		
	String relationshipActivityUserName = "comment" + revid + user;
    QName relationshipActivityUserNodeURI = new QName(WIKI_PROV_NS,relationshipActivityUserName,WIKI_PROV_PREFIX);
    properties = new ArrayList<Attribute>();	


    properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"user_name", user,"string"));
    properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"activity", commentId,"string"));
    properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"agent", user,"string"));
    properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"publicationpolicy", "null","string"));
    properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"relationshipName", relationshipActivityUserName,"string"));
    properties.add(createAttribute(WIKI_PROV_NS,WIKI_PROV_PREFIX,"id", relationshipActivityUserName,"string"));
    
		model.newWasAssociatedWith(relationshipActivityUserNodeURI, activityNodeURI,userNodeURI, null, properties);

	
	
	}
	

		
	
	private static String generateJsonRelationship(URI endNode, String relationshipType, String... jsonAttributes ){
		StringBuilder sb = new StringBuilder();
		sb.append("{ \"to\" : \"");
		sb.append(endNode.toString());
		sb.append("\", ");		
		sb.append("\"type\" : \"");
		sb.append(relationshipType);
		if (jsonAttributes == null || jsonAttributes.length<1){
			sb.append("\"");
		}else{
			sb.append("\", \"data\" : ");
			for(int i = 0; i < jsonAttributes.length; i++){
				sb.append(jsonAttributes[i]);
					if(i < jsonAttributes.length - 1){
						sb.append(", ");
					}
				}
			}	
		sb.append(" }");
		return sb.toString();
		}
	

		
	private static void addMetadataToProperty( URI relationshipUri,
            Map<String,String> property) throws URISyntaxException
    {
        URI propertyUri = new URI( relationshipUri.toString() + "/properties" );
        String entity = toJsonNameValuePairCollection( property );
        WebResource resource = Client.create()
                .resource( propertyUri );
        ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .entity( entity )
                .put( ClientResponse.class );

        System.out.println( String.format(
                "PUT [%s] to [%s], status code [%d]", entity, propertyUri,
                response.getStatus() ) );
        response.close();
    }
	
	private static String toJsonNameValuePairCollection(Map<String,String> property){
		
		Set<String> keys=property.keySet();
		Iterator<String> iter=keys.iterator();
		String outPut="{ ";
		while(iter.hasNext()){
			String key=iter.next();
			String value=property.get(key);
			
			outPut+="\""+key+"\" : \""+value+"\"";
			if(iter.hasNext()){
				outPut+=", ";
			}
		}
		outPut+="}";		
//		return "{ \"time\" : \"20120113\", \"title\" : \"test_title3\"}";		
		return outPut;
	}
	

	
	

}
