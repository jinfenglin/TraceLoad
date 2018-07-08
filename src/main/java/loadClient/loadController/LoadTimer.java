package loadClient.loadController;


import org.w3c.dom.Element;

import java.util.*;

public class LoadTimer {
    final String BATCH = "batch";
    final String INTERVAL = "interval";

    private long batchSize;
    private int interval;
    private long timeBase;
    private List<Long> batchTimes;
    private Random random;

    public LoadTimer(Element timerConfigBlock) {
        String batchSizeStr = timerConfigBlock.getElementsByTagName(BATCH).item(0).getTextContent();
        String intervalStr = timerConfigBlock.getElementsByTagName(INTERVAL).item(0).getTextContent();
        batchSize = Long.valueOf(batchSizeStr);
        interval = Integer.valueOf(intervalStr);
        timeBase = 0;
        batchTimes = new LinkedList<>();
        random = new Random();
    }

    private List<Long> genBatchOfTimes() {
        List<Long> times = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            long time = random.nextInt(interval) + timeBase;
            times.add(time);
        }
        Collections.sort(times);
        return times;
    }

    public long getNextTime() {
        if (batchTimes.size() == 0) {
            batchTimes = genBatchOfTimes();
            timeBase += interval;
        }
        return batchTimes.remove(0);
    }

}
