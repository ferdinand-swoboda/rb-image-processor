package datasource.remote;

import domain.Work;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;


public class RemoteXMLToWorksDataSourceTest {

    private RemoteXMLToWorksDataSource underTest;

    @After
    public void clearUnderTest() {
        underTest = null;
    }

    @Test
    public void readingXMLFromFilesystemShouldReturnListWithSampleWork() throws IOException {
        URL localXMLUrl = getClass().getResource("works.xml");
        underTest = new RemoteXMLToWorksDataSource(localXMLUrl);
        List<Work> works = underTest.read();

        int expectedSize = 2;
        int actualSize = works.size();
        Assert.assertEquals(expectedSize, actualSize);

        Work expectedWork = new Work(31820, Arrays.asList("http://ih1.redbubble.net/work.31820.1.flat,135x135,075,f.jpg", "http://ih1.redbubble.net/work.31820.1.flat,300x300,075,f.jpg", "http://ih1.redbubble.net/work.31820.1.flat,550x550,075,f.jpg"), "NIKON D80", "NIKON CORPORATION");
        Work actualWork = works.get(0);
        Assert.assertEquals(expectedWork, actualWork);
    }

    @Test
    public void readingXMLFromAPIShouldReturnListWithCertainLastWork() throws IOException {
        URL apiUrl = new URL("http://take-home-test.herokuapp.com/api/v1/works.xml");
        underTest = new RemoteXMLToWorksDataSource(apiUrl);
        List<Work> works = underTest.read();

        int expectedSize = 14;
        int actualSize = works.size();
        Assert.assertEquals(expectedSize, actualSize);

        Work expectedWork = new Work(867035, Arrays.asList("http://ih1.redbubble.net/work.867035.1.flat,135x135,075,f.jpg", "http://ih1.redbubble.net/work.867035.1.flat,300x300,075,f.jpg", "http://ih1.redbubble.net/work.867035.1.flat,550x550,075,f.jpg"), null, null);
        Work actualWork = works.get(0);
        Assert.assertEquals(expectedWork, actualWork);
    }
}
