import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Base64;
/*
 *	JSON object: https://www.tutorialspoint.com/org_json/org_json_quick_guide.htm
 *
*/
public class GetDmaapMsg {
	String ip;
	String port;
        String username;
	public GetDmaapMsg(){
		boolean status = true;

                //String ip;
                //String port;
                //String username;

                while(status){
                        try{
                                status = false;
                                URL target = new URL("http://192.168.0.110:3904/events/unauthenticated.VES_PNFREG_OUTPUT/users/sdn-r?timeout=20000&limit=1");
                                URLConnection conn = target.openConnection();

                                // Not required?
                                conn.setDoInput(true);

                                conn.connect();

                                InputStream response = conn.getInputStream();

                        /*
                         *      scanner.useDelimiter example:
                         *      https://www.javatpoint.com/post/java-scanner-usedelimiter-method
                         *
			*/
			       try (Scanner scanner = new Scanner(response)) {
                                        String responseBody = scanner.useDelimiter("\\A").next();
                                        //System.out.println(responseBody);
                                        String responseJson = responseBody.substring(2, responseBody.length()-2);
                                        responseJson = responseJson.replace("\\", "");
                                        //System.out.println(responseJson);
                                        try {
                                                JSONObject object = new JSONObject(responseJson);
                                                //System.out.println(object.toString());

                                                JSONObject Jevent = object.getJSONObject("event");
                                                JSONObject JpnfRegistrationFields = Jevent.getJSONObject("pnfRegistrationFields");
                                                JSONObject JadditionalFields = JpnfRegistrationFields.getJSONObject("additionalFields");

                                                //System.out.println(Jevent.toString());
                                                //
                                                System.out.println("PNF info: ");

                                                ip = JpnfRegistrationFields.getString("oamV4IpAddress");
                                                System.out.println(ip);

                                                port = JadditionalFields.getString("oamPort");
                                                System.out.println(port);

                                                username = JadditionalFields.getString("username");
                                                System.out.println(username);

                                                System.out.println("---------------");

                                        }catch (Exception pe){
                                                System.out.println("Json parser failed");
                                        }
                                }
                                response.close();
                        }
                        catch (IOException e){
                                status = true;
			}
		}
	}
	public String getDeviceIp(){
		return ip;
	}
	public String getDevicePort(){
		return port;
	}
	public String getDeviceUsername(){
		return username;
	}

	public static void main(String[] args) throws IOException {
		
		GetDmaapMsg device = new GetDmaapMsg();

		/*
                 *  Register DU to ONOS
                 *
                */
		String ip = device.getDeviceIp();
		String port = device.getDevicePort();
		String username = device.getDeviceUsername();

                String info = "netconf:" + ip + ":830";
		LinkedHashMap<String, String> netconfMap = new LinkedHashMap<>();

		String payload = "{\"devices\": {\"netconf:" + ip + ":" + port + "\": {\"netconf\": {\"ip\":\"" + ip + "\",\"port\":" + port + ",\"username\":\"" + username + "\",\"password\": \"netconf!\"},\"basic\": {\"driver\": \"ovs-netconf\"}}}}";

		//netconfMap.put("ip", ip);
		//netconfMap.put("port", port);
		//netconfMap.put("username", username);
		//netconfMap.put("password", "netconf!");

                JSONObject JsonPayload = new JSONObject(payload);
                //JSONObject devices = new JSONObject(new LinkedHashMap());
                //JSONObject deviceInfo = new JSONObject(new LinkedHashMap());
                //JSONObject netconf = new JSONObject(netconfMap);
                //JSONObject basicDriver = new JSONObject(new LinkedHashMap());

                //basicDriver.put("driver", "ovs-netconf");
                
                //deviceInfo.put("netconf", netconf);
                //deviceInfo.put("basic", basicDriver);

                //devices.put(info, deviceInfo);

                //payload.put("devices", devices);
                System.out.println("Encode payload: ");
		System.out.println(payload);
                System.out.println(JsonPayload.toString());

		String auth = "onos:rocks";
		//byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
		String authHeaderValue = "Basic " + new String(encodedAuth);

		URL url = new URL ("http://192.168.0.110:8181/onos/v1/network/configuration");
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("Authorization", authHeaderValue);
                con.setDoOutput(true);

		try(OutputStream os = con.getOutputStream()) {
                        byte[] input = payload.getBytes("utf-8");
                        os.write(input, 0, input.length);
                }
		/*
                try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine = null;
                        while ((responseLine = br.readLine()) != null) {
                                response.append(responseLine.trim());
                        }
                        System.out.println(response.toString());
                }
		*/

		// POST data to Django (test)
		// https://www.baeldung.com/httpurlconnection-post
		/*
		URL url = new URL ("http://localhost:8000/api/v1/postData");
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json; utf-8");
		con.setRequestProperty("Accept", "application/json");
		con.setDoOutput(true);

		String jsonInputString = "{\"data\":\"test message send by Java\"}";
		try(OutputStream os = con.getOutputStream()) {
    			byte[] input = jsonInputString.getBytes("utf-8");
    			os.write(input, 0, input.length);			
		}
		try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
    			StringBuilder response = new StringBuilder();
    			String responseLine = null;
    			while ((responseLine = br.readLine()) != null) {
        			response.append(responseLine.trim());
    			}
    			System.out.println(response.toString());
		}
		*/
	}
}
