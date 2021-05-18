import java.util.Hashtable;
import java.util.Set;

/**
 * Represents a DFID algorithm on a tile puzzle.
 */
public class DFID extends HelpFunctions {
    private int numNodes = 1;
    private int cost = 0;//total cost
    private Hashtable< String,NodeInfo> loopAvoidance;
    private boolean withOpen = false;
    private static boolean isCutoff = false;

    //Getter

    public int getNumNodes() {
        return numNodes;
    }

    private int rounds = 1;

    // public methods

    public NodeInfo DFID (TileObject [][] start, TileObject[][] goal,boolean withOpen) {
        if(matrixToString(start).equals(goal)) {//check if the start node is already the goal node
            return new NodeInfo(0,"no path",0,start);
        }
        int limit = 1;
        this.withOpen = withOpen;
        this.loopAvoidance = new Hashtable<String,NodeInfo>();
        NodeInfo startNode = new NodeInfo();
        startNode.copyNode(start);
        while(true)
        {
//            System.out.println(limit);
            NodeInfo result = new NodeInfo();
            result = Limited_DFS(startNode, goal, limit);
//            System.out.println(result.getPath());
            if (!result.getPath().contains("cutoff")) return result;
            limit++;
        }
    }


    // private methods

    private NodeInfo Limited_DFS(NodeInfo nodeInfo, TileObject[][] goal, int limit) {
        TileObject [][] node = nodeInfo.getNode();
        String nodeStr = matrixToString(node);
        String goalStr = matrixToString(goal);
        NodeInfo newNodeInfo = new NodeInfo();
        if(nodeStr.equals(goalStr)) return nodeInfo;
        if (limit == 0) {
            newNodeInfo.swap(new NodeInfo(0, "cutoff"));
            return newNodeInfo;
        }
        loopAvoidance.put(nodeStr,nodeInfo);
//        boolean isCutoff = false;
        isCutoff = false;
        int[][] blank = new int[2][2]; // represent the blank tiles
        blank = blank_(node);
        int blankNum = 0;
        // the rows and columns of the blank tiles
        int _row1 = blank[0][0];
        int _col1 = blank[0][1];
        int _row2 = blank[1][0];
        int _col2 = blank[1][1];
        // check if we have 1 or 2 blanks
        if(_row1 != -1 && _col1 != -1) blankNum = 1;
        if(_row2 != -1 && _col2 != -1) blankNum = 2;
        NodeInfo ans = new NodeInfo();
        if(blankNum == 2) // if there is 2 blanks
        {
            if((_row2 == _row1 && _col2 == _col1 + 1) || (_row2 == _row1 + 1 && _col2 == _col1)) // If they are next to each other
            {
                NodeInfo result = twoTiles(node, _row1, _row2, _col1, _col2, nodeInfo, goal, limit);
                if (!result.getPath().equals("")) {
                    return result;
                }
            }else{ // if there are two blank tiles but they are not next to each other
                NodeInfo result1 =  oneTile(node, _row1, _col1,nodeInfo, goal, limit);
                if (!result1.getPath().equals("")) {
                    return result1;
                }
                NodeInfo result2 = oneTile(node, _row2, _col2,nodeInfo, goal, limit);
                if (!result2.getPath().equals("")) {
                    return result2;
                }
            }
        }else if(blankNum == 1) // if there is 1 blank
        {
            NodeInfo result = oneTile(node, _row1, _col1,nodeInfo, goal, limit);
            if (!result.getPath().equals("")) {
                return result;
            }
        }

        if(this.withOpen) {// for the open list print
            System.out.println("round: "+rounds);//print the number of rounds when print the open list.
            rounds++;
            Set<String> keys= loopAvoidance.keySet();
            for(String key: keys){
                printNode(loopAvoidance.get(key).getNode());
                System.out.println("***");
            }
        }
        loopAvoidance.remove(nodeStr);
        if(isCutoff == true) {
            newNodeInfo.swap(new NodeInfo(0,"cutoff"));
            return newNodeInfo;
        }
        return new NodeInfo(0,"fail");
    }













    // ************* private methods ****************

