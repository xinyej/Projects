/* Xinye Jiang (20477351) Solution
 * Assignment #10
 * Description: Implement soft deletion in a general tree
 * Date: 08/02/2021
 */

public class Foothill
{
   public static void main(String[] args) throws Exception
   {
      FHTreeNode<String> tn;
      FHTree<String> sceneTree = new FHTree<>();
      
      // create a scene in a room
      tn = sceneTree.addChild(null, "room");

      // add three objects to the scene tree
      sceneTree.addChild(tn, "Lily the canine");
      sceneTree.addChild(tn, "Miguel the human");
      sceneTree.addChild(tn, "table");
      
      // add some parts to Miguel
      tn = sceneTree.find("Miguel the human");

      // Miguel's left arm
      tn = sceneTree.addChild(tn, "torso");
      tn = sceneTree.addChild(tn, "left arm");
      tn =  sceneTree.addChild(tn, "left hand");
      sceneTree.addChild(tn, "thumb");
      sceneTree.addChild(tn, "index finger");
      sceneTree.addChild(tn, "middle finger");
      sceneTree.addChild(tn, "ring finger");
      sceneTree.addChild(tn, "pinky");

      // Miguel's right arm
      tn = sceneTree.find("Miguel the human");
      tn = sceneTree.find(tn, "torso", 0);
      tn = sceneTree.addChild(tn, "right arm");
      tn =  sceneTree.addChild(tn, "right hand");
      sceneTree.addChild(tn, "thumb");
      sceneTree.addChild(tn, "index finger");
      sceneTree.addChild(tn, "middle finger");
      sceneTree.addChild(tn, "ring finger");
      sceneTree.addChild(tn, "pinky");

      // add some parts to Lily
      tn = sceneTree.find("Lily the canine");
      tn = sceneTree.addChild(tn, "torso");
      sceneTree.addChild(tn, "right front paw");
      sceneTree.addChild(tn, "left front paw");
      sceneTree.addChild(tn, "right rear paw");
      sceneTree.addChild(tn, "left rear paw");
      sceneTree.addChild(tn, "spare mutant paw");
      sceneTree.addChild(tn, "wagging tail");

      // add some parts to table
      tn = sceneTree.find("table");
      sceneTree.addChild(tn, "north east leg");
      sceneTree.addChild(tn, "north west leg");
      sceneTree.addChild(tn, "south east leg");
      sceneTree.addChild(tn, "south west leg");

      sceneTree.display();
      System.out.println("\nVirtual Size: " + sceneTree.size());
      System.out.println("\nPhysical Size: " + sceneTree.sizePhysical() + "\n");

      // clone
      FHTree<String> ClonedTree = (FHTree<String>) sceneTree.clone();

      // remove some nodes
      sceneTree.remove("spare mutant paw");
      sceneTree.remove("Miguel the human");
      sceneTree.remove("an imagined higgs boson");
      
      sceneTree.display();
      System.out.println("\nVirtual Size: " + sceneTree.size() + "\n");
      
      sceneTree.displayPhysical();
      System.out.println("\nPhysical Size: " + sceneTree.sizePhysical() + "\n");

      sceneTree.collectGarbage();

      sceneTree.displayPhysical();
 
      System.out.println("\nVirtual Size: " + sceneTree.size());
      System.out.println("\nPhysical Size: " + sceneTree.sizePhysical() + "\n");

      // see if the clone worked
      System.out.println("Clone display");
      ClonedTree.display();
      System.out.println("\nClone's Virtual Size: " + ClonedTree.size() + "\n");
   }
}

/**
 * interface Traverser<E>
 * An interface to design the way to visit the tree.
 */

interface Traverser<E>
{
   public void visit(E x);
}

/**
 * class FHtreeNode<E>
 * An object of this class represents a FHtree node, which has info about data,
 * first child, sibling, previous node, root node and whether it's deleted.
 */

class FHTreeNode<E>
{
   // Protected access so FHtree (same package) or derived classes can access
   protected FHTreeNode<E> firstChild, sib, prev;
   protected E data;
   protected FHTreeNode<E> myRoot;  // needed to test for certain error
   protected boolean deleted;
   
   /**
    * Default Constructor (public)
    */
   
   public FHTreeNode()
   {
      this(null, null, null, null, false); // deleted initialized to false
   }
   
   /**
    * Parameterized Constructor (public)
    * @param d The input FHtree node data.
    * @param sb The input sibling FHtree node.
    * @param chld The input first child FHtree node.
    * @param prv The input previous FHtree node.
    * @param isDeleted The input boolean showing whether the node is deleted.
    */
   
