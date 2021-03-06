/*
Copyright 2013 Thomas Scheuchzer, java-adventures.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.ja.security;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;

import com.ja.security.PasswordHash;

/**
 * 
 * @author Thomas Scheuchzer, www.java-adventures.com
 * 
 */
public class PasswordHashTest {

	/**
	 * We create a number of hashes of the same password. They all must be
	 * different. This test will take some time as we calcaulate a lot of
	 * hashes.
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	@Test
	public void testCreateHash() throws NoSuchAlgorithmException,
			InvalidKeySpecException {
		int numberOfHashes = 10000;
		String password = "My Test Password$";
		Set<String> hashes = new HashSet<>();
		PasswordHash pwHasher = new PasswordHash();
		// we work with less iterations to speed up the test
		pwHasher.setPbkdf2Iterations(10);
		for (int i = 0; i < numberOfHashes; i++) {
			hashes.add(pwHasher.createHash(password));
		}
		// check for duplicates
		assertThat(hashes.size(), is(numberOfHashes));
	}

	@Test
	public void testValidateCorrectPassword() throws NoSuchAlgorithmException,
			InvalidKeySpecException {
		String password = "My Test Password$";
		String correctHash = new PasswordHash().createHash(password);
		assertTrue(new PasswordHash().validatePassword(password, correctHash));
	}

	@Test
	public void testValidateNewConfigWithoutBreaking()
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		String password = "My Test Password$";
		String correctHash = new PasswordHash().createHash(password);
		PasswordHash newConfig = new PasswordHash();
		newConfig.setHashByteSize(48);
		newConfig.setSaltByteSize(48);
		newConfig.setPbkdf2Iterations(1000);
		newConfig.setSecureRandomAlgorithm("SHA256PRNG");
		assertTrue(newConfig.validatePassword(password, correctHash));
	}

	@Test
	public void testValidateWrongPassword() throws NoSuchAlgorithmException,
			InvalidKeySpecException {
		String password = "My Test Password$";
		String correctHash = new PasswordHash().createHash(password);
		assertFalse(new PasswordHash().validatePassword("My+Test Password$",
				correctHash));
	}

	@Test
	public void testConfigure() {
		Properties props = new Properties();
		props.setProperty(PasswordHash.PARAM_HASH_BYTE_SIZE, "24");
		props.setProperty(PasswordHash.PARAM_PBKDF2_ALGORITHM, "foo");
		props.setProperty(PasswordHash.PARAM_PBKDF2_ITERATIONS, "1000");
		props.setProperty(PasswordHash.PARAM_SALT_BYTE_SIZE, "32");
		props.setProperty(PasswordHash.PARAM_SECURE_RANDOM_ALGORITHM, "bar");
		PasswordHash ph = new PasswordHash();
		ph.configure(props);
		assertThat(ph.getHashByteSize(), is(24));
		assertThat(ph.getPbkdf2Algorithm(), is("foo"));
		assertThat(ph.getPbkdf2Iterations(), is(1000));
		assertThat(ph.getSaltByteSize(), is(32));
		assertThat(ph.getSecureRandomAlgorithm(), is("bar"));
	}
}
