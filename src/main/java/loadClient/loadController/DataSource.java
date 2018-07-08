package loadClient.loadController;

import Common.Utils;
import org.apache.commons.lang.RandomStringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import static java.util.logging.Level.WARNING;

/**
 * The data source configuration info
 */
public class DataSource {
    private final String FORMAT = "format";
    private final String FILE_NUM = "fileNum";
    private final String TOTAL_SIZE = "totalSize";
    private final String SIZE_DISTRIBUTION = "sizeDistribution";
    private final String PATH = "path";
    private final String OP_NUM = "operationNum";
    private Logger logger;

    public enum Format {
        GENERATE, JSON, PLAIN
    }

    public enum SizeDistribution {
        RANDOM, EVEN
    }

    private Format format;
    private long fileNum;
    private String totalSize;
    private String path;
    private SizeDistribution sizeDistribution;
    private List<File> files;
    private String id;
    private int operationNum;
    private File dsTempDir;

    public DataSource(Element dataSourceNode, File tempDir) throws IOException {
        logger = Logger.getLogger(getClass().getName());
        files = new ArrayList<>();
        id = dataSourceNode.getAttribute("id");
        if (dataSourceNode.hasChildNodes()) {
            NodeList dataSourceDetailsNodes = dataSourceNode.getChildNodes();
            for (int nodeIndex = 0; nodeIndex < dataSourceDetailsNodes.getLength(); nodeIndex += 1) {
                Node detailNode = dataSourceDetailsNodes.item(nodeIndex);
                if (detailNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                String name = detailNode.getNodeName();
                String value = detailNode.getTextContent().trim();
                switch (name) {
                    case FORMAT:
                        format = Format.valueOf(value.toUpperCase());
                        break;
                    case FILE_NUM:
                        fileNum = Long.valueOf(value.toUpperCase());
                        break;
                    case TOTAL_SIZE:
                        totalSize = value;
                        break;
                    case SIZE_DISTRIBUTION:
                        sizeDistribution = SizeDistribution.valueOf(value.toUpperCase());
                        break;
                    case PATH:
                        path = value;
                        break;
                    case OP_NUM:
                        operationNum = Integer.valueOf(value);
                        break;
                    default:
                        logger.log(WARNING, String.format("Data Source Config \\'%s\\' is invalid and ignored.", name));
                }
            }
        }
        dsTempDir = createDirUnderTempDir(tempDir);
        prepareFiles();
    }

    private File createDirUnderTempDir(File tempDir) {
        File dsTmpDir = new File(tempDir, getId());
        if (!dsTmpDir.exists() || !dsTmpDir.isDirectory()) {
            dsTmpDir.mkdir();
        }
        return dsTmpDir;
    }

    private long getRandomSize(DataSource.SizeDistribution sizeDistrib, long remainAvailableSize, long fileNum) {
        Random random = new Random();
        if (sizeDistrib == DataSource.SizeDistribution.EVEN) {
            return remainAvailableSize / fileNum;
        } else if (sizeDistrib == DataSource.SizeDistribution.RANDOM) {
            if (fileNum == 1)
                return remainAvailableSize;
            else {
                int size = random.nextInt((int) remainAvailableSize / 2);
                return size;
            }
        }
        return 0;
    }

    private String createFileNameFromFileIndex(long fileIndex) {
        String dataSourceId = getId();
        String fileIndexStr = String.valueOf(fileIndex);
        return String.format("%s-RandomFile-%s.txt", dataSourceId, fileIndex);
    }

    /**
     * Get the list of files of this datasource.
     */
    private void prepareFiles() throws IOException {
        if (format == Format.GENERATE) {
            DataSource.SizeDistribution sizeDistrib = getDistribution();
            long fileNum = getFileNum();
            long remainSize = Utils.formatFileSizeToByte(getTotalSize());
            for (long i = 0; i < fileNum; i++) {
                String fileName = createFileNameFromFileIndex(i);
                long targetFileSize = getRandomSize(sizeDistrib, remainSize, fileNum - i);
                String content = RandomStringUtils.randomAlphabetic((int) targetFileSize);
                remainSize -= targetFileSize;
                File fileToCreate = new File(dsTempDir, fileName);
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fileToCreate));
                outputStream.write(content.getBytes());
                outputStream.close();
                this.files.add(fileToCreate);
            }
        } else {
            File dir = new File(path);
            File[] files = dir.listFiles();
            this.files = Arrays.asList(files);
        }
    }

    public List<File> getFiles() {
        return files;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public long getFileNum() {
        return fileNum;
    }

    public void setFileNum(long fileNum) {
        this.fileNum = fileNum;
    }

    public String getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(String totalSize) {
        this.totalSize = totalSize;
    }

    public SizeDistribution getDistribution() {
        return sizeDistribution;
    }

    public void setDistribution(SizeDistribution distribution) {
        this.sizeDistribution = distribution;
    }

    public String getId() {
        return id;
    }

    public int getOperationNum() {
        return operationNum;
    }

    public void setOperationNum(int operationNum) {
        this.operationNum = operationNum;
    }
}

