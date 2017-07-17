package util;

/**
 * Created by netcsnuc on 6/16/17.
 */
public class Provisioning_Handler {
    public void provisioning(ResourcePool_Manager resource) throws Exception {
        ResourcePool_Manager.host_Info_list[] hlist= resource.host_init();
        ResourcePool_Manager.flow_Info_list[] flist= resource.flow_init();
        ResourcePool_Manager.link_Info_list[] llist= resource.link_init();
        ResourcePool_Manager.intents_Info_list[] ilist= resource.intent_init();
        ResourcePool_Manager.switch_Info_list[] slist= resource.switch_init();
        ResourcePool_Manager.serviceHosts_INFO_list[] shlists= resource.service_Hosts_init();
        ResourcePool_Manager.path_INFO_list[] path_list= resource.path_init();
        ResourcePool_Manager.template_INFO_list[] tlist= resource.template_init();

        Intent_Installer installer = new Intent_Installer();
        ONOS_API_Handler onos_APIHandler = new ONOS_API_Handler();
        Template_Handler parsingTemplate = new Template_Handler();
        DB_Manager DB = new DB_Manager();
        DB_Template_Manager compare = new DB_Template_Manager();

        installer.remove_All_Intent();
        Thread.sleep(3000);

        //get Template information
        //JSON_INPUT();

        //get ONOS info and mapping
        slist = onos_APIHandler.GET_ONOS_INFO_Switch(slist);
        hlist = onos_APIHandler.GET_ONOS_INFO_Host(hlist);
        llist = onos_APIHandler.GET_ONOS_INFO_Link(llist);
        flist = onos_APIHandler.GET_ONOS_INFO_Flow(flist);

        PRINT(slist,hlist,ilist,llist,flist);

        //DB access & Tables creation & put ONOS data
        DB.Access();
        DB.DB_Data_Push();

        //Read template and parsing
        parsingTemplate.Read_Remote_Template();

        //Compare DB & template
        compare.Comapre();
        for (int i=0; i< tlist.length;i++){
            if (tlist[i].service!=null){
                path_list = onos_APIHandler.GET_ONOS_INFO_Path(path_list, tlist[i].service);
            }
        }

        PRINT_PATH(path_list);

        installer.choice_path(path_list);
        Thread.sleep(6000);
        ilist = onos_APIHandler.GET_ONOS_Info_Intent(ilist);
        DB.DB_Intent_Push(ilist);
    }

    private void PRINT_PATH(ResourcePool_Manager.path_INFO_list[] path_list) {
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

    private void PRINT(ResourcePool_Manager.switch_Info_list[] slist, ResourcePool_Manager.host_Info_list[] hlist, ResourcePool_Manager.intents_Info_list[] ilist, ResourcePool_Manager.link_Info_list[] llist, ResourcePool_Manager.flow_Info_list[] flist) {
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
