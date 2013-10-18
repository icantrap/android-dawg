// LICENSE: GPLv3. http://www.gnu.org/licenses/gpl-3.0.txt

package com.icantrap.collections.dawg;

import org.apache.commons.io.LineIterator;

import java.io.*;
import java.util.*;

/**
 * This class builds a dawg from scratch.  It does this by adding all the words to a trie.  When it's time to build, the
 * trie is compressed into a dawg.
 */

class DawgBuilder
{
  private final Node root = new Node ((char) 0);
  private int wordCount = 0;

  public DawgBuilder ()
  {
  }
  
  // maybe.  once a dawg is built, you can't add words to it.

  /**
   * Adds a new word to the dawg.  Check for duplicate entry first.  Will not add null or words under two characters.
   *
   * @param word the word to add
   * @return the builder.
   */
  public DawgBuilder add (String word)
  {
    if ((null == word) || (word.length () < 2))
      return this;

    word = word.toUpperCase ();

    char[] chars = word.toCharArray ();
    Node ptr = root;
    boolean found = true;

    for (char ch: chars)
    {
      Node node = ptr.findChild (ch);

      if (null != node)
        ptr = node;
      else
      {
        found = false;
        ptr = ptr.addChild (ch);
      }
    }

    if (found && ptr.terminal)
      return this;
    else
    {
      ptr.terminal = true;
      ++wordCount;
      return this;
    }
  }

  /**
   * Adds a collection of words to the dawg.  Delegates to add.
   * 
   * @param words the array of words to add
   * @return the builder
   * @see DawgBuilder#add(String)
   */
  public DawgBuilder add (String[] words)
  {
    for (String word : words)
      add (word);

    return this;
  }

  /**
   * Adds a collection of words to the dawg
   * @param words the collecton of words to add
   * @return the builder
   * @see DawgBuilder#add(String)
   */
  public DawgBuilder add (Collection<String> words)
  {
    for (String word : words)
      add (word);

    return this;
  }

  /**
   * Adds words from a newline-delimited file using LineIterator.
   *
   * @param wordIter the line iterator
   * @return the builder
   * @see LineIterator
   */
  public DawgBuilder add (LineIterator wordIter)
  {
    while (wordIter.hasNext ())
      add (wordIter.next ());

    return this;
  }

  /**
   * Adds words from a newline-delimited InputStream.
   *
   * @param is the stream with the words
   * @return the builder
   * @throws java.io.IOException if reading from the InputStream causes an IOException
   */
  public DawgBuilder add (InputStream is) throws IOException
  {
    return add (new InputStreamReader (is));
  }

  /**
   * Adds words from a newline-delimited Reader.
   *
   * @param reader the reader with the words
   * @return the builder
   * @throws java.io.IOException if reading from the Reader causes an IOException
   */
  public DawgBuilder add (Reader reader) throws IOException
  {
    BufferedReader br = new BufferedReader (reader, 12*1024);
    String line;
    
    while ((line = br.readLine ()) != null)
      add (line);

    return this;
  }

  /**
   * The number of words - so far - that will be built into this Dawg.
   *
   * @return the number of words.
   */
  public int wordCount ()
  {
    return wordCount;
  }

  /**
   * The number of nodes - currently - in the structure that will become the Dawg.
   * @return the number of nodes
   */
  public int nodeCount ()
  {
    int nodeCount = 0;
    Deque<Node> stack = new LinkedList<Node> ();
    stack.push (root);
    
    while(!stack.isEmpty ())
    {
      Node ptr = stack.pop ();
      ++nodeCount;
      
      for (Node nextChild: ptr.nextChildren)
        stack.push (nextChild);
      if (null != ptr.child)
        stack.push (ptr.child);
    }

    return nodeCount;
  }

  /**
   * Returns whether the word would be contained in the Dawg being built.
   *
   * @param word the word to check
   * @return true, if the word is contained; false, otherwise.
   */
  boolean contains (String word)
  {
    if ((null == word) || (word.length () < 2))
      return false;

    word = word.toUpperCase ();

    char[] chars = word.toCharArray ();
    Node ptr = root;

    for (char ch: chars)
    {
      ptr = ptr.findChild (ch);
      if (null == ptr) return false;
    }

    return ptr.terminal;
  }

