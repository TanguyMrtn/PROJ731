package package1;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Mapper implements Runnable{

	Thread thread;
	BlockingQueue<Message> queue = new ArrayBlockingQueue<Message>(5);
	
	Mapper(BlockingQueue<Message> queue) {
		this.queue=queue;
		thread = new Thread(this,"thread");
		thread.start();
	}
	
	public void run() {
		
		Map<String, String> map = new HashMap<String, String> ();
		Message message = this.queue.poll();
		System.out.println("Thread message : " + message);
		if (message != null) {
            String[] separatedWords = message.getContent().split(" ");
            for (String str: separatedWords) {
                if (map.containsKey(str)) {
                    int count = Integer.parseInt(map.get(str));
                    map.put(str, String.valueOf(count + 1));
                } else {
                    map.put(str, "1");
                }
            }
            try {
				thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            System.out.println("Resultat : " + map);
            
        }
	}
	
	public final void join() {}
}




