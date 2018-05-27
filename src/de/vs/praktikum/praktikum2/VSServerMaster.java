package de.vs.praktikum.praktikum2;

import java.io.IOException;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;
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
    private Map<String, Date> lastHeartbeatAvailableServer = new ConcurrentHashMap<>();
    /**
     *     resouceLocation contains <resouce-id, serverObject> as value pairs.
     *     It indicates where the specific resource is stored.
     */
    private Map<String, VSServerSlave> resourceDistribution = new HashMap<String, VSServerSlave>();
    //private Map<String , String > resourceDistributionWithString =new HashMap<>();
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
//    void reassignResouces(String DeadServer){
//
//    }

    /**
     * Receive heartbeat from slaves, maintain the slave list
     *
     */
//todo exception
    public void slaveRegister(String slaveName){
            try{
                lastHeartbeatAvailableServer.put(slaveName, new Date());
            }catch (Exception e) {
            e.printStackTrace();
            System.out.println("put time to ServerMap failed");
        }


    }


    /**
     *     Assign a resource to proper server.
     * @param resource
     * @return  if assignment succeeded.
     */
    public void assignResource(Resource resource){
        String resourceId = resource.getId();
        List<String> availableSeverList = new ArrayList<>(serverSlaveMap.keySet());
        // TODO: Load balancing
       if (!availableSeverList.isEmpty()){
           try{
               int hash = resourceId.hashCode();
                int destServerSlaveIndex= (hash & Integer.MAX_VALUE) % availableSeverList.size();
                String destServerSlaveName = availableSeverList.get(destServerSlaveIndex);
                System.out.println("Get a valid destServer: " + destServerSlaveName+":"+  serverSlaveMap);
                VSServerSlave destServerSlave = serverSlaveMap.get(destServerSlaveName);
                if (serverSlaveMap.get(destServerSlaveName).storeResouce(resource)){
                    resourceDistribution.put(resourceId, destServerSlave);
                    //resourceDistributionWithString.put(resourceId,destServerSlave.getServerName());
                    System.out.println("add resource" + hash + "to " + destServerSlaveName);

                }
           }catch (Exception e) {
               e.printStackTrace();
               System.out.println("assignResource failed!");
           }
        }
        else{
            System.out.println("No valid server slaves, please add at least a server slave");
        }

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
        cleanup.schedule(new InvalidSlaveCleanupTimeTask(), 0, 500);
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

    public void printResourceDistibution() {
        System.out.println("-----Resource Distribution ----" + df.format(new Date()) + "------");

        System.out.println("ResourceID---------------Servername");
        for (String resourceId : resourceDistribution.keySet()) {
            System.out.println(resourceId + "------" + resourceDistribution.get(resourceId).getServerName());
//        Map<VSServerSlave,String> serverResourceIdMap = new HashMap<>();
//        for(Map.Entry<String,VSServerSlave> entry :resourceDistribution.entrySet()){
//            serverResourceIdMap.put(entry.getValue(), entry.getKey());
//        }System.out.println(serverResourceIdMap);
        }
    }



    public void receiveResource(Resource resource){
        assignResource(resource);
    }
    public void receiveResource(List<Resource> resourceList,String serverName) throws InterruptedException {
        long time = lastHeartbeatAvailableServer.get(serverName).getTime() - 5000;
        lastHeartbeatAvailableServer.put(serverName,new Date(time));
        manualCleanup();
        if(serverSlaveMap.keySet().size()!=(lastHeartbeatAvailableServer.keySet().size())){
            serverSlaveMap.remove(serverName);
            System.out.println("sdmsl");
            for (Resource resource : resourceList) {
                assignResource(resource);
            }
        }
        else System.out.println("buchenggong");
    }

    public void manualCleanup(){
        for(String slaveName: lastHeartbeatAvailableServer.keySet()){
            if ((new Date().getTime() - lastHeartbeatAvailableServer.get(slaveName).getTime())> 3000){
                lastHeartbeatAvailableServer.remove(slaveName);
                System.out.println("!!!!!!Server slave "+ slaveName +" broke down!!!!!!");
                System.out.println("lastHeartbeatAvailableServer number ："+ lastHeartbeatAvailableServer);
                System.out.println(serverSlaveMap);
            }
        }
    }

    public void reassignResouces() {
        List<VSServerSlave> SeverList = new ArrayList<>(serverSlaveMap.values());
        Set<Resource> resourceSet = new HashSet<>();
        if (!SeverList.isEmpty()){
            for (VSServerSlave server : SeverList){
                resourceSet.addAll(server.getResourceSet());
            }
        for (Resource resource : resourceSet){
                assignResource(resource);
            }
        }


    }

    public void receiveServer(VSServerSlave vsServerSlave) {
        reassignResouces();

    }
}



//TODO Exception
//TODO LOGLEVEL