/*
 * Copyright 2014 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.impl.authc

import com.stormpath.sdk.authc.AuthenticationRequest
import com.stormpath.sdk.authc.AuthenticationResult
import com.stormpath.sdk.authc.UsernamePasswordRequest
import com.stormpath.sdk.directory.AccountStore
import com.stormpath.sdk.impl.ds.InternalDataStore
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 1.0.alpha
 */
class BasicAuthenticatorTest {

    @Test
    void testNullHref() {
        def internalDataStore = createMock(InternalDataStore)
        def request = new UsernamePasswordRequest("foo", "bar")

        try {
            BasicAuthenticator basicAuthenticator = new BasicAuthenticator(internalDataStore)
            basicAuthenticator.authenticate(null, request)
            fail("Should have thrown")
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "href argument must be specified")
        }
    }

    @Test
    void testInvalidRequestClass() {
        def appHref = "https://api.stormpath.com/v1/applications/3TdbyY1qo74eDM4gTo2H95"
        def internalDataStore = createMock(InternalDataStore)
        def request = createMock(AuthenticationRequest)

        try {
            BasicAuthenticator basicAuthenticator = new BasicAuthenticator(internalDataStore)
            basicAuthenticator.authenticate(appHref, request)
            fail("Should have thrown")
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().contains("Only UsernamePasswordRequest instances are supported"))
            assertTrue(ex.getMessage().contains("must be an instance of class com.stormpath.sdk.authc.UsernamePasswordRequest"))
        }
    }

    @Test
    void testAuthenticateWithoutAccountStore() {

        def appHref = "https://api.stormpath.com/v1/applications/3TdbyY1qo74eDM4gTo2H95"
        def username = "fooUsername"
        def password = "barPasswd"

        def internalDataStore = createStrictMock(InternalDataStore)
        def basicLoginAttempt = createStrictMock(BasicLoginAttempt)
        def authenticationResultHelper = createStrictMock(AuthenticationResultHelper)
        def authenticationResult = createStrictMock(AuthenticationResult)

        def request = new UsernamePasswordRequest(username, password)

        expect(internalDataStore.instantiate(BasicLoginAttempt.class)).andReturn(basicLoginAttempt);
        expect(basicLoginAttempt.setType("basic"))
        expect(basicLoginAttempt.setValue("Zm9vVXNlcm5hbWU6YmFyUGFzc3dk"))
        expect(internalDataStore.create(appHref + "/loginAttempts", basicLoginAttempt, AuthenticationResultHelper.class)).andReturn(authenticationResultHelper)
        expect(authenticationResultHelper.getAuthenticationResult()).andReturn(authenticationResult)

        replay(internalDataStore, basicLoginAttempt, authenticationResultHelper, authenticationResult)

        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(internalDataStore)
        assertEquals(basicAuthenticator.authenticate(appHref, request), authenticationResult)

        verify(internalDataStore, basicLoginAttempt, authenticationResultHelper, authenticationResult)

    }

    @Test
    void testAuthenticateAccountStoreNull() {

        def appHref = "https://api.stormpath.com/v1/applications/3TdbyY1qo74eDM4gTo2H95"
        def username = "fooUsername"
        def password = "barPasswd"

        def internalDataStore = createStrictMock(InternalDataStore)
        def basicLoginAttempt = createStrictMock(BasicLoginAttempt)
        def authenticationResultHelper = createStrictMock(AuthenticationResultHelper)
        def authenticationResult = createStrictMock(AuthenticationResult)

        def request = new UsernamePasswordRequest(username, password, (AccountStore) null)

        expect(internalDataStore.instantiate(BasicLoginAttempt.class)).andReturn(basicLoginAttempt);
        expect(basicLoginAttempt.setType("basic"))
        expect(basicLoginAttempt.setValue("Zm9vVXNlcm5hbWU6YmFyUGFzc3dk"))
        expect(internalDataStore.create(appHref + "/loginAttempts", basicLoginAttempt, AuthenticationResultHelper.class)).andReturn(authenticationResultHelper)
        expect(authenticationResultHelper.getAuthenticationResult()).andReturn(authenticationResult)

        replay(internalDataStore, basicLoginAttempt, authenticationResultHelper, authenticationResult)

        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(internalDataStore)
        assertEquals(basicAuthenticator.authenticate(appHref, request), authenticationResult)

        verify(internalDataStore, basicLoginAttempt, authenticationResultHelper, authenticationResult)

    }

    @Test
    void testAuthenticateWithAccountStore() {

        def appHref = "https://api.stormpath.com/v1/applications/3TdbyY1qo74eDM4gTo2H95"
        def username = "fooUsername"
        def password = "barPasswd"

        def accountStore = createStrictMock(AccountStore)
        def internalDataStore = createStrictMock(InternalDataStore)
        def basicLoginAttempt = createStrictMock(BasicLoginAttempt)
        def authenticationResultHelper = createStrictMock(AuthenticationResultHelper)
        def authenticationResult = createStrictMock(AuthenticationResult)

        def request = new UsernamePasswordRequest(username, password, accountStore)

        expect(internalDataStore.instantiate(BasicLoginAttempt.class)).andReturn(basicLoginAttempt);
        expect(basicLoginAttempt.setType("basic"))
        expect(basicLoginAttempt.setValue("Zm9vVXNlcm5hbWU6YmFyUGFzc3dk"))
        expect(basicLoginAttempt.setAccountStore(accountStore))
        expect(internalDataStore.create(appHref + "/loginAttempts", basicLoginAttempt, AuthenticationResultHelper.class)).andReturn(authenticationResultHelper)
        expect(authenticationResultHelper.getAuthenticationResult()).andReturn(authenticationResult)

        replay(accountStore, internalDataStore, basicLoginAttempt, authenticationResultHelper, authenticationResult)

        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(internalDataStore)
        assertEquals(basicAuthenticator.authenticate(appHref, request), authenticationResult)

        verify(accountStore, internalDataStore, basicLoginAttempt, authenticationResultHelper, authenticationResult)

    }


}
