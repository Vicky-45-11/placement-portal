<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="My drives" scope="request"/>
<c:set var="nav" value="jobs" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="page-head"><div><h1>My drives</h1></div><a class="btn" href="${ctx}/company/job/new">Post a drive</a></div>
<div class="card table-wrap">
<table>
    <thead><tr><th>Role</th><th>Type</th><th class="num">LPA</th><th>Eligibility</th><th>Deadline</th><th class="num">Applicants</th><th>Status</th><th></th></tr></thead>
    <tbody>
    <c:forEach items="${jobs}" var="j">
        <tr>
            <td><strong><c:out value="${j.title}"/></strong><div class="small muted"><c:out value="${j.location}"/></div></td>
            <td>${j.jobTypeLabel}</td><td class="num">${j.ctcLpa}</td>
            <td class="small">CGPA ${j.minCgpa}+, ≤${j.maxBacklogs} backlogs<br><c:out value="${j.eligibleDepartments}"/></td>
            <td>${j.deadlineText}</td><td class="num">${j.applicantCount}</td>
            <td><span class="badge st-${j.status}">${j.status}</span></td>
            <td><div class="actions">
                <a class="btn sm" href="${ctx}/company/applicants?job=${j.id}">Applicants</a>
                <a class="btn sm ghost" href="${ctx}/company/job/edit?id=${j.id}">Edit</a>
                <form method="post" action="${ctx}/company/job/status" class="inline">
                    <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${j.id}">
                    <button class="btn sm ghost">${j.status == 'OPEN' ? 'Close' : 'Reopen'}</button></form>
            </div></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<c:if test="${empty jobs}"><p class="empty">You have not posted any drives yet.</p></c:if>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
