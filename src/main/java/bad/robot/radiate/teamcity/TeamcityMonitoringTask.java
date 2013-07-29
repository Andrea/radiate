package bad.robot.radiate.teamcity;

import bad.robot.http.CommonHttpClient;
import bad.robot.radiate.Environment;
import bad.robot.radiate.Status;
import bad.robot.radiate.monitor.MonitoringTask;
import bad.robot.radiate.ui.Ui;
import com.googlecode.totallylazy.Callable1;

import static bad.robot.http.HttpClients.anApacheClient;
import static bad.robot.radiate.teamcity.StatusAggregator.statusAggregator;
import static com.googlecode.totallylazy.Sequences.sequence;

public class TeamcityMonitoringTask implements MonitoringTask {

    private final Ui ui;

    public TeamcityMonitoringTask(Ui ui) {
        this.ui = ui;
    }

    @Override
    public Status call() throws Exception {
        String host = Environment.getEnvironmentVariable("teamcity.host");

        CommonHttpClient http = anApacheClient();
        TeamCity teamcity = new TeamCity(new Server(host), http, new JsonProjectsUnmarshaller(), new JsonProjectUnmarshaller(), new JsonBuildUnmarshaller());

        Iterable<Project> projects = teamcity.retrieveProjects();
        Iterable<BuildType> buildTypes = teamcity.retrieveBuildTypes(projects);
        Iterable<Status> statuses = sequence(buildTypes).map(toStatuses(teamcity));
        Status status = statusAggregator(statuses).getStatus();
        ui.update(status);
        return status;
    }

    private static Callable1<BuildType, Status> toStatuses(final TeamCity teamcity) {
        return new Callable1<BuildType, Status>() {
            @Override
            public Status call(BuildType buildType) throws Exception {
                Build build = teamcity.retrieveLatestBuild(buildType);
                System.out.printf("%s: #%s (id:%s) - %s (%s)%n", build.getBuildType().getName(), build.getNumber(), build.getId(), build.getStatus(), build.getStatusText());
                return build.getStatus();
            }
        };
    }

}
