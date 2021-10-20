/*
 * Copyright 2021-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nctu.winlab;

import org.onosproject.cfg.ComponentConfigService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Properties;

import static org.onlab.util.Tools.get;

// New import package
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;


import java.io.OutputStream;
import java.util.Base64;
import java.net.MalformedURLException;

//import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.annotation.JsonAnySetter;
//import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;
//import java.util.HashMap;
//import java.util.List;
import java.lang.reflect.Array;
//import java.lang.StringBuffer;

//import java.nio.file.Paths;

/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true,
           service = {SomeInterface.class},
           property = {
               "someProperty=Some Default String Value",
           })
public class AppComponent implements SomeInterface {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /** Some configurable property. */
    private String someProperty;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected ComponentConfigService cfgService;

    @Activate
    protected void activate() {
        cfgService.registerProperties(getClass());
        log.info("Started");

        while (true) {
            GetDmaapMsg device = new GetDmaapMsg();

            /*
            *  Register DU to ONOS
            *
            */
            String ip = device.getDeviceIp();
            String port = device.getDevicePort();
            String username = device.getDeviceUsername();
            String password = device.getPassword();

            //String info = "netconf:" + ip + ":830";
            String payloadHdr = "{\"devices\": {\"netconf:" + ip + ":" + port + "\": {\"netconf\": {\"ip\": \"" + ip;
            String payloadMid = "\",\"port\": " + port + ",\"username\":\"" + username + "\",";
            String payloadButm = "\"password\" : \"" + password + "\"},\"basic\": {\"driver\": \"ovs-netconf\"}}}}";
            String payload = payloadHdr + payloadMid + payloadButm;
            log.info("Get new device! Encode payload: ");
            log.info(payload);

            // Establish the POST request
            String auth = "onos:rocks";
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeaderValue = "Basic " + new String(encodedAuth);

            try {
                URL url = new URL("http://127.0.0.1:8181/onos/v1/network/configuration");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                //con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Authorization", authHeaderValue);
                con.setDoOutput(true);

                OutputStream os = con.getOutputStream();
                byte[] input = payload.getBytes("utf-8");
                os.write(input, 0, input.length);
                System.out.println(con.getResponseCode());
            } catch (MalformedURLException e) {
                log.info("Failed to build URL link");
            } catch (IOException e) {
                log.info("Failed to connect to ONOS");
            } catch (Exception e) {
                log.info("Loss the connection with ONOS");
            }

        }
    }
    @Deactivate
    protected void deactivate() {
        cfgService.unregisterProperties(getClass(), false);
        log.info("Stopped");
    }

    @Modified
    public void modified(ComponentContext context) {
        Dictionary<?, ?> properties = context != null ? context.getProperties() : new Properties();
        if (context != null) {
            someProperty = get(properties, "someProperty");
        }
        log.info("Reconfigured");
    }

    @Override
    public void someMethod() {
        log.info("Invoked");
    }

}

class Event {
    private String username;
    private String password;
    private String ip;
    private String port;

    @SuppressWarnings("unchecked")
    @JsonProperty("event")
    private void unpackedNested(Map<String, Object> event) {

        Map<String, Object> pnfRegField = (Map<String, Object>) event.get("pnfRegistrationFields");
        Map<String, String> addInfo = (Map<String, String>) pnfRegField.get("additionalFields");

        //Map<String, String> pnfRegField = (Map<String, String>)event.get("pnfRegistrationFields");

        this.password = (String) addInfo.get("password");
        this.port = (String) addInfo.get("oamPort");
        this.username = (String) addInfo.get("username");
        this.ip = (String) pnfRegField.get("oamV4IpAddress");
    }

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

class GetDmaapMsg {
    private final Logger log = LoggerFactory.getLogger(getClass());

    String ip;
    String port;
    String username;
    String password;

    public GetDmaapMsg() {
        boolean status = true;
        while (status) {
            try {
                status = false;
                String dmaapAddr = "http://127.0.0.1:3904";
                String dmaapEvnt = "/events/unauthenticated.VES_PNFREG_OUTPUT/";
                String dmaapUser = "users/sdn-r?timeout=20000&limit=1";
                URL target = new URL(dmaapAddr + dmaapEvnt + dmaapUser);
                URLConnection conn = target.openConnection();

                // Not required?
                conn.setDoInput(true);

                conn.connect();

                InputStream response = conn.getInputStream();
                try (Scanner scanner = new Scanner(response)) {
                    String responseBody = scanner.useDelimiter("\\A").next();
                    //responseBody = responseBody.substring(2, responseBody.length() - 2);
                    StringBuffer str = new StringBuffer(responseBody);
                    str.setCharAt(1, ' ');
                    str.setCharAt(responseBody.length() - 2, ' ');
                    responseBody = str.toString();
                    responseBody = responseBody.replace("\\", "");

                    //log.info("The responseBody is: ");
                    //log.info(responseBody);

                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.configure(Feature.AUTO_CLOSE_SOURCE, true);
                        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
                        Event[] events = mapper.readValue(responseBody, Event[].class);

                        // capture PNF registration info.
                        for (int i = 0; i < events.length; i++) {
                            Event event = (Event) Array.get(events, i);
                            ip = event.getIp();
                            port = event.getPort();
                            username = event.getUsername();
                            password = event.getPassword();
                        }

                        if (ip == null || port == null || username == null || password == null) {
                            log.info("No device found, keep waiting...");
                            status = true;
                        } else {
                            log.info("<<<<<<<< PNF info >>>>>>>>");
                            log.info("ip: " + ip);
                            log.info("port number: " + port);
                            log.info("username: " + username);
                            log.info("password: " + password);
                            log.info("<<<<<<<<<< End >>>>>>>>>>>");
                            //conn.close();
                        }
                        //System.out.println(Jevent.toString());
                        //
                        //System.out.println("PNF info: ");
                    } catch (Exception e) {
                        //log.info(e.toString());
                        //log.info("The response body is: ");
                        //log.info(responseBody);
                        log.info("No device found, keep waiting...");
                        //System.out.println("Json parser failed");
                        status = true;
                    }
                    response.close();
                } catch (Exception e) {
                    log.info("Failed to read message from DMaaP");
                }
            } catch (IOException e) {
                status = true;
            }
        }
    }
    public String getDeviceIp() {
        return ip;
    }
    public String getDevicePort() {
        return port;
    }
    public String getDeviceUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}

