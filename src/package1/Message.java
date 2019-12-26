package package1;

public class Message {

	private String label;
	private String content;
	
	public Message(String fileName, String content) {
		this.label = fileName;
		this.content = content;
		
	}

	public String getLabel() {
		return label;
	}

	public String getContent() {
		return content;
	}
	
}
