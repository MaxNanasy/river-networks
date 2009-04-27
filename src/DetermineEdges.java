
import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
//java DetermineEdges realData.txt 6000 6000 -Xms64m -Xmx256m
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
    //private static int[] colors;
    private static int threads = 1;
    private static String fileName ="";
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

    private static Integer colorIndex = 0; //keeps track of the number of colors there are;
    private static final int VISITED = 10;
    private static Random randGen = new Random ();
    
    //arguments
    //	1: fileName
    //	2. ROW_SIZE
    //  3. ROW_INDEX_MAX (or column size)
    //	4. Number of threads (default is 1);
	public static void main(String args[]) throws Exception
	{
        try {
 
            fileName = args[0];
            ROW_SIZE = Integer.parseInt(args[1]);
            ROW_INDEX_MAX = Integer.parseInt(args[2]);
            
            if(args.length > 3){threads = Integer.parseInt(args[3]);}
         
            
            if(ROW_INDEX_MAX < 3)
            {
            	System.out.println("Please provide a file with 3 or more rows");
            	System.exit(-1);
            }           
            
            /*outFile = new FileOutputStream(args[0]+".edges");
            fileData = new PrintStream( outFile );
            inputFile = new File(args[0]);
            s = new Scanner(inputFile);  */ 
           
       
        }
        
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("init: Errors accessing output file: "+args[0]+".edges");
            System.exit(-2);
        }
         data = new int[ROW_SIZE][ROW_INDEX_MAX];
        row1 = new ArrayList<Integer>();
        row2 = new ArrayList<Integer>();
        row3 = new ArrayList<Integer>();
        
        //testForCycle();
        
       
        //System.out.println("=============PARSING FILE=================");
        //parseFile();
       
        System.out.println("=============LOADING EDGE DATA INTO MEMORY=================");
        loadEdgeData(args[0]+".edges"); 
        
        System.out.println("=============FINDING CC=================");
        if(threads>1)
        { 
        	System.out.println("Running multithreaded application....");
        	startThreads();
        }
        else
		{
        	findCC();
		}
        
        outputCCDataAndCreateImage();
		
	}
	
	private static void outputCCDataAndCreateImage()
	{
		System.out.println("=============OUTPUTTING CC INTO FILE=================");
        
        outputCCData(fileName);
       // System.out.println("=============MAKING IMAGE!!=================");
        
        //createImage(fileName);
        System.out.println("=============DONE DONE DONE!!=================");
	}
	/*
	private static void testForCycle()
	{
		int col = 0;
		int row = 0;
		int temp = 0;
		for(int i = 0; i < ROW_SIZE; i++)
		{
			for(int j = 0; j < ROW_INDEX_MAX; j++)
			{
				data[i][j] = s.nextInt();
				if( j == 4324 && i == 5999)
					
				if(j == 4325 && i == 5999)
					System.out.println("col: " + col +" row: " + row + " value: " + temp);
					
			}
			if(i%1000 == 0)
				System.out.println("parsed 1000 numbers");
		}
		System.out.println(data[5996][4323] + " " + data[5996][4324] +" "+ data[5996][4325] +" "+ data[5996][4326]);
		System.out.println(data[5997][4323] + " " + data[5997][4324] +" "+ data[5997][4325] +" "+ data[5997][4326]);
		System.out.println(data[5998][4323] + " " + data[5998][4324] +" "+ data[5998][4325] +" "+ data[5998][4326]);
		System.out.println(data[5999][4323] + " " + data[5999][4324] +" "+ data[5999][4325] +" "+ data[5999][4326]);
	}*/
	
    private static class Cell <a> { public a object; public Cell (a object) { this.object = object; } }
    
	private static void startThreads() throws Exception
	{
	//	final int threads = 0;
	    	ArrayList<Integer> ranges = findRanges();
	   	final Cell <Integer> threadsActive = new Cell <Integer> (threads);
		class FindCCThread extends Thread {

        	int startRow = 0;
        	int endRow = 0;
        	
        	public FindCCThread(int startRow, int endRow)
        	{
        		this.startRow = startRow;
        		this.endRow = endRow;
        		
        	}
        	
        	public void run()
        	{
        		System.out.println("Thread: " + this.getId() + ": running...");
        		findCC(startRow, endRow, (int)this.getId());
        		
        		boolean lastThread;
        		synchronized (threadsActive) { lastThread = -- threadsActive.object == 0; }
        		if (lastThread) {
        			System.out.println("Last thread: "+this.getId()+" finishing up...");
        			outputCCDataAndCreateImage();
					System.exit(0);
        			
        		}
        	}
        }  
    	
		// create threads
		for(int i = 0; i < threads; i++)
    	{
    		FindCCThread thread = new FindCCThread(ranges.get(2*i), ranges.get(2*i + 1));
    		thread.start();
    	}
		
    	/*FindCCThread midthread = new FindCCThread(2997,3000);
    	midthread.start();
    	
    	FindCCThread endThread = new FindCCThread(5997, 6000);
    	endThread.start();
    	*/
    	Thread.currentThread().join();
	}
	private static void createImage(String fileName)
	{
		Scanner s;
		File file;
		BitmapWriter imageCreator;
		Color[] colorArray = new Color[colorIndex];
		try
		{
			file = new File(fileName+".CC");
			s = new Scanner(file);
			

			
				imageCreator = new BitmapWriter(fileName+".bmp", ROW_SIZE, ROW_INDEX_MAX);
	
		
			//String tempS = "";
			Color colorTemp;
			while(s.hasNext())
			{
				int tempN = s.nextInt();
				if(colorArray[tempN] != null)
					colorTemp = colorArray[tempN];
				else
				{
					colorArray[tempN] = new Color(randGen.nextInt());
					colorTemp = colorArray[tempN];
				}
				//colorTemp = new Color(s.nextInt() * 30);
				imageCreator.writePixel(colorTemp);
			
			}
			
			s.close();
			imageCreator.close();
		}
		catch(Exception e)
		{
			System.out.println("File " + fileName+".CC could not be open.");
		}
		System.out.println("Image " + fileName + ".bmp was created.");
		
	}
	private static ArrayList<Integer> findRanges()
    {
    	int rowsPerRange = (int) Math.ceil((ROW_SIZE)/ (double)threads);
    	ArrayList<Integer> ranges = new ArrayList<Integer>();
    	int startRow =0, endRow = rowsPerRange;
    	for(int i = 0; i < threads; i++)
    	{
    		ranges.add(startRow);
    		ranges.add(endRow);
    		startRow = endRow;;
    		endRow += rowsPerRange;
    	}
    	ranges.set(ranges.size()-1, Math.min(ROW_SIZE, ranges.get(ranges.size() - 1)));
    	System.out.println("Ranges: " + ranges);
    	return ranges;
    }
	public static void outputCCData(String fileName)
	{
		
		FileOutputStream outFile1;
		PrintStream fileData1;
		String temp ="";
		try{
			outFile1 = new FileOutputStream(fileName+".CC");
			fileData1 = new PrintStream( outFile1 );
			current_time = System.currentTimeMillis();
			for(int i =0; i < ROW_SIZE; i++)
			{
				//System.out.println("data: " + i + " : " +data[i][0]);
				temp = Integer.toString(data[i][0]).substring(2);
				fileData1.print(temp +" ");
				for(int j = 1; j <ROW_INDEX_MAX -1; j++)
				{
					temp = Integer.toString(data[i][j]).substring(2);
					fileData1.print(temp + " ");
				}
				temp = Integer.toString(data[i][ROW_INDEX_MAX-1]).substring(2);
				fileData1.println(temp);
				
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
		catch (StringIndexOutOfBoundsException e)
		{
			System.out.println(temp);
		}
	}
	public static void findCC()
	{
		current_time =  System.currentTimeMillis();
		for(int i = 0; i < ROW_SIZE; i++)
		{
			for(int j = 0; j < ROW_INDEX_MAX; j++)
			{
				searchCC(i, j);
			}
					
			after_time = System.currentTimeMillis();
			elapsed_time = (after_time - current_time) / 1000;
			System.out.println("Time to find CC for 1000 items (item " + i+") : "+elapsed_time + "seconds.");
			current_time = after_time;
			
		}

	}
	public static void findCC(int start, int end, int threadID)
	{
		long current_time = 0;
		long after_time = 0;
		long elapsed_time = 0;
		current_time = System.currentTimeMillis();
		
		
		for(int i = start; i < end; i++)
		{
			for(int j = 0; j < ROW_INDEX_MAX; j++)
			{
				searchCC(i, j);
			}
					
			after_time = System.currentTimeMillis();
			elapsed_time = (after_time - current_time) / 1000;
			System.out.println(threadID+ ": Time to find CC for 1000 items (row " + i+") : "+elapsed_time +" seconds.");
			current_time = after_time;
			
		}	
		
	}

	private static void searchCC(int row, int column)
	{
		System.out.println("===========================================================");
		int rowIndex = row;
		int columnIndex = column;
		int edge;
		synchronized(data) {edge = data[rowIndex][columnIndex];}
		
		int[] ccRows = new  int[360000];
		int[] ccColumns= new  int[360000];
		int[] ccValues = new int [360000];
		int ccIndex = 0;
		
		boolean end = false;
		int colorIndexTemp = 1;

		String colorTracker ="";
	
		
		boolean isVisited = false;
		while(!end)
		{
			//System.out.println("colIndex: " + columnIndex + " rowIndex: " + rowIndex + " ccIndex: " + ccIndex);
			//System.out.println("rowIndex: " + rowIndex + " columnIndex: "+ columnIndex);
			if(edge >= VISITED)
			{
				
				String tempString = Integer.toString(edge);
				colorTracker = tempString.substring(2);				
				edge = VISITED; //make it go to case VISITED.			
				
				if(colorIndex<1)
					synchronized(colorIndex) {colorIndex = 1;}
					
				isVisited = true;
				end = true;
			}
			else
			{
				switch(edge)
				{
					case TOP_LEFT:
						edge = edge+VISITED; //make it visited
						
						ccRows[ccIndex] = rowIndex;
						ccColumns[ccIndex] = columnIndex;
						//colorTracker = Integer.toString(colorIndexTemp);
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
						//colorTracker = Integer.toString(colorIndexTemp);
						ccValues[ccIndex] = edge;
						rowIndex--;
						ccIndex++;
						break;
					case TOP_RIGHT:
						edge = edge+VISITED; //make it visited
						//ccIndex++;
						ccRows[ccIndex] = rowIndex;
						ccColumns[ccIndex] = columnIndex;
						//colorTracker = Integer.toString(colorIndexTemp);
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
						//colorTracker = Integer.toString(colorIndexTemp);
						ccValues[ccIndex] = edge;
						columnIndex++;
						ccIndex++;
						break;
					case BOTTOM_RIGHT:
						edge = edge+VISITED; //make it visited
						ccRows[ccIndex] = rowIndex;
						ccColumns[ccIndex] = columnIndex;
						//colorTracker = Integer.toString(colorIndexTemp);
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
						//colorTracker = Integer.toString(colorIndexTemp);
						ccValues[ccIndex] = edge;
						rowIndex++;
						ccIndex++;
						break;
					case BOTTOM_LEFT:
						edge = edge+VISITED; //make it visited
						//ccIndex++;
						ccRows[ccIndex] = rowIndex;
						ccColumns[ccIndex] = columnIndex;
						//colorTracker = Integer.toString(colorIndexTemp);
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
						//colorTracker = Integer.toString(colorIndexTemp);
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
						//colorTracker = Integer.toString(colorIndexTemp);
						ccValues[ccIndex] = edge;
						ccIndex++;
						end = true;
						break;
				}
				
				synchronized(data) {edge = data[rowIndex][columnIndex];} //travel to along edge to next vertex.
			}
			
		}
		if(!isVisited)
		{
			//System.out.println("Not visited!");
			synchronized(colorIndex) {colorIndex++; colorIndexTemp = colorIndex;}
			
			colorTracker = Integer.toString(colorIndexTemp);
		}
		
		//coloring
		for(int i = 0; i < ccIndex; i++)
		{
			//uncomment line below if you want values+ color
			String temp = ccValues[i] + colorTracker;
			
			//String temp = colorTracker; //only keep track of colors.
			
			int edgeTemp = Integer.parseInt(temp);
			//System.out.println("TEMPVALUE: " + temp);
			//System.out.println("int value: " + edgeTemp);
			synchronized(data) {data[ccRows[i]][ccColumns[i]] = edgeTemp;}
			
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
		long current_time_temp = System.currentTimeMillis();
		parseFirstLine();
		
		
		//advanceParser();
		parse();
		
		parseLastLine();
	       fileData.close();
	        s.close();
			
		after_time = System.currentTimeMillis();
		elapsed_time = (after_time - current_time_temp) / 1000;
		System.out.println("Time to parse file " + fileName + ": " + elapsed_time + " seconds.");
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
			//int max = 0;
			int neighbor =0;
			int min = 0;
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
			
			min = current;
			//max = current;
			if(min > top)
			{
				min = top;
				neighbor = TOP;
			}
			if(min > topRight)
			{
				min = topRight;
				neighbor = TOP_RIGHT;
			}
			if(min > right)
			{
				min = right;
				neighbor = RIGHT;
			}
			if(min > btmRight)
			{
				min = btmRight;
				neighbor = BOTTOM_RIGHT;
			}
			if(min > btm)
			{
				min = btm;
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
				min = current;
				
				if(min > topLeft)
				{
					min = topLeft;
					neighbor = TOP_LEFT;
				}
				if(min > top)
				{
					min = top;
					neighbor = TOP;
				}
				if(min > topRight)
				{
					min = topRight;
					neighbor = TOP_RIGHT;
				}
				if(min > right)
				{
					min = right;
					neighbor = RIGHT;
					
				}
				if(min > btmRight)
				{
					min = btmRight;
					neighbor = BOTTOM_RIGHT;
				}
				if(min > btm)
				{
					min = btm;
					neighbor = BOTTOM;
				}
				if(min > btmLeft)
				{
					min = btmLeft;
					neighbor = BOTTOM_LEFT;
				}
				if(min > left)
				{
					min = left;
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
			
			min = current;
			
			if(min > btm)
			{
				min = btm;
				neighbor = BOTTOM;
			}
			if(min > btmLeft)
			{
				min = btmLeft;
				neighbor = BOTTOM_LEFT;
			}
			if(min > left)
			{
				min = left;
				neighbor = LEFT;
			}
			if(min > topLeft)
			{
				min = topLeft;
				neighbor = TOP_LEFT;
			}
			if(min > top)
			{
				min = top;
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
		int min = 0;
		
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
		
		min = current;
		if(min > top)
		{
			min = top;
			neighbor = TOP;
		}
		if(min > topRight)
		{
			min = topRight;
			neighbor = TOP_RIGHT;
		}
		if(min > right)
		{
			min = right;
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
			right = row3.get(i+1);
		    //btmRight = row2.get(i+1);
			//btm = row2.get(i);
			//btmLeft = row2.get(i-1);
			left = row3.get(i-1);
			
			min = current;
			
			if(min > topLeft)
			{
				min = topLeft;
				neighbor = TOP_LEFT;
				
			}
			if(min> top)
			{
				min = top;
				neighbor = TOP;
			}
			if(min >topRight)
			{
				min = topRight;
				neighbor = TOP_RIGHT;
			}
			if(min >right)
			{
				min = right;
				neighbor = RIGHT;
			}
			if(min > left)
			{
				min = left;
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
		min = current;
		
		if(min > topLeft)
		{
			min = topLeft;
			neighbor = TOP_LEFT;
		}
		if(min > top)
		{
			min = top;
			neighbor = TOP;
		}
		if(min > left)
		{
			min = left;
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
		
		//int max = 0;
		int min = 0;
		//doing first number
		int current = row1.get(0);
		int right = row1.get(1);
		int btmRight = row2.get(1);
		int btm = row2.get(0);
		int btmLeft = 0;
		int left = 0;
		int neighbor =0;
		
		min = current;
		//max = current;
		if(min > right)
		{
			min = right;
			neighbor = RIGHT;
		}
		if(min > btmRight)
		{
			min = btmRight;
			neighbor = BOTTOM_RIGHT;
		}
		if(min > btm)
		{
			min = btm;
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
			
			min = current;
			
			if(min > right)
			{
				min = right;
				neighbor = RIGHT;
				
			}
			if(min > btmRight)
			{
				min = btmRight;
				neighbor = BOTTOM_RIGHT;
			}
			if(min > btm)
			{
				min = btm;
				neighbor = BOTTOM;
			}
			if(min > btmLeft)
			{
				min = btmLeft;
				neighbor = BOTTOM_LEFT;
			}
			if(min > left)
			{
				min = left;
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
		min = current;
		
		if(min > btm)
		{
			min = btm;
			neighbor = BOTTOM;
		}
		if(min > btmLeft)
		{
			min = btmLeft;
			neighbor = BOTTOM_LEFT;
		}
		if(min > left)
		{
			min = left;
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
