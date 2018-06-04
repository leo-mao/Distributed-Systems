package de.vs.praktikum.praktikum3;

/**
 * Created by Yang Mao on 6/3/18.
 * email: yang.mao@stud.hs-emden-leer.de
 */

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
public class RmiServer extends UnicastRemoteObject implements RmiServerInterface, Runnable{
    public static final String MESSAGE= "From Server: Hello World";
    public RmiServer() throws RemoteException {
        super(0);
    }

    public String getMessage(){
        return MESSAGE;
    }

    public void run(){

    }
    public static void main(String args[]) throws Exception{
        System.out.println("RMI Server started");
        try{
            LocateRegistry.createRegistry(1099);
            System.out.println("Java RMI registry created.");
        }catch (RemoteException e){
            System.out.println("Java RMI already exists.");
        }
        //Instance RmiServer
        RmiServer obj = new RmiServer();

        //Bind this object instance to the name "RmiServer"
        Naming.rebind("//localhost/RmiServer", obj);
        System.out.println("PeerServer bound in registry");
    }

}