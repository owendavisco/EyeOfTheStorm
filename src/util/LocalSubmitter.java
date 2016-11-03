package util;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.utils.Utils;

public final class LocalSubmitter {
    private LocalSubmitter(){

    }

    public static void submitTopology(String topologyName, Config conf, StormTopology topology, int timeConstraint) {
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology(topologyName, conf, topology);
        Utils.sleep(timeConstraint);
        cluster.killTopology(topologyName);
        cluster.shutdown();
    }
}
