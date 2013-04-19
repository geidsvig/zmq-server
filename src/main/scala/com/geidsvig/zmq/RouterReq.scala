package com.geidsvig.zmq

import akka.actor.Actor
import akka.actor.ActorLogging

import org.zeromq.ZMQ
import org.zeromq.ZMQ.{ Context => ZMQContext }
import org.zeromq.ZMQ.{ Socket => ZMQSocket }

trait RouterReqRequirements {
  val zmqContext: ZMQContext
}

/**
 * ZMQ Router with Req/Rep echo behaviour.
 * Every message in is logged then sent back to the requester.
 */
class RouterReq(url: String) extends Actor
  with ActorLogging {
  this: RouterReqRequirements =>

  var zmqSocket: ZMQSocket = _

  val NOFLAGS = 0
  val ENDOFFRAMES = "END_OF_FRAMES"

  override def preStart() {
    zmqSocket = zmqContext.socket(ZMQ.ROUTER)
    zmqSocket.bind(url)
    zmqSocket.setHWM(30000)
    
    self ! 'start
  }

  override def postStop() {
    zmqSocket.unbind(url)
    zmqSocket.close()
  }

  def receive = {
    case 'start => {
      log info("Starting receive loop")
      while (true) {
        val address = zmqSocket.recv()
        val requestFrames = zmqReceive()
        //log info ("Received request: " + requestFrames)
        
        zmqSocket.send(address, ZMQ.SNDMORE)
        zmqSocket.send("".getBytes, ZMQ.SNDMORE)
        requestFrames.map {frame => zmqSocket.send(frame.getBytes, ZMQ.SNDMORE)}
        zmqSocket.send(ENDOFFRAMES.getBytes, NOFLAGS)
      }
    }
    case other => log warning ("Unsupported message %", other)
  }
  
  /**
   * Inbound ZMQ frames exist in the queue.
   * This method takes the set of frames off of the queue and returns a list of frames.
   *
   * @returns list of frames
   */
  private def zmqReceive(): List[String] = {
    var frames: List[String] = List()
    var done = false
    while (!done) {
      Option(zmqSocket.recv(ZMQ.DONTWAIT)).map{s: Array[Byte] => new String(s) } match {
        case Some(frame) if (!frame.equals(ENDOFFRAMES)) => frames ::= frame
        case _ => done = true
      } 
    }
    frames.reverse
  }

}