<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Applications" scope="request"/>
<c:set var="nav" value="applications" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="page-head"><div><span class="eyebrow">${fn:length(applications)} shown</span><h1>Applications</h1><p>Change a status here and the student sees it right away. Choosing SELECTED records the placement.</p></div></div>
<div class="tabs">
    <a href="${ctx}/admin/applications" class="${empty param.status ? 'on' : ''}">All</a>
    <c:forEach items="${statuses}" var="s"><a href="${ctx}/admin/applications?status=${s}" class="${param.status == s ? 'on' : ''}">${s}</a></c:forEach>
</div>
<div class="card table-wrap">
<table>
    <thead><tr><th>Student</th><th>Drive</th><th>Applied</th><th>Status</th></tr></thead>
    <tbody>
    <c:forEach items="${applications}" var="a">
        <tr><td><a href="${ctx}/admin/student?id=${a.studentId}"><c:out value="${a.studentName}"/></a><div class="small muted"><c:out value="${a.rollNo}"/> · ${a.department} · CGPA ${a.cgpa}</div></td>
            <td><a href="${ctx}/admin/applicants?job=${a.jobId}"><c:out value="${a.jobTitle}"/></a><div class="small muted"><c:out value="${a.companyName}"/> · ${a.ctcLpa} LPA</div></td>
            <td class="small">${a.appliedAtText}</td>
            <td><form method="post" action="${ctx}/admin/application/status" class="actions">
                <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${a.id}">
                <input type="hidden" name="back" value="/admin/applications?status=${param.status}">
                <select name="status" style="width:auto">
                    <c:forEach items="${statuses}" var="s"><option ${s == a.status ? 'selected' : ''}>${s}</option></c:forEach>
                </select><button class="btn sm ghost">Save</button></form></td></tr>
    </c:forEach>
    </tbody>
</table>
<c:if test="${empty applications}"><p class="empty">No applications.</p></c:if>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
