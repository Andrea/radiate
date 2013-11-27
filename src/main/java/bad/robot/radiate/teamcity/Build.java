package bad.robot.radiate.teamcity;

import bad.robot.radiate.Activity;
import bad.robot.radiate.Hypermedia;
import bad.robot.radiate.Monitorable;
import bad.robot.radiate.Status;
import com.googlecode.totallylazy.Callable1;

import static bad.robot.radiate.Activity.Idle;
import static bad.robot.radiate.Status.*;

public class Build extends TeamCityObject implements Hypermedia, Monitorable {

    private final String id;
    private final String number;
    private final String href;
    private final String status;
    private final String statusText;
    private final String startDate;
    private final String finishDate;
    private final BuildType buildType;

    public Build(String id, String number, String href, String success, String statusText, String startDate, String finishDate, BuildType buildType) {
        this.id = id;
        this.number = number;
        this.href = href;
        this.status = success;
        this.statusText = statusText;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.buildType = buildType;
    }

    public String getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public Status getStatus() {
        if (status.equalsIgnoreCase("SUCCESS"))
            return Ok;
        if (status.equalsIgnoreCase("FAILURE"))
            return Broken;
        return Unknown;
    }

    @Override
    public Activity getActivity() {
        return Idle;
    }

    public String getStatusString() {
        return status;
    }

    public String getStatusText() {
        return statusText;
    }

    public BuildType getBuildType() {
        return buildType;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    @Override
    public String getHref() {
        return href;
    }

    public static class Functions {
        public static Callable1<Build, Activity> toActivity() {
            return new Callable1<Build, Activity>() {
                @Override
                public Activity call(Build build) throws Exception {
                    return build.getActivity();
                }
            };
        }

        public static Callable1<Build, Status> toStatus() {
            return new Callable1<Build, Status>() {
                @Override
                public Status call(Build build) throws Exception {
                    return build.getStatus();
                }
            };
        }
    }
}
