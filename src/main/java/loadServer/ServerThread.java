package loadServer;

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

    public ServerThread(Socket socket) {
        this.socket = socket;
        logger = Logger.getLogger(getClass().getName());
    }

    private void receiveData(String fileName, DataInputStream inputStream) throws IOException {
        long size = inputStream.readLong();
        int bufSize = 256;
        byte[] buff = new byte[bufSize];
        int readSize = 0;
        while (size > 0 && (readSize = inputStream.read(buff, 0, (int) Math.min(buff.length, size))) != -1) {
            logger.info("Trans file ...");
            size -= readSize;
        }
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
                if (socket.isClosed())
                    break;
                String command = inputStream.readUTF();
                logger.info(String.format("Receive request %s", command));
                switch (command) {
                    case SET_TARGET_REQ:
                        setTarget(inputStream);
                        break;
                    case START_LOAD_REQ:
                        break;
                    case STOP_LOAD_REQ:
                        break;
                    case CLEAN_REQ:
                        break;
                    default:
                        //If no command matched, regard it as data transfer
                        receiveData(command, inputStream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        logger.info(String.format("Server thread for %s:%s is finished", socket.getInetAddress().getHostAddress(), socket.getPort()));
    }
}
