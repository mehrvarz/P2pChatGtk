P2pChatGtk
==========

P2pChatGtk is a secure peer-to-peer GTK chat application for clients operated behind firewalls. No XMPP or other accounts are needed to use this app. P2pChatGtk implements end-to-end encryption with [Off-the-Record Messaging](http://de.wikipedia.org/wiki/Off-the-Record_Messaging) and [Socialist Millionaire' Protocol](http://en.wikipedia.org/wiki/Socialist_millionaire).

*** This project is work in progress still. We will remove this warning when it becomes usable. Thank you. ***

P2pChatGtk builds upon [P2pChatSMP](https://github.com/mehrvarz/P2pChatSMP) and [P2pCore](https://github.com/mehrvarz/P2pCore). The `lib` folder contains jar files build from these projects source code.


System requirements
-------------------

To run P2pChatGtk, libjava-gnome and OpenJDK 6 must be installed. On Ubuntu 12.04 you would:

    apt-get install libjava-gnome-java

To build P2pChatGtk, Scala 2.9.x and Ant must be installed. On Ubuntu 12.04 you would:

    apt-get install scala ant


Running P2pChatGtk
------------------

A runnable jar file is provided with the source repository. On Ubuntu 12.04 you can start the Gtk application by right clicking and selecting "Open with OpenJDK Java 6 Runtime" (or "Open with OpenJDK Java 7 Runtime"). Alternatively you can start the same application from the command line by typing "java -jar P2pChatGtk.jar" or simply "scala P2pChatGtk.jar".

Two instances of the app need to be executed in parallel, so they can connect to each other. Running both instances on the same machine is possible, but the purpose of this application is to bridge clients located behind discrete firewalls. Possible setups to verify this functionality are: two machines in completely separate locations, or two PC's side by side, one connected via DSL or cable, the other one connected via mobile internet.

Two secret words must be entered to start a secure chat session. This means the two clients must agree upon the two secrets before the chat session begins. The first secret string will be used to match and connect two clients. The second secret string will be used as the OTR/SMP secret for dynamically creating encryption keys. Both clients must use the exact same secrets. As soon as a P2P connection has been established, OTR and SMP will start automatically. A few seconds later, the SMP handshake should be completed and a secure and private conversation can take place. 

Shown below is a log of Alice's client instance:

    P2pChatSMP accountname=alice.msn.com protocol=msn recipient=bob@msn.com
    P2pChatSMP relaySocket.getLocalPort=-1 relayServer=109.74.203.226 relayPort=18771
    P2pChatSMP receiveHandler send encrypted initialMsg='...'
    P2pChatSMP combinedUdpAddrString this peer udpAddress=89.201.71.60:33790|192.168.1.135:33790
    P2pChatSMP receiveMsgHandler other peer combindedUdpAddress='89.201.71.60:55130|192.168.1.135:55130'
    P2pChatSMP datagramSendThread udpIpAddr='192.168.1.135' udpPortInt=33790 abort
    P2pChatSMP datagramSendThread udpIpAddr='89.201.71.60' udpPortInt=55130 connected
    From network:32:stand up
    Injecting message to the recipient:326:?OTR:AAICAAAAxPaF08CY3FVioRfrGCgEvJ...
    From OTR:8:stand up
    From network:274:?OTR:AAIKAAAAwNvZMcndXAqJDvdqd/p9aWtEHKKyN...
    From network:690:?OTR:AAIRAAAAENsQ4J2Rx8Nq...
    New fingerprint is created.
    Writing fingerprints.
    Updating context list.
    AKE succeeded
    P2pChatSMP goneSecure -> init OMP with smpSecret=berlin
    Injecting message to the recipient:666:?OTR:AAISAAAB0tUvh3ZcWUOHl40...
    Injecting message to the recipient:991:?OTR,1,2,?OTR:AAIDAQAAAAEAAAABAAAAwDA...
    Injecting message to the recipient:515:?OTR,2,2,4C7tqiIkJV//ZdC8jimctJHJhd7...
    From network:991:?OTR,1,3,?OTR:AAIDAQAAAAEAAAACAAAAw...
    From network:991:?OTR,2,3,fNejafJRYsoWj12DJKOEIrXgYH7qVwQHZXDZ...
    From network:626:?OTR,3,3,TOgcciO69O/o6hFtr6nnCwSdLQJGimoi+ekQ...
    Injecting message to the recipient:991:?OTR,1,3,?OTR:AAIDAQAAAAIAAAACAAAAwCEOW...
    Injecting message to the recipient:991:?OTR,2,3,9oRam+5af6ZqgvkJ4UGuwYKX8ulPaJ...
    Injecting message to the recipient:58:?OTR,3,3,1YciE3FSKuYn41KCGSHSYxT7LscjNh...
    From OTR:0:
    From network:942:?OTR:AAIDAQAAAAIAAAADAAAA...
    Writing fingerprints.
    SMP succeeded.
    P2pChatSMP ************* SMP succeeded ***************


Building from source
--------------------

To build the project, run:

    ./make

`make` script will work two steps:

1. compile P2pChatGtk classes located in src/ using scalac
2. create P2pChatSMPForGtk.jar by running ./makejar script

You can now run the unpackaged application:

    ./run timur.p2pChatSMP.P2pChatGtk Aston Martin

To package a runnable single-file jar, run:

    ./makegtkjar

`makegtkjar` script will run proguard to combine all used library archives to generate a single runnable `P2pChatGtk.jar`.

Licenses
--------

- P2pChatSMP, P2pCore source code and library

  licensed under the GNU General Public [LICENSE](P2pChatSMP/blob/master/licenses/LICENSE), Version 3.

  Copyright (C) 2012 timur.mehrvarz@gmail.com

  https://github.com/mehrvarz/P2pChatSMP

  https://github.com/mehrvarz/P2pCore

- The Java Off-the-Record Messaging library

  covered by the LGPL [LICENSE](P2pChatSMP/blob/master/licenses/java-otr/COPYING).

  [java-otr README](P2pChatSMP/blob/master/licenses/java-otr/README)

  http://www.cypherpunks.ca/otr/
  
- java-gnome User Interface Library

  java-gnome is [Logiciel Libre](http://java-gnome.sourceforge.net/LICENCE.html) and is Open Source; you can redistribute it and/or modify it under the terms of the GNU General Public License, version 2 (“GPL”).

  http://java-gnome.sourceforge.net/

- ProGuard Java class file shrinker

  [ProGuard](http://proguard.sourceforge.net/license.html) is distributed under the terms of the GNU General Public License (GPL), version 2, as published by the Free Software Foundation (FSF)

  http://proguard.sourceforge.net/
  
- Bouncy Castle 

  http://bouncycastle.org/

- Google Protobuf 

  [New BSD License](http://www.opensource.org/licenses/bsd-license.php)

  https://code.google.com/p/protobuf/

- Apache Commons-codec 

  All software produced by The Apache Software Foundation or any of its projects or subjects is licensed according to the terms of [Apache License, Version 2.0](http://www.apache.org/licenses/)

  http://commons.apache.org/codec/