   public FHTreeNode(E d, FHTreeNode<E> sb, FHTreeNode<E> chld, 
         FHTreeNode<E> prv, boolean isDeleted)
   {
      firstChild = chld; 
      sib = sb;
      prev = prv;
      data = d;
      myRoot = null;
      deleted = isDeleted;
   }
   
   /**
    * Parameterized Constructor (protected, for use only by FHtree)
    * @param d The input FHtree node data.
    * @param sb The input sibling FHtree node.
    * @param chld The input first child FHtree node.
    * @param prv The input previous FHtree node.
    * @param root The input root FHtree node.
    * @param isDeleted The input boolean showing whether the node is deleted.
    */

   protected FHTreeNode(E d, FHTreeNode<E> sb, FHTreeNode<E> chld, 
         FHTreeNode<E> prv, FHTreeNode<E> root, boolean isDeleted)
   {
      this(d, sb, chld, prv, isDeleted);
      myRoot = root;
   }
   
   /**
    * Accessor for the data.
    */
   
   public E getData() { return data; }
} 

/**
 * class FHtree<E> implements Cloneable
 * An object of this class represents a FHtree and has methods such as addChild,
 * find, remove, display and so on.
 */

class FHTree<E> implements Cloneable
{
   private int mSize;  // Physical size of the tree
   FHTreeNode<E> mRoot;
   
   final static String blankString = "                                    ";
   
   /**
    * Default Constructor (reset the tree)
    */
   
   public FHTree() { clear(); }
   
   /**
    * The clear method clears and resets the whole tree.
    */
   
   public void clear() { mSize = 0; mRoot = null; }
   
   /**
    * The empty method returns the info about whether the tree is empty.
    */
   
   public boolean empty() { return size() == 0; }
   
   /**
    * The addChild method adds a child node to the tree if no errors.
    * @param treeNode The input parent node.
    * @param x The input data value.
    * @return The node added.
    */
   
   public FHTreeNode<E> addChild(FHTreeNode<E> treeNode,  E x)
   {
      // Empty tree, create a root node if user passes in null treeNode
      if(mSize == 0)
      {
         if(treeNode != null)  // Error! No such treeNode in the tree
            return null;
         mRoot = new FHTreeNode<E>(x, null, null, null, false);
         mRoot.myRoot = mRoot;
         mSize = 1;
         return mRoot;
      }
      
      // Non-empty tree, deal with error situations
      if(treeNode == null)  // Error! Insert with a null parent
         return null; 
      if(treeNode.myRoot != mRoot)  // Error! Node doesn't belong to this tree
         return null;
      if(treeNode.deleted == true) // Error! Insert with a deleted parent
         return null;

      // Non-empty tree with no errors
      // Push node to the head of sibling list & adjust prev pointers
      FHTreeNode<E> newNode = new FHTreeNode<E>(x, treeNode.firstChild, null, 
            treeNode, mRoot, false);  // d, sb, chld, prv, rt, deleted
      treeNode.firstChild = newNode;
      if(newNode.sib != null)
         newNode.sib.prev = newNode;
      ++mSize;
      return newNode;  
   }
   
   /**
    * The non-recursive find method takes only data x and calls the recursive.
    * @param x The input data.
    * @return The found node.
    */
   
   public FHTreeNode<E> find(E x) { return find(mRoot, x, 0); }
   
   /**
    * The recursive find method takes 3 parameters, returns node if not deleted.
    * @param root The input root of the subtree.
    * @param x The input data.
    * @param level The input current level.
    * @return The found node.
    */
   
   public FHTreeNode<E> find(FHTreeNode<E> root, E x, int level)
   {
      // If cannot find: 1. empty tree 2. root null
      if(mSize == 0 || root == null)
         return null;

      // If find data = x and not deleted, return current root node
      if(!root.deleted && root.data.equals(x))
         return root;

      // No matter deleted or not, recursively check sibs at the same level > 0
      FHTreeNode<E> retval;
      if(level > 0 && (retval = find(root.sib, x, level)) != null)
         return retval;
      
      // If not deleted, check children of the current root node at next level
      if(!root.deleted)
         return find(root.firstChild, x, ++level);
      
      return null;
   }
   
   /**
    * The non-recursive remove method takes only data and calls the recursive.
    * @param x The input data.
    * @return Whether the remove is successful.
    */
   
   public boolean remove(E x) { return remove(mRoot, x); }
   
   /**
    * The recursive remove method removes a node of x value given a subtree.
    * @param root The input root of the subtree.
    * @param x The input data.
    * @return Whether the remove is successful.
    */
   
