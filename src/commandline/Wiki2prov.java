package commandline;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.openrdf.rio.RDFFormat;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.resultset.ResultSetFormat;

import core.CreateProv;
import core.ReadXML;
import core.neo4j.Neo4jIndex;


public class Wiki2prov {

	public static String wikiUrl = "http://en.wikipedia.org/wiki/";
	private static String filename;
	private static String fileType;
	private static String page;
	private static String directory = "";
	//Default is to output to stdout
	private static Boolean stdout = true;
	private static Boolean redirects = false;
	//These (rvlimit and uclimit) are Strings for ReadXML.queryByArticle()
	private static String rvlimit = "1";
	private static String uclimit = "1";
	private static int depth = 1;
	private static String rvstartid = "";
	private static String rvstart = "";
	private static boolean diff = false;
	
	private static HashMap<String,String> uriStartid = new HashMap<String,String>();
	private static boolean neo4j;
	private static String server;

	
	//Lazy to throw Exception - I should really deal with them
	public static void main (String args []) throws Exception{
		
		
		
		Options options = new Options();
		options.addOption("f", true, "file listing URLs (http://en.wikipedia.org/wiki/{title}) or titles of wikipages to proccess (one per line or csv)");
		options.addOption("t", true,
				"file type of the input file, text, csv (title,startid,startdate), jena (result set where URL is a result var 'page'");

		options.addOption("p", true, "URL or title of a wiki-page for proccessing");
		options.addOption("o", true, "directory to output to (default is cwd)");
		options.addOption("r",true,"number of revisions");
		options.addOption("u",true,"number of user contributions");
		options.addOption("d",true,"depth");

		options.addOption("startid",true,"rvstartid: the numerical wikipedia revision id to start at");
		options.addOption("startdate",true,"rvstart: the timestamp to start at");
		options.addOption("diff",false,"diff: Evalaute diff between revisions (requires GNU wdiff)");
		options.addOption("neo4j",false,"neo4j: Use a neo4j store");
		options.addOption("server",true,"neo4j server: Use neo4j at address (default assumes localhost:7474)");
		options.addOption("h",false,"Help: display this usage info");
		
		
		//TODO support other language formats rather than assuming from my arrogant British imperialist point of view that everyone should
		//be using the english version of wikipedia!
		//	options.addOption("lang",true,"Alternative language verson of wikipedia: de,es etc");

	 	CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse(options, args);
		//The list of pages or URIs we will parse
		ArrayList<String> uris = new ArrayList<String>();
				
		if (cmd.hasOption('h')) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "wiki2prov", options );
			return;
		}
		
		if (cmd.hasOption('o')) {
			directory = cmd.getOptionValue('o');
			stdout = false;
		}
	
		if (cmd.hasOption('r')) {
			rvlimit = cmd.getOptionValue('r');
		}
		if (cmd.hasOption("startid")) {
			rvstartid = cmd.getOptionValue("startid");
		}
		if (cmd.hasOption("startdate")) {
			rvstart = cmd.getOptionValue("startdate");

		}
		if (cmd.hasOption("diff")) {
			diff = true;

		}
		if (cmd.hasOption("neo4j")) {
			neo4j = true;
		}
		if (cmd.hasOption("server")) {
			server = cmd.getOptionValue("server");
			Neo4jIndex.setServerRootURI(server);

		}
		if (cmd.hasOption('r')) {
			redirects = true;
		}
		
		if (cmd.hasOption('u')) {
			uclimit = cmd.getOptionValue('u');
		}
		//TODO Santiy check parseInt? Sanity check all arguments really! 
		if (cmd.hasOption('d')) {
			depth = Integer.parseInt(cmd.getOptionValue('d'));
		}
		if (cmd.hasOption('f')) {
			if (cmd.hasOption('t')) {
				filename = cmd.getOptionValue('f');
				fileType = cmd.getOptionValue('t');
				if (fileType.equals("jena")) {
					ResultSet rs = ResultSetFactory.load(filename,
							ResultSetFormat.syntaxRDF_N3);
					uris = listFromResultSet(rs, "page");
				}
				if (fileType.equals("text")) {
					uris = listFromTextFile(filename);
				}
				if (fileType.equals("csv")) {
					uris = listFromCSVFile(filename);
				}
			}
		} else if (cmd.hasOption('p')) {
			page = cmd.getOptionValue('p');
			uris.add(page);
		}

		ReadXML.generatePROV(true);
		ReadXML.useNeo4j(neo4j);
		ReadXML.generatePROV(!neo4j);
		ReadXML.generateDiff(diff);

		//TODO allow for different prov model types (Prov-JSON, Prov-N)
		
		for (String uri : uris) {

			String title;
			
			if(uriStartid.containsKey(uri))
				rvstartid = uriStartid.get(uri);
			

				if(uri.startsWith("http")){
				title = uri
						.substring(wikiUrl.length(), uri.length());
				}
				else{
					title = uri;
				}

				
				//TODO add argument to allow choice of RDF formats - currently defaults to ttl 
				String out = ReadXML.queryByArticle(title, rvlimit, rvstartid, rvstart, depth, uclimit, RDFFormat.TURTLE);
				if(stdout){
					System.out.println(out);
				}else {
					String filename = null;
					try {
						if( !rvstart.equalsIgnoreCase("")){
							rvstart = rvstart.replaceAll(":", "-");
							filename = title + "-start" + rvstart + "-r" + rvlimit +"-u" + uclimit + "-d" + depth + ".ttl";
							}else 
						if( !rvstartid.equalsIgnoreCase("")){
						filename = title + "-startid" + rvstartid + "-r" + rvlimit +"-u" + uclimit + "-d" + depth + ".ttl";
						}else{
					    filename = title + "-r" + rvlimit +"-u" + uclimit + "-d" + depth + ".ttl";
						}
						if (directory != null && !directory.equals("")) {
							FileWriter writer = new FileWriter(directory + "/" + filename);
							writer.write(out);
							writer.close();
						} else {
							FileWriter writer = new FileWriter(filename);
							writer.write(out);
							writer.close();
						}
					} catch (Exception e) {
						System.out.println("Failed to write: " + filename
								+ " " + e.getCause());
					}
				}
				
		}
		
		
		
	}
	
	
	
	// Extracts the specific 'resource' of a ResultSet into an ArrayList
	public static ArrayList<String> listFromResultSet(ResultSet rs,
			String resource) {

		ArrayList<String> uris = new ArrayList<String>();
		while (rs.hasNext()) {

			QuerySolution sol = rs.next();
			Resource pageResource = sol.getResource(resource);
			String pageString = pageResource.toString();
			uris.add(pageString);
		}
		return uris;
	}
	
	public static ArrayList<String> listFromTextFile(String filename)
			throws Exception {

		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(filename)));
		ArrayList<String> uris = new ArrayList<String>();
		String line;
		while ((line = br.readLine()) != null) {
			uris.add(line);
		}
		
		br.close();
		return uris;
	}
	
	
	public static ArrayList<String> listFromCSVFile(String filename)
			throws Exception {

		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(filename)));
		ArrayList<String> uris = new ArrayList<String>();
		String line;
		while ((line = br.readLine()) != null) {
		 String[] parts = line.split(" |,");
		 uris.add(parts[0]);
		 if(parts.length > 1){
			uriStartid.put(parts[0],parts[1]); 
		 }
		}
		
		br.close();
		return uris;
	}
	
}
