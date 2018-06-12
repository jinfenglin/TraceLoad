package loadClient.loadController;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public DataSource(Element dataSourceNode) throws IOException {
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
                    default:
                        logger.log(WARNING, String.format("Data Source Config \\'%s\\' is invalid and ignored.", name));
                }
            }
        }
        prepareFiles();
    }

    /**
     * Get the list of files of this datasource.
     */
    private void prepareFiles() {
        if (format == Format.GENERATE) {
            //TODO Generate files and put it into the list
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
}

