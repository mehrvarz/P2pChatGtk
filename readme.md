P2pChatGtk
==========

P2pChatGtk is a secure peer-to-peer GTK chat application for clients operated behind firewalls. No XMPP or other accounts are needed to use this app. P2pChatGtk implements end-to-end encryption with [Off-the-Record Messaging](http://de.wikipedia.org/wiki/Off-the-Record_Messaging) and [Socialist Millionaire' Protocol](http://en.wikipedia.org/wiki/Socialist_millionaire).

*** This project is work in progress still. We will remove this warning when it becomes usable. Thank you. ***


Running the app
---------------

You need Java6 (e.g. OpenJDK6) and the java-gnome package installed before you can run P2pChatGtk. On Ubuntu 12.04 you would install:

    apt-get install libjava-gnome-java

Precompiled class files of all required classes are provided in the code repository. You can run P2pChatGtk by running the `run` script:

    ./run

Two instances of this application need to run in parallel, so they can connect to each other. Running both instances on the same machine is possible, but the purpose of this application is to bridge clients located behind discrete firewalls. Possible setups to verify this functionality are: two machines in completely separate locations, or two PC's side by side, one connected via DSL or cable, the other one connected via mobile internet.

Two secret words must be entered to start a secure chat session. This means the two clients must agree upon the two secret words before the chat session begins. The first secret word will be used to match and connect the two clients. The second secret word will be used as the OTR/SMP secret for dynamically creating encryption keys. Both clients must use the exact same secrets. As soon as a P2P connection has been established, OTR and SMP will start automatically. A few seconds later, the SMP handshake should be completed and a secure and private conversation can take place. 

Shown below is one client's log window:

<img src="http://mehrvarz.github.com/img/screenshotP2pChatGtk.png" />


Building from source
--------------------

You need to install Scala 2.9.x and Ant, before you can build P2pChatGtk. On Ubuntu 12.04 you would:

    apt-get install scala ant

To build the project, run:

    ./make

`make` script will work two steps:

1. compile P2pChatGtk classes located in src/ using scalac
2. create P2pChatForGtk.jar by running ./makejar script

You can then run the unpackaged application:

    ./run

To package a runnable single-file jar, run:

    ./makegtkjar

`makegtkjar` script will run proguard to combine all required libraries and generate a single runnable `P2pChatGtk.jar`.


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


