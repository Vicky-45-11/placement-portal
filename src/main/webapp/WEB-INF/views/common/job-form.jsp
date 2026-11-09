<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="${job.id == 0 ? 'Post a drive' : 'Edit drive'}" scope="request"/>
<c:set var="nav" value="${job.id == 0 ? 'newjob' : 'jobs'}" scope="request"/>
<%@ include file="header.jspf" %>
<div class="page-head"><div><span class="eyebrow">${job.id == 0 ? 'New drive' : 'Edit drive'}</span><h1>${job.id == 0 ? 'Post a placement drive' : job.title}</h1>
    <p class="muted">Eligibility rules are checked automatically when students apply.</p></div></div>
<div class="card">
    <form method="post" action="${ctx}/${area}/job/save" class="stack">
        <input type="hidden" name="_csrf" value="${csrf}">
        <input type="hidden" name="id" value="${job.id}">
        <c:if test="${role == 'ADMIN'}">
            <label>Company</label>
            <select name="companyId" required>
                <c:forEach items="${companies}" var="co">
                    <option value="${co.id}" ${co.id == job.companyId ? 'selected' : ''}><c:out value="${co.name}"/></option>
                </c:forEach>
            </select>
        </c:if>
        <div class="row">
            <div><label>Role title</label><input type="text" name="title" value="<c:out value='${job.title}'/>" required></div>
            <div><label>Type</label>
                <select name="jobType">
                    <option value="FULL_TIME" ${job.jobType == 'FULL_TIME' ? 'selected' : ''}>Full time</option>
                    <option value="INTERNSHIP" ${job.jobType == 'INTERNSHIP' ? 'selected' : ''}>Internship</option>
                </select></div>
        </div>
        <label>Description</label><textarea name="description"><c:out value="${job.description}"/></textarea>
        <div class="row3">
            <div><label>Package (LPA)</label><input type="number" step="0.01" min="0" name="ctcLpa" value="${job.ctcLpa}" required></div>
            <div><label>Location</label><input type="text" name="location" value="<c:out value='${job.location}'/>"></div>
            <div><label>Apply by</label><input type="date" name="deadline" value="${job.deadline}" required></div>
        </div>
        <div class="row3">
            <div><label>Minimum CGPA</label><input type="number" step="0.01" min="0" max="10" name="minCgpa" value="${job.minCgpa}"></div>
            <div><label>Max backlogs allowed</label><input type="number" min="0" name="maxBacklogs" value="${job.maxBacklogs}"></div>
            <div><label>Status</label>
                <select name="status">
                    <option value="OPEN" ${job.status == 'OPEN' ? 'selected' : ''}>Open</option>
                    <option value="CLOSED" ${job.status == 'CLOSED' ? 'selected' : ''}>Closed</option>
                </select></div>
        </div>
        <label>Eligible departments <span class="hint">(tick none or all for every department)</span></label>
        <div class="checks">
            <c:forEach items="${departments}" var="d">
                <label><input type="checkbox" name="departments" value="${d}"
                    ${job.eligibleDepartments != 'ALL' && job.isDepartmentEligible(d) ? 'checked' : ''}> ${d}</label>
            </c:forEach>
        </div>
        <div class="actions" style="margin-top:20px">
            <button class="btn">${job.id == 0 ? 'Post drive' : 'Save changes'}</button>
            <a class="btn ghost" href="${ctx}/${area}/jobs">Cancel</a>
        </div>
    </form>
</div>
<%@ include file="footer.jspf" %>
