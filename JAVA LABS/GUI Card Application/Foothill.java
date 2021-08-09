/* Xinye Jiang (20477351) Solution
 * Assignment #8
 * Description: Design a 24-point card game with shuffle and verify buttons
 * Date: 07/27/2021
 */

package application;

// Import Libraries
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import java.util.Collections;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.image.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

/**
 * Class Foothill extends Application (main)
 * The class embodies the needed Panes and Layouts.
 */

public class Foothill extends Application
{  
   public static void main(String[] args) 
   {
      launch(args);
   }

   /**
    * The start method overrides the one in Application to create a GUI scene.
    */

   public void start(Stage primaryStage)
   {    
      // Create cards array to support the panes
      final int NUM_CARDS = 4;
      Card[] cards = new Card[NUM_CARDS];
      
      // Create HBox pane for the verification result and shuffle button
      Label verifyLbl = new Label();
      Button shuffleBtn = new Button("Shuffle");
      HBox shufflePane = new HBox(10);
      shufflePane.setPadding(new Insets(10, 10, 10, 10));
      shufflePane.setAlignment(Pos.CENTER_RIGHT);
      
      // Create HBox pane for 4 cards
      HBox cardPane = new HBox(15);
      cardPane.setPadding(new Insets(10, 10, 10, 10));
      cardPane.setAlignment(Pos.CENTER);
      
      // Create HBox pane for entering label, text field and verify button
      Label expLbl = new Label("Enter an expression: ");
      TextField expTf = new TextField();
      Button verifyBtn = new Button("Verify");
      HBox expressionPane = new HBox(10);
      expressionPane.setPadding(new Insets(10, 10, 10, 10));
      expressionPane.setAlignment(Pos.CENTER);
      
      // Shuffle the cards for the initial state, Add cards to cardPane
      shuffleCard(cardPane, cards);
      
      // Set buttons on action, Add elements for shufflePane, expressionPane
      shuffleBtn.setOnAction(e -> shuffleCard(cardPane, cards));
      shufflePane.getChildren().addAll(verifyLbl, shuffleBtn);
      verifyBtn.setOnAction(e -> verifyLbl.setText(
            verifyExpression(expTf.getText(), cards)));
      expressionPane.getChildren().addAll(expLbl, expTf, verifyBtn);
      
      // Set panes' positions
      BorderPane pane = new BorderPane();  
      pane.setTop(shufflePane);
      pane.setCenter(cardPane);  
      pane.setBottom(expressionPane); 
      
      // Create the scene and place it in the stage
      Scene scene = new Scene(pane, 500, 300);
      primaryStage.setTitle("Card Table");
      primaryStage.setScene(scene);
      
      // Show everything to the user
      primaryStage.show();   
   }
   
   /**
    * The shuffleCard method gets four new cards and adds them to the pane.
    * @param cardPane The pane to put the 4 card image views on.
    * @param cards The Card[] object to put 4 shuffled cards.
    */

   private static void shuffleCard(HBox cardPane, Card[] cards)
   {
      // Clear original pane
      cardPane.getChildren().clear();
      
      // Fill cards by 4 unique values ranging from 0 to 51
      Random rand = new Random();
      ArrayList<Integer> cardNumbers = new ArrayList<>();
      int i = 0;    // For the first card
      int cardNumber = rand.nextInt(52);
      int suitNumber;
      int valueNumber;
      while(i < 4)  // Loop until the 4th cards
      {
         while(cardNumbers.contains(cardNumber)) // Make sure unique values
            cardNumber = rand.nextInt(52);
         cardNumbers.add(cardNumber);
         suitNumber = cardNumber / 13;   // Get the suit number
         valueNumber = cardNumber % 13;  // Get the value number
         
         // Generate the unique card using the random number
         cards[i] = new Card(GUICard.turnIntIntoCardValueChar(valueNumber),
               GUICard.turnIntIntoSuit(suitNumber));
         i++;
      }
      
      // Generate image and imageview arrays, and fill the cardPane
      Image[] cardImages = new Image[cards.length];
      ImageView[] cardViews = new ImageView[cards.length];
      for(int j = 0; j < cards.length; j++) 
      {
         cardImages[j] = GUICard.getImage(cards[j]);
         cardViews[j] = new ImageView(cardImages[j]);
         cardPane.getChildren().add(cardViews[j]);
      }
   }
   
