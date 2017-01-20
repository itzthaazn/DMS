<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
   <TITLE>Customer Birthday</TITLE>
</HEAD>
<BODY>
   <H3>Customer Birthday</H3>
   <%-- Displays a response using customer bean properties --%>
   <jsp:useBean id="customer" class="multiteir.Customer"
      scope="session" />

   <P>The customers Birthday is
   <jsp:getProperty name="customer" property="birthday" />
   </P>
   
   <P><A HREF="CustomerDetails.jsp"> Find Birthday of another Customer</A></P>
</BODY>
</HTML>
