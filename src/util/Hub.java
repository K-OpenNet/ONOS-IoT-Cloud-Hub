package util;

/**
 * Created by netcsnuc on 5/9/17.
 */
public class Hub {
    public static void main(String[] args) throws Exception {

        System.out.println(" _____         _____          _____  _                    _   _   _         _     \n" +
                                   "|_   _|       |_   _|        /  __ \\| |                  | | | | | |       | |    \n" +
                                   "  | |    ___    | |   ______ | /  \\/| |  ___   _   _   __| | | |_| | _   _ | |__  \n" +
                                   "  | |   / _ \\   | |  |______|| |    | | / _ \\ | | | | / _` | |  _  || | | || '_ \\ \n" +
                                   " _| |_ | (_) |  | |          | \\__/\\| || (_) || |_| || (_| | | | | || |_| || |_) |\n" +
                                   " \\___/  \\___/   \\_/           \\____/|_| \\___/  \\__,_| \\__,_| \\_| |_/ \\__,_||_.__/ \n" +
                                   "                                                                                  \n" +
                                   "                                                                                  \n");

        System.out.println("______  _                    _____  _                      _                  __                      _    _               \n" +
                                   "|  ___|| |                  /  ___|| |                    (_)                / _|                    | |  (_)              \n" +
                                   "| |_   | |  ___  __      __ \\ `--. | |_   ___   ___  _ __  _  _ __    __ _  | |_  _   _  _ __    ___ | |_  _   ___   _ __  \n" +
                                   "|  _|  | | / _ \\ \\ \\ /\\ / /  `--. \\| __| / _ \\ / _ \\| '__|| || '_ \\  / _` | |  _|| | | || '_ \\  / __|| __|| | / _ \\ | '_ \\ \n" +
                                   "| |    | || (_) | \\ V  V /  /\\__/ /| |_ |  __/|  __/| |   | || | | || (_| | | |  | |_| || | | || (__ | |_ | || (_) || | | |\n" +
                                   "\\_|    |_| \\___/   \\_/\\_/   \\____/  \\__| \\___| \\___||_|   |_||_| |_| \\__, | |_|   \\__,_||_| |_| \\___| \\__||_| \\___/ |_| |_|\n" +
                                   "                                                                      __/ |                                                \n" +
                                   "                                                                     |___/                                                 \n");
        ResourcePool_Manager resource = ResourcePool_Manager.getInstance();
        ResourcePool_Manager.host_Info_list[] hlist= resource.host_init();
        ResourcePool_Manager.flow_Info_list[] flist= resource.flow_init();
        ResourcePool_Manager.link_Info_list[] llist= resource.link_init();
        ResourcePool_Manager.intents_Info_list[] ilist= resource.intent_init();
        ResourcePool_Manager.switch_Info_list[] slist= resource.switch_init();
        ResourcePool_Manager.serviceHosts_INFO_list[] shlists= resource.service_Hosts_init();
        ResourcePool_Manager.path_INFO_list[] path_list= resource.path_init();
        ResourcePool_Manager.template_INFO_list[] tlist= resource.template_init();
        ResourcePool_Manager.steering_path_INFO_list[] steer_list = resource.steer_init();

        Intent_Installer installer = new Intent_Installer();
        ONOS_API_Handler onos_APIHandler = new ONOS_API_Handler();
        Template_Handler parsingTemplate = new Template_Handler();
        DB_Manager DB = new DB_Manager();
        DB_Template_Manager compare = new DB_Template_Manager();
        Provisioning_Handler pHandler = new Provisioning_Handler();
        Steering_Handler sHandler = new Steering_Handler();
        Printer print = new Printer();

        //get Template information
        //JSON_INPUT();
        installer.remove_All_Intent();

        //get ONOS info and mapping
        slist = onos_APIHandler.GET_ONOS_INFO_Switch(slist);
        hlist = onos_APIHandler.GET_ONOS_INFO_Host(hlist);
        llist = onos_APIHandler.GET_ONOS_INFO_Link(llist);
        flist = onos_APIHandler.GET_ONOS_INFO_Flow(flist);

        print.PRINT(slist,hlist,ilist,llist,flist);

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
        print.PRINT_PATH(path_list);
        //PRINT_PATH(path_list);
        installer.choice_path(path_list);

        ilist = onos_APIHandler.GET_ONOS_Info_Intent(ilist);
        DB.DB_Intent_Push(ilist);

        Thread.sleep(60000);
        int k=0;
        while (true) {
            if(DB.compare_topology()) {
                //Steering
                System.out.println("No Topology Changed [log-K]: "+k);
                k+=1;
                sHandler.steering(resource,path_list);
            } else {
                System.out.println("Topology Changed [log-K]: "+k);
                k+=1;
                pHandler.provisioning(resource);
            }
            Thread.sleep(60000);
        }
    }
}

