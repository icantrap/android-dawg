// LICENSE: GPLv3. http://www.gnu.org/licenses/gpl-3.0.txt

package com.icantrap.collections.dawg;

import java.util.LinkedList;
import java.util.List;

class Node
{
  char value;
  Node parent = null;
  Node child = null;
  final List<Node> nextChildren = new LinkedList<Node> ();
  boolean terminal = false;

  private Node (Node parent, char value)
  {
    this.parent = parent;
    this.value = value;
  }

  public Node (char value)
  {
    this.value = value;
  }

  private Node ()
  {
  }

  public Node findChild (char value)
  {
    if (null == child)
      return null;

    if (value == child.value)
      return child;

    for (Node nextChild: nextChildren)
      if (nextChild.value == value)
        return nextChild;

    return null;
  }

  public Node addChild (char value)
  {
    Node rv;

    if (null == child)
    {
      rv = child = new Node (this, value);
      child.isChild = true;
      child.lastChild = true;
    }
    else
    {
      Node nextChild = new Node (this, value);
      nextChildren.add (nextChild);
      rv = nextChild;
    }

    return rv;
  }

  @Override
  public String toString ()
  {
    String prefix = prefix ();
    
    StringBuilder stringBuilder = new StringBuilder ();
    stringBuilder.append ("[value:")
      .append (value)
      .append (" prefix:")
      .append (prefix)
      .append (" child:")
      .append ((null != child) ? child.value : "n/a")
      .append (" next:");
    
    for (Node nextChild: nextChildren)
      stringBuilder.append (nextChild.value);

      stringBuilder.append ("]");

    return stringBuilder.toString ();
  }
  
  public int toInteger ()
  {
    int rv;
    
    // start with the first child index.  use MAX_INDEX, if there are no children
    if (nextChildren.isEmpty ())
      if (null == child)
        rv = -1;
      else rv = child.index;
    else rv = nextChildren.get (0).index;

    // shift 1 and add the last child bit
    rv = (rv << 1) | (lastChild ? 0x1 : 0x0);
    // shift 1 and add the terminal bit
    rv = (rv << 1) | (terminal ? 0x1 : 0x0);
    // shift 8 and add the value
    rv = (rv << 8) | value;

    return rv;
  }

  String prefix ()
  {
    StringBuilder prefix = new StringBuilder ();
    Node ptr = this;
    while (null != ptr.parent)
    {
      prefix.append (ptr.value);
      ptr = ptr.parent;
    }

    prefix.reverse ();
    return prefix.toString ();
  }

  // compression internals
  int index = -1;
  int childDepth = -1;
  boolean isChild = false;
  boolean lastChild = false;
  int siblings = 0;
  Node replaceMeWith = null;

  @Override
  public boolean equals (Object obj)
  {
    if (null == obj)
      return false;
    if (this == obj)
      return true;
    if (getClass () != obj.getClass ())
      return false;
    
    Node other = (Node) obj;

    if (value != other.value)
      return false;

    if (terminal != other.terminal)
      return false;
    
    if ((null != child) && (null == other.child))
      return false;

    if ((null == child) && (null != other.child))
      return false;
    
    if ((null != child) && (!child.equals (other.child)))
      return false;

    if (nextChildren.size () != other.nextChildren.size ())
      return false;
    
    int size = nextChildren.size ();
    for (int i = 0; i < size; ++i)
      if (!nextChildren.get (i).equals (other.nextChildren.get (i)))
        return false;
    
    return true;
  }
}

// todo.  hashcode
// todo.  node tests
// todo.  cleanup.