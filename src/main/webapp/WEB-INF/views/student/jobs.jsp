<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Job drives" scope="request"/>
<c:set var="nav" value="jobs" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="page-head"><div><h1>Job drives</h1><p class="muted">Open drives with deadlines still ahead.</p></div></div>
<div class="tabs">
    <a href="${ctx}/student/jobs" class="${param.filter != 'eligible' ? 'on' : ''}">All open drives</a>
    <a href="${ctx}/student/jobs?filter=eligible" class="${param.filter == 'eligible' ? 'on' : ''}">Only ones I am eligible for</a>
</div>
<div class="job-list">
<c:forEach items="${jobs}" var="j">
    <div class="card job" style="margin:0">
        <div>
            <h3><a href="${ctx}/student/job?id=${j.id}"><c:out value="${j.title}"/></a> <span class="muted">· <c:out value="${j.companyName}"/></span></h3>
            <div class="meta"><span>${j.jobTypeLabel}</span><span>${j.ctcLpa} LPA</span><span><c:out value="${j.location}"/></span>
                <span>CGPA ${j.minCgpa}+</span><span><c:out value="${j.eligibleDepartments}"/></span><span>Apply by ${j.deadlineText}</span></div>
            <c:if test="${empty j.applicationStatus and not empty j.ineligibleReason}"><div class="pill-note"><c:out value="${j.ineligibleReason}"/></div></c:if>
        </div>
        <div>
            <c:choose>
                <c:when test="${not empty j.applicationStatus}"><span class="badge st-${j.applicationStatus}">${j.applicationStatus}</span></c:when>
                <c:when test="${empty j.ineligibleReason}"><a class="btn sm" href="${ctx}/student/job?id=${j.id}">View &amp; apply</a></c:when>
                <c:otherwise><span class="badge st-CLOSED">Not eligible</span></c:otherwise>
            </c:choose>
        </div>
    </div>
</c:forEach>
<c:if test="${empty jobs}"><div class="card empty">No drives to show.</div></c:if>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
