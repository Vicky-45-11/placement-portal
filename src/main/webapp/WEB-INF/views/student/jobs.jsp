<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Drives" scope="request"/>
<c:set var="nav" value="jobs" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="page-head"><div><span class="eyebrow">Notice board</span><h1>Placement drives</h1>
    <p>Every drive that is still taking applications. Each one shows whether you meet its rules.</p></div></div>
<div class="tabs">
    <a href="${ctx}/student/jobs" class="${param.filter != 'eligible' ? 'on' : ''}">All open drives</a>
    <a href="${ctx}/student/jobs?filter=eligible" class="${param.filter == 'eligible' ? 'on' : ''}">Only ones I qualify for</a>
</div>
<div class="ticket-list">
<c:forEach items="${jobs}" var="j">
    <article class="ticket">
        <div class="body">
            <div class="company"><c:out value="${j.companyName}"/> · ${j.jobTypeLabel}</div>
            <h3><a href="${ctx}/student/job?id=${j.id}"><c:out value="${j.title}"/></a></h3>
            <div class="facts"><span><c:out value="${j.location}"/></span><span>CGPA <b>${j.minCgpa}+</b></span><span>Backlogs <b>≤ ${j.maxBacklogs}</b></span>
                <span>For <b><c:out value="${j.eligibleDepartments}"/></b></span><span>Closes <b>${j.deadlineText}</b></span></div>
            <c:choose>
                <c:when test="${not empty j.applicationStatus}"></c:when>
                <c:when test="${empty j.ineligibleReason}"><div class="eligibility yes">You meet every rule for this drive</div></c:when>
                <c:otherwise><div class="eligibility no"><c:out value="${j.ineligibleReason}"/></div></c:otherwise>
            </c:choose>
        </div>
        <div class="stub">
            <div class="pay">${j.ctcLpa}<small>LPA</small></div>
            <c:choose>
                <c:when test="${not empty j.applicationStatus}"><span class="badge st-${j.applicationStatus}">${j.applicationStatus}</span></c:when>
                <c:when test="${empty j.ineligibleReason}"><a class="btn sm accent" href="${ctx}/student/job?id=${j.id}">View &amp; apply</a></c:when>
                <c:otherwise><a class="btn sm quiet" href="${ctx}/student/job?id=${j.id}">Details</a></c:otherwise>
            </c:choose>
        </div>
    </article>
</c:forEach>
<c:if test="${empty jobs}"><div class="card empty">No drives match right now.</div></c:if>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
