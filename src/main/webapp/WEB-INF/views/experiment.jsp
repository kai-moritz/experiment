<%@page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
  <head>
    <title>Experiment</title>
  </head>
  <body>
    <h1>Experiment</h1>
    <c:if test="${!empty selection}">
      <h2>Selected: ${selection}</h2>
      <ul>
        <c:forEach var="picture" items="${selection.pictures}"><li>${picture}</li></c:forEach>
      </ul>
      <a href="?clone=${selection.id}">Clone this selection!</a>
    </c:if>
    <h2>All selections:</h2>
    <ul>
      <c:forEach var="selection" items="${selections}"><li><a href="?show=${selection.id}">${selection}</a></li></c:forEach>
    </ul>
  </body>
</html>
