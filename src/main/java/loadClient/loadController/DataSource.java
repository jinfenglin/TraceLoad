package loadClient.loadController;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
    private Logger logger;

    public enum Format {
        GENERAL, JSON, PLAIN
    }

    public enum SizeDistribution {
        RANDOM, EVEN
    }

    private Format format;
    private long fileNum;
    private String totalSize;
    private SizeDistribution sizeDistribution;

    public DataSource(Element dataSourceNode) {
        logger = Logger.getLogger(getClass().getName());
        if (dataSourceNode.hasChildNodes()) {
            NodeList dataSourceDetailsNodes = dataSourceNode.getChildNodes();
            for (int nodeIndex = 0; nodeIndex < dataSourceDetailsNodes.getLength(); nodeIndex += 1) {
                Node detailNode = dataSourceDetailsNodes.item(nodeIndex);
                String name = detailNode.getNodeName();
                String value = detailNode.getNodeValue();
                switch (name) {
                    case FORMAT:
                        format = Format.valueOf(value);
                        break;
                    case FILE_NUM:
                        fileNum = Long.valueOf(value);
                        break;
                    case TOTAL_SIZE:
                        totalSize = value;
                        break;
                    case SIZE_DISTRIBUTION:
                        sizeDistribution = SizeDistribution.valueOf(value);
                        break;
                    default:
                        logger.log(WARNING, String.format("Data Source Config \\'%s\\' is invalid and ignored.", name));
                }
            }
        }
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
}

