<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
   <TITLE>CustomerDetails</TITLE>
</HEAD>
<BODY>
   <H3>Customer Details</H3>
   <%-- Obtains the customer first name and last name using a form
   and sends request to CustomerServlet.java --%>
   <%@ page import = "javax.servlet.RequestDispatcher" %>
   <%
   String firstName = request.getParameter("firstname");
   String lastName = request.getParameter("lastname");
   if (firstName == null)
      firstName = "";
   if (lastName == null)
      lastName = "";
   if (firstName.length()>0 && lastName.length()>0)
   {  // there is no need to display form as name already provided
      RequestDispatcher dispatcher = getServletContext().
         getRequestDispatcher("/CustomerServlet");
      dispatcher.forward(request, response);
   }
   // else prepare appropriate instructions for form
   out.print("<P><B>Please enter ");
   if (firstName.length()==0)
   {  out.print("first name");
      if (lastName.length()==0)
         out.print(" and ");
   }
   if (lastName.length()==0)
   {  out.print("last name");
   }
   out.println("</B></P>");
   %>

   <FORM ACTION=
      "http://localhost:8080/Multiteir/CustomerServlet">
      <P>First name:
      <INPUT TYPE="TEXT" NAME="firstname" VALUE="<%= firstName %>"></P>
      <P>Last name:
      <INPUT TYPE="TEXT" NAME="lastname" VALUE="<%= lastName %>"></P>
      <INPUT TYPE="SUBMIT">
   </FORM>
</BODY>
</HTML>
