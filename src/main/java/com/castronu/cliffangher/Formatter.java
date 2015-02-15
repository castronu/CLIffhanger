package com.castronu.cliffangher;

import com.castronu.cliffangher.generated.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.JAXBElement;
import java.io.IOException;
import java.util.List;

/**
 * Created by castronu on 01/02/15.
 */
class Formatter {

    private static String formArgument = readFile("templates/formargument.h_tml");

    private static String formOption = readFile("templates/formoption.h_tml");

    private static String formOptionsHeader = readFile("templates/formoptionsheader.h_tml");

    private static String container = readFile("templates/container.h_tml");

    private static String formHeader = readFile("templates/formheader.h_tml");

    private static String formFooter = readFile("templates/formfooter.h_tml");

    private static String meteorCore = readFile("templates/js/core.j_s");

    private static String modeEvent = readFile("templates/js/modeEvent.j_s");

    private static String modeRendered = readFile("templates/js/modeRendered.j_s");

    private static String modeSession = readFile("templates/js/modeSession.j_s");


    public static String produceJavascript(JAXBElement<ExecutableType> root) {

        ModesType modes = root.getValue().getModes();

        if (modes == null) {
            return meteorCore;
        }

        String finalContent = "";

        String modeJsContent = "";

        List<ModeType> mode = root.getValue().getModes().getMode();

        String modeEventInnerLine = "";
        for (ModeType modeType : mode) {

            String modeName = modeType.getName();
            modeJsContent += String.format(modeRendered,modeName);
            modeEventInnerLine += "Session.set(\""+modeName+"Selected\", false);";

        }

        modeJsContent += String.format(modeEvent,modeEventInnerLine);

        for (ModeType modeType : mode) {

            String modeName = modeType.getName();
            modeJsContent += String.format(modeSession,modeName,modeName);

        }

        finalContent = meteorCore.replace("/*!MODES!*/", modeJsContent);

        return finalContent;
    }

    public static String produceHtmlPage(JAXBElement<ExecutableType> root) {

        String executablePath = root.getValue().getPath();
        String description = root.getValue().getDescription();

        String form = String.format(formHeader, executablePath, description,
                executablePath, executablePath);

        LogsType logs = root.getValue().getLogs();

        if (logs != null) {
            String loghidden = readFile("templates/loghidden.h_tml");
            String logpath = logs.getLog();

            form += String.format(loghidden, logpath);

        }

        //Produce global arguments and options
        OptionsType options = root.getValue().getOptions();
        ArgumentsType arguments = root.getValue().getArguments();
        form += produceArgumentsAndOptions(arguments, options);

        form += produceModes(root);

        form += formFooter;

        String modesTemplates = produceModesTemplates(root);

        String finalResult = container.replace("<!--[FORM_CONTENT]-->", form);

        finalResult = finalResult.replace("<!--[MODES]-->", modesTemplates);

        return finalResult;

    }

    private static String produceModesTemplates(JAXBElement<ExecutableType> root) {

        if (root.getValue().getModes() == null) {
            return "";
        }

        List<ModeType> mode = root.getValue().getModes().getMode();
        //TODO refactor to use standard "template" pattern...
        String modesTemplates="";
        modesTemplates += "<template name=\"modes\">";
        String template=" {{#if is%sSelected}}\n" +
                "         {{> %s}}\n" +
                "    {{/if}}";

        for (ModeType modeType : mode) {
            modesTemplates += String.format(template, modeType.getName(), modeType.getName());
        }

        modesTemplates += "</template>";

        for (ModeType modeType : mode) {
            String header = "<template name=\"%s\">";
            modesTemplates += String.format(header,modeType.getName());
            modesTemplates += produceArgumentsAndOptions(modeType.getArguments(), modeType.getOptions());
            String footer = "</template>";
            modesTemplates += footer;
        }

        return  modesTemplates;

    }

    private static String produceModes(JAXBElement<ExecutableType> root) {

        String modesOutput="";

        String formModes = readFile("templates/formmodes.h_tml");
        String formModesHeader = readFile("templates/formmodesheader.h_tml");
        String formModesFooter = readFile("templates/formmodesfooter.h_tml");

        ModesType modes = root.getValue().getModes();

        if (modes == null) {
            return "";
        }

        List<ModeType> mode = modes.getMode();

        modesOutput += formModesHeader;

        for (ModeType modeType : mode) {
            String name = modeType.getName();
            String value = modeType.getValue();
            if (StringUtils.isEmpty(name)||StringUtils.isEmpty(value)) {
               throw new IllegalStateException("Problems in your configuration on mode tag. Name or value empty.");
            }
            modesOutput += String.format(formModes, value, name);
        }

        modesOutput += formModesFooter;
        return modesOutput;
    }

    private static String produceArgumentsAndOptions(ArgumentsType arguments, OptionsType options) {
        String argumentsAndOptions = "";

        if (arguments != null) {
            List<ArgumentType> argument = arguments.getArgument();
            for (ArgumentType argumentType : argument) {
                String argumentName = argumentType.getName();
                argumentsAndOptions += String.format(formArgument, argumentName, argumentName, argumentName);
            }
        }
        argumentsAndOptions += formOptionsHeader;

        if (options != null) {
            List<OptionType> option = options.getOption();

            for (OptionType optionType : option) {
                argumentsAndOptions += String.format(formOption, optionType.getName(), optionType.getName(),
                        optionType.getName(),
                        optionType.getDescription());
            }
        }
        return argumentsAndOptions;
    }

    private static String readFile(String path) {
        try {
            return IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            throw new IllegalStateException("Problem reading templates. Cannot continue.", e);
        }

    }
}
