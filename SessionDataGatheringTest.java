/*
 * Copyright © 2019 JDA Software Group, Inc. All rights reserved.
 */

package com.redprairie.refs.services.security.test;

import java.util.concurrent.CountDownLatch;

public class SessionDataGatheringTest {
    public static void main(String args[]) {
        SessionDataService sessionDataService = new SessionDataService();

        // Gather session data call take 40 seconds [40000]

        Thread jwtThread1 = createThread("jwtThread1",false, sessionDataService, 4000, 0);
        Thread jwtThread2 = createThread("jwtThread2", false, sessionDataService, 5000, 0);

        Thread loginThread1 = createThread("loginThread1", true, sessionDataService, 0, 0);
        Thread loginThread2 = createThread("loginThread2", true, sessionDataService, 0, 0);
        Thread loginThread3 = createThread("loginThread3", true, sessionDataService, 0, 0);
        Thread loginThread4 = createThread("loginThread4", true, sessionDataService, 0, 0);

        Thread loginThread5 = createThread("loginThread5", true, sessionDataService, 7000, 50000);
        Thread loginThread6 = createThread("loginThread6", true, sessionDataService, 50000, 20000);

        Thread refreshThread1 = createRefreshThread("refreshThread1", true, sessionDataService, 10000, 20000);
        Thread refreshThread2 = createRefreshThread("refreshThread2", true, sessionDataService, 90000, 20000);


        jwtThread1.start();
        jwtThread2.start();
        loginThread1.start();
        loginThread2.start();
        loginThread3.start();
        loginThread4.start();
        loginThread5.start();
        loginThread6.start();
        refreshThread1.start();
        refreshThread2.start();

    }

    public static Thread createThread (String threadName, boolean sessinDataGatheringRequired , SessionDataService sessionDataServiceForGathingSessionData, long keepWorkingBeforeGatherSessionDataCall, long keepWorkingBeforeLockedForSessionData) {
        Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Session session = Utils.getSession();

                        Thread.sleep(keepWorkingBeforeGatherSessionDataCall);

                        System.out.println(threadName + " before session data fetch call " + " time is: " + new java.util.Date(System.currentTimeMillis()));

                        sessionDataServiceForGathingSessionData.gatherSessionData(session);

                        Thread.sleep(keepWorkingBeforeLockedForSessionData);

                        // block only if session data gathering completion is required
                        if (sessinDataGatheringRequired) {
                            System.out.println(threadName + " waiting for latch countdown " + " time is: " + new java.util.Date(System.currentTimeMillis()));
                            session.gatherDataLatch.await();
                        }
                        System.out.println("*********************************************************** " +threadName + " session id is : " + session.id + " and name is : " + session.fullName + " and sitename is : " + session.siteName + " time is: " + new java.util.Date(System.currentTimeMillis()));
                    }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.setName(threadName);
        return thread;
    }

    public static Thread createRefreshThread (String threadName, boolean sessinDataGatheringRequired , SessionDataService sessionDataServiceForGathingSessionData, long keepWorkingBeforeGatherSessionDataCall, long keepWorkingBeforeLockedForSessionData) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Session session = Utils.getSession();

                    Thread.sleep(keepWorkingBeforeGatherSessionDataCall);

                    System.out.println(threadName + " before session data fetch call session.gatherDataStarted.get() is : " + session.gatherDataStarted.get() + " time is: " + new java.util.Date(System.currentTimeMillis()));

                    // If session data gathering is in progress, wait untill it finishes
                    if(session.gatherDataStarted.get() == true) {
                        System.out.println(threadName + " waiting to finish gather data process finish " + " time is: " + new java.util.Date(System.currentTimeMillis()));
                        session.gatherDataLatch.await();
                    }

                    // Once data gathering is fiished
                    // Set a new CountDownLatch
                    session.gatherDataLatch = new CountDownLatch(1);

                    // Reset the flag to start session data gathering again
                    session.gatherDataStarted.getAndSet( false);
                    System.out.println(threadName + " refresh data started " + " time is: " + new java.util.Date(System.currentTimeMillis()));
                    sessionDataServiceForGathingSessionData.gatherSessionData(session);

                    Thread.sleep(keepWorkingBeforeLockedForSessionData);

                    session.gatherDataLatch.await();
                    System.out.println("*********************************************************** " +threadName + " session id is : " + session.id + " and name is : " + session.fullName + " and sitename is : " + session.siteName + " time is: " + new java.util.Date(System.currentTimeMillis()));
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.setName(threadName);
        return thread;
    }

}

