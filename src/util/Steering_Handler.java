package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by netcsnuc on 6/16/17.
 */
public class Steering_Handler {
    public void steering(ResourcePool_Manager resource, ResourcePool_Manager.path_INFO_list[] path_list) throws Exception {
        ResourcePool_Manager.host_Info_list[] hlist = resource.getHost_INFO_list();
        ResourcePool_Manager.flow_Info_list[] flist = resource.flow_init();
        ResourcePool_Manager.link_Info_list[] llist = resource.getLink_INFO_list();
        ResourcePool_Manager.intents_Info_list[] ilist = resource.getIntent_INFO_list();
        ResourcePool_Manager.switch_Info_list[] slist = resource.getSwitch_INFO_list();
        ResourcePool_Manager.serviceHosts_INFO_list[] shlists = null;

        int total_bytyes_b_c_per_min=0;
        int total_bytyes_p_b_per_min=0;
        int total_bytyes_b_c_per_min_temp=0;
        int total_bytyes_p_b_per_min_temp=0;

        int identifier_p_b=0;
        int identifier_b_c=0;
        List<String> temp_p_b_list = new ArrayList<String>();
        List<String> temp_b_c_list = new ArrayList<String>();
        List<String> temp_p_b_list2 = new ArrayList<String>();
        List<String> temp_b_c_list2 = new ArrayList<String>();
        List<String> temp_p_b_list3 = new ArrayList<String>();
        List<String> temp_b_c_list3 = new ArrayList<String>();

        ONOS_API_Handler onos_APIHandler = new ONOS_API_Handler();
        flist = onos_APIHandler.GET_ONOS_INFO_Flow(flist);

        DB_Manager DB = new DB_Manager();
        //DB.DB_Flow_Push(flist);

        Intent_Installer installer = new Intent_Installer();

        int p_b_path_byte_total=0;
        int b_c_path_byte_total=0;

        int temp_p_b_path_byte_total=0;
        int temp_b_c_path_byte_total=0;


        for (int i=0;i<path_list.length;i++) {
            for (int j=0; j<path_list[i].temp_p_b_path.length;j++){
                if (path_list[i].temp_p_b_path[j]!= null){
                    identifier_p_b=i;
                    temp_p_b_path_byte_total+= calculate_total_bytes(path_list[i].temp_p_b_path[j],flist);
                }
            }
            for (int j=0; j<path_list[i].temp_b_c_path.length;j++){
                if (path_list[i].temp_b_c_path[j]!=null){
                    identifier_b_c=i;
                    temp_b_c_path_byte_total+= calculate_total_bytes(path_list[i].temp_b_c_path[j],flist);
                }
            }
        }

        for (int i=0;i<path_list.length;i++) {
            for (int j=0;j<path_list[i].p_b_path.length;j++){
                if (path_list[i].p_b_path[j]!= null){
                    p_b_path_byte_total+= calculate_total_bytes(path_list[i].p_b_path[j],flist);
                }
            }
            for (int j=0; j<path_list[i].b_c_path.length;j++){
                if (path_list[i].b_c_path[j]!=null){
                    b_c_path_byte_total+= calculate_total_bytes(path_list[i].b_c_path[j],flist);
                }
            }
        }
        //System.out.println("Total Bytes of Temporal Producer <-> Broker paths: "+temp_p_b_path_byte_total);
        //System.out.println("Total Bytes of Temporal Broker <-> Consumer paths: "+temp_b_c_path_byte_total);


        //System.out.println("Total Bytes of Current Producer <-> Broker paths: "+p_b_path_byte_total);
        //System.out.println("Total Bytes of Current Broker <-> Consumer paths: "+b_c_path_byte_total);

        total_bytyes_b_c_per_min = b_c_path_byte_total - total_bytyes_b_c_per_min;
        total_bytyes_p_b_per_min = p_b_path_byte_total - total_bytyes_p_b_per_min;

        System.out.println(" Bytes of Current Producer <-> Broker paths per minute :"+total_bytyes_p_b_per_min);
        System.out.println(" Bytes of Current Broker <-> Consumer paths per minute :"+total_bytyes_b_c_per_min);

        total_bytyes_b_c_per_min_temp = temp_b_c_path_byte_total - total_bytyes_b_c_per_min_temp;
        total_bytyes_p_b_per_min_temp = temp_p_b_path_byte_total - total_bytyes_p_b_per_min_temp;

        System.out.println(" Bytes of Temporal Producer <-> Broker paths per minute :"+total_bytyes_p_b_per_min_temp);
        System.out.println(" Bytes of Temporal Broker <-> Consumer paths per minute :"+total_bytyes_b_c_per_min_temp);

        if (total_bytyes_b_c_per_min_temp<total_bytyes_b_c_per_min && total_bytyes_p_b_per_min_temp<total_bytyes_p_b_per_min){

            System.out.println("Steering Start !");
            installer.remove_All_Intent();
            Thread.sleep(3000);
            System.out.println("Remove Intents");

            System.out.println("Install new Intents");

            path_list[identifier_p_b].p_b_list.clear();
            path_list[identifier_b_c].b_c_list.clear();

            temp_p_b_list = Arrays.asList(path_list[identifier_p_b].temp_p_b_path);
            temp_p_b_list2 = Arrays.asList(path_list[identifier_p_b].p_b_path);
            temp_p_b_list3.addAll(temp_p_b_list);
            temp_p_b_list3.addAll(temp_p_b_list2);

            while (temp_p_b_list3.remove(null));

            temp_b_c_list = Arrays.asList(path_list[identifier_b_c].temp_b_c_path);
            temp_b_c_list2 = Arrays.asList(path_list[identifier_b_c].b_c_path);
            temp_b_c_list3.addAll(temp_b_c_list);
            temp_b_c_list3.addAll(temp_b_c_list2);

            while (temp_b_c_list3.remove(null));

            path_list[identifier_p_b].p_b_list = temp_p_b_list3;
            path_list[identifier_b_c].b_c_list = temp_b_c_list3;
            //System.out.println("tmp_list: "+temp_p_b_list3);
            //System.out.println("p_b_list: "+path_list[identifier_p_b].p_b_list);
            installer.choice_path(path_list);
        }

        else if(total_bytyes_b_c_per_min_temp < total_bytyes_b_c_per_min && total_bytyes_p_b_per_min_temp > total_bytyes_p_b_per_min) {
            System.out.println("Steering Start !");
            installer.remove_All_Intent();
            Thread.sleep(3000);
            System.out.println("Remove Intents");

            System.out.println("Install new Intents");

            path_list[identifier_b_c].b_c_list.clear();

            temp_b_c_list = Arrays.asList(path_list[identifier_b_c].temp_b_c_path);
            temp_b_c_list2 = Arrays.asList(path_list[identifier_b_c].b_c_path);
            temp_b_c_list3.addAll(temp_b_c_list);
            temp_b_c_list3.addAll(temp_b_c_list2);

            while (temp_b_c_list3.remove(null));

            path_list[identifier_b_c].b_c_list = temp_b_c_list3;
            //System.out.println("tmp_list: "+temp_p_b_list3);
            //System.out.println("p_b_list: "+path_list[identifier_p_b].p_b_list);
            installer.choice_path(path_list);
        }

        else if(total_bytyes_b_c_per_min_temp > total_bytyes_b_c_per_min && total_bytyes_p_b_per_min_temp > total_bytyes_p_b_per_min) {
            System.out.println("Steering Start !");
            installer.remove_All_Intent();
            Thread.sleep(3000);
            System.out.println("Remove Intents");

            System.out.println("Install new Intents");

            path_list[identifier_p_b].p_b_list.clear();

            temp_p_b_list = Arrays.asList(path_list[identifier_p_b].temp_p_b_path);
            temp_p_b_list2 = Arrays.asList(path_list[identifier_p_b].p_b_path);
            temp_p_b_list3.addAll(temp_p_b_list);
            temp_p_b_list3.addAll(temp_p_b_list2);

            while (temp_p_b_list3.remove(null));
            path_list[identifier_p_b].p_b_list = temp_p_b_list3;
            //System.out.println("tmp_list: "+temp_p_b_list3);
            //System.out.println("p_b_list: "+path_list[identifier_p_b].p_b_list);
            installer.choice_path(path_list);
        }
    }
    /*
    * calculate total_byte_temp method
    * : calculate each switch flow rule bytes (except controller flow)
     */

