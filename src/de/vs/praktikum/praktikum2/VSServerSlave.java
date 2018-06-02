package de.vs.praktikum.praktikum2;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Yang Mao on 5/24/18.
 * email: yang.mao@stud.hs-emden-leer.de
 */
public class VSServerSlave extends Thread{
    private String name;
    private Timer heartbeat;

    /*
        Create a server.
    */
    public VSServerSlave(String name){
        this.name = name;
    }
    /*
        Get server name
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

    //    public set<Resource> callBackResourceSet(String serverName) {
//        for ( VSServerSlave server : resourceDistribution.values()){
//            if(serverName == server.getServerName())
//
//    }
    public Resource getResouce(String id){
        if (resourceHashMap.containsKey(id)){
            return resourceHashMap.get(id);
        }
        return null;
    }

    public void run(){
        heartbeat = new Timer("heartbeat-"+name);
        heartbeat.schedule(new HeartbeatTimerTask(name), 0,2000 );
//      VSServerMaster.getInstance().getServerSlaveMap().put(name , this);
        VSServerMaster.getInstance().receiveServer(this);
        System.out.println("Slave "+name+" start running!");

        while (!exit){
            try{
                //10s
               sleep(1000);
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

