package com.geidsvig.parallel

/*
*
*  Task sink in Scala
*  Binds PULL socket to tcp://localhost:5558
*  Collects results from workers via that socket
*
* @author Giovanni Ruggiero
* @email giovanni.ruggiero@gmail.com
*/

import org.zeromq.ZMQ
import org.zeromq.ZMQ.{ Context, Socket }
import akka.actor.Actor

class Sink extends Actor {
  this: ZMQRequirements =>

  var receiver: Socket = _

  def receive = {
    case 'init => {
      //  Prepare our context and socket
      receiver = zmqContext.socket(ZMQ.PULL)
      receiver.bind("tcp://*:5558")
    }
    case 'start => {
      //  Wait for start of batch
      val string = new String(receiver.recv(0))

      //  Start our clock now
      val tstart = System.currentTimeMillis()

      //  Process 100 confirmations
      val total_msec = 0 //  Total calculated cost in msecs
      for (task_nbr <- 1 to 100) {
        val string = new String(receiver.recv(0)).trim()
        if ((task_nbr / 10) * 10 == task_nbr) {
          System.out.print(":")
        } else {
          System.out.print(".")
        }
        System.out.flush()
      }
      //  Calculate and report duration of batch
      val tend = System.currentTimeMillis()

      println("Total elapsed time: " + (tend - tstart) + " msec")
    }
    case _ => {}
  }

}