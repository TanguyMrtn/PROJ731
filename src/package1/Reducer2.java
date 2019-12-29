package package1;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
/*import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;*/

public class Reducer2 implements Runnable {
	
	Thread thread;
	int infInterval;
	int supInterval;
	int numberOfMappers;
	BlockingQueue<Message> queue;
	Map<String, Integer> map = new TreeMap<String,Integer>();
	AtomicInteger reducerRead;
	int messageProcess = 0;
	int reducerNumber;
	
	/*private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private final Lock writeLock = readWriteLock.writeLock();
	private final Lock readLock = readWriteLock.readLock(); */
	
	Reducer2(BlockingQueue<Message> messageQueue, int reducerNumber, AtomicInteger reducerRead, int numberOfReducers, int numberOfMappers) {
		this.queue=messageQueue;
		this.reducerRead = reducerRead;
		this.reducerNumber = reducerNumber;
		this.numberOfMappers = numberOfMappers;
		this.infInterval = Math.round((reducerNumber*57)/numberOfReducers+65);
		this.supInterval = Math.round(((reducerNumber+1)*57)/numberOfReducers+65);
		
		thread = new Thread(this,"thread");
		thread.start();
		
	}
	
	public void run() {
		while (true) {
			synchronized(queue) {
				
				try {
					//System.out.println(infInterval + " - " +supInterval);
					//System.out.println("I'm waiting");
					queue.wait();
					Message message = queue.peek();
					messageProcess += 1;
					//System.out.println("I stop waiting ");
					
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
					
					
					
					if (messageProcess == numberOfMappers) {
						//System.out.println("I kill myself");
						if (map.isEmpty()) {
							reducerRead.getAndIncrement();
							break;
						}
						else {
							String mapToString = mapToJSON(map);
				            Message newMessage = new Message("Phase2",mapToString);
				            this.queue.add(newMessage);
				            reducerRead.getAndIncrement();
							break;
						}
						
					}
					
					/* writeLock.lock();
					readLock.lock();
					try { */
					//System.out.println("Wtf");
					reducerRead.getAndIncrement();
					//System.out.println(reducerNumber + " " +infInterval + " - " +supInterval);
					/*}
					finally
					{
						writeLock.unlock();
						readLock.unlock();
					} */ 
					
					
			} catch(InterruptedException e){
                e.printStackTrace();
            	}
			}
		}
	}
	public String mapToJSON(Map<String,Integer> map) {
		
		String json = "{";
		
		for(Entry<String, Integer> entry : map.entrySet()) {
		    String cle = entry.getKey();
		    cle = cle.replace("\n", "");
		    String valeur = entry.getValue().toString();
		    String entryJSON = cle + ":" + "\"" + valeur + "\"" +" ,";
		    json= json + entryJSON;

		}
		if (json.length()>1) {
			json = json.substring(0, json.length() - 2);
		}
		json = json + "}";
		return json;
		
	}
	}
