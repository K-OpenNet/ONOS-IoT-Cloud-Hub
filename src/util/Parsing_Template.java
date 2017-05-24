package util;


import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.InputStream;

/**
 * Created by netcsnuc on 5/11/17.
 */
public class Parsing_Template {
    private static String USER_ID ="hub";
    private static String IP="210.125.84.113";
    private static String PASSWORD="netcs007bang";

    public static void Read_Remote_Template() throws Exception{
        JSch jsch = new JSch();

        Session session = jsch.getSession(USER_ID,IP,22);
        session.setPassword(PASSWORD);
        session.setConfig("StrictHostKeyChecking","no");
        session.connect();

        String command = "cat template";

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        channel.connect();
        InputStream in =channel.getInputStream();
        byte[] tmp = new byte[1024];
        String template="";
        while (true){
            while (in.available()>0){
                int i = in.read(tmp,0,1024);
                if (i<0)
                    break;
                //System.out.println(new String(tmp,0,i));
                template = new String(tmp,0,i);
            }
            if (channel.isClosed()) {
                System.out.println("exit-status: "+channel.getExitStatus());
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        //System.out.println("Template[JSON Format]: "+template);
        JSON_Parsing(template);

        channel.disconnect();
        session.disconnect();
    }
    public static void JSON_Parsing(String template) throws ParseException {
        org.json.simple.parser.JSONParser jsonParser = new org.json.simple.parser.JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(template);
        JSONArray InfoArray = (JSONArray) jsonObject.get("Service");
        /*
        org.json.simple.parser.JSONParser jsonParser = new org.json.simple.parser.JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(buffer);
        JSONArray InfoArray = (JSONArray) jsonObject.get("devices");
        */

        ResourcePool resource = ResourcePool.getInstance();
        ResourcePool.template_INFO_list[] tlist = resource.getTemplate_INFO_lists();

       for (int i=0;i<InfoArray.size();i++){
            JSONObject Object = (JSONObject) InfoArray.get(i);

            System.out.println("Version: "+Object.get("Version"));
            System.out.println("Name: "+Object.get("Name"));
            System.out.println("Zookeeper: "+Object.get("Zookeeper"));
            System.out.println("Broker: "+Object.get("Broker"));
            System.out.println("Consumer: "+Object.get("Consumer"));
            System.out.println("Producer: "+Object.get("Producer"));

           tlist[i].version = Object.get("Version").toString();
           tlist[i].service = Object.get("Name").toString();

           JSONObject Object2;
           JSONArray InfoArray2 = (JSONArray) Object.get("Zookeeper");
           for (int j=0;j<InfoArray2.size();j++){
               Object2 = (JSONObject) InfoArray2.get(j);
               tlist[i].zookeeper_IP[j] = Object2.get("IP").toString();
               tlist[i].zookeeper_Port[j] = Object2.get("Port").toString();
           }
           InfoArray2 = (JSONArray) Object.get("Broker");
           for (int j=0;j<InfoArray2.size();j++){
               Object2 = (JSONObject) InfoArray2.get(j);
               tlist[i].broker_IP[j] = Object2.get("IP").toString();
               tlist[i].broker_Port[j] = Object2.get("Port").toString();
           }
           InfoArray2 = (JSONArray) Object.get("Consumer");
           for (int j=0;j<InfoArray2.size();j++){
               Object2 = (JSONObject) InfoArray2.get(j);
               tlist[i].consumer_IP[j] = Object2.get("IP").toString();
               //tlist[i].consumer_Port[j] = Object2.get("Port").toString();
           }
           InfoArray2 = (JSONArray) Object.get("Producer");
           for (int j=0;j<InfoArray2.size();j++){
               Object2 = (JSONObject) InfoArray2.get(j);
               tlist[i].producer_IP[j] = Object2.get("IP").toString();
               //tlist[i].producer_Port[j] = Object2.get("Port").toString();
           }
        }
    }
}
