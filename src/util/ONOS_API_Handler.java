package util;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

/**
 * Created by netcsnuc on 12/8/16.
 */
public class ONOS_API_Handler {
    //private static String Controller_IP="203.237.53.145:8181";
    public static int MAX=100;
    ResourcePool_Manager resource = ResourcePool_Manager.getInstance();

    public ResourcePool_Manager.flow_Info_list[] GET_ONOS_INFO_Flow(ResourcePool_Manager.flow_Info_list[] list) throws IOException, ParseException{

        String DEVICE_API_URL= "http://"+resource.Controller_IP_Port+"/onos/v1/flows";
        URL onos = null;

        String buffer = URL_REQUEST(resource.Controller_ID,resource.Controller_Pw,DEVICE_API_URL,onos);
        org.json.simple.parser.JSONParser jsonParser = new org.json.simple.parser.JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(buffer);
        JSONArray InfoArray = (JSONArray) jsonObject.get("flows");

        for (int i=0;i<InfoArray.size();i++){
            JSONObject Object = (JSONObject) InfoArray.get(i);
            list[i].id = Object.get("id").toString();
            list[i].appId = Object.get("appId").toString();
            list[i].priority = Object.get("priority").toString();
            list[i].timeout = Object.get("timeout").toString();
            list[i].isPermanent = Object.get("isPermanent").toString();
            list[i].deviceId = Object.get("deviceId").toString();
            list[i].state = Object.get("state").toString();
            list[i].life = Object.get("life").toString();
            list[i].packets = Object.get("packets").toString();
            list[i].bytes = Object.get("bytes").toString();

            JSONObject Object2 = (JSONObject) Object.get("treatment");
            JSONArray InfoArray2 = (JSONArray) Object2.get("instructions");
            for (int j=0;j<InfoArray2.size();j++){
                JSONObject Object3 = (JSONObject) InfoArray2.get(j);
                list[i].type = Object3.get("type").toString();
                list[i].port = Object3.get("port").toString();
            }
            Object2 = (JSONObject) Object.get("selector");
            //System.out.println("sele "+Object2.toJSONString());
            InfoArray2 = (JSONArray) Object2.get("criteria");
            for (int j=0;j<InfoArray2.size();j++){
                JSONObject Object3 = (JSONObject) InfoArray2.get(j);
                if (Object3.get("ethType")!=null)
                    list[i].ethType = Object3.get("ethType").toString();
                list[i].ctype = Object3.get("type").toString();
            }
        }
        return list;
    }

    public ResourcePool_Manager.intents_Info_list[] GET_ONOS_Info_Intent(ResourcePool_Manager.intents_Info_list[] list) throws IOException, ParseException {
        String USERNAME= "karaf";
        String PASSWORD = "karaf";
        String DEVICE_API_URL= "http://"+resource.Controller_IP_Port+"/onos/v1/intents";
        URL onos = null;

        String buffer = URL_REQUEST(USERNAME,PASSWORD,DEVICE_API_URL,onos);
        org.json.simple.parser.JSONParser jsonParser = new org.json.simple.parser.JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(buffer);
        JSONArray InfoArray = (JSONArray) jsonObject.get("intents");

        for (int i =0; i<InfoArray.size();i++){
            JSONObject Object = (JSONObject) InfoArray.get(i);
            list[i].type = Object.get("type").toString();
            list[i].id = Object.get("id").toString();
            list[i].appId = Object.get("appId").toString();
            list[i].state = Object.get("state").toString();
        }
        return list;
    }

    public ResourcePool_Manager.link_Info_list[] GET_ONOS_INFO_Link(ResourcePool_Manager.link_Info_list[] list) throws IOException, ParseException {
        String USERNAME= "karaf";
        String PASSWORD = "karaf";
        String DEVICE_API_URL= "http://"+resource.Controller_IP_Port+"/onos/v1/links";
        URL onos = null;

        String buffer = URL_REQUEST(USERNAME,PASSWORD,DEVICE_API_URL,onos);
        org.json.simple.parser.JSONParser jsonParser = new org.json.simple.parser.JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(buffer);
        JSONArray InfoArray = (JSONArray) jsonObject.get("links");

        for (int i=0; i<InfoArray.size();i++){
            JSONObject Object = (JSONObject) InfoArray.get(i);
            list[i].type = Object.get("type").toString();
            list[i].state = Object.get("state").toString();
            JSONObject Object2 = (JSONObject) Object.get("src");
            list[i].src = Object2.get("device").toString();
            list[i].src += "/"+Object2.get("port").toString();

            JSONObject Object3 = (JSONObject) Object.get("dst");
            list[i].dst = Object3.get("device").toString();
            list[i].dst += "/"+Object3.get("port").toString();

        }
        return list;
    }


