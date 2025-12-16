package mocks;

import org.mockserver.client.MockServerClient;
import utils.TestConfig;

public class UserSegmentMock {

    private final MockServerClient client;

    public UserSegmentMock() {
        this.client = new MockServerClient(TestConfig.MOCKSERVER_HOST, TestConfig.MOCKSERVER_PORT);
    }

    public void reset() {
        client.reset();
    }

    public void mockUserSegment(int userId, String segment) {
        client
                .when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath(TestConfig.USER_SEGMENT_ENDPOINT)
                        .withQueryStringParameter("user_id", String.valueOf(userId)))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"segment\":\"" + segment + "\"}"));
    }
}
