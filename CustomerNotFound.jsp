<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
   <TITLE>CustomerNotFound</TITLE>
</HEAD>
<BODY>
   <H3>Customer Not Found</H3>
   <%-- Displays a response using customer bean properties --%>
   <jsp:useBean id="customer" class="multiteir.Customer"
      scope="session" />

   <P>The customer
   <jsp:getProperty name="customer" property="fullName" />
   was not found on the database.</P>
   
   <P><A HREF="CustomerDetails.jsp">Check another customer</A></P>
</BODY>
</HTML>