    public ResourcePool_Manager.host_Info_list[] GET_ONOS_INFO_Host(ResourcePool_Manager.host_Info_list[] list) throws IOException, ParseException {
        String USERNAME= "karaf";
        String PASSWORD = "karaf";
        String DEVICE_API_URL= "http://"+resource.Controller_IP_Port+"/onos/v1/hosts";
        URL onos = null;

        String buffer = URL_REQUEST(USERNAME,PASSWORD,DEVICE_API_URL,onos);
        org.json.simple.parser.JSONParser jsonParser = new org.json.simple.parser.JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(buffer);
        JSONArray InfoArray = (JSONArray) jsonObject.get("hosts");

        for (int i =0; i<InfoArray.size();i++){
            JSONObject Object = (JSONObject) InfoArray.get(i);
            list[i].ID = Object.get("id").toString();
            list[i].MAC = Object.get("mac").toString();
            list[i].vlan = Object.get("vlan").toString();
            int ip_len = Object.get("ipAddresses").toString().length();

            if (ip_len>3){
                list[i].IP = Object.get("ipAddresses").toString().subSequence(2,ip_len-2).toString();
            }
                //System.out.println("^^ IP: "+Object.get("ipAddresses").toString().subSequence(2,ip_len-2));

            /*
            if (Object.get("ipAddresses").toString().length()==18)
                list[i].IP = Object.get("ipAddresses").toString().subSequence(2,ip_len-2).toString();
            else if (Object.get("ipAddresses").toString().length()==17)
                list[i].IP = Object.get("ipAddresses").toString().substring(2,15);
            else if (Object.get("ipAddresses").toString().length()==16)
                list[i].IP = Object.get("ipAddresses").toString().substring(2,14);
            */
            JSONObject Object2 = (JSONObject) Object.get("location");
            list[i].location = Object2.get("elementId").toString();
            list[i].location += "/"+Object2.get("port").toString();
        }
        return list;
    }


    public ResourcePool_Manager.switch_Info_list[] GET_ONOS_INFO_Switch(ResourcePool_Manager.switch_Info_list[] list) throws IOException, ParseException {
        String USERNAME= "karaf";
        String PASSWORD = "karaf";
        String DEVICE_API_URL= "http://"+resource.Controller_IP_Port+"/onos/v1/devices";
        URL onos = null;

        String buffer = URL_REQUEST(USERNAME,PASSWORD,DEVICE_API_URL,onos);
        org.json.simple.parser.JSONParser jsonParser = new org.json.simple.parser.JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(buffer);
        JSONArray InfoArray = (JSONArray) jsonObject.get("devices");

        for (int i =0;i<InfoArray.size();i++)
        {
            //System.out.println("=device_"+i+"---------------------------------------");

            JSONObject Object = (JSONObject) InfoArray.get(i);
            /*
            System.out.println("device Info: ID==>"+Object.get("id"));
            System.out.println("device Info: type==>"+Object.get("type"));
            System.out.println("device Info: manufactory==>"+Object.get("mfr"));
            System.out.println("device Info: hardware==>"+Object.get("hw"));
            System.out.println("device Info: version==>"+Object.get("sw"));
            System.out.println("device Info: chassisID==>"+Object.get("chassisId"));
            */

            list[i].DPID = Object.get("id").toString();
            String url = DEVICE_API_URL+"/"+Object.get("id").toString()+"/ports";
            String buffer2 = URL_REQUEST(USERNAME,PASSWORD,url,onos);

            JSONObject Object2 = (JSONObject) jsonParser.parse(buffer2);
            JSONArray InfoArray2 =(JSONArray) Object2.get("ports");

            for (int j =0; j<InfoArray2.size();j++){
                if (Object.get("id").equals(Object2.get("id")))
                {
                    JSONObject Object3 = (JSONObject) InfoArray2.get(j);
                    JSONObject Object4 = (JSONObject) Object3.get("annotations");
                    //System.out.println("device Info: portName==>"+Object4.get("portName"));
                    //System.out.println("device Info: portNumber==>"+Object3.get("port"));
                    //System.out.println("J count: "+j);
                    list[i].port_name[j] = Object4.get("portName").toString();
                    list[i].port_number[j] = Object3.get("port").toString();
                    list[i].hardware = Object.get("hw").toString();
                    list[i].type = Object.get("type").toString();
                    list[i].chassisID = Object.get("chassisId").toString();
                    list[i].version = Object.get("sw").toString();
                    list[i].manufactory = Object.get("mfr").toString();
                }
            }
        }
        return list;
    }

