<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="My applications" scope="request"/>
<c:set var="nav" value="applications" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="page-head"><div><h1>My applications</h1><p class="muted">Status updates from recruiters show up here.</p></div></div>
<c:forEach items="${applications}" var="a">
    <div class="card">
        <div class="job">
            <div>
                <h3><c:out value="${a.jobTitle}"/> <span class="muted">· <c:out value="${a.companyName}"/></span></h3>
                <div class="meta"><span>${a.ctcLpa} LPA</span><span>Applied ${a.appliedAtText}</span><span>Updated ${a.updatedAtText}</span></div>
            </div>
            <div class="actions">
                <span class="badge st-${a.status}">${a.status}</span>
                <c:if test="${a.status == 'APPLIED'}">
                    <form method="post" action="${ctx}/student/withdraw" class="inline" onsubmit="return confirm('Withdraw this application?')">
                        <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${a.id}">
                        <button class="btn sm ghost">Withdraw</button></form>
                </c:if>
            </div>
        </div>
        <c:if test="${not empty a.interviews}">
            <ul class="timeline">
                <c:forEach items="${a.interviews}" var="i">
                    <li><strong><c:out value="${i.roundName}"/></strong> · ${i.scheduledAtText} · ${i.mode} · <c:out value="${i.venue}"/>
                        <span class="badge st-${i.result}">${i.result}</span></li>
                </c:forEach>
            </ul>
        </c:if>
    </div>
</c:forEach>
<c:if test="${empty applications}"><div class="card empty">You have not applied anywhere yet. <a href="${ctx}/student/jobs">Browse drives</a>.</div></c:if>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
