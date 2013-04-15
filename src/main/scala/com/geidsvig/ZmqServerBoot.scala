package com.geidsvig

import org.zeromq.ZMQ

/**
 * Bootstrap for ZmqServer Akka microkernel.
 */
class ZmqServerBoot extends akka.kernel.Bootable {
  def startup = {
    
    val config = com.typesafe.config.ConfigFactory.load()

    printf("Version string: %s, Version int: %d\n", ZMQ.getVersionString, ZMQ.getFullVersion)

    // start up server
    
    //HelloWorldServer
    
    //WeatherUpdateServer
    
    parallel.Ventilator
    parallel.Sink

  }

  def shutdown = {

  }
}

