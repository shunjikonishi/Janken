package models

import play.api.Logger

object JankenService {
  var rooms = Map[String, JankenService]()
  def get(name: String) = rooms.get(name)
  def open(name: String) = {
    rooms.get(name) match {
      case Some(x) => 
        None
      case None => 
        val service = new JankenService(name, RedisService)
        rooms += name -> service
        Some(service)
    }
  }
  
  def close(name: String) = {
    rooms.get(name).foreach(_.close)
  }
}

class JankenService(val name: String, redis: RedisService) {
  Logger.info("Room opened: " + name)
  
  def close = {
    Logger.info("Room closed: " + name)
  }
  
  override def toString = "JankenService: " + name
}
