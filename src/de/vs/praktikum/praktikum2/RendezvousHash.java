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

    public VSServerSlave getDestServer(Resource resource){
        String resourceId = resource.getId();
        List<VSServerSlave> serverList = VSServerMaster.getInstance().getAvailableServer();
        int currentScore = Integer.MIN_VALUE;
        int max = Integer.MIN_VALUE;
        VSServerSlave destSlave = null;
//        System.out.println("-------getting scores--------------");
//        System.out.println("Current slave number "+serverList.size());
        for (VSServerSlave slave: serverList){
            if (scoreTable.containsKey(new Pair<>(slave.getServerName(), resourceId))){
                currentScore = scoreTable.get(new Pair<>(slave.getServerName(), resourceId));
//                System.out.println("--------------Score table contains PAIR--------------");
            }
            else {
//                System.out.println("scoreTable doesn't contain the key, calculate the hash value responding to current key now");
                currentScore = hash(slave.getServerName(), resource.getId());
                scoreTable.put(new Pair<>(slave.getServerName(), resourceId), currentScore);
//                System.out.println("currentScore:"+currentScore);
            }
            if (currentScore > max){
                max = currentScore;
                destSlave = slave;
            }
        }
        if (destSlave == null){
            System.out.println("No Slave currently available!");
        }
        return destSlave;
    }

}
