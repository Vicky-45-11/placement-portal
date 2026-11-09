<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Drives" scope="request"/>
<c:set var="nav" value="jobs" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="page-head"><div><span class="eyebrow">All companies</span><h1>Drives</h1><p>Close a drive to stop new applications. Deleting a drive also deletes its applications.</p></div><a class="btn accent" href="${ctx}/admin/job/new">New drive</a></div>
<div class="card table-wrap">
<table>
    <thead><tr><th>Role</th><th>Company</th><th class="num">LPA</th><th>Eligibility</th><th>Deadline</th><th class="num">Applicants</th><th>Status</th><th></th></tr></thead>
    <tbody>
    <c:forEach items="${jobs}" var="j">
        <tr><td><strong><c:out value="${j.title}"/></strong><div class="small muted">${j.jobTypeLabel}</div></td>
            <td><c:out value="${j.companyName}"/></td><td class="num">${j.ctcLpa}</td>
            <td class="small">CGPA ${j.minCgpa}+, ≤${j.maxBacklogs} backlogs<br><c:out value="${j.eligibleDepartments}"/></td>
            <td>${j.deadlineText}</td><td class="num">${j.applicantCount}</td>
            <td><span class="badge st-${j.status}">${j.status}</span></td>
            <td><div class="actions">
                <a class="btn sm" href="${ctx}/admin/applicants?job=${j.id}">Applicants</a>
                <a class="btn sm ghost" href="${ctx}/admin/job/edit?id=${j.id}">Edit</a>
                <form method="post" action="${ctx}/admin/job/status" class="inline">
                    <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${j.id}">
                    <button class="btn sm ghost">${j.status == 'OPEN' ? 'Close' : 'Reopen'}</button></form>
                <form method="post" action="${ctx}/admin/job/delete" class="inline" onsubmit="return confirm('Delete this drive and its applications?')">
                    <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${j.id}">
                    <button class="btn sm danger">Delete</button></form>
            </div></td></tr>
    </c:forEach>
    </tbody>
</table>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
