<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Upload Page</title>
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
		<div id="front-page">
			<img src="${pageContext.request.contextPath}/images/icon.png" />
			<span>INQR</span>
		</div>
		<hr>
		<div id="menu-bar-content">
			<a href="${pageContext.request.contextPath}/">
				<i class="fas fa-list-alt"> Dashboard</i>
			</a>
		</div>
		<hr>
		<small>
			<strong>BUILDING</strong>
		</small>
		<div id="menu-bar-content">
			<a href="${pageContext.request.contextPath}/building">
				<i class="fas fa-building"> ${sessionScope.building.name }</i>
			</a>
		</div>
	</div>
	<div class="content">
		<div class="content-header shadow p-3 mb-3 bg-white rounded">
			<p>
				<strong>${sessionScope.building.name } - Upload building</strong>
			</p>
		</div>

		<div class="md-stepper-horizontal orange">
			<div class="md-step">
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
			<div class="md-step active">
				<div class="md-step-circle">
					<span>3</span>
				</div>
				<div class="md-step-title">Upload building to server</div>
				<div class="md-step-bar-left"></div>
				<div class="md-step-bar-right"></div>
			</div>
		</div>

		<div id="content-body-upload" class="shadow p-3 mb-3 bg-white rounded">
			<h5>Upload this building?</h5>
			<div id="navigate-footer">
				<a class="btn btn-outline-success" href="${pageContext.request.contextPath }/floor/">
					<i class="fas fa-arrow-circle-left"> Back to editing</i>
				</a>
				<form style="float: right;" action="${pageContext.request.contextPath}/building/upload"
					method="post">
					<button type="submit" class="btn btn-custom-1">
						<i class="far fa-check-circle"> Yes</i>
					</button>
				</form>
			</div>
		</div>
	</div>
</body>
</html>