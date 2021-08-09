/* Xinye Jiang (20477351) Solution
 * Assignment #9
 * Description: Get execution time for various sorting algorithms
 * Date: 07/31/2021
 */

// Import libraries
import java.util.ArrayList;
import java.util.Random;

/**
 * The Foothill program shows execution time of Insertion sort, Bubble sort, 
 * Merge sort, Quick sort, Heap sort, and Radix sort for input size 50,000, 
 * 100,000, 150,000, 200,000, 250,000, and 300,000.
 */

public class Foothill
{
   public static void main(String[] args) 
   {
      final int NUM_ALGORITHMS = 6;
      int[] arraySize = {50000, 100000, 150000, 200000, 250000, 300000};
      String[] tableTitle = {"Array Size", "Insertion Sort", "Bubble Sort", 
            "Merge Sort", "Quick Sort", "Heap Sort", "Radix Sort"};
      
      // Print out table title
      System.out.printf("%-12s", tableTitle[0]);
      for(int i = 1; i < 3; i++)
         System.out.printf("%-16s", tableTitle[i]);
      for(int i = 3; i < NUM_ALGORITHMS + 1; i++)
         System.out.printf("%-12s", tableTitle[i]);
      
      // Warm up JVM for more accurate results, not use initial execution time
      getExecutionTime(createRandomArray(200000));
      
      // Get execution time for algorithms and print them out
      for(int size: arraySize)
      {
         System.out.printf("\n%,-12d", size);
         long[] result = getExecutionTime(createRandomArray(size));
         for(int i = 0; i < 2; i++)
            System.out.printf("%,-16d", result[i]);
         for(int i = 2; i < NUM_ALGORITHMS; i++)
            System.out.printf("%,-12d", result[i]);
      }
   }
   
   /**
    * The createRandomArray method creates a given size random numbers array.
    * @param size The input size.
    * @return An integer array of given size.
    */
   
   public static int[] createRandomArray(int size)
   {
      int[] array = new int[size];
      Random rand = new Random();
      
      for(int i = 0; i < size; i++)
         array[i] = rand.nextInt(size);
      return array;
   }
   
   /**
    * The getExecutionTime method gets algorithms' execution time of an array.
    * @param array The input array.
    * @return A long array of execution time of all algorithms.
    */
   
   public static long[] getExecutionTime(int[] array)
   {
      final int NUM_ALGORITHMS = 6;
      long startTime;
      long endTime;
      long[] executionTime = new long[NUM_ALGORITHMS];
      int[] cloneArray;
      
      // Insertion Sort
      cloneArray = array.clone();
      startTime = System.nanoTime();
      insertionSort(cloneArray);
      endTime = System.nanoTime();
      executionTime[0] = endTime - startTime;
      
      // Bubble Sort
      cloneArray = array.clone();
      startTime = System.nanoTime();
      bubbleSort(cloneArray);
      endTime = System.nanoTime();
      executionTime[1] = endTime - startTime;
      
      // Merge Sort
      cloneArray = array.clone();
      startTime = System.nanoTime();
      mergeSort(cloneArray);
      endTime = System.nanoTime();
      executionTime[2] = endTime - startTime;
      
      // Quick Sort
      cloneArray = array.clone();
      startTime = System.nanoTime();
      quickSort(cloneArray);
      endTime = System.nanoTime();
      executionTime[3] = endTime - startTime;
      
      // Heap Sort
      cloneArray = array.clone();
      startTime = System.nanoTime();
      heapSort(cloneArray);
      endTime = System.nanoTime();
      executionTime[4] = endTime - startTime;
      
      // Radix Sort
      cloneArray = array.clone();
      startTime = System.nanoTime();
      radixSort(cloneArray);
      endTime = System.nanoTime();
      executionTime[5] = endTime - startTime;
      
      return executionTime;
   }
   
   /**
    * Method insertionSort repeatedly adds an element to a sorted sublist.
    * @param array The input array to be sorted.
    */
   
   public static void insertionSort(int[] array)
   {
      for(int i = 1; i < array.length; i++)
      {
         int curElement = array[i];
         int j;
         for(j = i - 1; j >= 0 && array[j] > curElement; j--)
            array[j + 1] = array[j];
         array[j + 1] = curElement;
      }
   }
   
   /**
    * Method bubbleSort sorts data in passes by swapping neighboring elements.
    * @param array The input array to be sorted.
    */
   
