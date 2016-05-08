package org.susa.microserver.launcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by susankha on 4/21/16.
 */
public class MicroServer extends Thread {

    private static final Logger log = Logger.getLogger(MicroServer.class.getName());
    private int serverPort = 6066;
    private Thread runningThread;
    private boolean isStoped = false;
    private ServerSocket serverSocket;

    public MicroServer(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override public void run() {
        super.run();

        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }

        try {
            serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Socket socket;
        while (!isStoped) {
            try {
                log.info(" Server is listening on port " + serverSocket.getLocalPort() + " ....");
                socket = serverSocket.accept();
                socket.setKeepAlive(true);
                log.info(" Server is connected to " + socket.getRemoteSocketAddress());
                new Thread(new ServerWorker(socket)).start();
            } catch (IOException e) {
                log.log(Level.SEVERE, " IO exception occured while reading the request  ", e);
            }

        }
    }
}
