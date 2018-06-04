package de.vs.praktikum.praktikum3;

/**
 * Created by Yang Mao on 6/4/18.
 * email: yang.mao@stud.hs-emden-leer.de
 */
import de.vs.praktikum.praktikum2.VSServerSlave;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class RmiClient extends UnicastRemoteObject implements RmiServerInterface, Runnable{
    private static final String MESSAGE = "From Client: Hello World";
    public RmiClient() throws RemoteException {
        super(0);
    }
    public static void main(String[] args) throws Exception{
        RmiServerInterface obj = (RmiServerInterface) Naming.lookup("//localhost/RmiServer");
        System.out.println("Client Started");
        System.out.println("Get the Message: " + obj.getMessage());
    }
    public String getMessage(){
        return MESSAGE;
    }
    public void run(){
        try{

            RmiServerInterface obj = (RmiServerInterface) Naming.lookup("//localhost/RmiServer");
        }catch (Exception e){
            System.out.println("Naming.lookup failed");
            e.printStackTrace();
        }

    }
}
