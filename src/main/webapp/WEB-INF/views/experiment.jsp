<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
  <head>
    <META http-equiv="Content-Type" content="text/html;charset=UTF-8">
      <title><fmt:message key="welcome.title"/></title>
  </head>
  <body>
    <div class="container">
      <h1>
        <fmt:message key="welcome.title"/>
      </h1>
      <c:if test="${!empty selection}">
        <h2>Ausgewählt: ${selection}</h2>
        <ul>
          <c:forEach var="picture" items="${selection.pictures}"><li>${picture}</li></c:forEach>
        </ul>
        <a href="?clone=${selection.id}">Clone!</a>
      </c:if>
      <h2>Alle</h2>
      <ul>
        <c:forEach var="selection" items="${selections}"><li><a href="?show=${selection.id}">${selection}</a></li></c:forEach>
      </ul>
      <hr>
      <ul>
        <li> <a href="?locale=en_us">us</a> |  <a href="?locale=en_gb">gb</a> | <a href="?locale=es_es">es</a> | <a href="?locale=de_de">de</a> </li>
      </ul>
    </div>
  </body>
</html>
