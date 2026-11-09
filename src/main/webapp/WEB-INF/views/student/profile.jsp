<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Profile" scope="request"/>
<c:set var="nav" value="profile" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="page-head"><div><h1>Profile &amp; resume</h1><p class="muted">Companies see this when you apply.</p></div></div>
<div class="grid two">
    <div class="card">
        <h2>Details</h2>
        <form method="post" action="${ctx}/student/profile" class="stack">
            <input type="hidden" name="_csrf" value="${csrf}">
            <label>Full name</label><input type="text" name="name" value="<c:out value='${me.name}'/>" required>
            <div class="row">
                <div><label>Roll number</label><input type="text" value="<c:out value='${me.rollNo}'/>" disabled></div>
                <div><label>Email</label><input type="email" value="<c:out value='${me.email}'/>" disabled></div>
            </div>
            <div class="row">
                <div><label>Department</label>
                    <select name="department"><c:forEach items="${departments}" var="d"><option ${me.department == d ? 'selected' : ''}>${d}</option></c:forEach></select></div>
                <div><label>Graduation year</label><input type="number" name="batchYear" value="${me.batchYear}"></div>
            </div>
            <div class="row">
                <div><label>CGPA</label><input type="number" step="0.01" min="0" max="10" name="cgpa" value="${me.cgpa}" required></div>
                <div><label>Active backlogs</label><input type="number" min="0" name="backlogs" value="${me.backlogs}"></div>
            </div>
            <label>Phone</label><input type="text" name="phone" value="<c:out value='${me.phone}'/>">
            <label>Skills</label><textarea name="skills"><c:out value="${me.skills}"/></textarea>
            <div style="margin-top:16px"><button class="btn">Save profile</button></div>
        </form>
    </div>
    <div class="card">
        <h2>Resume</h2>
        <c:choose>
            <c:when test="${me.hasResume}"><p>Current resume: <a href="${ctx}/resume?student=${me.id}" target="_blank">View PDF</a></p></c:when>
            <c:otherwise><p class="muted">No resume uploaded yet. You need one to apply.</p></c:otherwise>
        </c:choose>
        <form method="post" action="${ctx}/student/resume?_csrf=${csrf}" enctype="multipart/form-data" class="stack">
            <label>Upload a PDF <span class="hint">(max 2 MB, replaces the current one)</span></label>
            <input type="file" name="resume" accept="application/pdf" required>
            <div style="margin-top:16px"><button class="btn">Upload resume</button></div>
        </form>
    </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
