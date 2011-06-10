<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<tiles:insertDefinition name="basepage">
  <tiles:putAttribute name="title" value="${page.picture.filename}"/>
  <tiles:putAttribute name="content">
    <p><a href="/pictures/${page.picture.md5sum}.${page.picture.suffix}" title="Original-Bild anzeigen"><img src="/pictures/800/${page.picture.md5sum}.jpg" alt="${page.picture.filename}"/></a></p>
    <fmt:formatDate value="${page.picture.created}" var="year" pattern="yyyy"/>
    <fmt:formatDate value="${page.picture.created}" var="month" pattern="MMMM"/>
    <fmt:formatDate value="${page.picture.created}" var="month_num" pattern="MM"/>
    <fmt:formatDate value="${page.picture.created}" var="week" pattern="w"/>
    <fmt:formatDate value="${page.picture.created}" var="date" pattern="dd"/>
    <ul>
      <li><a href="/pictures.html?year=${year}&amp;month=${month_num}">Alle Bilder aus dem Monat ${month} anzeigen</a></li>
      <li><a href="/pictures.html?year=${year}&amp;week=${week}">Alle Bilder aus der ${week}. Kalenderwoche anzeigen</a></li>
      <li><a href="/pictures.html?year=${year}&amp;month=${month_num}&amp;date=${date}">Alle Bilder vom ${date}. ${month} anzeigen</a></li>
      <c:forEach var="tag" items="${page.picture.tags}"><li><a href="/pictures.html?tag=${tag.id}">Alle Bilder anzeigen, denen das Tag <em><c:out value="${tag}"/></em> zugeordnet ist</a></li></c:forEach>
    </ul>
    <h2>Fotograf</h2>
    <p><c:out value="${page.picture.photographer}"/></p>
    <h2>Aufnahmezeitpunkt</h2>
    <p>${page.picture.created}</p>
    <h2>EXIF-Data</h2>
    <ul>
      <c:forEach var="entry" items="${page.picture.exifData}">
        <li><strong><c:out value="${entry.key}"/></strong>: <c:out value="${entry.value}"/></li>
      </c:forEach>
    </ul>
  </tiles:putAttribute>
</tiles:insertDefinition>
