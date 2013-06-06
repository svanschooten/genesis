// @SOURCE:D:/workspace/play/genesis/conf/routes
// @HASH:74c48c0907fdcbd8e5754bcfbb3ed3fee67cbee4
// @DATE:Wed Jun 05 10:49:42 CEST 2013

import Routes.{prefix => _prefix, defaultPrefix => _defaultPrefix}
import play.core._
import play.core.Router._
import play.core.j._

import play.api.mvc._


import Router.queryString


// @LINE:38
// @LINE:35
// @LINE:34
// @LINE:33
// @LINE:32
// @LINE:31
// @LINE:28
// @LINE:25
// @LINE:24
// @LINE:21
// @LINE:20
// @LINE:19
// @LINE:16
// @LINE:13
// @LINE:12
// @LINE:9
// @LINE:6
package controllers {

// @LINE:38
class ReverseAssets {
    

// @LINE:38
def at(file:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "assets/" + implicitly[PathBindable[String]].unbind("file", file))
}
                                                
    
}
                          

// @LINE:25
// @LINE:24
class ReverseAdmin {
    

// @LINE:25
def saveUser(): Call = {
   Call("POST", _prefix + { _defaultPrefix } + "newuser")
}
                                                

// @LINE:24
def createUser(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "newuser")
}
                                                
    
}
                          

// @LINE:13
// @LINE:12
class ReverseSettings {
    

// @LINE:12
def settings(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "settings")
}
                                                

// @LINE:13
def saveSettings(): Call = {
   Call("POST", _prefix + { _defaultPrefix } + "settings")
}
                                                
    
}
                          

// @LINE:9
class ReverseAbout {
    

// @LINE:9
def about(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "about")
}
                                                
    
}
                          

// @LINE:6
class ReverseHome {
    

// @LINE:6
def home(): Call = {
   Call("GET", _prefix)
}
                                                
    
}
                          

// @LINE:35
// @LINE:34
// @LINE:33
// @LINE:32
// @LINE:31
// @LINE:28
// @LINE:21
// @LINE:20
// @LINE:19
class ReverseApplication {
    

// @LINE:28
def getCooking(): Call = {
   Call("POST", _prefix + { _defaultPrefix } + "getcooking")
}
                                                

// @LINE:32
def getlibrary(): Call = {
   Call("POST", _prefix + { _defaultPrefix } + "getlibrary")
}
                                                

// @LINE:19
def login(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "login")
}
                                                

// @LINE:35
def getalllibraries(): Call = {
   Call("POST", _prefix + { _defaultPrefix } + "getalllibraries")
}
                                                

// @LINE:31
def javascriptRoutes(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "assets/javascripts/routes")
}
                                                

// @LINE:34
def getallcircuits(): Call = {
   Call("POST", _prefix + { _defaultPrefix } + "getallcircuits")
}
                                                

// @LINE:21
def logout(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "logout")
}
                                                

// @LINE:33
def savecircuit(): Call = {
   Call("POST", _prefix + { _defaultPrefix } + "savecircuit")
}
                                                

// @LINE:20
def authenticate(): Call = {
   Call("POST", _prefix + { _defaultPrefix } + "login")
}
                                                
    
}
                          

// @LINE:16
class ReverseHelp {
    

// @LINE:16
def help(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "help")
}
                                                
    
}
                          
}
                  


// @LINE:38
// @LINE:35
// @LINE:34
// @LINE:33
// @LINE:32
// @LINE:31
// @LINE:28
// @LINE:25
// @LINE:24
// @LINE:21
// @LINE:20
// @LINE:19
// @LINE:16
// @LINE:13
// @LINE:12
// @LINE:9
// @LINE:6
package controllers.javascript {

// @LINE:38
class ReverseAssets {
    

// @LINE:38
def at : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Assets.at",
   """
      function(file) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "assets/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("file", file)})
      }
   """
)
                        
    
}
              

// @LINE:25
// @LINE:24
class ReverseAdmin {
    

// @LINE:25
def saveUser : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Admin.saveUser",
   """
      function() {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "newuser"})
      }
   """
)
                        

// @LINE:24
def createUser : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Admin.createUser",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "newuser"})
      }
   """
)
                        
    
}
              

// @LINE:13
// @LINE:12
class ReverseSettings {
    

// @LINE:12
def settings : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Settings.settings",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "settings"})
      }
   """
)
                        

// @LINE:13
def saveSettings : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Settings.saveSettings",
   """
      function() {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "settings"})
      }
   """
)
                        
    
}
              

// @LINE:9
class ReverseAbout {
    

// @LINE:9
def about : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.About.about",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "about"})
      }
   """
)
                        
    
}
              

// @LINE:6
class ReverseHome {
    

// @LINE:6
def home : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Home.home",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + """"})
      }
   """
)
                        
    
}
              

// @LINE:35
// @LINE:34
// @LINE:33
// @LINE:32
// @LINE:31
// @LINE:28
// @LINE:21
// @LINE:20
// @LINE:19
class ReverseApplication {
    

// @LINE:28
def getCooking : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.getCooking",
   """
      function() {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "getcooking"})
      }
   """
)
                        

// @LINE:32
def getlibrary : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.getlibrary",
   """
      function() {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "getlibrary"})
      }
   """
)
                        

// @LINE:19
def login : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.login",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "login"})
      }
   """
)
                        

