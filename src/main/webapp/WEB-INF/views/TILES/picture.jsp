<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="/WEB-INF/tlds/fotos.xml" prefix="f" %>
<tiles:insertDefinition name="basepage">
  <tiles:putAttribute name="title" value="${page.picture.filename}"/>
  <tiles:putAttribute name="content">
    <div id="head" class="einzelansicht">
      <h1><tiles:getAsString name="title" ignore="true"/></h1>
      <hr/>
      <f:calendar info="${page}"/>
      <hr/>
    </div>
    <div id="picture"><a href="/pictures/${page.picture.md5sum}.html" title="Details zu dem Bild anzeigen"><img src="/pictures/800/${page.picture.md5sum}.jpg" alt="${page.picture.filename}" class="w${page.width} h${page.height}"/></a></div>
    <div id="footer" class="einzelansicht">
      <hr/>
      <ul>
        <li> <a href="?locale=en_us">us</a> |  <a href="?locale=en_gb">gb</a> | <a href="?locale=es_es">es</a> | <a href="?locale=de_de">de</a> </li>
        <li><a href="/selection/add.html?id=${page.picture.id}">Merken</a></li>
      </ul>
    </div>
    <ul class="fotos einzelansicht">
      <c:forEach var="picture" items="${page.pictures}">
        <li><a name="_${picture.id}"></a><f:link info="${page}" picture="${picture}" title="Bild anzeigen" css="picture"><img src="/pictures/200/${picture.md5sum}.jpg" alt="${picture.filename}"/></f:link></li>
      </c:forEach>
    </ul>
  </tiles:putAttribute>
</tiles:insertDefinition>
