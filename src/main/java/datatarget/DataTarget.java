package datatarget;

import java.io.IOException;

/**
 * Represents a data target for the given type of data
 * @param <Type> the given type of data that can be written by this target
 */
public interface DataTarget<Type> {

    /**
     * Writes the given data
     * @param data the given data to be written
     * @throws IOException if the given data could not be written
     */
    void write(Type data) throws IOException;
}
