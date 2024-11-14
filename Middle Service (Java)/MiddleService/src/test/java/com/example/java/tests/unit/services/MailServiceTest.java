package com.example.java.tests.unit.services;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.communication.email.models.EmailSendStatus;
import com.azure.core.models.ResponseError;
import com.azure.core.util.polling.LongRunningOperationStatus;
import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.SyncPoller;
import com.example.java.service.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static com.example.java.TestUtils.TestConstants.EMAIL_RECEIVER_MOCK;
import static com.example.java.TestUtils.TestConstants.EMAIL_RECEIVER_WRONG_INVALID_FORMAT_MOCK;
import static com.example.java.TestUtils.TestConstants.EMAIL_URL_MOCK;
import static com.example.java.TestUtils.TestConstants.EXPIRATION_TIME_MOCK;
import static com.example.java.TestUtils.TestConstants.MAIL_CONTENT_MOCK;
import static com.example.java.TestUtils.TestConstants.MAIL_SUBJECT_MOCK;
import static com.example.java.TestUtils.TestConstants.POLLER_WAIT_TIME_NONE_MOCK;
import static com.example.java.TestUtils.TestConstants.TEMPLATE_BAD_MOCK;
import static com.example.java.TestUtils.TestConstants.TEMPLATE_GOOD_MOCK;
import static com.example.java.TestUtils.TestConstants.USERNAME_MOCK;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Profile("test")
@ActiveProfiles("test")
public class MailServiceTest {

    /**
     * Object to be tested.
     */
    @InjectMocks
    private MailService mailService;

    /**
     * Mock matching variables from templates with ones from given context.
     */
    @Mock
    private TemplateEngine templateEngineMock;

    /**
     * Mock sending messages.
     */
    @Mock
    private EmailClient emailClientMock;

    /**
     * Used to mock control over message sending.
     */
    @Mock
    private SyncPoller<EmailSendResult, EmailSendResult> pollerMock;

    /**
     * Used to get mocked responses from Email Client.
     */
    @Mock
    private PollResponse<EmailSendResult> pollResponseMock;

    /**
     * Used to mock final result Email Client.
     */
    @Mock
    private EmailSendResult finalResultMock;

    /**
     * Instantiate poller waiting time.
     */
    @BeforeEach
    public void resetPolarWaitTime() {
        ReflectionTestUtils.setField(mailService, "POLLER_WAIT_TIME", POLLER_WAIT_TIME_NONE_MOCK);
    }

    /**
     * Case when the email of the receiver has wrong format.
     */
    @SuppressWarnings("checkstyle:LineLength")
    @Test
    public void sendMailWrongEmailReceiverInvalidFormat() {
        when(emailClientMock.beginSend(any(EmailMessage.class), any())).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class,
                () -> mailService.sendMail(" ",
                        MAIL_SUBJECT_MOCK,
                        MAIL_CONTENT_MOCK));

