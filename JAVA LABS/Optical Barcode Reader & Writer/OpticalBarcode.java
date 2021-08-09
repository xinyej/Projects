/* Xinye Jiang (20477351) Solution
 * Assignment #3
 * Description: Build Optical Barcode and Text Readers and Writers
 * Date: 07/08/2021
 */

/**
 * OpticalBarcode program shows test runs of BarcodeImage, DataMatrix classes
 */
public class OpticalBarcode
{
   public static void main(String[] args)
   {
      String[] sImageIn =
      {
         "------------------------------",
         "|* * * * * * * * * * * * * * *|",
         "|*                            |",
         "|**** *** *******  ***** *****|",
         "|* **************** ********* |",
         "|** *  *   * *      *  * * * *|",
         "|***          **          * * |",
         "|* **   *  ** ***  * * * **  *|",
         "|* *   *   * **    **    **** |",
         "|**** * * ******** * **  ** **|",
         "|*****************************|",
         "------------------------------"
      };
      
      String[] sImageIn_2 =
      {
            "-----------------------------",
            "|* * * * * * * * * * * * * * |",
            "|*                          *|",
            "|*** ** ******** ** ***** ** |",
            "|*  **** ***************** **|",
            "|* *  *    *      *  *  *  * |",
            "|*       ** **** *          *|",
            "|*    * ****  **    * * * ** |",
            "|***    ***       * **    * *|",
            "|*** *   **  *   ** * **   * |",
            "|****************************|",
            "-----------------------------" 
      };
      
      BarcodeImage bc = new BarcodeImage(sImageIn);
      DataMatrix dm = new DataMatrix(bc);
      
      // First secret message
      dm.translateImageToText();
      dm.displayTextToConsole();
      dm.displayImageToConsole();
      
      // second secret message
      bc = new BarcodeImage(sImageIn_2);
      dm.scan(bc);
      dm.translateImageToText();
      dm.displayTextToConsole();
      dm.displayImageToConsole();
      
      // create your own message
      dm.readText("What a great resume builder this is!");
      dm.generateImageFromText();
      dm.displayTextToConsole();
      dm.displayImageToConsole();
   }
}

/**
 * Interface BarcodeIO
 * The interface contains method signatures to read, display, and translate 
 * (to each other) image and text related to that image.
 */
interface BarcodeIO
{
   // Method scan stores a copy of a BarcodeImage object, does nothing to text.
   public boolean scan(BarcodeImage bc);
   
   // Method readText stores a text string, does nothing to BarcodeImage.
   public boolean readText(String text);
   
   // Method generateImageFromText produces a BarcodeImage by internal text.
   public boolean generateImageFromText();
   
   // Method translateImageToText produces a text string by internal image.
   public boolean translateImageToText();
   
   // Method displayTextToConsole prints out the text string to the console.
   public void displayTextToConsole();
   
   // Method displayImageToConsole prints out the image to the console.
   public void displayImageToConsole();
}

/**
 * Class BarcodeImage (implements Cloneable)
 * The class stores and retrieves 2D data representing Barcode Image, and also 
 * has a clone method.
 */
class BarcodeImage implements Cloneable
{
   public static final int MAX_HEIGHT = 30;
   public static final int MAX_WIDTH = 65;
   public static final int NUM_BORDERS = 2;
   
   private boolean[][] imageData;
   
   /**
    * Default Constructor
    */
   public BarcodeImage()
   {
      // Default value of boolean elements in an array is false.
      imageData = new boolean[MAX_HEIGHT][MAX_WIDTH];
   }
   
   /**
    * Parameterized Constructor
    * @param strData A 1D String array needed to be converted to 2D booleans.
    */
   public BarcodeImage(String[] strData)
   {
      this();
      
      if(!checkSize(strData))
         return;
      // Pack the data into the lower-left corner of the 2D array
      else
      {
         int numStr = strData.length - NUM_BORDERS;
         int strLen = strData[1].length() - NUM_BORDERS;
         for(int i = 0; i < numStr; i++)
         {
            String str = strData[numStr - i];
            for(int j = 0; j < strLen; j++)
            {
               // false: white blanks, true: black *
               imageData[MAX_HEIGHT - 1 - i][j] = 
                     str.charAt(j + 1) == DataMatrix.BLACK_CHAR;
            }
         }
      }
   }
   
   /**
    * The checkSize method checks the data for every size or null error.
    * @param data A 1D String array needed to be checked.
    * @return Whether data is correct without size or null error.
    */
   private boolean checkSize(String[] data)
   {
      // Check whether the String array is null or too big
      if(data == null || data.length > MAX_HEIGHT + NUM_BORDERS)
         return false;
      // Check whether each element in the array is null or too big
      for(String s: data)
         if(s == null || s.length() > MAX_WIDTH + NUM_BORDERS)
            return false;
      return true;
   }
   
