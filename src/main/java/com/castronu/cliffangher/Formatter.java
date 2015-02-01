package com.castronu.cliffangher;

import com.castronu.cliffangher.generated.*;

import javax.xml.bind.JAXBElement;
import java.util.List;

/**
* Created by castronu on 01/02/15.
*/
class Formatter {
    private JAXBElement<ExecutableType> root;

    public Formatter(JAXBElement<ExecutableType> root) {
        this.root = root;
    }

    public StringBuilder invoke() {
        OptionsType options = root.getValue().getOptions();
        List<String> logs = root.getValue().getLogs().getLog();

        StringBuilder data=new StringBuilder();
        //Header
        data.append("<head>\n" +
                "    <title>Cliffhanger</title>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "<h1>Welcome to Cliffhanger!</h1>\n" +
                "\n" +
                "{{> commands}}\n" +
                "\n" +
                "{{> logss}}\n" +
                "\n" +
                "</body>\n" +
                "\n" +
                "\n" +
                "<template name=\"commands\">\n" +
                "     <!-- add a form below the h1 -->\n" +
                "    <form class=\"new-task\" name=\"myform\" id=\"myform\">");

        for (String log : logs) {
            App.LOGGER.info(log);
        }

        App.LOGGER.info(root.getValue().getPath());

        data.append(String.format("<input type=\"text\" name=\"%s\" value=\"%s\"/><br>",
                root.getValue().getPath(),root.getValue().getPath()));

        List<OptionType> option = options.getOption();

        for (OptionType optionType : option) {

            String template = "<input type=\"checkbox\" name=\"%s\" value=\"%s\"/> %s  %s<br>";



            data.append(String.format(template,optionType.getName(),optionType.getName(),optionType.getName(),
                    optionType.getDescription()));

            data.append("\n");

            App.LOGGER.info(optionType.getDescription());
            App.LOGGER.info(optionType.getName());
            App.LOGGER.info(optionType.getRequired());
        }

        List<ArgumentType> argument = root.getValue().getArguments().getArgument();

        for (ArgumentType argumentType : argument) {

            String template = "<input type=\"text\" name=\"%s\" value=\"%s\" placehloder=\"%s\"/><br>";

            data.append(String.format(template,argumentType.getName(),
                    argumentType.getName(),argumentType.getName()));
        }

        //Footer
        data.append("<input type=\"submit\" value=\"Submit\">" +
                " </form>\n" +
                "</template>" +
                " <template name=\"logss\">\n" +
                "    {{text}}\n" +
                "</template>");
        return data;
    }
}
