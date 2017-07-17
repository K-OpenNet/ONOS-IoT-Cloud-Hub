package util;

/**
 * Created by netcsnuc on 5/11/17.
 */
public class DB_Template_Manager {

    private  ResourcePool_Manager resource = ResourcePool_Manager.getInstance();
    private  ResourcePool_Manager.intents_Info_list[] ilist = resource.getIntent_INFO_list();
    private  ResourcePool_Manager.flow_Info_list[] flist = resource.getFlow_INFO_list();
    private  ResourcePool_Manager.switch_Info_list[] slist = resource.getSwitch_INFO_list();
    private  ResourcePool_Manager.host_Info_list[] hlist = resource.getHost_INFO_list();
    private  ResourcePool_Manager.link_Info_list[] llist = resource.getLink_INFO_list();
    private  ResourcePool_Manager.template_INFO_list[] tlist = resource.getTemplate_INFO_lists();
    private  ResourcePool_Manager.serviceHosts_INFO_list[] service_hostINFOlists = resource.getServiceHost_INFO_list();
    private static int MAX=100;

    public void Comapre(){

        // make Service_Host Object
        for (int i=0;i<MAX;i++){
            if(hlist[i].IP!=null){
                for (int j=0;j<MAX;j++){
                    for (int k=0;k<MAX;k++){
                        if(tlist[j].producer_IP[k]!=null){
                            if (hlist[i].IP.equals(tlist[j].producer_IP[k])){
                                System.out.println("Producer IP: "+hlist[i].IP);
                                service_hostINFOlists[i].IP = hlist[i].IP;
                                service_hostINFOlists[i].Type = "producer";
                                service_hostINFOlists[i].ID = hlist[i].ID;
                                service_hostINFOlists[i].Service = tlist[j].service;
                                tlist[j].producer_IP[k]="0";
                            }
                        } if (tlist[j].zookeeper_IP[k]!=null){
                            if (hlist[i].IP.equals(tlist[j].zookeeper_IP[k])){
                                System.out.println("Zookeeper IP: "+hlist[i].IP);
                                service_hostINFOlists[i].IP = hlist[i].IP;
                                service_hostINFOlists[i].Type = "zookeeper";
                                service_hostINFOlists[i].ID = hlist[i].ID;
                                service_hostINFOlists[i].Service = tlist[j].service;
                                tlist[j].zookeeper_IP[k]="0";
                            }
                        } if (tlist[j].broker_IP[k]!=null){
                            if (hlist[i].IP.equals(tlist[j].broker_IP[k])){
                                System.out.println("Broker IP: "+hlist[i].IP);
                                service_hostINFOlists[i].IP = hlist[i].IP;
                                service_hostINFOlists[i].Type = "broker";
                                service_hostINFOlists[i].ID = hlist[i].ID;
                                service_hostINFOlists[i].Service = tlist[j].service;
                                tlist[j].broker_IP[k]="0";
                            }
                        } if (tlist[j].consumer_IP[k]!=null) {
                            if (hlist[i].IP.equals(tlist[j].consumer_IP[k])){
                                System.out.println("Consumer IP: "+hlist[i].IP);
                                service_hostINFOlists[i].IP = hlist[i].IP;
                                service_hostINFOlists[i].Type = "consumer";
                                service_hostINFOlists[i].ID = hlist[i].ID;
                                service_hostINFOlists[i].Service = tlist[j].service;
                                tlist[j].consumer_IP[k]="0";
                            }
                        }
                    }
                }
            }
        }

        //there is no host in the real SDN OpenFlow topology, find that host identifier
        for (int i=0;i<MAX;i++){
            if (service_hostINFOlists[i].IP!=null){
               // System.out.println("ID: "+ service_hostINFOlists[i].ID+" IP: "+ service_hostINFOlists[i].IP+" Type: "+ service_hostINFOlists[i].Type+" Service: "+ service_hostINFOlists[i].Service);
            }
            for (int j=0;j<MAX;j++){
                if (tlist[i].producer_IP[j]!=null){
                    if (!tlist[i].producer_IP[j].equals("0")){
                        System.out.println("This Producer Host is not in topology IP: "+tlist[i].producer_IP[j]);
                    }
                }
                if (tlist[i].zookeeper_IP[j]!=null){
                    if (!tlist[i].zookeeper_IP[j].equals("0")){
                        System.out.println("This Zookeeper Host is not in topology IP: "+tlist[i].zookeeper_IP[j]);
                    }
                }
                if (tlist[i].broker_IP[j]!=null){
                    if (!tlist[i].broker_IP[j].equals("0")){
                        System.out.println("This Broker Host is not in topology IP: "+tlist[i].broker_IP[j]);
                    }
                }
                if (tlist[i].consumer_IP[j]!=null){
                    if (!tlist[i].consumer_IP[j].equals("0")){
                        System.out.println("This Consumer Host is not in topology IP: "+tlist[i].consumer_IP[j]);
                    }
                }
            }
        }
    }
}
