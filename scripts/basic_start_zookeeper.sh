java -cp zookeeper-3.4.9.jar:lib/log4j-1.2.16.jar:lib/slf4j-log4j12-1.6.1.jar:lib/slf4j-api-1.6.1.jar:conf \
  org.apache.zookeeper.server.quorum.QuorumPeerMain \
  conf/zoo.cfg
