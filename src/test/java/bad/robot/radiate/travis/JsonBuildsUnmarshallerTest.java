package bad.robot.radiate.travis;

import bad.robot.http.HttpResponse;
import bad.robot.http.StringHttpResponse;
import bad.robot.radiate.Hypermedia;
import bad.robot.radiate.JsonResponse;
import bad.robot.radiate.Unmarshaller;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static bad.robot.http.HeaderList.headers;
import static bad.robot.http.HeaderPair.header;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class JsonBuildsUnmarshallerTest {

    @Test
    public void unmarshallsBuilds() {
        Unmarshaller<HttpResponse, Iterable<Build>> unmarshaller = new Unmarshaller<HttpResponse, Iterable<Build>>() {
            @Override
            public Iterable<Build> unmarshall(HttpResponse response) {
                Type type = new TypeToken<Collection<Build>>(){}.getType();
                Collection<Build> builds = new Gson().fromJson(new JsonResponse(response).body(), type);
                return builds;
            }
        };
        Iterable<Build> builds = unmarshaller.unmarshall(new StringHttpResponse(200, "OK", buildsJson, headers(header("content-type", "application/json")), "http://example.com"));
        for (Build build : builds) {
            System.out.println(build);
        }
    }

    public class Builds implements Iterable<Build> {

        private final Collection<Build> builds = new ArrayList<>();

        @Override
        public Iterator<Build> iterator() {
            return builds.iterator();
        }
    }

    public class Build implements Hypermedia {
        private final String id;
        private final String slug;
        private final String description;
        @SerializedName("last_build_id")
        private final String lastBuildId;
        @SerializedName("last_build_status")
        private final String lastBuildStatus;
        @SerializedName("last_build_result")
        private final String lastBuildResult;
        @SerializedName("last_build_started_at")
        private final String lastBuildStartedAt;
        @SerializedName("last_build_finished_at")
        private final String lastBuildFinishedAt;

        public Build(String id, String slug, String description, String lastBuildId, String lastBuildStatus, String lastBuildResult, String lastBuildStartedAt, String lastBuildFinishedAt) {
            this.id = id;
            this.slug = slug;
            this.description = description;
            this.lastBuildId = lastBuildId;
            this.lastBuildStatus = lastBuildStatus;
            this.lastBuildResult = lastBuildResult;
            this.lastBuildStartedAt = lastBuildStartedAt;
            this.lastBuildFinishedAt = lastBuildFinishedAt;
        }

        @Override
        public String getHref() {
            return slug;
        }

        @Override
        public boolean equals(Object that) {
            return reflectionEquals(this, that);
        }

        @Override
        public int hashCode() {
            return reflectionHashCode(this);
        }

        @Override
        public String toString() {
            return reflectionToString(this, SHORT_PREFIX_STYLE);
        }

    }

    private final static String buildsJson = "[\n" +
            "  {\n" +
            "    \"id\": 1135341,\n" +
            "    \"slug\": \"tobyweston/radiate\",\n" +
            "    \"description\": \"Build monitor for TeamCity\",\n" +
            "    \"last_build_id\": 16452650,\n" +
            "    \"last_build_number\": \"158\",\n" +
            "    \"last_build_status\": 0,\n" +
            "    \"last_build_result\": 0,\n" +
            "    \"last_build_duration\": 173,\n" +
            "    \"last_build_language\": null,\n" +
            "    \"last_build_started_at\": \"2014-01-06T08:35:54Z\",\n" +
            "    \"last_build_finished_at\": \"2014-01-06T08:37:30Z\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 412045,\n" +
            "    \"slug\": \"tobyweston/simple-http\",\n" +
            "    \"description\": \"A simple Java HTTP client\",\n" +
            "    \"last_build_id\": 15041782,\n" +
            "    \"last_build_number\": \"17\",\n" +
            "    \"last_build_status\": null,\n" +
            "    \"last_build_result\": null,\n" +
            "    \"last_build_duration\": 294,\n" +
            "    \"last_build_language\": null,\n" +
            "    \"last_build_started_at\": \"2013-12-06T14:57:03Z\",\n" +
            "    \"last_build_finished_at\": \"2013-12-06T14:58:46Z\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 412044,\n" +
            "    \"slug\": \"tobyweston/simple-excel\",\n" +
            "    \"description\": \"Generate excel sheets in Java\",\n" +
            "    \"last_build_id\": 11687116,\n" +
            "    \"last_build_number\": \"22\",\n" +
            "    \"last_build_status\": 0,\n" +
            "    \"last_build_result\": 0,\n" +
            "    \"last_build_duration\": 424,\n" +
            "    \"last_build_language\": null,\n" +
            "    \"last_build_started_at\": \"2013-09-23T12:42:47Z\",\n" +
            "    \"last_build_finished_at\": \"2013-09-23T12:45:45Z\"\n" +
            "  }\n" +
            "]";

    private class BuildUnmarshaller implements Unmarshaller<HttpResponse, Build> {
        @Override
        public Build unmarshall(HttpResponse response) {
            return new Gson().fromJson(new JsonResponse(response).body(), Build.class);
        }
    }
}
