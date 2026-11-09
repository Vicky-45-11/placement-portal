<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Log in" scope="request"/>
<%@ include file="header.jspf" %>
<div class="card auth-card">
    <h1>Log in</h1>
    <p class="muted">Students, recruiters and the placement cell all sign in here.</p>
    <form method="post" action="${ctx}/login" class="stack">
        <input type="hidden" name="_csrf" value="${csrf}">
        <label for="email">Email</label>
        <input type="email" id="email" name="email" value="<c:out value='${email}'/>" required autofocus>
        <label for="password">Password</label>
        <input type="password" id="password" name="password" required>
        <div style="margin-top:18px"><button class="btn" type="submit">Log in</button></div>
    </form>
    <p class="small muted" style="margin-top:18px">New here? <a href="${ctx}/register">Create an account</a>.</p>
    <details class="panel"><summary>Demo accounts (sample data)</summary>
        <p class="small">Admin: admin@college.edu / admin123<br>
        Student: arun@student.college.edu / student123<br>
        Company: hr@nimbustech.example / company123</p>
    </details>
</div>
<%@ include file="footer.jspf" %>