    public NodeInfo twoTiles(TileObject[][] node, int _row1, int _row2, int _col1, int _col2, NodeInfo n, TileObject[][] goal, int limit){
        int rowLength = node.length;
        int colLength = node[0].length;
        TileObject[][] operator = new TileObject[rowLength][colLength];
        if(_col1 == _col2) // if the blank tiles are Vertical
        {
            // 2 cases for move 2 tiles left or right
            if(_col2 != colLength - 1) // If they are not in the last column
            {
                String father = node[_row1][_col1 + 1].getValue() + "&" + node[_row2][_col2 + 1].getValue() + "R"; // the opposite operation, check!
                if(!father.equals(n.getFather())) // if it is not the opposite operation of the father.
                {
                    // move left 2 tiles
                    operator = copy(node); // init operator with node
                    operator[_row1][_col1].swap(node[_row1][_col1 + 1]); // move the first tile to the first blank
                    operator[_row1][_col1 + 1].swap(node[_row1][_col1]); // move the blank
                    operator[_row2][_col2].swap(node[_row2][_col2 + 1]); // move the second tile to the second blank
                    operator[_row2][_col2 + 1].swap(node[_row2][_col2]); // move the blank
                    NodeInfo operatorInfo = new NodeInfo();
                    NodeInfo result = new NodeInfo();
                    operatorInfo = helpFunction2(node, operator ,node[_row1][_col1 + 1].getValue(), node[_row2][_col2 + 1].getValue(), "L", goal, 6);
                    if(operatorInfo.getNode() != null) result = Limited_DFS(operatorInfo, goal, limit - 1);
                    if (result.getPath().equals("cutoff")) isCutoff = true;
                    else if(!result.getPath().equals("fail")) return result;
                }
            }
            if(_col2 != 0) // if they are not in the first column
            {
                String father = node[_row1][_col1 - 1].getValue() + "&" + node[_row2][_col2 - 1].getValue() + "L"; // the opposite operation, check!
                if(!father.equals(n.getFather())) // if it is not the opposite operation of the father.
                {
                    // move right 2 tiles
                    operator = copy(node);
                    operator[_row1][_col1].swap(node[_row1][_col1 - 1]);
                    operator[_row1][_col1 - 1].swap(node[_row1][_col1]);
                    operator[_row2][_col2].swap(node[_row2][_col2 - 1]);
                    operator[_row2][_col2 - 1].swap(node[_row2][_col2]);
                    NodeInfo operatorInfo = new NodeInfo();
                    NodeInfo result = new NodeInfo();
                    operatorInfo = helpFunction2(node, operator, node[_row1][_col1 - 1].getValue(), node[_row2][_col2 - 1].getValue(), "R", goal, 6);
                    if(operatorInfo.getNode() != null) result = Limited_DFS(operatorInfo, goal, limit - 1);
                    if (result.getPath().equals("cutoff")) isCutoff = true;
                    else if(!result.getPath().equals("fail")) return result;
                }
            }
            NodeInfo result1 = oneTile(node, _row1, _col1, n, goal, limit);
            if(!result1.getPath().equals("fail") && !result1.getPath().equals("") && !result1.getPath().equals("cutoff")) return result1;
            NodeInfo result2 = oneTile(node, _row2, _col2, n, goal, limit);
            if(!result2.getPath().equals("fail") && !result2.getPath().equals("") && !result2.getPath().equals("cutoff")) return result2;
        } // end vertical

        if(_row1 == _row2) // if the blank tiles are horizontal
        {
            // 2 cases for move 2 tiles up or down
            if(_row1 != rowLength - 1) // If they are not in the first row
            {
                // move up 2 tiles
                String father1 = node[_row1 + 1][_col1].getValue() + "&" + node[_row2 + 1][_col2].getValue() + "D"; // the opposite operation, check!
                if(!father1.equals(n.getFather())) // if it is not the opposite operation of the father.
                {
                    operator = copy(node);
                    operator[_row1][_col1].swap(node[_row1 + 1][_col1]);
                    operator[_row1 + 1][_col1].swap(node[_row1][_col1]);
                    operator[_row2][_col2].swap(node[_row2 + 1][_col2]);
                    operator[_row2 + 1][_col2].swap(node[_row2][_col2]);
                    NodeInfo operatorInfo = new NodeInfo();
                    NodeInfo result = new NodeInfo();
                    operatorInfo = helpFunction2(node, operator, node[_row1 + 1][_col1].getValue(), node[_row2 + 1][_col2].getValue(), "U", goal, 7);
                    if(operatorInfo.getNode() != null) result = Limited_DFS(operatorInfo, goal, limit - 1);
                    if (result.getPath().equals("cutoff")) isCutoff = true;
                    else if(!result.getPath().equals("fail")) return result;
                }
            }
            if(_row2 != 0) // if they are not in the last row
            {
                // move down 2 tiles
                String father1 = node[_row1 - 1][_col1].getValue() + "&" + node[_row2 - 1][_col2].getValue() + "U"; // the opposite operation, check!
                if(!father1.equals(n.getFather())) // if it is not the opposite operation of the father.
                {
                    operator = copy(node);
                    operator[_row1][_col1].swap(node[_row1 - 1][_col1]);
                    operator[_row1 - 1][_col1].swap(node[_row1][_col1]);
                    operator[_row2][_col2].swap(node[_row2 - 1][_col2]);
                    operator[_row2 - 1][_col2].swap(node[_row2][_col2]);
                    NodeInfo operatorInfo = new NodeInfo();
                    NodeInfo result = new NodeInfo();
                    operatorInfo = helpFunction2(node, operator, node[_row1 - 1][_col1].getValue(), node[_row2 - 1][_col2].getValue(), "D", goal, 7);
                    if(operatorInfo.getNode() != null) result = Limited_DFS(operatorInfo, goal, limit - 1);
                    if (result.getPath().equals("cutoff")) isCutoff = true;
                    else if(!result.getPath().equals("fail")) return result;
                }
            }
            NodeInfo result1 = oneTile(node, _row1, _col1, n, goal, limit);
            if(!result1.getPath().equals("fail") && !result1.getPath().equals("") && !result1.getPath().equals("cutoff")) return result1;
            NodeInfo result2 = oneTile(node, _row2, _col2, n, goal, limit);
            if(!result2.getPath().equals("fail") && !result2.getPath().equals("") && !result2.getPath().equals("cutoff")) return result2;
        } // end horizontal
        return new NodeInfo();
    }


