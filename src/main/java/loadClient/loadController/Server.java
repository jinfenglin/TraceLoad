package loadClient.loadController;

import Common.ServerStatus;
import com.sun.deploy.trace.Trace;
import org.w3c.dom.Element;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.SortedMap;
import java.util.logging.Logger;

import static Common.RequestStrs.*;

/**
 * Server object in the controller side
 */
public class Server {
    Logger logger;
    final String IP = "IP";
    final String PORT = "port";
    final String TARGET = "target";

    private String ip, port;
    private Target target;
    private Socket socket;
    private ServerStatus status;

    public Server(Element serverElement) throws Exception{
        logger = Logger.getLogger(getClass().getName());
        ip = serverElement.getElementsByTagName(IP).item(0).getTextContent();
        port = serverElement.getElementsByTagName(PORT).item(0).getTextContent();
        Element targetElement = (Element) serverElement.getElementsByTagName(TARGET).item(0);
        target = new Target(targetElement);
        status = new ServerStatus();
        socket = new Socket(getIp(), getPort());
        setLoadTarget();
        monitorServerStatus();
    }

    private void monitorServerStatus() throws IOException, ClassNotFoundException {
        Thread statusMonitorDaemon = new Thread(new ServerStatusMonitor(socket, status));
        statusMonitorDaemon.setDaemon(true);
        statusMonitorDaemon.start();
    }

    private void sendRequest(String request) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        outputStream.writeUTF(request);
    }

    /**
     * Inform the load server to set a target platform
     *
     * @throws IOException
     */
    public void setLoadTarget() throws IOException {
        sendRequest(SET_TARGET_REQ);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(target);
    }

    /**
     * Start a load mission on the load server
     *
     * @throws IOException
     */
    public void startLoad() throws IOException {
        sendRequest(START_LOAD_REQ);
    }

    /**
     * Stop the load on the load server
     *
     * @throws IOException
     */
    public void stopLoad() throws IOException {
        sendRequest(STOP_LOAD_REQ);
    }

    /**
     * Clean the target platform where loads have been applied
     *
     * @throws IOException
     */
    public void clean() throws IOException {
        sendRequest(CLEAN_REQ);
    }

    public void transferTimeIndex(SortedMap timeIndex) throws IOException {
        sendRequest(TRANS_TIME_INDEX);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(timeIndex);
    }

    /**
     * Transfer the files to the load server
     *
     * @param files
     * @throws IOException
     */
    public void transferData(List<File> files) throws IOException {
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        byte[] buffer = new byte[256];
        for (File file : files) {
            sendRequest(TRANS_FILE);
            String fileName = file.getName();
            long fileSize = file.length();
            InputStream stream = new FileInputStream(file);
            dos.writeUTF(fileName);
            dos.writeLong(fileSize);
            int readSize = 0;
            while (fileSize > 0 && (readSize = stream.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
                dos.write(buffer, 0, readSize);
                fileSize -= readSize;
            }
        }
    }


    public void close() throws IOException {
        socket.close();
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return Integer.valueOf(port);
    }

    public Target getTarget() {
        return target;
    }

    public ServerStatus getStatus() {
        synchronized (status) {
            return new ServerStatus(status);
        }
    }
}

class ServerStatusMonitor implements Runnable {
    private Logger logger;
    private ServerStatus status;
    private Socket socket;

    public ServerStatusMonitor(Socket socket, ServerStatus status) throws IOException {
        this.socket = socket;
        this.status = status;
        this.logger = Logger.getLogger(this.getClass().getName());
    }

    @Override
    public void run() {
        logger.info("statusMonitor is activated...");
        List<ServerStatus> statusQueue = new ArrayList<>();

        Thread statusReceiver = new Thread(new Runnable() {
            @Override
            public void run() {
                ObjectInputStream inputStream = null;
                try {
                    inputStream = new ObjectInputStream(socket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (true) {
                    try {
                        ServerStatus tmpStatus = (ServerStatus) inputStream.readObject();
                        synchronized (statusQueue) {
                            statusQueue.add(tmpStatus);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        statusReceiver.setDaemon(true);
        statusReceiver.start();


        while (true) {
            if (socket.isClosed())
                return;
            synchronized (statusQueue) {
                if (statusQueue.size() > 0) {
                    ServerStatus lastStatus = statusQueue.get(statusQueue.size() - 1);
                    synchronized (status) {
                        status.setAlive(lastStatus.isAlive());
                        status.setProgress(lastStatus.getProgress());
                        status.setDataReady(lastStatus.isDataReady());
                        status.setServerStateType(lastStatus.getServerStateType());
                    }
                    while (statusQueue.size() > 0) {
                        statusQueue.remove(0);
                    }
                } else {
                    status.setAlive(false);
                    status.setServerStateType(ServerStatus.ServerStateType.UNKNOWN);
                }
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

