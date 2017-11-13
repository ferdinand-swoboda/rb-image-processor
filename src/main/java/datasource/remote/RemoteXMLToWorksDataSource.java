package datasource.remote;

import datasource.DataSource;
import domain.Work;
import domain.WorkList;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Represents a remote data source of XML-encoded works that provides its data as a list of works
 */
public class RemoteXMLToWorksDataSource implements DataSource<List<Work>> {

    /**
     * the webservice used to access remote work data
     */
    private final WorkWebservice webservice;

    /**
     * Creates a @{RemoteXMLToWorksDataSource} and uses the given API URL to access the remote work data
     * @param apiUrl the given API URL of the remote XML-encoded work data
     */
    public RemoteXMLToWorksDataSource(URL apiUrl) {
        webservice = new Retrofit.Builder()
                .baseUrl(apiUrl.toString()).addConverterFactory(SimpleXmlConverterFactory.create()).build().create(WorkWebservice.class);
    }

    /**
     * Retrieves the remote XML-encoded works and returns them as a list of works
     * @return the remote work data as a list of works
     * @throws IOException if the data could not be retrieved due to a network error or because the API response was invalid
     */
    @Override
    public List<Work> read() throws IOException{
        List<Work> result;
        Response<WorkList> response = webservice.getWorks().execute();
        if (response.isSuccessful()) {
            if(response.body() != null) {
                result = response.body().getWorks();
            } else {
                throw new IOException("Response body is empty or corrupted!");
            }
        } else {
            throw new IOException("Response from API failed! Response code was " + response.code() + " and the error message is\n" + response.errorBody().string());
        }

        return result;
    }
}
