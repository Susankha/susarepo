package org.susa.microserver.launcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by susankha on 5/7/16.
 */
public class ServerWorker implements Runnable {

    public static final Logger log = Logger.getLogger(ServerWorker.class.getName());

    private Socket socket;

    public ServerWorker(Socket socket) {
        this.socket = socket;
    }

    @Override public void run() {
        try{
            log.info("Starting worker Thread = "+Thread.currentThread().getId());
            processClientRequest(this.socket);
            Thread.sleep(1000);
            log.info("Done worker Thread = "+Thread.currentThread().getId());
        }catch (IOException e){
            log.log(Level.SEVERE, " IO exception occured while processing the client request  ", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param conectedSocket
     * @throws java.io.IOException
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
    private synchronized void readInput(InputStream inputStream) {

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
    private synchronized void writeOutput(OutputStream outputStream) {

        try {
            long currentTime = System.currentTimeMillis();
            outputStream
                    .write(("\"HTTP/1.1 200 OK\\n\\n<html><body>\" " + " Welcome to the Wso2 !!! "+currentTime + "</body></html>")
                            .getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, " IO exception occured while processing the response  ", e);
        }
    }
}
