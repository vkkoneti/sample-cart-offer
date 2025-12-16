package base;
import mocks.UserSegmentMock;
import org.junit.jupiter.api.BeforeEach;
public class BaseTest {

        protected UserSegmentMock segmentMock;

        @BeforeEach
        public void beforeEach() {
            segmentMock = new UserSegmentMock();
            segmentMock.reset();
        }
    }


