
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** @author  John Miller
 *  @version 1.0
 *  @date    Tue Jan 29 18:47:04 EST 2013
 *  @see     LICENSE (MIT style license file).
 */

import java.io.File
import tools.nsc.{Global, Settings}
import collection.mutable.ListBuffer

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** This class is used to compile Scala source files programmatically.
 *  @param _cp  for -classpath <path>    Specify where to find user class files
 *  @param _d   for -d <directory|jar>   destination for generated classfiles
 */
class Comp (_cp: String = "classes", _d: String = "classes")
{
    val _J: String  = null         // advanced Java option, e.g., "-J-mx1014m"

    val s = new Settings           // compiler settings
    if (_cp != null) s.classpath.append (_cp)
    if (_d  != null) s.outputDirs.setSingleOutput (_d)
    if (_J  != null) s.jvmargs.tryToSet (List (_J))

    val sep = File.separator       // file separator ('/' for UNIX, '\' for Windows)

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Get all the source files for the given package.
     *  @param pname  the package name
     */
    def getFiles (pname: String): List [String] =
    {
        val fList = ListBuffer [String] ()
        val dir = new File (pname)
        for (file <- dir.listFiles) {
           val fname = file.getName
           if (fname.endsWith (".scala")) fList += (pname + sep + fname)
        } // for
        fList.toList
    } // getFiles

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Compile all the source files for the given package, returning the number
     *  of files compiled.
     *  @param pname  the package name
     */
    def compPackage (pname: String): Int =
    {
        val gEnv = new Global (s)        // global environment for compiler
        val runner = new gEnv.Run        // object to run Scala compiler
        val files = getFiles (pname)     // list of file names
        runner.compile (files)           // compile all files in list
        files.size                       // number of source files compiled
    } // compPackage
    
} // Comp class

