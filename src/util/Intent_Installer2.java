package util;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.InputStream;

/**
 * Created by netcsnuc on 8/5/17.
 */
public class Intent_Installer2 {
    private ResourcePool_Manager resource = ResourcePool_Manager.getInstance();
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
        ResourcePool_Manager.B_C_Path[] list = resource.getB_C_Path();
        ResourcePool_Manager.P_B_Path[] list2 = resource.getP_B_Path();

        int t=0,z=0;
        // make new Resourceful Object: B_C_Path
        for (int i=0;i<path_list.length;i++){
            if (path_list[i].b_c_list.size()>0){
                //System.out.println("^^: B_C list: "+path_list[i].b_c_list);
                int u=0;
                while (u<path_list[i].b_c_list.size()) {
                    if (path_list[i].b_c_list.get(u).subSequence(13,17).equals("host")){
                       // System.out.println("T: "+t+" Host SRC ADDR: "+path_list[i].b_c_list.get(u).subSequence(20,37));
                        list[t].src = path_list[i].b_c_list.get(u).subSequence(20,37).toString();
                        list[t].arr[z] = path_list[i].b_c_list.get(u);

                    }
                    else if (path_list[i].b_c_list.get(u).subSequence(57,61).equals("host")){
                        //System.out.println("T: "+t+" Host DST ADDR: "+path_list[i].b_c_list.get(u).subSequence(64,81));
                        list[t].dst =path_list[i].b_c_list.get(u).subSequence(64,81).toString();
                        list[t].arr[z] += path_list[i].b_c_list.get(u);
                        list[t].srcTOdst = list[t].src+" to "+list[t].dst;
                        t++;
                        z=0;
                    }
                    else {
                        list[t].arr[z] += path_list[i].b_c_list.get(u);
                    }
                    u++;
                }
            }
        }

        // make new Resourceful Object: P_B_Path
        t=0;
        for (int i=0;i<path_list.length;i++){
            if (path_list[i].p_b_list.size()>0){
                //System.out.println("^^: B_C list: "+path_list[i].b_c_list);
                int u=0;
                while (u<path_list[i].p_b_list.size()) {
                    if (path_list[i].p_b_list.get(u).subSequence(13,17).equals("host")){
                        //System.out.println("T: "+t+" Host SRC ADDR: "+path_list[i].p_b_list.get(u).subSequence(20,37));
                        list2[t].src = path_list[i].p_b_list.get(u).subSequence(20,37).toString();
                        list2[t].arr[z] = path_list[i].p_b_list.get(u);

                    }
                    else if (path_list[i].p_b_list.get(u).subSequence(57,61).equals("host")){
                        //System.out.println("T: "+t+" Host DST ADDR: "+path_list[i].p_b_list.get(u).subSequence(64,81));
                        list2[t].dst =path_list[i].p_b_list.get(u).subSequence(64,81).toString();
                        list2[t].arr[z] += path_list[i].p_b_list.get(u);
                        list2[t].srcTOdst = list2[t].src+" to "+list2[t].dst;
                        t++;
                        z=0;
                    }
                    else {
                        list2[t].arr[z] += path_list[i].p_b_list.get(u);
                    }
                    u++;
                }
            }
        }

        //remove temp path(It needs to be changed for Steering)
        int addition=0;
        for (int i=0;i<list.length;i++){
            if (list[i].src!=null){
                //System.out.println("HOST MAPPING: "+list[i].srcTOdst);
                addition=0;
                for(int j=0;j<i;j++){
                    if (list[i].srcTOdst.equals(list[j].srcTOdst)){
                        addition +=1;
                        //System.out.println(list[j].srcTOdst+ " have "+ (addition+1)+" paths J: "+j);
                        list[j].src=null;
                        list[j].dst=null;
                        list[j].srcTOdst=null;
                    }
                }
            }
        }

        //remove temp path(It needs to be changed for Steering)
        int addition2=0;
        for (int i=0;i<list2.length;i++){
            if (list2[i].src!=null){
                //System.out.println("HOST MAPPING: "+list2[i].srcTOdst);
                addition2=0;
                for(int j=0;j<i;j++){
                    if (list2[i].srcTOdst.equals(list2[j].srcTOdst)){
                        addition2 +=1;
                        //System.out.println(list2[j].srcTOdst+ " have "+ (addition2+1)+" paths J: "+j);
                        list2[j].src=null;
                        list2[j].dst=null;
                        list2[j].srcTOdst=null;
                    }
                }
            }
        }

