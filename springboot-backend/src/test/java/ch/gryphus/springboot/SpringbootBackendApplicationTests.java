package ch.gryphus.springboot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SpringbootBackendApplicationTests {

    @Autowired
    private SpringbootBackendApplication springbootBackendApplication;

    @Test
    void contextLoads() {
        assertNotNull(springbootBackendApplication);
    }

    @Test
    void testMain() {
        assertThatCode(() -> SpringbootBackendApplication.main(new String[]{})).doesNotThrowAnyException();
    }

}
