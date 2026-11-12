<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="My desk" scope="request"/>
<c:set var="nav" value="dashboard" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="page-head">
    <div><span class="eyebrow"><c:out value="${me.rollNo}"/> · ${me.department} · Batch ${me.batchYear}</span>
        <h1>Hello, <c:out value="${me.name}"/></h1>
        <p>Here is where your placement stands today.</p></div>
    <a class="btn accent" href="${ctx}/student/jobs?filter=eligible">Drives I can apply to (${eligibleCount})</a>
</div>
<%@ include file="/WEB-INF/views/common/next.jspf" %>
<div class="ledger">
    <div class="cell feature"><div class="label">Open to you</div><div class="value">${eligibleCount}</div><div class="sub">eligible, not applied yet</div></div>
    <div class="cell"><div class="label">Applied</div><div class="value">${counts.APPLIED + counts.SHORTLISTED + counts.INTERVIEW + counts.SELECTED + counts.REJECTED}</div></div>
    <div class="cell"><div class="label">In progress</div><div class="value">${counts.SHORTLISTED + counts.INTERVIEW}</div><div class="sub">shortlisted or interviewing</div></div>
    <div class="cell"><div class="label">Offers</div><div class="value">${counts.SELECTED}</div></div>
</div>
<div class="grid side">
    <div>
        <div class="card">
            <div class="ready-head"><h2>Get ready to apply</h2><span class="ready-pct">${me.readiness}%<small>profile ready</small></span></div>
            <div class="meter" role="progressbar" aria-valuenow="${me.readiness}" aria-valuemin="0" aria-valuemax="100" aria-label="Profile readiness"><span style="--w:${me.readiness}%"></span></div>
            <ol class="steps">
                <li class="done">Create your account</li>
                <li class="${me.cgpa > 0 and not empty me.skills ? 'done' : ''}">Fill in CGPA and skills in <a href="${ctx}/student/profile">your profile</a></li>
                <li class="${me.hasResume ? 'done' : ''}">Upload your resume as a PDF<c:if test="${not me.hasResume}"> · <a href="${ctx}/student/profile">upload now</a></c:if></li>
                <li class="${(counts.APPLIED + counts.SHORTLISTED + counts.INTERVIEW + counts.SELECTED + counts.REJECTED) > 0 ? 'done' : ''}">Apply to a <a href="${ctx}/student/jobs?filter=eligible">drive you qualify for</a></li>
            </ol>
        </div>
        <c:if test="${not empty topPicks}">
        <div class="card">
            <h2>Best matches for you</h2>
            <p class="small muted" style="margin-top:-6px">Open drives you qualify for and haven't applied to, ranked by skill match.</p>
            <ul class="picks">
                <c:forEach items="${topPicks}" var="j">
                    <li><c:set var="m" value="${j.match}"/><c:set var="ringSize" value="sm"/><%@ include file="/WEB-INF/views/common/ring.jspf" %>
                        <div class="pk"><a href="${ctx}/student/job?id=${j.id}"><c:out value="${j.title}"/></a>
                            <span class="small muted"><c:out value="${j.companyName}"/> · ${j.ctcLpa} LPA</span></div>
                        <span class="countdown ${j.closingSoon ? 'soon' : ''}">${j.countdown}</span></li>
                </c:forEach>
            </ul>
        </div>
        </c:if>
        <div class="card">
            <h2>Coming up</h2>
            <c:forEach items="${upcoming}" var="i">
                <div class="notice"><span class="when">${i.scheduledAtText} · ${i.mode}</span><br>
                    <strong><c:out value="${i.roundName}"/> round</strong> with <c:out value="${i.companyName}"/> for <c:out value="${i.jobTitle}"/><br>
                    <span class="small muted"><c:out value="${i.venue}"/></span></div>
            </c:forEach>
            <c:if test="${empty upcoming}"><p class="muted">No interviews scheduled yet.</p></c:if>
        </div>
    </div>
    <div>
        <h2>Notice board</h2>
        <c:forEach items="${announcements}" var="a">
            <div class="pin"><strong><c:out value="${a.title}"/></strong><p class="small" style="margin:4px 0"><c:out value="${a.body}"/></p><span class="small muted">${a.createdAtText}</span></div>
        </c:forEach>
        <c:if test="${empty announcements}"><p class="muted">Nothing pinned by the placement cell.</p></c:if>
    </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
