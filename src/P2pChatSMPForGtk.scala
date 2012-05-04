/*
 * This file is part of P2pChatGtk
 *
 * Copyright (C) 2012 Timur Mehrvarz, timur.mehrvarz(at)gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation <http://www.gnu.org/licenses/>, either 
 * version 3 of the License, or (at your option) any later version.
 */

package timur.p2pChat

import timur.p2pCore._

class P2pChatOTRForGtk(p2pSecret:String, smpSecret:String, parent:timur.p2pChat.LogClassTrait) 
  extends P2pChatOTR(p2pSecret:String, smpSecret:String, parent:timur.p2pChat.LogClassTrait) {

  // this method makes it possible to run P2pChatOTRForGtk from within a runnable jar
  // it tries to load "relaykey.pub" from inside the JAR
  // if this fails, it tries to load "relaykey.pub" from the filesystem
  override def initHostPubKey() {
  	val relayKeyPathInRunnableJar = "/relaykey.pub"
    val is = getClass.getResourceAsStream(relayKeyPathInRunnableJar)
  	if(is==null) {
      hostPubKey = io.Source.fromFile("relaykey.pub").mkString
      if(hostPubKey.length>0)
      	log("initHostPubKey from filesystem="+hostPubKey.substring(0,math.min(60,hostPubKey.length)))
  	} else {
      hostPubKey = io.Source.fromInputStream(is).mkString
      if(hostPubKey.length>0)
        log("initHostPubKey from runnableJar="+hostPubKey.substring(0,math.min(60,hostPubKey.length)))
    }
    if(hostPubKey.length<=0)
      log("initHostPubKey failed to read keyFile from jar or filesystem")
  }

  /** a p2p connection has just ended */
  override def p2pExit(ret:Int) {
    // do NOT call System.exit()
    log("p2pExit ret="+ret)
  }

/*
  override def p2pEncryptedCommunication() {
    super.p2pEncryptedCommunication
  }

  override def p2pReceiveHandler(str:String, host:String, port:Int) {
    // disconnect our relay connection (stay connected via direct p2p)
    //if(relaySocket!=null && !relayBasedP2pCommunication) {
    //  log("disconnect our relay connection: relaySocket.close")
    //  relayQuitFlag=true
    //  relaySocket.close
    //  relaySocket=null
    //}

		log(esc1+"From network:"+str.length+":"+esc3+str.substring(0,math.min(str.length,60))+esc2)
		val stringTLV = otrInterface.messageReceiving(accountname, protocol, recipient, str, otrCallbacks)
		if(stringTLV!=null){
			val msg = stringTLV.msg
			if(msg.length>0) {
			  //log(esc1+"From OTR:"+msg.length+":"+esc2+msg)
			  log("< "+msg)
			}
		}
  }
*/
}

