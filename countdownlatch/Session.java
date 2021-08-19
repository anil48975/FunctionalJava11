/*
 * Copyright © 2019 JDA Software Group, Inc. All rights reserved.
 */

package com.redprairie.refs.services.security.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class Session {
    String id;
    String fullName;
    String siteName;
    CountDownLatch gatherDataLatch;
    AtomicBoolean gatherDataStarted;

    Session() {
        this.id = "12";
        this.gatherDataLatch = new CountDownLatch(1);
        gatherDataStarted = new AtomicBoolean();
    }
}
