import datasource.Datasource;
import datasource.remote.RemoteWorkDatasource;
import datatarget.Datatarget;
import datatarget.local.LocalFileDatatarget;
import domain.Work;
import transformation.WorksToHTMLFilesTransformation;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ImageProcessor {

    private Datasource<List<Work>> source;
    private Datatarget<File> target;

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
        source = new RemoteWorkDatasource(apiUrl);
        target = new LocalFileDatatarget(outputDir);
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
        File htmlPages = new WorksToHTMLFilesTransformation().apply(works);
        target.write(htmlPages);
    }
}
