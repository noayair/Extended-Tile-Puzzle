import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Stack;

public class DFBnB extends HelpFunctions{
    int numNodes;
    int cost; // total cost
    Hashtable< String, NodeInfo> loopAvoidance_HT;
    Stack<NodeInfo> loopAvoidance_stack;
    double threshold;

    // constructor

    DFBnB(){
        this.numNodes = 1;
        this.cost = 0;
        this.loopAvoidance_HT = new Hashtable<String, NodeInfo>();
        this.loopAvoidance_stack = new Stack<NodeInfo>();
    }

    // public methods

    public NodeInfo DFBnB (TileObject [][] start, TileObject[][] goal, boolean withOpen) {
        String startStr = matrixToString(start);
        String goalStr = matrixToString(goal);
        if (startStr.equals(goalStr)) // check if the start node is already the goal node
        {
            return new NodeInfo(0, "no path", 0, start);
        }
//        this.threshold = Math.min(Integer.MAX_VALUE, factorial(find_tile_number(start)));
        this.threshold = Integer.MAX_VALUE;
        NodeInfo startInf = new NodeInfo(0 ,"", heuristicFunction(start) ,start);
        loopAvoidance_stack.add(startInf);
//        String startNodeStr = matrixToString(start);
        loopAvoidance_HT.put(startStr,startInf);
        NodeInfo result = new NodeInfo();
        int rounds = 1; // for the open list print mode
        while(!loopAvoidance_stack.empty())
        {
            if(withOpen) // for the open list print
            {
                System.out.println("round: "+ rounds); // print the number of rounds when print the open list.
                rounds++;
                for(NodeInfo nodeinf : loopAvoidance_stack)
                {
                    printNode(nodeinf.getNode());
                    System.out.println("*******");
                }
            }
            NodeInfo node = loopAvoidance_stack.pop();
            if(node.isOut()) // if the node is marked as out
            {
                loopAvoidance_HT.remove(matrixToString(node.getNode()));
            }
            else {
                node.markAsOut();
                loopAvoidance_stack.push(node);

                int blankNum = 1;
                TileObject[][] nodeMatrix = node.getNode();
                ArrayList<NodeInfo> operatorsList = new ArrayList<NodeInfo>();
                int[][] blank = new int[2][2]; // will represents the blank location
                blank = blank_(nodeMatrix); // find the blank index
                // the rows and columns of the blank tiles
                int _row1 = blank[0][0];
                int _col1 = blank[0][1];
                int _row2 = blank[1][0];
                int _col2 = blank[1][1];
                // check if we have 1 or 2 blanks
                if (_row2 != -1 && _col2 != -1) blankNum = 2;
                if (blankNum == 2) // if there is 2 blanks
                {
                    if ((_row2 == _row1 && _col2 == _col1 + 1) || (_row2 == _row1 + 1 && _col2 == _col1)) // If they are next to each other
                    {
                        twoTiles(node, nodeMatrix, _row1, _row2, _col1, _col2, goal, operatorsList);
//                        operatorsList.add(result);
                    }
                    else { // if there are two blank tiles but they are not next to each other
                    oneTile(node, nodeMatrix, _row1, _col1, goal, operatorsList);
                    oneTile(node, nodeMatrix, _row2, _col2, goal, operatorsList);
                     }
                }else if (blankNum == 1) // if there is 1 blank
                {
                    oneTile(node, nodeMatrix, _row1, _col1, goal, operatorsList);
                }
                Collections.sort(operatorsList);
                boolean erase = false;
                for(int i = 0; i < operatorsList.size(); i++)
                {
                    String operatorStr = matrixToString(operatorsList.get(i).getNode());
                    if(erase == true) { // if the node need to be removed from the array list
                        operatorsList.remove(i);
                        i--; // after removing element from an arraylist, all elements moves one place left
                    }else if (operatorsList.get(i).getEvaluation() >= threshold) {
                        operatorsList.remove(i); // remove the current child from the array list
                        erase = true; // to erase the rest of children that after him in the array list order
                        i--; // after removing element from an arraylist, all elements moves one place left
                    }else if (loopAvoidance_HT.containsKey(operatorStr)) {
                        NodeInfo sameNodeStrInfo = loopAvoidance_HT.get(operatorStr);
                        if(sameNodeStrInfo.isOut() == true) {
                            operatorsList.remove(i);
                            i--; // after removing element from an arraylist, all elements moves one place left
                        }
                        else {
                            if(sameNodeStrInfo.getEvaluation() <= operatorsList.get(i).getEvaluation())
                            { // if the current child node has a bigger Evaluation value, remove it from the array list.
                                operatorsList.remove(i);
                                i--; // after removing element from an arraylist, all elements moves one place left
                            }
                            else { // if the exists node in the hash table has a bigger Evaluation value, remove it.
                                loopAvoidance_HT.remove(operatorStr);
                                loopAvoidance_stack.remove(sameNodeStrInfo);
                            }
                        }
                    }
                    else if(operatorStr.equals(goalStr)) {
                        threshold = operatorsList.get(i).getEvaluation();
                        result = operatorsList.get(i);
                        erase = true;
                        operatorsList.remove(i);
                        i--; // after removing element from an arraylist, all elements moves one place left
                    }
                }
                Collections.reverse(operatorsList);
                for(int i = 0; i < operatorsList.size(); i++) { // insert the remains child nodes to the loopAvoidance hash table and stack.
                    String operatorStr = matrixToString(operatorsList.get(i).getNode());
                    loopAvoidance_HT.put(operatorStr, operatorsList.get(i));
                    loopAvoidance_stack.add(operatorsList.get(i));
                }
            }
        }
        return result;
    }



