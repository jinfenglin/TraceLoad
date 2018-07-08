package Common;

import java.io.Serializable;

/**
 * Record one event in the load injection.The time is a long value which indicating how many millionsecs is this event
 * away from the beginning of the load injection.
 */
public class LoadOperation implements Serializable, Comparable<LoadOperation> {
    private String operationType;
    private String fileName; //unique id for a file
    private String content;
    private String time;

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        boolean haveContent = false;
        if(content!=null)
            haveContent = true;
        return "LoadOperation{" +
                "operationType='" + operationType + '\'' +
                ", fileName='" + fileName + '\'' +
                ", haveContent='" + haveContent + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    @Override
    public int compareTo(LoadOperation o) {
        long time1 = Long.valueOf(getTime());
        long time2 = Long.valueOf(o.getTime());
        return Long.compare(time1, time2);
    }
}
