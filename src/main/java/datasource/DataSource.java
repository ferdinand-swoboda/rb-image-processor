package datasource;

import java.io.IOException;

public interface DataSource<Type extends Object> {

    Type read() throws IOException;
}
