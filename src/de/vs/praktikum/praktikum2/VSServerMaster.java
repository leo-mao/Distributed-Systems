package de.vs.praktikum.praktikum2;

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
    int SLAVE_INACTIVE = 5000;
    int SLAVE_DOWN = SLAVE_INACTIVE * 10;
    int THREAD_SNOOZE = 1000;
    int SHORT_SNOOZE = THREAD_SNOOZE / 5;

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
                        ((new Date().getTime() - availableServerList.get(slaveName).getTime()) >= SLAVE_INACTIVE)){
                    availableServerList.put(slaveName, new Date());
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
    public boolean assignResource(Resource resource){
        String resourceId = resource.getId();
            try{
                VSServerSlave destSlave = RendezvousHash.getInstance().getDestServer(resource);
                if (destSlave != null) {
                    String destServerSlaveName = destSlave.getServerName();
                    System.out.println(resourceId + "----------->" + destServerSlaveName);
                    VSServerSlave destServerSlave = VSServerManager.getInstance().getServerSlaveMap().get(destServerSlaveName);
                    if (destServerSlave.storeResource(resource)) {
                        resourceDistribution.put(resourceId, destServerSlave);
                    }
                }
                else {// No server slave available
                    System.out.println("No server Slave available!");
                    return false;
                }
           }catch (Exception e) {
               e.printStackTrace();
               System.out.println("assignResource failed!");
           }
           return true;
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
        while (true){
            try {
                Resource resource;
                if ((resource = resourceQueue.poll()) != null && availableServerList.size() > 0){
                        while (!assignResource(resource)){
                            // when assignment failed, sleep.
                            sleep(SHORT_SNOOZE);
                        }
                }
                sleep(THREAD_SNOOZE);
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
        long time = availableServerList.get(serverName).getTime() - SLAVE_DOWN;
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
//            System.out.println("Check time difference:"+ (new Date().getTime() - availableServerList.get(slaveName).getTime()));
            if ((new Date().getTime() - availableServerList.get(slaveName).getTime()) < SLAVE_INACTIVE){
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
            lastHeartbeat = new Date(new Date().getTime() - SLAVE_DOWN);
            availableServerList.put(serverSlave.getServerName(), lastHeartbeat);
        }
        reassignResouces();
    }
}



//TODO Exception
//TODO LOG