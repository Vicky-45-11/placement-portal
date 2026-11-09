<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Change password" scope="request"/>
<c:set var="nav" value="account" scope="request"/>
<%@ include file="header.jspf" %>
<div class="page-head"><div><h1>Change password</h1></div></div>
<div class="card" style="max-width:460px">
    <form method="post" action="${ctx}/account" class="stack">
        <input type="hidden" name="_csrf" value="${csrf}">
        <label>Current password</label><input type="password" name="current" required>
        <label>New password</label><input type="password" name="password" minlength="6" required>
        <label>Confirm new password</label><input type="password" name="confirm" minlength="6" required>
        <div style="margin-top:18px"><button class="btn">Update password</button></div>
    </form>
</div>
<%@ include file="footer.jspf" %>
