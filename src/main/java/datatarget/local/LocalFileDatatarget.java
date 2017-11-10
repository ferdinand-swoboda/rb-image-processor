package datatarget.local;

import datatarget.Datatarget;

import java.io.File;

public class LocalFileDatatarget implements Datatarget<File> {

    private File outputDirectory;

    public LocalFileDatatarget(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void write(File data) {

    }
}
