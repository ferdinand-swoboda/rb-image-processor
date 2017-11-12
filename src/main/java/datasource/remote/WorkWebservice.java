package datasource.remote;

import domain.Work;
import domain.WorkList;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface WorkWebservice {

    @GET("v1/works.xml")
    Call<WorkList> getWorks();
}
