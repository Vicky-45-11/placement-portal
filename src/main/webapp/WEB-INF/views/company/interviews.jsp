<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Interviews" scope="request"/>
<c:set var="nav" value="interviews" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="page-head"><div><h1>Interviews</h1><p class="muted">Every round you have scheduled. Record results from a drive's applicants page.</p></div></div>
<div class="card table-wrap">
<table>
    <thead><tr><th>When</th><th>Student</th><th>Drive</th><th>Round</th><th>Mode / venue</th><th>Result</th></tr></thead>
    <tbody>
    <c:forEach items="${interviews}" var="i">
        <tr><td>${i.scheduledAtText}</td><td><c:out value="${i.studentName}"/><div class="small muted"><c:out value="${i.rollNo}"/></div></td>
            <td><c:out value="${i.jobTitle}"/></td><td><c:out value="${i.roundName}"/></td>
            <td>${i.mode}<div class="small muted"><c:out value="${i.venue}"/></div></td>
            <td><span class="badge st-${i.result}">${i.result}</span></td></tr>
    </c:forEach>
    </tbody>
</table>
<c:if test="${empty interviews}"><p class="empty">No interviews yet.</p></c:if>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
