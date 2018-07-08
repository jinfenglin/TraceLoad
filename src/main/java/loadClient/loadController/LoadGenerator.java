package loadClient.loadController;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LoadGenerator {
    List<DataSource> dataSources;
    List<Server> servers;
    Logger logger;
    LoadTimer loadTimer;
    String id;
    File tempDir;
    List<File> eventSeqFiles;

    public LoadGenerator(String id, List<Server> servers, List<DataSource> dataSources, LoadTimer timer, File tempDir) throws IOException {
        this.dataSources = dataSources;
        this.servers = servers;
        this.logger = Logger.getLogger(this.getClass().getName());
        this.loadTimer = timer;
        this.id = id;
        this.tempDir = tempDir;
        eventSeqFiles = new ArrayList<>();
    }

    public void createEventSequences() throws IOException {
        logger.info("Creating events ...");
        for (DataSource dataSource : dataSources) {
            EventSeqMaker eventSeqMaker = new EventSeqMaker(dataSource, loadTimer, tempDir);
            File eventSeqFile = eventSeqMaker.createEventSequenceFile();
            eventSeqFiles.add(eventSeqFile);
        }
    }

    public void transfer(List<File> files) throws InterruptedException {
        List<Thread> transferThread = new ArrayList<>();
        for (Server server : servers) {
            logger.info(String.format("Data trans thread for server %s is up...", server.getIp()));
            Thread thd = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        server.transferData(files);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            transferThread.add(thd);
            thd.start();
        }
        for (Thread thd : transferThread) {
            thd.join();
        }
        logger.info("Data transformation finished...");
    }

    private void waitTillDataReady() throws InterruptedException {
        List<Server> waitingServer = new ArrayList<>(servers);
        while (waitingServer.size() > 0) {
            Iterator<Server> it = waitingServer.iterator();
            while (it.hasNext()) {
                Server server = it.next();
                if (server.getStatus().isDataReady()) {
                    it.remove();
                }
            }
            Thread.sleep(5000);
            logger.info(String.format("Waiting %s server to finish data preparing...", waitingServer.size()));
        }
        logger.info("Data on all servers are ready");
    }

    private void loadDataToTarget() throws IOException {
        for (Server server : servers) {
            server.startLoad();
        }
    }

    public void startLoad() throws InterruptedException, IOException {
        createEventSequences(); //TODO add command line to control
        transfer(eventSeqFiles); //TODO BUG when no file is transferred, the cli will block on wating server side status to be ready
        waitTillDataReady();
        loadDataToTarget();
    }

    /**
     * Close a LoadGenerators, close the servers and terminate the works on the server
     */
    public void close() throws IOException {
        for (Server server : servers) {
            server.close();
        }
    }
}