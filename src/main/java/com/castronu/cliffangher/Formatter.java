package com.castronu.cliffangher;

import com.castronu.cliffangher.generated.*;

import javax.xml.bind.JAXBElement;
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
        OptionsType options = root.getValue().getOptions();
        String executablePath = root.getValue().getPath();
        String description = root.getValue().getDescription();
        List<String> logs = root.getValue().getLogs().getLog();

        try {
            String container = readFile("templates/container.h_tml");

            String formHeader = readFile("templates/formheader.h_tml");

            String formFooter = readFile("templates/formfooter.h_tml");

            String formArgument = readFile("templates/formargument.h_tml");

            String formOption = readFile("templates/formoption.h_tml");

            String formOptionsHeader = readFile("templates/formoptionsheader.h_tml");

            String form = String.format(formHeader,executablePath,description,
                    executablePath, executablePath);

            List<ArgumentType> argument = root.getValue().getArguments().getArgument();

            for (ArgumentType argumentType : argument) {
                String argumentName = argumentType.getName();
                form += String.format(formArgument, argumentName, argumentName, argumentName);
            }

            form += formOptionsHeader;

            List<OptionType> option = options.getOption();

            for (OptionType optionType : option) {
                form += String.format(formOption, optionType.getName(), optionType.getName(), optionType.getName(),
                        optionType.getDescription());
            }

            form += formFooter;

            String finalResult = container.replace("<!--[FORM_CONTENT]-->", form);

            return finalResult;
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException("Cannot create html page for meteor.");
        }
    }

    private static String readFile(String path)
            throws IOException, URISyntaxException {
        byte[] encoded = Files.readAllBytes(Paths.get(
                Thread.currentThread().getContextClassLoader().getResource(path).toURI()));
        return new String(encoded, Charset.defaultCharset());
    }
}
