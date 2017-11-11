package datasource;

import java.io.IOException;

public interface DataSource<Type> {

    Type read() throws IOException;
}
