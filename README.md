# Android DAWG

A small, fast implementation of [DAWG](http://en.wikipedia.org/wiki/Directed_acyclic_word_graph). Though, it can be used
anywhere, I developed it for Android applications. Using this implementation, I was able to compress a 178,000-word
dictionary to 671K. The dictionary is easily loaded into memory and can be queried. Most queries come back quickly, even
on old hardware. This allows apps to do word search functionality without needing to send requests back and forth to a
remote server.

In particular, the `Dawg` class support two query types.

1. Check for the presence of a particular work in the dictionary.
2. Find all the words in the dictionary that use only a subset of letters, conforming to a pattern. Imagine someone
wanted to ~~cheat at~~ find word combinations Scrabble(tm).

There are certainly more query methods that can be created. I simply haven't needed them, yet.

### How To Use

#### Prerequeisites

1. Maven 3

#### Clone the repo

`git clone https://github.com/icantrap/android-dawg.git`

#### Build the code

Run `mvn install`.

#### Create your dictionary

Your dictionary starts out as a list of words, one on each line. Run the DawgBuilder program to create a DAWG that can
be used in your Android app. From your build directory, run the following

    java -jar target/android-dawg-0.1.4.jar infilename outfilename

`infilename` is the name of the file with the word list. `outfilename` is the name of the file that will be created.

#### Add to your Android app

Copy the `target/android-dawg-0.1.4.jar` file into the directory where you're keeping your app's library files. If
you're using maven, add the following to your pom.xml

```xml
<dependency>
  <groupId>com.icantrap</groupId>
  <artifactId>android-dawg</artifactId>
  <version>0.1.4</version>
</dependency>
```

Copy the output of DawgBuilder to your `res/raw` directory.

All operations happen in the `Dawg` class. The first thing to do is to load the dawg into memory.

```java
InputStream is = getResources().openRawResource(R.raw.dawg);
try {
  dawg = Dawg.load(is);
}
catch (IOException ioe) {
  // handle this exception
}
finally {
  IOUtils.closeQuietly(is);
}
```

This step works in an `AsyncTask` executed in the home activity's `onCreate`.

The `contains` method checks to see if a particular word is in the dawg.

The `subwords` method returns an array of all words in the dawg that use a limiting subset of letters and an optional pattern.
Wildcards are supported. `?` matches a single letter. `*` matches any number of consecutive letters. The `Result` object
has two fields. `word` contains the word. `wildcardPositions` is an array of integers that signify that the letter in
that position matched a wildcard.

### About the Code
It's not pretty, but it gets the job done. There's no recursion, to avoid stack overflow. It avoids Collections in favor
of arrays classes to minimize memory use. It uses byte-packing to minimize storage rquirements. See the first item on
the TODO list.

### TODO
- Clean up this code.
- Remove dependency on Apache Commons Code. What it's providing isn't worth the dependency.
- Add more query methods.
- Better tests.
- Better way to do pattern matching?

License: [GPLv3](http://www.gnu.org/licenses/gpl-3.0.html)