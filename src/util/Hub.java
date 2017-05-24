package util;

/**
 * Created by netcsnuc on 5/9/17.
 */
public class Hub {
    public static void main(String[] args) throws Exception {

        ResourcePool resource = ResourcePool.getInstance();

        ResourcePool.host_Info_list[] hlist = resource.host_init();
        ResourcePool.flow_Info_list[] flist = resource.flow_init();
        ResourcePool.link_Info_list[] llist = resource.link_init();
        ResourcePool.intents_Info_list[] ilist = resource.intent_init();
        ResourcePool.switch_Info_list[] slist = resource.switch_init();
        ResourcePool.serviceHosts_INFO_list[] shlists = resource.service_Hosts_init();
        ResourcePool.path_INFO_list[] path_list = resource.path_init();
        ResourcePool.template_INFO_list[] tlist = resource.template_init();


        //get Template information
        //JSON_INPUT();

        //get ONOS info and mapping
        slist = ONOS_REST.GET_ONOS_INFO_Switch(slist);
        hlist = ONOS_REST.GET_ONOS_INFO_Host(hlist);
        llist = ONOS_REST.GET_ONOS_INFO_Link(llist);
        ilist = ONOS_REST.GET_ONOS_Info_Intent(ilist);
        flist = ONOS_REST.GET_ONOS_INFO_Flow(flist);

        PRINT(slist,hlist,ilist,llist,flist);

        //DB access & Tables creation & put ONOS data
        DB_Access.Access();
        DB_Access.DB_Data_Push();

        //Read template and parsing
        Parsing_Template.Read_Remote_Template();

        //Compare DB & template
        Compare_DB_Template.Comapre();
        for (int i=0; i< tlist.length;i++){
            if (tlist[i].service!=null){
                path_list = ONOS_REST.GET_ONOS_INFO_Path(path_list,tlist[i].service);
            }
        }
        for (int i=0;i<100;i++){
            if (path_list[i].service!=null)
                System.out.println("^^ "+path_list[i].service);
        }
        PRINT_PATH(path_list);
    }

    private static void PRINT_PATH(ResourcePool.path_INFO_list[] path_list) {
        for (int i=0;i<path_list.length;i++){
            if (path_list[i]==null)
                break;

            if (path_list[i].service!=null)
                System.out.println("++++++++++++Available Paths for Service "+path_list[i].service+" +++++++++++");

            System.out.println("Producer <-> Broker");
            for (int j=0;j<path_list[i].p_b_path.length;j++){
                if (path_list[i].p_b_path[j]==null)
                    break;
                System.out.println(path_list[i].p_b_path[j]);
            }
            System.out.println("Producer <-> Broker");
            for (int j=0;j<path_list[i].p_z_path.length;j++){
                if (path_list[i].p_z_path[j]==null)
                    break;
                System.out.println(path_list[i].p_z_path[j]);
            }
            System.out.println("Zookeeper <-> Consumer");
            for (int j=0;j<path_list[i].z_c_path.length;j++){
                if (path_list[i].z_c_path[j]==null)
                    break;

                System.out.println(path_list[i].z_c_path[j]);
            }
            System.out.println("Broker <-> Consumer");
            for (int j=0;j<path_list[i].b_c_path.length;j++){
                if (path_list[i].b_c_path[j]==null)
                    break;

                System.out.println(path_list[i].b_c_path[j]);
            }
            System.out.println("Broker <-> Zookeeper");
            for (int j=0;j<path_list[i].b_z_path.length;j++){
                if (path_list[i].b_z_path[j]==null)



                    break;

                System.out.println(path_list[i].b_z_path[j]);
            }

        }
    }


    private static void PRINT(ResourcePool.switch_Info_list[] slist, ResourcePool.host_Info_list[] hlist, ResourcePool.intents_Info_list[] ilist, ResourcePool.link_Info_list[] llist, ResourcePool.flow_Info_list[] flist) {
        for (int i=0;i<slist.length;i++){
            if (slist[i].DPID==null)
                break;

            System.out.println("++++++++++++Device DPID+++++++++++");
            System.out.println("++++++++++++"+slist[i].DPID+"++++++++++++");
            System.out.println("type: "+slist[i].type);
            System.out.println("manufactory: "+slist[i].manufactory);
            System.out.println("hardware: "+slist[i].hardware);
            System.out.println("version: "+slist[i].version);
            System.out.println("chassis: "+slist[i].chassisID);
            for (int j =0;j<slist[i].port_number.length;j++)
            {
                if (slist[i].port_number[j]==null)
                    break;
                System.out.println("Port Number: "+slist[i].port_number[j]);
                System.out.println("Port Name: "+slist[i].port_name[j]);
            }
        }
        for (int i=0;i<hlist.length;i++){
            if (hlist[i].ID==null)
                break;

            System.out.println("++++++++++++Host ID +++++++++++");
            System.out.println("++++++++++++"+hlist[i].ID+"++++++++++++");
            System.out.println("MAC: "+hlist[i].MAC);
            System.out.println("VLAN: "+hlist[i].vlan);
            System.out.println("IP: "+hlist[i].IP);
            System.out.println("Location: "+ hlist[i].location);
        }
        for (int i=0;i<llist.length;i++){
            if (llist[i].type==null)
                break;

            System.out.println("++++++++++++Link +++++++++++");
            System.out.println("Src: "+llist[i].src);
            System.out.println("Dst: "+llist[i].dst);
            System.out.println("Type: "+llist[i].type);
            System.out.println("State: "+llist[i].state);
        }

        for (int i=0;i<ilist.length;i++){
            if (ilist[i].id==null)
                break;

            System.out.println("++++++++++++Intent +++++++++++");
            System.out.println("Type: "+ilist[i].type);
            System.out.println("ID: "+ilist[i].id);
            System.out.println("AppId: "+ilist[i].appId);
            System.out.println("State: "+ilist[i].state);
        }

        for (int i=0;i<flist.length;i++){
            if (flist[i].id==null)
                break;

            System.out.println("++++++++++++Flow +++++++++++");
            System.out.println("ID: "+flist[i].id);
            System.out.println("appId: "+flist[i].appId);
            System.out.println("Priority: "+flist[i].priority);
            System.out.println("Timeout: "+flist[i].timeout);
            System.out.println("isPermanent: "+flist[i].isPermanent);
            System.out.println("DeviceId: "+flist[i].deviceId);
            System.out.println("State: "+flist[i].state);
            System.out.println("Life: "+flist[i].life);
            System.out.println("Packets: "+flist[i].packets);
            System.out.println("Bytes: "+flist[i].bytes);
            System.out.println("Type: "+flist[i].type);
            System.out.println("Port: "+flist[i].port);
            System.out.println("Criteria_type: "+flist[i].ctype);
            System.out.println("EthType: "+ flist[i].ethType);

        }
    }


}

