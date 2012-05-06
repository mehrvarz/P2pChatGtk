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

import java.io.FileNotFoundException
import java.net.Socket
import java.net.InetAddress
import java.io._
import java.util.jar._
import javax.sound.sampled._

import org.gnome.gdk.Event
import org.gnome.gdk.EventKey
import org.gnome.gdk.Keyval
import org.gnome.gdk.Pixbuf
import org.gnome.gtk.Gtk
import org.gnome.gtk.IconSize
import org.gnome.gtk.ScrolledWindow
import org.gnome.gtk.Stock
import org.gnome.gtk.TextBuffer
import org.gnome.gtk.TextIter
import org.gnome.gtk.TextTag
import org.gnome.gtk.TextView
import org.gnome.gtk.VBox
import org.gnome.gtk.Widget
import org.gnome.gtk.Window
import org.gnome.pango.Style
import org.gnome.pango.Weight

import org.gnome.notify.Notification
import org.gnome.notify.Notify

import org.gnome.gtk.PolicyType.ALWAYS
import org.gnome.gtk.PolicyType.NEVER
import org.gnome.gtk.ShadowType.IN
import org.gnome.gtk.WrapMode.NONE
import org.gnome.gtk.WrapMode.WORD
import org.freedesktop.bindings.Time.formatTime

object P2pChatGtk {

  var grey:TextTag = null
  var blue:TextTag = null
  var buffer:TextBuffer = null
  var incomingTextView:TextView = null
  var p2pChatGtk:P2pChatGtk = null
  
  def main(args:Array[String]): Unit = {
    try {
      Gtk.init(args)
    } catch {
      case ex:java.lang.UnsatisfiedLinkError =>
        System.err.println("failed to load java-gnome")  // libjava-gnome-java is not installed
        ex.printStackTrace
        // todo: use some kind of 'toast' to display this issue
        return
    }

    grey = new TextTag()
    blue = new TextTag()
    buffer = new TextBuffer()
    incomingTextView = new TextView(buffer)

    grey.setForeground("#777777")
    blue.setWeight(Weight.BOLD)
    blue.setForeground("blue")
    // Create a TextView which will display incoming text messages (and
    // also echo messages as they are sent). It is set up to be read only
    // and to not have a cursor, thereby conveying the impression that it
    // is just a display (a cursor especially would suggest that the text
    // there can be changed).
    incomingTextView.setEditable(false)
    incomingTextView.setCursorVisible(false)
    incomingTextView.setPaddingBelowParagraph(2)
    // We want word wrapping, otherwise messages wider that the screen
    // width will be truncated. We also need to set up vertical scrolling
    // so that as the conversation continues it won't be inaccessible off
    // the bottom of the screen.
    incomingTextView.setWrapMode(WORD)
    p2pChatGtk = new P2pChatGtk()
    Gtk.main
  }

  /**
   * Append a received (or sent) message to the incoming display.
   */
  def appendMessage(msg:String, outbound:Boolean) {
    val end = buffer.getIterEnd
    buffer.insert(end, "\n")
    if(msg.length>0) {
      val now = System.currentTimeMillis() / 1000
      val timestamp = formatTime("%H:%M:%S\t", now)
      buffer.insert(end, timestamp, grey)
      val colour = if(outbound) blue else null
      var prev = 0
      buffer.insert(end, msg.substring(prev), colour)
    }
    incomingTextView.scrollTo(end)
  }
}

class P2pChatGtk extends timur.p2pChat.LogClassTrait {
  //log("P2pChatGtk new Window()")
  val app = this
  val window = new Window()
  var p2pChatOtrThread:Thread = null
  val top = new VBox(false, 3)

  window.setTitle("P2pChatGtk")
  window.setDefaultSize(1040, 380)
  window.add(top)

  @volatile var p2pChatOTR:P2pChatOTRForGtk = null

  def logUser(msg:String) = synchronized {
    P2pChatGtk.appendMessage(msg, true)
  }

  /** our implementation of timur.p2pChatOTR.LogClassTrait
   *  p2pChatOTR will use this method to print into the gtk window 
   */
  def log(msg:String) = synchronized {
    if(msg.startsWith("< "))
      logUser(msg)
    else if(msg.startsWith("> "))
      logUser(msg.substring(2))
    else
      P2pChatGtk.appendMessage(msg, false)
  }

/*
  def playAudio(audioFile:String) {
    new Thread() {
      override def run() {
        try {
          println("playAudio "+audioFile)

          val ais = javax.sound.sampled.AudioSystem.getAudioInputStream(new File(audioFile))
          val clip = AudioSystem.getLine(new Line.Info(classOf[Clip])).asInstanceOf[Clip]
          clip.open(ais)
          clip.loop(0)

          clip.start
          try { Thread.sleep(200) } catch { case ex:Exception => }
          clip.drain
          clip.stop
    
          try { Thread.sleep(1400) } catch { case ex:Exception => }
          println("clip.close")
          clip.close
        } catch {
          case ex:Exception =>
            println(ex)
        }
      }
    }.start
    try { Thread.sleep(400) } catch { case ex:Exception => }
  }
*/

