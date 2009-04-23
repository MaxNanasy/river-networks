import java.io.*;
import java.awt.*;

public class BitmapWriter
{

    FileOutputStream output;

    // modified from http://forums.sun.com/thread.jspa?messageID=3517577#3517577
    static byte [] intToBytesLE (int integer)
    {
	byte[] bytes = new byte[4];
	bytes[0] = (byte) ( integer        & 0xFF);
	bytes[1] = (byte) ((integer >> 8 ) & 0xFF);
	bytes[2] = (byte) ((integer >> 16) & 0xFF);
	bytes[3] = (byte) ((integer >> 24) & 0xFF);
	return bytes;
    }

    public BitmapWriter (String fileName, int height, int width) throws IOException
    {
	int size = height * width;
	output = new FileOutputStream (fileName);
	output.write (new byte [] {0x42, 0x4D });         // magic number
	output.write (intToBytesLE (14 + 40 + 3 * size)); // size of file
	output.write (new byte [] { 0, 0, 0, 0            // reserved
				  , 14 + 40, 0, 0, 0      // offset of data
				  , 40, 0, 0, 0           // size of DIB header
	                          });
	output.write (intToBytesLE ( width ));            // bitmap width
	output.write (intToBytesLE (-height));    	  // bitmap height
	output.write (new byte [] { 1, 0                  // number of color planes (must be 1)
			          , 24, 0                 // bits per pixel
			          ,  0, 0, 0, 0           // compression method
	                          });
	output.write (intToBytesLE (size));               // image size
	output.write (new byte [] { 0, 0, 0, 0            // horizontal resolution (pix/m)
			          , 0, 0, 0, 0            // vertical   resolution (pix/m)
			          , 0, 0, 0, 0            // number of colors in color palette
			          , 0, 0, 0, 0            // number of important colors used
	                          });
    }

    public void writePixel (Color color) throws IOException
    {
	byte colorData [] = { (byte) color.getBlue (), (byte) color.getGreen (), (byte) color.getRed () };
	output.write (colorData); //intToBytesLE (colorData)); //color.getRGB ()));
    }

    public void close () throws IOException
    {
	output.close ();
    }

    public static void main (String barges []) throws Exception
    {
	BitmapWriter bw = new BitmapWriter (barges [0], 65536, 256);
	for (int i = 0; i < 16777216; i++) bw.writePixel (new Color (i));
	bw.close ();
    }

}