   public static void bubbleSort(int[] array)
   {
      boolean needNextPass = true;
      for(int i = 1; i < array.length && needNextPass; i++)
      {
         needNextPass = false;
         for(int j = 0; j < array.length - i; j++)
         {
            if(array[j] > array[j + 1])   // swap array[j] with array[j + 1]
            {
               int temp = array[j];
               array[j] = array[j + 1];
               array[j + 1] = temp;
               
               needNextPass = true;   // still need next pass
            }
         }
      }
   }
   
   /**
    * Method mergeSort divides data into halves, sort recursively and merge.
    * @param array The input array to be sorted.
    */
   
   public static void mergeSort(int[] array)
   {
      if(array.length > 1) 
      {
         // Merge sort the first half
         int firstHalfLength = array.length / 2;
         int[] firstHalf = new int[firstHalfLength];
         System.arraycopy(array, 0, firstHalf, 0, firstHalfLength);
         mergeSort(firstHalf);
         
         // Merge sort the second half
         int secondHalfLength = array.length - firstHalfLength;
         int[] secondHalf = new int[secondHalfLength];
         System.arraycopy(array, firstHalfLength, 
               secondHalf, 0, secondHalfLength);
         mergeSort(secondHalf);
         
         // Merge two parts into array
         int cur1 = 0;  // current index in firstHalf
         int cur2 = 0;  // current index in secondHalf
         int cur = 0;  // current index in array
         
         while(cur1 < firstHalfLength && cur2 < secondHalfLength)
         {
            if(firstHalf[cur1] < secondHalf[cur2])
               array[cur++] = firstHalf[cur1++];
            else
               array[cur++] = secondHalf[cur2++];
         }
         
         while(cur1 < firstHalfLength)
            array[cur++] = firstHalf[cur1++];
         
         while(cur2 < secondHalfLength)
            array[cur++] = secondHalf[cur2++];
      }
   }
   
   /**
    * The overloaded quickSort Method uses pivot to recursively sort the array.
    * @param array The input array to be sorted.
    */
   
   public static void quickSort(int[] array)
   {
      quickSort(array, 0, array.length - 1);
   }
   
   /**
    * The overloaded quickSort Method sorts array in a given range.
    * @param array The input array to be sorted.
    * @param first The starting index.
    * @param last The ending index.
    */
   
   public static void quickSort(int[] array, int first, int last)
   {
      if(last > first)
      {
         // Phase 1: put pivot to the correct position and get pivot id
         int pivotId;
         int pivot = array[first];  // choose the first element as pivot
         int low = first + 1;  // index for forward search
         int high = last;  // index for backward search
         
         while(high > low) 
         {
            while(low <= high && array[low] <= pivot)  // search forward
               low++;
            while(low <= high && array[high] > pivot)  // search backward
               high--;
            
            // elements in part 1 <= pivot, elements in part 2 > pivot
            if(high > low)  // swap elements at index high, low
            {
               int temp = array[high];
               array[high] = array[low];
               array[low] = temp;
            }
         }
         
         while(high > first && array[high] >= pivot)
            high--;
         
         // Get pivot id (the position of pivot)
         if(pivot > array[high])  // swap pivot with element at high
         {
            array[first] = array[high];
            array[high] = pivot;
            pivotId = high;
         }
         else 
            pivotId = first;
         
         // Phase 2: recursively quick sort (Must mutate the original array!)
         quickSort(array, first, pivotId - 1);  
         quickSort(array, pivotId + 1, last);
      }
   }
   
   /**
    * Method heapSort sorts array by adding elements to a heap and removing.
    * @param array The input array to be sorted.
    */
   
   public static void heapSort(int[] array)
   {
      Heap heap = new Heap(array);  // Generate heap by adding items in array
      
      for(int i = array.length - 1; i >= 0; i--)
         array[i] = heap.remove();  // Sort array by removing the largest items
   }
   
   /**
    * Method radixSort sorts array by repeatedly bucket sort on radix position.
    * @param array The input array to be sorted.
    */
   
