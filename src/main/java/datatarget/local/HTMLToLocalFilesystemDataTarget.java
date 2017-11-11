package datatarget.local;

import datatarget.DataTarget;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class HTMLToLocalFilesystemDataTarget implements DataTarget<Map<String, StringWriter>> {

    private File outputDirectory;

    public HTMLToLocalFilesystemDataTarget(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void write(Map<String, StringWriter> htmlPages) throws IOException{

        if (!outputDirectory.exists()) {
            if(!outputDirectory.mkdirs()) {
                throw new IOException("Directory " + outputDirectory.getAbsolutePath() + " or its parent directories could not be created!");
            }
        }

        for(Map.Entry<String, StringWriter> htmlPage : htmlPages.entrySet()) {

            String fileName = htmlPage.getKey();
            File file = new File(outputDirectory, fileName);

            try {
                file.createNewFile();
            } catch (IOException e){
                throw new IOException("File " + file.getAbsolutePath() + " could not be created!", e);
            }

            try(FileWriter fileWriter = new FileWriter(file, false)) {
                fileWriter.write(htmlPage.toString());
            } catch (IOException e) {
                throw new IOException("I/O error occurred while writing HTML text to file!", e);
            }
        }
    }
}
