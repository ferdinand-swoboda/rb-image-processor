package transformation;

import domain.Work;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.*;

public class WorksToHTMLTextsTransformationTest {

    @Test
    public void transformationShouldProduceCorrectNumberOfTexts() {
        WorksToHTMLTextsTransformation transformation = new WorksToHTMLTextsTransformation();
        Map<String, StringWriter> pages = transformation.apply(createWorks());

        Assert.assertEquals(5, pages.size());
    }

    @Test
    public void transformationShouldProduceCorrectIndexHTMLText() throws IOException {
        WorksToHTMLTextsTransformation transformation = new WorksToHTMLTextsTransformation();
        Map<String, StringWriter> pages = transformation.apply(createWorks());

        String expectedIndexPage;
        try(FileInputStream inputStream = new FileInputStream(getClass().getResource("test_index.html").getFile())) {
            expectedIndexPage = IOUtils.toString(inputStream, Charset.defaultCharset());
        }

        String actualIndexPage = pages.get("index.html").toString();

        Assert.assertEquals(expectedIndexPage, actualIndexPage);
    }

    @Test
    public void transformationShouldProduceCorrectCameraMakeHTMLText() throws IOException {
        WorksToHTMLTextsTransformation transformation = new WorksToHTMLTextsTransformation();
        Map<String, StringWriter> pages = transformation.apply(createWorks());

        String expectedNikonPage;
        try(FileInputStream inputStream = new FileInputStream(getClass().getResource("test_imagesOf_NIKON-CORPORATION.html").getFile())) {
            expectedNikonPage = IOUtils.toString(inputStream, Charset.defaultCharset());
        }

        String actualNikonPage = pages.get("imagesOf_NIKON-CORPORATION.html").toString();

        String expectedCanonPage;
        try(FileInputStream inputStream = new FileInputStream(getClass().getResource("test_imagesOf_Canon.html").getFile())) {
            expectedCanonPage = IOUtils.toString(inputStream, Charset.defaultCharset());
        }

        String actualCanonPage = pages.get("imagesOf_Canon.html").toString();

        Assert.assertEquals(expectedNikonPage, actualNikonPage);
        Assert.assertEquals(expectedCanonPage, actualCanonPage);
    }

    @Test
    public void transformationShouldProduceCorrectCameraMakeAndModelHTMLText() throws IOException {
        WorksToHTMLTextsTransformation transformation = new WorksToHTMLTextsTransformation();
        Map<String, StringWriter> pages = transformation.apply(createWorks());

        String expectedNikonD80Page;
        try(FileInputStream inputStream = new FileInputStream(getClass().getResource("test_imagesOf_NIKON-CORPORATION_Nikon-D80.html").getFile())) {
            expectedNikonD80Page = IOUtils.toString(inputStream, Charset.defaultCharset());
        }

        String actualNikonD80Page = pages.get("imagesOf_NIKON-CORPORATION_Nikon-D80.html").toString();

        String expectedCanonEOS20DPage;
        try(FileInputStream inputStream = new FileInputStream(getClass().getResource("test_imagesOf_Canon_Canon-EOS-20D.html").getFile())) {
            expectedCanonEOS20DPage = IOUtils.toString(inputStream, Charset.defaultCharset());
        }

        String actualCanonEOS20DPage = pages.get("imagesOf_Canon_Canon-EOS-20D.html").toString();

        Assert.assertEquals(expectedNikonD80Page, actualNikonD80Page);
        Assert.assertEquals(expectedCanonEOS20DPage, actualCanonEOS20DPage);
    }

    private List<Work> createWorks() {
        List<Work> works = new LinkedList<>();
        works.add(createWork(31820, "http://ih1.redbubble.net/work.31820.1.flat,135x135,075,f.jpg", "http://ih1.redbubble.net/work.31820.1.flat,300x300,075,f.jpg", "http://ih1.redbubble.net/work.31820.1.flat,550x550,075,f.jpg", "NIKON D80", "NIKON CORPORATION")
        );

        works.add(createWork(867035, "http://ih1.redbubble.net/work.867035.1.flat,135x135,075,f.jpg", "http://ih1.redbubble.net/work.867035.1.flat,300x300,075,f.jpg", "http://ih1.redbubble.net/work.867035.1.flat,550x550,075,f.jpg", null, null)
        );

        works.add(createWork(2041, "http://ih1.redbubble.net/work.2041.1.flat,135x135,075,f.jpg", "http://ih1.redbubble.net/work.2041.1.flat,300x300,075,f.jpg", "http://ih1.redbubble.net/work.2041.1.flat,550x550,075,f.jpg", "Canon EOS 20D", "Canon")
        );

        return works;
    }

    private Work createWork(int id, String urlSmallImage, String urlMediumImage, String urlLargeImage, String exifModel, String exifMake) {
        Map<String, String> urlsOfImages = new HashMap<>();
        urlsOfImages.put("small", urlSmallImage);
        urlsOfImages.put("medium", urlMediumImage);
        urlsOfImages.put("large", urlLargeImage);
        return new Work(id, urlsOfImages, exifModel, exifMake);
    }
}
