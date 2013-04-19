package com.geidsvig

import org.zeromq.ZMQ
import org.zeromq.ZMQ.Context
import akka.actor.ActorSystem
import akka.actor.Props
import com.geidsvig.parallel.ZMQRequirements
import parallel._
import com.geidsvig.zmq.RouterReqRequirements
import com.geidsvig.zmq.RouterReq

/**
 * Bootstrap for ZmqServer Akka microkernel.
 */
class ZmqServerBoot extends akka.kernel.Bootable {
  def startup = {
    
    val config = com.typesafe.config.ConfigFactory.load()
    val system = ActorSystem("zmqpServerSystem", config)

    printf("Version string: %s, Version int: %d\n", ZMQ.getVersionString, ZMQ.getFullVersion)

    val routerBind = system.settings.config.getString("zmq.router.bind") // "tcp://*:5559"
    
    trait RouterReqDependencies extends RouterReqRequirements {
      val zmqContext: Context = ZMQ.context(1)
    }
    val router = system.actorOf(Props(new RouterReq(routerBind) with RouterReqDependencies))
    
  }

  def shutdown = {

  }
}

