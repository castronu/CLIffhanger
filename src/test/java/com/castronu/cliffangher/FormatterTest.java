package com.castronu.cliffangher;

import com.castronu.cliffangher.generated.ExecutableType;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

import static org.hamcrest.CoreMatchers.is;


public class FormatterTest {

    @Test
    public void testInvoke() throws Exception {

        JAXBContext jc = JAXBContext.newInstance("com.castronu.cliffangher.generated");
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        Source source = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.xml"));

        JAXBElement<ExecutableType> root = unmarshaller.unmarshal(
                source, ExecutableType.class);

        StringBuilder data = new Formatter(root).invoke();

        String expected = FileUtils.readFileToString(new File(Thread.currentThread().getContextClassLoader().getResource("expectedIndex.html").getFile()));

        Assert.assertThat(data.toString(),is(expected));
    }
}