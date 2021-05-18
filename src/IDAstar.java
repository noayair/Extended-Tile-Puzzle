import java.util.Hashtable;
import java.util.Stack;

/**
 * Represents a IDA* algorithm on a tile puzzle.
 */
public class IDAstar extends HelpFunctions {
    int numNodes;
    int cost; // total cost
    Hashtable<String, NodeInfo> loopAvoidance_HT;
    Stack<NodeInfo> loopAvoidance_stack;
    double minEvaluationValue;
    double threshold;


    //constructor

    IDAstar() {
        this.numNodes = 1;
        this.cost = 0;
        this.loopAvoidance_HT = new Hashtable<String, NodeInfo>();
        this.loopAvoidance_stack = new Stack<NodeInfo>();
    }

    // public methods

    public NodeInfo IDAstar(TileObject[][] start, TileObject[][] goal, boolean withOpen) {
        String startStr = matrixToString(start);
        String goalStr = matrixToString(goal);
        if (startStr.equals(goalStr)) // check if the start node is already the goal node
        {
            return new NodeInfo(0, "no path", 0, start);
        }
        threshold = heuristicFunction(start);
        NodeInfo nodeInfo = new NodeInfo(0, "", threshold, start); // start mode info
        int rounds = 1; // for the open list print mode
        while (threshold < Integer.MAX_VALUE) {
            nodeInfo.setOut();
            minEvaluationValue = Integer.MAX_VALUE;
            String nodeStr = matrixToString(start);
            loopAvoidance_stack.push(nodeInfo);
            loopAvoidance_HT.put(nodeStr, nodeInfo);
            while (!loopAvoidance_stack.isEmpty()) {
                if (withOpen) { // for the open list print
                    System.out.println("round: " + rounds); // print the number of rounds when print the open list.
                    rounds++;
                    for (NodeInfo nodeinf : loopAvoidance_stack) {
                        printNode(nodeinf.getNode());
                        System.out.println("*******");
                    }
                }
                NodeInfo node = loopAvoidance_stack.pop();
                TileObject[][] nodeMatrix = node.getNode();
                String nodeMatrixStr = matrixToString(nodeMatrix);
                if (node.isOut()) { // if the node is marked as out
                    loopAvoidance_HT.remove(nodeMatrixStr);
                } else {
                    node.markAsOut();
                    loopAvoidance_stack.push(node);
                    int[][] blank = blank_(nodeMatrix);
                    int blankNum = 0;
                    // the rows and columns of the blank tiles
                    int _row1 = blank[0][0];
                    int _col1 = blank[0][1];
                    int _row2 = blank[1][0];
                    int _col2 = blank[1][1];
                    // check if we have 1 or 2 blanks
                    if (_row1 != -1 && _col1 != -1) blankNum = 1;
                    if (_row2 != -1 && _col2 != -1) blankNum = 2;
                    NodeInfo ans = new NodeInfo();
                    if (blankNum == 2) // if there is 2 blanks
                    {
                        if ((_row2 == _row1 && _col2 == _col1 + 1) || (_row2 == _row1 + 1 && _col2 == _col1)) // If they are next to each other
                        {
                            NodeInfo result = twoTiles(node, nodeMatrix, _row1, _row2, _col1, _col2, nodeInfo, goal);
                            if (!result.getPath().equals("")) {
                                return result;
                            }
                        } else { // if there are two blank tiles but they are not next to each other
                            oneTile(node, nodeMatrix, _row1, _col1, nodeInfo, goal);
                            oneTile(node, nodeMatrix, _row2, _col2, nodeInfo, goal);
                        }
                    } else if (blankNum == 1) // if there is 1 blank
                    {
                        ans = oneTile(node, nodeMatrix, _row1, _col1, nodeInfo, goal);
                        if (!ans.getPath().equals("")) {
                            return ans;
                        }
                    }
                }
            }
            this.threshold = this.minEvaluationValue;
        }
        return new NodeInfo(0, "no path");
    }


    //private methods

