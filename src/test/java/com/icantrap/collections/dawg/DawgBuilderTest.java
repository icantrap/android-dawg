// LICENSE: GPLv3. http://www.gnu.org/licenses/gpl-3.0.txt

package com.icantrap.collections.dawg;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeThat;

/**
 * Tests the add, contains, and wordCount methods of DawgBuilder.
 *
 * @see DawgBuilder#add(String)
 * @see DawgBuilder#add(String[])
 * @see DawgBuilder#add(java.util.List)
 * @see DawgBuilder#wordCount()
 */
public class DawgBuilderTest
{
  private static final String[] WORD_LIST = { "SEARCH", "SEARCHED", "SEARCHING" };

  private DawgBuilder dawgBuilder;

  @Before
  public void setup ()
  {
    dawgBuilder = new DawgBuilder ();

    assumeThat (dawgBuilder.wordCount (), is (0));
  }

  @Test
  public void add ()
  {
    dawgBuilder.add ("jimmy");

    assertThat ("Expected word count to increment; it did not.", dawgBuilder.wordCount (), is (1));
  }

  @Test
  public void add_null ()
  {
    dawgBuilder.add ((String) null);

    assertThat (dawgBuilder.wordCount (), is (0));
  }

  @Test
  public void add_shortWord ()
  {
    dawgBuilder.add ("j");

    assertThat (dawgBuilder.wordCount (), is (0));
  }

  @Test
  public void add_repeat ()
  {
    dawgBuilder.add ("JIMMY");
    assumeThat (dawgBuilder.wordCount (), is (1));

    dawgBuilder.add ("jimmy");  // also tests the uppercase-ness

    assertThat (dawgBuilder.wordCount (), is (1));
  }

  @Test
  public void add_subwordFirst ()
  {
    dawgBuilder.add ("JIMMY");
    assumeThat (dawgBuilder.wordCount (), is (1));

    dawgBuilder.add ("JIM");

    assertThat (dawgBuilder.wordCount (), is (2));
  }

  @Test
  public void add_subwordLast ()
  {
    dawgBuilder.add ("JIM");
    assumeThat (dawgBuilder.wordCount (), is (1));

    dawgBuilder.add ("JIMMY");

    assertThat (dawgBuilder.wordCount (), is (2));
  }

  @Test
  public void add_list ()
  {
    dawgBuilder.add (Arrays.asList (WORD_LIST));

    assertThat (dawgBuilder.wordCount (), is (3));

    for (String word: WORD_LIST)
      assertTrue (dawgBuilder.contains (word));
  }

  @Test
  public void add_array ()
  {
    dawgBuilder.add (WORD_LIST);

    assertThat (dawgBuilder.wordCount (), is (3));

    for (String word: WORD_LIST)
      assertTrue (dawgBuilder.contains (word));
  }

  @Test
  public void contains ()
  {
    dawgBuilder.add ("JIMMY");

    assertTrue (dawgBuilder.contains ("jimmy")); // also tests uppercase-ness
  }

  @Test
  public void contains_null ()
  {
    assertFalse (dawgBuilder.contains (null));
  }

  @Test
  public void contains_shortWord ()
  {
    assertFalse (dawgBuilder.contains ("j"));
  }

  @Test
  public void contains_doesNot ()
  {
    dawgBuilder.add ("JIMMY");

    assertFalse (dawgBuilder.contains ("JAMES"));
  }
}
