package de.vs.praktikum.praktikum2;

import java.util.*;

/**
 * Created by Yang Mao on 5/27/18.
 * email: yang.mao@stud.hs-emden-leer.de
 */
public class RendezvousHash {
    private Map<Pair<String, String>, Integer> scoreTable;
    private static RendezvousHash instance = new RendezvousHash();
    private RendezvousHash(){}

    public static RendezvousHash getInstance() {
        return instance;
    }

    public int hash(String serverSlaveName, String resourceId){
        int baseHash = resourceId.hashCode();
        return (serverSlaveName + baseHash).hashCode() & Integer.MAX_VALUE;
    }
    public Map<Pair<String, String>, Integer> getScoreTable(){
        scoreTable = new HashMap<>();
        Set serverSet = VSServerMaster.getInstance().getLastHeartbeatAvailableServer().keySet();
        List resourceList = VSServerManager.getInstance().getResourceList();
        for(Object serverSlaveName:serverSet){
            String slaveName = String.class.cast(serverSlaveName);
            for(Object object: resourceList){
                Resource resource =  Resource.class.cast(object);
                int score = hash(slaveName, resource.getId());
                scoreTable.put(new Pair<String, String>(slaveName, resource.getId()), score);
            }
        }
        return scoreTable;
    }
    public String getDestServer(Resource resource){
        String resouceId = resource.getId();
        List serverList = new ArrayList(VSServerMaster.getInstance().getLastHeartbeatAvailableServer().keySet());
        int index = -1;
        int max = Integer.MIN_VALUE;
        for(int i=0; i<serverList.size(); i++){
            String slaveName = String.class.cast(serverList.get(i));
            if (scoreTable.get(new Pair<>(slaveName, resouceId)) > max){
                index = i;
                max = scoreTable.get(new Pair<>(slaveName, resouceId));
            }
        }
        if (index != -1){
            return  String.class.cast(serverList.get(index));
        }
        System.out.println("cuowu");
        return null;
    }

}