    /**
     * The function handles in case there are two blank tiles next to each other.
     */
    private NodeInfo twoTiles(NodeInfo nodeInfo, TileObject[][] node, int _row1, int _row2, int _col1, int _col2, NodeInfo n, TileObject[][] goal) {
        int rowLength = node.length;
        int colLength = node[0].length;
        TileObject[][] operator = new TileObject[rowLength][colLength];
        if (_col1 == _col2) // if the blank tiles are Vertical
        {
            // 2 cases for move 2 tiles left or right
            if (_col2 != colLength - 1) // If they are not in the last column
            {
                String father = node[_row1][_col1 + 1].getValue() + "&" + node[_row2][_col2 + 1].getValue() + "R"; // the opposite operation, check!
//                String father2 = node[_row2][_col2 + 1].getValue() + "R"; // the opposite operation, check!
                if (!father.equals(n.getFather())) // if it is not the opposite operation of the father.
                {
                    // move left 2 tiles
                    operator = copy(node); // init operator with node
                    operator[_row1][_col1].swap(node[_row1][_col1 + 1]); // move the first tile to the first blank
                    operator[_row1][_col1 + 1].swap(node[_row1][_col1]); // move the blank
                    operator[_row2][_col2].swap(node[_row2][_col2 + 1]); // move the second tile to the second blank
                    operator[_row2][_col2 + 1].swap(node[_row2][_col2]); // move the blank
                    NodeInfo result = new NodeInfo();
                    result = helpFunction2(nodeInfo, node, operator, node[_row1][_col1 + 1].getValue(), node[_row2][_col2 + 1].getValue(), "L", goal, 6);
                    if (!result.getPath().equals("")) {
                        return result;
                    }
                }
            }
            if (_col2 != 0) // if they are not in the first column
            {
                String father = node[_row1][_col1 - 1].getValue() + "&" + node[_row2][_col2 - 1].getValue() + "L"; // the opposite operation, check!
//                String father2 = node[_row2][_col2 - 1].getValue() + "L"; // the opposite operation, check!
                if (!father.equals(n.getFather())) // if it is not the opposite operation of the father.
                {
                    // move right 2 tiles
                    operator = copy(node);
                    operator[_row1][_col1].swap(node[_row1][_col1 - 1]);
                    operator[_row1][_col1 - 1].swap(node[_row1][_col1]);
                    operator[_row2][_col2].swap(node[_row2][_col2 - 1]);
                    operator[_row2][_col2 - 1].swap(node[_row2][_col2]);
                    NodeInfo result = new NodeInfo();
                    result = helpFunction2(nodeInfo, node, operator, node[_row1][_col1 - 1].getValue(), node[_row2][_col2 - 1].getValue(), "R", goal, 6);
                    if (!result.getPath().equals("")) {
                        return result;
                    }
                }
            }
            oneTile(nodeInfo, node, _row1, _col1, n, goal);
            oneTile(nodeInfo, node, _row2, _col2, n, goal);
        } // end vertical

        if (_row1 == _row2) // if the blank tiles are horizontal
        {
            // 2 cases for move 2 tiles up or down
            if (_row1 != rowLength - 1) // If they are not in the first row
            {
                // move up 2 tiles
                String father = node[_row1 + 1][_col1].getValue() + "&" + node[_row2 + 1][_col2].getValue() + "D"; // the opposite operation, check!
//                String father2 = node[_row2 - 1][_col2].getValue() + "D"; // the opposite operation, check!
                if (!father.equals(n.getFather())) // if it is not the opposite operation of the father.
                {
                    operator = copy(node);
                    operator[_row1][_col1].swap(node[_row1 + 1][_col1]);
                    operator[_row1 + 1][_col1].swap(node[_row1][_col1]);
                    operator[_row2][_col2].swap(node[_row2 + 1][_col2]);
                    operator[_row2 + 1][_col2].swap(node[_row2][_col2]);
                    NodeInfo result = new NodeInfo();
                    result = helpFunction2(nodeInfo, node, operator, node[_row1 + 1][_col1].getValue(), node[_row2 + 1][_col2].getValue(), "U", goal, 7);
                    if (!result.getPath().equals("")) {
                        return result;
                    }
                }
            }
            if (_row2 != 0) // if they are not in the last row
            {
                // move down 2 tiles
                String father = node[_row1 - 1][_col1].getValue() + "&" + node[_row2 - 1][_col2].getValue() + "U"; // the opposite operation, check!
//                String father2 = node[_row2 + 1][_col2].getValue() + "U"; // the opposite operation, check!
                if (!father.equals(n.getFather())) // if it is not the opposite operation of the father.
                {
                    operator = copy(node);
                    operator[_row1][_col1].swap(node[_row1 - 1][_col1]);
                    operator[_row1 - 1][_col1].swap(node[_row1][_col1]);
                    operator[_row2][_col2].swap(node[_row2 - 1][_col2]);
                    operator[_row2 - 1][_col2].swap(node[_row2][_col2]);
                    NodeInfo result = new NodeInfo();
                    result = helpFunction2(nodeInfo, node, operator, node[_row1 - 1][_col1].getValue(), node[_row2 - 1][_col2].getValue(), "D", goal, 7);
                    if (!result.getPath().equals("")) {
                        return result;
                    }
                }
            }
            oneTile(nodeInfo, node, _row1, _col1, n, goal);
            oneTile(nodeInfo, node, _row2, _col2, n, goal);
        } // end horizontal
        return new NodeInfo();
    }

