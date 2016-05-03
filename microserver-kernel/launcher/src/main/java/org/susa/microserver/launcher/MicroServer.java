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

        while (!isStoped) {
            Socket socket;
            try {
                log.info(" Server is listening on port " + serverSocket.getLocalPort() + " ....");
                socket = serverSocket.accept();
                socket.setKeepAlive(true);
                log.info(" Server is connected to " + socket.getRemoteSocketAddress());
                processClientRequest(socket);
                log.info(" request done");
            } catch (IOException e) {
                log.log(Level.SEVERE, " IO exception occured while reading the request  ", e);
            }

        }
    }

    /**
     * @param conectedSocket
     * @throws IOException
     */
    public void processClientRequest(Socket conectedSocket) throws IOException {

        InputStream inputStream = conectedSocket.getInputStream();
        if (inputStream != null) {
            readInput(inputStream);
            conectedSocket.shutdownInput();
        }
        OutputStream outputStream = conectedSocket.getOutputStream();
        if (outputStream != null) {
            writeOutput(outputStream);
            conectedSocket.shutdownOutput();
        }
    }

    /**
     * @param inputStream
     * @throws IOException
     */
    public void readInput(InputStream inputStream) {

        BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder requestContent = new StringBuilder("");
        try {
            while (inputReader.ready()) {
                requestContent.append((char) inputReader.read());
            }
            log.info(" Request content : " + requestContent.toString());
        } catch (IOException e) {
            log.log(Level.SEVERE, " IO exception occured while processing the request  ", e);
        }

    }

    /**
     * @param outputStream
     * @throws IOException
     */
    public void writeOutput(OutputStream outputStream) {

        try {
            outputStream
                    .write(("\"HTTP/1.1 200 OK\\n\\n<html><body>\" " + " Welcome to the Wso2 !!! " + "</body></html>")
                            .getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, " IO exception occured while processing the response  ", e);
        }
    }

}
