package util;

/**
 * Created by netcsnuc on 7/9/17.
 */
public class Printer {
    public void PRINT(ResourcePool_Manager.switch_Info_list[] slist, ResourcePool_Manager.host_Info_list[] hlist, ResourcePool_Manager.intents_Info_list[] ilist, ResourcePool_Manager.link_Info_list[] llist, ResourcePool_Manager.flow_Info_list[] flist){
        SWITCH(slist);

        HOST(hlist);

        LINK(llist);

        INTENT(ilist);

        FLOW(flist);
    }

    private void FLOW(ResourcePool_Manager.flow_Info_list[] flist) {
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("-------------------------------------------FLOW--------------------------------------------------------");
        for (int i=0;i+2<flist.length;i++){
            if (flist[i].id==null)
                break;

            System.out.println("-------------------------------------------------------------------------------------------------------");
            System.out.printf("|ID: %19s          |ID: %19s          |ID: %19s          |\n",flist[i].id,flist[i+1].id,flist[i+2].id);
            System.out.printf("|AppID: %26s|AppID: %26s|AppID: %26s|\n",flist[i].appId,flist[i+1].appId,flist[i+2].appId);
            System.out.printf("|Priority: %5s                  |Priority: %5s                  |Priority: %5s                  |\n",flist[i].priority,flist[i+1].priority,flist[i+2].priority);
            System.out.printf("|Timeout: %4s                    |Timeout: %4s                    |Timeout: %4s                    |\n",flist[i].timeout,flist[i+1].timeout,flist[i+2].timeout);
            System.out.printf("|isPermanent: %4s                |isPermanent: %4s                |isPermanent: %4s                |\n",flist[i].isPermanent,flist[i+1].isPermanent,flist[i+2].isPermanent);
            System.out.printf("|DeviceID: %22s |DeviceID: %22s |DeviceID: %22s |\n",flist[i].deviceId,flist[i+1].deviceId,flist[i+2].deviceId);
            System.out.printf("|State: %10s                |State: %10s                |State: %10s                |\n",flist[i].state,flist[i+1].state,flist[i+2].state);
            System.out.printf("|Life: %5s                      |Life: %5s                      |Life: %5s                      |\n",flist[i].life,flist[i+1].life,flist[i+2].life);
            System.out.printf("|Packets: %10s              |Packets: %10s              |Packets: %10s              |\n",flist[i].packets,flist[i+1].packets,flist[i+2].packets);
            System.out.printf("|Bytes: %12s              |Bytes: %12s              |Bytes: %12s              |\n",flist[i].bytes,flist[i+1].bytes,flist[i+2].bytes);
            System.out.printf("|Type: %8s                   |Type: %8s                   |Type: %8s                   |\n",flist[i].type,flist[i+1].type,flist[i+2].type);
            System.out.printf("|Port: %10s                 |Port: %10s                 |Port: %10s                 |\n",flist[i].port,flist[i+1].port,flist[i+2].port);
            System.out.printf("|Criteria_type: %9s         |Criteria_type: %9s         |Criteria_type: %9s         |\n",flist[i].ctype,flist[i+1].ctype,flist[i+2].ctype);
            System.out.printf("|Eth_Type: %9s              |Eth_Type: %9s              |Eth_Type: %9s              |\n",flist[i].ethType,flist[i+1].ethType,flist[i+2].ethType);
        }
    }

    private void INTENT(ResourcePool_Manager.intents_Info_list[] ilist) {
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("------------------------------------------INTENT---------------------------------------------------------");
        for (int i=0;i<ilist.length;i++){
            if (ilist[i].id==null)
                break;

            System.out.println("-------------------------------------------------------------------------------------------------------");
            System.out.println("Type: "+ilist[i].type);
            System.out.println("ID: "+ilist[i].id);
            System.out.println("AppId: "+ilist[i].appId);
            System.out.println("State: "+ilist[i].state);
        }
    }

    private void LINK(ResourcePool_Manager.link_Info_list[] llist) {
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("-------------------------------------------LINK--------------------------------------------------------");
        for (int i=0;i+2<llist.length;i++){
            if (llist[i].type==null)
                break;

            System.out.println("-------------------------------------------------------------------------------------------------------");
            System.out.printf("|SRC: %22s      |SRC: %22s      |SRC: %22s      |\n",llist[i].src,llist[i+1].src,llist[i+2].src);
            System.out.printf("|DST: %22s      |DST: %22s      |DST: %22s      |\n",llist[i].dst,llist[i+1].dst,llist[i+2].dst);
            System.out.printf("|Type: %8s                   |Type: %8s                   |Type: %8s                   |\n",llist[i].type,llist[i+1].type,llist[i+2].type);
            System.out.printf("|State: %8s                  |State: %8s                  |State: %8s                  |\n",llist[i].state,llist[i+1].state,llist[i+2].state);
        }
    }