// @LINE:35
def getalllibraries : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.getalllibraries",
   """
      function() {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "getalllibraries"})
      }
   """
)
                        

// @LINE:31
def javascriptRoutes : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.javascriptRoutes",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "assets/javascripts/routes"})
      }
   """
)
                        

// @LINE:34
def getallcircuits : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.getallcircuits",
   """
      function() {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "getallcircuits"})
      }
   """
)
                        

// @LINE:21
def logout : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.logout",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "logout"})
      }
   """
)
                        

// @LINE:33
def savecircuit : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.savecircuit",
   """
      function() {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "savecircuit"})
      }
   """
)
                        

// @LINE:20
def authenticate : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.authenticate",
   """
      function() {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "login"})
      }
   """
)
                        
    
}
              

// @LINE:16
class ReverseHelp {
    

// @LINE:16
def help : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Help.help",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "help"})
      }
   """
)
                        
    
}
              
}
        


// @LINE:38
// @LINE:35
// @LINE:34
// @LINE:33
// @LINE:32
// @LINE:31
// @LINE:28
// @LINE:25
// @LINE:24
// @LINE:21
// @LINE:20
// @LINE:19
// @LINE:16
// @LINE:13
// @LINE:12
// @LINE:9
// @LINE:6
package controllers.ref {

// @LINE:38
class ReverseAssets {
    

// @LINE:38
def at(path:String, file:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Assets.at(path, file), HandlerDef(this, "controllers.Assets", "at", Seq(classOf[String], classOf[String]), "GET", """ Map static resources from the /public folder to the /assets URL path""", _prefix + """assets/$file<.+>""")
)
                      
    
}
                          

// @LINE:25
// @LINE:24
class ReverseAdmin {
    

// @LINE:25
def saveUser(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Admin.saveUser(), HandlerDef(this, "controllers.Admin", "saveUser", Seq(), "POST", """""", _prefix + """newuser""")
)
                      

// @LINE:24
def createUser(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Admin.createUser(), HandlerDef(this, "controllers.Admin", "createUser", Seq(), "GET", """ Admination""", _prefix + """newuser""")
)
                      
    
}
                          

// @LINE:13
// @LINE:12
class ReverseSettings {
    

// @LINE:12
def settings(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Settings.settings(), HandlerDef(this, "controllers.Settings", "settings", Seq(), "GET", """ The settings page""", _prefix + """settings""")
)
                      

// @LINE:13
def saveSettings(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Settings.saveSettings(), HandlerDef(this, "controllers.Settings", "saveSettings", Seq(), "POST", """""", _prefix + """settings""")
)
                      
    
}
                          

// @LINE:9
class ReverseAbout {
    

// @LINE:9
def about(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.About.about(), HandlerDef(this, "controllers.About", "about", Seq(), "GET", """ The about page""", _prefix + """about""")
)
                      
    
}
                          

// @LINE:6
class ReverseHome {
    

// @LINE:6
def home(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Home.home(), HandlerDef(this, "controllers.Home", "home", Seq(), "GET", """ The home page""", _prefix + """""")
)
                      
    
}
                          

// @LINE:35
// @LINE:34
// @LINE:33
// @LINE:32
// @LINE:31
// @LINE:28
// @LINE:21
// @LINE:20
// @LINE:19
class ReverseApplication {
    

// @LINE:28
def getCooking(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.getCooking(), HandlerDef(this, "controllers.Application", "getCooking", Seq(), "POST", """ JSON to model to simulation to JSON call""", _prefix + """getcooking""")
)
                      

// @LINE:32
def getlibrary(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.getlibrary(), HandlerDef(this, "controllers.Application", "getlibrary", Seq(), "POST", """""", _prefix + """getlibrary""")
)
                      

// @LINE:19
def login(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.login(), HandlerDef(this, "controllers.Application", "login", Seq(), "GET", """ Authentication""", _prefix + """login""")
)
                      

// @LINE:35
def getalllibraries(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.getalllibraries(), HandlerDef(this, "controllers.Application", "getalllibraries", Seq(), "POST", """""", _prefix + """getalllibraries""")
)
                      

// @LINE:31
def javascriptRoutes(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.javascriptRoutes(), HandlerDef(this, "controllers.Application", "javascriptRoutes", Seq(), "GET", """ Javascript routing""", _prefix + """assets/javascripts/routes""")
)
                      

// @LINE:34
def getallcircuits(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.getallcircuits(), HandlerDef(this, "controllers.Application", "getallcircuits", Seq(), "POST", """""", _prefix + """getallcircuits""")
)
                      

// @LINE:21
def logout(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.logout(), HandlerDef(this, "controllers.Application", "logout", Seq(), "GET", """""", _prefix + """logout""")
)
                      

// @LINE:33
def savecircuit(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.savecircuit(), HandlerDef(this, "controllers.Application", "savecircuit", Seq(), "POST", """""", _prefix + """savecircuit""")
)
                      

// @LINE:20
def authenticate(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.authenticate(), HandlerDef(this, "controllers.Application", "authenticate", Seq(), "POST", """""", _prefix + """login""")
)
                      
    
}
                          

// @LINE:16
class ReverseHelp {
    

// @LINE:16
def help(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Help.help(), HandlerDef(this, "controllers.Help", "help", Seq(), "GET", """ The help page""", _prefix + """help""")
)
                      
    
}
                          
}
                  
      