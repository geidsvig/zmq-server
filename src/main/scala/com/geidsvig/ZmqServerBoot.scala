package com.geidsvig

import org.zeromq.ZMQ
import org.zeromq.ZMQ.Context
import akka.actor.ActorSystem
import akka.actor.Props
import com.geidsvig.parallel.ZMQRequirements
import parallel._

/**
 * Bootstrap for ZmqServer Akka microkernel.
 */
class ZmqServerBoot extends akka.kernel.Bootable {
  def startup = {
    
    val config = com.typesafe.config.ConfigFactory.load()
    val system = ActorSystem("zmqpServerSystem", config)

    printf("Version string: %s, Version int: %d\n", ZMQ.getVersionString, ZMQ.getFullVersion)

    // start up server
    
    //HelloWorldServer
    
    //WeatherUpdateServer
    
    trait ZMQDependencies extends ZMQRequirements {
      val zmqContext: Context = ZMQ.context(1)
    }
    val ventilator = system.actorOf(Props(new Ventilator with ZMQDependencies))
    val sink = system.actorOf(Props(new Sink with ZMQDependencies))
    
    ventilator ! 'init
    sink ! 'init
    sink ! 'start
    
    println("Waiting 20 seconds for you to start your workers...")
    Thread.sleep(20000) // 20 seconds
    ventilator ! 'start
  }

  def shutdown = {

  }
}

