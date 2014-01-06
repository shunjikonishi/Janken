package models

import com.redis.RedisClientPool
import com.redis.Publisher
import com.redis.Subscriber
import com.redis.Register
import com.redis.Subscribe
import com.redis.Unsubscribe
import com.redis.Publish
import com.redis.{ PubSubMessage, S, U, M, E}
import play.api.Play
import play.api.Play.current
import play.api.Logger
import play.api.libs.iteratee.Iteratee
import play.api.libs.iteratee.Concurrent
import play.api.libs.concurrent.Akka
import akka.actor.Props
import scala.concurrent.ExecutionContext.Implicits.global

class RedisService(redisUrl: String) {
  lazy val pool = {
    val uri = new java.net.URI(redisUrl)
    val host = uri.getHost
    val port = uri.getPort
    val secret = uri.getUserInfo.split(":")(1)
    Logger.info(s"Redis host: $host, Redis port: $port")
    new RedisClientPool(host, port, secret=Some(secret))
  }
  
  def set(key: String, value: Object, expiration: Int) {
    pool.withClient { client =>
      val ret = client.set(key, value)
      if (expiration > 0) {
        client.expire(key, expiration)
      }
      ret
    }
  }
  
  def getString(key: String) = pool.withClient { client =>
    client.get[String](key)
  }
  
  def incr(key: String) = pool.withClient { client =>
    try {
      client.incr(key)
    } catch {
      case e: Exception =>
        e.printStackTrace
        None
    }
  }
  
  def decr(key: String) = pool.withClient { client =>
    try {
      client.decr(key)
    } catch {
      case e: Exception =>
        e.printStackTrace
        None
    }
  }
}

object RedisService extends RedisService(Play.configuration.getString("redis.uri").get)

class RedisWebSocket(pool: RedisClientPool, channel: String) {
  
  val client = pool.pool.borrowObject
  val (msgEnumerator, msgChannel) = Concurrent.broadcast[String]
  lazy val pub = Akka.system.actorOf(Props(new Publisher(client)))
  lazy val sub = {
    val ret = Akka.system.actorOf(Props(new Subscriber(client)))
    ret ! Register(callback)
    ret ! Subscribe(Array(channel))
    ret
  }
  
  lazy val in = Iteratee.foreach[String] { str =>
    Logger.info("in: " + str)
    pub ! Publish(channel, str)
  }.map { _ =>
    close
  }
  
  def callback(pubsub: PubSubMessage) = pubsub match {
    case E(ex) => 
      Logger.info("Fatal error caused consumer dead. Please init new consumer reconnecting to master or connect to backup")
      ex.printStackTrace
    case S(channel, no) => 
      Logger.info("subscribed to " + channel + " and count = " + no)
    case U(channel, no) => 
      Logger.info("unsubscribed from " + channel + " and count = " + no)
    case M(channel, msg) => 
      msgChannel.push(msg)
  }
  
  def close = {
    Logger.info("close: " + channel)
    sub ! Unsubscribe(Array(channel))
    pool.pool.returnObject(client)
  }

}

