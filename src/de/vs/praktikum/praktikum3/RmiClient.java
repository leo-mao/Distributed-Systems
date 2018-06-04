package de.vs.praktikum.praktikum3;

/**
 * Created by Yang Mao on 6/4/18.
 * email: yang.mao@stud.hs-emden-leer.de
 */
import java.rmi.Naming;
public class RmiClient {
    public static void main(String[] args) throws Exception{
        RmiServerInterface obj = (RmiServerInterface) Naming.lookup("//localhost/RmiServer");
        System.out.println("Client Started");
        System.out.println("Get the Message: " + obj.getMessage());
    }
}
