package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.cache.Cache
import play.api.Play.current
import play.api.Play

import models.RedisService
import models.JankenService

object Application extends Controller {
  
  /*
  new Thread() {
    override def run(): Unit = {
      val pool = Play.current.plugin[RedisPlugin].get.sedisPool
      val value = pool.withJedisClient { client =>
        client.subscribe(new MyJedisPubSub(), "test")
      } 
    }
  }.start()
  */
  
  def index = Action {
    val value = RedisService.getString("aaa").getOrElse("Not found")
    Ok(views.html.index(value))
  }
  
  def open(name: String) = Action {
    val service = JankenService.open(name)
    Ok("Janken: " + service)
  }
  
  def room(name: String) = Action {
    val service = JankenService.get(name)
    Ok("Janken: " + service)
  }
  
  
  
  def set(key: String, value: String) = Action {
    RedisService.set(key, value, 60)
    Ok(key + " = " + value)
  }
  
  def incr(key: String) = Action { 
    val ret = RedisService.incr(key)
    Ok(ret.toString)
  }
  
  def decr(key: String) = Action { 
    val ret = RedisService.decr(key)
    Ok(ret.toString)
  }
  
}