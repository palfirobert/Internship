package com.example.java.TestUtils;

import java.time.Duration;

public final class TestConstants {
    /**
     * Good page size for requests.
     */
    public static final int GOOD_PAGE_SIZE = 20;
    /**
     * Failure case page size for requests.
     */
    public static final int BAD_PAGE_SIZE = -20;
    /**
     * True python port.
     */
    public static final int PYTHON_PORT = 8000;
    /**
     * Mocked python port.
     */
    public static final int MOCKED_PYTHON_PORT = 8069;
    /**
     * Good request status.
     */
    public static final int GOOD_REQUEST_STATUS = 200;
    /**
     * Mocked tenant id.
     */
    public static final String TENANT_ID_MOCK = "1aa9d5e8-a182-414b-be97-1791fd89fc27";
    /**
     * Mocked battery id.
     */
    public static final String BATTERY_ID_MOCK = "test_battery_id";
    /**
     * Mocked battery type.
     */
    public static final String BATTERY_TYPE_MOCK = "test_type";
    /**
     * Plot type mock.
     */
    public static final String PLOT_TYPE_MOCK = "SOHC_MIN_CELL_EVOLUTION_AGAINST_TIME";
    /**
     * Mocked response message ready to be written as json.
     */
    public static final String RESPONSE_MESSAGE_MOCK;

    /**
     * Response message for test.
     */
    static {
        RESPONSE_MESSAGE_MOCK = "{\"message\":\"Data received successfully\",\"locations\":[\"/testImage\"]}";
    }

    /**
     * Test image url.
     */
    public static final String MOCKED_IMAGE_URL = "/testImage";

    /**
     * Time for thread to sleep until it verifies again the status id email client.
     */
    public static final Duration POLLER_WAIT_TIME_NONE_MOCK = Duration.ofMillis(1);

    /**
     * Receiver of the email wrong format.
     */
    public static final String EMAIL_RECEIVER_WRONG_INVALID_FORMAT_MOCK = "email";

    /**
     * Receiver of the email mock.
     */
    public static final String EMAIL_RECEIVER_MOCK = "fa@bosch.com";

    /**
     * Subject of the email mock.
     */
    public static final String MAIL_SUBJECT_MOCK = "Subject";

    /**
     * Content of the email mock.
     */
    public static final String MAIL_CONTENT_MOCK = "Content";

    /**
     * Username of the user mock.
     */
    public static final String USERNAME_MOCK = "user";

    /**
     * URL to be accessed by user in email mock.
     */
    public static final String EMAIL_URL_MOCK = "confirmEmailUrl";

    /**
     * Time left to access URL mock.
     */
    public static final String EXPIRATION_TIME_MOCK = "time";

    /**
     * Name of the file which contains an existing email template.
     */
    public static final String TEMPLATE_GOOD_MOCK = "resetPassword";

    /**
     * Name of an existing file of email template.
     */
    public static final String TEMPLATE_BAD_MOCK = "bad";

    /**
     * Token value for test.
     */
    public static final String TOKEN = "token_test";

    /**
     * Username value for test.
     */
    public static final String EMAIL = "email_test";
    /**
     * Username value for test.
     */
    public static final String EMAIL_BOSCH = "email_test@bosch.com";
    /**
     * Password value for test.
     */
    public static final String PASSWORD = "password_test";
    /**
     * Password value for test.
     */
    public static final String NEW_PASSWORD = "Pa2!aaa";
    /**
     * Name value for test.
     */
    public static final String NAME = "name_test";

    /**
     * Path value for test.
     */
    public static final String PATH = "/path";

    /**
     * Token index id for test.
     */
    public static final int TOKEN_INDEX_FOR_ID = 7;


    private TestConstants() {
    }
}
