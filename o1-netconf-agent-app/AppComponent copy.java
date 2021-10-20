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

import org.json.JSONObject;
import java.io.OutputStream;
import java.util.Base64;
import java.net.MalformedURLException;


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
        GetDmaapMsg device = new GetDmaapMsg();

                /*
                 *  Register DU to ONOS
                 *
                */
                String ip = device.getDeviceIp();
                String port = device.getDevicePort();
                String username = device.getDeviceUsername();

                //String info = "netconf:" + ip + ":830";
                String payloadHdr = "{\"devices\": {\"netconf:" + ip + ":" + port + "\": {\"netconf\": {\"ip\":\"" + ip;
                String payloadMid = "\",\"port\":" + port + ",\"username\":\"" + username;
                String payloadButm = "\",\"password\": \"netconf!\"},\"basic\": {\"driver\": \"ovs-netconf\"}}}}";
                String payload = payloadHdr + payloadMid + payloadButm;
                log.info("Get new device! Encode payload: ");
                //System.out.println("Encode payload: ");
                log.info(payload);
                //System.out.println(payload);

                String auth = "onos:rocks";
                byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
                String authHeaderValue = "Basic " + new String(encodedAuth);

        try {
                URL url = new URL("http://192.168.0.110:8181/onos/v1/network/configuration");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Authorization", authHeaderValue);
                con.setDoOutput(true);

                OutputStream os = con.getOutputStream();
                byte[] input = payload.getBytes("utf-8");
                os.write(input, 0, input.length);
        } catch (MalformedURLException e) {
                log.info("Failed to build URL link");
        } catch (IOException e) {
                log.info("Failed to connect to ONOS");
        } catch (Exception e) {
                log.info("Loss the connection with ONOS");
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

class GetDmaapMsg {
    private final Logger log = LoggerFactory.getLogger(getClass());

    String ip;
    String port;
    String username;
    public GetDmaapMsg() {
        boolean status = true;
        while (status) {
            try {
                status = false;
                String dmaapAddr = "http://192.168.0.110:3904";
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
                    //System.out.println(responseBody);
                    String responseJson = responseBody.substring(2, responseBody.length() - 2);
                    responseJson = responseJson.replace("\\", "");
                    //System.out.println(responseJson);
                    try {
                        JSONObject object = new JSONObject(responseJson);
                        //System.out.println(object.toString());

                        JSONObject jEvent = object.getJSONObject("event");
                        JSONObject jPnfRegistrationFields = jEvent.getJSONObject("pnfRegistrationFields");
                        JSONObject jAdditionalFields = jPnfRegistrationFields.getJSONObject("additionalFields");

                        //System.out.println(Jevent.toString());
                        //
                        //System.out.println("PNF info: ");
                        log.info("PNF info: ");

                        ip = jPnfRegistrationFields.getString("oamV4IpAddress");
                        //System.out.println(ip);
                        log.info("ip: " + ip);

                        port = jAdditionalFields.getString("oamPort");
                        //System.out.println(port);
                        log.info("port number: " + port);

                        username = jAdditionalFields.getString("username");
                        System.out.println(username);
                        log.info("username: " + username);

                        //System.out.println("---------------");

                    } catch (Exception e) {
                        log.info("JSON parser failed");
                        //System.out.println("Json parser failed");
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
}

