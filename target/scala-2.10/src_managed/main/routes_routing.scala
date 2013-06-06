// @SOURCE:D:/workspace/play/genesis/conf/routes
// @HASH:74c48c0907fdcbd8e5754bcfbb3ed3fee67cbee4
// @DATE:Wed Jun 05 10:49:42 CEST 2013


import play.core._
import play.core.Router._
import play.core.j._

import play.api.mvc._


import Router.queryString

object Routes extends Router.Routes {

private var _prefix = "/"

def setPrefix(prefix: String) {
  _prefix = prefix  
  List[(String,Routes)]().foreach {
    case (p, router) => router.setPrefix(prefix + (if(prefix.endsWith("/")) "" else "/") + p)
  }
}

def prefix = _prefix

lazy val defaultPrefix = { if(Routes.prefix.endsWith("/")) "" else "/" } 


// @LINE:6
private[this] lazy val controllers_Home_home0 = Route("GET", PathPattern(List(StaticPart(Routes.prefix))))
        

// @LINE:9
private[this] lazy val controllers_About_about1 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("about"))))
        

// @LINE:12
private[this] lazy val controllers_Settings_settings2 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("settings"))))
        

// @LINE:13
private[this] lazy val controllers_Settings_saveSettings3 = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("settings"))))
        

// @LINE:16
private[this] lazy val controllers_Help_help4 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("help"))))
        

// @LINE:19
private[this] lazy val controllers_Application_login5 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("login"))))
        

// @LINE:20
private[this] lazy val controllers_Application_authenticate6 = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("login"))))
        

// @LINE:21
private[this] lazy val controllers_Application_logout7 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("logout"))))
        

// @LINE:24
private[this] lazy val controllers_Admin_createUser8 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("newuser"))))
        

// @LINE:25
private[this] lazy val controllers_Admin_saveUser9 = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("newuser"))))
        

// @LINE:28
private[this] lazy val controllers_Application_getCooking10 = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("getcooking"))))
        

// @LINE:31
private[this] lazy val controllers_Application_javascriptRoutes11 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("assets/javascripts/routes"))))
        

// @LINE:32
private[this] lazy val controllers_Application_getlibrary12 = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("getlibrary"))))
        

// @LINE:33
private[this] lazy val controllers_Application_savecircuit13 = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("savecircuit"))))
        

// @LINE:34
private[this] lazy val controllers_Application_getallcircuits14 = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("getallcircuits"))))
        

// @LINE:35
private[this] lazy val controllers_Application_getalllibraries15 = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("getalllibraries"))))
        

// @LINE:38
private[this] lazy val controllers_Assets_at16 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("assets/"),DynamicPart("file", """.+"""))))
        
