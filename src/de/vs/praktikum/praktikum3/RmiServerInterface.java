package de.vs.praktikum.praktikum3;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Yang Mao on 6/3/18.
 * email: yang.mao@stud.hs-emden-leer.de
 */
public interface RmiServerInterface extends Remote {
    public String getMessage() throws RemoteException;
}
