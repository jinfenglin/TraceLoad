package loadClient.loadController;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LoadGenerator {
    List<DataSource> dataSources;
    List<Server> servers;
    Logger logger;
    LoadTimer loadTimer;

    public LoadGenerator(List<Server> servers, List<DataSource> dataSources, LoadTimer timer) {
        this.dataSources = dataSources;
        this.servers = servers;
        this.logger = Logger.getLogger(this.getClass().getName());
        this.loadTimer = timer;
    }

    public void transfer(List<DataSource> dataSources) throws InterruptedException {
        List<Thread> transferThread = new ArrayList<>();
        for (Server server : servers) {
            logger.info(String.format("Data trans thread for server %s is up...", server.getIp()));
            Thread thd = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        server.transferData(dataSources);
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

    public void setTimeIndex() throws IOException {
        List<FilesAtMoment> timeIndex = loadTimer.getTimeIndex(dataSources);
        for (Server server : servers) {
            server.transferTimeIndex(timeIndex);
        }
    }

    public void startLoad() throws InterruptedException, IOException {
        transfer(dataSources);
        //waitTillDataReady();
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

/**
 * class ServerThread implements Runnable {
 * Server server;
 * Queue<String> commandQueue = new ArrayDeque<>();
 * List<DataSource> dataSources;
 * String currentCommand;
 * <p>
 * public ServerThread(Server server, List<DataSource> dataSources) {
 * this.server = server;
 * this.dataSources = dataSources;
 * currentCommand = "";
 * }
 * <p>
 * public void addCommand(String commandStr) {
 * commandQueue.add(commandStr);
 * }
 * <p>
 * public Queue<String> getCommandQueue() {
 * return commandQueue;
 * }
 * <p>
 * public String getCurrentTask() {
 * return currentCommand;
 * }
 *
 * @Override public void run() {
 * final int TRAN_DATA = 1;
 * while (true) {
 * if (commandQueue.size() > 0) {
 * String curCommand = commandQueue.poll();
 * switch (curCommand) {
 * case fooCommand:
 * break;
 * }
 * }
 * }
 * }
 * }
 **/
