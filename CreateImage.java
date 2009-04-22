import java.util.*;

import java.io.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

import org.jgraph.*;
import org.jgraph.graph.*;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.graph.*;
import org.jgrapht.ext.*;
import org.jgrapht.alg.*;

public class CreateImage
{

    //    private static final Color DEFAULT_BG_COLOR = Color.decode("#FAFBFF");
    //    private static final Dimension DEFAULT_SIZE = new Dimension(1000, 1000);

    private static Random randGen = new Random ();

    private JGraphModelAdapter<Position,DefaultEdge> m_jgAdapter;
	
    ArrayList<ArrayList<Position>> grid = new ArrayList<ArrayList<Position>>();
    ListenableDirectedGraph<Position,DefaultEdge> graph = new ListenableDirectedGraph<Position,DefaultEdge>(DefaultEdge.class);
    JGraph jgraph;
	ArrayList<ArrayList<Position>> connectedComponents = new ArrayList<ArrayList<Position>>();
	private int threads;

    public void create()
    {
	JFrame frame = new JFrame();
	frame.getContentPane().add(new JScrollPane(jgraph));
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.pack();
	frame.setVisible(true);
    }

    public void parseInput(String file, int threads, int limit) throws FileNotFoundException, InterruptedException
    {
    	//long startParseTime = System.currentTimeMillis();
    	m_jgAdapter = new JGraphModelAdapter<Position, DefaultEdge>( graph);
        jgraph = new JGraph (m_jgAdapter);
        this.threads = threads;
        
        Scanner input = new Scanner(new File(file));
        try {
            for(int r = 0; input.hasNextLine() && r < limit; r++) {
                Scanner line = new Scanner(input.nextLine());
                try {

                	ArrayList<Position> row = new ArrayList<Position>();
                	grid.add(row);

                	System.out.println ("Row " + r);
		    
                	for(int c = 0; line.hasNextInt() && c < limit; c++) {
                		Position position = new Position (r, c, line.nextInt ());		    	
                		row.add (position);
                		graph.addVertex(position);
                		positionVertexAt(position, position.column * 5, position.row * 5);
		    	
                	}
                }
		finally {
		    line.close();
		}
	    }
	}
	finally	{
	    input.close();
	}
	
	graphGrid(grid);
    
	//ArrayList<ArrayList<Position>> grid2 = transpose(grid);
	//outputGrid(grid2);


  }
    private void outputGrid(ArrayList<ArrayList<Position>> grid)
    {
    	for(int i = 0; i <grid.size();i++)
    	{
    		for (int j = 0; j <grid.get(0).size();j++)
    		{
    			System.out.print(grid.get(i).get(j).value + " ");
    		}
    		System.out.println("");
    	}
    }
    private void graphGrid(ArrayList<ArrayList<Position>> grid) throws InterruptedException
    {
    	System.out.println("Size of file by rows = " + grid.size() + ", columns = " + grid.get(0).size());
    	final long current= System.currentTimeMillis();
    	//System.out.println("Time for parsing file: " + ((current - startParseTime) / 1000) + " sec");
    	//long afterEdge = 0;
    	//long timeFindEdge = 0;
    	
    	
    	ArrayList<Integer> ranges = findRanges();
    	final Cell <Integer> threadsActive = new Cell <Integer> (threads);
        class EdgeThread extends Thread {

        	int startRow = 0;
        	int endRow = 0;
        	public EdgeThread(int startRow, int endRow)
        	{
        		this.startRow = startRow;
        		this.endRow = endRow;
        	}
        	
        	public void run()
        	{
        		System.out.println("Thread: " + this.getId());
        		findEdges(startRow, endRow);
        		boolean lastThread;
        		synchronized (threadsActive) { lastThread = -- threadsActive.object == 0; }
        		if (lastThread) {
        			System.out.println("Last Thread: " + this.getId());
        	    	long afterEdge= System.currentTimeMillis();
        			long timeFindEdge = (afterEdge - current) / 1000;
        			System.out.println("Time for findEdges: " + timeFindEdge + " sec");
        			
        	    	colorSCC();
        	    	long afterColor = System.currentTimeMillis();
        	    	long timeForColor = (afterColor - afterEdge) / 1000;
        	    	System.out.println("Time for coloring and find CC: " + timeForColor + " sec");
        	    	//findConnectedComponents();
        	    	create();
        	    	long timeForCreate = (System.currentTimeMillis() - afterColor) / 1000;
        	    	System.out.println("Time for creating map: " + timeForCreate + " sec") ;
        		}
        	}
        }  
    	for(int i = 0; i < threads; i++)
    	{
    		EdgeThread thread = new EdgeThread(ranges.get(2*i), ranges.get(2*i + 1));
    		thread.start();
    	}
    	
    	Thread.currentThread().join();

    }
    private class Cell <a> { public a object; public Cell (a object) { this.object = object; } }
    private ArrayList<Integer> findRanges()
    {
    	int rowsPerRange = (int) Math.ceil((grid.size())/ (double)threads);
    	ArrayList<Integer> ranges = new ArrayList<Integer>();
    	int startRow =0, endRow = rowsPerRange;
    	for(int i = 0; i < threads; i++)
    	{
    		ranges.add(startRow);
    		ranges.add(endRow);
    		startRow = endRow;;
    		endRow += rowsPerRange;
    	}
    	ranges.set(ranges.size()-1, Math.min(grid.size(), ranges.get(ranges.size() - 1)));
    	System.out.println(ranges);
    	return ranges;
    }
    private static <a> ArrayList <ArrayList <a>> transpose (ArrayList <ArrayList <a>> matrix)
    {
        ArrayList<ArrayList<a>> grid2 = new ArrayList<ArrayList<a>>();
        if(matrix.isEmpty())
        	return grid2;
       for(int i = 0; i < matrix.get(0).size();i++)
       {
    	   
    	   grid2.add(new ArrayList<a> (matrix.size()));
    	   
       }
       System.out.println(grid2);
    	for(int i = 0; i < matrix.size(); i++)
    	{

    		ArrayList<a> row = matrix.get(i);
    		for(int j = 0; j < row.size(); j++)
    		{

    			grid2.get(j).add(row.get(j));//.set(i, row.get(j));
    		}
    	}
        
    	return grid2;
    	
    }
    
