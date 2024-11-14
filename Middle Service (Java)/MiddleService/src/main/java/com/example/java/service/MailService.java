package com.example.java.service;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.communication.email.models.EmailSendStatus;
import com.azure.core.util.polling.LongRunningOperationStatus;
import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.SyncPoller;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Duration;

@Service
public class MailService {

    /**
     * Time for thread to sleep until it verifies again the status id email client.
     */
    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:StaticVariableName"})
    private final Duration POLLER_WAIT_TIME = Duration.ofSeconds(3);
    /**
     * Logger used to log events in the servers console.
     */
    private final Log logger = LogFactory.getLog(this.getClass());
    /**
     * This matches variables from templates with ones from given context.
     */
    private final TemplateEngine templateEngine;
    /**
     * Responsible for sending messages.
     */
    private final EmailClient emailClient;
    /**
     * Name of the file which contains the template for email confirmation.
     */
    @Value("${template.emailConfirmation}")
    private String emailConfirmationTemplate;
    /**
     * Name of the file which contains the template for reset password.
     */
    @Value("${template.resetPassword}")
    private String resetPasswordTemplate;
    /**
     * Name of the file which contains the template for reset password confirmation.
     */
    @Value("${template.resetPasswordConfirmation}")
    private String resetPasswordConfirmation;
    /**
     * Domain of email sender.
     */
    @Value("${azure.mail-service.email-sender}")
    private String emailSender;


    /**
     * Constructor.
     *
     * @param templateEngineInstance - Instance of Template Engine.
     * @param emailClientInstance    - Instance of Email Client.
     */
    @Autowired
    public MailService(final TemplateEngine templateEngineInstance, final EmailClient emailClientInstance) {
        this.templateEngine = templateEngineInstance;
        this.emailClient = emailClientInstance;
    }

    /**
     * Sends email.
     *
     * @param emailReceiver - Receiver of the email.
     * @param mailSubject   - Subject of the email.
     * @param mailContent   - Content of the email.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    public void sendMail(final String emailReceiver, final String mailSubject, final String mailContent) {
        EmailMessage message = new EmailMessage()
                .setSenderAddress(emailSender)
                .setToRecipients(emailReceiver)
                .setSubject(mailSubject)
                .setBodyHtml(mailContent);

        SyncPoller<EmailSendResult, EmailSendResult> poller = emailClient.beginSend(message, null);

        PollResponse<EmailSendResult> pollResponse = null;

        Duration timeElapsed = Duration.ofSeconds(0);

        while (pollResponse == null
                || pollResponse.getStatus() == LongRunningOperationStatus.NOT_STARTED
                || pollResponse.getStatus() == LongRunningOperationStatus.IN_PROGRESS) {
            pollResponse = poller.poll();

            logger.info("Email send poller status: " + (pollResponse != null ? pollResponse.getStatus() : "null"));

            try {
                Thread.sleep(POLLER_WAIT_TIME.toMillis());
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
            timeElapsed = timeElapsed.plus(POLLER_WAIT_TIME);

            if (timeElapsed.compareTo(POLLER_WAIT_TIME.multipliedBy(18)) >= 0) {
                throw new RuntimeException("Polling timed out.");
            }
        }
        if (poller.getFinalResult().getStatus() == EmailSendStatus.SUCCEEDED) {
            logger.info("Successfully sent the email (operation id: " + poller.getFinalResult().getId() + ")");
        } else {
            throw new RuntimeException(poller.getFinalResult().getError().getMessage());
        }
    }

    /**
     * Constructs the message for account confirmation email.
     *
     * @param username        - Username of the user that created a new account.
     * @param email           - Email of the user.
     * @param confirmEmailUrl - URL for email confirmation.
     */
    public void sendConfirmationEmail(final String username,
                                      final String email,
                                      final String confirmEmailUrl) {
        String mailSubject = "Account created successfully";
        Context context = new Context();
        context.setVariable("title", "Account Created Successfully");
        context.setVariable("username", username);
        context.setVariable("confirmEmailUrl", confirmEmailUrl);

        String htmlContent = templateEngine.process(emailConfirmationTemplate, context);
        sendMail(email, mailSubject, htmlContent);
    }

    /**
     * Constructs the message for reset password email.
     *
     * @param username         - Username of the user that requested a password reset.
     * @param email            - Email of the user.
     * @param resetPasswordUrl - URL for reset password.
     */
    public void sendResetPasswordMail(final String username,
                                      final String email,
                                      final String resetPasswordUrl) {
        String mailSubject = "Reset Password";
        Context context = new Context();
        context.setVariable("title", "Reset Password");
        context.setVariable("username", username);
        context.setVariable("resetPasswordUrl", resetPasswordUrl);
        String htmlContent = templateEngine.process(resetPasswordTemplate, context);
        sendMail(email, mailSubject, htmlContent);
    }

    /**
     * Constructs the message for reset password confirmation email.
     *
     * @param username - Username of the user that requested a password reset.
     * @param email    - Email of the user.
     */
    public void sendPasswordResetConfirmationEmail(final String username, final String email) {
        String mailSubject = "Password Reset Successful";
        Context context = new Context();
        context.setVariable("title", "Password Reset Successful");
        context.setVariable("username", username);
        String htmlContent = templateEngine.process(resetPasswordConfirmation, context);
        sendMail(email, mailSubject, htmlContent);
    }

}