   public static void radixSort(int[] array)
   {
      final int RADIX = 10;
      ArrayList<Integer>[] bucket = new ArrayList[RADIX];
      for(int i = 0; i < RADIX; i++)
         bucket[i] = new ArrayList<Integer>();
      
      boolean needLeft = true; // Has remaining (left) radix positions to test
      int placement = 1;
      int temp;
      while(needLeft)
      {
         needLeft = false;
         
         // Put elements to corresponding buckets
         for(int element: array)
         {
            temp = element / placement;
            bucket[temp % RADIX].add(element);
            if(!needLeft && temp > 0)
               needLeft = true;
         }
         
         // Put bucket elements in order back to array
         int cur = 0;  // current index in array
         for(int i = 0; i < RADIX; i++)
         {
            for(int element: bucket[i])
               array[cur++] = element;
            bucket[i].clear();
         }
         
         // Go to the next digit if possible
         placement *= RADIX;
      }
   } 
   
}

/**
 * class Heap
 * The class is defined to help heap sort a positive int array. It has methods
 * to add, remove items and keeps the binary heap structure.
 */

class Heap
{
   // private instance member
   private ArrayList<Integer> list = new ArrayList<>();
   
   /**
    * Default Constructor
    */
   
   public Heap()
   {
      // Do nothing
   }
   
   /**
    * Parameterized Constructor
    * @param array The input array to generate the binary heap.
    */
   
   public Heap(int[] array) 
   {
      for(int i = 0; i < array.length; i++)
         add(array[i]);
   }
   
   /**
    * The addHeap methods adds an element to heap, moves it to the right place.
    * @param element The input int element.
    */
   
   public void add(int element)
   {
      list.add(element);
      int curIndex = list.size() - 1;
      boolean needUp = true;  // whether still need to go to the upper level
      
      while(curIndex > 0 && needUp)  // move the element to the right place
      {
         int parentIndex = (curIndex - 1) / 2;
         if(list.get(curIndex) > list.get(parentIndex))
         {
            int temp = list.get(curIndex);
            list.set(curIndex, list.get(parentIndex));
            list.set(parentIndex, temp);
            curIndex = parentIndex;
         }
         else
            needUp = false;
      }
   }
   
   /**
    * The remove methods removes and returns the largest element.
    * @return The largest int element in the heap (the original root).
    */
   
   public int remove()
   {
      if(list.size() == 0)  // -1 as a warning in a positive int array
         return -1;
      
      // Put the last element at the root and remove the last element
      int removedElement = list.get(0);  // The largest element is root
      list.set(0, list.get(list.size() - 1));
      list.remove(list.size() - 1);
      
      int curIndex = 0;
      boolean needDown = true;  // whether still need to go to the lower level
      while(curIndex < list.size() && needDown)  // move element to right place
      {
         int leftChildIndex = 2 * curIndex + 1;
         int rightChildIndex = 2 * curIndex + 2;
         
         if(leftChildIndex < list.size())
         {
            // Find the maximum of two children
            int maxIndex = leftChildIndex;
            if(rightChildIndex < list.size() &&
               list.get(maxIndex) < list.get(rightChildIndex))
               maxIndex = rightChildIndex;
            
            // If current node < maximum, then swap these two nodes
            if(list.get(curIndex) < list.get(maxIndex))
            {
               int temp = list.get(maxIndex);
               list.set(maxIndex, list.get(curIndex));
               list.set(curIndex, temp);
               curIndex = maxIndex;
            }
            else  // The tree is already a heap
               needDown = false;
         }
         else  // The tree is already a heap
            needDown = false;
      }
      
      return removedElement;
   }
   
   /**
    * The getSize methods returns the size of the heap.
    * @return The size of the heap.
    */
   
   public int getSize()
   {
      return list.size();
   }
}

/*
Array Size  Insertion Sort  Bubble Sort     Merge Sort  Quick Sort  Heap Sort   Radix Sort  
50,000      595,235,682     2,781,566,690   6,131,152   3,609,071   19,352,240  5,783,720   
100,000     705,547,806     11,012,121,353  13,392,513  7,857,473   56,934,975  10,310,206  
150,000     1,596,682,514   25,336,944,976  20,617,368  12,426,052  64,566,103  13,725,053  
200,000     2,909,295,808   44,120,138,879  22,824,707  17,554,633  95,973,466  17,159,387  
250,000     4,533,777,062   68,743,172,366  27,702,726  21,017,201  152,009,972 23,886,170  
300,000     6,529,398,318   98,750,931,743  33,179,416  26,467,627  200,671,586 29,640,809  
*/