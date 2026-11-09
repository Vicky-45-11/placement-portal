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
<div class="chart-grid">
    <div class="card"><h2>Placement rate</h2>
        <div class="donut">
            <div class="donut-wrap">
                <svg viewBox="0 0 42 42" aria-hidden="true"><circle class="track" cx="21" cy="21" r="15.9" pathLength="100"/><circle class="val" cx="21" cy="21" r="15.9" pathLength="100" style="--pct:${stats.placedPercent}"/></svg>
                <div class="center"><b>${stats.placedPercent}%</b><span>placed</span></div>
            </div>
            <div class="small"><p><strong>${stats.placed}</strong> of ${stats.students} students have an offer.</p>
                <p class="muted">${stats.students - stats.placed} students are still looking.</p></div>
        </div>
    </div>
    <div class="card"><h2>Placed by department</h2>
        <div class="hbars">
            <c:forEach items="${byDept}" var="r" varStatus="st">
                <div class="hbar"><span class="lbl">${r.label}</span>
                    <div class="trk"><i></i><span style="--w:${r.percent}%;--d:${st.index * 0.08}s"></span></div>
                    <span class="num">${r.placed}/${r.total}</span></div>
            </c:forEach>
        </div>
    </div>
</div>
<div class="chart-grid">
    <div class="card"><h2>Application funnel</h2>
        <%@ include file="/WEB-INF/views/common/funnel.jspf" %>
    </div>
    <div class="card"><h2>Offers by package (LPA)</h2>
        <c:set var="bmax" value="1"/>
        <c:forEach items="${bands}" var="b"><c:if test="${b.value > bmax}"><c:set var="bmax" value="${b.value}"/></c:if></c:forEach>
        <div class="columns">
            <c:forEach items="${bands}" var="b" varStatus="st">
                <div class="c"><b>${b.value}</b><span style="--h:${b.value * 100 / bmax}%;--d:${st.index * 0.08}s"></span></div>
            </c:forEach>
        </div>
        <div class="columns-lbl"><c:forEach items="${bands}" var="b"><span>${b.key}</span></c:forEach></div>
    </div>
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
