package bad.robot.radiate.teamcity;

import bad.robot.radiate.monitor.MonitoringTask;
import bad.robot.radiate.monitor.MonitoringTasksFactory;
import bad.robot.radiate.monitor.ThreadSafeObservable;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Sequence;

import java.util.List;

import static com.googlecode.totallylazy.Sequences.sequence;

public class AllProjectsOneTaskPerProject extends ThreadSafeObservable implements MonitoringTasksFactory {

    @Override
    public List<MonitoringTask> create() {
        TeamCity teamcity = new BootstrapTeamCity();
        TeamCityConfiguration configuration = YmlConfiguration.loadOrDefault(teamcity, this);
        Sequence<Project> projects = sequence(configuration.filter(teamcity.retrieveProjects()));
        return projects.map(toTasks(configuration)).toList();
    }

    private Callable1<Project, MonitoringTask> toTasks(final TeamCityConfiguration configuration) {
        return new Callable1<Project, MonitoringTask>() {
            @Override
            public MonitoringTask call(Project project) throws Exception {
                return new SingleProjectMonitor(project, configuration);
            }
        };
    }

    @Override
    public String toString() {
        return "monitoring multiple projects";
    }
}
