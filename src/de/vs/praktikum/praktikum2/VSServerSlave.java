package de.vs.praktikum.praktikum2;

import de.vs.praktikum.praktikum3.RmiServerInterface;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Yang Mao on 5/24/18.
 * email: yang.mao@stud.hs-emden-leer.de
 */
public class VSServerSlave extends UnicastRemoteObject implements RmiServerInterface, Serializable, Runnable {
    private String name;
    private Timer heartbeat;
    private final int THREAD_SNOOZE = 1000;
    private final int HEARTBEAT = 2 * THREAD_SNOOZE;
    private final Logger LOGGER = Logger.getLogger(VSServerSlave.class.getName());
    /*
        Create a server.
    */
    public VSServerSlave(String name) throws Exception{
        super(0);
        this.name = name;
    }
    /*
        Get the server name
    */
    public String getServerName(){
        return name;
    }
    /*
    A HashMap saves resources with index of resouce-ids.
     */
    private boolean exit = false;
    private Map<String, Resource> resourceHashMap = new HashMap<>();
    public Map<String, Resource> getResourceHashMap(){
        return resourceHashMap;
    }
    public boolean storeResource(Resource resource){
        resourceHashMap.put(resource.getId(), resource);
        return true;
    }

    public boolean removeResouce(String id){
        if (resourceHashMap.containsKey(id)){
            resourceHashMap.remove(id);
            return true;
        }
        return false;
    }
    public Set<Resource> getResourceSet() {
        return new HashSet<>(resourceHashMap.values());
    }

    public Resource getResouce(String id){
        if (resourceHashMap.containsKey(id)){
            return resourceHashMap.get(id);
        }
        return null;
    }

    public void run(){
        heartbeat = new Timer("heartbeat-"+name);
        heartbeat.schedule(new HeartbeatTimerTask(name), 0,HEARTBEAT);
//      VSServerMaster.getInstance().getServerSlaveMap().put(name , this);
        VSServerMaster.getInstance().receiveServer(this);
        LOGGER.log(Level.INFO, "Slave "+name+" start running!");

        while (!exit){
            try{
                //10s
               Thread.sleep(THREAD_SNOOZE);
            }
            catch (Exception e){
                e.printStackTrace();
                LOGGER.log(Level.WARNING,getServerName()+":"+"sleep failed!");
            }

        }
    }

    void exit(){
        heartbeat.cancel();
           try {
               VSServerMaster.getInstance().receiveResource(new ArrayList<>(resourceHashMap.values()),this.name);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           exit = true;
        }

    @Override
    public String getMessage() {
        return "Hello From " + name;
    }
}

