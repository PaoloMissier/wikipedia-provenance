package testInte;

import org.codehaus.jettison.json.JSONException;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

import findVandalism.QuerySpecialNode;

public class testConnToquerySpecialNode {

	/**
	 * @param args
	 * @throws JSONException 
	 * @throws UniformInterfaceException 
	 * @throws ClientHandlerException 
	 */
	public static void main(String[] args) throws ClientHandlerException, UniformInterfaceException, JSONException {
		
		//String revid = "488426475";
		String revid = "11";
		String totalNodeNumber = "50";
		QuerySpecialNode queryspecialnode = new QuerySpecialNode();
		//String specialNodeUrl = queryspecialnode.queryArticleNode(revid, totalNodeNumber);
		//System.out.println("specialNodeUrl:" + specialNodeUrl);

	}

}
