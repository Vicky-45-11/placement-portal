<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Interviews" scope="request"/>
<c:set var="nav" value="interviews" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="page-head"><div><span class="eyebrow">Schedule</span><h1>Interviews</h1><p>Every round across every drive, latest first.</p></div></div>
<%@ include file="/WEB-INF/views/common/calendar.jspf" %>
<h2 style="margin-top:8px">All rounds</h2>
<div class="card table-wrap">
<table>
    <thead><tr><th>When</th><th>Student</th><th>Company / drive</th><th>Round</th><th>Mode / venue</th><th>Result</th></tr></thead>
    <tbody>
    <c:forEach items="${interviews}" var="i">
        <tr><td>${i.scheduledAtText}<c:if test="${i.upcoming}"> <span class="badge">Upcoming</span></c:if></td>
            <td><c:out value="${i.studentName}"/><div class="small muted"><c:out value="${i.rollNo}"/></div></td>
            <td><c:out value="${i.companyName}"/><div class="small muted"><c:out value="${i.jobTitle}"/></div></td>
            <td><c:out value="${i.roundName}"/></td><td>${i.mode}<div class="small muted"><c:out value="${i.venue}"/></div></td>
            <td><span class="badge st-${i.result}">${i.result}</span></td></tr>
    </c:forEach>
    </tbody>
</table>
<c:if test="${empty interviews}"><p class="empty">No interviews scheduled.</p></c:if>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
