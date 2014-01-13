package commandline;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;

import testInte.CreateProv;
import testInte.ReadXML;
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
	
	//Lazy to throw Exception - I should really deal with them
	public static void main (String args []) throws Exception{
		
		
		
		Options options = new Options();
		options.addOption("f", true, "file listing URLs (http://en.wikipedia.org/wiki/{title}) or titles of wikipages to proccess (one per line)");
		options.addOption("t", true,
				"file type of the input file, text, csv, jena (result set where URL is a result var 'page'");
		options.addOption("p", true, "URL or title of a wiki-page for proccessing");
		options.addOption("o", true, "directory to output to (default is cwd)");
		options.addOption("r",true,"number of revisions");
		options.addOption("u",true,"number of user contributions");
		options.addOption("d",true,"depth");
		options.addOption("h",false,"Help: display this usage info");
		//TODO support other language formats rather than assuming from my arrogant imperialist point of view that everyone should
		//be using the english version of wikipedia
		//	options.addOption("lang",true,"Alternative language verson of wikipedia: de,es etc");

	//Not sure how redirects are dealt with atm	
	//	options.addOption("r", false,
	//			"Attempt to deal with Wikipedia redirects");

		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse(options, args);

		ArrayList<String> uris = new ArrayList<String>();

		
		
		if (cmd.hasOption('h')) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "wiki2prov", options );
			return;
		}
		
		if (cmd.hasOption('o')) {
			directory = cmd.getOptionValue('o');
		}
		if (cmd.hasOption('r')) {
			redirects = true;
		}
		
		if (cmd.hasOption('r')) {
			rvlimit = cmd.getOptionValue('r');
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
			}
		} else if (cmd.hasOption('p')) {
			page = cmd.getOptionValue('p');
			uris.add(page);
		}
		
		ReadXML.generatePROV(true);
		ReadXML.useNeo4j(false);
		//TODO allow for different prov model types (Prov-JSON, Prov-N)
		//Requires missing support in CreateProv
		//CreateProv.setModelType(ModelType.PROV_N)
		
		for (String uri : uris) {
			String title;	
			//Is the uri a page title or page url?
				if(uri.startsWith("http")){
				title = uri
						.substring(wikiUrl.length(), uri.length());
				}
				else{
					title = uri;
				}
				//TODO add argument to allow choice of RDF formats
				
				String out = ReadXML.queryRDFByArticle(title, rvlimit, depth, uclimit, RDFFormat.TURTLE);
				if(stdout){
					System.out.println(out);
				}else {
					
					try {
						String filename = title + "-r" + rvlimit +"-u" + uclimit + "-d" + depth;
						
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
	
	
}
