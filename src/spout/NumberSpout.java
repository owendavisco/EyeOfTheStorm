package spout;

import org.apache.storm.spout.ISpout;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.util.Map;

public class NumberSpout extends BaseRichSpout implements ISpout {
    public static final int TOTAL_NUMBERS = 10000;

    private SpoutOutputCollector collector;
    private int[] numbers = generateAscendingNumbers();
    private int index = 0;

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("number"));
    }

    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
    }

    public void nextTuple() {
        if(index < TOTAL_NUMBERS) {
            collector.emit(new Values(numbers[index]));
            index++;
        }
    }

    @Override
    public void ack(Object msgId) {
        System.out.println("ack on msgId" + msgId);
    }

    @Override
    public void fail(Object msgId){
        System.out.println("fail on msgId" + msgId);
    }

    private static int[] generateAscendingNumbers() {
        int[] numberList = new int[TOTAL_NUMBERS];
        for(int i = 0; i < TOTAL_NUMBERS; i++) {
            numberList[i] = i + 1;
        }
        return numberList;
    }
}
