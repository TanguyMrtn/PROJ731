package package1;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class MessageQueueBuilder {


	public BlockingQueue<Message> getMessageList(String filesRepository, int numberOfMappers) throws IOException {


		
		File repositoryFile = new File(filesRepository);

		File[] files = repositoryFile.listFiles();
	
		BlockingQueue<Message> messageQueue = new ArrayBlockingQueue<Message>(numberOfMappers*2);
		
		ArrayList<String> strings = new ArrayList<String>();
		
		StringBuilder sb = new StringBuilder(); 
		
		for (int i = 0; i < files.length;i++) {
			//System.out.println(files[i].getName());
			BufferedReader buf = new BufferedReader(new FileReader(files[i]));

			String line = buf.readLine(); 
			
			
			while(line != null){
				
				if (!line.isEmpty()) {
					line = Normalizer.normalize(line, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}", "")+" ";
					
					line = line.replaceAll("[^\\p{L}\\s']", " ");
					
					line = line.replaceAll("\\s{1,}", " ").trim()+" ";
					
					try {
						sb.append(line);
					}
					catch (java.lang.OutOfMemoryError e) {  //If the stringbuilder size is getting too big
						System.out.println("Warning : text too big so I have to build another string");
						strings.add(sb.toString().substring(0,sb.length()-1));
						sb.setLength(0);
					}
					
					if (sb.length()> 2100000000) {
						System.out.println("Warning : text too big so I have to build another string");
						strings.add(sb.toString().substring(0,sb.length()-1));
						sb.setLength(0);
					}
				}
				line = buf.readLine();
			}
			buf.close();
		}
		
		if (sb.length() != 0) {
			strings.add(sb.toString().substring(0,sb.length()-1));
		}
		
		for (String string : strings) {
			
			int stringSize = string.length();
			//System.out.println(string);
			int infSub = 0 ;
			int supSub = Math.round(stringSize/numberOfMappers);
			int varSup = supSub;
			if (supSub == 0) {
				supSub = 1;
				varSup = 1;
			}
			
			
			
			for (int i = 0; i<numberOfMappers;i++) {
				
				if ((infSub != 0) && (string.charAt(infSub) != ' ')) {
					while (string.charAt(infSub) != ' ') {
						infSub -= 1;
					}
				}
				
				if ((supSub != string.length()) && (string.charAt(supSub) != ' ')) {
					while (string.charAt(supSub) != ' ' ) {
						
						supSub += 1;
						if (supSub == string.length()) {
							break;
						}
					}
					
				}
				Message newMessage = new Message("Phase0",string.toString().substring(infSub,supSub));
				if ((infSub != supSub) && (!newMessage.getContent().isEmpty())) {
					messageQueue.add(newMessage);
					//System.out.println("Message : " + newMessage.getContent());
				}
				
				infSub = supSub;
				supSub += varSup;
				
				if (supSub >= string.length()) {
					supSub = string.length();
				}
				
				if (infSub >= string.length()) {
					break;
				}
			}
		}
		return messageQueue;

	}

}
