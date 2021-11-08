package multithreading;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/*
The Java concurrency API provides a class that allows one or more threads to wait until a
set of operations are made. It's the CountDownLatch class.
This class is initialized with an integer number,
which is the number of operations the threads are going to wait for.
When a thread wants to wait for the execution of these operations, it uses the await() method.
This method puts the thread to sleep until the operations are completed.
When one of these operations finishes, it uses the countDown() method to decrement the internal counter
of the CountDownLatch class. When the counter arrives to 0, the class wakes up all the
threads that were sleeping in the await() method.

In this recipe, you will learn how to use the CountDownLatch class implementing a videoconference
system. The video-conference system will wait for the arrival of all the participants
before it begins.
 */

public class CountDownLatchDemo {
    public static void main(String[] args) {
        Videoconference conference=new Videoconference(10);
        Thread threadConference=new Thread(conference);
        threadConference.start();

        for (int i=0; i<10; i++){
            Participant p=new Participant(conference, "Participant "+i);
            Thread t=new Thread(p);
            t.start();
        }

    }
}
class Videoconference implements Runnable {

    private final CountDownLatch controller;

    public Videoconference(int number) {
        controller = new CountDownLatch(number);
    }
    public void arrive(String name){
        System.out.printf("%s has arrived.",name);
        controller.countDown();
        System.out.printf("VideoConference: Waiting for %d participants.\n",controller.getCount());
    }

    @Override
    public void run() {
        System.out.printf("VideoConference: Initialization: %d participants.\n",controller.getCount());
        try {
            controller.await();
            System.out.printf("VideoConference: All the participants have come\n");
                    System.out.printf("VideoConference: Let's start...\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Participant implements Runnable {
    private Videoconference conference;
    private String name;
    public Participant(Videoconference conference, String name) {
        this.conference=conference;
        this.name=name;
    }

    @Override
    public void run() {
        long duration=(long)(Math.random()*10);
        try {
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        conference.arrive(name);
    }
}

/* Program output:
VideoConference: Initialization: 10 participants.
Participant 8 has arrived.VideoConference: Waiting for 9 participants.
Participant 3 has arrived.VideoConference: Waiting for 8 participants.
Participant 1 has arrived.VideoConference: Waiting for 7 participants.
Participant 6 has arrived.VideoConference: Waiting for 6 participants.
Participant 4 has arrived.VideoConference: Waiting for 5 participants.
Participant 2 has arrived.VideoConference: Waiting for 4 participants.
Participant 5 has arrived.VideoConference: Waiting for 3 participants.
Participant 9 has arrived.VideoConference: Waiting for 2 participants.
Participant 0 has arrived.VideoConference: Waiting for 1 participants.
Participant 7 has arrived.VideoConference: Waiting for 0 participants.
VideoConference: All the participants have come
VideoConference: Let's start...
 */