   /**
    * Accessor for the Pixel at the given position.
    */
   public boolean getPixel(int row, int col)
   {
      if(row >= 0 && row < MAX_HEIGHT && col >= 0 && col < MAX_WIDTH)
         return imageData[row][col];
      else
         return false;
   }
   
   /**
    * Mutator for the Pixel at the given position with given value.
    * @return Whether the setting is successful.
    */
   public boolean setPixel(int row, int col, boolean value)
   {
      if(row >= 0 && row < MAX_HEIGHT && col >= 0 && col < MAX_WIDTH)
      {
         imageData[row][col] = value;
         return true;
      }
      else
         return false;
   }
   
   /**
    * The clone method overrides the one in Cloneable interface.
    * @return A deep copy of the current object.
    */
   public Object clone() throws CloneNotSupportedException
   {
      BarcodeImage copy = (BarcodeImage) super.clone();
      copy.imageData = new boolean[MAX_HEIGHT][MAX_WIDTH];
      for(int i = 0; i < MAX_HEIGHT; i++)
         for(int j = 0; j < MAX_WIDTH; j++)
            copy.imageData[i][j] = this.imageData[i][j];
      return copy;
   }
}

/**
 * Class DataMatrix (implements BarcodeIO)
 * The class object stores pseudo data matrix. It has default constructor and 
 * constructors that takes a BarcodeImage or a text. It also implements the 
 * methods that can convert image and text to each other and display messages.
 */
class DataMatrix implements BarcodeIO
{
   public static final char BLACK_CHAR = '*';
   public static final char WHITE_CHAR = ' ';
   
   private BarcodeImage image;
   private String text;
   private int actualWidth;
   private int actualHeight;
   
   /**
    * Default Constructor
    */
   public DataMatrix()
   {
      image = new BarcodeImage();
      text = "";
      actualWidth = 0;
      actualHeight = 0;
   }
   
   /**
    * Parameterized Constructor
    * @param image The input image to construct all the instance members.
    */
   public DataMatrix(BarcodeImage image)
   {
      // this() initializes all instance members, leaves text default
      // If scan succeeds, correct image, width, height will cover old ones.
      this();
      scan(image);
   }
   
   /**
    * Parameterized Constructor
    * @param text The input text to construct all the instance members.
    */
   public DataMatrix(String text)
   {
      // this() initializes all instance members, leaves image default
      // If readText succeeds, correct text will cover the old one.
      this();
      readText(text);
   }
   
   /**
    * The scan method mutates image (clone), actualWidth and actualHeight.
    * @param bc The input barcode image.
    * @return Whether scan is successful.
    */
   public boolean scan(BarcodeImage bc)
   {
      if(bc == null)
         return false;
      
      try
      {
         image = (BarcodeImage) bc.clone();
      }
      catch (CloneNotSupportedException ex)
      {
         return false;
      }
      
      actualWidth = computeSignalWidth();
      actualHeight = computeSignalHeight();
      return true;
   }
   
   /**
    * The readText method mutates text if it is valid.
    * @param text The input string text.
    * @return Whether readText is successful.
    */
   public boolean readText(String text)
   {
      if(text.length() > BarcodeImage.MAX_WIDTH - BarcodeImage.NUM_BORDERS)
         return false;
      else
      {
         // Strings are immutable objects, so we can just copy reference
         this.text = text;
         return true;
      }
   }
   
   /**
    * Getter for actualWidth
    */
   public int getActualWidth()
   {
      return actualWidth;
   }
   
   /**
    * Getter for actualHeight
    */
   public int getActualHeight()
   {
      return actualHeight;
   }
   
   /**
    * The computeSignalWidth method gets the width of image by bottom spine.
    * @return The width of current image.
    */
   private int computeSignalWidth()
   {
      int width = 0;
      for(int col = 0; col < BarcodeImage.MAX_WIDTH; col++)
         if(image.getPixel(BarcodeImage.MAX_HEIGHT - 1, col))
            width++;
      return width;
   }
   
   /**
    * The computeSignalHeight method gets the height of image by left spine.
    * @return The height of current image.
    */
   private int computeSignalHeight()
   {
      int height = 0;
      for(int row = 0; row < BarcodeImage.MAX_HEIGHT; row++)
         if(image.getPixel(row, 0))
            height++;
      return height;
   }
   
