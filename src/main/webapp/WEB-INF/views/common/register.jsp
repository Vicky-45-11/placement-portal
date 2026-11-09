<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Register" scope="request"/>
<%@ include file="header.jspf" %>
<c:set var="t" value="${not empty type ? type : param.type}"/>
<div class="auth wide">
    <span class="eyebrow">New account</span><h1>Create an account</h1>
    <div class="tabs" style="margin-top:12px">
        <a href="${ctx}/register" class="${t != 'company' ? 'on' : ''}">I am a student</a>
        <a href="${ctx}/register?type=company" class="${t == 'company' ? 'on' : ''}">I am a recruiter</a>
    </div>
    <form method="post" action="${ctx}/register" class="stack card">
        <input type="hidden" name="_csrf" value="${csrf}">
        <input type="hidden" name="type" value="${t == 'company' ? 'company' : 'student'}">
        <div class="row">
            <div><label>${t == 'company' ? 'Your name (HR contact)' : 'Full name'}</label>
                <input type="text" name="name" value="<c:out value='${param.name}'/>" required></div>
            <div><label>Email</label><input type="email" name="email" value="<c:out value='${param.email}'/>" required></div>
        </div>
        <div class="row">
            <div><label>Password <span class="hint">(6+ characters)</span></label><input type="password" name="password" minlength="6" required></div>
            <div><label>Confirm password</label><input type="password" name="confirm" minlength="6" required></div>
        </div>
        <c:choose>
        <c:when test="${t == 'company'}">
            <div class="row">
                <div><label>Company name</label><input type="text" name="companyName" value="<c:out value='${param.companyName}'/>" required></div>
                <div><label>Industry</label><input type="text" name="industry" value="<c:out value='${param.industry}'/>"></div>
            </div>
            <div class="row">
                <div><label>Website</label><input type="url" name="website" placeholder="https://" value="<c:out value='${param.website}'/>"></div>
                <div><label>Location</label><input type="text" name="location" value="<c:out value='${param.location}'/>"></div>
            </div>
            <label>About the company</label><textarea name="description"><c:out value="${param.description}"/></textarea>
            <p class="small muted">The placement cell approves recruiter accounts before they can log in.</p>
        </c:when>
        <c:otherwise>
            <div class="row3">
                <div><label>Roll number</label><input type="text" name="rollNo" value="<c:out value='${param.rollNo}'/>" required></div>
                <div><label>Department</label>
                    <select name="department" required>
                        <option value="">Choose</option>
                        <c:forEach items="${departments}" var="d"><option ${param.department == d ? 'selected' : ''}>${d}</option></c:forEach>
                    </select></div>
                <div><label>Graduation year</label><input type="number" name="batchYear" min="2000" max="2100" value="${empty param.batchYear ? 2026 : param.batchYear}" required></div>
            </div>
            <div class="row3">
                <div><label>CGPA</label><input type="number" name="cgpa" step="0.01" min="0" max="10" value="<c:out value='${param.cgpa}'/>" required></div>
                <div><label>Active backlogs</label><input type="number" name="backlogs" min="0" value="${empty param.backlogs ? 0 : param.backlogs}"></div>
                <div><label>Phone</label><input type="text" name="phone" value="<c:out value='${param.phone}'/>"></div>
            </div>
            <label>Skills <span class="hint">(comma separated)</span></label>
            <input type="text" name="skills" value="<c:out value='${param.skills}'/>" placeholder="Java, SQL, React">
        </c:otherwise>
        </c:choose>
        <div style="margin-top:18px"><button class="btn accent" type="submit">Create account</button></div>
    </form>
</div>
<%@ include file="footer.jspf" %>