   public boolean remove(FHTreeNode<E> root, E x)
   {
      if(mSize == 0 || root == null || root.deleted)
         return false;
     
      FHTreeNode<E> tn = null;
      if((tn = find(root, x, 0)) != null)
      {
         tn.deleted = true;
         return true;
      }
      return false;
   }
   
   /**
    * The non-recursive size method calls recursive and returns virtual size.
    * @return The size of the tree.
    */
   
   public int size() 
   {
      if(mSize == 0 || mRoot == null || mRoot.deleted)
         return 0;
      else 
         return 1 + size(mRoot.firstChild); 
   }
   
   /**
    * The recursive size method computes (recursively) virtual tree size.
    * @param root The given root of the subtree.
    * @return The size of the subtree from the given root.
    */
   
   public int size(FHTreeNode<E> root)
   { 
      if(root == null)  // root null
         return 0;
      else if(root.deleted)  // root not null but deleted, all subtree deleted
         return size(root.sib);
      else  // root not null not deleted
         return 1 + size(root.firstChild) + size(root.sib);
   }
   
   /**
    * The sizePhysical method returns the actual, physical tree size.
    * @return The actual physical size of the tree.
    */
   
   public int sizePhysical()
   {
      return mSize;
   }
   
   /**
    * The clone method deep clones the whole tree.
    * @return A copy of the tree.
    */
   
   public Object clone() throws CloneNotSupportedException
   {
      FHTree<E> newObject = (FHTree<E>) super.clone();
      newObject.clear();

      newObject.mRoot = cloneSubtree(mRoot);
      newObject.mSize = mSize;
      newObject.setMyRoots(newObject.mRoot);
      
      return newObject;
   }
   
   /**
    * The cloneSubtree method helps deep clone each node of the whole tree.
    * @param root The current tree node root of the subtree.
    * @return A copy of the current tree node.
    */
   
   private FHTreeNode<E> cloneSubtree(FHTreeNode<E> root)
   {
      if(root == null)
         return null;

      FHTreeNode<E> newNode;
      newNode = new FHTreeNode<E> 
      (
         root.data, 
         cloneSubtree(root.sib), cloneSubtree(root.firstChild), null, 
         root.deleted
      );  // d, sb, chld, prv, deleted, set myRoot afterwards
      
      // set prev pointer by parent recursive call
      if(newNode.sib != null)
         newNode.sib.prev = newNode;
      if(newNode.firstChild != null)
         newNode.firstChild.prev = newNode;
      return newNode;
   }
   
   /**
    * The setMyRoots method helps set the root of all the nodes in the tree.
    * @param treeNode The current tree node.
    */
   
   private void setMyRoots(FHTreeNode<E> treeNode)
   {
      if(treeNode == null)
         return;

      treeNode.myRoot = mRoot;
      setMyRoots(treeNode.sib);
      setMyRoots(treeNode.firstChild);
   }
   
   /**
    * The non-recursive display method calls the recursive to display.
    */
   
   public void display()  { display(mRoot, 0); }
   
   /**
    * The recursive display method displays the tree without deleted nodes.
    * @param treeNode The current tree node.
    * @param level The current level.
    */
   
   public void display(FHTreeNode<E> treeNode, int level)
   {
      String indent;

      // stop runaway indentation/recursion
      if(level > (int)blankString.length() - 1)
      {
         System.out.println( blankString + " ... " );
         return;
      }
      
      if (treeNode == null)
         return;

      indent = blankString.substring(0, level);

      if(!treeNode.deleted)  // if not deleted, show data and display children
      {
         System.out.println(indent + treeNode.data);
         display(treeNode.firstChild, level + 1);
      }
      
      if(level > 0)  // no matter deleted or not, display sibs
         display(treeNode.sib, level);
   }

   /**
    * The non-recursive displayPhysical method calls the recursive to display.
    */
   
   public void displayPhysical() { displayPhysical(mRoot, 0); }
   
   /**
    * The recursive displayPhysical method displays the physical tree.
    * @param treeNode The current tree node.
    * @param level The current level.
    */
   
   public void displayPhysical(FHTreeNode<E> treeNode, int level) 
   {
      String indent;

      // stop runaway indentation/recursion
      if(level > (int)blankString.length() - 1)
      {
         System.out.println( blankString + " ... " );
         return;
      }
      
      if (treeNode == null)
         return;

      indent = blankString.substring(0, level);

      System.out.println(indent + treeNode.data 
         + (treeNode.deleted ? " (D)" : ""));
      
      // recursive step done here
      displayPhysical(treeNode.firstChild, level + 1);
      if(level > 0 )
         displayPhysical(treeNode.sib, level);
   }
   
