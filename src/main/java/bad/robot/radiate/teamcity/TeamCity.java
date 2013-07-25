package bad.robot.radiate.teamcity;

import bad.robot.http.HttpClient;
import bad.robot.http.HttpResponse;
import bad.robot.radiate.Unmarshaller;

import java.net.URL;

import static bad.robot.http.HeaderList.headers;
import static bad.robot.http.HeaderPair.header;
import static bad.robot.radiate.teamcity.TeamCityEndpoint.projectsEndpoint;
import static java.lang.String.*;

class TeamCity {

    private Server server;
    private HttpClient http;
    private Unmarshaller<HttpResponse, Iterable<Project>> unmarshaller;

    public TeamCity(Server server, HttpClient http, Unmarshaller<HttpResponse, Iterable<Project>> unmarshaller) {
        this.server = server;
        this.http = http;
        this.unmarshaller = unmarshaller;
    }

    public Iterable<Project> retrieveProjects() {
        URL url = server.urlFor(projectsEndpoint);
        HttpResponse response = http.get(url, headers(
            header("Accept", "application/json")
        ));
        if (response.ok())
            return unmarshaller.unmarshall(response);
        throw new RuntimeException(format("Unexpected HTTP response from %s (%d, %s)", url, response.getStatusCode(), response.getStatusMessage()));
    }
}
