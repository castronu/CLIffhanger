package com.castronu.cliffangher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by castronu on 31/01/15.
 */
class StreamGobbler extends Thread {
    InputStream is;
    String type;

    public StreamGobbler(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }

    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null)
                System.out.println(type + "> " + line);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
