
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
//import java.util.Random;
import java.util.Scanner;
//import java.util.Set;




public class DetermineEdges {
    private static FileOutputStream outFile;
    private static PrintStream fileData;
    private static File inputFile;
    private static Scanner s;
    private static int rowIndex = 0;
    private static int ROW_SIZE = 6000;
    private static int ROW_INDEX_MAX = 6000;
    private static ArrayList<Integer> row1;
    private static ArrayList<Integer> row2;
    private static ArrayList<Integer> row3;
    private static int[][] data;
    private static int[] colors;
    // private static int row = 0, column = 0;
    
    private static long current_time = 0, elapsed_time = 0, after_time =0;
    private static final int 
    	TOP_LEFT = 1,
    	TOP =2,
    	TOP_RIGHT =3,
    	RIGHT =4,
    	BOTTOM_RIGHT = 5,
    	BOTTOM = 6,
    	BOTTOM_LEFT =7,
    	LEFT = 8;	

    private static int colorIndex = 0; //keeps track of the number of colors there are;
    private static final int VISITED = 10;
    
    public static void main(String args[])
    {
        try {
 
            
            ROW_SIZE = Integer.parseInt(args[1]);
            ROW_INDEX_MAX = Integer.parseInt(args[2]);
         
            
            if(ROW_INDEX_MAX < 3)
		{
		    System.out.println("Please provide a file with 3 or more rows");
		    System.exit(-1);
		}           
            
            /*outFile = new FileOutputStream(args[0]+".edgess");
	      fileData = new PrintStream( outFile );
	      inputFile = new File(args[0]);
	      s = new Scanner(inputFile);   */
            data = new int[ROW_SIZE][ROW_INDEX_MAX];
       
        }
        
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("init: Errors accessing output file: "+args[0]+".edgess");
            System.exit(-2);
        }
        
        row1 = new ArrayList<Integer>();
        row2 = new ArrayList<Integer>();
        row3 = new ArrayList<Integer>();
        //System.out.println("=============PARSING FILE=================");
        //parseFile();
        System.out.println("=============LOADING EDGE DATA INTO MEMORY=================");
        loadEdgeData(args[0]+".edges"); 
        System.out.println("=============FINDING CC=================");
        findCC();
        System.out.println("=============OUTPUTTING CC INTO FILE=================");
        outputCCData(args[0]);
        
