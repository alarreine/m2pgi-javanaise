package irc;

import jvn.JvnObject;
import jvn.impl.JvnServerImpl;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IrcBurst {

    public static void main(String argv[]) {
        int threadN=4;
        ExecutorService executor = Executors.newFixedThreadPool(threadN);
        try {
            for (int i = 0; i < threadN; i++) {
                JvnThread t = new JvnThread();
                executor.execute(t);

            }

            executor.shutdown();

            while (!executor.isTerminated()) {

            }

        } catch (Exception e) {
            System.out.println("IRC BURST : " + e.getMessage());
        }
    }

}

class JvnThread extends Thread {

    JvnObject sentence;
    JvnServerImpl js;
    int tmp;
    Random random;

    public JvnThread() {
        try {
            int tmp=0;
            random = new Random();
            js = JvnServerImpl.jvnGetServer();
            JvnObject jo = js.jvnLookupObject("IRC");


            if (jo == null) {
                jo = js.jvnCreateObject((Serializable) new Sentence());
                // after creation, I have a write lock on the object
                jo.jvnUnLock();
                js.jvnRegisterObject("IRC", jo);
            }

            sentence = jo;

        } catch (Exception e) {

        }


    }

    @Override
    public void run() {
        try {
            Long i;
            while (true){
                i = random.nextLong()%1000;

                sleep(i>0?i:i*-1);

                tmp++;
                sentence.jvnLockWrite();
                ((Sentence) (sentence.jvnGetObjectState())).write(String.valueOf(tmp));
                sentence.jvnUnLock();

                System.out.println("IRC Sentences Write : " + ((Sentence) (sentence.jvnGetObjectState())).read());

                sentence.jvnLockRead();

                // invoke the method
                tmp = Integer.valueOf(((Sentence) (sentence.jvnGetObjectState())).read());

                sentence.jvnUnLock();

                System.out.println("IRC Sentences Got : " + ((Sentence) (sentence.jvnGetObjectState())).read());

            }

        } catch (Exception e) {
            System.out.println("IRC BURST : " + e.getMessage());
        }


    }
}
