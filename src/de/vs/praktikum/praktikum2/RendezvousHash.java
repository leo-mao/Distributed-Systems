package de.vs.praktikum.praktikum2;

import java.util.*;

/**
 * Created by Yang Mao on 5/27/18.
 * email: yang.mao@stud.hs-emden-leer.de
 */
public class RendezvousHash {
    private Map<Pair<String, String>, Integer> scoreTable = new HashMap<>();
    private static RendezvousHash instance = new RendezvousHash();
    private Set serverSet = VSServerMaster.getInstance().getAvailableServerList().keySet();
    private List resourceList = VSServerManager.getInstance().getResourceList();
    private RendezvousHash(){}

    public Map<Pair<String, String>, Integer> getScoreTable() {
        return scoreTable;
    }

    public static RendezvousHash getInstance() {
        return instance;
    }

    public int hash(String serverSlaveName, String resourceId){
        int baseHash = resourceId.hashCode();
        return (serverSlaveName + baseHash).hashCode() & Integer.MAX_VALUE;
    }

    public String getDestServer(Resource resource){
        String resourceId = resource.getId();
        List<VSServerSlave> serverList = VSServerMaster.getInstance().getAvailableServer();
        int index = -1;
        int max = Integer.MIN_VALUE;
        System.out.println("-------getting scores--------------");
        for(int i=0; i<serverList.size(); i++){
            String slaveName = serverList.get(i).getServerName();
            int currentScore;
            if (scoreTable.containsKey(new Pair<>(slaveName, resourceId))){
                currentScore = scoreTable.get(new Pair<>(slaveName, resourceId));
            }
            else {
                System.out.println("scoreTable doesn't contain the key, calculate the hash value responding to current key now");
                currentScore = hash(slaveName, resource.getId());
                scoreTable.put(new Pair<>(slaveName, resourceId), currentScore);
            }
            System.out.println(resourceId+":"+slaveName+":"+currentScore);
            if (currentScore > max){
                index = i;
                max = currentScore;
            }
        }

        if (index != -1){
            return  serverList.get(index).getName();
        }
        System.out.println("ServerSlave index = -1!\n No Slave currently available!");
        return null;
    }

}
