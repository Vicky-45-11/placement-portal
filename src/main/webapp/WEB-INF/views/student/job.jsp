<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="${job.title}" scope="request"/>
<c:set var="nav" value="jobs" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="page-head">
    <div><h1><c:out value="${job.title}"/></h1><p class="muted"><c:out value="${job.companyName}"/> · <c:out value="${job.location}"/></p></div>
    <a class="btn ghost" href="${ctx}/student/jobs">Back to drives</a>
</div>
<div class="grid two">
    <div class="card">
        <h2>About the role</h2>
        <p style="white-space:pre-line"><c:out value="${job.description}"/></p>
        <dl class="kv">
            <dt>Type</dt><dd>${job.jobTypeLabel}</dd>
            <dt>Package</dt><dd>${job.ctcLpa} LPA</dd>
            <dt>Apply by</dt><dd>${job.deadlineText}</dd>
            <dt>Status</dt><dd><span class="badge st-${job.status}">${job.status}</span></dd>
        </dl>
    </div>
    <div class="card">
        <h2>Eligibility</h2>
        <dl class="kv">
            <dt>Minimum CGPA</dt><dd>${job.minCgpa} <span class="muted small">(yours: ${me.cgpa})</span></dd>
            <dt>Backlogs allowed</dt><dd>${job.maxBacklogs} <span class="muted small">(yours: ${me.backlogs})</span></dd>
            <dt>Departments</dt><dd><c:out value="${job.eligibleDepartments}"/> <span class="muted small">(yours: ${me.department})</span></dd>
        </dl>
        <div style="margin-top:18px">
        <c:choose>
            <c:when test="${not empty job.applicationStatus}">
                <p>You applied. Current status: <span class="badge st-${job.applicationStatus}">${job.applicationStatus}</span></p>
                <a href="${ctx}/student/applications">Track it in My applications</a>
            </c:when>
            <c:when test="${not empty job.ineligibleReason}"><div class="alert bad">You cannot apply: <c:out value="${job.ineligibleReason}"/>.</div></c:when>
            <c:otherwise>
                <form method="post" action="${ctx}/student/apply">
                    <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="jobId" value="${job.id}">
                    <button class="btn">Apply now</button>
                    <c:if test="${not me.hasResume}"><p class="small pill-note">Upload your resume first.</p></c:if>
                </form>
            </c:otherwise>
        </c:choose>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
