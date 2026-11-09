<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Applicants" scope="request"/>
<c:set var="nav" value="jobs" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<c:set var="back" value="/${area}/applicants?job=${job.id}"/>
<div class="page-head">
    <div><h1>Applicants · <c:out value="${job.title}"/></h1>
        <p class="muted"><c:out value="${job.companyName}"/> · ${job.ctcLpa} LPA · ${fn:length(applications)} applicant(s) · <span class="badge st-${job.status}">${job.status}</span></p></div>
    <a class="btn ghost" href="${ctx}/${area}/jobs">Back to drives</a>
</div>
<c:forEach items="${applications}" var="a">
<div class="card">
    <div class="job">
        <div>
            <h3><c:out value="${a.studentName}"/> <span class="muted small"><c:out value="${a.rollNo}"/> · ${a.department}</span></h3>
            <div class="meta"><span>CGPA ${a.cgpa}</span><span>${a.backlogs} backlog(s)</span><span><c:out value="${a.studentEmail}"/></span><span>Applied ${a.appliedAtText}</span></div>
            <div class="small" style="margin-top:4px"><c:out value="${a.skills}"/></div>
            <c:if test="${a.hasResume}"><a class="small" href="${ctx}/resume?student=${a.studentId}" target="_blank">View resume (PDF)</a></c:if>
        </div>
        <form method="post" action="${ctx}/${area}/application/status" class="actions">
            <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${a.id}"><input type="hidden" name="back" value="${back}">
            <span class="badge st-${a.status}">${a.status}</span>
            <select name="status" style="width:auto">
                <c:forEach items="${statuses}" var="s"><option ${s == a.status ? 'selected' : ''}>${s}</option></c:forEach>
            </select>
            <button class="btn sm">Update</button>
        </form>
    </div>
    <c:if test="${not empty a.interviews}">
        <ul class="timeline">
        <c:forEach items="${a.interviews}" var="i">
            <li>
                <strong><c:out value="${i.roundName}"/></strong> · ${i.scheduledAtText} · ${i.mode} · <c:out value="${i.venue}"/>
                <form method="post" action="${ctx}/${area}/interview/result" class="inline">
                    <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${i.id}">
                    <select name="result" style="width:auto;padding:3px 6px">
                        <c:forEach items="PENDING,PASSED,FAILED" var="r"><option ${r == i.result ? 'selected' : ''}>${r}</option></c:forEach>
                    </select>
                    <input type="text" name="notes" value="<c:out value='${i.notes}'/>" placeholder="Notes" style="width:180px;padding:3px 6px">
                    <button class="btn sm ghost">Save</button>
                </form>
            </li>
        </c:forEach>
        </ul>
    </c:if>
    <c:if test="${a.status != 'SELECTED' and a.status != 'REJECTED'}">
    <details class="panel"><summary>Schedule an interview round</summary>
        <form method="post" action="${ctx}/${area}/interview/schedule" class="stack">
            <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="applicationId" value="${a.id}">
            <div class="row">
                <div><label>Round</label><input type="text" name="roundName" placeholder="Technical, HR..." required></div>
                <div><label>Date and time</label><input type="datetime-local" name="scheduledAt" required></div>
            </div>
            <div class="row">
                <div><label>Mode</label><select name="mode"><option>OFFLINE</option><option>ONLINE</option></select></div>
                <div><label>Venue or meeting link</label><input type="text" name="venue"></div>
            </div>
            <div style="margin-top:12px"><button class="btn sm">Schedule</button></div>
        </form>
    </details>
    </c:if>
</div>
</c:forEach>
<c:if test="${empty applications}"><div class="card empty">No one has applied yet.</div></c:if>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
