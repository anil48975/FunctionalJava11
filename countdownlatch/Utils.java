/*
 * Copyright © 2019 JDA Software Group, Inc. All rights reserved.
 */

package com.redprairie.refs.services.security.test;

import java.util.concurrent.CountDownLatch;

public class Utils {
    private static Session session = new Session();
    public static synchronized Session getSession() {
        return session;
    }
}