   /**
    * The verifyExpression method returns corresponding response by inputs.
    * @param expression The expression to evaluate.
    * @param cards The Card[] object to verify whether the numbers match.
    * @return The string that shows the corresponding response.
    */
   
   public static String verifyExpression(String expression, Card[] cards) 
   {
      // Create Stacks to store operands and operators
      Stack<Integer> operandStack = new Stack<>();
      Stack<Character> operatorStack = new Stack<>();
      
      // Create ArrayLists to store operands and card values
      ArrayList<Integer> operandList = new ArrayList<>();
      ArrayList<Integer> cardValueList = new ArrayList<>();
      
      // Get card values
      for(int i = 0; i < cards.length; i++)
         cardValueList.add(GUICard.valueAsInt(cards[i]) + 1);
      
      // Insert blanks around (, ), +, -, /, and *
      expression = insertBlanks(expression);
      
      // Extract operands and operators
      String[] tokens = expression.split(" ");
      
      // Scan tokens
      for(String token: tokens) 
      {
         if(token.length() == 0) 
         {
            // do nothing
         }
         else if(token.charAt(0) == '+' || token.charAt(0) == '-') 
         {
            // Process all +, -, *, / in the top of the operator stack 
            while(!operatorStack.isEmpty() && 
                  (operatorStack.peek() == '+' || 
                   operatorStack.peek() == '-' ||
                   operatorStack.peek() == '*' || 
                   operatorStack.peek() == '/')) 
               processAnOperator(operandStack, operatorStack);
            
            // Push the + or - operator into the operator stack
            operatorStack.push(token.charAt(0));
         }
         else if(token.charAt(0) == '*' || token.charAt(0) == '/') 
         {
            // Process all *, / in the top of the operator stack 
            while(!operatorStack.isEmpty() &&
               (operatorStack.peek() == '*' || operatorStack.peek() == '/'))
            {
               processAnOperator(operandStack, operatorStack);
            }
            
            // Push the * or / operator into the operator stack
            operatorStack.push(token.charAt(0));
         }
         else if(token.trim().charAt(0) == '(') 
         {
            operatorStack.push('('); // Push '(' to stack
         }
         else if(token.trim().charAt(0) == ')') 
         {
            // Process all the operators in the stack until seeing '('
            while(operatorStack.peek() != '(') 
               processAnOperator(operandStack, operatorStack);
            operatorStack.pop(); // Pop the '(' symbol from the stack
         }
         else 
         { 
            // An operand scanned, push an operand to the stack and arraylist
            int expressionInt = Integer.parseInt(token);
            operandList.add(expressionInt);
            operandStack.push(expressionInt);
         }
      }
      
      // Sort the numbers in cardValueList and operandList
      Collections.sort(operandList);
      Collections.sort(cardValueList);
      
      // If the numbers in the expression don't match card numbers, return hint
      if(!operandList.equals(cardValueList)) 
         return "The numbers in the expression don't match the numbers "
               + "in the set";
      
      // Calculate all the remaining operators in the stack 
      while(!operatorStack.isEmpty()) 
      {
         processAnOperator(operandStack, operatorStack);
      }
      
      // Numbers match, return label text based on whether expression = 24
      if(operandStack.pop() == 24)
         return "Correct";
      else
         return "Incorrect result";
   }

   /**
    * The processAnOperator method processes an operator.
    * @param operandStack The stack of operands.
    * @param operatorStack The stack of operators.
    */
   
   public static void processAnOperator(
      Stack<Integer> operandStack, Stack<Character> operatorStack) 
   {
      char op = operatorStack.pop();
      int op1 = operandStack.pop();
      int op2 = operandStack.pop();
      
      if(op == '+') 
         operandStack.push(op2 + op1);
      else if(op == '-') 
         operandStack.push(op2 - op1);
      else if(op == '*') 
         operandStack.push(op2 * op1);
      else if(op == '/') 
         operandStack.push(op2 / op1);
   }
   
   /**
    * The insertBlanks method inserts blanks around (, ), +, -, /, and *
    * @param s The input string.
    * @return The modified string.
    */
   
