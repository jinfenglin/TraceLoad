package loadClient.loadController;

import Common.LoadOperation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Level;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

/**
 *
 */
public class EventSeqMaker {
    public static final String END_MARK = "$END$";
    public static final String OP_CREATE = "create";
    public static final String OP_READ = "read";
    public static final String OP_UPDAET = "update";
    public static final String OP_DELETE = "delete";
    public static List<String> operations = Arrays.asList(OP_CREATE, OP_DELETE, OP_READ, OP_UPDAET);
    private Logger logger;

    private DataSource dataSource;
    private LoadTimer timer;
    private File tempDir;
    private Random random;

    public EventSeqMaker(DataSource dataSource, LoadTimer timer, File tempDir) {
        this.dataSource = dataSource;
        this.timer = timer;
        this.tempDir = tempDir;
        random = new Random();
        this.logger = Logger.getLogger(getClass().getName());
    }

    private File getOpSequenceFile(File tempDir) {
        String fileName = String.format("%s.op", dataSource.getId());
        Path opSeqPath = Paths.get(tempDir.toPath().toString(), fileName);
        File opSeqFile = new File(opSeqPath.toUri());
        return opSeqFile;
    }

    /**
     * Convert the data source to a sequence of operations and write them into disk
     *
     * @throws IOException
     */
    public File createEventSequenceFile() throws IOException {
        File opSeqFile = getOpSequenceFile(tempDir);
        ObjectOutputStream outputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(opSeqFile)));
        switch (dataSource.getFormat()) {
            case JSON:
                processJsonFiles(outputStream);
                break;
            case PLAIN:
                processPlainTextFiles(outputStream);
                break;
            case GENERATE:
                processGenerateingFile(outputStream);
                break;
        }
        outputStream.close();
        return opSeqFile;
    }


    private void processPlainTextFiles(ObjectOutputStream opSeqOutputStream) throws IOException {
        List<File> files = dataSource.getFiles();
        for (File file : files) {
            String content = String.join("\\n", Files.readAllLines(file.toPath()));
            LoadOperation loadOp = new LoadOperation();
            loadOp.setContent(content);
            loadOp.setFileName(file.getName());
            loadOp.setTime(String.valueOf(timer.getNextTime()));
            loadOp.setOperationType(OP_CREATE);
            opSeqOutputStream.writeObject(loadOp);
        }
        opSeqOutputStream.writeObject(END_MARK);
    }

    /**
     * Serize the event to a file and the last object the file is a special string '$END$'
     *
     * @return
     * @throws IOException
     */
    private void processJsonFiles(ObjectOutputStream opSeqOutputStream) throws IOException {
        Gson gson = new GsonBuilder().create();
        for (File jsonFile : dataSource.getFiles()) {
            JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(jsonFile), "UTF-8"));
            reader.beginObject();
            String name = reader.nextName();
            reader.beginArray();
            while (reader.hasNext()) {
                LoadOperation event = gson.fromJson(reader, LoadOperation.class);
                opSeqOutputStream.writeObject(event);
            }
            opSeqOutputStream.writeObject(END_MARK);
            reader.endArray();
            reader.endObject();
            reader.close();
        }
    }

    private String getValidOperation(Map<String, Boolean> fileOpRecord, String fileName) {
        boolean validOp = false;
        String operation = null;
        boolean exist = fileOpRecord.get(fileName);
        while (!validOp) {
            int randOpIndex = random.nextInt(operations.size());
            operation = operations.get(randOpIndex);
            validOp = exist ^ (operation == OP_CREATE);
        }
        return operation;
    }

    private LoadOperation genCreateEvent(File file, String time) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        String content = new String(bytes);
        LoadOperation loadOp = new LoadOperation();
        loadOp.setOperationType(OP_CREATE);
        loadOp.setTime(time);
        loadOp.setContent(content);
        loadOp.setFileName(file.getName());
        return loadOp;
    }

    private LoadOperation genDeleteEvent(File file, String time) {
        LoadOperation loadOp = new LoadOperation();
        loadOp.setOperationType(OP_DELETE);
        loadOp.setFileName(file.getName());
        loadOp.setTime(time);
        return loadOp;
    }

    private LoadOperation genUpdateEvent(File file, String time) {
        LoadOperation loadOp = new LoadOperation();
        loadOp.setOperationType(OP_UPDAET);
        loadOp.setFileName(file.getName());
        loadOp.setTime(time);
        loadOp.setContent(RandomStringUtils.randomAlphabetic((int) file.length()));
        return loadOp;
    }

    private LoadOperation genReadEvent(File file, String time) {
        LoadOperation loadOp = new LoadOperation();
        loadOp.setTime(time);
        loadOp.setFileName(file.getName());
        loadOp.setOperationType(OP_READ);
        return loadOp;
    }


    private void processGenerateingFile(ObjectOutputStream opSeqOutputStream) throws IOException {
        //Record whether a file exist or not
        Map<String, Boolean> opRecord = new HashMap<>();
        List<File> files = dataSource.getFiles();
        for (File file : files) {
            opRecord.put(file.getName(), false);
        }
        int totalOpNum = dataSource.getOperationNum();
        for (int op_num = 0; op_num < totalOpNum; op_num++) {
            int randFileIndex = random.nextInt((int) dataSource.getFileNum());
            File opFile = files.get(randFileIndex);
            String operation = getValidOperation(opRecord, opFile.getName());
            String opTime = String.valueOf(timer.getNextTime());
            LoadOperation loadOp = null;
            switch (operation) {
                case OP_CREATE:
                    opRecord.put(opFile.getName(), true);
                    loadOp = genCreateEvent(opFile, opTime);
                    break;
                case OP_DELETE:
                    opRecord.put(opFile.getName(), false);
                    loadOp = genDeleteEvent(opFile, opTime);
                    break;
                case OP_UPDAET:
                    loadOp = genUpdateEvent(opFile, opTime);
                    break;
                case OP_READ:
                    loadOp = genReadEvent(opFile, opTime);
                    break;
            }
            logger.info(loadOp.toString());
            opSeqOutputStream.writeObject(loadOp);
        }
        opSeqOutputStream.writeObject(END_MARK);
    }
}
