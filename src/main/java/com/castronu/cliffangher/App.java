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
import java.util.List;

/**
 * Created by castronu on 31/01/15.
 */
public class App {

    public static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws JAXBException, IOException {
        JAXBContext jc = JAXBContext.newInstance("com.castronu.cliffangher.generated");
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        Source source = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.xml"));

        JAXBElement<ExecutableType> root = unmarshaller.unmarshal(
                source, ExecutableType.class);

        OptionsType options = root.getValue().getOptions();
        List<String> logs = root.getValue().getLogs().getLog();

        StringBuilder data=new StringBuilder();
        //Header
        data.append("<head>\n" +
                "    <title>simple-todos</title>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "<h1>Welcome to Meteor!</h1>\n" +
                "\n" +
                "{{> hello}}\n" +
                "\n" +
                "\n" +
                "{{> commands}}\n" +
                "\n" +
                "\n" +
                "</body>\n" +
                "\n" +
                "<template name=\"commands\">");

        for (String log : logs) {
            data.append("<h1> Log path:"+log+"</h1>");
            LOGGER.info(log);

        }

        LOGGER.info(root.getValue().getPath());

        data.append("<h1>Executable path:"+root.getValue().getPath()+"</h1>");

        List<OptionType> option = options.getOption();
        data.append("<ul>");
        for (OptionType optionType : option) {

            data.append("<li>");
            data.append(optionType.getDescription());
            LOGGER.info(optionType.getDescription());
            data.append("</li>");

            data.append("<li>");data.append(optionType.getName());LOGGER.info(optionType.getName());     data.append("</li>");
            data.append("<li>");data.append(optionType.getRequired());LOGGER.info(optionType.getRequired());
            data.append("</li>");


        }
        data.append("</ul>");
        //Footer
        data.append("</template>\n" +
                "\n" +
                "\n" +
                "<template name=\"hello\">\n" +
                "    <button>click button</button>\n" +
                "\n" +
                "    <p>You've pressed the button {{counter}} times.</p>\n" +
                "</template>");

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

