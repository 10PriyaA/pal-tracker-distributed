package io.pivotal.pal.tracker.timesheets;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;
import java.util.concurrent.ConcurrentHashMap;
import java.util.*;


public class ProjectClient {

    private final RestOperations restOperations;
    private final String endpoint;

    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    Map<Long,ProjectInfo> projectInfoMap = new ConcurrentHashMap<>();

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations = restOperations;
        this.endpoint = registrationServerEndpoint;
    }

    @CircuitBreaker(name = "project", fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo projectInfo = restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);
        projectInfoMap.put(projectId,projectInfo);
        return projectInfo;
    }


    public ProjectInfo getProjectFromCache(long projectId, Throwable cause) {
        logger.info("Getting project with id {} from cache", projectId);
        return projectInfoMap.get(projectId);
    }
}
