package loadClient.loadController;

import Common.LoadOperation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 */
public class EventSeqMaker {

    DataSource dataSource;
    LoadTimer timer;
    File tempDir;

    public EventSeqMaker(DataSource dataSource, LoadTimer timer, File tempDir) {
        this.dataSource = dataSource;
        this.timer = timer;
        this.tempDir = tempDir;
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


    private void processPlainTextFiles(OutputStream outputStream) throws IOException {
//        File dir = new File(this.path);
//        List<File> files = new ArrayList<>();
//        if (dir.isDirectory()) {
//            Files.walk(Paths.get(path)).filter(Files::isRegularFile).forEach(fp -> files.add(new File(fp.toUri().getPath())));
//        } else if (dir.isFile()) {
//            logger.warning(String.format("Path %s is a file not a directory.", dir.getPath()));
//            files.add(dir);
//        }
//        this.files = files;
//        for (File file : files) {
//            LoadOperation op = new LoadOperation(LoadOperation.Operation.CREATE, file.getName());
//            this.loadOperations.add(op);
//        }
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
            opSeqOutputStream.writeObject("$END$");
            reader.endArray();
            reader.endObject();
            reader.close();
        }
    }

    private void processGenerateingFile(ObjectOutputStream opSeqOutputStream) throws IOException {

    }
}
