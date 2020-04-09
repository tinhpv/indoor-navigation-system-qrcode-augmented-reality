<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Floor detail page</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/floor.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/stepper.css">
<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.7.0/css/all.css">
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css">
<script src="https://code.jquery.com/jquery-3.4.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>
</head>
<body>
	<div id="menu-bar">
		<div id="front-page">INQR</div>
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
		<hr>
		<div id="menu-bar-floor-list">
			<a href="${pageContext.request.contextPath}/floor">
				<i class="fas fa-layer-group"> Floor plans</i>
			</a>
			<c:forEach var="floorDto" items="${sessionScope.building.listFloor }">
				<a href="${pageContext.request.contextPath}/floor/floorDetail?id=${floorDto.id}">
					<i class="fas fa-kaaba"> ${floorDto.name }</i>
				</a>
			</c:forEach>
		</div>
	</div>
	<div class="content">
		<div id="content-header" class="shadow p-3 mb-3 bg-white rounded">
			<c:if test="${not empty sessionScope.building && not empty sessionScope.floor }">
				<p>
					<strong>${sessionScope.building.name } - ${sessionScope.floor.name }</strong>
				</p>
				<div id="content-header-action">
					<form action="${pageContext.request.contextPath}/building/updateBuilding" method="post">
						<button class="btn btn-outline-success" type="submit">
							<i class="fas fa-file-upload"> Update building to server</i>
						</button>
					</form>
				</div>
			</c:if>
		</div>

		<div class="wrapper-progressBar">
			<ul class="progressBar">
				<li class="done">Building's Information Management</li>
				<li class="active">Building's Data Management</li>
			</ul>
		</div>

		<div id="content-body-location" class="shadow p-3 mb-3 bg-white rounded">
			<div id="list-group">
				<div id="list-group-1" class="list-group-item">Location(s) of this floor:</div>
				<a id="list-group-2" href="${pageContext.request.contextPath}/location/map"
					class="list-group-item list-group-item-action  list-group-item-success">
					<i class="fas fa-plus"> New Location</i>
				</a>
			</div>

			<c:if test="${empty sessionScope.floor.listLocation }">
				<h1>
					<strong>This floor don't contain any location!</strong>
				</h1>
			</c:if>
			<c:if test="${not empty sessionScope.floor.listLocation }">
				<c:forEach var="locationDto" items="${sessionScope.floor.listLocation }" varStatus="counter">
					<div id="locationList">
						<a id="locationLink" class="list-group-item list-group-item-action"
							href="${pageContext.request.contextPath}/location?id=${locationDto.id}">${locationDto.name }</a>

						<!-- edit location button -->
						<button data-target="#editLocationModal${counter.count }" data-toggle="modal" id="editButton"
							class="list-group-item list-group-item-action list-group-item-warning">
							<i class="fas fa-edit"></i>
						</button>

						<!-- qr code modal button -->
						<button id="qrButton" class="list-group-item list-group-item-action" data-toggle="modal"
							data-target="#qrCodeModal${counter.count }">
							<i class="fas fa-qrcode"></i>
						</button>

						<!-- collapse button -->
						<button type="button" class="list-group-item list-group-item-info list-group-item-action"
							id="neighbourCollapse" data-toggle="collapse"
							data-target="#neighbourCollapse${counter.count}" aria-expanded="false"
							aria-controls="neighbourCollapse${counter.count}">
							<i class="fas fa-route"></i>
							<i class="fas fa-caret-down"></i>
						</button>
						<button type="button" class="list-group-item list-group-item-secondary list-group-item-action"
							id="roomCollapse" data-toggle="collapse" data-target="#roomCollapse${counter.count}"
							aria-expanded="false" aria-controls="roomCollapse${counter.count}">
							<i class="fas fa-map-marker-alt"></i>
							<i class="fas fa-caret-down"></i>
						</button>

						<!-- delete button -->
						<button id="locationDelete"
							class="list-group-item list-group-item-warning list-group-item-action" data-toggle="modal"
							data-target="#comfirmDeletionModal${counter.count }">
							<i class="fas fa-trash-alt"></i>
						</button>

						<div class="row">
							<div class="col">
								<!-- neighbor collapse -->
								<div class="collapse" id="neighbourCollapse${counter.count}">
									<c:if test="${not empty locationDto.listLocationBeside}">
										<c:forEach var="neighbourDto" items="${locationDto.listLocationBeside }">
											<div id="neighborWraper" class="list-group-item list-group-item-info">
												<p id="neighborName">${neighbourDto.name }</p>
												<p id="neighborOrien">${neighbourDto.orientation }</p>
												<p id="neighborDistance">${neighbourDto.distance }(m)</p>
											</div>
										</c:forEach>
									</c:if>
									<c:if test="${empty locationDto.listLocationBeside}">
										<div class="list-group-item list-group-item-info">
											<strong>${locationDto.name } don't have any neighbor.</strong>
										</div>
									</c:if>
								</div>
							</div>
							
							<!-- room collapse -->
							<div class="col">
								<div class="collapse" id="roomCollapse${counter.count}">
									<c:if test="${not empty locationDto.listRoom}">
										<c:forEach var="roomDto" items="${locationDto.listRoom }">
											<div class="list-group-item list-group-item-secondary">${roomDto.name }</div>
										</c:forEach>
									</c:if>
									<c:if test="${empty locationDto.listRoom}">
										<div class="list-group-item list-group-item-secondary">
											<strong>${locationDto.name } don't have any room.</strong>
										</div>
									</c:if>
								</div>
							</div>
						</div>
						<!-- delete modal -->
						<div class="modal fade" id="comfirmDeletionModal${counter.count }" tabindex="-1" role="dialog"
							aria-labelledby="exampleModalLabel" aria-hidden="true">
							<div class="modal-dialog" role="document">
								<div class="modal-content">
									<div class="modal-header">
										<h5 class="modal-title" id="exampleModalLabel" style="color: red;">Warning</h5>
										<button type="button" class="close" data-dismiss="modal" aria-label="Close">
											<span aria-hidden="true">&times;</span>
										</button>
									</div>
									<div class="modal-body">
										<p style="color: black;">This will remove all relations of other locations with this
											one. Confirm deletion?</p>
									</div>
									<div class="modal-footer">
										<button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
										<form action="${pageContext.request.contextPath}/location/remove" method="post">
											<input type="hidden" name="id" value="${locationDto.id }" />
											<button type="submit" class="btn btn-danger">Delete</button>
										</form>
									</div>
								</div>
							</div>
						</div>

						<!-- qrCode modal -->
						<div class="modal fade" id="qrCodeModal${counter.count }" aria-hidden="true">
							<div class="modal-dialog modal-dialog-centered" role="document">
								<div class="modal-content">
									<div class="modal-header">
										<h5 class="modal-title" id="exampleModalLongTitle">QR Code of "${locationDto.name }"</h5>
										<button type="button" class="close" data-dismiss="modal" aria-label="Close">
											<span aria-hidden="true">&times;</span>
										</button>
									</div>
									<div id="qrCodeWraper" class="modal-body">
										<img src="${locationDto.linkQr}" />
									</div>
								</div>
							</div>
						</div>

						<!-- edit location modal -->
						<div class="modal fade" id="editLocationModal${counter.count }" tabindex="-1" role="dialog"
							aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
							<div class="modal-dialog modal-dialog-centered" role="document">
								<div class="modal-content">
									<form action="${pageContext.request.contextPath}/location/edit" method="post">
										<div class="modal-header">
											<h5 class="modal-title" id="exampleModalLongTitle">Information of
												"${locationDto.name }"</h5>
											<button type="button" class="close" data-dismiss="modal" aria-label="Close">
												<span aria-hidden="true">&times;</span>
											</button>
										</div>
										<div class="modal-body">
											<input type="hidden" name="id" value="${locationDto.id }" />
											<div class="form-group row">
												<label for="name" class="col-sm-4 col-form-label">Location name:</label>
												<div class="col-sm-7">
													<input type="text" class="form-control" id="name" name="name" required="required"
														placeholder="Location Name" value="${locationDto.name }">
												</div>
												<p class="col-sm-1">*</p>
											</div>
											<div class="form-group row">
												<label for="ratioX" class="col-sm-4 col-form-label">Ratio X:</label>
												<div class="col-sm-4">
													<input type="number" step="0.001" class="form-control" id="ratioX" name="ratioX"
														required="required" placeholder="${locationDto.ratioX }">
												</div>
												<p class="col-sm-1">*</p>
											</div>
											<div class="form-group row">
												<label for="ratioY" class="col-sm-4 col-form-label">Ratio Y:</label>
												<div class="col-sm-4">
													<input type="number" step="0.001" class="form-control" id="ratioY" name="ratioY"
														required="required" placeholder="${locationDto.ratioY }">
												</div>
												<p class="col-sm-1">*</p>
											</div>
										</div>
										<div class="modal-footer">
											<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
											<button type="submit" class="btn btn-primary">Save changes</button>
										</div>
									</form>
								</div>
							</div>
						</div>
					</div>
				</c:forEach>

				<div id="navigate-footer">
					<a class="btn btn-outline-success" href="${pageContext.request.contextPath}/floor">
						<i class="fas fa-arrow-circle-left"> Back to ${sessionScope.building.name }'s floor list</i>
					</a>
				</div>
			</c:if>

			<!-- create location modal -->
			<%-- <div class="modal fade" id="createLocationModal" tabindex="-1" role="dialog"
				aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
				<div class="modal-dialog modal-dialog-centered" role="document">
					<div class="modal-content">
						<form action="${pageContext.request.contextPath}/location" method="post">
							<div class="modal-header">
								<h5 class="modal-title" id="exampleModalLongTitle">Create new location</h5>
								<button type="button" class="close" data-dismiss="modal" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
							</div>
							<div class="modal-body">
								<div class="form-group">
									<label for="name">Location name:</label>
									<input type="text" class="form-control" id="name" name="name" placeholder="Location Name">
								</div>
								<div>
									<div id="ratioWarper" class="form-group">
										<label for="ratioX">Ratio X:</label>
										<input type="number" step="0.0001" class="form-control" id="ratioX" name="ratioX"
											placeholder="Ratio X">
									</div>
									<div id="ratioWarper" class="form-group">
										<label for="ratioY">Ratio Y:</label>
										<input type="number" step="0.0001" class="form-control" id="ratioY" name="ratioY"
											placeholder="Ratio Y">
									</div>
								</div>
								<input type="hidden" name="currentPage" value="floorDetail" />
								<a href="${pageContext.request.contextPath}/location/map">Locate X and Y ratios.</a>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
								<button type="submit" class="btn btn-primary">Create</button>
							</div>
						</form>
					</div>
				</div>
			</div> --%>
		</div>
	</div>
</body>
</html>