def documentation = List(("""GET""", prefix,"""controllers.Home.home"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """about""","""controllers.About.about"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """settings""","""controllers.Settings.settings"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """settings""","""controllers.Settings.saveSettings"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """help""","""controllers.Help.help"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """login""","""controllers.Application.login"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """login""","""controllers.Application.authenticate"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """logout""","""controllers.Application.logout"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """newuser""","""controllers.Admin.createUser"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """newuser""","""controllers.Admin.saveUser"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """getcooking""","""controllers.Application.getCooking"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """assets/javascripts/routes""","""controllers.Application.javascriptRoutes"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """getlibrary""","""controllers.Application.getlibrary"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """savecircuit""","""controllers.Application.savecircuit"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """getallcircuits""","""controllers.Application.getallcircuits"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """getalllibraries""","""controllers.Application.getalllibraries"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """assets/$file<.+>""","""controllers.Assets.at(path:String = "/public", file:String)""")).foldLeft(List.empty[(String,String,String)]) { (s,e) => e match {
  case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
  case l => s ++ l.asInstanceOf[List[(String,String,String)]] 
}}
       
    
def routes:PartialFunction[RequestHeader,Handler] = {        

// @LINE:6
case controllers_Home_home0(params) => {
   call { 
        invokeHandler(controllers.Home.home, HandlerDef(this, "controllers.Home", "home", Nil,"GET", """ The home page""", Routes.prefix + """"""))
   }
}
        

// @LINE:9
case controllers_About_about1(params) => {
   call { 
        invokeHandler(controllers.About.about, HandlerDef(this, "controllers.About", "about", Nil,"GET", """ The about page""", Routes.prefix + """about"""))
   }
}
        

// @LINE:12
case controllers_Settings_settings2(params) => {
   call { 
        invokeHandler(controllers.Settings.settings, HandlerDef(this, "controllers.Settings", "settings", Nil,"GET", """ The settings page""", Routes.prefix + """settings"""))
   }
}
        

// @LINE:13
case controllers_Settings_saveSettings3(params) => {
   call { 
        invokeHandler(controllers.Settings.saveSettings, HandlerDef(this, "controllers.Settings", "saveSettings", Nil,"POST", """""", Routes.prefix + """settings"""))
   }
}
        

// @LINE:16
case controllers_Help_help4(params) => {
   call { 
        invokeHandler(controllers.Help.help, HandlerDef(this, "controllers.Help", "help", Nil,"GET", """ The help page""", Routes.prefix + """help"""))
   }
}
        

// @LINE:19
case controllers_Application_login5(params) => {
   call { 
        invokeHandler(controllers.Application.login, HandlerDef(this, "controllers.Application", "login", Nil,"GET", """ Authentication""", Routes.prefix + """login"""))
   }
}
        

// @LINE:20
case controllers_Application_authenticate6(params) => {
   call { 
        invokeHandler(controllers.Application.authenticate, HandlerDef(this, "controllers.Application", "authenticate", Nil,"POST", """""", Routes.prefix + """login"""))
   }
}
        

// @LINE:21
case controllers_Application_logout7(params) => {
   call { 
        invokeHandler(controllers.Application.logout, HandlerDef(this, "controllers.Application", "logout", Nil,"GET", """""", Routes.prefix + """logout"""))
   }
}
        

// @LINE:24
case controllers_Admin_createUser8(params) => {
   call { 
        invokeHandler(controllers.Admin.createUser, HandlerDef(this, "controllers.Admin", "createUser", Nil,"GET", """ Admination""", Routes.prefix + """newuser"""))
   }
}
        

// @LINE:25
case controllers_Admin_saveUser9(params) => {
   call { 
        invokeHandler(controllers.Admin.saveUser, HandlerDef(this, "controllers.Admin", "saveUser", Nil,"POST", """""", Routes.prefix + """newuser"""))
   }
}
        

// @LINE:28
case controllers_Application_getCooking10(params) => {
   call { 
        invokeHandler(controllers.Application.getCooking, HandlerDef(this, "controllers.Application", "getCooking", Nil,"POST", """ JSON to model to simulation to JSON call""", Routes.prefix + """getcooking"""))
   }
}
        

// @LINE:31
case controllers_Application_javascriptRoutes11(params) => {
   call { 
        invokeHandler(controllers.Application.javascriptRoutes, HandlerDef(this, "controllers.Application", "javascriptRoutes", Nil,"GET", """ Javascript routing""", Routes.prefix + """assets/javascripts/routes"""))
   }
}
        

// @LINE:32
case controllers_Application_getlibrary12(params) => {
   call { 
        invokeHandler(controllers.Application.getlibrary, HandlerDef(this, "controllers.Application", "getlibrary", Nil,"POST", """""", Routes.prefix + """getlibrary"""))
   }
}
        

// @LINE:33
case controllers_Application_savecircuit13(params) => {
   call { 
        invokeHandler(controllers.Application.savecircuit, HandlerDef(this, "controllers.Application", "savecircuit", Nil,"POST", """""", Routes.prefix + """savecircuit"""))
   }
}
        

// @LINE:34
case controllers_Application_getallcircuits14(params) => {
   call { 
        invokeHandler(controllers.Application.getallcircuits, HandlerDef(this, "controllers.Application", "getallcircuits", Nil,"POST", """""", Routes.prefix + """getallcircuits"""))
   }
}
        

// @LINE:35
case controllers_Application_getalllibraries15(params) => {
   call { 
        invokeHandler(controllers.Application.getalllibraries, HandlerDef(this, "controllers.Application", "getalllibraries", Nil,"POST", """""", Routes.prefix + """getalllibraries"""))
   }
}
        

// @LINE:38
case controllers_Assets_at16(params) => {
   call(Param[String]("path", Right("/public")), params.fromPath[String]("file", None)) { (path, file) =>
        invokeHandler(controllers.Assets.at(path, file), HandlerDef(this, "controllers.Assets", "at", Seq(classOf[String], classOf[String]),"GET", """ Map static resources from the /public folder to the /assets URL path""", Routes.prefix + """assets/$file<.+>"""))
   }
}
        
}
    
}
        