package io.github.easyretrofit.adapter.simplebody;

import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public class GithubService {
    public static final String API_URL = "https://api.github.com";

    public static class Contributor {
        public final String login;
        public final int contributions;

        public Contributor(String login, int contributions) {
            this.login = login;
            this.contributions = contributions;
        }
    }

    public interface GitHubApi {
        @GET("/repos/{owner}/{repo}/contributors")
        List<Contributor> contributors(@Path("owner") String owner, @Path("repo") String repo);
    }

}
