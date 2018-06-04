package loadServer;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the work which create data based on the configurations. It host on the server and receive command from manger.
 */
public class LoadServer {
    ServerConfig config;
    ServerSocket serverSocket;
    List<Thread> clients;
    Logger logger;

    public LoadServer(String configClassPath) throws IOException {
        logger = Logger.getLogger(getClass().getName());
        logger.info("Initializing load server ...");
        config = new ServerConfig(configClassPath);
        String port = config.getProperty("port");
        serverSocket = new ServerSocket(Integer.valueOf(port));
        clients = new ArrayList<>();
        logger.info("Server init finished...");
    }

    public void start() throws IOException {
        Path fileTmpDir = Paths.get(config.getProperty("fileTempPath"));
        File tmpDir = new File(fileTmpDir.toUri());
        assert tmpDir.isDirectory();
        logger.info("Load server started,waiting for connection...");
        while (true) {
            Socket socket = serverSocket.accept();
            logger.info(String.format("Connection from %s is accepted.", socket.getInetAddress().getHostAddress()));
            Thread clientThread = new Thread(new ServerThread(socket, tmpDir));
            clientThread.start();
            clients.add(clientThread);
        }
    }

    public static void main(String[] args) throws IOException {
        LoadServer loadServer = new LoadServer("loadServerConfig.properties");
        loadServer.start();
    }
}
