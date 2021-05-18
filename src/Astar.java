import java.util.Hashtable;
import java.util.PriorityQueue;

/**
 * Represents a A* algorithm on a tile puzzle.
 */
public class Astar extends HelpFunctions{
    int numNodes;
    int cost; // total cost
    Hashtable<String, NodeInfo> frontier_HT;
    Hashtable<String, NodeInfo> exploreSet;
    PriorityQueue<NodeInfo> frontier_Q;

    // constructor

    Astar(){
        numNodes = 1;
        cost = 0;
        frontier_HT = new Hashtable<String,NodeInfo>();
        exploreSet = new Hashtable<String,NodeInfo>();
        frontier_Q = new PriorityQueue<>();
    }

    // public methods

    public NodeInfo Astar (TileObject [][] start, TileObject[][] goal,Boolean withOpen){
        String startNodeStr = matrixToString(start);
        String goalStr = matrixToString(goal);
        if(startNodeStr.equals(goalStr)) {//check if the start node is already the goal node
            return new NodeInfo(0,"no path",0,start);
        }
        double heuristicStartValue = heuristicFunction(start);
        NodeInfo StartNode = new NodeInfo(0, "", heuristicStartValue, start);
        frontier_Q.add(StartNode);
        frontier_HT.put(startNodeStr, StartNode);
        int rounds = 1; // for the open list print mode
        while(!frontier_HT.isEmpty())
        {
            if(withOpen) // for the open list print
            {
                System.out.println("round: " + rounds); // print the number of rounds when print the open list.
                rounds++;
                for(NodeInfo nodeinf : frontier_Q)
                {
                    printNode(nodeinf.getNode());
                    System.out.println("*******");
                }
            }
            NodeInfo n = frontier_Q.remove();
            TileObject [][] node = n.getNode();
            String nodeStr = matrixToString(node);
            if(nodeStr.equals(goalStr)) return n;
            NodeInfo nodeInfo = new NodeInfo();
            nodeInfo = frontier_HT.get(nodeStr);
            exploreSet.put(nodeStr, nodeInfo); // get in the close list
            frontier_HT.remove(nodeStr); // remove node from the open list
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
                    twoTiles(node, _row1, _row2, _col1, _col2, nodeInfo, goal);
                }else{ // if there are two blank tiles but they are not next to each other
                    oneTile(node, _row1, _col1,nodeInfo, goal);
                    oneTile(node, _row2, _col2,nodeInfo, goal);
                }
            }else if(blankNum == 1) // if there is 1 blank
            {
                oneTile(node, _row1, _col1,nodeInfo, goal);
            }
        }
        return new NodeInfo(0,"no path");
        }


