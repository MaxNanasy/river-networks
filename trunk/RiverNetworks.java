import java.io.FileNotFoundException;
import javax.swing.*;


public class RiverNetworks{

	/**
	 * @param args
	 */
	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		CreateImage creator = new CreateImage();
		int threads = Integer.parseInt(args[1]);
		int limit = Integer.parseInt(args[2]);
		creator.parseInput(args[0], threads, limit);
		
	}

}
