/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.AndroidPublisherScopes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * Helper class to initialize the publisher APIs client library.
 * <p>
 * Before making any calls to the API through the client library you need to
 * call the {@link MyAndroidPublisherHelper#init(String,String)} method.
 * </p>
 */
public class MyAndroidPublisherHelper {

    private static final Log log = LogFactory.getLog(MyAndroidPublisherHelper.class);

    static final String MIME_TYPE_APK = "application/vnd.android.package-archive";

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /**
     * Authorizes the installed application to access user's protected data.
     *
     * @throws IOException
     */
    private static Credential authorizeWithInstalledApplication(String secretsJsonPath) throws IOException {
        log.info("Authorizing using installed application");

        return GoogleCredential.fromStream(new FileInputStream(new File(secretsJsonPath)),HTTP_TRANSPORT,JSON_FACTORY)
                .createScoped(Collections.singleton(AndroidPublisherScopes.ANDROIDPUBLISHER));
    }


    /**
     * Performs all necessary setup steps for running requests against the API
     * using the Installed Application auth method.
     *@param secretsJsonPath the path of the client secret json
     * @param applicationName the name of the application: com.example.app
     * @return the {@Link AndroidPublisher} service
     */
    protected static AndroidPublisher init(String secretsJsonPath,String applicationName) throws Exception {
        // Authorization.
        newTrustedTransport();
        Credential credential;
        credential=authorizeWithInstalledApplication(secretsJsonPath);

        // Set up and return API client.
        return new AndroidPublisher.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(applicationName)
                .build();
    }

    private static void newTrustedTransport() throws GeneralSecurityException,
            IOException {
        if (null == HTTP_TRANSPORT) {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        }
    }
}
