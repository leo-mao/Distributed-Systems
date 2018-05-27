package de.vs.praktikum.praktikum2;

import java.util.*;

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
    public boolean storeResouce(Resource resource){
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
    public Set<String> getResouceSet(){
        return resourceHashMap.keySet();
    }
    public Resource getResouce(String id){
        if (resourceHashMap.containsKey(id)){
            return resourceHashMap.get(id);
        }
        return null;
    }
    public void run(){
        heartbeat = new Timer("heartbeat-"+name);
        heartbeat.schedule(new HeartbeatTimerTask(name), 0,1000 * 2);
//        System.out.println(new Date().getTime());
        System.out.println("Slave "+name+" start running!");
        while (!exit){
            try{
                //10s
                sleep(10000);
            }
            catch (Exception e){
                e.printStackTrace();
                System.out.println(getServerName()+":"+"sleep failed!");
            }

        }
    }
    void exit(){
        exit = true;
    }
}
