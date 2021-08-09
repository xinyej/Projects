/* Xinye Jiang (20477351) Solution
 * Assignment #7
 * Description: Design class Complex,ComplexMatrix for complex matrix operation
 * Date: 07/26/2021
 */

/**
 * The Foothill class
 * The class performs complex number matrix operations.
 */

public class Foothill
{
   public static void main(String[] args)
   {
      // Create 2 matrix arrays of size 3*3 and a ComplexMatrix
      Complex[][] m1 = new Complex[3][3];
      Complex[][] m2 = new Complex[3][3];
      ComplexMatrix complexMatrix = new ComplexMatrix();
      
      // Fill both arrays with some complex numbers and use zero() method
      for(int i = 0; i < m1.length; i++)
      {
         for (int j = 0; j < m1[0].length; j++) {
            m1[i][j] = new Complex(i + 1, j);
            m2[i][j] = new Complex(i + 2, j + 3);
         }
      }
      m1[0][0] = complexMatrix.zero();
      m2[2][0] = complexMatrix.zero();
      
      // Display the result of addition
      System.out.println("m1 + m2 is ");
      GenericMatrix.printResult(m1, m2, complexMatrix.addMatrix(m1, m2), '+');
      
      // Display the result of multiplication
      System.out.println("\nm1 * m2 is ");
      GenericMatrix.printResult(
            m1, m2, complexMatrix.multiplyMatrix(m1, m2), '*');
      
      // Create a third matrix array of size 2*2
      Complex[][] m3 = new Complex[2][2];
      for(int i = 0; i < m3.length; i++)
      {
         for (int j = 0; j < m3[0].length; j++) {
            m3[i][j] = new Complex(i, j);
         }
      }
      
      // Try to add it to one of the 3*3 arrays to see the result
      try 
      {
         System.out.println("\nm1 + m3 is ");
         GenericMatrix.printResult(
               m1, m3, complexMatrix.addMatrix(m1, m3), '+');
      }
      catch (RuntimeException ex)
      {
         System.out.println("Runtime Exception! m3 cannot be added to m1, "
               + "as they do not have the same size.");
      } 
   }
}

/**
 * The Complex class implements Cloneable, Comparable<Complex>
 * The class represents complex numbers and has methods for performing complex
 * number operations.
 */

class Complex implements Cloneable, Comparable<Complex>
{
   // Private instance members
   private double a;
   private double b;
   
   // Private default values for a, b
   private static final double DEFAULT_A = 0.0;
   private static final double DEFAULT_B = 0.0;
   
   /**
    * Default Constructor
    */
   
   public Complex()
   {
      this.a = DEFAULT_A;
      this.b = DEFAULT_B;
   }
   
   /**
    * Parameterized Constructor (given one parameter: a)
    * @param a The input real part of the complex number.
    */
   
   public Complex(double a)
   {
      this.a = a;
      this.b = DEFAULT_B;
   }
   
   /**
    * Parameterized Constructor (given two parameters: a, b)
    * @param a The input real part of the complex number.
    * @param b The input imaginary part of the complex number.
    */
   
   public Complex(double a, double b)
   {
      this.a = a;
      this.b = b;
   }
   
   /**
    * The add method returns the addition of current, input complex.
    * @param second The input second complex.
    * @return The addition of two complex numbers.
    */
   
   public Complex add(Complex second)
   {
      return new Complex(a + second.a, b + second.b);
   }
   
   /**
    * The subtract method returns subtraction of current, input complex.
    * @param second The input second complex.
    * @return The subtraction of current complex and input complex.
    */
   
   public Complex subtract(Complex second)
   {
      return new Complex(a - second.a, b - second.b);
   }
   
   /**
    * The multiply method returns multiplication of current, input complex.
    * @param second The input second complex.
    * @return The multiplication of current complex and input complex.
    */
   
   public Complex multiply(Complex second)
   {
      double newA = a * second.a - b * second.b;
      double newB = b * second.a + a * second.b;
      return new Complex(newA, newB);
   }
   
   /**
    * The divide method returns division of current, input complex.
    * @param second The input second complex.
    * @return The division of current complex and input complex.
    */
   
   public Complex divide(Complex second)
   {
      double newA = (a * second.a + b * second.b) / Math.pow(second.abs(), 2);
      double newB = (b * second.a - a * second.b) / Math.pow(second.abs(), 2);
      return new Complex(newA, newB);
   }
   
   /**
    * The abs method returns the absolute value of the current complex.
    * @return The absolute value of the current complex.
    */
   
   public double abs()
   {
      return Math.sqrt(a * a + b * b);
   }
   
   /**
    * The toString method overrides the one in Class Object.
    * @return The formatted display of Complex Class object.
    */
   
   public String toString()
   {
      if(b == 0)
         return String.format("%.2f ", a);
      else 
         return String.format("%.2f + %.2fi", a, b);
   }
   
   /**
    * Accessor for the real part of the current complex number.
    */
   
   public double getRealPart()
   {
      return a;
   }
   
   /**
    * Accessor for the imaginary part of the current complex number.
    */
   
   public double getImaginaryPart()
   {
      return b;
   }
   
   /**
    * The clone method implements the one in Cloneable interface.
    * @return A copy of the current complex number.
    */
   
   public Object clone()
   {
      Object o = null;
      
      try
      {
         o = super.clone();
      }
      catch (CloneNotSupportedException ex)
      {
         return null;
      }
      
      return o;
   }
   
