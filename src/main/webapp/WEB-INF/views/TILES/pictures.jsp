<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="/WEB-INF/tlds/fotos.xml" prefix="f" %>
<tiles:insertDefinition name="basepage">
  <tiles:putAttribute name="title" value="${page}"/>
  <tiles:putAttribute name="content">
    <div id="head">
      <h1><tiles:getAsString name="title" ignore="true"/></h1>
      <hr/>
      <f:calendar info="${page}"/>
      <hr/>
    </div>
    <ul class="fotos">
      <c:forEach var="picture" items="${page.pictures}">
        <li><f:link info="${page}" picture="${picture}" title="Bild anzeigen" css="picture"><img src="/pictures/200/${picture.md5sum}.jpg" alt="${picture.filename}"/></f:link></li>
      </c:forEach>
    </ul>
    <div id="footer">
      <hr/>
      <ul>
        <li> <a href="?locale=en_us">us</a> |  <a href="?locale=en_gb">gb</a> | <a href="?locale=es_es">es</a> | <a href="?locale=de_de">de</a> </li>
      </ul>
    </div>
  </tiles:putAttribute>
</tiles:insertDefinition>
