package com.castronu.cliffangher;

import com.castronu.cliffangher.generated.ExecutableType;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class FormatterTest {

    @Test
    public void testHtml() throws Exception {

        JAXBContext jc = JAXBContext.newInstance("com.castronu.cliffangher.generated");
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        Source source = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.xml"));

        JAXBElement<ExecutableType> root = unmarshaller.unmarshal(
                source, ExecutableType.class);

        String data = Formatter.produceHtmlPage(root);

        String expected = FileUtils.readFileToString(new File(Thread.currentThread().getContextClassLoader().getResource("expectedIndex.html").getFile()));

        assertThat(expected.replaceAll("\\s+",""),is(data.replaceAll("\\s+","")));
    }
}