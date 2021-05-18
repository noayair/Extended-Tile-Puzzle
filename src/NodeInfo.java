
public class NodeInfo implements Comparable<NodeInfo>{
    private TileObject [][] nodes;
    private String path;
    private int cost;
    private double heuristicResult;
    private Boolean out = false;
    private String father;
    private float time;

    //constructors

    NodeInfo(){
        this.path = "";
        this.cost = 0;
        this.heuristicResult = 0;
        this.time = 0;
    }

    NodeInfo(TileObject[][] nodes, String path, int cost, double heuristicResult){
        this.nodes = nodes;
        this.path = path;
        this.cost = cost;
        this.heuristicResult = heuristicResult;
    }

    public NodeInfo(int cost, String path) {
        this.cost = cost;
        this.path = path;
    }

    public NodeInfo (int newCost, String newPath, double huristic, TileObject [][] newNode){
        this.path=newPath;
        this.cost=newCost;
        this.heuristicResult=huristic;
        copyNode(newNode);
    }

    //Getter and Setter
//    public TileObject[][] getNodes() {
//        return nodes;
//    }

//    public void setNodes(TileObject[][] nodes) {
//        this.nodes = nodes;
//    }

    public float getTime(){
        return this.time;
    }


    public void setTime(float time){
        this.time = time;
    }


    public String getPath() {
        return path;
    }

//    public void setPath(String path) {
//        this.path = path;
//    }

    public int getCost() {
        return cost;
    }

//    public void setCost(int cost) {
//        this.cost = cost;
//    }
//
//    public int getHeuristicResult() {
//        return heuristicResult;
//    }

    public void setHeuristicResult(double heuristicResult) {
        this.heuristicResult = heuristicResult;
    }

//    public Boolean getOut() {
//        return out;
//    }

    public void setOut() {
        this.out = false;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public void swap(NodeInfo newNodeInfo){
        this.cost = newNodeInfo.cost;
        this.path = newNodeInfo.path;
    }

    public void addPath(String newPath) {
        this.path=this.path+(newPath);
    }

    public void addCost(int newCost) {
        this.cost=this.cost+newCost;
    }

    /**
     * this function sets the node matrix of the current object to another given matrix.
     */
    public void copyNode(TileObject [][] newNode) { // copy matrix function
        this.nodes = new TileObject[newNode.length][newNode[0].length] ;
        for(int i = 0; i < newNode.length; i++)
        {
            for(int j = 0; j < newNode[0].length; j++) {
                this.nodes[i][j] = new TileObject();
                this.nodes[i][j].swap(newNode[i][j]);
            }
        }
    }

    public TileObject[][] getNode() {
        return this.nodes;
    }

    public void setHeuristicFunResulte(double result) {
        this.heuristicResult = result;
    }

    public double getEvaluation() {
        return this.heuristicResult + this.cost;
    }

    @Override
    public int compareTo(NodeInfo other) {
        return (int) (this.getEvaluation() - other.getEvaluation());
    }

    public boolean isOut() {
        return this.out;
    }

    public void markAsOut() {
        this.out = true;
    }
}
