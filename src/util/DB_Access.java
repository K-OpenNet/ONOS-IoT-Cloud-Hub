package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by netcsnuc on 2/20/17.
 */
public class DB_Access {
    private static String DB_IP="203.237.53.141";
    public static void Access()
    {
        try{
            Connection connection =null;
            Statement statement = null;

            System.out.println("DB success");
            String driver = "com.mysql.jdbc.Driver";
            Class.forName(driver).newInstance();
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1", "root", "secrete");
            System.out.println("Opened database successfully");
            statement = connection.createStatement();

            statement.executeQuery("use netdb;");
            statement.executeUpdate("drop table IF EXISTS FLOW");
            statement.executeUpdate("drop table IF EXISTS LINK");
            statement.executeUpdate("drop table IF EXISTS SWITCH");
            statement.executeUpdate("drop table IF EXISTS SWITCH_PORT");
            statement.executeUpdate("drop table IF EXISTS HOST");
            statement.executeUpdate("drop table IF EXISTS INTENT");

            String sql = "CREATE TABLE FLOW" +
                    "(ID VARCHAR(32)," +
                    " APPID VARCHAR(32)," +
                    " PRIORITY VARCHAR(32)," +
                    " TIMEOUT VARCHAR(32)," +
                    " ISPERMANENT VARCHAR(32),"+
                    " DEVICEID VARCHAR(32)," +
                    " STATE VARCHAR(32)," +
                    " LIFE VARCHAR(32)," +
                    " PACKETS VARCHAR(32)," +
                    " BYTES VARCHAR(32)," +
                    " TYPE VARCHAR(32)," +
                    " PORT VARCHAR(32)," +
                    " CRITERIA_TYPE VARCHAR(32)," +
                    " ETH_TYPE VARCHAR(32))";

            statement.executeUpdate(sql);

            sql = "CREATE TABLE SWITCH" +
                    "(DPID VARCHAR(32)," +
                    " TYPE VARCHAR(32)," +
                    " MADE VARCHAR(32)," +
                    " HW VARCHAR(32)," +
                    " OS_Ver VARCHAR(32)," +
                    " PORT VARCHAR(256))";

            statement.executeUpdate(sql);

            sql = "CREATE TABLE SWITCH_PORT" +
                    " (DPID VARCHAR(32)," +
                    " PORT_NAME VARCHAR(32)," +
                    " PORT VARCHAR(32))";

            statement.executeUpdate(sql);

            sql = "CREATE TABLE HOST" +
                    " (ID VARCHAR(32)," +
                    " MAC VARCHAR(32)," +
                    " VLAN VARCHAR(32)," +
                    " IP VARCHAR(32)," +
                    " LOCATION VARCHAR(32));";

            statement.executeUpdate(sql);

            sql = "CREATE TABLE INTENT" +
                    " (TYPE VARCHAR(32)," +
                    " ID VARCHAR(32)," +
                    " APPID VARCHAR(32)," +
                    " STATE VARCHAR(32));";

            statement.executeUpdate(sql);

            sql = "CREATE TABLE LINK" +
                    " (SRC VARCHAR(32)," +
                    " DST VARCHAR(32)," +
                    " TYPE VARCHAR(32)," +
                    " STATE VARCHAR(32));";

            statement.executeUpdate(sql);

        } catch (SQLException e){
            System.out.println("SQLException: "+ e.getMessage());
            System.out.println("SQLState: "+ e.getSQLState());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("org.sqlite.JDBC can not found");
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    public static void DB_Data_Push() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Connection connection =null;
        Statement statement = null;


        String driver = "com.mysql.jdbc.Driver";
        Class.forName(driver).newInstance();
        connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1", "root", "secrete");

        statement = connection.createStatement();

        statement.executeQuery("use netdb;");
        System.out.println("Opened netdb database successfully");

        ResourcePool resource = ResourcePool.getInstance();
        ResourcePool.host_Info_list[] hlist = resource.getHost_INFO_list();
        ResourcePool.switch_Info_list[] slist = resource.getSwitch_INFO_list();
        ResourcePool.flow_Info_list[] flist = resource.getFlow_INFO_list();
        ResourcePool.link_Info_list[] llist = resource.getLink_INFO_list();
        ResourcePool.intents_Info_list[] ilist = resource.getIntent_INFO_list();

        for (int i=0;i<flist.length;i++){
            if (flist[i].id==null)
                continue;
            String sql = "INSERT INTO FLOW (ID,APPID,PRIORITY,TIMEOUT,ISPERMANENT,DEVICEID,STATE,LIFE,PACKETS,BYTES,TYPE,PORT,CRITERIA_TYPE,ETH_TYPE) " +
                    "VALUES ( '"+ flist[i].id+"', '"+flist[i].appId+"', '"+flist[i].priority+"', '"+flist[i].timeout+"', '"+flist[i].isPermanent+"', '"+flist[i].deviceId+"', '" +
                    flist[i].state+"', '"+flist[i].life+"', '"+flist[i].packets+"', '"+flist[i].bytes+"', '"+flist[i].type+"', '"+flist[i].port+"', '"+flist[i].ctype+"', '"+flist[i].ethType+"');";

            statement.executeUpdate(sql);
        }
        for (int i=0;i<slist.length;i++){
            if (slist[i].DPID==null)
                continue;
            String port_name="";
            String port_num="";
            String sql="INSERT INTO SWITCH (DPID,TYPE,MADE,HW,OS_Ver,PORT)" +
                    "VALUES ( '"+ slist[i].DPID+"', '"+slist[i].type+"', '"+slist[i].manufactory+"', '"+slist[i].hardware+"', '"+slist[i].version+"', '";
            for (int j=0;j<slist[i].port_number.length;j++){
                if (slist[i].port_number[j]==null)
                    break;
                //port_name+=slist[i].port_name[j]+" ";
                port_num+=slist[i].port_number[j]+" ";
            }
            sql += port_num+"');";

            statement.executeUpdate(sql);
        }
        for (int i=0;i<slist.length;i++){
            if (slist[i].DPID==null)
                continue;
            String port_name="";
            for (int j=0;j<slist[i].port_number.length;j++)
            {
                if (slist[i].port_number[j]==null)
                    continue;
                String sql = "INSERT INTO SWITCH_PORT (DPID,PORT_NAME,PORT)" +
                        "VALUES ( '"+ slist[i].DPID+"', '" ;
                port_name = slist[i].port_name[j]+"', '"+slist[i].port_number[j]+"');";
                sql+=port_name;
                statement.executeUpdate(sql);
            }
        }
        for (int i=0;i<hlist.length;i++){
            if (hlist[i].ID==null)
                continue;
            String sql = "INSERT INTO HOST (ID,MAC,VLAN,IP,LOCATION)" +
                    "VALUES ( '"+ hlist[i].ID+"', '"+hlist[i].MAC+"', '"+hlist[i].vlan+"', '"+hlist[i].IP+"', '"+hlist[i].location+"');";
            statement.executeUpdate(sql);

        }
        for (int i=0;i<ilist.length;i++){
            if (ilist[i].id==null)
                continue;
            String sql = "INSERT INTO INTENT (TYPE,ID,APPID,STATE)"+
                    "VALUES ( '"+ilist[i].type+"', '"+ilist[i].id+"', '"+ilist[i].appId+"', '"+ilist[i].state+"');";
            statement.executeUpdate(sql);
        }
        for (int i=0;i<llist.length;i++){
            if (llist[i].src==null)
                continue;

            String sql = "INSERT INTO LINK (SRC,DST,TYPE,STATE)"+
                    "VALUES ( '"+llist[i].src+"', '"+llist[i].dst+"', '"+llist[i].type+"', '"+llist[i].state+"');";

            statement.executeUpdate(sql);
        }

    }
}