   /**
    * The compareTo method implements the one in Comparable<Complex> interface.
    * @param second The input second complex.
    * @return The integer showing which complex number (abs value) is larger.
    */
   
   public int compareTo(Complex second)
   {
      double thisAbs = this.abs();
      double secondAbs = second.abs();
      
      if(thisAbs > secondAbs)      // Return 1 if the first is larger
         return 1;
      else if(thisAbs < secondAbs) // Return -1 if the second is larger
         return -1;
      else                         // Return 0 if they are equal
         return 0;
   }
}

/**
 * The abstract class GenericMatrix<E extends Object> 
 * The class has abstract methods for matrix element's addition, multiplication 
 * and zero definition, and methods for matrix addition, multiplication and 
 * display.
 */

abstract class GenericMatrix<E extends Object> 
{
   /** Abstract method for adding two elements of the matrices */
   protected abstract E add(E o1, E o2);

   /** Abstract method for multiplying two elements of the matrices */
   protected abstract E multiply(E o1, E o2);

   /** Abstract method for defining zero for the matrix element */
   protected abstract E zero();

   /** Add two matrices */
   public E[][] addMatrix(E[][] matrix1, E[][] matrix2) 
   {
      // Check bounds of the two matrices
      if((matrix1.length != matrix2.length) ||
         (matrix1[0].length != matrix2[0].length)) 
      {
         throw new RuntimeException("The matrices do not have the same size");
      }
      
      E[][] result = (E[][]) new Object[matrix1.length][matrix1[0].length];
      
      // Perform addition
      for (int i = 0; i < result.length; i++)
         for (int j = 0; j < result[i].length; j++) 
         {
            result[i][j] = add(matrix1[i][j], matrix2[i][j]);
         }
      
      return result;
   }

   /** Multiply two matrices */
   public E[][] multiplyMatrix(E[][] matrix1, E[][] matrix2) 
   {
      // Check bounds
      if(matrix1[0].length != matrix2.length) 
      {
         throw new RuntimeException("The matrices do not have compatible size");
      }
      
      // Create result matrix
      E[][] result = (E[][]) new Object[matrix1.length][matrix2[0].length];
      
      // Perform multiplication of two matrices
      for(int i = 0; i < result.length; i++) 
      {
         for (int j = 0; j < result[0].length; j++) 
         {
            result[i][j] = zero();
            for(int k = 0; k < matrix1[0].length; k++) {
               result[i][j] = add(result[i][j], 
                     multiply(matrix1[i][k], matrix2[k][j]));
            }
         }
      }
      
      return result;
   }

   /** Print matrices, the operator, and their operation result */
   public static void printResult(
         Object[][] m1, Object[][] m2, Object[][] m3, char op) 
   {
      for(int i = 0; i < m1.length; i++) 
      {
         for(int j = 0; j < m1[0].length; j++)
            System.out.print(" " + m1[i][j]);
         
         if (i == m1.length / 2)
            System.out.print("  " + op + "  ");
         else
            System.out.print("     ");
         
         for (int j = 0; j < m2.length; j++)
            System.out.print(" " + m2[i][j]);
         
         if (i == m1.length / 2)
            System.out.print("  =  ");
         else
            System.out.print("     ");
         
         for (int j = 0; j < m3.length; j++)
            System.out.print(m3[i][j] + " ");
         
         System.out.println();
      }
   }
}

/**
 * The ComplexMatrix class extends GenericMatrix<Complex>
 * The class implements the add, multiply and zero methods.
 */

class ComplexMatrix extends GenericMatrix<Complex>
{
   /** The add method adds two elements of the complex matrices */
   protected Complex add(Complex o1, Complex o2)
   {
      return o1.add(o2);
   }
   
   /** The multiply method multiplies two elements of the complex matrices */
   protected Complex multiply(Complex o1, Complex o2)
   {
      return o1.multiply(o2);
   }
   
   /** The multiply method returns the complex number 0 */
   protected Complex zero()
   {
      return new Complex();
   }
}

/*
m1 + m2 is 
 0.00  1.00 + 1.00i 1.00 + 2.00i      2.00 + 3.00i 2.00 + 4.00i 2.00 + 5.00i     2.00 + 3.00i 3.00 + 5.00i 3.00 + 7.00i 
 2.00  2.00 + 1.00i 2.00 + 2.00i  +   3.00 + 3.00i 3.00 + 4.00i 3.00 + 5.00i  =  5.00 + 3.00i 5.00 + 5.00i 5.00 + 7.00i 
 3.00  3.00 + 1.00i 3.00 + 2.00i      0.00  4.00 + 4.00i 4.00 + 5.00i     3.00  7.00 + 5.00i 7.00 + 7.00i 

m1 * m2 is 
 0.00  1.00 + 1.00i 1.00 + 2.00i      2.00 + 3.00i 2.00 + 4.00i 2.00 + 5.00i     0.00 + 6.00i -5.00 + 19.00i -8.00 + 21.00i 
 2.00  2.00 + 1.00i 2.00 + 2.00i  *   3.00 + 3.00i 3.00 + 4.00i 3.00 + 5.00i  =  7.00 + 15.00i 6.00 + 35.00i 3.00 + 41.00i 
 3.00  3.00 + 1.00i 3.00 + 2.00i      0.00  4.00 + 4.00i 4.00 + 5.00i     12.00 + 21.00i 15.00 + 47.00i 12.00 + 56.00i 

m1 + m3 is 
Runtime Exception! m3 cannot be added to m1, as they do not have the same size.

*/