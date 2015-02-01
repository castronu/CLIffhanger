package com.castronu.cliffangher;

import com.castronu.cliffangher.generated.ExecutableType;
import com.castronu.cliffangher.generated.OptionType;
import com.castronu.cliffangher.generated.OptionsType;

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
            App.LOGGER.info(log);

        }

        App.LOGGER.info(root.getValue().getPath());

        data.append("<h1>Executable path:"+root.getValue().getPath()+"</h1>");

        List<OptionType> option = options.getOption();
        data.append("<ul>");
        for (OptionType optionType : option) {

            data.append("<li>");
            data.append(optionType.getDescription());
            App.LOGGER.info(optionType.getDescription());
            data.append("</li>");

            data.append("<li>");data.append(optionType.getName());
            App.LOGGER.info(optionType.getName());     data.append("</li>");
            data.append("<li>");data.append(optionType.getRequired());
            App.LOGGER.info(optionType.getRequired());
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
        return data;
    }
}
