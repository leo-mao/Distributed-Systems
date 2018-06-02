package de.vs.praktikum.praktikum2;

import javax.sound.midi.SysexMessage;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Yang Mao on 5/24/18.
 * email: yang.mao@stud.hs-emden-leer.de
 */
public class VSServerMaster extends Thread{
    private static VSServerMaster instance = new VSServerMaster();
    private VSServerMaster(){}
    private ConcurrentLinkedQueue<Resource> resourceQueue = new ConcurrentLinkedQueue();
    static VSServerMaster getInstance(){
        return instance;
    }
    int INACTIVE = 5000;
    int INACTIVE_LONG_AGO = 50000;
    /**
     *     resouceLocation contains <resource-id, serverObject> as value pairs.
     *     It indicates where the specific resource is stored.
     */
    private Map<String, VSServerSlave> resourceDistribution = new HashMap<String, VSServerSlave>();
    // All servers references(including those are not available)
    private Map<String, VSServerSlave> serverSlaveMap = new HashMap<>();
    // This map records the last heartbeat of servers
    private Map<String, Date> availableServerList = new ConcurrentHashMap<>();
    private SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
    public Map<String, Date> getAvailableServerList() {
        return availableServerList;
    }

    /**
     * Receive heartbeat from slaves, maintain the slave list
     *
     */
    public void slaveRegister(String slaveName){
            try{
                if (availableServerList.get(slaveName) == null ||
                        ((new Date().getTime() - availableServerList.get(slaveName).getTime()) >= INACTIVE)){
                    reassignResouces();
                }
                else {
                    availableServerList.put(slaveName, new Date());
                }
            }catch (Exception e) {
            e.printStackTrace();
            System.out.println("Slave register failed");
        }
    }

    /**
     *     Assign a resource to proper server.
     * @param resource
     * @return  if assignment succeeded.
     */
    public void assignResource(Resource resource){
        String resourceId = resource.getId();
            try{
                String destServerSlaveName = RendezvousHash.getInstance().getDestServer(resource);
                if (!destServerSlaveName.isEmpty()) {
                    System.out.println(resourceId + "----------->" + destServerSlaveName);
                    VSServerSlave destServerSlave = VSServerManager.getInstance().getServerSlaveMap().get(destServerSlaveName);
                    if (destServerSlave.storeResource(resource)) {
                        resourceDistribution.put(resourceId, destServerSlave);
                    }
                }
                else {// No server slave available
                    System.out.println("No server Slave available!");
                }
           }catch (Exception e) {
               e.printStackTrace();
               System.out.println("assignResource failed!");
           }
        }

    /**
     *
     * @return getServerSlaveMap
     */
    public Map<String, VSServerSlave> getServerSlaveMap() {
        return serverSlaveMap;
    }
    /**
     * Thread run
     */
    public void run() {
//        cleanup = new Timer("Cleanup");
//        cleanup.schedule(new InvalidSlaveCleanupTimeTask(), 5000, 1000);
        while (true){
            try {
                if (resourceQueue.size() > 0 && availableServerList.size() > 0){
                    for(Resource resource: resourceQueue){
                        System.out.println("Assigning "+resource.getId());
                        assignResource(resource);
                    }
                }
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
        for(VSServerSlave key:getAvailableServer())
        System.out.println(key.getServerName()+" since "+ availableServerList.get(key.getServerName()));
    }

    public void printResourceDistibution() {
        System.out.println("-------ResourceID-------Servername---- " + df.format(new Date()) + "-----");
        for (String resourceId : resourceDistribution.keySet()) {
            System.out.println(resourceId + "---------" + resourceDistribution.get(resourceId).getServerName());
        }
    }


    public void receiveResource(List<Resource> resourceList,String serverName) throws InterruptedException {
        long time = availableServerList.get(serverName).getTime() - INACTIVE_LONG_AGO;
        availableServerList.put(serverName,new Date(time));
        if(serverSlaveMap.keySet().size()!=(availableServerList.keySet().size())){
            serverSlaveMap.remove(serverName);
            for (Resource resource : resourceList) {
                receiveResource(resource);
            }
        }
    }

    public void receiveResource(Resource resource){
        resourceQueue.add(resource);
    }

    public ArrayList<VSServerSlave> getAvailableServer(){
        ArrayList<VSServerSlave> availableList = new ArrayList<>();
        for(String slaveName: availableServerList.keySet()){
            if ((new Date().getTime() - availableServerList.get(slaveName).getTime()) < INACTIVE){
                availableList.add(VSServerManager.getInstance().getServerSlaveMap().get(slaveName));
            }
        }
        return new ArrayList<>(availableList);
    }

    //TODO Bug
    public void reassignResouces() {
        List<VSServerSlave> SeverList = getAvailableServer();
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

    public void receiveServer(VSServerSlave serverSlave) {
        Date lastHeartbeat = availableServerList.get(serverSlave.getServerName());

        if (lastHeartbeat == null){
            System.out.println(serverSlave.getServerName());
            lastHeartbeat = new Date(new Date().getTime() - INACTIVE_LONG_AGO);
            availableServerList.put(serverSlave.getServerName(), lastHeartbeat);
        }
        reassignResouces();
    }
}



//TODO Exception
//TODO LOG