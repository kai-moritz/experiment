<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page session="false" %>
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
      <p><a href="/experiment.html"><fmt:message key="experiment"/></a></p>
      <p> Locale = ${pageContext.response.locale} </p>
      <hr>
      <ul>
        <li> <a href="?locale=en_us">us</a> |  <a href="?locale=en_gb">gb</a> | <a href="?locale=es_es">es</a> | <a href="?locale=de_de">de</a> </li>
      </ul>
    </div>
  </body>
</html>
