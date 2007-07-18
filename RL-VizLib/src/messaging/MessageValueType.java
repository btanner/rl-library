package messaging;

public enum MessageValueType {
	kStringList(0),
	kString(1),
	kBoolean(2),
	kNone(3);
	
	private final int id;
	
	MessageValueType(int id){
        this.id = id;
    }
    public int id()   {return id;}

}
