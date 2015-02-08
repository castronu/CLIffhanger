package com.castronu.cliffangher;

import com.castronu.cliffangher.generated.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by castronu on 01/02/15.
 */
class Formatter {

    public static String produceHtmlPage(JAXBElement<ExecutableType> root) {

        String executablePath = root.getValue().getPath();
        String description = root.getValue().getDescription();


        try {
            String container = readFile("templates/container.h_tml");

            String formHeader = readFile("templates/formheader.h_tml");

            String formFooter = readFile("templates/formfooter.h_tml");

            String formArgument = readFile("templates/formargument.h_tml");

            String formOption = readFile("templates/formoption.h_tml");

            String formOptionsHeader = readFile("templates/formoptionsheader.h_tml");

            String form = String.format(formHeader, executablePath, description,
                    executablePath, executablePath);

            ArgumentsType arguments = root.getValue().getArguments();

            LogsType logs = root.getValue().getLogs();

            if (logs != null) {
                String loghidden = readFile("templates/loghidden.h_tml");
                String logpath = logs.getLog();

                    form += String.format(loghidden,logpath);

            }

            if (arguments != null) {

                List<ArgumentType> argument = arguments.getArgument();

                for (ArgumentType argumentType : argument) {
                    String argumentName = argumentType.getName();
                    form += String.format(formArgument, argumentName, argumentName, argumentName);
                }
            }
            form += formOptionsHeader;


            OptionsType options = root.getValue().getOptions();
            if (options != null) {
                List<OptionType> option = options.getOption();

                for (OptionType optionType : option) {
                    form += String.format(formOption, optionType.getName(), optionType.getName(), optionType.getName(),
                            optionType.getDescription());
                }
            }

            form += formFooter;

            String finalResult = container.replace("<!--[FORM_CONTENT]-->", form);

            return finalResult;
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException("Cannot create html page for meteor.", e);
        }
    }

    private static String readFile(String path)
            throws IOException, URISyntaxException {
        String fileContent = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
        return fileContent;
    }
}
