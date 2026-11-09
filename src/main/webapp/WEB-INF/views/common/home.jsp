<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Welcome" scope="request"/>
<%@ include file="header.jspf" %>
<section class="hero">
    <div>
        <h1>One place for every campus placement drive.</h1>
        <p>Students build a profile and apply to drives they are eligible for. Companies post openings and run interviews.
           The placement cell tracks every application through to the final offer.</p>
        <div class="actions" style="margin-top:18px">
            <a class="btn" href="${ctx}/register">Register as student</a>
            <a class="btn ghost" href="${ctx}/register?type=company">Register a company</a>
        </div>
    </div>
    <div class="grid stats" style="margin:0">
        <div class="stat"><div class="label">Students</div><div class="value">${stats.students}</div></div>
        <div class="stat"><div class="label">Placed</div><div class="value">${stats.placedPercent}%</div><div class="sub">${stats.placed} students</div></div>
        <div class="stat"><div class="label">Recruiters</div><div class="value">${stats.companies}</div></div>
        <div class="stat"><div class="label">Highest package</div><div class="value">${stats.highestCtc}</div><div class="sub">LPA</div></div>
    </div>
</section>

<h2>Open drives</h2>
<div class="job-list">
    <c:forEach items="${jobs}" var="j">
        <div class="card job" style="margin:0">
            <div>
                <h3><c:out value="${j.title}"/> <span class="muted">· <c:out value="${j.companyName}"/></span></h3>
                <div class="meta"><span>${j.jobTypeLabel}</span><span>${j.ctcLpa} LPA</span><span><c:out value="${j.location}"/></span>
                    <span>Min CGPA ${j.minCgpa}</span><span>Apply by ${j.deadlineText}</span></div>
            </div>
            <a class="btn sm ghost" href="${ctx}/login">Log in to apply</a>
        </div>
    </c:forEach>
    <c:if test="${empty jobs}"><div class="card empty">No open drives right now.</div></c:if>
</div>
<%@ include file="footer.jspf" %>
