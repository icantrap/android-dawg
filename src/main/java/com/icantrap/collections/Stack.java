// LICENSE: GPLv3. http://www.gnu.org/licenses/gpl-3.0.txt

package com.icantrap.collections;

import java.util.EmptyStackException;
import java.util.LinkedList;

public class Stack<E>
{
  private final LinkedList<E> entries;

  public Stack ()
  {
    entries = new LinkedList<E> ();
  }

  public boolean empty ()
  {
    return entries.isEmpty ();
  }

  public E peek ()
  {
    int size = entries.size ();

    if (size <= 0)
      throw new EmptyStackException ();

    return entries.get (size - 1);
  }

  public E pop ()
  {
    int size = entries.size ();

    if (size <= 0)
      throw new EmptyStackException ();

    return entries.remove (size - 1);
  }

  public E push (E entry)
  {
    entries.add (entry);
    return entry;
  }
}
