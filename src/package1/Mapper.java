package package1;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;


public class Mapper implements Runnable {

	Thread thread;
	BlockingQueue<Message> queue;
	
	Mapper(BlockingQueue<Message> queue) {
		this.queue=queue;
		thread = new Thread(this,"thread");
		thread.start();
	}
	
	public void run() {
		
		Message message = this.queue.poll();
		//System.out.println("Maping...");
		Map<String, Integer> map = new HashMap<String, Integer> ();
		
		if (message != null) {
            String[] separatedWords = message.getContent().split(" ");
            for (String str: separatedWords) {
                if (map.containsKey(str)) {
                    int count = map.get(str);
                    map.put(str, count + 1);
                } else {
                    map.put(str, 1);
                }
                //System.out.println("working");
            }
            
            TreeMap<String, Integer> sorted = new TreeMap<>(map);
            
            String mapToString = mapToJSON(sorted);
            Message newMessage = new Message("Phase1",mapToString);
            this.queue.add(newMessage);
            
        }
	}
	
	public String mapToJSON(Map<String,Integer> map) {
		
		String json = "{";
		
		for(Entry<String, Integer> entry : map.entrySet()) {
		    String cle = entry.getKey();
		    Integer valeur = entry.getValue();
		    String entryJSON = "\"" + cle + "\"" + ":" + "\"" + valeur + "\"" +" ,";
		    json= json + entryJSON;

		}
		
		json = json.substring(0, json.length() - 1);
		json = json + "}";
		return json;
		
	}
}




