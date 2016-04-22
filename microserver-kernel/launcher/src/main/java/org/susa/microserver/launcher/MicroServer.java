package org.susa.microserver.launcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by susankha on 4/21/16.
 */
public class MicroServer extends Thread {

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

        Socket socket;

        try {
            serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!isStoped) {
            try {
                System.out.println(" Server is listening on port " + serverSocket.getLocalPort() + " ....");
                socket = serverSocket.accept();
                System.out.println(" Server is connected to " + socket.getRemoteSocketAddress());
                processclientRequest(socket);
                System.out.println(" Thank you !!!");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * @param conectedSocket
     * @throws IOException
     */
    public void processclientRequest(Socket conectedSocket) throws IOException {

        InputStream inputStream = conectedSocket.getInputStream();
        readInput(inputStream);

        OutputStream outputStream = conectedSocket.getOutputStream();
        writeOutput(outputStream);
        System.out.println(" Request Proceed !!!! ");

    }

    public void readInput(InputStream inputStream) throws IOException {

        BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
        String inputLine = null;
        try {
            inputLine = inputReader.readLine();
            while (inputLine != null || inputLine == "") {
                System.out.println(" *** input *** = " + inputLine);
                inputLine = inputReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null)
                inputStream.close();
            if (inputReader != null)
                inputReader.close();
        }

    }

    public void writeOutput(OutputStream outputStream) throws IOException {

        outputStream.write(("\"HTTP/1.1 200 OK\\n\\n<html><body>\" " + " Welcome to the Wso2 !!! " + "</body></html>")
                .getBytes());
        outputStream.flush();
        outputStream.close();
    }

}
