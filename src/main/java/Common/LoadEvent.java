package Common;

import java.util.List;

/**
 * The files should be operated at a moment
 */
public class LoadEvent {
    long relativeTime;
    List<LoadOperation> operations;


    public LoadEvent(Long relativeTime, List<String> fileNames) {
        this.relativeTime = relativeTime;
        this.fileNames = fileNames;
    }

    public long getRelativeTime() {
        return relativeTime;
    }

    public void setRelativeTime(long relativeTime) {
        this.relativeTime = relativeTime;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }
}