    private void HOST(ResourcePool_Manager.host_Info_list[] hlist) {
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("-------------------------------------------HOST--------------------------------------------------------");
        for (int i=0;i+2<hlist.length;i++){
            if (hlist[i].ID==null)
                break;
            System.out.println("-------------------------------------------------------------------------------------------------------");
            System.out.printf("|Host ID                          "+"|Host ID                          "+"|Host ID                          |\n");
            System.out.printf("|%22s           |%22s           |%22s           |\n",hlist[i].ID,hlist[i+1].ID,hlist[i+2].ID);
            System.out.printf("|MAC: %17s           |MAC: %17s           |MAC: %17s           |\n",hlist[i].MAC,hlist[i+1].MAC,hlist[i+2].MAC);
            System.out.printf("|VLAN: %4s                       |VLAN: %4s                       |VLAN: %4s                       |\n",hlist[i].vlan,hlist[i+1].vlan,hlist[i+2].vlan);
            System.out.printf("|IP: %15s              |IP: %15s              |IP: %15s              |\n",hlist[i].IP,hlist[i+1].IP,hlist[i+2].IP);
            System.out.println("|Location: "+ hlist[i].location+"  |"+"Location: "+ hlist[i].location+"  |"+"Location: "+ hlist[i].location+"  |");
        }
    }

    private void SWITCH(ResourcePool_Manager.switch_Info_list[] slist) {
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("------------------------------------------SWITCH-------------------------------------------------------");
        for (int i=0;i+2<slist.length;i++){
            if (slist[i].DPID==null)
                break;

            System.out.println("-------------------------------------------------------------------------------------------------------");
            System.out.printf("|DPID: %19s        |DPID: %19s        |DPID: %19s        |\n",slist[i].DPID,slist[i+1].DPID,slist[i+2].DPID);
            System.out.printf("|Type: %6s                     |Type: %6s                     |Type: %6s                     |\n",slist[i].type,slist[i+1].type,slist[i+2].type);
            System.out.printf("|Manufactory: %19s |Manufactory: %19s |Manufactory: %19s |\n",slist[i].manufactory,slist[i+1].manufactory,slist[i+2].manufactory);
            System.out.printf("|Hardware: %19s    |Hardware: %19s    |Hardware: %19s    |\n",slist[i].hardware,slist[i+1].hardware,slist[i+2].hardware);
            System.out.printf("|Version: %19s     |Version: %19s     |Version: %19s     |\n",slist[i].version,slist[i+1].version,slist[i+2].version);
            System.out.printf("|Chassis: %19s     |Chassis: %19s     |Chassis: %19s     |\n",slist[i].chassisID,slist[i+1].chassisID,slist[i+2].chassisID);

            /* switch port print
            for (int j =0;j<slist[i].port_number.length;j++)
            {
                if (slist[i].port_number[j]==null)
                    break;
                System.out.println("Port Number: "+slist[i].port_number[j]);
                System.out.println("Port Name: "+slist[i].port_name[j]);
            }
            */
        }
    }

    public void PRINT_PATH(ResourcePool_Manager.path_INFO_list[] path_list) {
        for (int i=0;i<path_list.length;i++){
            if (path_list[i]==null)
                break;

            if (path_list[i].service!=null){
                System.out.println("++++++++++++Available Paths for Service "+path_list[i].service+" +++++++++++");
            }

            if (path_list[i].p_b_list.size()>0){
                System.out.println("Producer <-> Broker");
                System.out.println(path_list[i].p_b_list);
                //break;
            }

            if (path_list[i].p_z_list.size()>0){
                System.out.println("Producer <-> Zookeeper");
                System.out.println(path_list[i].p_z_list);
            }

            if (path_list[i].z_c_list.size()>0){
                System.out.println("Zookeeper <-> Consumer");
                System.out.println(path_list[i].z_c_list);
            }
            if (path_list[i].b_c_list.size()>0){
                System.out.println("Broker <-> Consumer");
                System.out.println(path_list[i].b_c_list);
                //System.out.println("i: "+i+" j: "+j);
            }
            if (path_list[i].b_z_list.size()>0){
                System.out.println("Broker <-> Zookeeper");
                System.out.println(path_list[i].b_z_list);
            }
        }
    }
}
