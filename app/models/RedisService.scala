package models

import com.redis.RedisClientPool
import play.api.Play
import play.api.Play.current
import play.api.Logger

class RedisService(redisUrl: String) {
  val pool = {
    val uri = new java.net.URI(redisUrl)
    val host = uri.getHost
    val port = uri.getPort
    val secret = uri.getUserInfo.split(":")(1)
    Logger.info(s"Redis host: $host, Redis port: $port")
    new RedisClientPool(host, port, secret=Some(secret))
  }
  
  def set(key: String, value: String, expiration: Int) {
    pool.withClient { client =>
      val ret = client.set(key, value)
      if (expiration > 0) {
        client.expire(key, expiration)
      }
      ret
    }
  }
  
  def get(key: String) = pool.withClient { client =>
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
