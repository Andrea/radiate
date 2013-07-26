package bad.robot.radiate.teamcity;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

class Projects extends TeamCityObject implements Iterable<Project> {

    @SerializedName("project")
    private Collection<Project> project = new ArrayList<Project>();

    @Override
    public Iterator<Project> iterator() {
        return project.iterator();
    }
}
