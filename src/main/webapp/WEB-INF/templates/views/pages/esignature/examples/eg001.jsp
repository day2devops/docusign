<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../../../partials/head.jsp"/>


<div style="padding: 10px">

    <h2>A document requires your signature</h2>

    <form class="eg" action="" method="post" data-busy="form">
        <div class="form-group">
            <input type="hidden" class="form-control" id="signerEmail" name="signerEmail"
                   aria-describedby="emailHelp" placeholder="pat@example.com" required
                   value="${locals.dsConfig.signerEmail}">
        </div>
        <div class="form-group">
            <input type="hidden" class="form-control" id="signerName" placeholder="Pat Johnson" name="signerName"
                   value="${locals.dsConfig.signerName}" required>
        </div>
        <input type="hidden" name="_csrf" value="${csrfToken}">
        <input type="hidden" name="uuid" value="${param.uuid}"/>

        <button type="submit" class="btn btn-docu">Open Document</button>
    </form>
</div>

<jsp:include page="../../../partials/foot.jsp"/>
