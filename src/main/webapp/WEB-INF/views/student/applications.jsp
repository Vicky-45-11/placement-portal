<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="My applications" scope="request"/>
<c:set var="nav" value="applications" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="page-head"><div><span class="eyebrow">Tracker</span><h1>My applications</h1><p>Recruiters move you along this track. Interview rounds appear under each one.</p></div></div>
<c:forEach items="${applications}" var="a">
    <div class="card">
        <div class="actions" style="justify-content:space-between;align-items:flex-start">
            <div>
                <div class="small muted mono" style="text-transform:uppercase;letter-spacing:.06em"><c:out value="${a.companyName}"/></div>
                <h3><c:out value="${a.jobTitle}"/> <span class="muted">· ${a.ctcLpa} LPA</span></h3>
                <div class="small muted">Applied ${a.appliedAtText} · last update ${a.updatedAtText}</div>
            </div>
            <div class="actions">
                <span class="badge st-${a.status}">${a.status}</span>
                <c:if test="${a.status == 'APPLIED'}">
                    <form method="post" action="${ctx}/student/withdraw" class="inline" onsubmit="return confirm('Withdraw this application?')">
                        <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${a.id}">
                        <button class="btn sm quiet">Withdraw</button></form>
                </c:if>
            </div>
        </div>
        <c:set var="pa" value="${a}"/>
        <%@ include file="/WEB-INF/views/common/pipeline.jspf" %>
        <c:if test="${not empty a.interviews}">
            <ul class="timeline">
                <c:forEach items="${a.interviews}" var="i">
                    <li><span class="when">${i.scheduledAtText}</span><strong><c:out value="${i.roundName}"/></strong><span class="small">${i.mode} · <c:out value="${i.venue}"/></span>
                        <span class="badge st-${i.result}">${i.result}</span></li>
                </c:forEach>
            </ul>
        </c:if>
    </div>
</c:forEach>
<c:if test="${empty applications}"><div class="card empty">Nothing yet. <a href="${ctx}/student/jobs?filter=eligible">Find a drive you qualify for</a>.</div></c:if>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
