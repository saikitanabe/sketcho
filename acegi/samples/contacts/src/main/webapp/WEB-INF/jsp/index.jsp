<%@ include file="/WEB-INF/jsp/include.jsp" %>

<html>
<head><title>Your Contacts</title></head>
<body>
<h1><authz:authentication operation="username"/>'s Contacts</h1>
<P>
<table cellpadding=3 border=0>
<tr><td><b>id</b></td><td><b>Name</b></td><td><b>Email</b></td></tr>
<c:forEach var="contact" items="${model.contacts}">
  <tr>
  <td>
      <c:out value="${contact.id}"/>
  </td>
  <td>
      <c:out value="${contact.name}"/>
  </td>
  <td>
      <c:out value="${contact.email}"/>
  </td>
  <authz:accesscontrollist domainObject="${contact}" hasPermission="8,16">
    <td><A HREF="<c:url value="del.htm"><c:param name="contactId" value="${contact.id}"/></c:url>">Del</A></td>
  </authz:accesscontrollist>
  <authz:accesscontrollist domainObject="${contact}" hasPermission="16">
    <td><A HREF="<c:url value="adminPermission.htm"><c:param name="contactId" value="${contact.id}"/></c:url>">Admin Permission</A></td>
  </authz:accesscontrollist>
  </tr>
</c:forEach>
</table>
<p><a href="<c:url value="add.htm"/>">Add</a>   <p><a href="<c:url value="../j_acegi_logout"/>">Logoff</a> (also clears any remember-me cookie)
</body>
</html>
