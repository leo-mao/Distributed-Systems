package de.vs.praktikum.praktikum2;

import java.util.Date;
import java.util.Map;
import java.util.TimerTask;

/**
 * Created by Yang Mao on 5/27/18.
 * email: yang.mao@stud.hs-emden-leer.de
 */
public class InvalidSlaveCleanupTimeTask extends TimerTask {
    public void run(){
        Map<String, Date> heartbeatMap = VSServerMaster.getInstance().getLastHeartbeatAvailableServer();
        for(String slaveName: heartbeatMap.keySet()){
            if ((new Date().getTime() - heartbeatMap.get(slaveName).getTime())> 1000 * 3){
                heartbeatMap.remove(slaveName);
                System.out.println("!!!!!!Server slave "+ slaveName +" broke down!!!!!!");
            }
        }
    }
}
