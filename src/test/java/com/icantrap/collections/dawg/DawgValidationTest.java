// LICENSE: GPLv3. http://www.gnu.org/licenses/gpl-3.0.txt

package com.icantrap.collections.dawg;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.time.StopWatch;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

public class DawgValidationTest
{
  private static Dawg dawg;

  @BeforeClass
  public static void init () throws IOException
  {
    assumeThat (System.getProperty ("RUN_VALIDATION"), is ("on"));

    StopWatch stopWatch = new StopWatch ();
    stopWatch.start ();
    dawg = Dawg.load (DawgValidationTest.class.getResourceAsStream ("/twl06.dat"));
    stopWatch.stop ();
    System.out.println ("Time to load " + dawg.nodeCount () + " node dawg:  " + stopWatch.getTime () + " ms.");
  }

  @Test
  public void containsAllWords () throws IOException
  {
    LineIterator iter = IOUtils.lineIterator (getClass ().getResourceAsStream ("/TWL06.txt"), null);

    StopWatch stopWatch = new StopWatch ();
    stopWatch.start ();

    while (iter.hasNext ())
    {
      String word = iter.next ();
      assertTrue ("Missing word (" + word + ")", dawg.contains (word));
    }

    stopWatch.stop ();
    System.out.println ("Time to query:  " + stopWatch.getTime () + " ms.");

    LineIterator.closeQuietly (iter);
  }
  
  @Test
  public void subwords_noWildcards ()
  {
    Dawg.Result[] subwords = dawg.subwords ("PHONE", null);
    Set<String> words = Dawg.extractWords (subwords);

    assertThat (words, hasItem ("PHONE"));
    assertThat (words, hasItem ("HONE"));
    assertThat (words, hasItem ("PONE"));
    assertThat (words, hasItem ("NOPE"));
    assertThat (words, hasItem ("EON"));
    assertThat (words, hasItem ("HON"));
    assertThat (words, hasItem ("ONE"));
    assertThat (words, hasItem ("EH"));
    assertThat (words, hasItem ("PE"));
    assertThat (words, hasItem ("OP"));
    
    assertThat (subwords.length, Matchers.is (31));
  }
  
  @Test
  public void subwords_wildcard ()
  {
    Dawg.Result[] subwords = dawg.subwords ("?Q", null);
    Set<String> words = Dawg.extractWords (subwords);
    
    assertThat (words, hasItem ("QI"));
    assertThat (subwords.length, Matchers.is (1));
    assertThat (subwords[0].wildcardPositions[0], is (1));
  }
}
