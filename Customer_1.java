/**
   A class that represents a customer having firstName and lastName
   properties, as well as a startDate determined by when the customer
   is constructed. Implemented as a simple example of a bean
   On Tomcat place in folder webapps\ROOT\WEB-INF\classes\multitier
   @see CustomerServlet.java
*/
package multiteir;

import java.io.Serializable;
import java.util.Date;

public class Customer_1 implements Serializable
{
   private String firstName, lastName, birthDate;
   private Date startDate;
   
   public Customer_1()
   {  firstName = null;
      lastName = null;
      birthDate = null;
      startDate = new Date(); // current date and time
   }
   
   public String getFirstName()
   {  return firstName;
   }
   
   public void setFirstName(String firstName)
   {  this.firstName = firstName;
   }
   
   public String getLastName()
   {  return lastName;
   }
   
   public void setLastName(String lastName)
   {  this.lastName = lastName;
   }
   
   public Date getStartDate()
   {  return startDate;
   }
   
      public void setBirthday(String birthDate)
   {
       this.birthDate = birthDate;
   }
   
   
   public String getBirthday()
   {
       return birthDate;
   }
   
   public String getFullName()
   {  String fullName = "";
      if (firstName != null)
         fullName += firstName;
      if (firstName!=null && lastName!=null)
         fullName += " ";
      if (lastName != null)
         fullName += lastName;
      return fullName;
   }
} 
