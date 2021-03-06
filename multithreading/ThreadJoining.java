package multithreading;

/*
Select the two correct answers.
(a) The first number printed is 13.
(b) The number 14 is printed before the number 22.
(c) The number 24 is printed before the number 21.
(d) The last number printed is 12.
(e) The number 11 is printed before the number 23.

Answer:
13.16 (b) and (d)
The inner createThread() call is evaluated first, and will print 23 as the first number.
The last number the main thread prints is 14. After the main thread ends, the thread
created by the inner createdThread() completes its join() call and prints 22. After
this thread ends, the thread created by the outer createThread() call completes its
join() call and prints the number 12 before the program terminates. Note that in
the inner call to the createThread() method, the thread t2 can start to execute before
this call finishes, resulting in 21 being printed before 24.
 */

public class ThreadJoining {
    static Thread createThread(final int i, final Thread t1) {
        Thread t2 = new Thread() {
            public void run() {
                System.out.println(i+1);
                try {
                    t1.join();
                } catch (InterruptedException ie) {
                }
                System.out.println(i+2);
            }
        };
        System.out.println(i+3);
        t2.start();
        System.out.println(i+4);
        return t2;
    }
    public static void main(String[] args) {
        createThread(10, createThread(20, Thread.currentThread()));
    }
}