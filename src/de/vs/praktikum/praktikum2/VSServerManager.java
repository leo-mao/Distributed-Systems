package de.vs.praktikum.praktikum2;

import java.io.*;
import java.nio.Buffer;
import java.util.*;

/**
 * Created by Yang Mao on 5/25/18.
 * email: yang.mao@stud.hs-emden-leer.de
 */
public class VSServerManager extends Thread{
    private static VSServerManager instance = new VSServerManager();
    private int ServerDefaultNameIndex = 0;
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
        ServerDefaultNameIndex++;
        String serverName = "ns"+ ServerDefaultNameIndex +".example.com";
        if (!serverMap.containsKey(serverName)){
            VSServerSlave slave = new VSServerSlave(serverName);
            serverMap.put(slave.getServerName(), slave);
            slave.start();
            return true;
        }
        return false;
    }
    public boolean addServerSlave(String serverName){
        if (!serverMap.containsKey(serverName)){
            VSServerSlave slave = new VSServerSlave(serverName);
            serverMap.put(slave.getServerName(), slave);
            slave.start();
            return true;
        }
        return false;
    }
    public void removeServerSlave(String name){
        VSServerSlave slave = serverMap.get(name);
        if (slave != null){
            slave.exit();
            //TODO  receive message to master
//            serverMap.remove(name);
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
     * @return
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
        while (true){
            Scanner s = new Scanner(System.in);
            String input = s.nextLine();
            String[] commands = input.toUpperCase().split(" ");
            switch (commands[0]){
                case "ADD":
                    if (commands[1].equals("SERVER")){
                        if(commands.length == 2) {
                            instance.addServerSlave();
                            System.out.println("Add a server slave");
                        }
                        else if(commands.length == 3)instance.addServerSlave(commands[2]);
                        else {
                            System.out.println("Add Server failed");
                            break;
                        }
                    }
                    else if (commands[1].equals("RESOURCE")){
                        if(commands.length == 2) instance.addResource();
                        else if(commands.length == 3)instance.addResource(commands[2]);
                        else{
                            System.out.println("Add Resource failed");
                            break;
                        }

                    }
                    break;
                case "REMOVE":
                    if (commands[1].equals("SERVER")){
                        if (commands[2].isEmpty() || commands[2].length() == 0 || !serverMap.containsKey(commands[2])){
                            System.out.println("No Server Name!");
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

    public static void main(String[] args) throws IOException {
        VSServerMaster master = VSServerMaster.getInstance();
        VSServerManager instance = VSServerManager.getInstance();
        master.start();
        instance.start();
        //Have 3 Server at the beginning
        for(int i=0; i<3; i++){
            if (instance.addServerSlave()) {
                System.out.println("Add a server slave");
            }
            else System.out.println("Add a server failed");
        }
        //Have 10 different files at the beginning
//        for(int i=0; i<10; i++){
//            instance.addResource();
//        }
        instance.readResourceList("resource");
        System.out.println("the initial resourceids are:" + instance.resourceList.size());
    }


}
