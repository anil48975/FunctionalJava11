/*
 * Copyright © 2019 JDA Software Group, Inc. All rights reserved.
 */

package com.redprairie.refs.services.security.test;


import java.util.concurrent.CountDownLatch;

public class SessionDataService {
    private static volatile int sessionDataGatheringCounter = 0;
    public void gatherSessionData(Session session) {
        System.out.println(Thread.currentThread().getName() + " entered gatherSessionData method time is: "+  new java.util.Date(System.currentTimeMillis()));
        if(session.gatherDataStarted.compareAndSet(false, true)) {
            System.out.println("----------------------------------------------------------  Session data gathering started by thread: " + Thread.currentThread().getName() + " time is: "+  new java.util.Date(System.currentTimeMillis()));
            try {
                Thread.sleep(40000);
                session.fullName="session full name "+ sessionDataGatheringCounter;
                Thread.sleep(10000);
                session.siteName = "session site name " + sessionDataGatheringCounter;
                sessionDataGatheringCounter++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++  Session data gathering finished by thread: " + Thread.currentThread().getName()  + " time is: "+  new java.util.Date(System.currentTimeMillis()));
            session.gatherDataLatch.countDown();
        }
        System.out.println(Thread.currentThread().getName() + " exited gatherSessionData method time is: "+  new java.util.Date(System.currentTimeMillis()));
    }
}


