package de.vs.praktikum.praktikum2;

import java.util.*;

/**
 * Created by Yang Mao on 5/27/18.
 * email: yang.mao@stud.hs-emden-leer.de
 */
public class RendezvousHash {
    private Map<Pair<String, String>, Integer> scoreTable = new HashMap<>();
    private static RendezvousHash instance = new RendezvousHash();
    private Set serverSet = VSServerMaster.getInstance().getLastHeartbeatAvailableServer().keySet();
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
//    public boolean calculateScoreForNewResource(Resource resource){
////        serverSet = VSServerMaster.getInstance().getLastHeartbeatAvailableServer().keySet();
////        resourceList = VSServerManager.getInstance().getResourceList();
//        for(Object serverSlaveName:serverSet){
//            String slaveName = String.class.cast(serverSlaveName);
//
//        }
//        return false;
//    }
//    public void generateScoreTable(){
////        scoreTable = new HashMap<>();
////        serverSet = VSServerMaster.getInstance().getLastHeartbeatAvailableServer().keySet();
////        resourceList = VSServerManager.getInstance().getResourceList();
//        for(Object serverSlaveName:serverSet){
//            String slaveName = String.class.cast(serverSlaveName);
//            for(Object object: resourceList){
//                Resource resource =  Resource.class.cast(object);
//                int score = hash(slaveName, resource.getId());
//                scoreTable.put(new Pair<String, String>(slaveName, resource.getId()), score);
//            }
//        }
//    }

    public String getDestServer(Resource resource){
        String resourceId = resource.getId();
        List serverList = new ArrayList(VSServerMaster.getInstance().getLastHeartbeatAvailableServer().keySet());
        int index = -1;
        int max = Integer.MIN_VALUE;
        for(int i=0; i<serverList.size(); i++){
            String slaveName = String.class.cast(serverList.get(i));
            int currentScore;
            if (scoreTable.containsKey(new Pair<>(slaveName, resourceId))){
                currentScore = scoreTable.get(new Pair<>(slaveName, resourceId));
            }
            else {
                System.out.println("scoreTable doesn't contain the key, calculate the hash value responding to key now");
                currentScore = hash(slaveName, resource.getId());
                scoreTable.put(new Pair<>(slaveName, resourceId),currentScore);
            }
            System.out.println(resourceId+":"+slaveName+":"+currentScore);
            if (currentScore > max){
                index = i;
                max = currentScore;
            }

        }
        if (index != -1){
            return  String.class.cast(serverList.get(index));
        }
        System.out.println("index = -1!");
        return null;
    }

}