    // private methods



    /**
     * The function handles in case there are two blank tiles next to each other.
     */
    private NodeInfo twoTiles(NodeInfo nodeInfo, TileObject[][] node, int _row1, int _row2, int _col1, int _col2, TileObject[][] goal, ArrayList<NodeInfo> operatorList){
        int rowLength = node.length;
        int colLength = node[0].length;
        TileObject[][] operator = new TileObject[rowLength][colLength];
        if(_col1 == _col2) // if the blank tiles are Vertical
        {
            // 2 cases for move 2 tiles left or right
            if(_col2 != colLength - 1) // If they are not in the last column
            {
                String father = node[_row1][_col1 + 1].getValue() + "&" + node[_row2][_col2 + 1].getValue() + "R"; // the opposite operation, check!
                if(!father.equals(nodeInfo.getFather())) // if it is not the opposite operation of the father.
                {
                    // move left 2 tiles
                    operator = copy(node); // init operator with node
                    operator[_row1][_col1].swap(node[_row1][_col1 + 1]); // move the first tile to the first blank
                    operator[_row1][_col1 + 1].swap(node[_row1][_col1]); // move the blank
                    operator[_row2][_col2].swap(node[_row2][_col2 + 1]); // move the second tile to the second blank
                    operator[_row2][_col2 + 1].swap(node[_row2][_col2]); // move the blank
                    NodeInfo result = new NodeInfo();
                    result = helpFunction2(nodeInfo, operator ,node[_row1][_col1 + 1].getValue(), node[_row2][_col2 + 1].getValue(), "L", goal, 6);
                    operatorList.add(result);
//                    if (!result.getPath().equals("")) {
//                        return result;
//                    }
                }
            }
            if(_col2 != 0) // if they are not in the first column
            {
                String father = node[_row1][_col1 - 1].getValue() + "&" + node[_row2][_col2 - 1].getValue() + "L"; // the opposite operation, check!
                if(!father.equals(nodeInfo.getFather())) // if it is not the opposite operation of the father.
                {
                    // move right 2 tiles
                    operator = copy(node);
                    operator[_row1][_col1].swap(node[_row1][_col1 - 1]);
                    operator[_row1][_col1 - 1].swap(node[_row1][_col1]);
                    operator[_row2][_col2].swap(node[_row2][_col2 - 1]);
                    operator[_row2][_col2 - 1].swap(node[_row2][_col2]);
                    NodeInfo result = new NodeInfo();
                    result = helpFunction2(nodeInfo, operator, node[_row1][_col1 - 1].getValue(), node[_row2][_col2 - 1].getValue(), "R", goal, 6);
                    operatorList.add(result);
//                    if (!result.getPath().equals("")) {
//                        return result;
//                    }
                }
            }
            oneTile(nodeInfo, node, _row1, _col1, goal, operatorList);
            oneTile(nodeInfo, node, _row2, _col2, goal, operatorList);
        } // end vertical

        if(_row1 == _row2) // if the blank tiles are horizontal
        {
            // 2 cases for move 2 tiles up or down
            if(_row1 != rowLength - 1) // If they are not in the first row
            {
                // move up 2 tiles
                String father = node[_row1 + 1][_col1].getValue() + "&" + node[_row2 + 1][_col2].getValue() + "D"; // the opposite operation, check!
                if(!father.equals(nodeInfo.getFather())) // if it is not the opposite operation of the father.
                {
                    operator = copy(node);
                    operator[_row1][_col1].swap(node[_row1 + 1][_col1]);
                    operator[_row1 + 1][_col1].swap(node[_row1][_col1]);
                    operator[_row2][_col2].swap(node[_row2 + 1][_col2]);
                    operator[_row2 + 1][_col2].swap(node[_row2][_col2]);
                    NodeInfo result = new NodeInfo();
                    result = helpFunction2(nodeInfo, operator, node[_row1 + 1][_col1].getValue(), node[_row2 + 1][_col2].getValue(), "U", goal, 7);
                    operatorList.add(result);
//                    if (!result.getPath().equals("")) {
//                        return result;
//                    }
                }
            }
            if(_row2 != 0) // if they are not in the last row
            {
                // move down 2 tiles
                String father = node[_row1 - 1][_col1].getValue() + "&" + node[_row2 - 1][_col2].getValue() + "U"; // the opposite operation, check!
                if(!father.equals(nodeInfo.getFather())) // if it is not the opposite operation of the father.
                {
                    operator = copy(node);
                    operator[_row1][_col1].swap(node[_row1 - 1][_col1]);
                    operator[_row1 - 1][_col1].swap(node[_row1][_col1]);
                    operator[_row2][_col2].swap(node[_row2 - 1][_col2]);
                    operator[_row2 - 1][_col2].swap(node[_row2][_col2]);
                    NodeInfo result = new NodeInfo();
                    result = helpFunction2(nodeInfo, operator, node[_row1 - 1][_col1].getValue(), node[_row2 - 1][_col2].getValue(), "D", goal, 7);
                    operatorList.add(result);
//                    if (!result.getPath().equals("")) {
//                        return result;
//                    }
                }
            }
            oneTile(nodeInfo, node, _row1, _col1, goal, operatorList);
            oneTile(nodeInfo, node, _row2, _col2, goal, operatorList);
        } // end horizontal
        return new NodeInfo();
    }



