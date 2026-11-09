<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="${student.name}" scope="request"/>
<c:set var="nav" value="students" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="page-head">
    <div><h1><c:out value="${student.name}"/></h1><p class="muted"><c:out value="${student.rollNo}"/> · ${student.department} · Batch ${student.batchYear}</p></div>
    <div class="actions">
        <a class="btn ghost" href="${ctx}/admin/students">Back</a>
        <form method="post" action="${ctx}/admin/student/delete" class="inline" onsubmit="return confirm('Delete this student and all their applications?')">
            <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${student.id}">
            <button class="btn danger">Delete student</button></form>
    </div>
</div>
<div class="grid two">
    <div class="card"><h2>Profile</h2>
        <dl class="kv">
            <dt>Email</dt><dd><c:out value="${student.email}"/></dd>
            <dt>Phone</dt><dd><c:out value="${student.phone}"/></dd>
            <dt>CGPA</dt><dd>${student.cgpa}</dd>
            <dt>Backlogs</dt><dd>${student.backlogs}</dd>
            <dt>Skills</dt><dd><c:out value="${student.skills}"/></dd>
            <dt>Resume</dt><dd><c:choose><c:when test="${student.hasResume}"><a href="${ctx}/resume?student=${student.id}" target="_blank">View PDF</a></c:when><c:otherwise>Not uploaded</c:otherwise></c:choose></dd>
            <dt>Placement</dt><dd><c:choose><c:when test="${student.placed}"><c:out value="${student.placedCompany}"/> · ${student.placedCtc} LPA</c:when><c:otherwise>Not placed</c:otherwise></c:choose></dd>
        </dl>
    </div>
    <div class="card"><h2>Applications</h2>
        <table><tbody>
        <c:forEach items="${applications}" var="a">
            <tr><td><c:out value="${a.jobTitle}"/><div class="small muted"><c:out value="${a.companyName}"/> · ${a.appliedAtText}</div></td>
                <td><span class="badge st-${a.status}">${a.status}</span></td></tr>
        </c:forEach></tbody></table>
        <c:if test="${empty applications}"><p class="muted">No applications.</p></c:if>
    </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
