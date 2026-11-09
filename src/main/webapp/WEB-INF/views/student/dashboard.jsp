<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Dashboard" scope="request"/>
<c:set var="nav" value="dashboard" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="page-head">
    <div><h1>Hello, <c:out value="${me.name}"/></h1>
        <p class="muted"><c:out value="${me.rollNo}"/> · ${me.department} · CGPA ${me.cgpa}</p></div>
    <a class="btn" href="${ctx}/student/jobs?filter=eligible">See drives I can apply to</a>
</div>
<c:if test="${me.placed}">
    <div class="alert ok">Congratulations! You are placed at <strong><c:out value="${me.placedCompany}"/></strong> with ${me.placedCtc} LPA.</div>
</c:if>
<c:if test="${not me.hasResume}">
    <div class="alert bad">You have not uploaded a resume yet. <a href="${ctx}/student/profile">Upload it</a> so you can apply to drives.</div>
</c:if>
<div class="grid stats">
    <div class="stat"><div class="label">Open for you</div><div class="value">${eligibleCount}</div><div class="sub">eligible drives not applied</div></div>
    <div class="stat"><div class="label">Applied</div><div class="value">${counts.APPLIED + counts.SHORTLISTED + counts.INTERVIEW + counts.SELECTED + counts.REJECTED}</div></div>
    <div class="stat"><div class="label">Shortlisted / interview</div><div class="value">${counts.SHORTLISTED + counts.INTERVIEW}</div></div>
    <div class="stat"><div class="label">Offers</div><div class="value">${counts.SELECTED}</div></div>
</div>
<div class="grid two">
    <div class="card">
        <h2>Upcoming interviews</h2>
        <c:forEach items="${upcoming}" var="i">
            <div class="notice"><strong><c:out value="${i.roundName}"/></strong> · <c:out value="${i.companyName}"/> (<c:out value="${i.jobTitle}"/>)<br>
                <span class="small muted">${i.scheduledAtText} · ${i.mode} · <c:out value="${i.venue}"/></span></div>
        </c:forEach>
        <c:if test="${empty upcoming}"><p class="muted">No interviews scheduled.</p></c:if>
    </div>
    <div class="card">
        <h2>Announcements</h2>
        <c:forEach items="${announcements}" var="a">
            <div class="notice"><strong><c:out value="${a.title}"/></strong><br><span class="small"><c:out value="${a.body}"/></span><br>
                <span class="small muted">${a.createdAtText}</span></div>
        </c:forEach>
        <c:if test="${empty announcements}"><p class="muted">Nothing new from the placement cell.</p></c:if>
    </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
