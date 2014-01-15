package bad.robot.radiate;

import bad.robot.http.Header;
import bad.robot.http.HeaderPair;
import bad.robot.http.HttpResponse;
import bad.robot.radiate.teamcity.TeamCityException;
import com.googlecode.totallylazy.Predicate;
import org.apache.commons.lang3.StringUtils;

import static com.googlecode.totallylazy.Sequences.sequence;
import static java.lang.String.format;

public class UnexpectedContentType extends TeamCityException {

    public UnexpectedContentType(HttpResponse response) {
        super(format("Unexpected response content-type '%s' (%s %s) from %s%n%s", sequence(response.getHeaders()).find(contentType()).getOrElse(HeaderPair.header("content-type", "not set")).value(), response.getStatusCode(), response.getStatusMessage(), response.getOriginatingUri(), abbreviated(response)));
    }

    private static String abbreviated(HttpResponse response) {
        String body = response.getContent().asString();
        return StringUtils.abbreviate(body, 400);
    }

    private static Predicate<Header> contentType() {
        return new Predicate<Header>() {
            @Override
            public boolean matches(Header header) {
                return header.name().equalsIgnoreCase("content-type");
            }
        };
    }

}
