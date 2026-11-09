<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Something went wrong" scope="request"/>
<%@ include file="header.jspf" %>
<div class="card">
    <h1>Something went wrong</h1>
    <p><c:out value="${errorMessage}"/></p>
    <a class="btn ghost" href="javascript:history.back()">Go back</a>
</div>
<%@ include file="footer.jspf" %>
