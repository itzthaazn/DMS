<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
   <TITLE>SimpleJSP</TITLE>
</HEAD>
<BODY>
   <H3>Simple Example of a JavaServer Page</H3>
   <%-- The following is an example of a JSP page directive --%>
   <%@ page import = "java.util.Date" %>
   
   <%-- The following is an example of a JSP declaration --%>
   <%!
   private int numVisits = 0; // servlet field
   %>
   
   <%-- The following is an example of a JSP scriplet --%>
   <%
   String firstName = request.getParameter("firstname");
   String lastName = request.getParameter("lastname");
   if (firstName==null && lastName==null)
      out.println("Hello stranger");
   else if (lastName==null)
      out.println("Hi " + firstName);
   else if (firstName==null)
      out.println("Welcome " + lastName);
   else
      out.println("Hello " + firstName + " " + lastName);
   %>
   
   <%-- The following are examples of JSP expressions --%>
   <P>Current time is <%= new Date() %></P>
   <P>This page has been visited <%= numVisits++ %> times since
   server was started</P>
   <P>Session ID is <%= session.getId() %></P>
</BODY>
</HTML>
