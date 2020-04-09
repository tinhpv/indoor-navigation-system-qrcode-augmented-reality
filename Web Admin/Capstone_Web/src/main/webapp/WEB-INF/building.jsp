<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Building Page</title>
<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.7.0/css/all.css">
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/building.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/stepper.css">
<script src="https://code.jquery.com/jquery-3.4.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>
</head>
<body>
	<div class="menu-bar">
		<div id="front-page">INQR</div>
		<hr>
		<div id="menu-bar-content">
			<a href="${pageContext.request.contextPath}/">
				<i class="fas fa-list-alt"> Dashboard</i>
			</a>
		</div>
		<hr>
	</div>
	<div class="content">
		<div id="content-header" class="shadow p-3 mb-3 bg-white rounded">
			<c:if test="${not empty sessionScope.building }">
				<p>
					<strong>${sessionScope.building.name }</strong>
				</p>
				<div id="content-header-action">
					<form action="${pageContext.request.contextPath}/building/updateBuilding" method="post">
						<button class="btn btn-outline-success" type="submit">
							<i class="fas fa-file-upload"> Update building to server</i>
						</button>
					</form>
				</div>
			</c:if>
			<c:if test="${empty sessionScope.building }">
				<p>
					<strong> Create a new building for ${requestScope.companyName }</strong>
				</p>
			</c:if>
		</div>

		<div class="wrapper-progressBar">
			<ul class="progressBar">
				<li class="active">Building's Information Management</li>
				<li>Building's Data Management</li>
			</ul>
		</div>

		<c:if test="${empty sessionScope.building }">
			<div id="content-body-buidling" class="shadow p-3 mb-5 bg-white rounded">
				<form action="${pageContext.request.contextPath}/building/createBuilding" method="post">
					<div id="form-header" class="list-group-item">
						<h5>Enter building information</h5>
					</div>
					<div class="form-group row">
						<label for="name" class="col-sm-3 col-form-label">Building name: </label>
						<div class="col-sm-5">
							<input type="text" class="form-control" id="name" name="name" placeholder="Ex: FPT Q.9 HCM"
								required="required" value="${sessionScope.building.name }" />
						</div>
						<p class="col-sm-1">*</p>
					</div>
					<div class="form-group row">
						<label for="description" class="col-sm-3 col-form-label">Description: </label>
						<div class="col-sm-7">
							<textarea class="form-control" id="description" name="description" required="required">Ex: This building is ...</textarea>
						</div>
						<p class="col-sm-1">*</p>
					</div>
					<div class="form-group row">
						<label for="dayExpired" class="col-sm-3 col-form-label">Expired date: </label>
						<div class="col-sm-5">
							<input type="date" class="form-control" id="dayExpired-1" name="dayExpired"
								placeholder="Expired Day" value="${sessionScope.building.dayExpired }" />
							<small id="idHelp" class="form-text text-muted">By default, expired date is 30 days
								from now.</small>
						</div>
						<p class="col-sm-1">*</p>
					</div>
					<input type="hidden" name="companyId" value="${requestScope.companyId }">
					<button type="submit" class="btn btn-primary">Create building</button>
				</form>
			</div>
		</c:if>

		<c:if test="${not empty sessionScope.building }">
			<div id="content-body-buidling" class="shadow p-3 mb-3 bg-white rounded">
				<form action="${pageContext.request.contextPath}/building/edit" method="post">
					<input type="hidden" class="form-control" id="id" name="id" placeholder="Building ID"
						readonly="readonly" value="${sessionScope.building.id }" />
					<div id="form-header" class="list-group-item">
						<h5>Information of ${sessionScope.building.name}</h5>
					</div>
					<div class="form-group row">
						<label for="name" class="col-sm-3 col-form-label">Building name: </label>
						<div class="col-sm-5">
							<input type="text" class="form-control" id="name" name="name" placeholder="Building Name"
								required="required" value="${sessionScope.building.name }" />
						</div>
						<p class="col-sm-1">*</p>
					</div>
					<div class="form-group row">
						<label for="description" class="col-sm-3 col-form-label">Description: </label>
						<div class="col-sm-7">
							<textarea class="form-control" id="description" name="description" required="required">${sessionScope.building.description }</textarea>
						</div>
						<p class="col-sm-1">*</p>
					</div>
					<div class="form-group row">
						<label for="dayExpired" class="col-sm-3 col-form-label">Expired date: </label>
						<div class="col-sm-5">
							<input type="date" class="form-control" id="dayExpired" name="dayExpired"
								placeholder="Expired Day" value="${sessionScope.building.dayExpired }" />
							<small id="idHelp" class="form-text text-muted">By default, expired date is 30 days
								from now.</small>
						</div>
						<p class="col-sm-1">*</p>
					</div>
					<div class="form-group row">
						<div class="col-sm-3">Building status</div>
						<div class="col-sm-7">
							<div class="form-check">
								<c:if test="${sessionScope.building.active }">
									<input type="checkbox" class="form-check-input" value="true" id="gridCheck1"
										checked="checked" name="active" />
									<small id="activeHelp" class="form-text text-muted">Mobile users will see this
										building.</small>
								</c:if>
								<c:if test="${not sessionScope.building.active }">
									<input type="checkbox" class="form-check-input" value="true" id="gridCheck2" name="active" />
									<small id="activeHelp" class="form-text text-muted">Mobile users won't be able to
										see this building.</small>
								</c:if>
							</div>
						</div>
					</div>
					<%-- <div class="form-group form-check">
						<c:if test="${sessionScope.building.active }">
							<input type="checkbox" class="form-check-input" value="true" id="active" checked="checked"
								name="active" />
						</c:if>
						<c:if test="${not sessionScope.building.active }">
							<input type="checkbox" class="form-check-input" value="true" id="active" name="active" />
						</c:if>
						<label for="active" class="form-check-label">Active building</label>
						<small id="activeHelp" class="form-text text-muted">Mobile user won't be able to see
							inactive building.</small>
					</div> --%>
					<a style="float: right;" href="${pageContext.request.contextPath}/floor/"
						class="btn btn-outline-success">
						Next
						<i class="fas fa-arrow-circle-right"></i>
					</a>
					<button style="float: right; margin-right: 1%;" class="btn btn-primary" type="submit">Save
						changes</button>
				</form>
				<p style="color: green;">${requestScope.updateSuccess }</p>
				<p style="color: green;">${requestScope.createSuccess }</p>
				<p style="color: green;">${requestScope.updateMessage }</p>
			</div>
		</c:if>
	</div>

	<script>
		window.onload = function() {
			var tomorrowLocal = new Date(new Date().getTime() + 24 * 60 * 60
					* 1000 * 30 - new Date().getTimezoneOffset() * 60 * 1000)
					.toISOString().substr(0, 10);
			document.getElementById('dayExpired-1').value = tomorrowLocal;
		}
	</script>
</body>
</html>