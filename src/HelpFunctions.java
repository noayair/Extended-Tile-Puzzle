import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 * Contains some functions for the use of all the algorithms.
 *
 */

public class HelpFunctions {

    public String matrixToString(TileObject[][] node){
        String ans = "";
        for(int i = 0; i < node.length; i++)
        {
            for(int j = 0; j < node[0].length; j++)
            {
                if(i == 0 && j == 0)
                {
                    ans += node[i][j].getValue();
                }else{
                    ans += "," + node[i][j].getValue();
                }
            }
        }
        return ans;
    }


    /**
     * this function gets a matrix that represents a tile node. 
     * @return the node's heuristic function result- by the Manhattan distance formula .
     */
    public double heuristicFunction(TileObject [][] nodeMat) {
        double result = 0;
        double price = 5;
        int blankNum = 1;
        int [][] blank = blank_(nodeMat);
        int _row1 = blank[0][0];
        int _col1 = blank[0][1];
        int _row2 = blank[1][0];
        int _col2 = blank[1][1];
        // check if we have 1 or 2 blanks
        if(_row2 != -1 && _col2 != -1) blankNum = 2;
        if(blankNum == 2) // if there is 2 blanks
        {
            if ((_row2 == _row1 && _col2 == _col1 + 1) || (_row2 == _row1 + 1 && _col2 == _col1)) // If they are next to each other
            {
                if (_col1 == _col2) // if the blank tiles are Vertical
                {
                    price = 3.5;
                } else if (_row1 == _row2) {
                    price = 3;
                }
            }
        }
        for(int i = 0; i < nodeMat.length; i++)
        {
            for(int j = 0; j < nodeMat[0].length; j++)
            {
                if(!nodeMat[i][j].getValue().equals("_")) { // The function does not calculate the blank value result
                    int numValue = Integer.parseInt(nodeMat[i][j].getValue());
                    int [] index = getCorrectIndex(numValue, nodeMat.length, nodeMat[0].length);
                    int correctLengthIndex = index[0];
                    int correctWidthIndex = index[1];
                    result = result + (Math.abs(i - correctLengthIndex) + Math.abs(j - correctWidthIndex)) * price;
                }
            }
        }
        return result;
    }


    /**
     * this function gets single value and a size of a matrix.
     * @return an two size array that represents the location the value should be at in the matrix.
     */
    private int[] getCorrectIndex(int value, int rowLength, int colLength) {
        int[] index = new int [2];
        int mod = value % colLength;
        int lengthIndex = value / colLength;
        int widthIndex = 0;
        if(mod == 0) {
            lengthIndex = lengthIndex - 1;
            widthIndex = colLength - 1;
        }
        else {
            widthIndex = mod - 1;

        }
        index[0] = lengthIndex;
        index[1] = widthIndex;
        return index;
    }


    public void printNode(TileObject[][] nodes) {
        for(int i = 0; i < nodes.length; i++)
        {
            String ans = "";
            for(int j = 0; j < nodes[0].length; j++)
            {
                ans += nodes[i][j].getValue();
                if( j != nodes[0].length-1) ans += ",";
            }
            System.out.println(ans);
        }
    }


    /**
     * this function check where the blank tiles are
     * @param tileObj
     * @return
     */
    public int[][] blank_(TileObject tileObj[][]){
        int counter = 0;
        int[][] blankMat = new int[2][2];
//        boolean found_ = false;
        // set all to be -1
        for(int i = 0; i < 2; i++)
        {
            for(int j = 0; j < 2; j++)
            {
                blankMat[i][j] = -1;
            }
        }
        // check where is the blank
        for(int i = 0; i < tileObj.length; i++)
        {
            for(int j = 0; j < tileObj[0].length; j++)
            {
                if(tileObj[i][j].getValue().equals("_"))
                {
                    counter++;
                    if(counter == 1)
                    {
                        blankMat[0][0] = i;
                        blankMat[0][1] = j;
                    }
                    if (counter == 2)
                    {
                        blankMat[1][0] = i;
                        blankMat[1][1] = j;
                        break;
                    }
                }
            }
        }
        return blankMat;
    }


    public TileObject[][] copy(TileObject[][] source){
        TileObject[][] ans = new TileObject[source.length][source[0].length];
        for(int i = 0; i < ans.length; i++)
        {
            for (int j = 0; j < ans[0].length; j++)
            {
                ans[i][j] = new TileObject();
                ans[i][j].setValue(source[i][j].getValue());
            }
        }
        return ans;
    }
}
