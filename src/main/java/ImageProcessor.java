import datasource.DataSource;
import datasource.remote.RemoteXMLToWorksDataSource;
import datatarget.DataTarget;
import datatarget.local.TextsToLocalFilesystemDataTarget;
import domain.Work;
import transformation.WorksToHTMLTextsTransformation;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * An image processor that retrieves work image data from an API URL, generates a static set of HTML pages of the image data
 * and writes the HTML pages to the local filesystem.
 * It takes two command line arguments, the API URL and the file directory to be written to.
 */
public class ImageProcessor {

    /**
     * the source of the list of works
     */
    private DataSource<List<Work>> source;
    /**
     * the target of the HTML texts
     */
    private DataTarget<Map<String, StringWriter>> target;

    /**
     * Reads the required 2 command line arguments, sets data source and target and starts the process
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        if (args.length != 2){
            System.err.println("Usage: java ImageProcessor <API URL> <output directory>");
            System.exit(1);
        }

        URL apiUrl = null;
        try {
            apiUrl = new URL(args[0]);
        } catch (MalformedURLException e){
            System.err.println("Given API URL is malformed!");
            e.printStackTrace();
            System.exit(1);
        }

        File outputDir = new File(args[1]);

        ImageProcessor processor = new ImageProcessor();
        processor.init(apiUrl, outputDir);
        processor.run();
    }

    /**
     * Sets data source and target based on the given API URL and output file directory
     * @param apiUrl the given API URL
     * @param outputDir the given output file directory
     */
    private void init(URL apiUrl, File outputDir) {
        source = new RemoteXMLToWorksDataSource(apiUrl);
        target = new TextsToLocalFilesystemDataTarget(outputDir);
    }

    /**
     * Processes the data obtained from the data source using a works to HTML transformation and
     * writes the resulting HTML texts to the data target
     */
    private void run() {
        List<Work> works = null;

        try {
            works = source.read();
        } catch (IOException e) {
            System.err.println("An error occurred while reading from the data source!");
            e.printStackTrace();
            System.exit(1);
        }

        Map<String, StringWriter> htmlPages = new WorksToHTMLTextsTransformation().apply(works);

        try {
            target.write(htmlPages);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the data target!");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
