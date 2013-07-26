package bad.robot.radiate.teamcity;

import bad.robot.http.Headers;
import bad.robot.http.HttpClient;
import bad.robot.http.HttpResponse;
import bad.robot.radiate.Unmarshaller;

import java.net.URL;

import static bad.robot.http.HeaderList.headers;
import static bad.robot.http.HeaderPair.header;
import static bad.robot.radiate.teamcity.TeamCityEndpoint.projectsEndpoint;

class TeamCity {

    private final Headers headers = headers(header("Accept", "application/json"));
    private final Server server;
    private final HttpClient http;
    private final Unmarshaller<HttpResponse, Iterable<Project>> unmarshaller;

    public TeamCity(Server server, HttpClient http, Unmarshaller<HttpResponse, Iterable<Project>> unmarshaller) {
        this.server = server;
        this.http = http;
        this.unmarshaller = unmarshaller;
    }

    public Iterable<Project> retrieveProjects() {
        URL url = server.urlFor(projectsEndpoint);
        HttpResponse response = http.get(url, headers);
        if (response.ok())
            return unmarshaller.unmarshall(response);
        throw new UnexpectedResponse(url, response);
    }

    public void retrieveFullProjectInformation(Iterable<Project> projects) {
        for (Project project : projects) {
            URL url = server.urlFor(project);
            HttpResponse response = http.get(url, headers);
            System.out.println(response.getContent().asString() +"\n\n");
        }
    }

}