    public ResourcePool_Manager.path_INFO_list[] GET_ONOS_INFO_Path(ResourcePool_Manager.path_INFO_list[] list, String service) throws  IOException, ParseException {
        String PATH_API_URL= "http://"+resource.Controller_IP_Port+"/onos/v1/paths/";
        String ORIGIN= "http://"+resource.Controller_IP_Port+"/onos/v1/paths/";
        URL onos = null;
        String[] producer = new String[MAX];
        String[] broker = new String[MAX];
        String[] zookeeper = new String[MAX];
        String[] consumer = new String[MAX];


        ResourcePool_Manager resource = ResourcePool_Manager.getInstance();
        ResourcePool_Manager.serviceHosts_INFO_list[] shlist = resource.getServiceHost_INFO_list();

        for (int i=0;i<shlist.length;i++) {
            if (shlist[i].ID!=null && service.equals(shlist[i].Service)){
                if (shlist[i].Type.equals("producer")){
                    String s = shlist[i].ID;
                    s = s.replace(":","%3A");
                    s = s.replace("/","%2F");
                    producer[i] = s;
                }
                if (shlist[i].Type.equals("broker")){
                    String s = shlist[i].ID;
                    s = s.replace(":","%3A");
                    s = s.replace("/","%2F");
                    broker[i] = s;
                }
                if (shlist[i].Type.equals("consumer")){
                    String s = shlist[i].ID;
                    s = s.replace(":","%3A");
                    s = s.replace("/","%2F");
                    consumer[i] = s;
                }
                if (shlist[i].Type.equals("zookeeper")){
                    String s = shlist[i].ID;
                    s = s.replace(":","%3A");
                    s = s.replace("/","%2F");
                    zookeeper[i] = s;
                }
            }
        }

        //making path
        for (int i =0; i<MAX;i++){
            if (shlist[i].ID!=null && service.equals(shlist[i].Service)){
                if (producer[i]!=null){
                    for (int j=0; j<MAX;j++){
                        if (broker[j]!=null){
                            PATH_API_URL += producer[i]+"/" + broker[j];
                            //System.out.println("PATH_API_URL: "+PATH_API_URL);
                            String buffer = URL_REQUEST(resource.Controller_ID,resource.Controller_Pw,PATH_API_URL,onos);
                            org.json.simple.parser.JSONParser jsonParser = new org.json.simple.parser.JSONParser();
                            JSONObject jsonObject = (JSONObject) jsonParser.parse(buffer);
                            JSONArray InfoArray = (JSONArray) jsonObject.get("paths");

                            for (int k=0;k<InfoArray.size();k++){
                                JSONObject object = (JSONObject) InfoArray.get(k);
                                list[k].cost = object.get("cost").toString();
                                JSONArray InfoArray2 = (JSONArray) object.get("links");
                                for (int t=0; t<InfoArray2.size();t++){
                                    JSONObject Object2 = (JSONObject) InfoArray2.get(t);
                                    String path = Object2.get("src").toString();
                                    path += "%"+Object2.get("dst");
                                    //list[j].p_b_path[t] = Object2.get("src").toString();
                                    //list[j].p_b_path[t] += "%"+Object2.get("dst");
                                    list[j].description = "Producer <-> Broker";
                                    list[i].service = service;
                                    list[j].p_b_list.add(path);
                                }
                            }
                            //System.out.println("Path for Service: "+service);
                            //System.out.println("Producer <-> Broker "+buffer);
                            PATH_API_URL = ORIGIN;
                        }
                    }
                    for (int j=0; j<MAX;j++){
                        if (zookeeper[j]!=null){
                            PATH_API_URL += producer[i]+"/" + zookeeper[j];
                            //System.out.println("PATH_API_URL: "+PATH_API_URL);
                            String buffer = URL_REQUEST(resource.Controller_ID,resource.Controller_Pw,PATH_API_URL,onos);
                            org.json.simple.parser.JSONParser jsonParser = new org.json.simple.parser.JSONParser();
                            JSONObject jsonObject = (JSONObject) jsonParser.parse(buffer);
                            JSONArray InfoArray = (JSONArray) jsonObject.get("paths");

                            for (int k=0;k<InfoArray.size();k++){
                                JSONObject object = (JSONObject) InfoArray.get(k);
                                list[k].cost = object.get("cost").toString();
                                JSONArray InfoArray2 = (JSONArray) object.get("links");
                                for (int t=0; t<InfoArray2.size();t++){
                                    JSONObject Object2 = (JSONObject) InfoArray2.get(t);
                                    String path = Object2.get("src").toString();
                                    path += "%"+Object2.get("dst");
                                    //list[j].p_z_path[t] = Object2.get("src").toString();
                                    //list[j].p_z_path[t] += "%"+Object2.get("dst");
                                    list[j].description = "Producer <-> Zookeeper";
                                    list[i].service = service;
                                    list[j].p_z_list.add(path);
                                }
                            }

                            //System.out.println("Path for Service: "+service);
                            //System.out.println("Producer <-> Zookeeper "+buffer);
                            PATH_API_URL = ORIGIN;
                        }
                    }
                }
                if (broker[i]!=null){
                    for (int j=0;j<MAX;j++){
                        if (consumer[j]!=null){
                            PATH_API_URL +=broker[i]+"/" +consumer[j];
                            //System.out.println("PATH_API_URL: "+PATH_API_URL);
                            String buffer = URL_REQUEST(resource.Controller_ID,resource.Controller_Pw,PATH_API_URL,onos);
                            org.json.simple.parser.JSONParser jsonParser = new org.json.simple.parser.JSONParser();
                            JSONObject jsonObject = (JSONObject) jsonParser.parse(buffer);
                            JSONArray InfoArray = (JSONArray) jsonObject.get("paths");

                            for (int k=0;k<InfoArray.size();k++){
                                JSONObject object = (JSONObject) InfoArray.get(k);
                                list[k].cost = object.get("cost").toString();
                                JSONArray InfoArray2 = (JSONArray) object.get("links");
                                for (int t=0; t<InfoArray2.size();t++){
                                    JSONObject Object2 = (JSONObject) InfoArray2.get(t);
                                    String path = Object2.get("src").toString();
                                    path += "%"+Object2.get("dst");
                                    //list[j].b_c_path[t] = Object2.get("src").toString();
                                    //list[j].b_c_path[t] += "%"+Object2.get("dst");
                                    list[j].description = "Broker <-> Consumer";
                                    list[i].service = service;

                                    //System.out.println("^^path: "+path);
                                    list[i].b_c_list.add(path);
                                    //System.out.println(" b_c_path: "+list[j].b_c_path[t]);
                                    //System.out.println("K: "+k+" t: "+t+" J: "+j+" I: "+i);
                                }
                            }
                            //System.out.println(" Path for Service: "+service);
                            //System.out.println(" +_+ Broker <-> Consumer "+buffer);
                            PATH_API_URL = ORIGIN;
                        }
                    }
                    for (int j=0;j<MAX;j++){
                        if (zookeeper[j]!=null){
                            PATH_API_URL +=broker[i]+"/" +zookeeper[j];
                            //System.out.println("PATH_API_URL: "+PATH_API_URL);
                            String buffer = URL_REQUEST(resource.Controller_ID,resource.Controller_Pw,PATH_API_URL,onos);
                            org.json.simple.parser.JSONParser jsonParser = new org.json.simple.parser.JSONParser();
                            JSONObject jsonObject = (JSONObject) jsonParser.parse(buffer);
                            JSONArray InfoArray = (JSONArray) jsonObject.get("paths");

                            for (int k=0;k<InfoArray.size();k++){
                                JSONObject object = (JSONObject) InfoArray.get(k);
                                list[k].cost = object.get("cost").toString();
                                JSONArray InfoArray2 = (JSONArray) object.get("links");
                                for (int t=0; t<InfoArray2.size();t++){
                                    JSONObject Object2 = (JSONObject) InfoArray2.get(t);
                                    String path = Object2.get("src").toString();
                                    path += "%"+Object2.get("dst");
                                    //list[j].b_z_path[t] = Object2.get("src").toString();
                                    //list[j].b_z_path[t] += "%"+Object2.get("dst");
                                    list[j].description = "Broker <-> Zookeeper";
                                    list[i].service = service;
                                    list[i].b_z_list.add(path);
                                }
                            }
                            //System.out.println("Path for Service: "+service);
                            //System.out.println("Broker <-> Zookeeper "+buffer);
                            PATH_API_URL = ORIGIN;
                        }
                    }
                }
                if (consumer[i]!=null){
                    for (int j=0;j<MAX;j++){
                        if (zookeeper[j]!=null){
                            PATH_API_URL +=consumer[i]+"/" +zookeeper[j];
                            //System.out.println("PATH_API_URL: "+PATH_API_URL);
                            String buffer = URL_REQUEST(resource.Controller_ID,resource.Controller_Pw,PATH_API_URL,onos);
                            org.json.simple.parser.JSONParser jsonParser = new org.json.simple.parser.JSONParser();
                            JSONObject jsonObject = (JSONObject) jsonParser.parse(buffer);
                            JSONArray InfoArray = (JSONArray) jsonObject.get("paths");

                            for (int k=0;k<InfoArray.size();k++){
                                JSONObject object = (JSONObject) InfoArray.get(k);
                                list[k].cost = object.get("cost").toString();
                                JSONArray InfoArray2 = (JSONArray) object.get("links");
                                for (int t=0; t<InfoArray2.size();t++){
                                    JSONObject Object2 = (JSONObject) InfoArray2.get(t);
                                    String path = Object2.get("src").toString();
                                    //list[j].z_c_path[t] = Object2.get("src").toString();
                                    //list[j].z_c_path[t] += "%"+Object2.get("dst");

                                    path += "%"+Object2.get("dst");
                                    list[j].description = "Zookeeper <-> Consumer";
                                    list[i].service = service;
                                    list[i].z_c_list.add(path);
                                    //System.out.println(" z_c_path: "+list[j].z_c_path[t]);
                                    //System.out.println(" z_c_list: "+list[j].z_c_list);
                                    //System.out.println("K: "+k+" t: "+t+" J: "+j+" I: "+i);
                                    //System.out.println();
                                }
                            }
                            //System.out.println("Path for Service: "+service);
                            //System.out.println("Zookeeper <-> Consumer "+buffer);
                            PATH_API_URL = ORIGIN;
                        }
                    }
                }
            }
        }
        return list;
    }


