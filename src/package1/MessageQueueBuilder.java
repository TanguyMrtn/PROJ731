package package1;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class MessageQueueBuilder {
	
	
	public BlockingQueue<Message> getMessageList(String filesRepository) throws IOException {
		
		BlockingQueue<Message> messageQueue = new ArrayBlockingQueue<Message>(5);
		
		File repositoryFile = new File(filesRepository);
		
		File[] files = repositoryFile.listFiles();
	
	    for (int i = 0; i < files.length;i++) {

	    	InputStream is = new FileInputStream(files[i]); 
	    	BufferedReader buf = new BufferedReader(new InputStreamReader(is));
	    	
	    	String line = buf.readLine(); 
	    	StringBuilder sb = new StringBuilder(); 
	    	while(line != null){ 
	    		sb.append(line).append("\n"); 
	    		line = buf.readLine();
	    	} 
	    	buf.close();
	    	String fileAsString = sb.toString(); 
	    	
	    	Message newMessage = new Message("Phase0:"+files[i].getName(),fileAsString);
	    	
	    	messageQueue.add(newMessage);
	    
	    }

		return messageQueue;
		
	}
	
}
