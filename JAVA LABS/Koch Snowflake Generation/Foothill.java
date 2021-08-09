/* Xinye Jiang (20477351) Solution
 * Assignment #6
 * Description: Use recursion to display Koch snowflake
 * Date: 07/16/2021
 */

package application;

//Import Libraries
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

/**
 * Class Foothill extends Application (main)
 * The class embodies the needed Panes and Layouts.
 */

public class Foothill extends Application 
{
   public void start(Stage primaryStage) 
   {
      // Set pane for Koch snowflake and text field for its order
      KochSnowFlakePane trianglePane = new KochSnowFlakePane(); 
      TextField tfOrder = new TextField(); 
      tfOrder.setOnAction(
            e -> trianglePane.setOrder(Integer.parseInt(tfOrder.getText())));
      tfOrder.setPrefColumnCount(4);
      tfOrder.setAlignment(Pos.BOTTOM_RIGHT);
      
      // Pane to hold label, text field
      HBox hBox = new HBox(10);
      hBox.getChildren().addAll(new Label("Enter an order: "), tfOrder);
      hBox.setAlignment(Pos.BOTTOM_CENTER);
      
      // Set BorderPane for Koch snowflake and text field layout
      BorderPane borderPane = new BorderPane();
      borderPane.setCenter(trianglePane);
      borderPane.setBottom(hBox);
      
      // Create a scene and place it in the stage
      Scene scene = new Scene(borderPane, 200, 250);
      primaryStage.setTitle("KochSnowFlake");
      primaryStage.setScene(scene);
      primaryStage.show();
   }
   
   /**
    * Class KochSnowFlakePane extends Pane
    * The class generates and displays Koch snowflake of a given order.
    */
   
   static class KochSnowFlakePane extends Pane 
   {
      // Private member
      private int order;
      
      /**
       * Default Constructor (set order to default 0)
       */
      
      KochSnowFlakePane()
      {
         this.order = 0;
      }
     
      /**
       * The setOrder method sets the order of Koch snowflake and paints it.
       * @param order The input order of Koch snowflake.
       * @return Whether the input order is valid, i.e. is >= 0.
       */
      
      public boolean setOrder(int order)
      {
         boolean res = order >= 0;
         if(res)
         {
            this.order = order;
            paint();
         }
         return res;
      }
      
      /**
       * The paint method paints the Koch snowflake of a given order.
       */
      
      protected void paint()
      {
         this.getChildren().clear();  // Clear the pane before redisplay
         
         // Creates the start equilateral triangle of order 0
         double lateral = getWidth() - 20;
         double height = lateral * Math.cos(Math.toRadians(30));
         Point2D p1 = new Point2D(getWidth() / 2, 10);
         Point2D p2 = new Point2D(10, 10 + height);
         Point2D p3 = new Point2D(10 + lateral, 10 + height);
         
         // Display each line (recursively)
         displayKochSnowFlake(order, p1, p2);
         displayKochSnowFlake(order, p2, p3);
         displayKochSnowFlake(order, p3, p1);
      }
      
      /**
       * Method displayKochSnowFlake recursively paints image of given order.
       * @param order The input order of Koch snowflake.
       * @param p1 The input point 1. (Order of p1, p2 matters!)
       * @param p2 The input point 2. (Order of p1, p2 matters!)
       */
      
      private void displayKochSnowFlake(int order, Point2D p1, Point2D p2) 
      {
         if(order == 0)  // If order is 0, then display to the pane
         {
            Line line = new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            this.getChildren().add(line);
         }
         else  // Calculate new points, recursively display snow flakes on line
         {
            // Get the x, y coordinate differences between p2 and p1
            double deltaX = p2.getX() - p1.getX();
            double deltaY = p2.getY() - p1.getY();
           
            // Set 3 points of the outward equilateral triangle by line segment
            Point2D x = new Point2D(
                  p1.getX() + deltaX / 3, p1.getY() + deltaY / 3);
            Point2D y = new Point2D(
                  p1.getX() + deltaX * 2 / 3, p1.getY() + deltaY * 2 / 3);
            Point2D z = new Point2D(
                  (p1.getX() + p2.getX()) / 2 + 
                  Math.cos(Math.toRadians(30)) * (p1.getY() - p2.getY()) / 3,
                  (p1.getY() + p2.getY()) / 2 +
                  Math.cos(Math.toRadians(30)) * (p2.getX() - p1.getX()) / 3);

            // Recursively display snow flakes on lines
            displayKochSnowFlake(order - 1, p1, x);
            displayKochSnowFlake(order - 1, x, z);
            displayKochSnowFlake(order - 1, z, y);
            displayKochSnowFlake(order - 1, y, p2);
         }  
      }
   }

   public static void main(String[] args) 
   {
      launch(args);
   }
}