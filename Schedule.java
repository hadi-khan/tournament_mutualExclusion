import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicInteger;

public class Schedule implements Runnable{
    static AtomicInteger counter = new AtomicInteger();
    static int NUMBER_OF_ITERATIONS = 1000000;

    private int id;
    static int nodes, firstLeaf, num_proc;
    volatile static AtomicIntegerArray flag;
    volatile static AtomicIntegerArray victim;
    volatile static AtomicIntegerArray process;


    public void acquire() throws InterruptedException{
        int ipid = firstLeaf + id - 1;
        int k = (int) Math.ceil(Math.log(num_proc) / Math.log(2));
        for(int i = 0; i < k; ++i) {
            flag.set(ipid, 1);
            victim.set((ipid - 1) / 2, id);
            if(ipid % 2 == 0) {
                while(flag.get(ipid - 1) ==1 & victim.get((ipid - 1) / 2) == id );
            } else {
                while(flag.get(ipid + 1) == 1 & victim.get((ipid - 1) / 2) == id );
            }
            process.set((id - 1) * num_proc + i, ipid);
            ipid = (ipid - 1) / 2;
        }
    }

    public void release() {
        int k = (int) Math.ceil(Math.log(num_proc) / Math.log(2));
        for(int j = 0; j < k; ++j ) {
            int temp = process.get((id-1)* num_proc + j);
            flag.set(temp, 0);
        }
    }

    public Schedule(int ID, int N) {
        this.id = ID;
        int powTwoNode = (int) Math.pow(2, Math.ceil((Math.log(N) / Math.log(2))));
        nodes = powTwoNode + (powTwoNode - 1);
        firstLeaf = powTwoNode - 1;
        num_proc = N;
        process = new AtomicIntegerArray(N * num_proc);

        flag = new AtomicIntegerArray(nodes);
        victim = new AtomicIntegerArray(powTwoNode-1);
        for(int j = 0; j < N * num_proc; ++j)
            process.set(j, 0);
        for(int j = 0; j < nodes; ++j)
            flag.set(j, 0);
       for (int k=0; k < powTwoNode-1; k++)
           victim.set(k, 0);

    }

    public void doWork() throws InterruptedException{
        for(int x=0; x < NUMBER_OF_ITERATIONS; x++) {
            //lock
            acquire();

            counter.getAndIncrement();

            //unlock
            release();
        }
    }

    @Override
    public void run() {
        try {
            doWork();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}

