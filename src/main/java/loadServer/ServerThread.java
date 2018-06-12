package loadServer;

import Common.ServerStatus;
import com.sun.corba.se.pept.encoding.OutputObject;
import loadClient.loadController.Target;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Statement;
import java.util.logging.Logger;

import static Common.RequestStrs.*;

/**
 *
 */
public class ServerThread implements Runnable {
    Logger logger;
    Socket socket;
    Target loadTarget;
    File filePathDir;
    ServerStatus status;

    public ServerThread(Socket socket, File filePathDir) {
        this.socket = socket;
        logger = Logger.getLogger(getClass().getName());
        this.filePathDir = filePathDir;
        status = new ServerStatus();
    }

    private void receiveData(DataInputStream inputStream) throws IOException {
        String fileName = inputStream.readUTF();
        long size = inputStream.readLong();
        int bufSize = 256;
        byte[] buff = new byte[bufSize];
        int readSize = 0;

        File writeFile = new File(filePathDir, fileName);
        FileOutputStream fos = new FileOutputStream(writeFile);
        while (size > 0 && (readSize = inputStream.read(buff, 0, (int) Math.min(buff.length, size))) != -1) {
            fos.write(buff);
            size -= readSize;
        }
        fos.close();
    }

    private void setTarget(InputStream inputStream) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        loadTarget = (Target) objectInputStream.readObject();
    }

    @Override
    public void run() {
        DataInputStream inputStream = null;
        ObjectOutputStream outputStream = null;
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread statusReportThread = new Thread(new StatusReportThread(200, outputStream, status));
        statusReportThread.setDaemon(true);
        statusReportThread.start();

        synchronized (status) {
            status.setAlive(true);
        }

        // Server hold a status related to current mission/client configuration
        while (true) {
            try {
                if (!socket.isConnected() || socket.isInputShutdown())
                    break;
                String request = inputStream.readUTF();
                logger.info(String.format("Receive request %s", request));
                switch (request) {
                    case SET_TARGET_REQ:
                        setTarget(inputStream);
                        break;
                    case START_LOAD_REQ:
                        synchronized (status) {
                            status.setServerStateType(ServerStatus.ServerStateType.LOADING);
                        }
                        break;
                    case STOP_LOAD_REQ:
                        synchronized (status) {
                            status.setServerStateType(ServerStatus.ServerStateType.WAITING);
                        }
                        break;
                    case CLEAN_REQ:
                        break;
                    case TRANS_FILE:
                        synchronized (status) {
                            status.setServerStateType(ServerStatus.ServerStateType.TRANSMITTING);
                            status.setDataReady(false);
                        }
                        receiveData(inputStream);
                        synchronized (status) {
                            status.setDataReady(true); //clean data ready status after reload configuration on client side
                            status.setServerStateType(ServerStatus.ServerStateType.WAITING);
                        }
                    default:
                        logger.info(String.format("Request %s is not valid, ignore this request", request));
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }
        }
        logger.info(String.format("Server thread for %s:%s is finished", socket.getInetAddress().getHostAddress(), socket.getPort()));
    }
}

class StatusReportThread implements Runnable {
    long interval;
    ObjectOutputStream outputStream;
    ServerStatus status;
    Logger logger;

    public StatusReportThread(long interval, ObjectOutputStream outputStream, ServerStatus status) {
        this.interval = interval;
        this.outputStream = outputStream;
        this.status = status;
        this.logger = Logger.getLogger(this.getClass().getName());
    }

    @Override
    public void run() {
        logger.info("Status report thread running ...");
        while (true) {
            try {
                synchronized (status) {
                    outputStream.writeObject(status);
                }
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}