        verify(emailClientMock, times(1)).beginSend(any(), any());
        verifyNoInteractions(pollerMock);
    }

    /**
     * Case when poller response is null and time allocated to send message is exceeded.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void sendEmailTimeElapsedPollResponseNull() {
        when(emailClientMock.beginSend(any(EmailMessage.class), any())).thenReturn(pollerMock);
        when(pollerMock.poll()).thenReturn(null);
        assertThrows(RuntimeException.class,
                () -> mailService.sendMail(EMAIL_RECEIVER_MOCK, MAIL_SUBJECT_MOCK, MAIL_CONTENT_MOCK));

        verify(emailClientMock, times(1)).beginSend(any(), any());
        verify(pollerMock, times(18)).poll();
    }

    /**
     * Case when poller response is NOT_STARTED (sending did not begin) and time allocated to send message is exceeded.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void sendEmailTimeElapsedPollStatusNotStarted() {
        when(emailClientMock.beginSend(any(EmailMessage.class), any())).thenReturn(pollerMock);

        when(pollResponseMock.getStatus()).thenReturn(LongRunningOperationStatus.NOT_STARTED);
        when(pollerMock.poll()).thenReturn(pollResponseMock);

        assertThrows(RuntimeException.class,
                () -> mailService.sendMail(EMAIL_RECEIVER_MOCK, MAIL_SUBJECT_MOCK, MAIL_CONTENT_MOCK));

        verify(emailClientMock, times(1)).beginSend(any(), any());
        verify(pollerMock, times(18)).poll();

    }

    /**
     * Case when poller response is IN_PROGRESS (sending is in progress) and time allocated to send message is exceeded.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void sendEmailTimeElapsedPollStatusInProgress() {
        when(emailClientMock.beginSend(any(EmailMessage.class), any())).thenReturn(pollerMock);

        when(pollResponseMock.getStatus()).thenReturn(LongRunningOperationStatus.IN_PROGRESS);
        when(pollerMock.poll()).thenReturn(pollResponseMock);

        assertThrows(RuntimeException.class,
                () -> mailService.sendMail(EMAIL_RECEIVER_MOCK, MAIL_SUBJECT_MOCK, MAIL_CONTENT_MOCK));

        verify(emailClientMock, times(1)).beginSend(any(), any());
        verify(pollerMock, times(18)).poll();

    }

    /**
     * Case email is sent successfully.
     *
     * @throws Exception - no.
     */
    @Test
    public void sendEmailSuccess() throws Exception {

        when(emailClientMock.beginSend(any(EmailMessage.class), any())).thenReturn(pollerMock);
        when(pollerMock.poll()).thenReturn(pollResponseMock);
        when(pollResponseMock.getStatus()).thenReturn(LongRunningOperationStatus.SUCCESSFULLY_COMPLETED);

        when(finalResultMock.getStatus()).thenReturn(EmailSendStatus.SUCCEEDED);
        when(pollerMock.getFinalResult()).thenReturn(finalResultMock);

        mailService.sendMail(EMAIL_RECEIVER_MOCK, MAIL_SUBJECT_MOCK, MAIL_CONTENT_MOCK);

        verify(emailClientMock, times(1)).beginSend(any(), any());
        verify(pollerMock, times(1)).poll();

    }

    /**
     * Case email sending fails.
     */
    @Test
    public void sendEmailFail() {
        when(emailClientMock.beginSend(any(EmailMessage.class), any())).thenReturn(pollerMock);
        when(pollerMock.poll()).thenReturn(pollResponseMock);
        when(pollResponseMock.getStatus()).thenReturn(LongRunningOperationStatus.FAILED);

        when(finalResultMock.getStatus()).thenReturn(EmailSendStatus.FAILED);
        when(pollerMock.getFinalResult()).thenReturn(finalResultMock);

        ResponseError errorMock = mock(ResponseError.class);

        when(finalResultMock.getError()).thenReturn(errorMock);

        when(errorMock.getMessage()).thenReturn("failed");

        assertThrowsExactly(RuntimeException.class,
                () -> mailService.sendMail(EMAIL_RECEIVER_MOCK, MAIL_SUBJECT_MOCK, MAIL_CONTENT_MOCK), "failed");

        verify(emailClientMock, times(1)).beginSend(any(), any());
        verify(pollerMock, times(1)).poll();

    }

    /**
     * Case when constructing confirmation email fails - no template.
     */
    @Test
    public void sendConfirmationEmailWrongTemplate() {
        ReflectionTestUtils.setField(mailService, "emailConfirmationTemplate", TEMPLATE_BAD_MOCK);

        when(templateEngineMock.process(anyString(), any(Context.class))).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class,
                () -> mailService.sendConfirmationEmail(USERNAME_MOCK,
                        EMAIL_RECEIVER_MOCK,
                        EMAIL_URL_MOCK));

        verify(templateEngineMock, times(1)).process(anyString(), any());
        verify(emailClientMock, times(0)).beginSend(any(), any());

    }

    /**
     * Case when constructing reset password email fails - no template.
     */
    @Test
    public void sendResetPasswordMailWrongTemplate() {
        ReflectionTestUtils.setField(mailService, "resetPasswordTemplate", TEMPLATE_BAD_MOCK);

        when(templateEngineMock.process(anyString(), any(Context.class))).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class,
                () -> mailService.sendResetPasswordMail(USERNAME_MOCK,
                        EMAIL_RECEIVER_MOCK,
                        EMAIL_URL_MOCK));

        verify(templateEngineMock, times(1)).process(anyString(), any());
        verify(emailClientMock, times(0)).beginSend(any(), any());

    }

    /**
     * Case when constructing reset password confirmation email fails - no template.
     */
    @Test
    public void sendPasswordResetConfirmationEmailWrongTemplate() {
        ReflectionTestUtils.setField(mailService, "resetPasswordConfirmation", TEMPLATE_BAD_MOCK);

        when(templateEngineMock.process(anyString(), any(Context.class))).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class,
                () -> mailService.sendPasswordResetConfirmationEmail(USERNAME_MOCK, EMAIL_RECEIVER_MOCK));

        verify(templateEngineMock, times(1)).process(anyString(), any());
        verify(emailClientMock, times(0)).beginSend(any(), any());

    }

    /**
     * Case when constructing confirmation email succeeds as well as sending the email.
     */
    @Test
    public void sendConfirmationEmailSuccess() throws Exception {
        ReflectionTestUtils.setField(mailService, "emailConfirmationTemplate", TEMPLATE_GOOD_MOCK);
        when(templateEngineMock.process(anyString(), any())).thenReturn(MAIL_CONTENT_MOCK);

        when(emailClientMock.beginSend(any(EmailMessage.class), any())).thenReturn(pollerMock);
        when(pollerMock.poll()).thenReturn(pollResponseMock);
        when(pollResponseMock.getStatus()).thenReturn(LongRunningOperationStatus.SUCCESSFULLY_COMPLETED);

        when(finalResultMock.getStatus()).thenReturn(EmailSendStatus.SUCCEEDED);
        when(pollerMock.getFinalResult()).thenReturn(finalResultMock);

        mailService.sendConfirmationEmail(USERNAME_MOCK, EMAIL_RECEIVER_MOCK, EMAIL_URL_MOCK);

        verify(templateEngineMock, times(1)).process(anyString(), any());
        verify(emailClientMock, times(1)).beginSend(any(), any());
        verify(pollerMock, times(1)).poll();

    }

    /**
     * Case when constructing reset password email succeeds as well as sending the email.
     */
    @Test
    public void sendResetPasswordMailSuccess() throws Exception {
        ReflectionTestUtils.setField(mailService, "resetPasswordTemplate", TEMPLATE_GOOD_MOCK);
        when(templateEngineMock.process(anyString(), any())).thenReturn(MAIL_CONTENT_MOCK);

        when(emailClientMock.beginSend(any(EmailMessage.class), any())).thenReturn(pollerMock);
        when(pollerMock.poll()).thenReturn(pollResponseMock);
        when(pollResponseMock.getStatus()).thenReturn(LongRunningOperationStatus.SUCCESSFULLY_COMPLETED);

        when(finalResultMock.getStatus()).thenReturn(EmailSendStatus.SUCCEEDED);
        when(pollerMock.getFinalResult()).thenReturn(finalResultMock);

        mailService.sendResetPasswordMail(USERNAME_MOCK, EMAIL_RECEIVER_MOCK, EMAIL_URL_MOCK);

        verify(templateEngineMock, times(1)).process(anyString(), any());
        verify(emailClientMock, times(1)).beginSend(any(), any());
        verify(pollerMock, times(1)).poll();

    }

    /**
     * Case when constructing reset password confirmation email succeeds as well as sending the email.
     */
    @Test
    public void sendPasswordResetConfirmationEmailSuccess() throws Exception {
        ReflectionTestUtils.setField(mailService, "resetPasswordConfirmation", TEMPLATE_GOOD_MOCK);
        when(templateEngineMock.process(anyString(), any())).thenReturn(MAIL_CONTENT_MOCK);

        when(emailClientMock.beginSend(any(EmailMessage.class), any())).thenReturn(pollerMock);
        when(pollerMock.poll()).thenReturn(pollResponseMock);
        when(pollResponseMock.getStatus()).thenReturn(LongRunningOperationStatus.SUCCESSFULLY_COMPLETED);

        when(finalResultMock.getStatus()).thenReturn(EmailSendStatus.SUCCEEDED);
        when(pollerMock.getFinalResult()).thenReturn(finalResultMock);

        mailService.sendPasswordResetConfirmationEmail(USERNAME_MOCK, EMAIL_RECEIVER_MOCK);

        verify(templateEngineMock, times(1)).process(anyString(), any());
        verify(emailClientMock, times(1)).beginSend(any(), any());
        verify(pollerMock, times(1)).poll();

    }


}
