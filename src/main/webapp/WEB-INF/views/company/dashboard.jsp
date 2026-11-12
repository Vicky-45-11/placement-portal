<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Dashboard" scope="request"/>
<c:set var="nav" value="dashboard" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="page-head">
    <div><span class="eyebrow">Recruiter overview</span><h1><c:out value="${me.name}"/></h1><p class="muted"><c:out value="${me.industry}"/> · <c:out value="${me.location}"/></p></div>
    <a class="btn accent" href="${ctx}/company/job/new">Post a drive</a>
</div>
<%@ include file="/WEB-INF/views/common/next.jspf" %>
<div class="ledger">
    <div class="cell"><div class="label">Drives posted</div><div class="value">${me.jobCount}</div></div>
    <div class="cell"><div class="label">Applicants</div><div class="value">${applicantTotal}</div></div>
    <div class="cell"><div class="label">Upcoming interviews</div><div class="value">${fn:length(interviews)}</div></div>
    <div class="cell"><div class="label">Students hired</div><div class="value">${me.hiredCount}</div></div>
</div>
<div class="grid two">
    <div class="card">
        <h2>Your drives</h2>
        <table><thead><tr><th>Role</th><th class="num">Applicants</th><th>Status</th></tr></thead><tbody>
        <c:forEach items="${jobs}" var="j">
            <tr><td><a href="${ctx}/company/applicants?job=${j.id}"><c:out value="${j.title}"/></a><div class="small muted">Apply by ${j.deadlineText}</div></td>
                <td class="num">${j.applicantCount}<c:if test="${waiting[j.id] > 0}"> <span class="new-pill" title="Not reviewed yet">${waiting[j.id]} new</span></c:if></td><td><span class="badge st-${j.status}">${j.status}</span></td></tr>
        </c:forEach>
        </tbody></table>
        <c:if test="${empty jobs}"><p class="empty">No drives yet.</p></c:if>
    </div>
    <div class="card">
        <h2>Upcoming interviews</h2>
        <c:forEach items="${interviews}" var="i">
            <div class="notice"><strong><c:out value="${i.studentName}"/></strong> · <c:out value="${i.roundName}"/> for <c:out value="${i.jobTitle}"/><br>
                <span class="small muted">${i.scheduledAtText} · ${i.mode} · <c:out value="${i.venue}"/></span></div>
        </c:forEach>
        <c:if test="${empty interviews}"><p class="muted">Nothing scheduled.</p></c:if>
    </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
