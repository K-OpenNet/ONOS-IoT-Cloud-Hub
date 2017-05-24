package util;

/**
 * Created by netcsnuc on 5/9/17.
 */
public class ResourcePool {

    public static int MAX=100;
    private static ResourcePool resourcePool = null;
    switch_Info_list[] slist = new switch_Info_list[MAX];
    host_Info_list[] hlist = new host_Info_list[MAX];
    link_Info_list[] llist = new link_Info_list[MAX];
    intents_Info_list[] ilist = new intents_Info_list[MAX];
    flow_Info_list[] flist = new flow_Info_list[MAX];
    template_INFO_list[] template_INFO_lists = new template_INFO_list[MAX];
    serviceHosts_INFO_list[] service_host_list = new serviceHosts_INFO_list[MAX];
    path_INFO_list[] path_list = new path_INFO_list[MAX];


    private ResourcePool(){
        System.out.println("Singleton Started");
    }
    public static ResourcePool getInstance(){
        if(resourcePool == null)
            synchronized (ResourcePool.class) {
            if (resourcePool == null){
                resourcePool = new ResourcePool();
                System.out.println("New Singleton Instance !");
            }
                System.out.println("Existing SingleTon Instance !");
        }
        return resourcePool;
    }
    public switch_Info_list[] getSwitch_INFO_list() {
        return slist;
    }

    public host_Info_list[] getHost_INFO_list() {
        return hlist;
    }

    public link_Info_list[] getLink_INFO_list() {
        return llist;
    }

    public intents_Info_list[] getIntent_INFO_list() {
        return ilist;
    }

    public flow_Info_list[] getFlow_INFO_list() {
        return flist;
    }

    public template_INFO_list[] getTemplate_INFO_lists() { return template_INFO_lists; }

    public serviceHosts_INFO_list[] getServiceHost_INFO_list() { return service_host_list; }

    public switch_Info_list[] switch_init() {
        switch_Info_list[] list = new switch_Info_list[MAX];
        for (int i=0;i<list.length;i++)
        {
            list[i] = new switch_Info_list();
        }
        slist= list;
        return list;
    }
    public host_Info_list[] host_init(){
        host_Info_list[] list = new host_Info_list[MAX];
        for (int i=0;i<list.length;i++)
        {
            list[i] = new host_Info_list();
        }
        hlist=list;
        return list;
    }
    public link_Info_list[] link_init(){
        link_Info_list[] list = new link_Info_list[MAX];
        for (int i=0;i<list.length;i++)
        {
            list[i] = new link_Info_list();
        }
        llist=list;
        return list;
    }
    public intents_Info_list[] intent_init(){
        intents_Info_list[] list = new intents_Info_list[MAX];
        for (int i=0; i<list.length;i++)
        {
            list[i]= new intents_Info_list();
        }
        ilist=list;
        return list;
    }
    public flow_Info_list[] flow_init(){
        flow_Info_list[] list = new flow_Info_list[MAX];
        for (int i=0;i<list.length;i++)
        {
            list[i] = new flow_Info_list();
        }
        flist=list;
        return list;
    }
    public template_INFO_list[] template_init(){
        template_INFO_list[] list = new template_INFO_list[MAX];
        for (int i=0;i<list.length;i++){
            list[i] = new template_INFO_list();
        }
        template_INFO_lists =list;
        return list;
    }
    public serviceHosts_INFO_list[] service_Hosts_init(){
        serviceHosts_INFO_list[] list = new serviceHosts_INFO_list[MAX];
        for (int i =0; i<list.length;i++){
            list[i]= new serviceHosts_INFO_list();
        }
        service_host_list=list;
        return list;
    }
    public path_INFO_list[] path_init(){
        path_INFO_list[] list = new path_INFO_list[MAX];
        for (int i=0; i<list.length;i++){
            list[i]= new path_INFO_list();
        }
        path_list = list;
        return list;
    }
    public class switch_Info_list
    {
        public String DPID;
        public String type;
        public String manufactory;
        public String hardware;
        public String version;
        public String chassisID;

        public String[] port_name = new String[MAX];
        public String[] port_number= new String[MAX];

    }
    public class host_Info_list
    {
        public String ID;
        public String MAC;
        public String vlan;
        public String IP;
        public String location;
        //public String location_port;
    }
    public class link_Info_list
    {
        public String src;
        public String dst;
        public String type;
        public String state;
    }
    public class intents_Info_list
    {
        public String type;
        public String id;
        public String appId;
        public String[] resource = new String[MAX];
        public String state;
    }
    public class flow_Info_list
    {
        public String id;
        public String appId;
        public String priority;
        public String timeout;
        public String isPermanent;
        public String deviceId;
        public String state;
        public String life;
        public String packets;
        public String bytes;
        public String type;
        public String port;
        public String ctype;
        public String ethType;
    }
    public class template_INFO_list {
        public String version;
        public String service;
        public String[] zookeeper_IP = new String[MAX];
        public String[] zookeeper_Port = new String[MAX];
        public String[] broker_IP= new String[MAX];
        public String[] broker_Port = new String[MAX];
        public String[] consumer_IP= new String[MAX];
        public String[] consumer_Port = new String[MAX];
        public String[] producer_IP= new String[MAX];
        public String[] producer_Port = new String[MAX];
    }
    public class serviceHosts_INFO_list {
        public String ID;
        public String IP;
        public String Port;
        public String Vlan;
        public String Type;
        public String Service;
    }
    public class path_INFO_list {
        public String cost;
        public String[] links = new String[MAX];
        public String[] p_b_path = new String[MAX];
        public String[] p_z_path = new String[MAX];
        public String[] b_c_path = new String[MAX];
        public String[] b_z_path = new String[MAX];
        public String[] z_c_path = new String[MAX];
        public String type;
        public String state;
        public String description;
        public String service;
    }
}
