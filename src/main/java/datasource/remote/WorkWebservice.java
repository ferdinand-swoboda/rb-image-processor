package datasource.remote;

import domain.WorkList;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Represents a webservice proxy that handles the transfer of work items from a server endpoint.
 */
interface WorkWebservice {

    /**
     * Retrieves a list of work items from the server and returns it as a @code{WorkList}
     * @return the custom list of works
     */
    @GET("v1/works.xml")
    Call<WorkList> getWorks();
}