   public static String insertBlanks(String s) 
   {
      String result = "";
      
      for(int i = 0; i < s.length(); i++) 
      {
         if(s.charAt(i) == '(' || s.charAt(i) == ')' || 
            s.charAt(i) == '+' || s.charAt(i) == '-' ||
            s.charAt(i) == '*' || s.charAt(i) == '/')
         {
            result += " " + s.charAt(i) + " ";
         }
         else
            result += s.charAt(i);
      }
      
      return result;
   }
}

/**
 * Class GUICard
 * The class reads and stores images in the file and has several methods to 
 * convert from chars and suits to ints and back.
 */

class GUICard
{
   // Static Members (13 = A thru K)
   private static Image[][] imageCards = new Image[13][4];
   private static ImageView[][] imageCardViews = new ImageView[13][4];
   private static boolean imagesLoaded = false; 
   
   // Helper Static Arrays and Numbers
   private static String cardlValsConvertAssist = "A23456789TJQK";
   private static String suitValsConvertAssist  = "CDHS";
   private static Card.Suit suitConvertAssist[] =
   {
      Card.Suit.clubs,
      Card.Suit.diamonds,
      Card.Suit.hearts,
      Card.Suit.spades
   };
   protected static int numCardVals = cardlValsConvertAssist.length();
   protected static int numSuitVals = suitConvertAssist.length;
   
   /**
    * Method loadCardImages loads images in the file to private static members.
    */
   
   private static void loadCardImages()
   {
      if(imagesLoaded)   // If loaded before, just return
         return;
      else 
      {
         String imageFileName;
         int intSuit, intVal;

         for (intSuit = 0; intSuit < numSuitVals; intSuit++)
            for (intVal = 0; intVal < numCardVals; intVal++ )
            {
               // Card images stored in images file has name like "AC.gif"
               imageFileName = "images/" + 
                     turnIntIntoCardValueChar(intVal) + 
                     turnIntIntoCardSuitChar(intSuit) + ".gif";
               imageCards[intVal][intSuit] = new Image(imageFileName);
               imageCardViews[intVal][intSuit] = 
                     new ImageView(imageCards[intVal][intSuit]);
            }
         
         imagesLoaded = true;
      }
   }
   
   /**
    * The getImage method returns the image of the given card.
    * @param card The input card object.
    * @return The corresponding image of the input card.
    */
   
   public static Image getImage(Card card)
   {
      loadCardImages();
      return imageCards[valueAsInt(card)][suitAsInt(card)];
   }
   
   /**
    * The turnIntIntoCardValueChar method turns 0-12 into 'A','2','3',...,'K'.
    * @param k An integer (0-12) (index of a char in cardlValsConvertAssist)
    * @return The corresponding character in cardlValsConvertAssist.
    */

   public static char turnIntIntoCardValueChar(int k)
   {
      if ( k < 0 || k > 12)
         return '?'; 
      return cardlValsConvertAssist.charAt(k);
   }
   
   /**
    * The turnIntIntoCardSuitChar method turns 0-3 into 'C', 'D', 'H', 'S'.
    * @param k An integer (0-3) (index of a char in suitValsConvertAssist)
    * @return The corresponding character in suitValsConvertAssist.
    */

   public static char turnIntIntoCardSuitChar(int k) 
   {
      if ( k < 0 || k > 3)
         return '?'; 
      return suitValsConvertAssist.charAt(k); 
   }
   
   /**
    * The turnIntIntoSuit method turns 0-3 into Card.Suit enum values.
    * @param k An integer (0-3) (index of a Card.Suit in suitConvertAssist)
    * @return The corresponding Card.Suit value in suitConvertAssist.
    */
   
   public static Card.Suit turnIntIntoSuit(int k) 
   {
      return suitConvertAssist[k];
   }
   
   /**
    * The valueAsInt method turns card into an index in cardlValsConvertAssist.
    * @param card An input card.
    * @return The corresponding index of card value in cardlValsConvertAssist.
    */
   
   public static int valueAsInt(Card card)  
   {
      return cardlValsConvertAssist.indexOf(card.getValue());
   }
   
   /**
    * The suitAsInt method turns card into an index in suitConvertAssist.
    * @param card An input card.
    * @return The corresponding index of suit value in suitConvertAssist.
    */
   