   /**
    * The non-recursive collectGarbage method calls the recursive to remove
    * deleted nodes.
    */
   
   public void collectGarbage()
   {
      if(mSize == 0 || mRoot == null)
         return;
      
      collectGarbage(mRoot, 0);
   }
   
   /**
    * The recursive collectGarbage method collects garbage for each node.
    * @param root The root tree node of the subtree.
    * @param level The current level.
    */
   
   public void collectGarbage(FHTreeNode<E> root, int level)
   {
      if(root == null)
         return;
      
      if(root.deleted)
         removeNode(root);
      else 
         collectGarbage(root.firstChild, level + 1);
      
      if(level > 0)
         collectGarbage(root.sib, level);
   }
   
   /**
    * The removeNode method removes the node and its subtree.
    * @param nodeToDelete The node to be deleted.
    */
   
   private void removeNode(FHTreeNode<E> nodeToDelete)
   {
      if(nodeToDelete == null || mRoot == null)
         return;
      if(nodeToDelete.myRoot != mRoot)  // node does not belong to this tree
         return;  

      mSize--;
      
      // remove all the children of this node
      while(nodeToDelete.firstChild != null)
         removeNode(nodeToDelete.firstChild);

      if(nodeToDelete.prev == null)  // last node in tree
         mRoot = null;  
      else if(nodeToDelete.prev.sib == nodeToDelete)  // adjust left sibling
         nodeToDelete.prev.sib = nodeToDelete.sib; 
      else  // adjust parent
         nodeToDelete.prev.firstChild = nodeToDelete.sib;  

      // adjust the successor sib's prev pointer
      if(nodeToDelete.sib != null)
         nodeToDelete.sib.prev = nodeToDelete.prev;
   }

   /**
    * The non-recursive traverse method calls the recursive.
    * @param func The input interface.
    */
   
   public <F extends Traverser<? super E>> 
   void traverse(F func) { traverse(func, mRoot, 0); }
   
   /**
    * The recursive traverse method traverses the tree.
    * @param func The input interface.
    * @param treeNode The root tree node of the subtree.
    * @param level The current level.
    */

   public <F extends Traverser<? super E>> 
   void traverse(F func, FHTreeNode<E> treeNode, int level)
   {
      if(treeNode == null)
         return;
      
      if(!treeNode.deleted)  // If not deleted, visit the node and its children
      {
         func.visit(treeNode.data);
         traverse(func, treeNode.firstChild, level + 1);
      }
      
      if(level > 0)  // If level > 0, no matter deleted or not, visit its sibs
         traverse(func, treeNode.sib, level);
   }
}

/*
room
 table
  south west leg
  south east leg
  north west leg
  north east leg
 Miguel the human
  torso
   right arm
    right hand
     pinky
     ring finger
     middle finger
     index finger
     thumb
   left arm
    left hand
     pinky
     ring finger
     middle finger
     index finger
     thumb
 Lily the canine
  torso
   wagging tail
   spare mutant paw
   left rear paw
   right rear paw
   left front paw
   right front paw

Virtual Size: 30

Physical Size: 30

room
 table
  south west leg
  south east leg
  north west leg
  north east leg
 Lily the canine
  torso
   wagging tail
   left rear paw
   right rear paw
   left front paw
   right front paw

Virtual Size: 13

room
 table
  south west leg
  south east leg
  north west leg
  north east leg
 Miguel the human (D)
  torso
   right arm
    right hand
     pinky
     ring finger
     middle finger
     index finger
     thumb
   left arm
    left hand
     pinky
     ring finger
     middle finger
     index finger
     thumb
 Lily the canine
  torso
   wagging tail
   spare mutant paw (D)
   left rear paw
   right rear paw
   left front paw
   right front paw

Physical Size: 30

room
 table
  south west leg
  south east leg
  north west leg
  north east leg
 Lily the canine
  torso
   wagging tail
   left rear paw
   right rear paw
   left front paw
   right front paw

Virtual Size: 13

Physical Size: 13

Clone display
room
 table
  south west leg
  south east leg
  north west leg
  north east leg
 Miguel the human
  torso
   right arm
    right hand
     pinky
     ring finger
     middle finger
     index finger
     thumb
   left arm
    left hand
     pinky
     ring finger
     middle finger
     index finger
     thumb
 Lily the canine
  torso
   wagging tail
   spare mutant paw
   left rear paw
   right rear paw
   left front paw
   right front paw

Clone's Virtual Size: 30


*/