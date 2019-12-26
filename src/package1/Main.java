package package1;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		
		MessageQueueBuilder messageBuilder = new MessageQueueBuilder();
		
		BlockingQueue<Message> messageQueue = messageBuilder.getMessageList("C:/Users/tangu/eclipse-workspace/Projet PROJ731 Map Reduce/filesTest");
		
		Scheduler scheduler = new Scheduler(messageQueue);
		
		scheduler.processQueue();
		
	}

}