  // compression internals
  private List<Node> nodeList = new ArrayList<Node> ();
  private Map<Integer, LinkedList<Node>> childDepths = new LinkedHashMap<Integer, LinkedList<Node>> ();
  
  private void compress ()
  {
    LinkedList<Node> stack = new LinkedList<Node> ();
    int index = 0;

    stack.addLast (root);
    while(!stack.isEmpty ())
    {
      Node ptr = stack.removeFirst ();

      ptr.index = index++;
      if (root != ptr)
        ptr.siblings = ptr.parent.nextChildren.size () - 1 + (null == ptr.parent.child ? 0 : 1);
      nodeList.add (ptr);

      for (Node nextChild: ptr.nextChildren)
        stack.add (nextChild);
      if (null != ptr.child)
        stack.add (ptr.child);
    }

    // assign child depths to all nodes
    for (Node node: nodeList)
      if (node.terminal)
      {
        node.childDepth = 0;
        
        Node ptr = node;
        int depth = 0;
        while (root != ptr)
        {
          ptr = ptr.parent;
          ++depth;
          if (depth > ptr.childDepth)
            ptr.childDepth = depth;
          else break;
        }
      }
    
    // bin nodes by child depth
    for (Node node: nodeList)
    {
      LinkedList<Node> nodes = childDepths.get (node.childDepth);
      if (null == nodes)
      {
        nodes = new LinkedList<Node> ();
        nodes.add (node);
        childDepths.put (node.childDepth, nodes);
      }
      else nodes.add (node);
    }

    int maxDepth = -1;
    for (int depth:childDepths.keySet ())
      if (depth > maxDepth)
        maxDepth = depth;

    for (int depth = 0; depth <= maxDepth; ++depth)
    {
      LinkedList<Node> nodes = childDepths.get (depth);
      if (null == nodes)
        continue;

      for (ListIterator<Node> pickNodeIter = nodes.listIterator (); pickNodeIter.hasNext ();)
      {
        Node pickNode = pickNodeIter.next ();

        if ((null == pickNode.replaceMeWith) && pickNode.isChild && (0 == pickNode.siblings))
          for (ListIterator<Node> searchNodeIter = nodes.listIterator (pickNodeIter.nextIndex ()); searchNodeIter.hasNext (); )
          {
            Node searchNode = searchNodeIter.next ();
            if ((null == searchNode.replaceMeWith) && searchNode.isChild && (0 == searchNode.siblings) && pickNode.equals (searchNode))
            {
              searchNode.parent.child = pickNode;
              searchNode.replaceMeWith = pickNode;
            }
          }
      }
    }
  }

  /**
   * Builds the dawg based on the words added.
   *
   * @return the new Dawg instance
   */
  public Dawg build ()
  {
    compress ();
    
    for (Node node:nodeList)
      node.index = -1;

    LinkedList<Node> stack = new LinkedList<Node> ();
    
    nodeList.clear ();
    stack.clear ();
    stack.addLast (root);
    
    int index = 0;

    while(!stack.isEmpty ())
    {
      Node ptr = stack.removeFirst ();
      if (-1 == ptr.index)
        ptr.index = index++;
      nodeList.add (ptr);

      for (Node nextChild: ptr.nextChildren)
        stack.addLast (nextChild);
      if (null != ptr.child)
        stack.addLast (ptr.child);
    }

    int[] ints = new int[index];

    for (Node node: nodeList)
      ints[node.index] = node.toInteger ();

    return new Dawg (ints);
  }

  public static void main (String[] args) throws IOException
  {
    if (args.length != 2)
    {
      System.out.println ("Usage:  DawgBuilder infilename outfilename");
      System.out.println ("  infilename - filename of newline-delimited list of words to put in the DAWG.");
      System.out.println ("  outfilename - filename of new file to be created containing the binary DAWG data.");

      return;
    }

    String infilename = args[0];
    String outfilename = args[1];
    
    FileReader reader = new FileReader (infilename);
    DawgBuilder builder = new DawgBuilder ();
    Dawg dawg = builder.add (reader).build ();
    dawg.store (new FileOutputStream (outfilename));
  }
}
