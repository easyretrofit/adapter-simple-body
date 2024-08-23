package io.github.easyretrofit.adapter.simplebody;

import io.github.easyretrofit.adapter.simplebody.GithubService.Contributor;
import io.github.easyretrofit.adapter.simplebody.GithubService.GitHubApi;
import org.junit.Test;
import retrofit2.Retrofit;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class SimpleBodyTest {


    @Test
    public void simpleBodySuccessTest() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GithubService.API_URL)
                .addCallAdapterFactory(SimpleBodyCallAdapterFactory.create())
                .build();

        // Create a MockRetrofit object with a NetworkBehavior which manages the fake behavior of calls.
        NetworkBehavior behavior = NetworkBehavior.create();
        MockRetrofit mockRetrofit =
                new MockRetrofit.Builder(retrofit).networkBehavior(behavior).build();

        BehaviorDelegate<GitHubApi> delegate = mockRetrofit.create(GitHubApi.class);
        MockGitHub gitHub = new MockGitHub(delegate);
        List<Contributor> contributors = gitHub.contributors("square", "retrofit");

        assertEquals(contributors.size(), 3);
    }

    static final class MockGitHub implements GitHubApi {
        private final BehaviorDelegate<GitHubApi> delegate;
        private final Map<String, Map<String, List<Contributor>>> ownerRepoContributors;

        MockGitHub(BehaviorDelegate<GitHubApi> delegate) {
            this.delegate = delegate;
            ownerRepoContributors = new LinkedHashMap<>();

            // Seed some mock data.
            addContributor("square", "retrofit", "John Doe", 12);
            addContributor("square", "retrofit", "Bob Smith", 2);
            addContributor("square", "retrofit", "Big Bird", 40);
            addContributor("square", "picasso", "Proposition Joe", 39);
            addContributor("square", "picasso", "Keiser Soze", 152);
        }

        @Override
        public List<Contributor> contributors(String owner, String repo) {
            List<Contributor> response = Collections.emptyList();
            Map<String, List<Contributor>> repoContributors = ownerRepoContributors.get(owner);
            if (repoContributors != null) {
                List<Contributor> contributors = repoContributors.get(repo);
                if (contributors != null) {
                    response = contributors;
                }
            }
            return delegate.returningResponse(response).contributors(owner, repo);
        }

        void addContributor(String owner, String repo, String name, int contributions) {
            Map<String, List<Contributor>> repoContributors = ownerRepoContributors.get(owner);
            if (repoContributors == null) {
                repoContributors = new LinkedHashMap<>();
                ownerRepoContributors.put(owner, repoContributors);
            }
            List<Contributor> contributors = repoContributors.get(repo);
            if (contributors == null) {
                contributors = new ArrayList<>();
                repoContributors.put(repo, contributors);
            }
            contributors.add(new Contributor(name, contributions));
        }
    }

    private static void printContributors(GitHubApi gitHub, String owner, String repo) throws IOException {
        System.out.println(String.format("== Contributors for %s/%s ==", owner, repo));
        List<Contributor> contributors = gitHub.contributors(owner, repo);
        for (Contributor contributor : contributors) {
            System.out.println(contributor.login + " (" + contributor.contributions + ")");
        }
        System.out.println();
    }

}
