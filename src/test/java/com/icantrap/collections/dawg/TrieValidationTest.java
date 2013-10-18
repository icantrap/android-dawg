// LICENSE: GPLv3. http://www.gnu.org/licenses/gpl-3.0.txt

package com.icantrap.collections.dawg;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.time.StopWatch;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;

public class TrieValidationTest
{
  private DawgBuilder dawgBuilder;

  @Before
  public void before () throws IOException
  {
    assumeThat (System.getProperty ("RUN_VALIDATION"), is ("on"));
    LineIterator iter = IOUtils.lineIterator (getClass ().getResourceAsStream ("/TWL06.txt"), null);
    dawgBuilder = new DawgBuilder ();

    while (iter.hasNext ())
      dawgBuilder.add (iter.next ());
    
    LineIterator.closeQuietly (iter);

    System.out.println ("Uncompressed:  " + dawgBuilder.nodeCount () + " nodes");

    StopWatch stopWatch = new StopWatch ();
    stopWatch.start ();
    dawgBuilder.build ();
    stopWatch.stop ();
    
    System.out.println ("Time to compress:  " + stopWatch.getTime () + " ms.");
    System.out.println ("Compressed:  " + dawgBuilder.nodeCount () + " nodes");
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
      assertTrue ("Missing word (" + word + ")", dawgBuilder.contains (word));
    }
    
    stopWatch.stop ();
    System.out.println ("Time to query:  " + stopWatch.getTime () + " ms.");

    LineIterator.closeQuietly (iter);    
  }
}
