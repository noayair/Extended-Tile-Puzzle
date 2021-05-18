/**
 * Represents a single tile.
 *
 */

public class TileObject {
    private String value;
    private int cost;

    //constructors

    TileObject(){
        this.value = "";
        this.cost = 0;
    }

    TileObject(String s){
        this.value = s;
    }

    //Getters and Setters

    public String getValue(){
        return this.value;
    }

    public void setValue(String s){
        this.value = s;
    }

    public int getCost(){
        return this.cost;
    }

    public void setCost(int cost){
        this.cost = cost;
    }

    public void swap(TileObject newTile) {
        this.value = newTile.value;
    }


}
