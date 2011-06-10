<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tiles:insertDefinition name="basepage">
  <tiles:putAttribute name="title" value="${import}"/>
  <tiles:putAttribute name="description" value="Alle Bilder aus dem Import ${import}"/>
  <tiles:putAttribute name="content">
    <div id="head">
      <h1><tiles:getAsString name="title" ignore="true"/></h1>
      <hr/>
    </div>
    <ul class="fotos">
      <c:forEach var="picture" items="${import.pictures}">
        <li><a href="/pictures/${picture.md5sum}.html" title="Details zu diesem Bild anzeigen" class="picture"><img src="/pictures/200/${picture.md5sum}.jpg" alt="${picture.filename}"/></a></li>
      </c:forEach>
    </ul>
    <div id="footer">
      <hr/>
      <ul>
        <li> <a href="?locale=en_us">us</a> |  <a href="?locale=en_gb">gb</a> | <a href="?locale=es_es">es</a> | <a href="?locale=de_de">de</a> </li>
        <li><a href="/selection/clone.html?id=${import.id}">In Auswahl verwandeln</a></li>
      </ul>
    </div>
  </tiles:putAttribute>
</tiles:insertDefinition>
