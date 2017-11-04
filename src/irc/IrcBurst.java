package irc;

import jvn.JvnDynamicProxy;
import jvn.JvnObject;
import jvn.impl.JvnServerImpl;
import jvn.inter.ISentence;

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

    ISentence sentence;
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

            sentence = (ISentence) JvnDynamicProxy.getProxyInstance(js, "IRC",new Sentence());

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

                sentence.write(String.valueOf(tmp));
                sentence.unlock();

                sentence.read();

                // invoke the method
                tmp = Integer.valueOf(sentence.read());

                sentence.unlock();

                System.out.println("IRC Sentences Got : " + tmp);

            }

        } catch (Exception e) {
            System.out.println("IRC BURST : " + e.getMessage());
        }


    }
}
