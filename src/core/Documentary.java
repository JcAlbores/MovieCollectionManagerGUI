/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core;

// -- This class inherits or extends collection class
public class Documentary extends Collection {
	// -- Variable that stores what the documentary is about
    private final String subject;
    
    // -- A constructor used to create a new documentary object
    public Documentary(int id, String title, String genre, int year, double rating, String subject) {
    	// -- Super calls the constructor of the parent class (collection)
        super(id, title, genre, year, rating);
        this.subject = subject;
    }
    // -- Getter method for "subject" 
    public String getSubject() {
        return subject;
    }
    // -- This method override the parent class's getcategory() method  
    @Override
    public String getCategory() {
        return "Documentary";
    }
}

