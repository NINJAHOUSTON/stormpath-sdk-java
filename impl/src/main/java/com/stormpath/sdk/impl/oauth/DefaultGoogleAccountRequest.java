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
package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.*;

public class DefaultGoogleAccountRequest extends AbstractProviderAccountRequest {

    private DefaultGoogleAccountRequest(ProviderData providerData) {
        super(providerData);
    }

    public static final class Builder extends ProviderAccountRequest.Builder<GoogleAccountRequestBuilder> implements GoogleAccountRequestBuilder {

        private String code;

        @Override
        public Builder setCode(String code) {
            this.code = code;
            return this;
        }

        @Override
        public ProviderAccountRequest build() {
            Assert.state(!(Strings.hasText(this.code) && Strings.hasText(this.accessToken)), "Either 'code' or 'accessToken' properties must exist in a Google account request, but not both.");
            Assert.state(Strings.hasText(this.code) || Strings.hasText(this.accessToken), "Either 'code' or 'accessToken' properties must exist in a Google account request.");

            DefaultGoogleProviderData providerData = new DefaultGoogleProviderData(null);

            if(this.accessToken != null) {
                providerData.setAccessToken(this.accessToken);
            } else {
                providerData.setCode(this.code);
            }
            providerData.setProviderId(IdentityProviderType.GOOGLE.getNameKey());

            return new DefaultGoogleAccountRequest(providerData);
        }

    }

}