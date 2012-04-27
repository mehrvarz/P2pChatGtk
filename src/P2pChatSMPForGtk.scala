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

package timur.p2pChatSMP

import timur.p2pCore._

class P2pChatSMPForGtk(p2pSecret:String, smpSecret:String, parent:timur.p2pChatSMP.LogClassTrait) 
  extends P2pChatSMP(p2pSecret:String, smpSecret:String, parent:timur.p2pChatSMP.LogClassTrait) {

  // this method makes it possible to run P2pChatSMPForGtk using a runnable jar
  // it tries to load "relaykey.pub" from inside the runnable JAR
  // if this fails it tries to load "relaykey.pub" from the filesystem
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
}

