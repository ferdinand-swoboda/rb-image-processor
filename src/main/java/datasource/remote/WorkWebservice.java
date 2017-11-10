package datasource.remote;

import domain.Work;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface WorkWebservice {

    @GET()
    Call<List<Work>> getWorks();
}
