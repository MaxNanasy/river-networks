import java.awt.Color;


public class Position {
    public int row, column, value;
    public boolean visited;
 

    public Position (int r, int c, int v)
    {
	row = r;
	column = c;
	value = v;
	visited = false;
    }
	
    public String toString()
    {
	return value + "";
    }
    public String toString2()
    {
    	return "(" + row + ", " + column + ")";
    			
    }
}
