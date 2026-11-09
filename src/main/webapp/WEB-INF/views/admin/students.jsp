<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Students" scope="request"/>
<c:set var="nav" value="students" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="page-head"><div><span class="eyebrow">${fn:length(students)} shown</span><h1>Students</h1><p>Search by name or roll number, or filter by department and placement.</p></div></div>
<form class="filters" method="get" action="${ctx}/admin/students">
    <input type="search" name="q" value="<c:out value='${param.q}'/>" placeholder="Name or roll no">
    <select name="dept"><option value="">All departments</option>
        <c:forEach items="${departments}" var="d"><option ${param.dept == d ? 'selected' : ''}>${d}</option></c:forEach></select>
    <select name="placed">
        <option value="">Placed or not</option>
        <option value="yes" ${param.placed == 'yes' ? 'selected' : ''}>Placed</option>
        <option value="no" ${param.placed == 'no' ? 'selected' : ''}>Not placed</option>
    </select>
    <button class="btn">Filter</button>
</form>
<div class="card table-wrap">
<table>
    <thead><tr><th>Roll no</th><th>Name</th><th>Dept</th><th class="num">CGPA</th><th class="num">Backlogs</th><th>Resume</th><th>Placement</th></tr></thead>
    <tbody>
    <c:forEach items="${students}" var="s">
        <tr><td><c:out value="${s.rollNo}"/></td>
            <td><a href="${ctx}/admin/student?id=${s.id}"><c:out value="${s.name}"/></a><div class="small muted"><c:out value="${s.email}"/></div></td>
            <td>${s.department}</td><td class="num">${s.cgpa}</td><td class="num">${s.backlogs}</td>
            <td><c:choose><c:when test="${s.hasResume}"><a href="${ctx}/resume?student=${s.id}" target="_blank">PDF</a></c:when><c:otherwise><span class="muted">None</span></c:otherwise></c:choose></td>
            <td><c:choose><c:when test="${s.placed}"><span class="badge st-SELECTED"><c:out value="${s.placedCompany}"/> · ${s.placedCtc}</span></c:when><c:otherwise><span class="muted">Not yet</span></c:otherwise></c:choose></td></tr>
    </c:forEach>
    </tbody>
</table>
<c:if test="${empty students}"><p class="empty">No students match.</p></c:if>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
