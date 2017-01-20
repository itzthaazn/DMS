/**
   A servlet that demonstrates how a multi-tier system is built using
   servlets, JSP, and beans. Note that the servlet requires some
   Java Enterprise edition API, a compatible web server, and
   an installed database driver as given in CustomerServletConfig.xml
   On Tomcat place in folder webapps\ROOT\WEB-INF\classes\multitier so
   has URL http://localhost:8080/servlet/multitier.CustomerServlet
   @author Andrew Ensor
*/
package multiteir;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.Properties;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomerServlet extends HttpServlet
{
   private Connection conn;
   private PreparedStatement stmt;

   public CustomerServlet()
      throws SQLException, ClassNotFoundException, IOException
   {  // obtain database parameters from configuration file
      Properties properties = new Properties();
      properties.loadFromXML(getClass().getResourceAsStream
         ("CustomerServletConfig.xml"));
      String dbDriver = properties.get("dbDriver").toString();
      String dbUrl = properties.get("dbUrl").toString();
      String dbTable = properties.get("dbTable").toString();
      String dbFirstNameAtt = 
         properties.get("dbFirstNameAtt").toString();
      String dbLastNameAtt = 
         properties.get("dbLastNameAtt").toString();
      
      
//      String dbBirthDateAtt = properties.get("dbBirthDateAtt").toString();// Birthday!!
      
      
      
      
      String userName = properties.get("user").toString();
      String password = properties.get("password").toString();
      // connect to the database and create a prepared statement
      Class.forName(dbDriver);
      conn = DriverManager.getConnection(dbUrl, userName, password);
      stmt = conn.prepareStatement("SELECT * FROM "+dbTable+
         " WHERE "+dbFirstNameAtt+" = ? AND "+dbLastNameAtt+" = ?");
      
      
      //stmt = conn.prepareStatement("SELECT * FROM "+dbTable+ " WHERE "+dbBirthDateAtt); /// BIRTHDAY
      
   }
   
   // handle the initial HTTP request and check whether client name is
   // in database before passing on request to a JSP
   public void doGet(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException
   {  // obtain the values of the form data automatically URL decoded
      String firstName = request.getParameter("firstname");
      String lastName = request.getParameter("lastname");
      if (firstName==null || lastName==null ||
         firstName.length()==0 || lastName.length()==0)
      {  // show page with form to obtain client name
         RequestDispatcher dispatcher = getServletContext().
            getRequestDispatcher("/CustomerDetails.jsp");
         dispatcher.forward(request, response);
      }
      else
      {  // put client name into a bean
         Customer customer = new Customer();
         customer.setFirstName(firstName);
         customer.setLastName(lastName);
         
  
         
         // check database for name using an SQL command
         boolean customerFound;
         try
         {  synchronized(this) // synchronize access to stmt
            {  stmt.setString(1, firstName);
               stmt.setString(2, lastName);
               ResultSet rs = stmt.executeQuery();
               customerFound = rs.next();//true if there is a record
               
               if (customerFound) {
                   customer.setBirthday(rs.getString("B_Date")); // BIRTHDAY
               }
               
               
            }
         }
         catch (SQLException e)
         {  System.err.println("SQL Exception during query: " + e);
            customerFound = false;
         }
         
         // make customer bean available for session
         HttpSession session = request.getSession(true);
         session.setAttribute("customer", customer);
         
         
         
         
         
         
         
         // pass bean to appropriate page for displaying response
         if (customerFound)
         {  RequestDispatcher dispatcher = getServletContext().
               getRequestDispatcher("/CustomerFound.jsp");
            dispatcher.forward(request, response);
         }
         else
         {  RequestDispatcher dispatcher = getServletContext().
               getRequestDispatcher("/CustomerNotFound.jsp");
            dispatcher.forward(request, response);
         }
      }
   }
   
   public void doPost(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException
   {  doGet(request, response);
   }
   
   public void destroy()
   {  super.destroy();
      // close database connection
      try
      {  if (stmt != null) stmt.close();
         if (conn != null) conn.close();
      }
      catch (SQLException e)
      {  System.err.println("SQL Exception while closing: " + e);
      }
   }
}