    /**
     * The function handles in case there is only one blank tile.
     * @param node
     * @param _row1
     * @param _col1
     */
    private NodeInfo oneTile(NodeInfo nodeInfo, TileObject[][] node, int _row1, int _col1, TileObject[][] goal, ArrayList<NodeInfo> operatorList) {
        int rowLength = node.length;
        int colLength = node[0].length;
        TileObject[][] operator = new TileObject[rowLength][colLength];
        // cases for move 1 tile
        if(_col1 != colLength - 1 && !node[_row1][_col1 + 1].getValue().equals("_")) // If the blank is not in the last column, move left
        {
            String father = node[_row1][_col1 + 1].getValue() + "R"; // the opposite operation, check!
            if(!father.equals(nodeInfo.getFather())) // if it is not the opposite operation of the father.
            {
                // move left
                operator = copy(node);
                operator[_row1][_col1].setValue(node[_row1][_col1 + 1].getValue());
                operator[_row1][_col1 + 1].setValue(node[_row1][_col1].getValue());
                NodeInfo result = new NodeInfo();
                result = helpFunction1(nodeInfo, operator, node[_row1][_col1 + 1].getValue(), "L", goal, 5);
                operatorList.add(result);
//                if (!result.getPath().equals("")) {
//                    return result;
//                }
            }
        }
        if(_row1 != rowLength - 1 && !node[_row1 + 1][_col1].getValue().equals("_")) // if the blank is not in the last row, move up
        {
            String father = node[_row1 + 1][_col1].getValue() + "D"; // the opposite operation, check!
            if(!father.equals(nodeInfo.getFather())) // if it is not the opposite operation of the father.
            {
                // move up
                operator = copy(node);
                operator[_row1][_col1].setValue(node[_row1 + 1][_col1].getValue());
                operator[_row1 + 1][_col1].setValue(node[_row1][_col1].getValue());
                NodeInfo result = new NodeInfo();
                result = helpFunction1(nodeInfo, operator, node[_row1 + 1][_col1].getValue(), "U", goal, 5);
                operatorList.add(result);
//                if (!result.getPath().equals("")) {
//                    return result;
//                }
            }
        }
        if(_col1 != 0 && !node[_row1][_col1 - 1].getValue().equals("_")) // if the blank is not in the first column, move right
        {
            String father = node[_row1][_col1 - 1].getValue() + "L"; // the opposite operation, check!
            if(!father.equals(nodeInfo.getFather())) // if it is not the opposite operation of the father.
            {
                // move right
                operator = copy(node);
                operator[_row1][_col1].setValue(node[_row1][_col1 - 1].getValue());
                operator[_row1][_col1 - 1].setValue(node[_row1][_col1].getValue());
                NodeInfo result = new NodeInfo();
                result = helpFunction1(nodeInfo, operator, node[_row1][_col1 - 1].getValue(), "R", goal, 5);
                operatorList.add(result);
//                if (!result.getPath().equals("")) {
//                    return result;
//                }
            }
        }
        if(_row1 != 0 && !node[_row1 - 1][_col1].getValue().equals("_")) // if the blank is not in the first row, move down
        {
            String father = node[_row1 - 1][_col1].getValue() + "U"; // the opposite operation, check!
            if(!father.equals(nodeInfo.getFather())) // if it is not the opposite operation of the father.
            {
                // move down
                operator = copy(node);
                operator[_row1][_col1].setValue(node[_row1 - 1][_col1].getValue());
                operator[_row1 - 1][_col1].setValue(node[_row1][_col1].getValue());
                NodeInfo result = new NodeInfo();
                result = helpFunction1(nodeInfo, operator, node[_row1 - 1][_col1].getValue(), "D", goal, 5);
                operatorList.add(result);
//                if (!result.getPath().equals("")) {
//                    return result;
//                }
            }
        }
        return new NodeInfo();
    }