// private methods

    private void twoTiles(TileObject[][] node, int _row1, int _row2, int _col1, int _col2, NodeInfo n, TileObject[][] goal){
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
                    helpFunction2(node, operator ,node[_row1][_col1 + 1].getValue(), node[_row2][_col2 + 1].getValue(), "L", goal, 6);
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
                    helpFunction2(node, operator, node[_row1][_col1 - 1].getValue(), node[_row2][_col2 - 1].getValue(), "R", goal, 6);
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
                    helpFunction2(node, operator, node[_row1 + 1][_col1].getValue(), node[_row2 + 1][_col2].getValue(), "U", goal, 7);
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
                    helpFunction2(node, operator, node[_row1 - 1][_col1].getValue(), node[_row2 - 1][_col2].getValue(), "D", goal, 7);
                }
            }
            oneTile(node, _row1, _col1, n, goal);
            oneTile(node, _row2, _col2, n, goal);
        } // end horizontal
    }

    /**
     * The function handles in case there is only one blank tile.
     * @param node
     * @param _row1
     * @param _col1
     */
    public void oneTile(TileObject[][] node, int _row1, int _col1, NodeInfo n, TileObject[][] goal) {
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
                helpFunction1(node, operator, node[_row1][_col1 + 1].getValue(), "L", goal, 5);
            }
        }
        if(_row1 != rowLength - 1 && !node[_row1 + 1][_col1].getValue().equals("_")) // if the blank is not in the last row, move up
        {
                String father = node[_row1 + 1][_col1].getValue() + "D"; // the opposite operation, check!
                if (!father.equals(n.getFather())) // if it is not the opposite operation of the father.
                {
                    // move up
                    operator = copy(node);
                    operator[_row1][_col1].setValue(node[_row1 + 1][_col1].getValue());
                    operator[_row1 + 1][_col1].setValue(node[_row1][_col1].getValue());
                    helpFunction1(node, operator, node[_row1 + 1][_col1].getValue(), "U", goal, 5);
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
                helpFunction1(node, operator, node[_row1][_col1 - 1].getValue(), "R", goal, 5);
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
                helpFunction1(node, operator, node[_row1 - 1][_col1].getValue(), "D", goal, 5);
            }
        }
    }

    private void helpFunction1(TileObject[][] node, TileObject[][] operator, String data, String direction, TileObject[][] goal, int price) {
        this.numNodes++;
        String nodeStr = matrixToString(node);
        String operatorStr = matrixToString(operator);
        NodeInfo newNodeInfo = new NodeInfo(); // create new node info for the operator
        String operation = data + direction;
        newNodeInfo.setFather(operation); // set the father to bo like "6L"
        newNodeInfo.swap(exploreSet.get(nodeStr)); // set the new node to be the node that his key in explore set is the old node
        String newPath = "-" + operation;
        newNodeInfo.addPath(newPath);
        newNodeInfo.addCost(price);
        double result = heuristicFunction(operator); // calculate the heuristic function of the operator node
        newNodeInfo.setHeuristicFunResulte(result);//add the heuristic function value
        newNodeInfo.copyNode(operator);// add the operator node matrix
        if(!frontier_HT.containsKey(operatorStr) && !exploreSet.containsKey(operatorStr)) // check if the matrix node is not in the open list and not in the close list
        {
            this.frontier_HT.put(operatorStr,newNodeInfo);
            this.frontier_Q.add(newNodeInfo);
        }else if(frontier_HT.containsKey(operatorStr)) {
            double evaluation = frontier_HT.get(operatorStr).getEvaluation();
            if (newNodeInfo.getEvaluation() < evaluation) {// if the exists node in the open list has a bigger Evaluation value, remove it and insert the new operator node instead.
                frontier_Q.remove(frontier_HT.get(operatorStr));
                frontier_HT.remove(operatorStr);
                frontier_HT.put(operatorStr, newNodeInfo);
                frontier_Q.add(newNodeInfo);
            }
        }
    }


    private void helpFunction2(TileObject[][] node, TileObject[][] operator, String data1, String data2, String direction, TileObject[][] goal, int price) {
        this.numNodes++;
        String nodeStr = matrixToString(node);
        String operatorStr = matrixToString(operator);
        NodeInfo newNodeInfo = new NodeInfo(); // create new node info for the operator
        String operation = data1 + "&" + data2 + direction;
        newNodeInfo.setFather(operation); // set the father to bo like "6L"
        newNodeInfo.swap(exploreSet.get(nodeStr)); // set the new node to be the node that his key in explore set is the old node
        String newPath = "-" + operation;
        newNodeInfo.addPath(newPath);
        newNodeInfo.addCost(price);
        double result = heuristicFunction(operator); // calculate the heuristic function of the operator node
        newNodeInfo.setHeuristicFunResulte(result);//add the heuristic function value
        newNodeInfo.copyNode(operator);// add the operator node matrix
        if (!frontier_HT.containsKey(operatorStr) && !exploreSet.containsKey(operatorStr)) // check if the matrix node is not in the open list and not in the close list
        {
            frontier_HT.put(operatorStr, newNodeInfo);
            frontier_Q.add(newNodeInfo);
        } else if (frontier_HT.containsKey(operatorStr)) {
            double evaluation = frontier_HT.get(operatorStr).getEvaluation();
            if (newNodeInfo.getEvaluation() < evaluation) {// if the exists node in the open list has a bigger Evaluation value, remove it and insert the new operator node instead.
                frontier_Q.remove(frontier_HT.get(operatorStr));
                frontier_HT.remove(operatorStr);
                frontier_HT.put(operatorStr, newNodeInfo);
                frontier_Q.add(newNodeInfo);
            }
        }
    }
}
