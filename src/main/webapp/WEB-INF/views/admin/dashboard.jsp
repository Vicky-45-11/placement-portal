<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Admin dashboard" scope="request"/>
<c:set var="nav" value="dashboard" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="page-head">
    <div><span class="eyebrow">Placement officer</span><h1>Season overview</h1><p>Live from the database. Placed means at least one SELECTED offer.</p></div>
    <div class="actions"><a class="btn ghost" href="${ctx}/admin/reports/placements.csv">Export placements (CSV)</a><a class="btn accent" href="${ctx}/admin/job/new">New drive</a></div>
</div>
<%@ include file="/WEB-INF/views/common/next.jspf" %>
<div class="ledger">
    <div class="cell feature"><div class="label">Placement rate</div><div class="value">${stats.placedPercent}<span class="unit">%</span></div><div class="sub">${stats.placed} of ${stats.students} students placed</div></div>
    <div class="cell"><div class="label">Best offer</div><div class="value">${stats.highestCtc}<span class="unit">LPA</span></div><div class="sub">average ${stats.averageCtc}</div></div>
    <div class="cell"><div class="label">Open drives</div><div class="value">${stats.openJobs}</div></div>
    <div class="cell"><div class="label">Applications</div><div class="value">${stats.applications}</div></div>
    <div class="cell"><div class="label">Recruiters</div><div class="value">${stats.companies}</div></div>
</div>
<div class="card"><h2>Where every application stands</h2>
    <%@ include file="/WEB-INF/views/common/funnel.jspf" %>
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
