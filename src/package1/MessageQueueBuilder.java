package package1;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class MessageQueueBuilder {
	
	
	public BlockingQueue<Message> getMessageList(String filesRepository) throws IOException {
		
		
		
		File repositoryFile = new File(filesRepository);
		
		File[] files = repositoryFile.listFiles();
		BlockingQueue<Message> messageQueue = new ArrayBlockingQueue<Message>(15);
	    for (int i = 0; i < files.length;i++) {

	    	InputStream is = new FileInputStream(files[i]); 
	    	BufferedReader buf = new BufferedReader(new InputStreamReader(is));
	    	
	    	String line = buf.readLine(); 
	    	StringBuilder sb = new StringBuilder(); 
	    	while(line != null){
	    		line = Normalizer.normalize(line, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	    		sb.append(line.replaceAll("[^\\p{L}\\s']", " ").replaceAll("\\s{2,}", " ").trim()+" "); 
	    		
	    		line = buf.readLine();
	    	} 
	    	
	    	buf.close();
	    	String fileAsString = sb.toString(); 
	    	//System.out.println(fileAsString);
	    	Message newMessage = new Message("Phase0:"+files[i].getName(),fileAsString);
	    	
	    	messageQueue.add(newMessage);
	    	System.out.println("Message built");
	    
	    }

		return messageQueue;
		
	}
	
}
