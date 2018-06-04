package de.vs.praktikum.praktikum2;

import de.vs.praktikum.praktikum3.RmiServerInterface;

import java.io.*;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Yang Mao on 5/25/18.
 * email: yang.mao@stud.hs-emden-leer.de
 */
public class VSServerManager implements Runnable{
    private static VSServerManager instance = new VSServerManager();
    private static Logger LOGGER = Logger.getLogger(VSServerManager.class.getName());
    private int ServerDefaultNameIndex = 0;

    public Map<String, VSServerSlave> getServerSlaveMap() {
        return serverSlaveMap;
    }

    private Map<String, VSServerSlave> serverSlaveMap = new HashMap<>();
    private VSServerMaster master = VSServerMaster.getInstance();

    public List<Resource> getResourceList() {
        return resourceList;
    }

    private List<Resource> resourceList= new ArrayList<>();
    private VSServerManager(){
    }
    public static VSServerManager getInstance(){
        LOGGER.log(Level.INFO, "Server Manager started!");
        return instance;
    }
    public void addServerSlave(){
        ServerDefaultNameIndex++;
        String serverName = "ns"+ ServerDefaultNameIndex +".example.com";
        try {
            if (!serverSlaveMap.containsKey(serverName)) {
                VSServerSlave slave = new VSServerSlave(serverName);
                serverSlaveMap.put(slave.getServerName(), slave);

                try {
//                    RmiServerInterface stub = (RmiServerInterface) UnicastRemoteObject.exportObject(this, 0);

                    // Bind the remote object's stub in the registry
//                    Registry registry = LocateRegistry.getRegistry();
                    Naming.rebind("//localhost/" + serverName, slave);

                    System.err.println("Server " + serverName + " ready");
                } catch (Exception e) {
                    System.err.println("Server exception: " + e.toString());
                    e.printStackTrace();
                }


                new Thread(slave).start();
            }
        }catch (Exception e) {
            e.printStackTrace();
            LOGGER.log(Level.WARNING, "add server slave failed!");
        }
    }
    public void addServerSlave(String serverName) {
        try {
            if (!serverSlaveMap.containsKey(serverName)) {
                VSServerSlave slave = new VSServerSlave(serverName);
                serverSlaveMap.put(slave.getServerName(), slave);
                new Thread(slave).start();
            }}catch(Exception e){
                e.printStackTrace();
                LOGGER.log(Level.WARNING, "add server slave failed!");
            }
    }

    public void removeServerSlave(String name) throws InterruptedException {
        VSServerSlave slave = serverSlaveMap.get(name);
        if (slave!= null){
            slave.exit();
            serverSlaveMap.remove(name);
        }
    }

    /**
     *    Generate a resource id
     * @return resource id
     */
    private static int generateResourceID(){
        Random random = new Random();
        return random.hashCode();
    }

    /**
     *     Generate a resource randomly
     * @return resouce
     */
    public Resource generateResource(){
        String resourceid = Integer.toHexString(generateResourceID());
        Resource resource = new Resource(resourceid);
        return resource;
    }

    /**
     * Generate a resource with the parameter resourceid
     * @param resourceid
     */
    public Resource generateResource(String resourceid){
        Resource resource = new Resource(resourceid);
        return resource;
    }
    /**
     *
     */
    public void addResource(){
        Resource resource = generateResource();
        master.receiveResource(resource);
        resourceList.add(resource);
    }

    public void addResource(String resourceid){
        Resource resource = generateResource(resourceid);
        master.receiveResource(resource);
        resourceList.add(resource);
    }

    public void run(){
        try {
            instance.readResourceList("resource");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.log(Level.WARNING, "Reading resource list failed!");
        }
        while (true){
            Scanner s = new Scanner(System.in);
            String input = s.nextLine();
            String[] commands = input.split(" ");
            switch (commands[0]){
                case "add":
                    if (commands[1].equals("server")){
                        if(commands.length == 2) {
                            instance.addServerSlave();
                            LOGGER.log(Level.INFO, "Add a server slave!");
                        }
                        else if(commands.length == 3)instance.addServerSlave(commands[2]);
                        else {
                            LOGGER.log(Level.WARNING, "Add server slave failed!");
                            break;
                        }
                    }
                    else if (commands[1].equals("resource")){
                        if(commands.length == 2) instance.addResource();
                        else if(commands.length == 3){for (int i=0;i< Integer.parseInt(commands[2]);i++) instance.addResource(); }
                        else{
                            LOGGER.log(Level.WARNING, "Add resource failed!");
                            break;
                        }
                    }
                    break;
                case "remove":
                    if (commands[1].equals("server")){
                        try {
                            instance.removeServerSlave(commands[2]);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    else if (commands[1].equals("resource")){
                        //TODO resource removal
                    }
                    else System.err.println("Command not found!");
                    break;
                case "getall":
                    master.printResourceDistibution();
                    System.out.println(serverSlaveMap);
                    break;
                case "message":
                    getMessage(commands[1]);
                    break;
                default:
                    System.err.println("Command not found!");
                    break;
            }
        }
    }
    public void printAllResouce(){
        resourceList.forEach(resource -> System.out.println(resource.getId()));
    }

    /**
     * read 12 initial resources from file.
     * @param filename
     * @throws IOException
     */
    private void readResourceList(String filename) throws IOException {
        File file = new File(filename);
        BufferedReader reader  = new BufferedReader(new FileReader(file));
        String tempString;
        while((tempString =reader.readLine())!= null){
            addResource(tempString);
        }
        reader.close();
    }

    private void getMessage(String serverName){
        try{
            RmiServerInterface obj = (RmiServerInterface) Naming.lookup("//localhost/"+serverName);
//            System.out.println("Client Started");
            System.out.println("Get the Message: " + obj.getMessage());
        }catch (Exception e){
            LOGGER.log(Level.WARNING, "Naming.lookup failed!");
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        LOGGER.setLevel(Level.WARNING);
        VSServerMaster master = VSServerMaster.getInstance();
        VSServerManager instance = VSServerManager.getInstance();
        LocateRegistry.createRegistry(1099);
        LOGGER.log(Level.INFO, "Java RMI registry created.");
        Thread vsServerMaster =  new Thread(master);
        Thread vsServerManager = new Thread(instance);
        vsServerMaster.start();
        for(int i=0; i<3; i++) {instance.addServerSlave();}
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        vsServerManager.start();
    }


}
