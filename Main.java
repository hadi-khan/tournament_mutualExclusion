import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        int num_processes = Integer.parseInt(args[0]);
        int num_of_iterations = Integer.parseInt(args[1]);
        //int num_processes = 10;
        Thread thread[] = new Thread[num_processes];
        Schedule.NUMBER_OF_ITERATIONS = num_of_iterations;

        long startTimeMillis = System.currentTimeMillis();
        for(int x=0; x< num_processes; x++) {
            Schedule sch = new Schedule(x+1, num_processes);
            Thread next = new Thread(sch);
            thread[x] = next;
            next.start();
        }

        for(int y=0; y< num_processes; y++) {
            try {
                thread[y].join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        long diff = System.currentTimeMillis() - startTimeMillis;

        System.out.println("counter = "+ Schedule.counter.get());


        double throughput = (double)(num_processes *  Schedule.NUMBER_OF_ITERATIONS) / ((double)diff * 1.0D);
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            String data =  Schedule.NUMBER_OF_ITERATIONS + "," + num_processes + "," + diff + "," + throughput + "\n";
            File file = new File("output.csv");
            if (!file.exists()) {
                file.createNewFile();
            }

            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write(data);
        } catch (IOException var21) {
            var21.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }

                if (fw != null) {
                    fw.close();
                }
            } catch (IOException var20) {
                var20.printStackTrace();
            }

        }

    }
}
