import java.io.*;
import java.util.ArrayList;

public class Ex1 {

    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub

        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        String algorithm = "";
        Boolean time = false;
        Boolean openList = false;
        ArrayList<String> fileLines = new ArrayList<>();

        //open the input file and insert to the fileLines array
        File file = new File("input.txt");
        BufferedReader bf = new BufferedReader(new FileReader(file));
        String str;
        while((str = bf.readLine()) != null)
        {
            fileLines.add(str);
        }

        //read from fileLines the inputs
        algorithm = fileLines.get(0);
        if(fileLines.get(1).contains("with")) time = true;
        if(fileLines.get(2).contains("with")) openList = true;
        int rowLength = Integer.parseInt(fileLines.get(3).split("x")[0]);
        int colLength = Integer.parseInt(fileLines.get(3).split("x")[1]);
//        TileObject[] arrPuzzle=new TileObject[rowLength * colLength]; // array that represent all the tiles. array[0] will represent the tile "1". array[1]- tile"2" and so on.
        TileObject[][] startMat = new TileObject[rowLength][colLength];
        TileObject[][] goalMat = new TileObject[rowLength][colLength];
//        String goal;
        int index = 4;
        for(int i = 0; i < rowLength; i++) // insert the start matrix input to startMat
        {
            String [] tiles = fileLines.get(index).split(",");
            index++;
            for (int j = 0; j < colLength; j++)
            {
                TileObject to = new TileObject(tiles[j]);
                startMat[i][j] = to;
            }
        }
        index++;
        for(int i = 0; i < rowLength; i++) // insert the goal matrix input to goalMat
        {
            String [] tiles = fileLines.get(index).split(",");
            index++;
            for (int j = 0; j < colLength; j++)
            {
                TileObject to = new TileObject(tiles[j]);
                goalMat[i][j] = to;
            }
        }

        //create output file
        try{
            File output = new File("output.txt");
            output.createNewFile();
        }catch (IOException e){
            System.out.println("Error");
            e.printStackTrace();
        }
        FileWriter fw = new FileWriter("output.txt");
        PrintWriter pw = new PrintWriter(fw);


        //the outputs by every algorithm
        if(algorithm.equals("BFS")) // if the request algorithm is BFS
        {
            startTime = System.currentTimeMillis();
            BFS bfs = new BFS();
            endTime = System.currentTimeMillis();
            NodeInfo BFSans = new NodeInfo();
            BFSans.swap(bfs.BFS(startMat, goalMat, openList));
            if(BFSans.getPath().equals("no path"))
            {
                pw.println(BFSans.getPath());
                pw.println("Num: " + bfs.getNumNodes());
            }else{ // if the algorithm found path
                pw.println(BFSans.getPath().substring(1));
                pw.println("Num: " + bfs.getNumNodes());
                pw.println("Cost: " + bfs.getCost());
//                if(time)
//                {
//                    fw.write(BFSans.getTime() + " seconds");
//                }
            }
        }
        else if(algorithm.equals("DFID")) {//if the requested algorithm is DFID
            startTime=System.currentTimeMillis();// start time
            DFID dfid= new DFID();
            endTime=System.currentTimeMillis();// end time
            NodeInfo dfifAns=new NodeInfo();
            dfifAns.swap(dfid.DFID(startMat, goalMat,openList));
            if(dfifAns.getPath().equals("no path")) {//If there is no path
                pw.println(dfifAns.getPath());
                pw.println("Num: "+ dfid.getNumNodes());
            }
            else {//if the algorithm finds a path
                pw.println(dfifAns.getPath().substring(1));
                pw.println("Num: "+ dfid.getNumNodes());
                pw.println("Cost: "+dfifAns.getCost());
            }
        }
        else if(algorithm.equals("A*")) {//if the requested algorithm is A*
            startTime = System.currentTimeMillis();// start time
            Astar aStar = new Astar();
            endTime = System.currentTimeMillis();// end time
            NodeInfo astarAns = new NodeInfo();
            astarAns.swap(aStar.Astar(startMat, goalMat ,openList));
            if(astarAns.getPath().equals("no path")) {//If there is no path
                pw.println(astarAns.getPath());
                pw.println("Num: "+ aStar.numNodes);
            }
            else {//if the algorithm finds a path
                pw.println(astarAns.getPath().substring(1));
                pw.println("Num: "+ aStar.numNodes);
                pw.println("Cost: "+astarAns.getCost());
            }
        }
        else if(algorithm.equals("IDA*")) {//if the requested algorithm is IDA*
            startTime=System.currentTimeMillis();// start time
            IDAstar idaStar= new IDAstar();
            endTime=System.currentTimeMillis();// end time
            NodeInfo IDAans = new NodeInfo();
            IDAans.swap(idaStar.IDAstar(startMat, goalMat, openList));
            if(IDAans.getPath().equals("no path")) { // If there is no path
                pw.println(IDAans.getPath());
                pw.println("Num: "+ idaStar.numNodes);
            }
            else { // if the algorithm finds a path
                pw.println(IDAans.getPath().substring(1));
                pw.println("Num: "+ idaStar.numNodes);
                pw.println("Cost: "+IDAans.getCost());
            }
        }
        else if(algorithm.equals("DFBnB")) { // if the requested algorithm is DFBnB
            startTime = System.currentTimeMillis(); // start time
            DFBnB dfbnb = new DFBnB();
            endTime = System.currentTimeMillis(); // end time
            NodeInfo DFBnBans = new NodeInfo();
            DFBnBans.swap(dfbnb.DFBnB(startMat, goalMat, openList));
            if(DFBnBans.getPath().equals("no path")) { // If there is no path
                pw.println(DFBnBans.getPath());
                pw.println("Num: "+ dfbnb.numNodes);

            }
            else { // if the algorithm finds a path
                pw.println(DFBnBans.getPath().substring(1));
                pw.println("Num: "+ dfbnb.numNodes);
                pw.println("Cost: "+DFBnBans.getCost());
            }
        }



        if(time)
        {
            float realTime = (endTime - startTime) / 1000F;
            fw.write(realTime + " seconds");
        }
        fw.close();
    }
}
