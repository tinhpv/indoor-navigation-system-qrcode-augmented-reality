<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Building Information Management</title>
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
	<c:if test="${empty sessionScope.building }">
		<c:redirect url="${pageContext.request.contextPath}/"></c:redirect>
	</c:if>

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
		<div class="content-header shadow p-3 mb-3 bg-white rounded">
			<p>
				<strong>${sessionScope.building.name} - Building's information</strong>
			</p>
		</div>

		<div class="md-stepper-horizontal orange">
			<div class="md-step active">
				<div class="md-step-circle">
					<span>1</span>
				</div>
				<div class="md-step-title">Manage building info</div>
				<div class="md-step-bar-left"></div>
				<div class="md-step-bar-right"></div>
			</div>
			<div class="md-step">
				<div class="md-step-circle">
					<span>2</span>
				</div>
				<div class="md-step-title">Manage building data</div>
				<div class="md-step-bar-left"></div>
				<div class="md-step-bar-right"></div>
			</div>
			<div class="md-step">
				<div class="md-step-circle">
					<span>3</span>
				</div>
				<div class="md-step-title">Upload building to server</div>
				<div class="md-step-bar-left"></div>
				<div class="md-step-bar-right"></div>
			</div>
		</div>

		<div class="building shadow p-3 mb-3 bg-white rounded">
			<form action="${pageContext.request.contextPath}/building/edit" method="post">
				<input type="hidden" class="form-control" id="id" name="id" placeholder="Building ID"
					readonly="readonly" value="${sessionScope.building.id }" />
				<div class="form-header list-group-item">
					<h5>Edit building information</h5>
				</div>
				<div class="form-group row">
					<label for="name" class="col-sm-3 col-form-label">Building name </label>
					<div class="col-sm-5">
						<input type="text" class="form-control" id="name" name="name" placeholder="Building Name"
							required="required" value="${sessionScope.building.name }" />
					</div>
					<p class="col-sm-1">*</p>
				</div>
				<div class="form-group row">
					<label for="description" class="col-sm-3 col-form-label">Description </label>
					<div class="col-sm-7">
						<textarea class="form-control" id="description" name="description" required="required">${sessionScope.building.description }</textarea>
					</div>
					<p class="col-sm-1">*</p>
				</div>
				<div class="form-group row">
					<label for="dayExpired" class="col-sm-3 col-form-label">Expired date </label>
					<div class="col-sm-4">
						<input type="date" class="form-control" id="dayExpired" name="dayExpired"
							placeholder="Expired Day" value="${sessionScope.building.dayExpired }" required="required" />
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
				<div class="wraper">
					<div id="buildingMessageWraper">
						<p class="success">${requestScope.createSuccess }${requestScope.editSuccess }</p>
					</div>
					<div id="buildingActionWraper">
						<a href="${pageContext.request.contextPath}/floor/" class="right btn btn-outline-success">
							Next
							<i class="fas fa-arrow-circle-right"></i>
						</a>
						<button id="rightBtn" class="right btn btn-primary" type="submit">Save changes</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</body>
</html>