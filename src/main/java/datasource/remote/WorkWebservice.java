package datasource.remote;

import domain.WorkList;
import retrofit2.Call;
import retrofit2.http.GET;

interface WorkWebservice {

    @GET("v1/works.xml")
    Call<WorkList> getWorks();
}
