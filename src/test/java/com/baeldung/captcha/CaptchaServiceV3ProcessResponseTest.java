
// ********RoostGPT********
/*
Test generated by RoostGPT for test unitTestJava using AI Type Open AI and AI Model gpt-4

ROOST_METHOD_HASH=processResponse_51dfb91df8
ROOST_METHOD_SIG_HASH=processResponse_5b70d2c99d

"""
Scenario 1: Test to check if the processResponse method handles the successful reCaptcha validation

Details:
  TestName: processResponseSuccessfulValidation
  Description: This test is meant to check if the processResponse method successfully validates the reCaptcha response, where the GoogleResponse object's isSuccess method returns true, getAction method returns the same action as the input action, and getScore method returns a value equal to or greater than the threshold.
Execution:
  Arrange: Mock the GoogleResponse object to return true for isSuccess, same action as input for getAction, and a score equal to or greater than the threshold for getScore. Also, mock the restTemplate to return this GoogleResponse object when getForObject is called.
  Act: Invoke the processResponse method with the appropriate parameters.
  Assert: Use JUnit assertions to check if the reCaptchaAttemptService's reCaptchaSucceeded method was called once.
Validation:
  The assertion verifies that the processResponse method correctly calls the reCaptchaAttemptService's reCaptchaSucceeded method when the reCaptcha validation is successful. This test is significant as it ensures that the application correctly handles successful reCaptcha validations.

Scenario 2: Test to check if the processResponse method handles the reCaptcha validation failure due to client error

Details:
  TestName: processResponseFailureDueToClientError
  Description: This test is meant to check if the processResponse method correctly handles the reCaptcha validation failure due to a client error, where the GoogleResponse object's isSuccess method returns false and hasClientError method returns true.
Execution:
  Arrange: Mock the GoogleResponse object to return false for isSuccess and true for hasClientError. Also, mock the restTemplate to return this GoogleResponse object when getForObject is called.
  Act: Invoke the processResponse method with the appropriate parameters.
  Assert: Use JUnit assertions to check if a ReCaptchaInvalidException is thrown and if the reCaptchaAttemptService's reCaptchaFailed method was called once.
Validation:
  The assertion verifies that the processResponse method correctly throws a ReCaptchaInvalidException and calls the reCaptchaAttemptService's reCaptchaFailed method when the reCaptcha validation fails due to a client error. This test is significant as it ensures that the application correctly handles reCaptcha validation failures due to client errors.

Scenario 3: Test to check if the processResponse method handles the situation where the registration is unavailable

Details:
  TestName: processResponseRegistrationUnavailable
  Description: This test is meant to check if the processResponse method correctly handles the situation where the registration is unavailable, which is simulated by throwing a RestClientException when the restTemplate's getForObject method is called.
Execution:
  Arrange: Mock the restTemplate to throw a RestClientException when getForObject is called.
  Act: Invoke the processResponse method with the appropriate parameters.
  Assert: Use JUnit assertions to check if a ReCaptchaUnavailableException is thrown.
Validation:
  The assertion verifies that the processResponse method correctly throws a ReCaptchaUnavailableException when the registration is unavailable. This test is significant as it ensures that the application correctly handles situations where the registration is unavailable.
"""
*/

// ********RoostGPT********

package com.baeldung.captcha;

import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.baeldung.web.error.ReCaptchaInvalidException;
import com.baeldung.web.error.ReCaptchaUnavailableException;

public class CaptchaServiceV3ProcessResponseTest {

	@InjectMocks
	private CaptchaServiceV3 captchaServiceV3;

	@Mock
	private RestOperations restTemplate;

	@Mock
	private ReCaptchaAttemptService reCaptchaAttemptService;

	@Mock
	private CaptchaSettings captchaSettings;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	@Tag("valid")
	public void processResponseSuccessfulValidation() {
		GoogleResponse googleResponse = mock(GoogleResponse.class);
		when(googleResponse.isSuccess()).thenReturn(true);
		when(googleResponse.getAction()).thenReturn(CaptchaServiceV3.REGISTER_ACTION);
		when(googleResponse.getScore()).thenReturn(captchaSettings.getThreshold());
		when(restTemplate.getForObject(any(URI.class), eq(GoogleResponse.class))).thenReturn(googleResponse);
		captchaServiceV3.processResponse("response", CaptchaServiceV3.REGISTER_ACTION);
		verify(reCaptchaAttemptService, times(1)).reCaptchaSucceeded(anyString());
	}

	@Test
	@Tag("invalid")
	public void processResponseFailureDueToClientError() {
		GoogleResponse googleResponse = mock(GoogleResponse.class);
		when(googleResponse.isSuccess()).thenReturn(false);
		when(googleResponse.hasClientError()).thenReturn(true);
		when(restTemplate.getForObject(any(URI.class), eq(GoogleResponse.class))).thenReturn(googleResponse);
		assertThrows(ReCaptchaInvalidException.class, () -> {
			captchaServiceV3.processResponse("response", CaptchaServiceV3.REGISTER_ACTION);
		});
		verify(reCaptchaAttemptService, times(1)).reCaptchaFailed(anyString());
	}

	@Test
    @Tag("integration")
    public void processResponseRegistrationUnavailable() {
        when(restTemplate.getForObject(any(URI.class), eq(GoogleResponse.class))).thenThrow(RestClientException.class);
        assertThrows(ReCaptchaUnavailableException.class, () -> {
            captchaServiceV3.processResponse("response", CaptchaServiceV3.REGISTER_ACTION);
        });
    }

}