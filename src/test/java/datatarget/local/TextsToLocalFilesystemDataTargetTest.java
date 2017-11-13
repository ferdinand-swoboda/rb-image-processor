package datatarget.local;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class TextsToLocalFilesystemDataTargetTest {

    private File outputDir;
    private TextsToLocalFilesystemDataTarget underTest;

    @Before
    public void setOutputDir() throws IOException {
        outputDir = Files.createTempDirectory("image-processor-test-output").toFile();
        underTest = new TextsToLocalFilesystemDataTarget(outputDir);
    }

    @After
    public void cleanupOutputDir() throws IOException{
        FileUtils.deleteDirectory(outputDir);
        outputDir = null;
        underTest = null;
    }

    @Test
    public void writingTextsShouldWriteAllTexts() throws IOException {
        Map<String, StringWriter> texts = createTexts();
        underTest.write(texts);

        int expected = 3;
        int actual = outputDir.listFiles().length;
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void writingTextsShouldPersistFilesCorrectly() throws IOException {
        Map<String, StringWriter> texts = createTexts();
        underTest.write(texts);

        String expected;
        String actual;

        for(Map.Entry<String, StringWriter> page : texts.entrySet()) {
            expected = page.getValue().toString();
            try(FileInputStream inputStream = new FileInputStream(new File(outputDir, page.getKey()))) {
                actual = IOUtils.toString(inputStream, Charset.defaultCharset());
            }
            Assert.assertEquals(expected, actual);
        }
    }

    private Map<String, StringWriter> createTexts() {
        Map<String, StringWriter> pages = new HashMap<>();
        pages.put("page1.txt", createText(1));
        pages.put("page2.txt", createText(2));
        pages.put("page3.txt", createText(3));
        return  pages;
    }

    private StringWriter createText(int id) {
        StringWriter page = new StringWriter();
        page.append("This the text of text " + id + ".");
        return page;
    }
}
