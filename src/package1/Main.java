package package1;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		
		int numberOfMappers =10;
		
		int numberOfReducers = 4;
		
		MessageQueueBuilder messageBuilder = new MessageQueueBuilder();
		
		BlockingQueue<Message> messageQueue = messageBuilder.getMessageList("C:/Users/tangu/eclipse-workspace/Projet PROJ731 Map Reduce/bigtest",numberOfMappers);
		
		System.out.println(messageQueue.size());
		
		numberOfMappers = messageQueue.size();
		
		Scheduler scheduler = new Scheduler(messageQueue,numberOfReducers,numberOfMappers);
		
		scheduler.processQueue();
		
	}

}
