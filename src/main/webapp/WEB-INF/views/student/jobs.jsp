<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Drives" scope="request"/>
<c:set var="nav" value="jobs" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="page-head"><div><span class="eyebrow">Notice board</span><h1>Placement drives</h1>
    <p>Every drive still taking applications. The ring shows how well you match: the skills the recruiter asked for that are on your profile, plus how far your CGPA clears their cut-off.</p></div></div>
<div class="tabs">
    <a href="${ctx}/student/jobs" class="${param.filter != 'eligible' ? 'on' : ''}">All open drives</a>
    <a href="${ctx}/student/jobs?filter=eligible" class="${param.filter == 'eligible' ? 'on' : ''}">Only ones I qualify for</a>
</div>
<div class="toolbar">
    <input type="search" id="q" placeholder="Search role, company, skill, city" aria-label="Search drives">
    <select id="sort" aria-label="Sort drives">
        <option value="match">Best match first</option>
        <option value="pay">Highest package first</option>
        <option value="deadline">Closing soonest first</option>
    </select>
    <span class="count"></span>
</div>
<div class="ticket-list" data-filter-list>
<c:forEach items="${jobs}" var="j">
    <article class="ticket" data-pay="${j.ctcLpa}" data-days="${j.daysLeft}" data-match="${j.match.percent}"
             data-text="<c:out value='${fn:toLowerCase(j.title)} ${fn:toLowerCase(j.companyName)} ${fn:toLowerCase(j.location)} ${fn:toLowerCase(j.requiredSkills)} ${fn:toLowerCase(j.eligibleDepartments)}'/>">
        <div class="body">
            <div class="topline">
                <div class="company"><c:out value="${j.companyName}"/> · ${j.jobTypeLabel}</div>
                <span class="countdown ${j.closingSoon ? 'soon' : ''}">${j.countdown}</span>
            </div>
            <h3><a href="${ctx}/student/job?id=${j.id}"><c:out value="${j.title}"/></a></h3>
            <div class="facts"><span><c:out value="${j.location}"/></span><span>CGPA <b>${j.minCgpa}+</b></span><span>Backlogs <b>≤ ${j.maxBacklogs}</b></span>
                <span>For <b><c:out value="${j.eligibleDepartments}"/></b></span></div>
            <c:if test="${not empty j.requiredSkillList}">
                <div class="chips">
                    <c:forEach items="${j.match.matched}" var="k"><span class="chip have"><c:out value="${k}"/></span></c:forEach>
                    <c:forEach items="${j.match.missing}" var="k"><span class="chip need" title="Not on your profile yet"><c:out value="${k}"/></span></c:forEach>
                </div>
            </c:if>
            <c:choose>
                <c:when test="${not empty j.applicationStatus}"></c:when>
                <c:when test="${empty j.ineligibleReason}"><div class="eligibility yes">You meet every rule for this drive</div></c:when>
                <c:otherwise><div class="eligibility no"><c:out value="${j.ineligibleReason}"/></div></c:otherwise>
            </c:choose>
        </div>
        <div class="stub">
            <div><c:set var="m" value="${j.match}"/><c:set var="ringSize" value=""/><%@ include file="/WEB-INF/views/common/ring.jspf" %><div class="match-cap">match</div></div>
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
