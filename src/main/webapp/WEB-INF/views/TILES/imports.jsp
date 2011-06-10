<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tiles:insertDefinition name="basepage">
  <tiles:putAttribute name="title" value="Alle Importe"/>
  <tiles:putAttribute name="description" value="Auflistung aller Importe"/>
  <tiles:putAttribute name="content">
    <ul>
      <c:forEach var="import" items="${imports}"><li><a href="/imports.html?id=${import.id}"><c:out value="${import}"/></a></li></c:forEach>
    </ul>
  </tiles:putAttribute>
</tiles:insertDefinition>
