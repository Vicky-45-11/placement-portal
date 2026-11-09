<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Companies" scope="request"/>
<c:set var="nav" value="companies" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="page-head"><div><h1>Companies</h1><p class="muted">Approve new recruiters before they can post drives.</p></div></div>
<div class="card table-wrap">
<table>
    <thead><tr><th>Company</th><th>Industry</th><th>Contact</th><th class="num">Drives</th><th class="num">Hired</th><th>Account</th><th></th></tr></thead>
    <tbody>
    <c:forEach items="${companies}" var="co">
        <tr><td><strong><c:out value="${co.name}"/></strong><div class="small muted"><c:out value="${co.location}"/></div></td>
            <td><c:out value="${co.industry}"/></td><td class="small"><c:out value="${co.email}"/></td>
            <td class="num">${co.jobCount}</td><td class="num">${co.hiredCount}</td>
            <td><span class="badge st-${co.active ? 'ACTIVE' : 'PENDING'}">${co.active ? 'Active' : 'Pending'}</span></td>
            <td><form method="post" action="${ctx}/admin/company/toggle" class="inline">
                <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${co.id}">
                <button class="btn sm ${co.active ? 'ghost' : ''}">${co.active ? 'Disable' : 'Approve'}</button></form></td></tr>
    </c:forEach>
    </tbody>
</table>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