    /**
     * The function handles in case there is only one blank tile.
     *
     * @param node
     * @param _row1
     * @param _col1
     */
    private NodeInfo oneTile(NodeInfo nodeInfo, TileObject[][] node, int _row1, int _col1, NodeInfo n, TileObject[][] goal) {
        int rowLength = node.length;
        int colLength = node[0].length;
        TileObject[][] operator = new TileObject[rowLength][colLength];
        // cases for move 1 tile
        if (_col1 != colLength - 1 && !node[_row1][_col1 + 1].getValue().equals("_")) // If the blank is not in the last column, move left
        {
            String father = node[_row1][_col1 + 1].getValue() + "R"; // the opposite operation, check!
            if (!father.equals(n.getFather())) // if it is not the opposite operation of the father.
            {
                // move left
                operator = copy(node);
                operator[_row1][_col1].setValue(node[_row1][_col1 + 1].getValue());
                operator[_row1][_col1 + 1].setValue(node[_row1][_col1].getValue());
                NodeInfo result = new NodeInfo();
                result = helpFunction1(nodeInfo, node, operator, node[_row1][_col1 + 1].getValue(), "L", goal, 5);
                if (!result.getPath().equals("")) {
                    return result;
                }
            }
        }
        if (_row1 != rowLength - 1 && !node[_row1 + 1][_col1].getValue().equals("_")) // if the blank is not in the last row, move up
        {
            String father = node[_row1 + 1][_col1].getValue() + "D"; // the opposite operation, check!
            if (!father.equals(n.getFather())) // if it is not the opposite operation of the father.
            {
                // move up
                operator = copy(node);
                operator[_row1][_col1].setValue(node[_row1 + 1][_col1].getValue());
                operator[_row1 + 1][_col1].setValue(node[_row1][_col1].getValue());
                NodeInfo result = new NodeInfo();
                result = helpFunction1(nodeInfo, node, operator, node[_row1 + 1][_col1].getValue(), "U", goal, 5);
                if (!result.getPath().equals("")) {
                    return result;
                }
            }
        }
        if (_col1 != 0 && !node[_row1][_col1 - 1].getValue().equals("_")) // if the blank is not in the first column, move right
        {
            String father = node[_row1][_col1 - 1].getValue() + "L"; // the opposite operation, check!
            if (!father.equals(n.getFather())) // if it is not the opposite operation of the father.
            {
                // move right
                operator = copy(node);
                operator[_row1][_col1].setValue(node[_row1][_col1 - 1].getValue());
                operator[_row1][_col1 - 1].setValue(node[_row1][_col1].getValue());
                NodeInfo result = new NodeInfo();
                result = helpFunction1(nodeInfo, node, operator, node[_row1][_col1 - 1].getValue(), "R", goal, 5);
                if (!result.getPath().equals("")) {
                    return result;
                }
            }
        }
        if (_row1 != 0 && !node[_row1 - 1][_col1].getValue().equals("_")) // if the blank is not in the first row, move down
        {
            String father = node[_row1 - 1][_col1].getValue() + "U"; // the opposite operation, check!
            if (!father.equals(n.getFather())) // if it is not the opposite operation of the father.
            {
                // move down
                operator = copy(node);
                operator[_row1][_col1].setValue(node[_row1 - 1][_col1].getValue());
                operator[_row1 - 1][_col1].setValue(node[_row1][_col1].getValue());
                NodeInfo result = new NodeInfo();
                result = helpFunction1(nodeInfo, node, operator, node[_row1 - 1][_col1].getValue(), "D", goal, 5);
                if (!result.getPath().equals("")) {
                    return result;
                }
            }
        }
        return new NodeInfo();
    }

