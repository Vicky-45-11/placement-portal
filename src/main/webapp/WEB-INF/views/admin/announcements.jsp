<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Announcements" scope="request"/>
<c:set var="nav" value="announcements" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="page-head"><div><span class="eyebrow">Notice board</span><h1>Notices</h1><p>Pinned on every student's desk, newest first.</p></div></div>
<div class="grid two">
    <div class="card"><h2>Post a notice</h2>
        <form method="post" action="${ctx}/admin/announcements" class="stack">
            <input type="hidden" name="_csrf" value="${csrf}">
            <label>Title</label><input type="text" name="title" maxlength="200" required>
            <label>Message</label><textarea name="body" required></textarea>
            <div style="margin-top:14px"><button class="btn">Post</button></div>
        </form>
    </div>
    <div class="card"><h2>Recent</h2>
        <c:forEach items="${announcements}" var="a">
            <div class="notice">
                <strong><c:out value="${a.title}"/></strong><br><span class="small"><c:out value="${a.body}"/></span><br>
                <span class="small muted">${a.createdAtText}</span>
                <form method="post" action="${ctx}/admin/announcement/delete" class="inline">
                    <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${a.id}">
                    <button class="link-btn small" style="color:var(--bad)">Delete</button></form>
            </div>
        </c:forEach>
        <c:if test="${empty announcements}"><p class="muted">None yet.</p></c:if>
    </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
