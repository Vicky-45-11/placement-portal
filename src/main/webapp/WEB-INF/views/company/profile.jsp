<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Company profile" scope="request"/>
<c:set var="nav" value="profile" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="page-head"><div><span class="eyebrow">Your company</span><h1>Company profile</h1><p>Students see this on your drives.</p></div></div>
<div class="card" style="max-width:720px">
    <form method="post" action="${ctx}/company/profile" class="stack">
        <input type="hidden" name="_csrf" value="${csrf}">
        <div class="row">
            <div><label>Company name</label><input type="text" name="name" value="<c:out value='${me.name}'/>" required></div>
            <div><label>Industry</label><input type="text" name="industry" value="<c:out value='${me.industry}'/>"></div>
        </div>
        <div class="row">
            <div><label>Website</label><input type="url" name="website" value="<c:out value='${me.website}'/>"></div>
            <div><label>Location</label><input type="text" name="location" value="<c:out value='${me.location}'/>"></div>
        </div>
        <label>About</label><textarea name="description"><c:out value="${me.description}"/></textarea>
        <div style="margin-top:16px"><button class="btn">Save</button></div>
    </form>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
