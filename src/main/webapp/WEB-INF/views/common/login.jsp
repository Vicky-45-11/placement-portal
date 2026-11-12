<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Log in" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="auth-split">
<aside class="pass" aria-hidden="true">
    <div class="pass-main">
        <span class="pass-k">Placement pass</span>
        <span class="pass-title">Season 2025</span>
        <dl>
            <div><dt>Holder</dt><dd>You</dd></div>
            <div><dt>Gate</dt><dd>Drives</dd></div>
            <div><dt>Board by</dt><dd>Deadline</dd></div>
            <div><dt>Destination</dt><dd>Offer letter</dd></div>
        </dl>
        <span class="pass-stamp">Admit one</span>
    </div>
    <div class="pass-stub"><span class="bars"></span><span class="pass-no">PC-2025-0001</span></div>
</aside>
<div class="auth">
    <span class="eyebrow">Welcome back</span>
    <h1>Log in</h1>
    <p class="muted">One login for students, recruiters and the placement cell.</p>
    <div class="card" style="margin-top:18px">
        <form method="post" action="${ctx}/login" class="stack">
            <input type="hidden" name="_csrf" value="${csrf}">
            <label for="email" style="margin-top:0">Email</label>
            <input type="email" id="email" name="email" value="<c:out value='${email}'/>" autocomplete="username" required autofocus>
            <label for="password">Password</label>
            <input type="password" id="password" name="password" autocomplete="current-password" required>
            <div style="margin-top:20px" class="actions"><button class="btn accent" type="submit">Log in</button>
                <span class="small muted">No account? <a href="${ctx}/register">Register</a></span></div>
        </form>
    </div>
    <details class="panel demo"><summary>Trying the demo? Use these sample logins</summary>
        <p style="margin-top:8px">Placement officer: <code>admin@college.edu</code> / <code>admin123</code><br>
        Student: <code>arun@student.college.edu</code> / <code>student123</code><br>
        Recruiter: <code>hr@nimbustech.example</code> / <code>company123</code></p>
    </details>
</div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