    public NodeInfo oneTile(TileObject[][] node, int _row1, int _col1, NodeInfo n, TileObject[][] goal, int limit) {
        int rowLength = node.length;
        int colLength = node[0].length;
        TileObject[][] operator = new TileObject[rowLength][colLength];
        // cases for move 1 tile
        if(_col1 != colLength - 1 && !node[_row1][_col1 + 1].getValue().equals("")) // If the blank is not in the last column, move left
        {
            String father = node[_row1][_col1 + 1].getValue() + "R"; // the opposite operation, check!
            if(!father.equals(n.getFather())) // if it is not the opposite operation of the father.
            {
                // move left
                operator = copy(node);
                operator[_row1][_col1].setValue(node[_row1][_col1 + 1].getValue());
                operator[_row1][_col1 + 1].setValue(node[_row1][_col1].getValue());
                NodeInfo operatorInfo = new NodeInfo();
                NodeInfo result = new NodeInfo();
                operatorInfo = helpFunction1(node, operator, node[_row1][_col1 + 1].getValue(), "L", goal, 5);
                if(operatorInfo.getNode() != null) result = Limited_DFS(operatorInfo, goal, limit - 1);
                if (result.getPath().equals("cutoff")) isCutoff = true;
                else if(!result.getPath().equals("fail")) return result;
            }
        }
        if(_row1 != rowLength - 1 && !node[_row1 + 1][_col1].getValue().equals("")) // if the blank is not in the last row, move up
        {
            String father = node[_row1 + 1][_col1].getValue() + "D"; // the opposite operation, check!
            if(!father.equals(n.getFather())) // if it is not the opposite operation of the father.
            {
                // move up
                operator = copy(node);
                operator[_row1][_col1].setValue(node[_row1 + 1][_col1].getValue());
                operator[_row1 + 1][_col1].setValue(node[_row1][_col1].getValue());
                NodeInfo operatorInfo = new NodeInfo();
                NodeInfo result = new NodeInfo();
                operatorInfo = helpFunction1(node, operator, node[_row1 + 1][_col1].getValue(), "U", goal, 5);
                if(operatorInfo.getNode() != null) result = Limited_DFS(operatorInfo, goal, limit - 1);
                if (result.getPath().equals("cutoff")) isCutoff = true;
                else if(!result.getPath().equals("fail")) return result;
            }
        }
        if(_col1 != 0 && !node[_row1][_col1 - 1].getValue().equals("")) // if the blank is not in the first column, move right
        {
            String father = node[_row1][_col1 - 1].getValue() + "L"; // the opposite operation, check!
            if(!father.equals(n.getFather())) // if it is not the opposite operation of the father.
            {
                // move right
                operator = copy(node);
                operator[_row1][_col1].setValue(node[_row1][_col1 - 1].getValue());
                operator[_row1][_col1 - 1].setValue(node[_row1][_col1].getValue());
                NodeInfo operatorInfo = new NodeInfo();
                NodeInfo result = new NodeInfo();
                operatorInfo = helpFunction1(node, operator, node[_row1][_col1 - 1].getValue(), "R", goal, 5);
                if(operatorInfo.getNode() != null) result = Limited_DFS(operatorInfo, goal, limit - 1);
                if (result.getPath().equals("cutoff")) isCutoff = true;
                else if(!result.getPath().equals("fail")) return result;
            }
        }
        if(_row1 != 0 && !node[_row1 - 1][_col1].getValue().equals("")) // if the blank is not in the first row, move down
        {
            String father = node[_row1 - 1][_col1].getValue() + "U"; // the opposite operation, check!
            if(!father.equals(n.getFather())) // if it is not the opposite operation of the father.
            {
                // move down
                operator = copy(node);
                operator[_row1][_col1].setValue(node[_row1 - 1][_col1].getValue());
                operator[_row1 - 1][_col1].setValue(node[_row1][_col1].getValue());
                NodeInfo operatorInfo = new NodeInfo();
                NodeInfo result = new NodeInfo();
                operatorInfo = helpFunction1(node, operator, node[_row1 - 1][_col1].getValue(), "D", goal, 5);
                if(operatorInfo.getNode() != null) result = Limited_DFS(operatorInfo, goal, limit - 1);
                if (result.getPath().equals("cutoff")) isCutoff = true;
                else if(!result.getPath().equals("fail")) return result;
            }
        }
        return new NodeInfo();
    }


