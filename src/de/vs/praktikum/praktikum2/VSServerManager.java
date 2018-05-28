package de.vs.praktikum.praktikum2;

import java.io.*;
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
    public void addServerSlave(){
        ServerDefaultNameIndex++;
        String serverName = "ns"+ ServerDefaultNameIndex +".example.com";
        try {
            if (!serverMap.containsKey(serverName)) {
                VSServerSlave slave = new VSServerSlave(serverName);
                serverMap.put(slave.getServerName(), slave);
                slave.start();
            }
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("add server slave failed !");

        }
    }
    public void addServerSlave(String serverName) {
        try {
            if (!serverMap.containsKey(serverName)) {
                VSServerSlave slave = new VSServerSlave(serverName);
                serverMap.put(slave.getServerName(), slave);
                slave.start();
            }}catch(Exception e){
                e.printStackTrace();
                System.out.println("add server slave failed !");
            }
    }

    public void removeServerSlave(String name) throws InterruptedException {
        VSServerSlave slave = serverMap.get(name);

        if (slave!= null){
            System.out.println("sds");
            slave.exit();
            serverMap.remove(name);
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
        try {
            instance.readResourceList("resource");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("read Resourcelist failed");
        }
        System.out.println(serverMap);
        while (true){
            Scanner s = new Scanner(System.in);
            String input = s.nextLine();
            String[] commands = input.split(" ");
            switch (commands[0]){
                case "add":
                    if (commands[1].equals("server")){
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
                    else if (commands[1].equals("resource")){
                        if(commands.length == 2) instance.addResource();
                        //else if(commands.length == 3)instance.addResource(commands[2]);
                        else if(commands.length == 3){for (int i=0;i< Integer.parseInt(commands[2]);i++)instance.addResource(); }
                        else{
                            System.out.println("Add Resource failed");
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
                    else System.out.println("Command not found!");
                    break;
                case "getall":
                    printAllResouce();
                    master.printAvailableServerlist();
                    master.printResourceDistibution();
                    break;
                case "gettable":
                    System.out.println(RendezvousHash.getInstance().getScoreTable());
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
        for(int i=0; i<3; i++) {instance.addServerSlave();}

        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        instance.start();
        //Have 3 Server at the beginning

    }


}
