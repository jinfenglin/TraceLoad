package Common;

import java.io.InputStream;


/**
 *
 */
public class FileStream {
    private long size;
    private String fileName;
    private InputStream dataStream;

    public FileStream(String fileName, long size, InputStream stream) {
        this.size = size;
        this.fileName = fileName;
        this.dataStream = stream;
    }

    public long getSize() {
        return size;
    }

    public String getFileName() {
        return fileName;
    }

    public InputStream getDataStream() {
        return dataStream;
    }
}
