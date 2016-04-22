package org.susa.microserver.launcher;

/**
 * Created by susankha on 4/21/16.
 */
public class Main {
    /**
     * @param args
     */

    public static void main(String[] args) {

        MicroServer microServer = new MicroServer(9000);
        microServer.start();

    }

}