    private int calculate_total_bytes(String path, ResourcePool_Manager.flow_Info_list[] flist) {
        int total_bytes=0;
        if (path!=null){
            String[] str = path.split("%");
            //System.out.println("temp_p_b_path: "+str[0]+"%"+str[1]);
            if (str[0].substring(13,17).equals("host")){
                //System.out.println("Device ID: "+str[1].substring(22,41));
                for (int z=0;z<flist.length;z++){
                    if (str[1].substring(22,41).equals(flist[z].deviceId)) {
                        if (!flist[z].port.equals("CONTROLLER")){
                            total_bytes += Integer.parseInt(flist[z].bytes);
                            //System.out.println("Total Data Bytes: "+total_bytes);
                        }
                    }
                }
            }
            else if (str[1].substring(13,19).equals("device")){
                //System.out.println("Device ID: "+str[1].substring(22,41));
                for (int z=0;z<flist.length;z++){
                    if (str[1].substring(22,41).equals(flist[z].deviceId)) {
                        if (!flist[z].port.equals("CONTROLLER")){
                            total_bytes += Integer.parseInt(flist[z].bytes);
                            //System.out.println("Total Data Bytes: "+total_bytes);
                        }
                    }
                }
            }
        }
        //System.out.println("Total Bytes: "+total_bytes);
        return total_bytes;
    }
}