    private NodeInfo helpFunction1(TileObject[][] node, TileObject[][] operator, String data, String direction, TileObject[][] goal, int price) {
        this.numNodes++;
        String operatorStr = matrixToString(operator);
        NodeInfo newNodeInfo = new NodeInfo(); // create new node info for the operator
        if(!loopAvoidance.containsKey(operatorStr)) // check if the matrix node is not in the path nodes list
        {
            newNodeInfo.setFather(data + direction); // set the father to bo like "6&7L"
            newNodeInfo.swap(loopAvoidance.get(matrixToString(node))); // get the path and cost from the father node
            newNodeInfo.copyNode(operator);
            String newPath = "-" + data + direction;
            newNodeInfo.addPath(newPath);
            newNodeInfo.addCost(price);
        }
        return  newNodeInfo;
    }


    private NodeInfo helpFunction2(TileObject[][] node, TileObject[][] operator, String data1, String data2, String direction, TileObject[][] goal, int price){
        this.numNodes++;
        String operatorStr = matrixToString(operator);
        NodeInfo newNodeInfo = new NodeInfo(); // create new node info for the operator
        if(!loopAvoidance.containsKey(operatorStr)) // check if the matrix node is not in the path nodes list
        {
            newNodeInfo.setFather(data1 + "&" + data2 + direction); // set the father to bo like "6&7L"
            newNodeInfo.swap(loopAvoidance.get(matrixToString(node))); // get the path and cost from the father node
            newNodeInfo.copyNode(operator);
            String newPath = "-" + data1 + "&" + data2 + direction;
            newNodeInfo.addPath(newPath);
            newNodeInfo.addCost(price);
        }
        return  newNodeInfo;
    }

}