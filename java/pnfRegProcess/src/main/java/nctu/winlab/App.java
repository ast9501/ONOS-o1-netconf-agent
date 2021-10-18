package nctu.winlab;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.core.JsonProcessingException;

//import com.fasterxml.jackson.databind;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.lang.reflect.Array;

import java.nio.file.Paths;


/**
 * Hello world!
 *
 */

class Event {
    private String username;
    private String password;
    private String ip;
    private String port;

    @SuppressWarnings("unchecked")
    @JsonProperty("event")
    private void unpackedNested (Map<String, Object> event) {

        Map<String, Object> pnfRegField = (Map<String, Object>)event.get("pnfRegistrationFields");
        Map<String, String> addInfo = (Map<String, String>)pnfRegField.get("additionalFields");

        //Map<String, String> pnfRegField = (Map<String, String>)event.get("pnfRegistrationFields");

        this.password = (String)addInfo.get("password");
        this.port = (String)addInfo.get("oamPort");
        this.username = (String)addInfo.get("username");
        this.ip = (String)pnfRegField.get("oamV4IpAddress");
        
    }

    /*
    private Map<String, Object> other = new HashMap<>();

    @JsonAnySetter
    public void set(String key, Object value) {
        this.other.put(key, value);
    }
    */
    public String getIp() {
        return ip;
    }

    
    public String getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

public class App 
{
    public static void main( String[] args ) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // example .json file capture from DMaaP message
        Event[] events = mapper.readValue(Paths.get("/home/alan/work/onos-agent/java/pnfRegProcess/src/main/java/nctu/winlab/data.json").toFile(), Event[].class);
        
        for (int i = 0; i < events.length; i++){
            System.out.println("=====================");
            Event event = (Event)Array.get(events, i);
            System.out.println(event.getIp());
            System.out.println(event.getPort());
            System.out.println(event.getUsername());
            System.out.println(event.getPassword());
            System.out.println("=====================");
        }
        
        //System.out.println(events.getUsername());
        System.out.println( "Hello World!" );
    }
}

/*
class PnfRegField {
    private String num;
    private String username;
    private String password;

    @SuppressWarnings("unchecked")
    @JsonProperty("pnfRegField")
    private void unpackedPnf (Map<String, Object> pnf) {
        this.num = (String)pnf.get("num");
        Map<String, String> info = (Map<String, String>)pnf.get("addInfo");
        this.username = info.get("username");
    }


    private Map<String, Object> other = new HashMap<>();

    @JsonAnySetter
    public void set(String key, Object value) {
        this.other.put(key, value);
    }
    
    public String getUsername() {
        return username;
    }
}


public class App 
{
    public static void main( String[] args ) throws IOException
    {
        String sample = "{\"pnfRegField\": {\"num\": \"00\", \"addInfo\": {\"username\": \"a\", \"password\": \"b\"}}}";
        //String sample = "[\"event\": {}]"
        ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        PnfRegField pnfRegField =  mapper.readerFor(PnfRegField.class).readValue(sample);
        System.out.println(pnfRegField.getUsername());

        System.out.println( "Hello World!" );
    }
}

*/
 /*
class AdditionField {
    private String password;
    private String port;
    private String username;
    private Map<String, String> properties;

    @JsonCreator
	public Netconf (
		@JsonProperty("password") String password,
		@JsonProperty("oamPort") String port,
		@JsonProperty("username") String username
		) {
			this.port = port;
			this.username = username;
            this.password = password;
		}

    @JsonAnySetter
    public void add(String key, String value) {
        properties.put(key, value);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPort() {
        return port;
    }
}

class PnfRegFields {
    private AdditionField additionField;
    private String ip;

    private Map<String, String> properties;

    @JsonCreator
	public PnfRegFields (
		@JsonProperty("oamV4IpAddress") String ip,
		@JsonProperty("additionalFields") AdditionField additionField
		) {
			this.ip = ip;
			this.additionalField = additionalField;
		}
    @JsonAnySetter
    public void add(String key, String value) {
        properties.put(key, value);
    }


}

class ComEventHdr {
    private Map<String, String> properties;

    @JsonAnySetter
    public void add(String key, String value) {
        properties.put(key, value);
    }
}
 // describe event structure
class EventsParser {
	private ComEventHdr commonEventHeader;
    private PnfRegFields pnfRegistrationFields;



    @JsonCreator
	public DevicesParser (
		@JsonProperty("event") Object event
		) {
		this.event = event;
	}

	public String getEvent() {
		return event;
	}
}

public class App 
{
    public static void main( String[] args ) throws JsonParseException, JsonMappingException, IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        EventsParser[] eventsArray = mapper.readValue(jsonArray, EventsParser[].class);
        
        System.out.println( "Hello World!" );
    }
}
*/
