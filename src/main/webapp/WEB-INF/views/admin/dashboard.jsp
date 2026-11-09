<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Admin dashboard" scope="request"/>
<c:set var="nav" value="dashboard" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="page-head">
    <div><h1>Placement dashboard</h1><p class="muted">Live numbers from the database.</p></div>
    <div class="actions"><a class="btn ghost" href="${ctx}/admin/reports/placements.csv">Export placements (CSV)</a><a class="btn" href="${ctx}/admin/job/new">New drive</a></div>
</div>
<c:if test="${stats.pendingCompanies > 0}">
    <div class="alert bad">${stats.pendingCompanies} recruiter account(s) waiting for approval. <a href="${ctx}/admin/companies">Review</a></div>
</c:if>
<div class="grid stats">
    <div class="stat"><div class="label">Students</div><div class="value">${stats.students}</div></div>
    <div class="stat"><div class="label">Placed</div><div class="value">${stats.placed}</div><div class="sub">${stats.placedPercent}% of students</div></div>
    <div class="stat"><div class="label">Companies</div><div class="value">${stats.companies}</div></div>
    <div class="stat"><div class="label">Open drives</div><div class="value">${stats.openJobs}</div></div>
    <div class="stat"><div class="label">Applications</div><div class="value">${stats.applications}</div></div>
    <div class="stat"><div class="label">Highest / average</div><div class="value">${stats.highestCtc}</div><div class="sub">avg ${stats.averageCtc} LPA</div></div>
</div>
<div class="grid two">
    <div class="card">
        <h2>Department-wise placement</h2>
        <table><thead><tr><th>Dept</th><th class="num">Placed</th><th>Progress</th></tr></thead><tbody>
        <c:forEach items="${byDept}" var="r">
            <tr><td>${r.label}</td><td class="num">${r.placed} / ${r.total}</td>
                <td><div class="bar"><span style="width:${r.percent}%"></span></div><span class="small muted">${r.percent}%</span></td></tr>
        </c:forEach></tbody></table>
    </div>
    <div class="card">
        <h2>Latest applications</h2>
        <table><tbody>
        <c:forEach items="${recent}" var="a">
            <tr><td><c:out value="${a.studentName}"/><div class="small muted"><c:out value="${a.jobTitle}"/> · <c:out value="${a.companyName}"/></div></td>
                <td><span class="badge st-${a.status}">${a.status}</span></td></tr>
        </c:forEach></tbody></table>
        <a class="small" href="${ctx}/admin/applications">All applications</a>
    </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