   /**
    * The generateImageFromText method produces an image by the internal text.
    * @return Whether the image is produced.
    */
   public boolean generateImageFromText()
   {
      if(text == "")
         return false;
      else
      {
         image = new BarcodeImage();  // Clear original image
         int textLen = text.length();
         
         // Set solid closed limitation line
         for(int i = 1; i <= 10; i++)
            image.setPixel(BarcodeImage.MAX_HEIGHT - i, 0, true);
         for(int i = 1; i <= textLen + 1; i++)
            image.setPixel(BarcodeImage.MAX_HEIGHT - 1, i, true);
         
         // Set open borderline (odd numbered pixels are black)
         for(int i = 2; i <= 9; i++)
            image.setPixel(
                  BarcodeImage.MAX_HEIGHT - i, textLen + 1, i % 2 == 1);
         for(int i = 1; i <= textLen + 1; i++)
            image.setPixel(BarcodeImage.MAX_HEIGHT - 10, i, i % 2 == 0);
         
         // Generate each column in the image excluding the lines above
         for(int i = 1; i <= textLen; i++)
         {
            if(!WriteCharToCol(i, (int) text.charAt(i - 1)))
               return false;
         }
         
         // Reset actual width and height
         actualWidth = computeSignalWidth();
         actualHeight = computeSignalHeight();
         
         return true;
      }
   }
   
   /**
    * The WriteCharToCol method fills in image column by a given valid code.
    * @param col The given column index of the image.
    * @param code The integer corresponding to a character in the text.
    * @return Whether the filling succeeds.
    */
   private boolean WriteCharToCol(int col, int code)
   {
      if(code < 0 || code > 255)
         return false;
      else
      {
         for(int i = 7; i >= 0; i--)
         {
            int level = (int) Math.pow(2, i);
            boolean value = code >= level;
            image.setPixel(BarcodeImage.MAX_HEIGHT - i - 2, col, value);
            if(value)
               code -= level;
         }
         return true;
      }
   }
   
   /**
    * The translateImageToText method produces a text string by internal image.
    * @return Whether a text string is produced.
    */
   public boolean translateImageToText()
   {
      if(this.image == null)
         return false;
      else
      {
         this.text = "";
         for(int col = 1; col < actualWidth - 1; col++)
            this.text += readCharFromCol(col);
         return true;
      }
   }
   
   /**
    * The readCharFromCol method computes the character of a column of image.
    * @param col The given column index of the image.
    * @return The character that the column corresponds to.
    */
   private char readCharFromCol(int col)
   {
      int value = 0;
      for(int i = 0; i < actualHeight - 2; i++)
         if(image.getPixel(BarcodeImage.MAX_HEIGHT - i - 2, col))
            value += Math.pow(2, i);
      return (char) value;
   }
   
   /**
    * The displayTextToConsole method prints out the text to the console.
    */
   public void displayTextToConsole()
   {
      System.out.println(text);
   }
   
   /**
    * The displayImageToConsole method prints out the image to the console.
    */
   public void displayImageToConsole()
   {
      System.out.println("-".repeat(actualWidth + BarcodeImage.NUM_BORDERS));
      for(int i = 0; i < actualHeight; i++)
      {
         System.out.print("|");
         int row = BarcodeImage.MAX_HEIGHT - actualHeight + i;
         for(int col = 0; col < actualWidth; col++)
         {
            char cur = image.getPixel(row, col) ? BLACK_CHAR : WHITE_CHAR;
            System.out.print(cur);
         }
         System.out.printf("|\n");
      }
   }
}

/*
You are awesome! Great work
-------------------------------
|* * * * * * * * * * * * * * *|
|*                            |
|**** *** *******  ***** *****|
|* **************** ********* |
|** *  *   * *      *  * * * *|
|***          **          * * |
|* **   *  ** ***  * * * **  *|
|* *   *   * **    **    **** |
|**** * * ******** * **  ** **|
|*****************************|
CS at Foothill is great Fu
------------------------------
|* * * * * * * * * * * * * * |
|*                          *|
|*** ** ******** ** ***** ** |
|*  **** ***************** **|
|* *  *    *      *  *  *  * |
|*       ** **** *          *|
|*    * ****  **    * * * ** |
|***    ***       * **    * *|
|*** *   **  *   ** * **   * |
|****************************|
What a great resume builder this is!
----------------------------------------
|* * * * * * * * * * * * * * * * * * * |
|*                                    *|
|***** * ***** ****** ******* **** **  |
|* ************************************|
|**  *    *  * * **    *    * *  *  *  |
|* *               *    **     **  *  *|
|**  *   * * *  * ***  * ***  *        |
|**      **    * *    *     *    *  * *|
|** *  * * **   *****  **  *    ** *** |
|**************************************|

*/