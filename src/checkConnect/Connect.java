package checkConnect;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class Connect {
private static final String SERVER_ROOT_URI="http://localhost:7474/db/data/";
	
	public static void checkDatabaseIsRunning(){
		WebResource resource = Client.create().resource(SERVER_ROOT_URI);
		ClientResponse response = resource.get(ClientResponse.class);
		
		System.out.println(String.format("GET on [%s], status code [%d]",SERVER_ROOT_URI, response.getStatus()));
		response.close();
	}

}
