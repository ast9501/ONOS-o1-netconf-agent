import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import java.lang.Byte;

public class GetUrl{
	public static void main(String[] args){
		try{
                	//URL target = new URL("http://127.0.0.1:8000/api/v1/status");
			//HttpURLConnection conn = (HttpURLConnection)target.openConnection();
                	//conn.connect();
			//conn.setRequestMethod("GET");
                	//InputStream response = conn.openStream();
			
			URL target = new URL("http://127.0.0.1:8000/api/v1/status");
			URLConnection conn = target.openConnection();
			//HttpURLConnection conn = (HttpURLConnection)target.openConnection();
			
			// Not required?
			conn.setDoInput(true);
			
			conn.connect();

			InputStream response = conn.getInputStream();

			//InputStream response = new URL("http://127.0.0.1:8000/api/v1/status").openStream();
			//byte[] data;
			//int i = response.read(data);
			//String str = new String(data, StandardCharsets.UTF_8);
			//System.out.println(str);
			
			/*
			 *	scanner.useDelimiter example:
			 *	https://www.javatpoint.com/post/java-scanner-usedelimiter-method
			 *
			*/
			 try (Scanner scanner = new Scanner(response)) {
                       		String responseBody = scanner.useDelimiter("\\A").next();
                        	System.out.println(responseBody);
                	}
			
			response.close();
                	//byte[] ioByte;
                	//int numByte;
                	//numByte = response.read(ioByte);
        	}
        	catch (IOException e){
                	System.out.println("IOException error!");
        	}
	}
}