        //Print
        System.out.println("==========================================");
        for (int k=0;k<list.length;k++){
            if (list[k].src!=null){
                System.out.println("Broker to Consumer HOST MAPPING: "+list[k].srcTOdst+" K= "+k);
                for (int r=0;r<list[k].arr.length;r++){
                    if (list[k].arr[r]!=null)
                        System.out.println("PATH: "+list[k].arr[r]+" R: "+r);
                }
            }
        }

        //Print
        System.out.println("==========================================");
        for (int k=0;k<list2.length;k++){
            if (list2[k].src!=null){
                System.out.println("Broker to Consumer HOST MAPPING: "+list2[k].srcTOdst+" K= "+k);
                for (int r=0;r<list2[k].arr.length;r++){
                    if (list2[k].arr[r]!=null)
                        System.out.println("PATH: "+list2[k].arr[r]+" R: "+r);
                }
            }
        }
        install_Intent(list,list2);

    }

    //install Intent using Tagging
    private void install_Intent(ResourcePool_Manager.B_C_Path[] list, ResourcePool_Manager.P_B_Path[] list2) throws Exception {

        //install Intent B_C Path
        for (int i=0;i<list.length;i++){
            if (list[i].src!=null){
                for (int j=0;j<list[i].arr.length;j++){
                    if (list[i].arr[j]!=null){
                        String[] values = list[i].arr[j].split("%");
                        for (int k=1;k<values.length;k++){
                            String command = "add-point-intent -s "+list[i].src+" -d "+list[i].dst;
                            String command2 = "add-point-intent -s "+list[i].dst+" -d "+list[i].src;
                            //System.out.println("(*: "+values[k].subSequence(13,19));
                            if (values[k].subSequence(13,19).equals("device")){
                                command += " "+values[k].subSequence(22,41)+"/"+values[k].charAt(9);
                                command2 += " "+values[k].subSequence(65,84)+"/"+values[k].charAt(52);
                                //System.out.println("(*: "+values[k].subSequence(62,85)+"/"+values[k].charAt(53));
                                command += " "+values[k].subSequence(65,84)+"/"+values[k].charAt(52);
                                command2 += " "+values[k].subSequence(22,41)+"/"+values[k].charAt(9);
                                System.out.println("ONOS Command: "+command);
                                System.out.println("ONOS Command: "+command2);
                                Connect_to_ONOS_Controller(command);
                                Connect_to_ONOS_Controller(command2);
                            }
                            else if(values[k].subSequence(13,17).equals("host")){
                                break;
                            }
                        }
                    }
                }
            }
        }
        //install Intent P_B Path
        for (int i=0;i<list2.length;i++){
            if (list2[i].src!=null){
                for (int j=0;j<list2[i].arr.length;j++){
                    if (list2[i].arr[j]!=null){
                        String[] values = list2[i].arr[j].split("%");
                        for (int k=1;k<values.length;k++){
                            String command = "add-point-intent -s "+list2[i].src+" -d "+list2[i].dst;
                            String command2 = "add-point-intent -s "+list2[i].dst+" -d "+list2[i].src;
                            //System.out.println("(*: "+values[k].subSequence(13,19));
                            if (values[k].subSequence(13,19).equals("device")){
                                command += " "+values[k].subSequence(22,41)+"/"+values[k].charAt(9);
                                command2 += " "+values[k].subSequence(65,84)+"/"+values[k].charAt(52);
                                //System.out.println("(*: "+values[k].subSequence(62,85)+"/"+values[k].charAt(53));
                                command += " "+values[k].subSequence(65,84)+"/"+values[k].charAt(52);
                                command2 += " "+values[k].subSequence(22,41)+"/"+values[k].charAt(9);
                                System.out.println("ONOS Command: "+command);
                                System.out.println("ONOS Command: "+command2);
                                Connect_to_ONOS_Controller(command);
                                Connect_to_ONOS_Controller(command2);
                            }
                            else if(values[k].subSequence(13,17).equals("host")){
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    public void remove_All_Intent() throws Exception {
        System.out.println("Remove all Intent! ");
        String command = "remove-intent --purge org.onosproject.cli";
        Connect_to_ONOS_Controller(command);
    }
}
