package loadServer;

import Common.ServerStatus;
import loadClient.loadController.Target;

import java.io.*;
import java.net.Socket;
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
        try {
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                        break;
                    case STOP_LOAD_REQ:
                        break;
                    case CLEAN_REQ:
                        break;
                    case TRANS_FILE:
                        receiveData(inputStream);
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
