<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Applicants" scope="request"/>
<c:set var="nav" value="jobs" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<c:set var="back" value="/${area}/applicants?job=${job.id}"/>
<c:set var="best" value="0"/>
<c:forEach items="${applications}" var="a"><c:if test="${a.match.percent > best}"><c:set var="best" value="${a.match.percent}"/></c:if></c:forEach>
<div class="page-head">
    <div><span class="eyebrow">Hiring board · ${fn:length(applications)} applicant(s)</span><h1><c:out value="${job.title}"/></h1>
        <p><c:out value="${job.companyName}"/> · ${job.ctcLpa} LPA · <span class="countdown ${job.closingSoon ? 'soon' : ''}">${job.countdown}</span>
        <c:if test="${not empty job.requiredSkills}"> · looking for <c:out value="${job.requiredSkills}"/></c:if></p></div>
    <a class="btn ghost" href="${ctx}/${area}/jobs">Back to drives</a>
</div>

<div class="tabs viewtabs" data-view-tabs>
    <a href="#board" data-view="board" class="on">Board</a>
    <a href="#details" data-view="details">Details &amp; interviews</a>
</div>

<section data-view="board">
    <p class="small muted" style="margin-top:-6px">Drag a card to another column to change its stage, or use the menu on the card. Moving to SELECTED records the placement.</p>
    <div class="board" data-endpoint="${ctx}/${area}/application/status" data-csrf="${csrf}">
        <c:forEach items="${statuses}" var="s">
            <div class="col" data-status="${s}">
                <div class="col-head"><span class="badge st-${s}">${s}</span><span class="n">0</span></div>
                <div class="col-body">
                    <div class="col-empty">Drop here</div>
                    <c:forEach items="${applications}" var="a">
                        <c:if test="${a.status == s}">
                            <article class="kcard" draggable="true" data-id="${a.id}" data-name="<c:out value='${a.studentName}'/>">
                                <c:if test="${a.match.percent == best and best > 0}"><span class="best">Top match</span></c:if>
                                <div class="who-row">
                                    <span class="avatar"><c:out value="${a.initials}"/></span>
                                    <div style="flex:1;min-width:0"><div class="nm"><c:out value="${a.studentName}"/></div>
                                        <div class="sub"><c:out value="${a.rollNo}"/> · ${a.department} · CGPA ${a.cgpa}</div></div>
                                </div>
                                <c:if test="${not empty a.match.matched}">
                                    <div class="chips"><c:forEach items="${a.match.matched}" var="k"><span class="chip have"><c:out value="${k}"/></span></c:forEach></div>
                                </c:if>
                                <div class="foot">
                                    <div class="mini-match"><c:set var="m" value="${a.match}"/><c:set var="ringSize" value="sm"/>
                                        <%@ include file="/WEB-INF/views/common/ring.jspf" %>
                                        <c:choose><c:when test="${a.hasResume}"><a class="small" href="${ctx}/resume?student=${a.studentId}" target="_blank">Resume</a></c:when>
                                            <c:otherwise><span class="small muted">No CV</span></c:otherwise></c:choose></div>
                                    <select aria-label="Move ${a.studentName} to">
                                        <c:forEach items="${statuses}" var="o"><option ${o == a.status ? 'selected' : ''}>${o}</option></c:forEach>
                                    </select>
                                </div>
                            </article>
                        </c:if>
                    </c:forEach>
                </div>
            </div>
        </c:forEach>
    </div>
</section>

<section data-view="details" hidden>
<c:forEach items="${applications}" var="a">
<div class="card" id="app-${a.id}">
    <div class="job">
        <div class="actions" style="align-items:flex-start;gap:14px">
            <c:set var="m" value="${a.match}"/><c:set var="ringSize" value=""/>
            <div><%@ include file="/WEB-INF/views/common/ring.jspf" %><div class="match-cap">match</div></div>
            <div>
                <h3><c:out value="${a.studentName}"/> <span class="muted small"><c:out value="${a.rollNo}"/> · ${a.department}</span></h3>
                <div class="meta"><span>CGPA ${a.cgpa}</span><span>${a.backlogs} backlog(s)</span><span><c:out value="${a.studentEmail}"/></span><span>Applied ${a.appliedAtText}</span></div>
                <div class="chips">
                    <c:forEach items="${a.match.matched}" var="k"><span class="chip have"><c:out value="${k}"/></span></c:forEach>
                    <c:forEach items="${a.match.missing}" var="k"><span class="chip need"><c:out value="${k}"/></span></c:forEach>
                </div>
                <c:if test="${a.hasResume}"><a class="small" href="${ctx}/resume?student=${a.studentId}" target="_blank">View resume (PDF)</a></c:if>
            </div>
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
    <c:set var="pa" value="${a}"/>
    <%@ include file="/WEB-INF/views/common/pipeline.jspf" %>
    <c:if test="${not empty a.interviews}">
        <ul class="timeline">
        <c:forEach items="${a.interviews}" var="i">
            <li>
                <span class="when">${i.scheduledAtText}</span><strong><c:out value="${i.roundName}"/></strong><span class="small">${i.mode} · <c:out value="${i.venue}"/></span>
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
</section>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