    private void findEdges(int startRow, int endRow)
    {
	int gridSize = grid.size();
	int sizeOfArrays = grid.get(0).size();
	for(int i = startRow; i < endRow; i++) {
		    
		
	    for(int j = 0; j < sizeOfArrays;j++) {

		
		Position currentPosition = grid.get(i).get(j);
		int current = currentPosition.value;
		int min = current;
		Position minP = currentPosition;

		if(i-1 >= 0) {
		    Position north = grid.get(i-1).get(j);
		    if (north.value < min) {
			min = north.value;
			minP = north;
		    }
		}
				
		if(i-1 >= 0 && j-1 >= 0) {
		    Position northWest = grid.get(i-1).get(j-1);
		    if(northWest.value < min) {
			min = northWest.value;
			minP = northWest;
		    }
		}
				
		if(j-1 >= 0) {
		    Position west = grid.get(i).get(j-1);
		    if(west.value < min) {
			    min = west.value;
			    minP = west;
			}
		}
		if(i+1 < gridSize && j-1 >= 0) {
		    Position southWest = grid.get(i+1).get(j-1);
		    if(southWest.value < min) {
			min = southWest.value;
			minP = southWest;
		    }
		}
		if(i+1 < gridSize) {
		    Position south = grid.get(i+1).get(j);
		    if(south.value < min) {
			min = south.value;
			minP = south;
		    }
		}
		if(i+1 < gridSize && j+1 < sizeOfArrays) {
		    Position southEast = grid.get(i+1).get(j+1);
		    if(southEast.value < min) {
			min = southEast.value;
			minP = southEast;
		    }
		}
		if(j+1 < sizeOfArrays) {
		    Position east = grid.get(i).get(j+1);
			
		    if(east.value < min) {
			min = east.value;
			minP = east;
		    }
		}
				
		if( i-1 >= 0 && j+1 < sizeOfArrays) {
		    Position northEast = grid.get(i-1).get(j+1);
		    if(northEast.value< min) {
			min = northEast.value;
			minP = northEast;
		    }
		}

		if(currentPosition.equals (minP)) { }
		else {
		    synchronized(graph) {graph.addEdge(currentPosition, minP);}
		}

	    }
	}
		
    }
   /* private void colorComponent(Position p, Color color)
    {
    	//System.out.println(p.toString2());
    	if(p.visited)
    		return;
    	
    	p.visited = true;
    
    	setVertexColor(p, color);
    	
    	Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(p);
    	Set<DefaultEdge> outgoingEdges = graph.outgoingEdgesOf(p);
    	
    	for(DefaultEdge inEdge : incomingEdges)
    	{
    		colorComponent(graph.getEdgeSource(inEdge), color);
    	}
    	for(DefaultEdge outEdge : outgoingEdges)
    	{
    		colorComponent(graph.getEdgeTarget(outEdge), color);
    	}
    	
    	
    }
    private void findConnectedComponents()
    {
    	java.util.Set<Position> vSet = graph.vertexSet();

    	int componentIndex = 0;
    	
    	for(Position p: vSet)
    	{
    		if(!p.visited)
    		{
    			Color color = new Color(randGen.nextInt());
    			colorComponent(p, color);
    		}
    	}
    }*/
   // private void colorSCC (ArrayList<Position> vertexSet, Color color)
    private void colorSCC ()
    {
    	/*
	    
    	for (Position p:vertexSet)
	    {
	    	setVertexColor (p, color);
	    
	    }*/
    	
    	java.util.List <Set <Position>> sccs = new ConnectivityInspector<Position, DefaultEdge>(graph).connectedSets ();
    	for (Set <Position> s:sccs) {
    	    Color color = new Color (randGen.nextInt ());
    	    for (Position p:s) {
    		setVertexColor (p, color);
    	    }
    	}
    }

    private void setVertexColor (Object vertex, Color color)
    {

	DefaultGraphCell cell = m_jgAdapter.getVertexCell( vertex );
	AttributeMap attr =  cell.getAttributes(  );

	GraphConstants.setBackground(attr, color);
	

	AttributeMap cellAttr = new AttributeMap();

	cellAttr.put( cell, attr );
	    
	m_jgAdapter.edit (cellAttr, null, null, null);

    }


    private void positionVertexAt( Object vertex, int x, int y )
    {

	DefaultGraphCell cell = m_jgAdapter.getVertexCell( vertex );
        AttributeMap attr =  cell.getAttributes(  );
        Rectangle2D  b    =  GraphConstants.getBounds( attr );
        
        Rectangle2D newBounds = new Rectangle2D.Double(x,y, 5, 5);
        GraphConstants.setBounds( attr, newBounds);

        AttributeMap cellAttr = new AttributeMap();
        
        cellAttr.put( cell, attr );

        m_jgAdapter.edit (cellAttr, null, null, null);

    }
 

}
