package bad.robot.radiate.teamcity;

import bad.robot.http.CommonHttpClient;
import bad.robot.radiate.Status;
import bad.robot.radiate.monitor.MonitoringTask;
import bad.robot.radiate.ui.Ui;
import com.googlecode.totallylazy.Callable1;

import static bad.robot.http.HttpClients.anApacheClient;
import static bad.robot.radiate.Status.Unknown;
import static bad.robot.radiate.teamcity.StatusAggregator.statusAggregator;
import static com.googlecode.totallylazy.Sequences.sequence;

public class TeamcityMonitoringTask implements MonitoringTask {

    private final Ui ui;
    private final CommonHttpClient http = anApacheClient();
    private final Server server;

    public TeamcityMonitoringTask(Ui ui, Server server) {
        this.ui = ui;
        this.server = server;
    }

    @Override
    public Status call() throws Exception {
        try {
            TeamCity teamcity = new TeamCity(server, http, new JsonProjectsUnmarshaller(), new JsonProjectUnmarshaller(), new JsonBuildUnmarshaller());
            Iterable<Project> projects = teamcity.retrieveProjects();
            Iterable<BuildType> buildTypes = teamcity.retrieveBuildTypes(projects);
            Iterable<Status> statuses = sequence(buildTypes).mapConcurrently(toStatuses(teamcity));
            Status status = statusAggregator(statuses).getStatus();
            ui.update(status);
            return status;
        } catch (Exception e) {
            ui.update(e);
            e.printStackTrace(System.err);
            return Unknown;
        }
    }

    private static Callable1<BuildType, Status> toStatuses(final TeamCity teamcity) {
        return new Callable1<BuildType, Status>() {
            @Override
            public Status call(BuildType buildType) throws Exception {
                Build build = teamcity.retrieveLatestBuild(buildType);
                System.out.printf("%s: #%s (id:%s) - %s (%s) %s %s%n", build.getBuildType().getName(), build.getNumber(), build.getId(), build.getStatus(), build.getStatusText(), build.getBuildType().getProjectName(), Thread.currentThread());
                return build.getStatus();
            }
        };
    }

}
