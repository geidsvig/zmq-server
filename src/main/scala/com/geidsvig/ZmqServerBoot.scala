package com.geidsvig

/**
 * Bootstrap for ZmqServer Akka microkernel.
 */
class ZmqServerBoot extends akka.kernel.Bootable {
  def startup = {
    
    val config = com.typesafe.config.ConfigFactory.load()
    println("Boot successful")

  }

  def shutdown = {

  }
}

