package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.cache.Cache
import play.api.Play.current
import play.api.Play
import com.typesafe.plugin.RedisPlugin
import org.sedis.Dress
import redis.clients.jedis.JedisPubSub

object Application extends Controller {
  
  new Thread() {
    override def run(): Unit = {
      val pool = Play.current.plugin[RedisPlugin].get.sedisPool
      val value = pool.withJedisClient { client =>
        client.subscribe(new MyJedisPubSub(), "test")
      } 
    }
  }.start()
  
  def index = Action {
    val pool = Play.current.plugin[RedisPlugin].get.sedisPool
    val value = pool.withJedisClient { client =>
      client.get("aaa")
    } 
    Ok(views.html.index(value))
  }
  
}