package models

object RedisTest {
  def apply(pool: JedisPool, channel: String) = new RedisTest(pool, channel)
}

class RedisTest(pool: JedisPool, channel: String) {
  Logger.info("Connected: " + channel)
  
  private val jedis = poolgetResource
  
  lazy val in = Iteratee.foreach[String](jedis.publish(channel, _)).map { _ =>
    Logger.info("Disconnected: " + name)
    close
  }
  
  lazy val out = 
  private def close = {
    pool.returnResourceObject(jedis)
  }
  class MyJedisPubSub extends JedisPubSub {
    override def onMessage(channel: String, msg: String) {
      println("onMessage: " + channel + ", " + msg)
    }

    override def onPMessage(pattern: String, channel: String, msg: String) {
      println("onPMessage: " + pattern + ", " + channel + ", " + msg)
    }
    
    override def onSubscribe(channel: String, sc: Int) {
    }
    
    override def onUnsubscribe(channel: String, sc: Int) {
    }
    
    override def onPSubscribe(channel: String, sc: Int) {
    }
    
    override def onPUnsubscribe(channel: String, sc: Int) {
    }
  }
}