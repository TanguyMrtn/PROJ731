package package1;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Scheduler {

	private BlockingQueue<Message> queue = new ArrayBlockingQueue<Message>(5);
	int mapperDesiredNumber = 4;
	int mapperActualNumber = 0;
	ArrayList<Mapper> mapperList = new ArrayList<Mapper>();
	ArrayList<Reducer> reducerList = new ArrayList<Reducer>();
	
	boolean mapperJoined = false;
	boolean reducerJoined = false;
	
	IntHolder phase1read = new IntHolder(0) ;
	
	public Scheduler(BlockingQueue<Message> messageQueue) {
		this.queue = messageQueue;
		for (int i = 0; i<=1;i++) {
			Reducer reducer = new Reducer(this.queue,i,this.phase1read);
			reducerList.add(reducer);
		}
		
		
	}
	

	public void processQueue() throws InterruptedException {
				
		while (true) {
			
			if ((mapperJoined == false) && (mapperActualNumber == mapperDesiredNumber)) {
				for(Mapper mapper :mapperList) {
					mapper.thread.join();
					System.out.println("I joined mappers");
				}
				mapperJoined = true;
			}
			
			if ((reducerJoined == false) && (phase1read.value == mapperDesiredNumber)) {
				for (Reducer reducer:reducerList) {
					reducer.thread.join();
					System.out.println("I joined reducers");
				}
				reducerJoined = true;
			}
			
			Message currentMessage = queue.peek();
			if (currentMessage != null) {
				this.processMessage(currentMessage);
			}
		}
	}
	
	public void processMessage(Message message) {

		String messagePhase = message.getLabel().substring(0, 6);
		
		if (messagePhase.equals("Phase0")) {
			Mapper mapper = new Mapper(this.queue);
			mapperActualNumber += 1;
			mapperList.add(mapper);
		}
		
		if ((messagePhase.equals("Phase1")) && (mapperJoined == true)) {
			phase1read.value += 1;
			for (Reducer r : reducerList) {
				r.run();
			}
			
			queue.poll();
			
		}
	}
	
}
