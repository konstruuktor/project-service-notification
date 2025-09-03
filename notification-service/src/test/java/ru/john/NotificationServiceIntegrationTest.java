package ru.john;

import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import ru.john.service.EmailService;

import javax.mail.internet.MimeMessage;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")

class NotificationServiceIntegrationTest {

    private static GreenMail greenMail;

    @LocalServerPort
    private int port;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    static void startMailServer() {
        greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.start();
    }

    @AfterAll
    static void stopMailServer() {
        greenMail.stop();
    }

    @BeforeEach
    void clearMail() throws FolderException {
        greenMail.purgeEmailFromAllMailboxes();
    }

    @Test
    void testSendEmailDirectly() throws Exception {
        // given
        String to = "test@example.com";

        // when
        emailService.send(to, "Тест", "Привет из NotificationService!");

        // then
        javax.mail.internet.MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertThat(receivedMessages).hasSize(1);
        assertThat(receivedMessages[0].getAllRecipients()[0].toString()).isEqualTo(to);
        assertThat(receivedMessages[0].getSubject()).isEqualTo("Тест");
    }

    @Test
    void testSendEmailViaApi() throws Exception {
        // given
        String url = "http://localhost:" + port + "/api/notify?email=test@example.com&subject=API&message=HelloAPI";

        // when
        ResponseEntity<Void> response = restTemplate.postForEntity(url, null, Void.class);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertThat(receivedMessages).hasSize(1);
        assertThat(receivedMessages[0].getSubject()).isEqualTo("API");
        String body = new String(receivedMessages[0].getInputStream().readAllBytes());
        assertThat(body).contains("HelloAPI");
    }
}