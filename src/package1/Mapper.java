package package1;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class Mapper implements Runnable {

	Thread thread;
	BlockingQueue<Message> queue = new ArrayBlockingQueue<Message>(5);
	
	Mapper(BlockingQueue<Message> queue) {
		this.queue=queue;
		thread = new Thread(this,"thread");
		thread.start();
	}
	
	public void run() {
		
		Message message = this.queue.poll();
		Map<String, String> map = new HashMap<String, String> ();
		
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
            
            TreeMap<String, String> sorted = new TreeMap<>(map);
            
            String mapToString = mapToJSON(sorted);
            Message newMessage = new Message("Phase1",mapToString);
            
            this.queue.add(newMessage);
            
        }
	}
	
	public String mapToJSON(Map<String,String> map) {
		
		String json = "{";
		
		for(Entry<String, String> entry : map.entrySet()) {
		    String cle = entry.getKey();
		    cle = cle.replace("\n", "");
		    String valeur = entry.getValue();
		    String entryJSON = "\"" + cle + "\"" + ":" + "\"" + valeur + "\"" +" ,";
		    json= json + entryJSON;

		}
		
		json = json.substring(0, json.length() - 1);
		json = json + "}";
		
		return json;
		
	}
}




