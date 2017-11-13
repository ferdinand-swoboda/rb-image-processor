package datatarget.local;

import datatarget.DataTarget;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * Represents a data target for String texts and associated text names. The texts are written to the local file system.
 */
public class TextsToLocalFilesystemDataTarget implements DataTarget<Map<String, StringWriter>> {

    /**
     * The lcoal file directory where the texts should be written to
     */
    private final File outputDirectory;

    /**
     * Creates a @code{TextsToLocalFilesystemDataTarget} with the given directory as the target file system directory
     * @param outputDirectory the given directory to be written to
     */
    public TextsToLocalFilesystemDataTarget(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * Write the given texts to the local file system using their associated text names
     * @param texts the given texts to be written
     * @throws IOException if the directory to be written to does not exist and cannot be created or a text could not be written
     */
    @Override
    public void write(Map<String, StringWriter> texts) throws IOException{

        if (!outputDirectory.exists()) {
            // create the output directory if it does not exist
            if(!outputDirectory.mkdirs()) {
                throw new IOException("Directory " + outputDirectory.getAbsolutePath() + " or its parent directories could not be created!");
            }
        }

        // for each text,
        for(Map.Entry<String, StringWriter> text : texts.entrySet()) {

            // create a new file with the name of the text as the file name
            String textName = text.getKey();
            File file = new File(outputDirectory, textName);

            try {
                file.createNewFile();
            } catch (IOException e){
                throw new IOException("File " + file.getAbsolutePath() + " could not be created!", e);
            }

            // write the text body to the created file
            try(FileWriter fileWriter = new FileWriter(file, false)) {
                StringWriter textBody = text.getValue();
                fileWriter.write(textBody.toString());
            } catch (IOException e) {
                throw new IOException("I/O error occurred while writing HTML text to file!", e);
            }
        }
    }
}
