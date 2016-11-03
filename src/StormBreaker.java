import bolt.AdderBolt;
import bolt.SubtractorBolt;
import bolt.SumBolt;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import spout.NumberSpout;
import util.LocalSubmitter;

public class StormBreaker {
    private static final int SECOND = 1000;
    private static final int MINUTE = SECOND * 60;

    public static void main(String[] args) throws Exception {
        System.out.println("***************** StormBreaker Started *****************");

        String topologyName = "StormBreaker";
        if (args.length >= 1) {
            topologyName = args[0];
        }
        boolean runLocally = true;
        if (args.length >= 2 && args[1].equalsIgnoreCase("remote")) {
            runLocally = false;
        }

        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("number", new NumberSpout());
        builder.setBolt("adder", new AdderBolt()).shuffleGrouping("number");
        builder.setBolt("subtractor", new SubtractorBolt()).shuffleGrouping("adder");
        builder.setBolt("sum", new SumBolt()).shuffleGrouping("subtractor");

        Config conf = new Config();
        conf.setDebug(true);

        if (runLocally) {
            LocalSubmitter.submitTopology(topologyName, conf, builder.createTopology(), SECOND * 20);
        }
        else {
            StormSubmitter.submitTopologyWithProgressBar(topologyName, conf, builder.createTopology());
        }
    }
}