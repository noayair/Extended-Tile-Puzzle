import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents a BFS algorithm on a tile puzzle.
 *
 */

public class BFS extends HelpFunctions{
    private int numNodes;
    private int cost;
    private Hashtable<String, NodeInfo> frontier_HT;
    private Hashtable<String, NodeInfo> exploreSet;
    private Queue<TileObject[][]> frontier_Q;
    private long startTime, endTime;

    //constructors

    BFS(){
        this.numNodes = 1;
        this.cost = 0;
        this.frontier_HT = new Hashtable<String, NodeInfo>();
        this.exploreSet = new Hashtable<String, NodeInfo>();
        this.frontier_Q = new LinkedList<>();
    }

    //Getter
    public int getNumNodes() {
        return numNodes;
    }

    public int getCost() {
        return cost;
    }


    //public methods

    public NodeInfo BFS(TileObject[][] start, TileObject[][] goal, Boolean Open){
//        startTime = System.nanoTime();
        String startStr = matrixToString(start);
        if(matrixToString(start).equals(matrixToString(goal))) // if the start is already the goal
        {
            return new NodeInfo(start, "", 0, 0);
        }
        frontier_Q.add(start); // reset the queue with start node
        NodeInfo n = new NodeInfo();
        frontier_HT.put(startStr, n); // add the start node to the frontier hash
        int roundsLoop = 1; // for the open list
        while (!frontier_HT.isEmpty()) // while the hash is not empty
        {
            if(Open) // if we need to print the open list
            {
                System.out.println("round:" + roundsLoop);
                roundsLoop++;
                for(TileObject[][] nodes : frontier_Q)
                {
                    printNode(nodes);
                    System.out.println("*****");
                }
            }
            TileObject[][] node = frontier_Q.remove(); // remove the first node from the queue
            String nodeStr = matrixToString(node);
            NodeInfo nodeInfo = new NodeInfo();
            nodeInfo = frontier_HT.get(nodeStr);
            exploreSet.put(nodeStr, nodeInfo); // add the node we remove from the queue to the explore set hash
            frontier_HT.remove(nodeStr); // remove node from the frontier
            int[][] blank = new int[2][2]; // represent the blank tiles
            blank = blank_(node);
            int blankNum = 1;
            // the rows and columns of the blank tiles
            int _row1 = blank[0][0];
            int _col1 = blank[0][1];
            int _row2 = blank[1][0];
            int _col2 = blank[1][1];
            // check if we have 1 or 2 blanks
            if(_row2 != -1 && _col2 != -1) blankNum = 2;
            if(blankNum == 2) // if there is 2 blanks
            {
                if((_row2 == _row1 && _col2 == _col1 + 1) || (_row2 == _row1 + 1 && _col2 == _col1)) // If they are next to each other
                {
                    NodeInfo result = twoTiles(node, _row1, _row2, _col1, _col2, nodeInfo, goal);
                    if (!result.getPath().equals("")) {
                        return result;
                    }
                }else{ // if there are two blank tiles but they are not next to each other
                    oneTile(node, _row1, _col1,nodeInfo, goal);
                    oneTile(node, _row2, _col2,nodeInfo, goal);
                }
            }else if(blankNum == 1) // if there is 1 blank
            {
                NodeInfo result = oneTile(node, _row1, _col1,nodeInfo, goal);
                if (!result.getPath().equals("")) {
                    return result;
                }
            }
        }
        return new NodeInfo(0,"no path");
    }

// private methods

