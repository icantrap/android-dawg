// LICENSE: GPLv3. http://www.gnu.org/licenses/gpl-3.0.txt
package com.icantrap.collections.dawg;

import java.io.IOException;

/**
 * Exception thrown when a Dawg can't be loaded from a data file.
 */
public class DataFormatException extends IOException
{
  public DataFormatException (String message, Throwable cause)
  {
    super (message, cause);
  }
}