    private NodeInfo helpFunction2(NodeInfo node, TileObject[][] operator, String data1, String data2, String direction, TileObject[][] goal, int price) {
        this.numNodes++;
        TileObject[][] nodeMatrix = node.getNode();
        NodeInfo operatorInfo = new NodeInfo();
        String Operation = data1 + "&" + data2 + direction; // the node operation ("8D" as example)
        operatorInfo.setFather(Operation); // update the node operation
        operatorInfo.swap(node); // get the path and cost from the father node
        operatorInfo.copyNode(operator); // insert the operator matrix to the object
        operatorInfo.addPath("-" + Operation); // update the operator node path
        operatorInfo.addCost(price); // update the operator node cost
        operatorInfo.setHeuristicFunResulte(heuristicFunction(operator)); // calculate and update the heuristic Function of the node matrix
        return operatorInfo;
    }

    private NodeInfo helpFunction1(NodeInfo node, TileObject[][] operator, String data, String direction, TileObject[][] goal, int price) {
        this.numNodes++;
        TileObject[][] nodeMatrix = node.getNode();
        NodeInfo operatorInfo = new NodeInfo();
        String Operation = data + direction; // the node operation ("8D" as example)
        operatorInfo.setFather(Operation); // update the node operation
        operatorInfo.swap(node); // get the path and cost from the father node
        operatorInfo.copyNode(operator); // insert the operator matrix to the object
        operatorInfo.addPath("-" + Operation); // update the operator node path
        operatorInfo.addCost(price); // update the operator node cost
        operatorInfo.setHeuristicFunResulte(heuristicFunction(operator)); // calculate and update the heuristic Function of the node matrix
        return operatorInfo;
    }


//    /**
//     *
//     * this function gets a matrix that represents a tile puzzle node.
//     * @return the number of tiles that can be moved .
//     */
//    private int find_tile_number(TileObject[][] node) {
//        int num = 0;
//        for(int i = 0; i < node.length; i++)
//        {
//            for(int j = 0; j < node[0].length; j++)
//            {
//                if (!node[i][j].color.equals("B")) {
//                    num++;
//                }
//            }
//        }
//        return num;
//    }



    /**
     *
     * this function gets an integer.
     * @return the factorial of the number.
     */
    private static int factorial(int n) {
        int res = 1;

        for (int i = 2; i <= n; i++) {
            res *= i;
        }

        return res;
    }
}
