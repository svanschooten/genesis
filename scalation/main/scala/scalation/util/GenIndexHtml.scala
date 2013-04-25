
/**::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 * @author  Srikalyan Swayampakula, John Miller
 * @version 1.0
 * @date    Tue Feb 23 12:01:36 EST 2010
 * @see     LICENSE (MIT style license file).
 */

package scalation.util

import java.io.{BufferedWriter, File, FileWriter}

import collection.mutable.ArrayBuffer

/**::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 * This object is used to create "index.html" files in source code directories
 * to enable Web browsing of source code.
 */
object GenIndexHtml extends App
{
    val home = System.getenv ("SCALATION_HOME")
    val currentDir = (if (home == null) "." else home) + "/src/scalation"
    println ("Generate index.html files starting from currentDir = " + currentDir)
    recCreate (new File (currentDir))

    /**::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Recursively create index.html files for each directory.
     * @param f  the file/directory to examine
     */
    def recCreate (f: File)
    {
        recDeleteIndex (f)
        val dirs = new ArrayBuffer [File] ()
        val fos  = new BufferedWriter (new FileWriter (new File (f.getAbsolutePath () + "/index.html")))
        fos.write ("<html>\n<body>\n<h1> Source files in " + f.getName () + " Package </h1><p>\n<ul>\n")

        for (f1 <- f.listFiles ()) {
            if (! f1.isDirectory ()) {
                fos.write ("<li> <a href = './" + f1.getName () + "'> " + f1.getName () + " </a> </li>\n")
            } else {
                dirs += f1
            } // if
        } // for

        for (f1 <- dirs) fos.write ("<li> <a href = './" + f1.getName () + "'> " + f1.getName () + " </a> </li>\n")
        fos.write ("</ul>\n</body>\n<html>")
        fos.close ()
        for (f1 <- dirs if f1.isDirectory ()) recCreate (f1)
    } // recCreate

    /**::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Recursively delete index.html files for each directory (clean up step).
     * @param f  the file/directory to examine
     */
    def recDeleteIndex (f: File)
    {
        if ( ! f.isDirectory ()) {
            if (f.getName () == "index.html") f.delete ()
        } else {
            for (f1 <- f.listFiles ()) recDeleteIndex (f1)
        } // if
    } // recDeleteIndex

} // GenIndexHtml object

