<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tiles:insertDefinition name="basepage">
  <tiles:putAttribute name="title" value="Alle Tags"/>
  <tiles:putAttribute name="description" value="Auflistung aller Tags"/>
  <tiles:putAttribute name="content">
    <ul>
      <c:forEach var="tag" items="${tags}"><li><a href="/pictures.html?tag=${tag.id}"><c:out value="${tag}"/></a></li></c:forEach>
    </ul>
  </tiles:putAttribute>
</tiles:insertDefinition>