    private NodeInfo helpFunction1(NodeInfo nodeInfo, TileObject[][] node, TileObject[][] operator, String data, String direction, TileObject[][] goal, int price) {
        this.numNodes++;
        String nodeStr = matrixToString(node);
        String operatorStr = matrixToString(operator);
        String goalStr = matrixToString(goal);
        NodeInfo newNodeInfo = new NodeInfo(); // create new node info for the operator
        String operation = data + direction;
        newNodeInfo.setFather(operation); // set the father to bo like "6L"
        newNodeInfo.swap(nodeInfo); // set the new node to be the node that his key in explore set is the old node
        newNodeInfo.copyNode(operator);
        String newPath = "-" + operation;
        newNodeInfo.addPath(newPath);
        newNodeInfo.addCost(price);
        newNodeInfo.setHeuristicResult(heuristicFunction(operator));
//        int result = heuristicFunction(operator, price); // calculate the heuristic function of the operator node
//        newNodeInfo.setHeuristicFunResulte(result);//add the heuristic function value
//        newNodeInfo.copyNode(operator);// add the operator node matrix
        if (newNodeInfo.getEvaluation() > threshold) // check if the matrix node is not in the open list and not in the close list
        {
            minEvaluationValue = Math.min(newNodeInfo.getEvaluation(), minEvaluationValue);
            return new NodeInfo();
        }
        if (loopAvoidance_HT.contains(operatorStr)) {
            NodeInfo sameNodeStrInfo = loopAvoidance_HT.get(operatorStr);
            if (!sameNodeStrInfo.isOut()) {
                if (sameNodeStrInfo.getEvaluation() > newNodeInfo.getEvaluation()) // if the exists node in the open list has a bigger Evaluation value, remove it and insert the new operator node instead.
                {
                    loopAvoidance_HT.remove(operatorStr);
                    loopAvoidance_stack.remove(sameNodeStrInfo);
                } else {
                    return new NodeInfo();
                }
            } else {
                return new NodeInfo();
            }
        }
        if (operatorStr.equals(goalStr)) {
            return newNodeInfo;
        } else {
            loopAvoidance_HT.put(operatorStr, newNodeInfo);
            loopAvoidance_stack.push(newNodeInfo);
        }
        return new NodeInfo();
    }


    private NodeInfo helpFunction2(NodeInfo nodeInfo, TileObject[][] node, TileObject[][] operator, String data1, String data2, String direction, TileObject[][] goal, int price) {
        this.numNodes++;
        String nodeStr = matrixToString(node);
        String operatorStr = matrixToString(operator);
        String goalStr = matrixToString(goal);
        NodeInfo newNodeInfo = new NodeInfo(); // create new node info for the operator
        String operation = data1 + "&" + data2 + direction;
        newNodeInfo.setFather(operation); // set the father to bo like "6L"
        newNodeInfo.swap(nodeInfo); // set the new node to be the node that his key in explore set is the old node
        newNodeInfo.copyNode(operator);
        String newPath = "-" + operation;
        newNodeInfo.addPath(newPath);
        newNodeInfo.addCost(price);
        newNodeInfo.setHeuristicResult(heuristicFunction(operator));
        if (newNodeInfo.getEvaluation() > threshold) // check if the matrix node is not in the open list and not in the close list
        {
            minEvaluationValue = Math.min(newNodeInfo.getEvaluation(), minEvaluationValue);
            return new NodeInfo();
        }
        if (loopAvoidance_HT.contains(operatorStr)) {
            NodeInfo sameNodeStrInfo = loopAvoidance_HT.get(operatorStr);
            if (!sameNodeStrInfo.isOut()) {
                if (sameNodeStrInfo.getEvaluation() > newNodeInfo.getEvaluation()) { // if the exists node in the open list has a bigger Evaluation value, remove it and insert the new operator node instead.
                    loopAvoidance_HT.remove(operatorStr);
                    loopAvoidance_stack.remove(sameNodeStrInfo);
                } else {
                    return new NodeInfo();
                }
            } else {
                return new NodeInfo();
            }
        }
        if (operatorStr.equals(goalStr)) {
            return newNodeInfo;
        } else {
            loopAvoidance_HT.put(operatorStr, newNodeInfo);
            loopAvoidance_stack.push(newNodeInfo);
        }
        return new NodeInfo();
    }
}