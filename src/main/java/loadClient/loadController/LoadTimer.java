package loadClient.loadController;


import org.w3c.dom.Element;

import java.io.File;
import java.util.*;

/**
 *
 */
public class LoadTimer {
    final String BATCH = "batch";
    final String INTERVAL = "interval";

    private long batchSize;
    private long interval;

    public LoadTimer(Element timerConfigBlock) {
        String batchSizeStr = timerConfigBlock.getElementsByTagName(BATCH).item(0).getTextContent();
        String intervalStr = timerConfigBlock.getElementsByTagName(INTERVAL).item(0).getTextContent();
        batchSize = Long.valueOf(batchSizeStr);
        interval = Long.valueOf(intervalStr);
    }

    public SortedMap<Long, List<String>> getTimeIndex(List<DataSource> dataSources) {
        SortedMap<Long, List<String>> timeIndex = new TreeMap<>();
        List<File> allFiles = new ArrayList<>();
        for (DataSource dataSource : dataSources) {
            allFiles.addAll(dataSource.getFiles());
        }
        long startTime = 0;
        for (long batchNum = 0; batchNum < allFiles.size() / batchSize; batchNum++) {
            long batchBase = batchNum * batchSize;
            for (long fileIndex = batchBase; fileIndex < allFiles.size(); fileIndex++) {
                long generatedLong = startTime + (long) (Math.random() * (interval));
                if (!timeIndex.containsKey(generatedLong)) {
                    timeIndex.put(generatedLong, new ArrayList<>());
                }
                List<String> filesAtMoment = timeIndex.get(generatedLong);
                filesAtMoment.add(allFiles.get((int) fileIndex).getName());
                timeIndex.put(generatedLong, filesAtMoment);
            }
            startTime += interval;
        }
        return timeIndex;
    }
}
