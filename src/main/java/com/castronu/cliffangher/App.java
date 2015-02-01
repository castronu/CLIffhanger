package com.castronu.cliffangher;

import javax.xml.bind.JAXBContext;
import com.castronu.cliffangher.generated.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;

/**
 * Created by castronu on 31/01/15.
 */
public class App {

    public static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws JAXBException, IOException {
        JAXBContext jc = JAXBContext.newInstance("com.castronu.cliffangher.generated");
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        if (args.length==0) {
            LOGGER.error("Please provide the config file. Usage ./CLIffhanger.sh [config.xml]");
            System.exit(1);
        }
        String config = args[0];
        Source source = new StreamSource(new File(config));

        JAXBElement<ExecutableType> root = unmarshaller.unmarshal(
                source, ExecutableType.class);

        StringBuilder data = new Formatter(root).invoke();

        FileUtils.writeStringToFile(new File("index.html"),data.toString());

        ProcessBuilder builder = new ProcessBuilder("meteor");
        builder.redirectErrorStream(true);
        Process process = builder.start();
        StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");

// any output?
        StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT");

// start gobblers
        outputGobbler.start();
        errorGobbler.start();

    }

}

