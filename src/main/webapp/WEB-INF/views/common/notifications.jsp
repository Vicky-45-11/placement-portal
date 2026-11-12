<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Notifications" scope="request"/>
<c:set var="nav" value="notifications" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="page-head">
    <div><span class="eyebrow">Inbox</span><h1>Notifications</h1>
        <p>Every change that affects you: status moves, interview rounds, new applicants and notices. Click one to open the page it is about.</p></div>
    <c:if test="${bellUnread > 0}">
        <form method="post" action="${ctx}/notifications/read-all"><input type="hidden" name="_csrf" value="${sessionScope.csrf}"><button class="btn ghost">Mark all as read</button></form>
    </c:if>
</div>
<div class="card flush">
    <ul class="inbox">
        <c:forEach items="${items}" var="n">
            <li class="${n.read ? '' : 'unread'}">
                <a href="${ctx}/notifications/open?id=${n.id}">
                    <span class="dot" aria-hidden="true"></span>
                    <span class="txt"><c:out value="${n.body}"/></span>
                    <span class="ago">${n.ago}</span>
                </a>
            </li>
        </c:forEach>
    </ul>
    <c:if test="${empty items}"><p class="empty">Nothing yet. You'll see updates here as soon as something changes.</p></c:if>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
