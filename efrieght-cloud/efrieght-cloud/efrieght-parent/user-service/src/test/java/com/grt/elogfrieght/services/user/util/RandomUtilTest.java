package com.grt.elogfrieght.services.user.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = { "test" })
public class RandomUtilTest {

	@Test
	void testGenerateRandomAlphanumericString() {
		String randomString = RandomUtil.generateRandomAlphanumericString();
		assertThat(randomString).isNotNull();
		assertThat(randomString).isNotEmpty();
	}

	@Test
	void testGenerateSixDigitRandomAlphanumericString() {
		String randomString = RandomUtil.generateSixDigitRandomAlphanumericString();
		assertThat(randomString).isNotNull();
		assertThat(randomString).isNotEmpty();
		assertThat(randomString.length()).isEqualTo(6);
	}

	@Test
	void testGeneratePassword() {
		String password = RandomUtil.generatePassword();
		assertThat(password).isNotNull();
		assertThat(password).isNotEmpty();
	}

	@Test
	void testGenerateActivationKey() {
		String activationKey = RandomUtil.generateActivationKey();
		assertThat(activationKey).isNotNull();
		assertThat(activationKey).isNotEmpty();
	}

	@Test
	void testGenerateResetKey() {
		String resetKey = RandomUtil.generateResetKey();
		assertThat(resetKey).isNotNull();
		assertThat(resetKey).isNotEmpty();
	}
}
