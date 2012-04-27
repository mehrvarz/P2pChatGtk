package timur.p2pChatSMP

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
  
  def main(args:Array[String]): Unit = {
    try {
      Gtk.init(args)
      // will throw java.lang.UnsatisfiedLinkError if libjava-gnome-java is not installed
    } catch {
      case ex:java.lang.UnsatisfiedLinkError =>
        println("failed to load java-gnome")
        ex.printStackTrace
        // todo: use some kind of 'toast' to display this issue
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
    new P2pChatGtk()
    Gtk.main
  }

  /**
   * Append a received (or sent) message to the incoming display.
   */
  def appendMessage(msg:String, outbound:Boolean) {
    val end = P2pChatGtk.buffer.getIterEnd
    buffer.insert(end, "\n")
    val now = System.currentTimeMillis() / 1000
    val timestamp = formatTime("%H:%M:%S\t", now)
    buffer.insert(end, timestamp, grey)
    val colour = if(outbound) blue else null
    var prev = 0
    P2pChatGtk.buffer.insert(end, msg.substring(prev), colour)
    incomingTextView.scrollTo(end)
  }
}

class P2pChatGtk extends timur.p2pChatSMP.LogClassTrait {
  //log("class P2pChatGtk new Window()")
  val window = new Window()
  val top = new VBox(false, 3)
  window.setTitle("SocketProxy Admin Client")
  window.setDefaultSize(1020, 380)
  window.add(top)

  var p2pChatSMP:P2pChatSMPForGtk = null
  var quitApp = false
  window.connect(new Window.DeleteEvent() {
    def onDeleteEvent(source:Widget, event:Event) :Boolean = {
      log("class P2pChatGtk onDeleteEvent -> Gtk.mainQuit")
      if(p2pChatSMP!=null)
        p2pChatSMP.relayQuit
      quitApp = true // will stop the P2pChatSMP thread loop
      Gtk.mainQuit
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
      if(event.getKeyval() == Keyval.Return) {
        // a command was entered in the GUI
        log(outgoingTextView.getBuffer.getText)
        // send command to server
        if(p2pChatSMP!=null)
          p2pChatSMP.p2pSend(outgoingTextView.getBuffer.getText)
        outgoingTextView.getBuffer.setText("")
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
/*
  try {
    window.setIcon(new Pixbuf("res/java-gnome_Icon.png"))
  } catch {
    case fnfe:FileNotFoundException =>
      System.err.println("Warning: appicon " + fnfe.getMessage)
      window.setIcon(Gtk.renderIcon(window, Stock.MISSING_IMAGE, IconSize.BUTTON))
  }
*/


  val obj = this
  new Thread("P2pChatSMP") { override def run() { 
/*
    // Mark this thread as a daemon thread
    // else the main thread terminating after Gtk.main() returns will not end the program
    synchronized { 
      try { setDaemon(true) } catch { 
        case ex:Exception => 
          ex.printStackTrace
      }
    }
*/    

    while(!quitApp) {
      val p2pSecret = "paris"
      val smpSecret = "texas"
      // todo: open dialog to let user enter two secret strings

      // todo: fix futex_wait_queue_me / high-load issue

      log("new P2pChatSMPForGtk(p2pSecret="+p2pSecret+", smpSecret="+smpSecret+")")
      p2pChatSMP = new P2pChatSMPForGtk(p2pSecret,smpSecret,obj)
      p2pChatSMP.start
      println("p2pChatSMP finished")
    }
  } }.start

  /** our implementation of timur.p2pChatSMP.LogClassTrait
   *  p2pChatSMP will use this method to print into the gtk window 
   */
  def log(msg:String) = synchronized {
    P2pChatGtk.appendMessage(msg, true)
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
}

