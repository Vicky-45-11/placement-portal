<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Campus placements" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<section class="hero">
    <div>
        <span class="eyebrow">Training &amp; Placement Cell</span>
        <h1>Every drive, every application, <em class="swash">every offer<svg viewBox="0 0 300 24" preserveAspectRatio="none" aria-hidden="true"><path d="M4 16 C 60 6, 120 4, 180 9 S 270 18, 296 7" /></svg></em>, in one register.</h1>
        <p class="lede">Students see only the drives they qualify for. Recruiters shortlist and interview in one place. The placement cell always knows who is placed.</p>
        <div class="actions">
            <a class="btn accent" href="${ctx}/register">I'm a student</a>
            <a class="btn quiet" href="${ctx}/register?type=company">I'm a recruiter</a>
        </div>
    </div>
    <div class="hero-board">
    <span class="tape t1" aria-hidden="true"></span><span class="tape t2" aria-hidden="true"></span>
    <span class="rubber" aria-hidden="true">Season<br><b>2025</b><br>open</span>
    <div class="ledger" style="margin:0">
        <div class="cell feature"><div class="label">Placed so far</div><div class="value">${stats.placedPercent}<span class="unit">%</span></div><div class="sub">${stats.placed} of ${stats.students} students</div></div>
        <div class="cell"><div class="label">Best offer</div><div class="value">${stats.highestCtc}<span class="unit">LPA</span></div></div>
        <div class="cell"><div class="label">Recruiters</div><div class="value">${stats.companies}</div></div>
        <div class="cell"><div class="label">Open drives</div><div class="value">${stats.openJobs}</div></div>
    </div>
    </div>
</section>

<c:if test="${not empty offers}">
<div class="offerwall" aria-label="Recent offers">
    <div class="track">
        <c:forEach begin="1" end="${fn:length(offers) < 4 ? 4 : 2}" var="rep">
            <c:forEach items="${offers}" var="o">
                <span class="o"><i>Offer</i><c:out value="${o[0]}"/> (${o[1]}) → <c:out value="${o[2]}"/><b>${o[3]} LPA</b></span>
            </c:forEach>
        </c:forEach>
    </div>
</div>
</c:if>
<div class="how steps-3">
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
                <div class="facts"><span class="countdown ${j.closingSoon ? 'soon' : ''}">${j.countdown}</span><span>${j.jobTypeLabel}</span><span><c:out value="${j.location}"/></span><span>CGPA <b>${j.minCgpa}+</b></span><span>Closes <b>${j.deadlineText}</b></span></div>
            </div>
            <div class="stub"><div class="pay">${j.ctcLpa}<small>LPA</small></div><a class="btn sm quiet" href="${ctx}/login">Log in to apply</a></div>
        </article>
    </c:forEach>
    <c:if test="${empty jobs}"><div class="card empty">The board is empty right now. Check back soon.</div></c:if>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
