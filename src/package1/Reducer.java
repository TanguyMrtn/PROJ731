package package1;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Reducer implements Runnable {
	
	Thread thread;
	int infInterval;
	int supInterval;
	BlockingQueue<Message> queue = new ArrayBlockingQueue<Message>(5);
	Map<String, Integer> map = new TreeMap<String,Integer>();
	boolean reducersJoined;
	IntHolder numberOfPhase1read;
	
	Reducer(BlockingQueue<Message> messageQueue, int threadNumber, IntHolder numberOfPhase1read) {
		this.queue=messageQueue;
		this.numberOfPhase1read = numberOfPhase1read;
		this.infInterval = (threadNumber*127)/2;
		this.supInterval = ((threadNumber+1)*127)/2;
		thread = new Thread(this,"thread");
		
	}
	
	public void run() {
		
		if (numberOfPhase1read.value == 4) {
			System.out.println("I'm building phase2 message");
			String mapToString = mapToJSON(map);
            Message newMessage = new Message("Phase2",mapToString);
            
            this.queue.add(newMessage);
			
		}
		else {
		Message message = this.queue.peek();
		
		TreeMap<String,Integer> myMap = new TreeMap<String,Integer>();
		String[] pairs = message.getContent().replace("{", "").replace("}", "").split(" ,");
		for (int i=0;i<pairs.length;i++) {
		    String pair = pairs[i];
		    String[] keyValue = pair.split(":");
		    
		
		    int firstChar = (int) keyValue[0].charAt(1);
		    if ((infInterval <= firstChar) && (firstChar < supInterval)){
		    	myMap.put(keyValue[0], Integer.parseInt(keyValue[1].replace("\"", "").replace(" ", "")));
		    }
		    
		} 

		myMap.forEach((key, value) -> this.map.merge(key, value, (v1, v2) -> v1+v2));
		
		}
		
	}
	
	public String mapToJSON(Map<String,Integer> map) {
		
		String json = "{";
		
		for(Entry<String, Integer> entry : map.entrySet()) {
		    String cle = entry.getKey();
		    cle = cle.replace("\n", "");
		    String valeur = entry.getValue().toString();
		    String entryJSON = "\"" + cle + "\"" + ":" + "\"" + valeur + "\"" +" ,";
		    json= json + entryJSON;

		}
		
		json = json.substring(0, json.length() - 1);
		json = json + "}";
		
		return json;
		
	}

}
