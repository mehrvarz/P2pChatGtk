test -d build || mkdir -p build
#rm -rf build/*
export CLASSPATH=lib/P2pChatSMP.jar:lib/p2pCore.jar:lib/protobuf-java-2.3.0.jar:lib/bcprov-jdk15on-147-ext.jar:lib/commons-codec-1.6.jar:lib/gtk-4.1.jar
scalac -d build -deprecation -classpath build:$CLASSPATH src/*.scala
./makejar

