package org.example;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import java.nio.file.Paths;

public class Connector {
    private static final String SECURE_BUNDLE_PATH = "C:/Users/nehaj/OneDrive/Desktop/secure-connect-trade-app.zip";
    private static final String KEYSPACE = "trade_app";
    private static final String CLIENT_ID = "JyUZqcvZXWSSzocnYJmPRrgp";
    private static final String CLIENT_SECRET = "SWpze1-+L6kB7KIqS3AEbTJXpx2r5kazXrha.S8Z0LFoM1Z-duebPPhLteLlwuE4T7i0gaRihm7fiitfr27Rp9rB7cxq2bBZRnHxIv3-2oWNnpZhDa6+ZKpeWNZ96.PG";

    public static CqlSession getSession() {
        CqlSession session = null;
        try {
            CqlSessionBuilder builder = CqlSession.builder();
            builder.withCloudSecureConnectBundle(Paths.get(SECURE_BUNDLE_PATH))
                    .withAuthCredentials(CLIENT_ID, CLIENT_SECRET)
                    .withKeyspace(KEYSPACE);
            session = builder.build();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return session;
    }

}