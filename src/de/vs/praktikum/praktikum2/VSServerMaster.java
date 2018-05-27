package de.vs.praktikum.praktikum2;

import java.util.*;
import java.text.SimpleDateFormat;
//import java.security.MessageDigest;
/**
 * Created by Yang Mao on 5/24/18.
 * email: yang.mao@stud.hs-emden-leer.de
 */
public class VSServerMaster extends Thread{
    private static VSServerMaster instance = new VSServerMaster();
    private VSServerMaster(){}
    static VSServerMaster getInstance(){
        return instance;
    }
    private Map<String, VSServerSlave> serverSlaveMap = new HashMap<>();
    private Map<String, Date> lastHeartbeatAvailableServer = new HashMap<>();
    /**
     *     resouceLocation contains <resouce-id, serverObject> as value pairs.
     *     It indicates where the specific resource is stored.
     */
    private Map<String, VSServerSlave> resourceDistribution = new HashMap<String, VSServerSlave>();
    private SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
    private Timer cleanup;
    public Map<String, Date> getLastHeartbeatAvailableServer() {
        return lastHeartbeatAvailableServer;
    }

    /*
        Add a new server to the available server set.
         */
//    boolean addServerSlave(VSServerSlave slave){
//        serverSlaveMap.put(slave.getServerName(), slave);
//        System.out.println("Add a new slave: "+slave.getServerName());
//        printAvailableServerlist();
//        return true;
//    }
//
//    /**
//     *     Update the available server set.
//     * @param slavename
//     * @return if remove succeeded.
//     */
//    boolean removeServerSlave(String slavename){
//        if (serverSlaveMap.keySet().contains(slavename)){
//            serverSlaveMap.remove(slavename);
//            return true;
//        }
//        return false;
//    }

    /**
     *     Redistribute resources from a specific server to other servers.
     * @param DeadServer
     */
    private void reassignResouces(String DeadServer){
    }

    /**
     * Receive heartbeat from slaves, maintain the slave list
     * @return
     */

    boolean slaveRegister(String slaveName){
//        if (serverSlaveMap.containsKey(slaveName)){
            lastHeartbeatAvailableServer.put(slaveName, new Date());
//        }
        return false;
    }


    /**
     *     Assign a resource to proper server.
     * @param resource
     * @return  if assignment succeeded.
     */
    private boolean assignResource(Resource resource){
        String resourceId = resource.getId();
        List<String> availableSeverList = new ArrayList<>(serverSlaveMap.keySet());
        // TODO: Load balancing
        if (!availableSeverList.isEmpty()){
            int hash = resourceId.hashCode();
            int destServerSlaveIndex= (hash & Integer.MAX_VALUE) % availableSeverList.size();
            String destServerSlaveName = availableSeverList.get(destServerSlaveIndex);
            System.out.println("Get a valid destServer: " + destServerSlaveName+":"+  serverSlaveMap.get(destServerSlaveName));
            VSServerSlave destServerSlave = serverSlaveMap.get(destServerSlaveName);
            if (serverSlaveMap.get(destServerSlaveName).storeResouce(resource)){
                resourceDistribution.put(resourceId, destServerSlave);
                System.out.println("add resource" + hash + "to " + destServerSlaveName);
                return true;
            }
        }
        else{
            System.out.println("No valid server slaves, please add at least a server slave");
        }
        return false;
    }

    /**
     *
     * @return getServerSlaveMap
     */
    public Map<String, VSServerSlave> getServerSlaveMap() {
        return serverSlaveMap;
    }
    public void getAvailableServerList(){
        printAvailableServerlist();
    }
    /**
     * Thread run
     */
    public void run() {
        cleanup = new Timer("cleanup");
        cleanup.schedule(new InvalidSlaveCleanupTimeTask(), 0, 1000 * 5);
        while (true){
            try {
//                printAvailableServerlist();
                sleep(1000);

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Sleep failed");
            }
        }
    }

    /**
     *    Show the list of available slaves
     */
    public void printAvailableServerlist(){
        System.out.println("-----available Slave ------"+df.format(new Date())+"------");
        System.out.println(lastHeartbeatAvailableServer);
    }
    public void printResourceDistibution(){
        System.out.println("-----Resource Distribution ----"+df.format(new Date())+"------");
//        serverSlaveMap.entrySet().forEach(System.out::println);
//        System.out.println();
        System.out.println(resourceDistribution);
    }
    public void receiveResource(Resource resource){
        assignResource(resource);
    }
}

//TODO Exception
//TODO LOGLEVEL