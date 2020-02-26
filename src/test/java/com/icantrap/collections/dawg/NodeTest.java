// LICENSE: GPLv3. http://www.gnu.org/licenses/gpl-3.0.txt
package com.icantrap.collections.dawg;


import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NodeTest
{
  @Test
  public void equals_null ()
  {
    Node node = new Node ('x');
    assertTrue (node.equals (null));
  }

  @Test
  public void equals_sameObject ()
  {
    Node node = new Node ('x');
    assertTrue (node.equals (node));
  }

  @Test
  public void equals_differentType ()
  {
    Node node = new Node ('x');
    assertFalse (node.equals (7));
  }

  @Test
  public void equals_differentValue ()
  {
    Node lhs = new Node ('x');
    Node rhs = new Node ('y');

    assertFalse (lhs.equals (rhs));
  }
  
  @Test
  public void equals_differentTerminal ()
  {
    Node lhs = new Node ('x');
    lhs.terminal = true;
    Node rhs = new Node ('y');
    rhs.terminal = false;

    assertFalse (lhs.equals (rhs));
  }

  @Test
  public void equals_differentChildPresence1 ()
  {
    Node lhs = new Node ('x');
    lhs.addChild ('a');
    Node rhs = new Node ('x');

    assertFalse (lhs.equals (rhs));
  }

  @Test
  public void equals_differentChildPresence2 ()
  {
    Node lhs = new Node ('x');
    Node rhs = new Node ('x');
    rhs.addChild ('a');

    assertFalse (lhs.equals (rhs));
  }

  @Test
  public void equals_matchingChildred1 ()
  {
    Node lhs = new Node ('x');
    lhs.addChild ('a');
    Node rhs = new Node ('x');
    rhs.addChild ('a');

    assertTrue (lhs.equals (rhs));
  }

  @Test
  public void equals_matchingChildred2 ()
  {
    Node lhs = new Node ('x');
    lhs.addChild ('a');
    lhs.addChild ('b');
    lhs.addChild ('c');
    Node rhs = new Node ('x');
    rhs.addChild ('a');
    rhs.addChild ('b');
    rhs.addChild ('c');

    assertTrue (lhs.equals (rhs));
  }

  @Test
  public void equals_matchingChildredRecursive ()
  {
    Node lhs = new Node ('x');
    lhs.addChild ('a');
    lhs.addChild ('b').addChild ('j');
    lhs.addChild ('c');
    Node rhs = new Node ('x');
    rhs.addChild ('a');
    rhs.addChild ('b').addChild ('j');
    rhs.addChild ('c');

    assertTrue (lhs.equals (rhs));
  }

  @Test
  public void equals_differentChildren ()
  {
    Node lhs = new Node ('x');
    lhs.addChild ('a');
    lhs.addChild ('b');
    Node rhs = new Node ('x');
    rhs.addChild ('a');

    assertFalse (lhs.equals (rhs));
  }

  @Test
  public void equals_differentChildredRecursive ()
  {
    Node lhs = new Node ('x');
    lhs.addChild ('a');
    lhs.addChild ('b').addChild ('j');
    lhs.addChild ('c');
    Node rhs = new Node ('x');
    rhs.addChild ('a');
    rhs.addChild ('b').addChild ('j').addChild ('k');
    rhs.addChild ('c');

    assertFalse (lhs.equals (rhs));
  }
}
