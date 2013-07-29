package bad.robot.radiate.teamcity

import bad.robot.http.{Headers, HttpClient, HttpResponse}
import bad.robot.radiate.Unmarshaller
import bad.robot.http.HeaderList._
import bad.robot.http.HeaderPair.header
import bad.robot.radiate.teamcity.BuildLocatorBuilder.latest
import bad.robot.radiate.teamcity.BuildLocatorBuilder.running
import bad.robot.radiate.teamcity.TeamCityEndpoint.projectsEndpoint

import scala.collection.JavaConversions._

case class TeamCity(server: Server, http: HttpClient, projects: Unmarshaller[HttpResponse, java.lang.Iterable[Project]], project: Unmarshaller[HttpResponse, Project], build: Unmarshaller[HttpResponse, Build]) {

  private final val accept: Headers = headers(header("Accept", "application/json"))

  def retrieveProjects: java.lang.Iterable[Project] = {
    val url = server.urlFor(projectsEndpoint)
    val response = http.get(url, accept)
    if (response.ok)
      return projects.unmarshall(response)
    throw new UnexpectedResponse(url, response)
  }

  def retrieveBuildTypes(projects: java.lang.Iterable[Project]): java.lang.Iterable[BuildType] = {
    val expanded = projects.map(
      (project) => {
        val url = server.urlFor(project)
        val response = http.get(url, accept)
        if (response.ok)
          Right(TeamCity.this.project.unmarshall(response))
        else
          Left(new UnexpectedResponse(url, response))
      })

    val exceptions = expanded.collect{ case Left(exception) => exception }
    if (!exceptions.isEmpty)
      throw exceptions.head

    asJavaIterable(expanded.collect{ case Right(p) => p }.flatMap{ p => p })
  }

  def retrieveLatestBuild(buildType: BuildType): Build = {
    val url = server.urlFor(running(buildType))
    val response = http.get(url, accept)
    if (response.ok)
      return build.unmarshall(response)
    if (response.getStatusCode == 404)
      return retrieveBuild(latest(buildType))
    throw new UnexpectedResponse(url, response)
  }

  private def retrieveBuild(locator: BuildLocatorBuilder): Build = {
    val url = server.urlFor(locator)
    val response = http.get(url, accept)
    if (response.ok)
      return build.unmarshall(response)
    throw new UnexpectedResponse(url, response)
  }

}

