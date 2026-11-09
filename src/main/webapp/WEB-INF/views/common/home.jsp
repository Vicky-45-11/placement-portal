<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Campus placements" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<section class="hero">
    <div>
        <span class="eyebrow">Training &amp; Placement Cell</span>
        <h1>Every drive, every application, <em>every offer</em>, in one register.</h1>
        <p class="lede">Students see only the drives they qualify for. Recruiters shortlist and interview in one place. The placement cell always knows who is placed.</p>
        <div class="actions">
            <a class="btn accent" href="${ctx}/register">I'm a student</a>
            <a class="btn quiet" href="${ctx}/register?type=company">I'm a recruiter</a>
        </div>
    </div>
    <div class="ledger" style="margin:0">
        <div class="cell feature"><div class="label">Placed so far</div><div class="value">${stats.placedPercent}<span class="unit">%</span></div><div class="sub">${stats.placed} of ${stats.students} students</div></div>
        <div class="cell"><div class="label">Best offer</div><div class="value">${stats.highestCtc}<span class="unit">LPA</span></div></div>
        <div class="cell"><div class="label">Recruiters</div><div class="value">${stats.companies}</div></div>
        <div class="cell"><div class="label">Open drives</div><div class="value">${stats.openJobs}</div></div>
    </div>
</section>

<div class="how">
    <div><span class="eyebrow">Step 1</span><b>Build your profile</b><span class="small muted">Add CGPA, backlogs and skills, then upload a PDF resume.</span></div>
    <div><span class="eyebrow">Step 2</span><b>Apply where you qualify</b><span class="small muted">Each drive checks CGPA, backlogs, department and deadline for you.</span></div>
    <div><span class="eyebrow">Step 3</span><b>Track it to the offer</b><span class="small muted">Watch each application move from shortlist to interview to offer.</span></div>
</div>

<div class="page-head" style="margin-bottom:18px"><div><h2 style="margin:0">On the notice board</h2><p>Drives open right now. Log in to check if you're eligible.</p></div></div>
<div class="ticket-list">
    <c:forEach items="${jobs}" var="j">
        <article class="ticket">
            <div class="body">
                <div class="company"><c:out value="${j.companyName}"/></div>
                <h3><c:out value="${j.title}"/></h3>
                <div class="facts"><span>${j.jobTypeLabel}</span><span><c:out value="${j.location}"/></span><span>CGPA <b>${j.minCgpa}+</b></span><span>Closes <b>${j.deadlineText}</b></span></div>
            </div>
            <div class="stub"><div class="pay">${j.ctcLpa}<small>LPA</small></div><a class="btn sm quiet" href="${ctx}/login">Log in to apply</a></div>
        </article>
    </c:forEach>
    <c:if test="${empty jobs}"><div class="card empty">The board is empty right now. Check back soon.</div></c:if>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