    private String URL_REQUEST(String USERNAME, String PASSWORD, String DEVICE_API_URL, URL onos) throws IOException {

        try {
            onos = new URL(DEVICE_API_URL);
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME,PASSWORD.toCharArray());
                }
            });
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        String buffer="";
        HttpURLConnection urlConnection = (HttpURLConnection) onos.openConnection();
        //System.out.println(urlConnection.getResponseCode());
        int responseCode = urlConnection.getResponseCode();
        if(responseCode==200){
            InputStream is = urlConnection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = in.readLine())!=null){
                buffer = line;
            }
            //System.out.println(buffer);
        }
        return buffer;
    }
    private void PTP_INTENT(String DPID, String ingress, String egress) throws IOException {

        String address = "http://"+resource.Controller_IP_Port+"/onos/v1/intents";
        URL onos=null;

        JSONObject ingressport = new JSONObject();
        ingressport.put("port",Integer.parseInt(ingress));
        ingressport.put("device",DPID);

        JSONObject egressport = new JSONObject();
        egressport.put("port",Integer.parseInt(egress));
        egressport.put("device",DPID);

        JSONObject json = new JSONObject();
        json.put("type","PointToPointIntent");
        json.put("appId","org.onosproject.cli");
        json.put("priority",55);
        json.put("ingressPoint",ingressport);
        json.put("egressPoint",egressport);

        String body = json.toString();
        System.out.println(json);

        try {
            onos = new URL(address);
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(resource.Controller_ID,resource.Controller_Pw.toCharArray());
                }
            });
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        String buffer="";
        HttpURLConnection urlConnection = (HttpURLConnection) onos.openConnection();

        urlConnection.setDoOutput(true);
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type","application/json");
        OutputStream os = urlConnection.getOutputStream();
        os.write(body.getBytes());
        os.flush();
        System.out.println("Location: "+urlConnection.getHeaderField("Location"));

        BufferedReader br = new BufferedReader(new InputStreamReader(
                (urlConnection.getInputStream())));

        String output;
        System.out.println("Output from ONOS ....");

        while ((output = br.readLine())!=null){
            System.out.println(output);
        }
        urlConnection.disconnect();
    }
}
