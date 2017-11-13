package datasource.remote;

import domain.Work;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RemoteXMLToWorksDataSourceTest {

    private RemoteXMLToWorksDataSource underTest;

    @After
    public void clearUnderTest() {
        underTest = null;
    }

    @Test
    public void readingXMLFromAPIShouldReturn14Works() throws IOException{
        URL apiUrl = new URL("http://take-home-test.herokuapp.com/api/");
        underTest = new RemoteXMLToWorksDataSource(apiUrl);
        List<Work> works = underTest.read();

        int expectedSize = 14;
        int actualSize = works.size();
        Assert.assertEquals(expectedSize, actualSize);
    }

    @Test
    public void readingXMLFromAPIShouldReturnListWithCertainLastWork() throws IOException {
        URL apiUrl = new URL("http://take-home-test.herokuapp.com/api/");
        underTest = new RemoteXMLToWorksDataSource(apiUrl);
        List<Work> works = underTest.read();

        Map<String, String> urlsOfImages = new HashMap<>();
        urlsOfImages.put("small","http://ih1.redbubble.net/work.31820.1.flat,135x135,075,f.jpg");
        urlsOfImages.put("medium","http://ih1.redbubble.net/work.31820.1.flat,300x300,075,f.jpg");
        urlsOfImages.put("large", "http://ih1.redbubble.net/work.31820.1.flat,550x550,075,f.jpg");
        Work expectedWork = new Work(31820, urlsOfImages, "NIKON D80", "NIKON CORPORATION");

        Work actualWork = works.get(0);
        Assert.assertEquals(expectedWork, actualWork);
    }
}
