package datasource;

import java.io.IOException;

/**
 * Represents a generic data source that provides data of the given type
 * @param <Type> type of data provided by this data source
 */
public interface DataSource<Type> {

    /**
     * Returns the data from the data source
     * @return the data
     * @throws IOException if the data cannot be read
     */
    Type read() throws IOException;
}