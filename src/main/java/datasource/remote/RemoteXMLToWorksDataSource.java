package datasource.remote;

import datasource.DataSource;
import domain.Work;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class RemoteXMLToWorksDataSource implements DataSource<List<Work>> {

    private WorkWebservice webservice;

    public RemoteXMLToWorksDataSource(URL apiUrl) {
        webservice = new Retrofit.Builder()
                .baseUrl(apiUrl.toString()).addConverterFactory(SimpleXmlConverterFactory.create()).build().create(WorkWebservice.class);
    }

    @Override
    public List<Work> read() throws IOException{
        List<Work> result;
        Response<List<Work>> response = webservice.getWorks().execute();
        if (response.isSuccessful()) {
            result = response.body();
        } else {
            throw new IOException("Response from API failed! Response code was " + response.code() + " and the error message is\n" + response.errorBody().string());
        }

        return result;
    }
}
