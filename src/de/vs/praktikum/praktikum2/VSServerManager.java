package de.vs.praktikum.praktikum2;

import java.util.*;

/**
 * Created by Yang Mao on 5/25/18.
 * email: yang.mao@stud.hs-emden-leer.de
 */
public class VSServerManager extends Thread{
    private static VSServerManager instance = new VSServerManager();
    private int nameCounter = 0;
    private Map<String, VSServerSlave> serverMap = new HashMap<>();
    private VSServerMaster master = VSServerMaster.getInstance();

    public List<Resource> getResourceList() {
        return resourceList;
    }

    private List<Resource> resourceList= new ArrayList<>();
    private VSServerManager(){
    }
    public static VSServerManager getInstance(){
        return instance;
    }
    public boolean addServerSlave(){
        nameCounter++;
        String serverName = "ns"+ nameCounter +".example.com";
        if (!serverMap.containsKey(serverName)){
            VSServerSlave slave = new VSServerSlave(serverName);
            serverMap.put(slave.getServerName(), slave);
            slave.run();
            return true;
        }
        return false;
    }
    public boolean addServerSlave(String serverName){
        if (!serverMap.containsKey(serverName)){
            VSServerSlave slave = new VSServerSlave(serverName);
            serverMap.put(slave.getServerName(), slave);
            slave.run();
            return true;
        }
        return false;
    }
    public void removeServerSlave(String name){
        VSServerSlave slave = serverMap.get(name);
        if (slave != null){
            slave.exit();
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
     *
     */
    public void addResource(){
        Resource resource = generateResource();
        master.receiveResource(resource);
        resourceList.add(resource);
    }

    public void run(){
        while (true){
            Scanner s = new Scanner(System.in);
            String input = s.nextLine();
            System.out.println(input);
            String[] commands = input.toUpperCase().split(" ");
            for(String item:commands){
                System.out.print(item);
            }

            switch (commands[0]){
                case "ADD":
                    if (commands[1].equals("SERVER")){
                        instance.addServerSlave();
                    }
                    else if (commands[1].equals("RESOURCE")){
                        instance.addResource();
                    }
                    break;
                case "REMOVE":
                    if (commands[1].equals("SERVER")){
                        if (commands[2].isEmpty() || commands[2].length() == 0 || !serverMap.containsKey(commands[2])){
                            System.out.println("No Server Name!");
                            //TODO random removal
                            break;
                        }
                        instance.removeServerSlave(commands[2]);
                    }
                    else if (commands[1].equals("RESOURCE")){
                        //TODO resource removal
                    }
                    break;
                case "GETALL":
                    printAllResouce();
                    master.printAvailableServerlist();
                    master.printResourceDistibution();
                    break;
                default:
                    System.out.println("Command not found!");
                    break;
            }
        }
    }
    public void printAllResouce(){
        resourceList.forEach(System.out::println);
    }

    public static void main(String[] args){
        VSServerMaster master = VSServerMaster.getInstance();
        VSServerManager instance = VSServerManager.getInstance();
        master.start();
        instance.start();
        //Have 3 Server at the beginning
        for(int i=0; i<3; i++){
            instance.addServerSlave();
        }
        //Have 10 different files at the beginning
        for(int i=0; i<1; i++){
            instance.addResource();
        }
    }
}
