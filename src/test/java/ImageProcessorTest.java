import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImageProcessorTest {

    @Test
    public void processImagesShouldResultIn2CanonModelPages() throws IOException{
        String[] args = new String[2];
        args[0] = "http://take-home-test.herokuapp.com/api/";
        Path outputDir = Files.createTempDirectory("image-processor-test-output");
        args[1] = outputDir.toString();
        ImageProcessor.main(args);

        Assert.assertTrue(new File(outputDir.toFile(), "imagesOf_Canon_Canon-EOS-20D.html").exists());
        Assert.assertTrue(new File(outputDir.toFile(), "imagesOf_Canon_Canon-EOS-400D-DIGITAL.html").exists());

        FileUtils.deleteDirectory(outputDir.toFile());
    }
}
