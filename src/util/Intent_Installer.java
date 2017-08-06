package util;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by netcsnuc on 6/4/17.
 */
public class Intent_Installer {

    private  ResourcePool_Manager resource = ResourcePool_Manager.getInstance();
    private  HashMap<String, String> ingress_map = new HashMap<String, String>();
    private  HashMap<String, String> egress_map = new HashMap<String, String>();

    public void Connect_to_ONOS_Controller(String command) throws Exception{

        JSch jsch = new JSch();
        Session session = jsch.getSession(resource.Controller_ID, resource.Controoler_IP, 8101);
        session.setPassword(resource.Controller_Pw);
        session.setConfig("StrictHostKeyChecking","no");
        session.connect();

        //String command = "hosts";

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
                //System.out.println("exit-status: "+channel.getExitStatus());
                break;
            }
            try {
                Thread.sleep(100);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        channel.disconnect();
        session.disconnect();
    }

    public void choice_path(ResourcePool_Manager.path_INFO_list[] path_list) throws Exception {

        boolean[] flag = {false,false,false,false,false};

        for (int i=0;i<path_list.length;i++){
            if (path_list[i].z_c_list.size()>0){
                //System.out.println("^^ z_c I: "+i);
                //System.out.println("^^ z_c: "+path_list[i].z_c_list.get(0));

                int j=1;
                path_list[i].z_c_path[0] = path_list[i].z_c_list.get(0);
                while (j<path_list[i].z_c_list.size()){
                    if (!path_list[i].z_c_list.get(0).equals(path_list[i].z_c_list.get(j))){
                        //System.out.println(path_list[i].z_c_list.get(0));
                        //System.out.println("********  "+path_list[i].z_c_list.get(j));
                        path_list[i].z_c_path[j] = path_list[i].z_c_list.get(j);
                    } else {
                        break;
                    }
                    j++;
                }
            }
            if (path_list[i].b_z_list.size()>0){
                //System.out.println("^^ b_z I: "+i);
                //System.out.println("^^ b_z: "+path_list[i].b_z_list);

                int j=1;
                path_list[i].b_z_path[0] = path_list[i].b_z_list.get(0);
                while (j<path_list[i].b_z_list.size()){
                    if (!path_list[i].b_z_list.get(0).equals(path_list[i].b_z_list.get(j))){
                        //System.out.println(path_list[i].b_z_list.get(0));
                        //System.out.println("********  "+path_list[i].b_z_list.get(j));
                        path_list[i].b_z_path[j] = path_list[i].b_z_list.get(j);
                    } else {
                        break;
                    }
                    j++;
                }
            }
            if (path_list[i].p_z_list.size()>0){
                //System.out.println("^^ p_z I: "+i);
                //System.out.println("^^ p_z: "+path_list[i].p_z_list);

                int j=1;
                path_list[i].p_z_path[0] = path_list[i].p_z_list.get(0);
                while (j<path_list[i].p_z_list.size()){
                    if (!path_list[i].p_z_list.get(0).equals(path_list[i].p_z_list.get(j))){
                        //System.out.println(path_list[i].p_z_list.get(0));
                        //System.out.println("********  "+path_list[i].p_z_list.get(j));
                        path_list[i].p_z_path[j] = path_list[i].p_z_list.get(j);
                    } else {
                        break;
                    }
                    j++;
                }
            }
            if (path_list[i].b_c_list.size()>0){
                //System.out.println("^^ b_c I: "+i);
                //System.out.println("^^ b_c: "+path_list[i].b_c_list);

                int j=1;
                path_list[i].b_c_path[0] = path_list[i].b_c_list.get(0);
                while (j<path_list[i].b_c_list.size()){
                    if (!path_list[i].b_c_list.get(0).equals(path_list[i].b_c_list.get(j))){
                        //System.out.println(path_list[i].b_c_list.get(0));
                        //System.out.println("********  "+path_list[i].b_c_list.get(j));
                        path_list[i].b_c_path[j] = path_list[i].b_c_list.get(j);
                    } else {
                        if (path_list[i].b_c_list.get(j+1)!=null) {
                            flag[3] = true;
                        }
                        break;
                    }
                    j++;
                }
                if (flag[3]){
                    for (int k=j;k<path_list[i].b_c_list.size();k++){
                        path_list[i].temp_b_c_path[k] = path_list[i].b_c_list.get(k);
                        //System.out.println("^&^: "+arr[k]);
                        //System.out.println("^&^ temp B-C: "+path_list[i].temp_b_c_path[k]);
                    }
                }
            }
            if (path_list[i].p_b_list.size()>0){
                //System.out.println("^^ p_b I: "+i);
                //System.out.println("^^ p_b: "+path_list[i].p_b_list);

                int j=1;
                path_list[i].p_b_path[0] = path_list[i].p_b_list.get(0);
                while (j<path_list[i].p_b_list.size()){
                    if (!path_list[i].p_b_list.get(0).equals(path_list[i].p_b_list.get(j))){
                        //System.out.println(path_list[i].p_b_list.get(0));
                        //System.out.println("********  "+path_list[i].p_b_list.get(j));
                        path_list[i].p_b_path[j] = path_list[i].p_b_list.get(j);
                    } else {
                        if (path_list[i].p_b_list.get(j+1)!=null){
                            flag[4] = true;
                            break;
                        }
                    }
                    j++;
                }
                if (flag[4]) {
                    for(int k=j;k<path_list[i].p_b_list.size();k++){
                        path_list[i].temp_p_b_path[k] = path_list[i].p_b_list.get(k);
                        //System.out.println("^&^ temp P-B: "+path_list[i].temp_p_b_path[k]);
                    }
                }
            }
        }
        classify_ports(path_list);
    }
    private void classify_ports(ResourcePool_Manager.path_INFO_list[] path_list) throws Exception {
        for (int i=0;i<path_list.length;i++) {
            for (int k=0;k<100;k++){
                if (path_list[i].z_c_path[k]!=null){
                    //System.out.println("*********** z_c one path: "+ path_list[i].z_c_path[k]);
                    makeHashMap(path_list[i].z_c_path[k]);
                }
                if (path_list[i].p_b_path[k]!=null) {
                    //System.out.println("*********** p_b one path: " + path_list[i].p_b_path[k]);
                    makeHashMap(path_list[i].p_b_path[k]);
                }
                if (path_list[i].b_c_path[k]!=null){
                    //System.out.println("*********** b_c one path: "+ path_list[i].b_c_path[k]);
                    makeHashMap(path_list[i].b_c_path[k]);
                }
                if (path_list[i].b_z_path[k]!=null){
                    //System.out.println("*********** b_z one path: "+ path_list[i].b_z_path[k]);
                    makeHashMap(path_list[i].b_z_path[k]);
                }
                if (path_list[i].p_z_path[k]!=null){
                    //System.out.println("*********** p_z one path: "+ path_list[i].p_z_path[k]);
                    makeHashMap(path_list[i].p_z_path[k]);
                }
            }
        }
        Set<Map.Entry<String,String>> set = ingress_map.entrySet();
        Iterator<Map.Entry<String, String>> itr = set.iterator();
        String[] ingressValues;
        String[] egressValues;
        while (itr.hasNext()){
            Map.Entry<String, String> e = (Map.Entry<String, String>) itr.next();
            //System.out.println("deviceId: "+e.getKey()+" ingress_port: "+e.getValue());

            Set<Map.Entry<String,String>> set2 = egress_map.entrySet();
            Iterator<Map.Entry<String, String>> itr2 = set2.iterator();
            while (itr2.hasNext()){
                Map.Entry<String, String> e2 = (Map.Entry<String, String>) itr2.next();
                if (e.getKey().equals(e2.getKey())){
                    System.out.println("deviceId: "+e2.getKey()+" ingress_port: "+e.getValue()+" egress_port: "+e2.getValue());
                        ingressValues= e.getValue().split(" ");
                        egressValues = e2.getValue().split(" ");
                    //if (e2.getKey().equals("of:0001d4ca6dca9639"))
                        decide_Intent(ingressValues,egressValues,e2.getKey());
                }
            }
        }
    }
    private void decide_Intent(String[] ingressValues, String[] egressValues, String key) throws Exception {
        List list = new ArrayList<>();
        List list2 = new ArrayList<>();
        HashMap<String,String> intent_map = new HashMap<String,String>();

        for (int i=0;i<ingressValues.length;i++){
            list.add(ingressValues[i]+" "+egressValues[i]);
        }

        for (int i=0;i<list.size();i++){
            String tmp = list.get(i).toString();
            for (int j=0;j<list.size();j++){
                if (i!=j && tmp.equals(list.get(j).toString())) {
                    list.remove(j);
                }
                if (tmp.equals(egressValues[j]+" "+ingressValues[j])) {
                    list.remove(j);
                }
            }
        }
        for (int i=0;i<list.size();i++){
            list2.add(list.get(i).toString());
            String[] value = list.get(i).toString().split(" ");
            list2.add(value[1]+" "+value[0]);
        }
        //System.out.println("List2: "+list2);
        for (int i=0;i<list2.size();i++){
            String[] port = list2.get(i).toString().split(" ");
            if (intent_map.containsKey(port[0])) {
                if (!intent_map.get(port[0]).equals(port[1])){
                    String value = intent_map.get(port[0]);
                    value += ","+port[1];
                    intent_map.put(port[0],value);
                }
            } else {
                intent_map.put(port[0],port[1]);
            }
        }
        //System.out.println("Map: "+intent_map);
        //System.out.println("Key: "+key);

        Set<Map.Entry<String,String>> set = intent_map.entrySet();
        Iterator<Map.Entry<String,String>> itr = set.iterator();
        while (itr.hasNext()){
            Map.Entry<String,String> e = (Map.Entry<String,String>) itr.next();
            if (e.getValue().contains(",")){
                String command = "add-single-to-multi-intent "+key+"/"+e.getKey()+" ";
                String[] value = e.getValue().split(",");
                for (int i=0;i<value.length;i++){
                    command += key+"/"+value[i]+" ";
                }
                System.out.println("Command: "+command);
                Connect_to_ONOS_Controller(command);
            }
            else {
                String command = "add-point-intent "+key+"/"+e.getKey()+" "+key+"/"+e.getValue();
                System.out.println("Command: "+command);
                Connect_to_ONOS_Controller(command);
            }
        }
    }
    private void makeHashMap(String s) {
        String[] values= s.split("%");

        String ingress="";
        String egress="";

        if (values[0].substring(13,17).equals("host")){
            ingress = values[1].substring(9,10);
            if (ingress_map.containsKey(values[1].substring(22,41))){
                //if (!ingress.equals(ingress_map.get(values[1].substring(22,41))))
                ingress += " "+ingress_map.get(values[1].substring(22,41));
            }
            ingress_map.put(values[1].substring(22,41),ingress);
            //System.out.println("device Id: "+values[1].substring(22,41)+" ingress port: "+ingress);
        }
        else if (values[0].substring(13,19).equals("device") && values[1].substring(13,19).equals("device")){
            egress = values[0].substring(9,10);
            if (egress_map.containsKey(values[0].substring(22,41))){
                //if (!egress.equals(egress_map.get(values[0].substring(22,41))))
                egress += " "+egress_map.get(values[0].substring(22,41));
            }
            egress_map.put(values[0].substring(22,41),egress);
            //System.out.println("device Id: "+values[0].substring(22,41)+" egress port: "+egress);

            ingress = values[1].substring(9,10);
            if (ingress_map.containsKey(values[1].substring(22,41))){
                // if (!ingress.equals(ingress_map.get(values[1].substring(22,41))))
                ingress += " "+ingress_map.get(values[1].substring(22,41));
            }
            ingress_map.put(values[1].substring(22,41),ingress);
            //System.out.println("device Id: "+values[1].substring(22,41)+" ingress port: "+ingress);
        }
        else if (values[1].substring(13,17).equals("host")) {
            egress = values[0].substring(9,10);
            if (egress_map.containsKey(values[0].substring(22,41))){
                // if (!egress.equals(egress_map.get(values[0].substring(22,41))))
                egress += " "+egress_map.get(values[0].substring(22,41));
            }
            egress_map.put(values[0].substring(22,41),egress);
            //System.out.println("device Id: "+values[0].substring(22,41)+" egress port: "+egress);
        }
    }
    public void remove_All_Intent() throws Exception {

        System.out.println("Remove all Intent! ");
        String command = "remove-intent --purge org.onosproject.cli";
        Connect_to_ONOS_Controller(command);
    }
}
