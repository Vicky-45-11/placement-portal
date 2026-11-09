<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<%@ page isErrorPage="true" %>
<c:set var="code" value="${requestScope['jakarta.servlet.error.status_code']}"/>
<c:set var="pageTitle" value="Error ${code}" scope="request"/>
<%@ include file="header.jspf" %>
<div class="card">
    <h1>${code == 404 ? 'Page not found' : code == 403 ? 'Not allowed' : 'Something went wrong'}</h1>
    <p class="muted"><c:out value="${requestScope['jakarta.servlet.error.message']}"/></p>
    <a class="btn ghost" href="${ctx}/">Go to home</a>
</div>
<%@ include file="footer.jspf" %>
