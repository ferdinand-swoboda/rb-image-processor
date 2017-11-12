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

public class ImageProcessor {

    private DataSource<List<Work>> source;
    private DataTarget<Map<String, StringWriter>> target;

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

    private void init(URL apiUrl, File outputDir) {
        source = new RemoteXMLToWorksDataSource(apiUrl);
        target = new TextsToLocalFilesystemDataTarget(outputDir);
    }

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
