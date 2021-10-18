import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
//import java.lang.Byte;

import org.json.JSONObject;
import org.json.JSONException;

/*
 *	JSON object: https://www.tutorialspoint.com/org_json/org_json_quick_guide.htm
 *
*/
public class GetJson{
	public static void main(String[] args){
		try{
			URL target = new URL("http://127.0.0.1:8000/api/v1/status");
			URLConnection conn = target.openConnection();
			
			// Not required?
			conn.setDoInput(true);
			
			conn.connect();

			InputStream response = conn.getInputStream();

			/*
			 *	scanner.useDelimiter example:
			 *	https://www.javatpoint.com/post/java-scanner-usedelimiter-method
			 *
			*/

			 try (Scanner scanner = new Scanner(response)) {
                       		String responseBody = scanner.useDelimiter("\\A").next();
                        	JSONObject jsonObject = new JSONObject(responseBody);
				System.out.println(jsonObject.toString());
				System.out.println(jsonObject.getString("status"));
                	}
			response.close();
        	}
        	catch (IOException e){
                	System.out.println("IOException error!");
        	}
	}
}
