package test

import org.specs2.mutable._
import models._
import factories._
import play.api.test._
import play.api.test.Helpers._
import play.api.db.DB
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import org.json.JSONObject
import play.api.libs.json.JsObject
import play.api.libs.json.JsArray

class ProteinJsonFactorySpec extends Specification{
 sequential
	"LibraryListJson" should {
	  "return correct value" in {
	    running(FakeApplication()) {
	      val lib = ProteinJSONFactory.libraryListJSON(-1)
	      val libName = (lib \\ "libraryname").map(_.as[String])
	      val libId = (lib \\ "libraryId").map(_.as[Int])
	      libName must be equalTo(List("default","newLibrary1"))
	      libId must be equalTo(List(0,2))
	    }
	  }
	}

	"ProteinNotParamJSON" should {
	  "return correct value" in {
	    running(FakeApplication()) {
	      val protein = ProteinJSONFactory.proteinNotParamsJSON()
	      val proteinA = (protein \\ "A").map(_.as[JsObject])
	      for (info <- proteinA) {
	        val k1 = (info \ "k1").as[Double]
	        val km = (info \ "km").as[Double]
	        val n = (info \ "n").as[Int]
	        k1 must be equalTo(4.7313)
	        km must be equalTo(224.0227)
	        n must be equalTo(1)
	      }
	    }
	  }
	}
		
	"ProteinAllAndParamJSON" should {
	  "return correct value" in {
	    running(FakeApplication()) {
	      val lib = ProteinJSONFactory.proteinAllAndParamsJSON()
	      val input1 = (lib \\ "input1").map(_.as[String]).distinct
	      val input2 = (lib \\ "input2").map(_.as[String]).distinct
	      val n = ( lib \\ "n").map(_.as[Int]).distinct.sorted
	      input1 must equalTo(List("A","B","C","D","E","F","G","H","I"))
	      input2 must equalTo(List("B","C","D","E","F","G","H","I","J"))
	      n must equalTo(List(1,2,3,4,5))
	    }
	  }
	}

	"ProteinCDSParamJSON" should {
	  "return correct value" in {
	    running(FakeApplication()) {
	      val protein = ProteinJSONFactory.proteinCDSParamsJSON()
	      val proteinA = (protein \\ "A").map(_.as[JsObject])
	      for (info <- proteinA) {
	        val k2 = (info \ "k2").as[Double]
	        val d1 = (info \ "d1").as[Double]
	        val d2 = (info \ "d2").as[Double]
	        k2 must be equalTo(4.6337)
	        d1 must be equalTo(0.024)
	        d2 must be equalTo(0.8466)
	      }
	    }
	  }
	}
		"ProteinAndParamJSON" should {
	  "return correct value" in {
	    running(FakeApplication()) {
	      val protein = ProteinJSONFactory.proteinAndParamsJSON("A")
	      val proteinB = (protein \\ "B").map(_.as[JsObject])
	      for (info <- proteinB) {
	        val k1 = (info \ "k1").as[Double]
	        val km = (info \ "km").as[Double]
	        val n = (info \ "n").as[Int]
	        k1 must be equalTo(4.5272)
	        km must be equalTo(238.9569)
	        n must be equalTo(3)
	      }
	    }
	  }
	}
		
	"ProteinAllAndParamJSON" should {
	  "return correct value" in {
	    running(FakeApplication()) {
	      val lib = ProteinJSONFactory.proteinAllAndParamsJSON(0)
	      val input1 = (lib \\ "input1").map(_.as[String]).distinct
	      val input2 = (lib \\ "input2").map(_.as[String]).distinct
	      val n = ( lib \\ "n").map(_.as[Int]).distinct.sorted
	      input1 must equalTo(List("A","B","C","D","E","F","G","H","I"))
	      input2 must equalTo(List("B","C","D","E","F","G","H","I","J"))
	      n must equalTo(List(1,2,3,4,5))
	    }
	  }
	}
	
	/*	
	 * There is a protein B (in db table andparams with input1 is "A" and libraryID is 0) with values(k1:4.5272,km:238.9569,n:3)
	 */
	"ProteinAndParamJSON" should {
	  "return correct value" in {
	    running(FakeApplication()) {
	      val protein = ProteinJSONFactory.proteinAndParamsJSON("A", 0)
	      val proteinB = (protein \\ "B").map(_.as[JsObject])
	      for (info <- proteinB) {
	        val k1 = (info \ "k1").as[Double]
	        val km = (info \ "km").as[Double]
	        val n = (info \ "n").as[Int]
	        k1 must be equalTo(4.5272)
	        km must be equalTo(238.9569)
	        n must be equalTo(3)
	      }
	    }
	  }
	}
	
	/*	
	 * There is a protein A (in db table andparams with input2 is "J" and libraryID is 0) with values(k1:6.2219,km:236.9308,n:4)
	 */
	"ProteinAndParamJSON" should {
	  "return correct value" in {
	    running(FakeApplication()) {
	      val protein = ProteinJSONFactory.proteinAndParamsJSON("J", 0)
	      val proteinA = (protein \\ "A").map(_.as[JsObject])
	      for (info <- proteinA) {
	        val k1 = (info \ "k1").as[Double]
	        val km = (info \ "km").as[Double]
	        val n = (info \ "n").as[Int]
	        k1 must be equalTo(6.2219)
	        km must be equalTo(236.9308)
	        n must be equalTo(4)
	      }
	    }
	  }
	}
	
	/*
	 * There is a protein A (in db table notparams with libraryID 0) with values(k1:4.7313,km:224.0227,n:1)
	 */
	"ProteinNotParamJSON" should {
	  "return correct value" in {
	    running(FakeApplication()) {
	      val protein = ProteinJSONFactory.proteinNotParamsJSON(0)
	      val proteinA = (protein \\ "A").map(_.as[JsObject])
	      for (info <- proteinA) {
	        val k1 = (info \ "k1").as[Double]
	        val km = (info \ "km").as[Double]
	        val n = (info \ "n").as[Int]
	        k1 must be equalTo(4.7313)
	        km must be equalTo(224.0227)
	        n must be equalTo(1)
	      }
	    }
	  }
	}
	
  /*
   * There is a protein A (in db table cdsparams with libraryID 0) with values(k2:4.6337,d1:0.024,d2:0.8466)
   */
	"ProteinCDSParamJSON" should {
	  "return correct value" in {
	    running(FakeApplication()) {
	      val protein = ProteinJSONFactory.proteinCDSParamsJSON(0)
	      val proteinA = (protein \\ "A").map(_.as[JsObject])
	      for (info <- proteinA) {
	        val k2 = (info \ "k2").as[Double]
	        val d1 = (info \ "d1").as[Double]
	        val d2 = (info \ "d2").as[Double]
	        k2 must be equalTo(4.6337)
	        d1 must be equalTo(0.024)
	        d2 must be equalTo(0.8466)
	      }
	    }
	  }
	}	
}

