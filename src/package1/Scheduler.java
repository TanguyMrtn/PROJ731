package package1;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Scheduler {

	private BlockingQueue<Message> queue = new ArrayBlockingQueue<Message>(5);
	
	public Scheduler(BlockingQueue<Message> messageQueue) {
		this.queue = messageQueue;
	}
	
	public void processQueue() {
		System.out.println("Processing...");
		
		while (!(queue.isEmpty())) {
			System.out.println(queue.size());
			System.out.println(queue.isEmpty());
			Message currentMessage = queue.peek();
			System.out.println(queue);
			if (currentMessage != null) {
				this.processMessage(currentMessage);
			}
		
				
		}
		
	}
	
	public void processMessage(Message message) {
		String messagePhase = message.getLabel().substring(0, 6);
		
		if (messagePhase.equals("Phase0")) {
			Mapper mapper = new Mapper(this.queue);
			mapper.join();
		}
		
		if (messagePhase.equals("Phase1")) {
			Reducer reducer = new Reducer();
		}
	}
}