    /**
     * The function handles in case there are two blank tiles next to each other.
     */
    private NodeInfo twoTiles(TileObject[][] node, int _row1, int _row2, int _col1, int _col2, NodeInfo n, TileObject[][] goal){
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
                    NodeInfo result = new NodeInfo();
                    result = helpFunction2(node, operator ,node[_row1][_col1 + 1].getValue(), node[_row2][_col2 + 1].getValue(), "L", goal, 6);
                    if (!result.getPath().equals("")) {
                        return result;
                    }
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
                    NodeInfo result = new NodeInfo();
                    result = helpFunction2(node, operator, node[_row1][_col1 - 1].getValue(), node[_row2][_col2 - 1].getValue(), "R", goal, 6);
                    if (!result.getPath().equals("")) {
                        return result;
                    }
                }
            }
            oneTile(node, _row1, _col1, n, goal);
            oneTile(node, _row2, _col2, n, goal);
        } // end vertical

        if(_row1 == _row2) // if the blank tiles are horizontal
        {
            // 2 cases for move 2 tiles up or down
            if(_row1 != rowLength - 1) // If they are not in the first row
            {
                // move up 2 tiles
                String father = node[_row1 + 1][_col1].getValue() + "&" + node[_row2 + 1][_col2].getValue() + "D"; // the opposite operation, check!
                if(!father.equals(n.getFather())) // if it is not the opposite operation of the father.
                {
                    operator = copy(node);
                    operator[_row1][_col1].swap(node[_row1 + 1][_col1]);
                    operator[_row1 + 1][_col1].swap(node[_row1][_col1]);
                    operator[_row2][_col2].swap(node[_row2 + 1][_col2]);
                    operator[_row2 + 1][_col2].swap(node[_row2][_col2]);
                    NodeInfo result = new NodeInfo();
                    result = helpFunction2(node, operator, node[_row1 + 1][_col1].getValue(), node[_row2 + 1][_col2].getValue(), "U", goal, 7);
                    if (!result.getPath().equals("")) {
                        return result;
                    }
                }
            }
            if(_row2 != 0) // if they are not in the last row
            {
                // move down 2 tiles
                String father = node[_row1 - 1][_col1].getValue() + "&" + node[_row2 - 1][_col2].getValue() + "U"; // the opposite operation, check!
                if(!father.equals(n.getFather())) // if it is not the opposite operation of the father.
                {
                    operator = copy(node);
                    operator[_row1][_col1].swap(node[_row1 - 1][_col1]);
                    operator[_row1 - 1][_col1].swap(node[_row1][_col1]);
                    operator[_row2][_col2].swap(node[_row2 - 1][_col2]);
                    operator[_row2 - 1][_col2].swap(node[_row2][_col2]);
                    NodeInfo result = new NodeInfo();
                    result = helpFunction2(node, operator, node[_row1 - 1][_col1].getValue(), node[_row2 - 1][_col2].getValue(), "D", goal, 7);
                    if (!result.getPath().equals("")) {
                        return result;
                    }
                }
            }
            oneTile(node, _row1, _col1, n, goal);
            oneTile(node, _row2, _col2, n, goal);
        } // end horizontal
        return new NodeInfo();
    }

    /**
     * The function handles in case there is only one blank tile.
     * @param node
     * @param _row1
     * @param _col1
     */
    private NodeInfo oneTile(TileObject[][] node, int _row1, int _col1, NodeInfo n, TileObject[][] goal) {
        int rowLength = node.length;
        int colLength = node[0].length;
        TileObject[][] operator = new TileObject[rowLength][colLength];
        // cases for move 1 tile
        if(_col1 != colLength - 1 && !node[_row1][_col1 + 1].getValue().equals("_")) // If the blank is not in the last column, move left
        {
            String father = node[_row1][_col1 + 1].getValue() + "R"; // the opposite operation, check!
            if(!father.equals(n.getFather())) // if it is not the opposite operation of the father.
            {
                // move left
                operator = copy(node);
                operator[_row1][_col1].setValue(node[_row1][_col1 + 1].getValue());
                operator[_row1][_col1 + 1].setValue(node[_row1][_col1].getValue());
                NodeInfo result = new NodeInfo();
                result = helpFunction1(node, operator, node[_row1][_col1 + 1].getValue(), "L", goal, 5);
                if (!result.getPath().equals("")) {
                    return result;
                }
            }
        }
        if(_row1 != rowLength - 1 && !node[_row1 + 1][_col1].getValue().equals("_")) // if the blank is not in the last row, move up
        {
            String father = node[_row1 + 1][_col1].getValue() + "D"; // the opposite operation, check!
            if(!father.equals(n.getFather())) // if it is not the opposite operation of the father.
            {
                // move up
                operator = copy(node);
                operator[_row1][_col1].setValue(node[_row1 + 1][_col1].getValue());
                operator[_row1 + 1][_col1].setValue(node[_row1][_col1].getValue());
                NodeInfo result = new NodeInfo();
                result = helpFunction1(node, operator, node[_row1 + 1][_col1].getValue(), "U", goal, 5);
                if (!result.getPath().equals("")) {
                    return result;
                }
            }
        }
        if(_col1 != 0 && !node[_row1][_col1 - 1].getValue().equals("_")) // if the blank is not in the first column, move right
        {
            String father = node[_row1][_col1 - 1].getValue() + "L"; // the opposite operation, check!
            if(!father.equals(n.getFather())) // if it is not the opposite operation of the father.
            {
                // move right
                operator = copy(node);
                operator[_row1][_col1].setValue(node[_row1][_col1 - 1].getValue());
                operator[_row1][_col1 - 1].setValue(node[_row1][_col1].getValue());
                NodeInfo result = new NodeInfo();
                result = helpFunction1(node, operator, node[_row1][_col1 - 1].getValue(), "R", goal, 5);
                if (!result.getPath().equals("")) {
                    return result;
                }
            }
        }
        if(_row1 != 0 && !node[_row1 - 1][_col1].getValue().equals("_")) // if the blank is not in the first row, move down
        {
            String father = node[_row1 - 1][_col1].getValue() + "U"; // the opposite operation, check!
            if(!father.equals(n.getFather())) // if it is not the opposite operation of the father.
            {
                // move down
                operator = copy(node);
                operator[_row1][_col1].setValue(node[_row1 - 1][_col1].getValue());
                operator[_row1 - 1][_col1].setValue(node[_row1][_col1].getValue());
                NodeInfo result = new NodeInfo();
                result = helpFunction1(node, operator, node[_row1 - 1][_col1].getValue(), "D", goal, 5);
                if (!result.getPath().equals("")) {
                    return result;
                }
            }
        }
        return new NodeInfo();
    }

    private NodeInfo helpFunction1(TileObject[][] node, TileObject[][] operator, String data, String direction, TileObject[][] goal, int price) {
        this.numNodes++;
        String operatorStr = matrixToString(operator);
        String goalStr = matrixToString(goal);
        if(!frontier_HT.containsKey(operatorStr) && !exploreSet.containsKey(operatorStr)) //check if the operator is not on the open list and not in the close list
        {
            NodeInfo newNodeInfo = new NodeInfo(); // create new node info for the operator
            newNodeInfo.setFather(data + direction); // set the father to bo like "6&7L"
            newNodeInfo.swap(exploreSet.get(matrixToString(node))); // set the new node to be the node that his key in explore set is the old node
            String newPath = "-" + data + direction;
            newNodeInfo.addPath(newPath);
            newNodeInfo.addCost(price);
            if(operatorStr.equals(goalStr))
            {
                this.cost = newNodeInfo.getCost();
                return newNodeInfo;
            }
            frontier_HT.put(operatorStr, newNodeInfo);
            frontier_Q.add(operator);
        }
        return  new NodeInfo();
    }


    private NodeInfo helpFunction2(TileObject[][] node, TileObject[][] operator, String data1, String data2, String direction, TileObject[][] goal, int price){
        this.numNodes++;
        String operatorStr = matrixToString(operator);
        String goalStr = matrixToString(goal);
        if(!frontier_HT.containsKey(operatorStr) && !exploreSet.containsKey(operatorStr)) //check if the operator is not on the open list and not in the close list
        {
            NodeInfo newNodeInfo = new NodeInfo(); // create new node info for the operator
            newNodeInfo.setFather(data1 + "&" + data2 + direction); // set the father to bo like "6&7L"
            newNodeInfo.swap(exploreSet.get(matrixToString(node))); // set the new node to be the node that his key in explore set is the old node
            String newPath = "-" + data1 + "&" + data2 + direction;
            newNodeInfo.addPath(newPath);
            newNodeInfo.addCost(price);
            if(operatorStr.equals(goalStr))
            {
                this.cost = newNodeInfo.getCost();
                return newNodeInfo;
            }
            frontier_HT.put(operatorStr, newNodeInfo);
            frontier_Q.add(operator);
        }
        return  new NodeInfo();
    }
}
