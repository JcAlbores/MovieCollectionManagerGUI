package core;

// -- This class inherits or extends collection class
public class Documentary extends Collection {
	// -- Variable that stores what the documentary is about
    private String subject;
    
    // -- A constructor used to create a new documentary object
    public Documentary(int id, String title, String genre, int year, double rating, String subject) {
    	// -- Super calls the constructor of the parent class (collection)
        super(id, title, genre, year, rating);
        setSubject(subject);
    }
    // -- Getter method for "subject" 
    public String getSubject() {
        return subject;
    }
    
    // Setter for Subject
    public void setSubject(String subject) {
    	this.subject = subject;
    }
    
    // -- This method override the parent class's getcategory() method  
    @Override
    public String getCategory() {
        return "Documentary";
    }
}