  window.connect(new Window.DeleteEvent() {
    def onDeleteEvent(source:Widget, event:Event) :Boolean = {
      // user has closed our gtk window
      //println("P2pChatGtk onDeleteEvent -> Gtk.mainQuit")
      //log("P2pChatGtk onDeleteEvent -> Gtk.mainQuit")
      if(p2pChatOTR!=null)
        p2pChatOTR.relayQuit
      Gtk.mainQuit
      //System.exit(0)    // todo
      return false
    }
  })

  val scroll1 = new ScrolledWindow()
  scroll1.setPolicy(NEVER, ALWAYS)
  scroll1.setShadowType(IN)
  scroll1.add(P2pChatGtk.incomingTextView)
  top.packStart(scroll1, true, true, 0)

  // Create the place for the user to enter messages they want to send.
  // The interesting part here is that when the user presses Enter in
  // the TextView it "sends" a message and appends it to the log in the
  // incoming TextView.
  val outgoingTextView = new TextView()
  outgoingTextView.setSizeRequest(0, 20)
  outgoingTextView.setAcceptsTab(false)
  outgoingTextView.setWrapMode(NONE)
  outgoingTextView.connect(new Widget.KeyPressEvent() {
    def onKeyPressEvent(source:Widget, event:EventKey) :Boolean = {
      if(event.getKeyval==Keyval.Return && outgoingTextView.getBuffer.getText.length>0) {
        // user entered text in the GUI
        if(p2pChatOTR==null) {
          // there is currently no active chat session
          // parse outgoingTextView.getBuffer.getText as secret strings
          val tokenArrayOfStrings = outgoingTextView.getBuffer.getText split ' '
          outgoingTextView.getBuffer.setText("")
          p2pChatOtrThread = new Thread("P2pChatOTR") { override def run() {
            val p2pSecret = tokenArrayOfStrings(0)
            val smpSecret = if(tokenArrayOfStrings.length>1) tokenArrayOfStrings(1) else null
            log("starting new chat session...")
            p2pChatOTR = new P2pChatOTRForGtk(p2pSecret,smpSecret,app)
            p2pChatOTR.start
            p2pChatOTR = null

            log("Please enter shared secret to start new chat session...")
          } }
          p2pChatOtrThread.setDaemon(true)
          p2pChatOtrThread.start

        } else {
          // a chat session is active

          // show entered text in log window
          //logUser(outgoingTextView.getBuffer.getText)

          // send entered text to other chat client
          //p2pChatOTR.p2pSend(outgoingTextView.getBuffer.getText)
          p2pChatOTR.otrMsgSend(outgoingTextView.getBuffer.getText)

          outgoingTextView.getBuffer.setText("")
        }

        // don't process the keystroke further.
        return true
      }
      return false
    }
  })

  val scroll2 = new ScrolledWindow()
  scroll2.setPolicy(NEVER, NEVER)
  scroll2.setShadowType(IN)
  scroll2.add(outgoingTextView)
  top.packStart(scroll2, false, false, 0)

  // Put the Window and all its children on screen.
  window.showAll

  // Make sure the user's text Entry has the keyboard focus. For a
  // number of reasons, this won't work until late in the game after
  // everything else is packed. If you try it earlier something else
  // will end up with focus despite this call having been made.
  outgoingTextView.grabFocus

  try {
    // todo: need to load this icon from runnable jar
    //window.setIcon(new Pixbuf("face-smile.png"))
    //window.setIcon(new Pixbuf("online-icon-48.png"))
    //window.setIcon(new Pixbuf("Gnome-Stock-Person-64.png"))
    window.setIcon(new Pixbuf("Emoticon-Confuse.png"))
    //window.setIcon(new Pixbuf("Emoticon-Confuse-48.png"))

  } catch {
    case fnfe:FileNotFoundException =>
      System.err.println("Warning: appicon " + fnfe.getMessage)
      window.setIcon(Gtk.renderIcon(window, Stock.MISSING_IMAGE, IconSize.BUTTON))
  }


  // start printing further down
  for(i <- 0 until 20)
    logUser("")

  // tell user to enter secret word(s) into form
  log("Please enter shared secret to start new chat session...")
  
  // waiting for events: onDeleteEvent + onKeyPressEvent
}

