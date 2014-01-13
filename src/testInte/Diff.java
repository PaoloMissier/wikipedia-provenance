package testInte;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 *  @author matthewgamble
 *  Class to perform a word diff for two strings.
 *  Currently requires the wdiff binary to be available on the system. 
 *  Would be nicer to have a java based implementation of wdiff 
 *
 */


public class Diff{
	
	protected int words;
	protected int common;
	protected int deleted;
	protected int changed;
	
	public int getWords() {
		return words;
	}


	protected void setWords(int words) {
		this.words = words;
	}


	public int getCommon() {
		return common;
	}


	protected void setCommon(int common) {
		this.common = common;
	}


	public int getDeleted() {
		return deleted;
	}


	protected void setDeleted(int deleted) {
		this.deleted = deleted;
	}


	public int getChanged() {
		return changed;
	}


	protected void setChanged(int changed) {
		this.changed = changed;
	}

	protected Diff(int words, int common, int deleted, int changed){
		this.words = words;
		this.common = common;
		this.deleted = deleted;
		this.changed = changed;
	}
	
	
	public static Diff generateDiff(String o, String n){
		
		int words = 0, common = 0, deleted = 0, changed = 0;
		 
		  try
	        {
			   //write old and new to file and use wdiff
			  	Path original = Files.createTempFile("prov", "Original-wdiff");
			  	Path newer = Files.createTempFile("prov", "New-wdiff");
			  	Files.write(original, o.getBytes());
			  	Files.write(newer, n.getBytes());
			  	
	            Runtime r = Runtime.getRuntime();
	            Process p = r.exec("wdiff --no-deleted --no-inserted --no-common -s " + original.toAbsolutePath().toString() + " " + newer.toAbsolutePath().toString());
	            
	            BufferedReader errorReader =  new BufferedReader(new InputStreamReader(p.getErrorStream()));
	            BufferedReader outputReader =  new BufferedReader(new InputStreamReader(p.getInputStream()));
	           
	            String line = null;
	            ArrayList<String> outputLines = new ArrayList<String>();
	            while ((line = outputReader.readLine()) != null){
	            	outputLines.add(line);
	            }
	            while ((line = errorReader.readLine()) != null){
	            	System.err.println(line);
	            }
	            
	            int exitVal = p.waitFor();
	            //System.err.println("Diff exit status: " + exitVal);
	           
	            String details = null;
	            if((details = outputLines.get(1)) != null){
	            	System.err.println(details);
	            	String[] detailsSplit = details.split("\\s");
	            	words = Integer.parseInt(detailsSplit[1]);
	            	common = Integer.parseInt(detailsSplit[4]);
	            	deleted = Integer.parseInt(detailsSplit[8]);
	            	changed = Integer.parseInt(detailsSplit[12]);
	            }
	            
	        } catch (Throwable t)
	          {
	        	System.err.println("Error Diff-ing revisions");
	            t.printStackTrace();
	          }

		 
 
		  Diff result = new Diff(words,common,deleted,changed);
		  
		  return result;
		}
	
}