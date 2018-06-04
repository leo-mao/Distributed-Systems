package de.vs.praktikum.praktikum2;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Yang Mao on 5/24/18.
 * email: yang.mao@stud.hs-emden-leer.de
 */
public class VSServerSlave extends Thread implements Serializable {
    private String name;
    private Timer heartbeat;
    private final int THREAD_SNOOZE = 1000;
    private final int HEARTBEAT = 2 * THREAD_SNOOZE;
    /*
        Create a server.
    */
    public VSServerSlave(String name){
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
    private Map<String, Resource> resourceHashMap = new HashMap<String, Resource>();
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
        return new HashSet<Resource>(resourceHashMap.values());
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
        System.out.println("Slave "+name+" start running!");

        while (!exit){
            try{
                //10s
               sleep(THREAD_SNOOZE);
            }
            catch (Exception e){
                e.printStackTrace();
                System.out.println(getServerName()+":"+"sleep failed!");
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

    }