   public static int suitAsInt(Card card)
   {
      for(int i = 0; i < numSuitVals; i++)
      {
         if(suitConvertAssist[i] == card.getSuit())
            return i;
      }
      return -1;
   }
}

/**
 * Class CardIdentity
 * A CardIdentity object contains the suit and value of a card. It has a 
 * default constructor and methods to validate, set and get suit and value.
 */

class CardIdentity
{
   public enum Suit {clubs, diamonds, hearts, spades}
   
   private char value;
   private Suit suit;
   
   protected static final char DEFAULT_VALUE = 'A';
   protected static final Suit DEFAULT_SUIT = Suit.spades;
   protected static final char[] LEGAL_VALUES = {'A', '2', '3', '4', '5', '6', 
         '7', '8', '9', 'T', 'J', 'Q', 'K'};  
   
   /**
    * Default Constructor
    */
   
   public CardIdentity()
   {
      this.value = DEFAULT_VALUE;
      this.suit = DEFAULT_SUIT;
   }
   
   /**
    * The set method sets value and suit of CardIdentity if they are valid.
    * @param value The input value of the CardIdentity.
    * @param suit The input suit of the CardIdentity.
    * @return Whether the set is successful.
    */
   
   public boolean set(char value, Suit suit)
   {
      boolean isValidInput = isValid(value, suit);
      if(isValidInput)
      {
         this.value = value;
         this.suit = suit;
      }
      return isValidInput;
   }
   
   /**
    * Getter for suit
    */
   
   public Suit getSuit()
   {
      return this.suit;
   }
   
   /**
    * Getter for value
    */
   
   public char getValue()
   {
      return this.value;
   }
   
   /**
    * The isValid method validates the input value and suit.
    * @param value The input value of the CardIdentity.
    * @param suit The input suit of the CardIdentity.
    * @return Whether the value and suit are valid.
    */

   private static boolean isValid(char value, Suit suit)
   {
      // If value not in LEGAL_VALUES, return false
      for(char legalValue: LEGAL_VALUES)
      {
         if(value == legalValue)
            return true;
      }
      return false;
   }  
}

/**
 * Class Card (a subclass of CardIdentity)
 * A Card object has cardError value indicating the card is good or not besides
 * card suit and value. It has overloaded constructors, setters, getters, 
 * toString and equals methods.
 */

class Card extends CardIdentity
{
   private boolean cardError;
   
   private static final boolean DEFAULT_CARD_ERROR = false;
   protected static final Card BAD_CARD = 
         new Card('Y', CardIdentity.Suit.diamonds);
   
   /**
    * Default Constructor
    */
   
   public Card()
   {
      super();
      this.cardError = DEFAULT_CARD_ERROR;
   }
   
   /**
    * Parameterized Constructor
    * @param value The input value of the Card.
    * @param suit The input suit of the Card.
    */
   
   public Card(char value, Suit suit)
   {
      boolean setSuccess = set(value, suit);
      if(!setSuccess)
      {
         set(CardIdentity.DEFAULT_VALUE, CardIdentity.DEFAULT_SUIT);
         this.cardError = true;
      } 
   }
   
   /**
    * The set method overrides the one in superclass and sets cardError.
    * @param value The input value of the Card.
    * @param suit The input suit of the Card.
    * @return Whether the setting is successful.
    */
   
   public boolean set(char value, Suit suit)
   {
      boolean isSettingSuccessful = super.set(value, suit);
      this.cardError = !isSettingSuccessful;
      return isSettingSuccessful;
   }
   
   /**
    * The toString method overrides the one in Class Object.
    * @return The formatted display of Card Class object.
    */
   
   public String toString()
   {
      if(this.cardError)
         return "Invalid card suit and value!";
      else
         return this.getValue() + " of " + this.getSuit();
   }
   
   /**
    * Getter for cardError
    */
   
   public boolean getCardError()
   {
      return this.cardError;
   }
   
   /**
    * The equals method tests whether two cards are the same.
    * @param card The input card to be compared.
    * @return Whether the input card is the same as the current card.
    */
   
   public boolean equals(Card card)
   {
      return this.getValue() == card.getValue()
            && this.getSuit() == card.getSuit()
            && this.getCardError() == card.getCardError();
   }
}