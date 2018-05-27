package de.vs.praktikum.praktikum2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * Created by Yang Mao on 5/27/18.
 * email: yang.mao@stud.hs-emden-leer.de
 */
public class HeartbeatTimerTask extends TimerTask {
    private String serverSlaveName;
    private SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
    public HeartbeatTimerTask(String serverSlaveName){
        this.serverSlaveName = serverSlaveName;
    }
    public void run(){
        VSServerMaster.getInstance().slaveRegister(serverSlaveName);
        //
       // System.out.println(serverSlaveName+" Send heardbeart at " + df.format(new Date().getTime()));
    }
}
