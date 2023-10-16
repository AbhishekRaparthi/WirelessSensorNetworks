package WirelessSensorNetworks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class WirelessSensorNetworks {
    public static void main(String args[]){
        WirelessSensorNetworks wsn=new WirelessSensorNetworks();
        Scanner sc=new Scanner(System.in);

        List<Node> nodes=new ArrayList<>();
        List<Cluster> network=new ArrayList<>();

        boolean flag=true;
        while(flag){
            System.out.println("Menu \n 1.RandomMode \n 2.UserMode \n 3.Exit");
            int c=sc.nextInt();
            switch(c){
                case 1:
                    
                    nodes=wsn.createNodesRandomly();
                    network=wsn.clustering(nodes);
                    //printing to console
                    wsn.printNetwork(network);
                    //printing network to Network.txt
                    wsn.printToText(network, nodes);
                    //printing path from src to dst
                    
                    wsn.printPath(nodes);
                    break;
                case 2: 
                    //create method for userinput nodes
                    nodes=wsn.createNodesUserrInput();
                    network=wsn.clustering(nodes);
                    //printing to console
                    wsn.printNetwork(network);
                    //printing network to Network.txt
                    wsn.printToText(network, nodes);
                    //printing path from src to dst
                    
                    wsn.printPath(nodes);
                    break;
                case 3:
                    flag=false;
                    System.out.println("Succesfully Exited, Thank you");
                    break;
                default:
                    System.out.println("Please enter correct values");
                    break;
            }
        }

        sc.close();
    }
    public void printToText(List<Cluster> network,List<Node> nodes){
        // Replace with your desired file path
        String filePath = "C:\\Users\\Abhis\\OneDrive\\Desktop\\Network.txt"; 

        try {
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            writer.write(nodes.size()+"\n");
            for(Node n:nodes){
                writer.write(n.getX()+" "+n.getY()+" "+n.getRange()+" "+n.getEnergy()+" "+n.getPower()+"\n");
            }
            writer.newLine();

            writer.write("Cluster Details \n\n");
            for(Cluster c:network){
                writer.write("\n\n"+c.toString()+"\n\n");
                for(Node n:c.nodesInCluster){
                    writer.write(n.toString()+"\n");
                }
            }
            writer.newLine();
            writer.close();

            System.out.println("Data written to " + filePath +"\n ");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void printNetwork(List<Cluster> network){
        for(Cluster c:network){
            System.out.println(c.toString());
            for(Node n:c.nodesInCluster){
                System.out.println(n.toString());
            }
            System.out.println("\n");
        }
    }

    public void printPath(List<Node> nodes){
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter id of source node");
        int srcId=sc.nextInt();
        System.out.println("Enter id of destination node ");
        int dstId=sc.nextInt();

        WirelessSensorNetworks wsn=new WirelessSensorNetworks();
        Node src=wsn.getNodeWithId(srcId, nodes);
        Node dst=wsn.getNodeWithId(dstId, nodes);

        if(srcId==dstId || src==null || dst==null){
            System.out.println("The source id and destination id are wrong, kindly enter the right id");
            return;
        }

        System.out.print(src.getId()+"->");
        src.setVisited(true);
        while(src!=null){
            src=wsn.findNextNode(src, nodes);
            if(src==null){
                System.out.println(" No node in range \n");
                break;
            }
            else if(src==dst){
                System.out.print(src.getId()+". \n");
                break;
            }else{
                System.out.print(src.getId()+"->");
            }
        }
    }

    public Node getNodeWithId(int id,List<Node> nodes){
        for(Node x:nodes){
            if(x.getId()==id){
                return x;
            }
        }
        return null;
    }

    public Node findNextNode(Node n,List<Node> nodes){
        WirelessSensorNetworks wsn=new WirelessSensorNetworks();
        Node next=null;
        double currentDistance=Double.MAX_VALUE;

        for(Node x:nodes){
            if(x.getId()!=n.getId() && x.isVisited()==false){
                double dst=wsn.distanceBetweenNodes(n, x);
                if(dst<=n.getRange() && dst<currentDistance){
                        next=x;
                        currentDistance=dst;
                }  
            }
                   
        }
        if(next!=null){
        next.setVisited(true);
        }
        return next;
    }

    public double distanceBetweenNodes(Node n1,Node n2){
        double xDifference = n2.getX()-n1.getX();
        double yDifference = n2.getY() - n1.getY();

        double distance = Math.sqrt((xDifference * xDifference )+(yDifference * yDifference));

        return distance;
    }

    public List<Node> createNodesUserrInput(){
        List<Node> nodes=new ArrayList<>();
        // Replace with your desired file path
        String inputPath="C:\\Users\\Abhis\\OneDrive\\Desktop\\Input.txt";
        List<String> lines=new ArrayList<>();
        try {
			BufferedReader my_Reader = new BufferedReader(new FileReader(new File(inputPath)));
			String line = "";
			while((line = my_Reader.readLine()) != null)
			{
				lines.add(line);
			}
			my_Reader.close();
		} catch (Exception e) {
			System.out.println("File not exists or insufficient rights");
			e.printStackTrace();
		} 

        int numOfNodes=Integer.parseInt(lines.get(0)); 
        int z=1; 
        for(int i=0;i<numOfNodes;i++){
            String[] words=lines.get(z).split(" ");
            int x=Integer.parseInt(words[0]);
            int y=Integer.parseInt(words[1]);

            Node n=new Node(x, y);
            n.setRange(Integer.parseInt(words[2]));
            n.setEnergy(Integer.parseInt(words[3]));
            n.setPower(Integer.parseInt(words[4]));
            n.setId(i);
            double F=(0.4 * n.getRange())+(0.4 * n.getEnergy())+(0.2*n.getPower());
            n.setF(F);
            nodes.add(n);
            z+=1;
        }
        return nodes;
    }

    public List<Node> createNodesRandomly() {
        List<Node> nodes=new ArrayList<>();
        Random random = new Random();
        int noOfnodes = random.nextInt(100-10+1)+10;
        System.out.println("NumberofNodes : "+noOfnodes);
        for(int i=0;i<noOfnodes;i++){
            int x=random.nextInt(20-1+1)+1;
            int y=random.nextInt(20-1+1)+1;
            Node n=new Node(x, y);
            int range=random.nextInt(8-1+1)+1;
            int energy=random.nextInt(100-1+1)+1;
            int power=random.nextInt(100-1+1)+1;
            n.setRange(range);
            n.setEnergy(energy);
            n.setPower(power);
            n.setId(i);
            double F=(0.4 * n.getRange())+(0.4 * n.getEnergy())+(0.2*n.getPower());
            n.setF(F);
            nodes.add(n);
        }
        return nodes;
    }

    public List<Cluster> clustering(List<Node> nodes){
        List<Cluster> network=new ArrayList<>();
        //create clusters
        int i,j;
        int id=1;
        for(i=0;i<20;i+=5){
            int ui=i+5;
            for(j=0;j<20;j+=5){
                int uj=j+5;
                Cluster c= new Cluster(id, i, j, ui, uj);
                network.add(c);
                id+=1;
            }
        }
        //assign nodes to clusters
        for(Node n : nodes){
            if(n.isClustered()==false){
                for(Cluster c:network){
                    if(n.getX()>=c.lowerX && n.getX()<=c.upperX && n.getY()>=c.lowerY && n.getY()<=c.upperY){
                        c.nodesInCluster.add(n);
                        n.setClustered(true);
                        break;
                    }
                }
            }
        }
        //find clusterheads
        for(Cluster c:network){
    
            //assigning the max F as clusterhead
            Node ch=new Node(-1, -1);
            for(Node n:c.nodesInCluster){
                if(n.getF()>ch.getF()){
                    ch=n;
                }
            }
            c.setClusterhead(ch);
        }
        return network;
    }
}

class Cluster{
    int lowerX,lowerY;
    int upperX,upperY;
    Node clusterhead;
    ArrayList<Node> nodesInCluster;
    int clusterId;
    public Cluster(int clusterId,int lowerX, int lowerY, int upperX, int upperY) {
        this.clusterId=clusterId;
        this.lowerX = lowerX;
        this.lowerY = lowerY;
        this.upperX = upperX;
        this.upperY = upperY;
        nodesInCluster=new ArrayList<>();
    }
    public Node getClusterhead() {
        return clusterhead;
    }
    public void setClusterhead(Node clusterhead) {
        this.clusterhead = clusterhead;
    }
    public ArrayList<Node> getNodesInCluster() {
        return nodesInCluster;
    }
    public void setNodesInCluster(ArrayList<Node> nodesInCluster) {
        this.nodesInCluster = nodesInCluster;
    }
    @Override
    public String toString() {
        return "Cluster [lowerX=" + lowerX + ", lowerY=" + lowerY + ", upperX=" + upperX + ", upperY=" + upperY
                + ", clusterhead=" + clusterhead + ", clusterId=" + clusterId + "]";
    }
    


}

class Node{
    private int x,y;
    private int range;
    private int energy;
    private int power;
    private int id;
    private boolean isClustered;
    private double F;
    private boolean visited;

    public Node(int x,int y){
        this.x=x;
        this.y=y;
        isClustered=false;
        visited=false;
    }
    
    
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isClustered() {
        return isClustered;
    }

    public void setClustered(boolean isClustered) {
        this.isClustered = isClustered;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }


    public double getF() {
        return F;
    }


    public void setF(double f) {
        F = f;
    }


    public boolean isVisited() {
        return visited;
    }


    public void setVisited(boolean visited) {
        this.visited = visited;
    }


    @Override
    public String toString() {
        return "Node [x=" + x + ", y=" + y + ", range=" + range + ", energy=" + energy + ", power=" + power + ", id="
                + id +"]";
    }

    
    
    
}
