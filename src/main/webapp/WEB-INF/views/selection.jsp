<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tiles:insertDefinition name="basepage">
  <tiles:putAttribute name="title" value="${selection}"/>
  <tiles:putAttribute name="content">
    <div id="head">
      <h1><tiles:getAsString name="title" ignore="true"/></h1>
      <hr/>
    </div>
    <ul class="fotos">
      <c:forEach var="picture" items="${selection.pictures}">
        <li>
          <a href="/pictures/${picture.md5sum}.html" title="Details zu diesem Bild anzeigen" class="picture"><img src="/pictures/200/${picture.md5sum}.jpg" alt="${picture.filename}"/></a>
          <a href="/selection/remove.html?id=${picture.id}">Verwerfen</a>
          <a href="/selection/move/down.html?id=${picture.id}">&lt;</a>
          <a href="/selection/move/up.html?id=${picture.id}">&gt;</a>
        </li>
      </c:forEach>
    </ul>
    <div id="footer">
      <hr/>
      <ul>
        <li> <a href="?locale=en_us">us</a> |  <a href="?locale=en_gb">gb</a> | <a href="?locale=es_es">es</a> | <a href="?locale=de_de">de</a> </li>
        <li><a href="/selection/sort.html">Bilder sortieren (nach Aufnahme-Zeitpunkt)</a></li>
      </ul>
    </div>
  </tiles:putAttribute>
</tiles:insertDefinition>
