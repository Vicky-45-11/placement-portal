<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Reports" scope="request"/>
<c:set var="nav" value="reports" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="page-head"><div><span class="eyebrow">Reports</span><h1>Placement report</h1><p>Placed means a student has at least one SELECTED offer. Packages are in lakhs per annum.</p></div>
    <a class="btn" href="${ctx}/admin/reports/placements.csv">Download placements CSV</a></div>
<div class="ledger">
    <div class="cell"><div class="label">Placement rate</div><div class="value">${stats.placedPercent}%</div><div class="sub">${stats.placed} of ${stats.students}</div></div>
    <div class="cell"><div class="label">Highest package</div><div class="value">${stats.highestCtc}</div><div class="sub">LPA</div></div>
    <div class="cell"><div class="label">Average package</div><div class="value">${stats.averageCtc}</div><div class="sub">LPA</div></div>
    <div class="cell"><div class="label">Upcoming interviews</div><div class="value">${stats.upcomingInterviews}</div></div>
</div>
<div class="card table-wrap"><h2>By department</h2>
<table><thead><tr><th>Department</th><th class="num">Students</th><th class="num">Placed</th><th>Rate</th><th class="num">Avg LPA</th><th class="num">Highest LPA</th></tr></thead><tbody>
<c:forEach items="${byDept}" var="r">
    <tr><td>${r.label}</td><td class="num">${r.total}</td><td class="num">${r.placed}</td>
        <td><div class="bar"><span style="width:${r.percent}%"></span></div><span class="small muted">${r.percent}%</span></td>
        <td class="num">${empty r.avgCtcRounded ? '–' : r.avgCtcRounded}</td><td class="num">${empty r.maxCtc ? '–' : r.maxCtc}</td></tr>
</c:forEach></tbody></table></div>
<div class="card table-wrap"><h2>By company</h2>
<table><thead><tr><th>Company</th><th class="num">Applications</th><th class="num">Offers</th><th class="num">Avg LPA</th><th class="num">Highest LPA</th></tr></thead><tbody>
<c:forEach items="${byCompany}" var="r">
    <tr><td><c:out value="${r.label}"/></td><td class="num">${r.total}</td><td class="num">${r.placed}</td>
        <td class="num">${empty r.avgCtcRounded ? '–' : r.avgCtcRounded}</td><td class="num">${empty r.maxCtc ? '–' : r.maxCtc}</td></tr>
</c:forEach></tbody></table></div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