        System.out.println("=============DONE DONE DONE!!=================");


    }
    public static void outputCCData(String fileName)
    {
		
	FileOutputStream outFile1;
	PrintStream fileData1;
	try{
	    outFile1 = new FileOutputStream(fileName+".CC");
	    fileData1 = new PrintStream( outFile1 );
	    current_time = System.currentTimeMillis();
	    for(int i =0; i < ROW_SIZE; i++)
		{
		    fileData1.print(data[i][0] +" ");
		    for(int j = 1; j <ROW_INDEX_MAX -1; j++)
			{
			    fileData1.print(data[i][j] + " ");
			}
		    fileData1.println(data[i][ROW_INDEX_MAX-1]);
				
		    if(i%1000 ==0)
			{
			    after_time = System.currentTimeMillis();
			    elapsed_time = (after_time - current_time) / 1000;
			    System.out.println("Time to output CC data into file for 1000 rows: "+elapsed_time);
			    current_time = after_time;
			}

		}	
	    fileData1.close();
	    outFile1.close();
	    System.out.println("ColorIndex = : " +colorIndex);
			
	}
	catch(IOException e)
		
	
	    {
		System.out.println("File could not be accessed.");
	    }		
    }
    public static void findCC()
    {
	current_time = System.currentTimeMillis();
	for(int i = 0; i < ROW_SIZE; i++)
	    {
		for(int j = 0; j < ROW_INDEX_MAX; j++)
		    {
			searchCC(i, j);
		    }
					
		after_time = System.currentTimeMillis();
		elapsed_time = (after_time - current_time) / 1000;
		System.out.println("Time to find CC for 1000 items (item " + i+") : "+elapsed_time);
		current_time = after_time;
			
	    }

    }
    private static void searchCC(int row, int column)
    {
	int rowIndex = row;
	int columnIndex = column;
	int edge = data[rowIndex][columnIndex];
		
	int[] ccRows = new  int[360000];
	int[] ccColumns= new  int[360000];
	int[] ccValues = new int [360000];
	int ccIndex = 0;
		
	boolean end = false;
		
	//System.out.println("FIRST EDGE: " + edge);
	//int multiplier = 10;
	String colorTracker ="";
	//if the start of the cc is not visited, then make a new color index;
	if(edge < VISITED)
	    {
		colorIndex++;
	    }
		
	while(!end)
	    {
		//System.out.println("rowIndex: " + rowIndex + " columnIndex: "+ columnIndex);
		if(edge >= VISITED)
		    {
			//System.out.println("ALREADY VISITED!");
			//System.out.println("original edge: " + edge);
			String tempString = Integer.toString(edge);
			colorTracker = tempString.substring(2);
			//System.out.println("color from edge: " + colorTracker);
				
			edge = VISITED; //make it go to case VISITED.			
			colorIndex = colorIndex - 1; //revert the colorIndex back since everything will be turned into color of the already visited node.
			if(colorIndex<1)
			    colorIndex = 1;
		    }
			
					
		switch(edge)
		    {
		    case TOP_LEFT:
			edge = edge+VISITED; //make it visited
					
			ccRows[ccIndex] = rowIndex;
			ccColumns[ccIndex] = columnIndex;
			colorTracker = Integer.toString(colorIndex);
			ccValues[ccIndex] = edge;
			rowIndex--;
			columnIndex--;
			ccIndex++;
			break;
		    case TOP:
			edge = edge+VISITED; //make it visited
			//ccIndex++;
			ccRows[ccIndex] = rowIndex;
			ccColumns[ccIndex] = columnIndex;
			colorTracker = Integer.toString(colorIndex);
			ccValues[ccIndex] = edge;
			rowIndex--;
			ccIndex++;
			break;
		    case TOP_RIGHT:
			edge = edge+VISITED; //make it visited
			//ccIndex++;
			ccRows[ccIndex] = rowIndex;
			ccColumns[ccIndex] = columnIndex;
			colorTracker = Integer.toString(colorIndex);
			ccValues[ccIndex] = edge;
			rowIndex--;
			columnIndex++;
			ccIndex++;
			break;
		    case RIGHT:
			edge = edge+VISITED; //make it visited
			//ccIndex++;
			ccRows[ccIndex] = rowIndex;
			ccColumns[ccIndex] = columnIndex;
			colorTracker = Integer.toString(colorIndex);
			ccValues[ccIndex] = edge;
			columnIndex++;
			ccIndex++;
			break;
		    case BOTTOM_RIGHT:
			edge = edge+VISITED; //make it visited
			ccRows[ccIndex] = rowIndex;
			ccColumns[ccIndex] = columnIndex;
			colorTracker = Integer.toString(colorIndex);
			ccValues[ccIndex] = edge;
			rowIndex = rowIndex + 1;
			columnIndex = columnIndex + 1;
			ccIndex++;
			break;
		    case BOTTOM:
			edge = edge+VISITED; //make it visited
			//ccIndex++;
			ccRows[ccIndex] = rowIndex;
			ccColumns[ccIndex] = columnIndex;
			colorTracker = Integer.toString(colorIndex);
			ccValues[ccIndex] = edge;
			rowIndex++;
			ccIndex++;
			break;
		    case BOTTOM_LEFT:
			edge = edge+VISITED; //make it visited
			//ccIndex++;
			ccRows[ccIndex] = rowIndex;
			ccColumns[ccIndex] = columnIndex;
			colorTracker = Integer.toString(colorIndex);
			ccValues[ccIndex] = edge;
			rowIndex++;
			columnIndex--;
			ccIndex++;
			break;
		    case LEFT:
			edge = edge+VISITED; //make it visited
			//ccIndex++;
			ccRows[ccIndex] = rowIndex;
			ccColumns[ccIndex] = columnIndex;
			colorTracker = Integer.toString(colorIndex);
			ccValues[ccIndex] = edge;
			columnIndex--;
			ccIndex++;
			break;
		    case VISITED:
			end=true;
			break;
		    default: //case with no edges going out.
			//System.out.println("End of one CC: " + edge);
			edge = edge+VISITED; //make it visited
			//ccIndex++;
			ccRows[ccIndex] = rowIndex;
			ccColumns[ccIndex] = columnIndex;
			colorTracker = Integer.toString(colorIndex);
			ccValues[ccIndex] = edge;
			ccIndex++;
			end = true;
			break;
		    }
		//System.out.println("current Edge: "+edge);
		edge = data[rowIndex][columnIndex]; //travel to along edge to next vertex.
		//System.out.println("next Edge: "+edge);
	    }
		
	for(int i = 0; i < ccIndex; i++)
	    {

		String temp = ccValues[i] + colorTracker;
			
		int edgeTemp = Integer.parseInt(temp);
		//System.out.println("TEMPVALUE: " + temp);
		//System.out.println("int value: " + edgeTemp);
		data[ccRows[i]][ccColumns[i]] = edgeTemp;
			
	    }
		
    }
    public static void loadEdgeData(String fileName)
    {

		
        File inputFile2;
        Scanner s2;
	try
	    {

		inputFile2 = new File(fileName);
		s2 = new Scanner(inputFile2);
		current_time = System.currentTimeMillis();
		for(int i = 0; i <ROW_SIZE; i++)
		    {
			data[i][0] = s2.nextInt();

        	

			for(int j = 1; j < ROW_INDEX_MAX-1; j++)
			    {
				data[i][j] = s2.nextInt();
				;

     		   
			    }
			data[i][ROW_INDEX_MAX-1] = s2.nextInt();


			if(i%1000 == 0)
			    {
				after_time = System.currentTimeMillis();
				elapsed_time = (after_time - current_time ) /1000;
				current_time = after_time;
				System.out.println("Time it takes to read in 1000 rows: " + elapsed_time);
			    }
		    }
        
		s2.close();
		System.out.println(data.length);
	    }
	catch(IOException e)
	    {
    	   
	    }	
    }
    public static void parseFile()
    {
	current_time = 	System.currentTimeMillis();
	parseFirstLine();
		
		
	//advanceParser();
	parse();
		
	parseLastLine();
	fileData.close();
	s.close();
    }
    private static void parse()
    {
	while(rowIndex <= ROW_INDEX_MAX)
	    {
		if(rowIndex % 1000 == 0)
		    {
			after_time = System.currentTimeMillis();
				
			elapsed_time = (after_time - current_time)/ 1000;
			System.out.println("Time to parse + find edges for 1000 rows: " + elapsed_time + " seconds");
			current_time = after_time;
		    }
		int max = 0;
		int neighbor =0;
			
		//doing first number
		int current = row2.get(0);
		int topLeft = 0;
		int top = row1.get(0);
		int topRight = row1.get(1);
		int right = row2.get(1);
		int btmRight = row3.get(1);
		int btm = row3.get(0);
		int btmLeft = 0;
		int left = 0;
			
			
		max = current;
		if(max < top)
		    {
			max = top;
			neighbor = TOP;
		    }
		if(max < topRight)
		    {
			max = topRight;
			neighbor = TOP_RIGHT;
		    }
		if(max < right)
		    {
			max = right;
			neighbor = RIGHT;
		    }
		if(max < btmRight)
		    {
			max = btmRight;
			neighbor = BOTTOM_RIGHT;
		    }
		if(max < btm)
		    {
			max = btm;
			neighbor = BOTTOM;
		    }
		fileData.print(neighbor + " ");
		//data[row][0] = neighbor;
			
		// finding edges 1 - 5998
		for(int i = 1; i < ROW_SIZE - 1; i++)
		    {
			neighbor = 0;
			current = row2.get(i);
			topLeft = row1.get(i-1);
			top = row1.get(i);
			topRight = row1.get(i+1);
			right = row2.get(i+1);
			btmRight = row3.get(i+1);
			btm = row3.get(i);
			btmLeft = row3.get(i-1);
			left = row2.get(i-1);
			max = current;
				
			if(max<topLeft)
			    {
				max = topLeft;
				neighbor = TOP_LEFT;
			    }
			if(max < top)
			    {
				max = top;
				neighbor = TOP;
			    }
			if(max < topRight)
			    {
				max = topRight;
				neighbor = TOP_RIGHT;
			    }
			if(max < right)
			    {
				max = right;
				neighbor = RIGHT;
					
			    }
			if(max < btmRight)
			    {
				max = btmRight;
				neighbor = BOTTOM_RIGHT;
			    }
			if(max <btm)
			    {
				max = btm;
				neighbor = BOTTOM;
			    }
			if(max <btmLeft)
			    {
				max = btmLeft;
				neighbor = BOTTOM_LEFT;
			    }
			if(max < left)
			    {
				max = left;
				neighbor = LEFT;
			    }
				
			fileData.print(neighbor+ " ");
			//data[row][i] = neighbor;
		    }
			
		//find edge for last number
		current = row2.get(ROW_SIZE -1);
		btm = row3.get(ROW_SIZE-1);
		btmLeft = row3.get(ROW_SIZE -2);
		left = row2.get(ROW_SIZE-2);
		topLeft = row1.get(ROW_SIZE-2);
		top = row1.get(ROW_SIZE -1);
		neighbor =0;
		max = current;
			
		if(max < btm)
		    {
			max = btm;
			neighbor = BOTTOM;
		    }
		if(max < btmLeft)
		    {
			max = btmLeft;
			neighbor = BOTTOM_LEFT;
		    }
		if(max < left)
		    {
			max = left;
			neighbor = LEFT;
		    }
		if(max < topLeft)
		    {
			max = topLeft;
			neighbor = TOP_LEFT;
		    }
		if(max < top)
		    {
			max = top;
			neighbor = TOP;
		    }
		fileData.println(neighbor);
		//data[row][ROW_SIZE-1] = neighbor;
			
		if(rowIndex == ROW_INDEX_MAX)
		    { rowIndex++;}
		else
		    {
			advanceParser(); // read in new data
			//row++;
		    }
	    }
    }
	
    private static void parseLastLine()
    {
	int max = 0;
		
	//doing first number
	int current = row3.get(0);
	int topLeft = 0;
	int top = row2.get(0);
	int topRight = row2.get(1);
	int right = row3.get(1);
	//int btmRight = 0;
	//int btm = 0; 
	//int btmLeft = 0;
	int left = 0;
	int neighbor =0;
		
	max = current;
	if(max < top)
	    {
		max = top;
		neighbor = TOP;
	    }
	if(max < topRight)
	    {
		max = topRight;
		neighbor = TOP_RIGHT;
	    }
	if(max < right)
	    {
		max = right;
		neighbor = RIGHT;
	    }
	fileData.print(neighbor + " ");
	//data[ROW_SIZE-1][0] = neighbor;
		
	// finding edges 1 - 5998
	for(int i = 1; i < ROW_SIZE - 1; i++)
	    {
		neighbor = 0;
		current = row3.get(i);
		topLeft = row2.get(i-1);
		top = row2.get(i);
		topRight = row2.get(i+1);
		right = row1.get(i+1);
		//btmRight = row2.get(i+1);
		//btm = row2.get(i);
		//btmLeft = row2.get(i-1);
		left = row3.get(i-1);
		max = current;
			
		if(max < topLeft)
		    {
			max = topLeft;
			neighbor = TOP_LEFT;
				
		    }
		if(max < top)
		    {
			max = top;
			neighbor = TOP;
		    }
		if(max <topRight)
		    {
			max = topRight;
			neighbor = TOP_RIGHT;
		    }
		if(max <right)
		    {
			max = right;
			neighbor = RIGHT;
		    }
		if(max < left)
		    {
			max = left;
			neighbor = LEFT;
		    }
			
		fileData.print(neighbor+ " ");
		//data[ROW_SIZE-1][i] = neighbor;
	    }
		
	//find edge for last number
	current = row3.get(ROW_SIZE -1);
	topLeft = row2.get(ROW_SIZE -2);
	top = row2.get(ROW_SIZE -1);
	//btm = row2.get(ROW_SIZE-1);
	//btmLeft = row2.get(ROW_SIZE -2);
	left = row3.get(ROW_SIZE-2);
		
	neighbor =0;
	max = current;
		
	if(max < topLeft)
	    {
		max = topLeft;
		neighbor = TOP_LEFT;
	    }
	if(max < top)
	    {
		max = top;
		neighbor = TOP;
	    }
	if(max < left)
	    {
		max = left;
		neighbor = LEFT;
	    }
	fileData.println(neighbor);
	//data[ROW_SIZE-1][ROW_INDEX_MAX-1] = neighbor;
			
    }
	
    private static void parseFirstLine()
    {
	for(int i = 0; i < ROW_SIZE; i++)
	    {
		//System.out.println(s.nextInt());
		row1.add(s.nextInt());
	    }
	for(int i =0; i<ROW_SIZE;i++)
	    {
		row2.add(s.nextInt());
	    }
	for(int i = 0; i<ROW_SIZE;i++)
	    {
		row3.add(s.nextInt());
	    }
	rowIndex = 3;
		
	int max = 0;
		
	//doing first number
	int current = row1.get(0);
	int right = row1.get(1);
	int btmRight = row2.get(1);
	int btm = row2.get(0);
	int btmLeft = 0;
	int left = 0;
	int neighbor =0;
		
	max = current;
	if(max < right)
	    {
		max = right;
		neighbor = RIGHT;
	    }
	if(max < btmRight)
	    {
		max = btmRight;
		neighbor = BOTTOM_RIGHT;
	    }
	if(max < btm)
	    {
		max = btm;
		neighbor = BOTTOM;
	    }
	fileData.print(neighbor + " ");
	//data[0][0] = neighbor;
		
	// finding edges 1 - 5998
	for(int i = 1; i < ROW_SIZE - 1; i++)
	    {
		neighbor = 0;
		current = row1.get(i);
		right = row1.get(i+1);
		btmRight = row2.get(i+1);
		btm = row2.get(i);
		btmLeft = row2.get(i-1);
		left = row1.get(i-1);
		max = current;
			
		if(max < right)
		    {
			max = right;
			neighbor = RIGHT;
				
		    }
		if(max < btmRight)
		    {
			max = btmRight;
			neighbor = BOTTOM_RIGHT;
		    }
		if(max <btm)
		    {
			max = btm;
			neighbor = BOTTOM;
		    }
		if(max <btmLeft)
		    {
			max = btmLeft;
			neighbor = BOTTOM_LEFT;
		    }
		if(max < left)
		    {
			max = left;
			neighbor = LEFT;
		    }
			
		fileData.print(neighbor+ " ");
		//data[0][i] = neighbor;
	    }
		
	//find edge for last number
	current = row1.get(ROW_SIZE -1);
	btm = row2.get(ROW_SIZE-1);
	btmLeft = row2.get(ROW_SIZE -2);
	left = row1.get(ROW_SIZE-2);
	neighbor =0;
	max = current;
		
	if(max < btm)
	    {
		max = btm;
		neighbor = BOTTOM;
	    }
	if(max < btmLeft)
	    {
		max = btmLeft;
		neighbor = BOTTOM_LEFT;
	    }
	if(max < left)
	    {
		max = left;
		neighbor = LEFT;
	    }
	fileData.println(neighbor);
		
	//data[0][ROW_INDEX_MAX-1] = neighbor;
	//row++;
		
    }
	
    private static void advanceParser()
    {

	Collections.copy(row1, row2);
	Collections.copy(row2, row3);

	row3.clear();
		
	for(int j = 0; j< ROW_SIZE; j++)
	    {
		row3.add(s.nextInt());
	    }
		
	rowIndex++;
	if(rowIndex % 100 == 0)
	    System.out.println(rowIndex);
		
    }

 




}
