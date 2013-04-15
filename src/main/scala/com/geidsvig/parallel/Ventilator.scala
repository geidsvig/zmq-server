package com.geidsvig.parallel

/*
*
*  Task ventilator in Scala
*  Binds PUSH socket to tcp://localhost:5557
*  Sends batch of tasks to workers via that socket
*
* @author Giovanni Ruggiero
* @email giovanni.ruggiero@gmail.com
*/

import java.util.Random
import org.zeromq.ZMQ
import org.zeromq.ZMQ.{Context, Socket}
import akka.actor.Actor

trait ZMQRequirements {
  val zmqContext: Context
}

class Ventilator extends Actor {
  this: ZMQRequirements =>

  var zmqSender: Socket = _
    
  def receive = {
    case 'init => {
      //  Socket to send messages on
      zmqSender = zmqContext.socket(ZMQ.PUSH)
      zmqSender.bind("tcp://*:5557")
    }
    case 'start => {
      println("Sending tasks to workers...\n")

      //  The first message is "0" and signals start of batch
      zmqSender.send("0\u0000".getBytes(), 0)

      //  Initialize random number generator
      val srandom = new Random(System.currentTimeMillis())

      //  Send 100 tasks
      var total_msec = 0 //  Total expected cost in msecs
      for (i <- 1 to 100) {
        //  Random workload from 1 to 100msecs
        val workload = srandom.nextInt(100) + 1
        total_msec += workload
        print(workload + ".")
        val string = String.format("%d\u0000", workload.asInstanceOf[Integer])
        zmqSender.send(string.getBytes(), 0)
      }
      println("Total expected cost: " + total_msec + " msec")

    }
    case _ => println("unsupported message")
  }

}