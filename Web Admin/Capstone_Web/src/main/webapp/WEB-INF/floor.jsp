<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Building floors page</title>
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
		<div id="content-header" class="shadow p-2 mb-2 bg-white rounded">
			<c:if test="${not empty sessionScope.building }">
				<p>
					<strong>${sessionScope.building.name } - Building's Floor(s)</strong>
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

		<div id="content-body-floor" class="shadow p-3 mb-3 bg-white rounded">
			<c:if test="${empty sessionScope.building.listFloor }">
				<h3>This building don't contain any floor!</h3>
			</c:if>
			<c:if test="${not empty sessionScope.building.listFloor }">
				<div class="list-group">
					<div id="list-group">
						<div id="list-group-1" class="list-group-item">Building's floor:</div>
						<button id="list-group-2" data-toggle="modal" data-target="#createFloorModal"
							class="list-group-item list-group-item-action  list-group-item-success">
							<i class="fas fa-plus"> New Floor</i>
						</button>
					</div>
					<c:forEach var="floorDto" items="${sessionScope.building.listFloor }" varStatus="counter">
						<div id="buildingList">
							<a id="floorLink" class="list-group-item list-group-item-action"
								href="${pageContext.request.contextPath}/floor/floorDetail?id=${floorDto.id}">${floorDto.name }
								<span class="badge badge-primary badge-pill">${floorDto.listLocation.size()}</span>
							</a>
							<a id="infoButton" href="#floorInfoModal${counter.count }" data-toggle="modal"
								class="list-group-item list-group-item-info list-group-item-action">
								<i class="fas fa-edit"></i>
							</a>
							<a id="imageButton" class="list-group-item list-group-item-action" data-toggle="modal"
								href="#floorImageModal${counter.count }">
								<i class="fas fa-images"></i>
							</a>
							<a id="deleteButton" href="#comfirmDeletionModal${counter.count }" data-toggle="modal"
								class="list-group-item list-group-item-warning list-group-item-action">
								<i class="fas fa-trash-alt"></i>
							</a>
						</div>

						<!-- create floor modal -->
						<div class="modal fade" id="createFloorModal" tabindex="-1" role="dialog"
							aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
							<div class="modal-dialog modal-dialog-centered" role="document">
								<div class="modal-content">
									<form action="${pageContext.request.contextPath}/floor" method="post"
										enctype="multipart/form-data">
										<div class="modal-header">
											<h5 class="modal-title" id="exampleModalLongTitle">Create new floor</h5>
											<button type="button" class="close" data-dismiss="modal" aria-label="Close">
												<span aria-hidden="true">&times;</span>
											</button>
										</div>
										<div class="modal-body">
											<div class="form-group row">
												<label for="name" class="col-sm-3 col-form-label">Floor name: </label>
												<div class="col-sm-6">
													<input type="text" required="required" class="form-control" id="name" name="name"
														placeholder="Floor name" />
												</div>
												<p class="col-sm-1">*</p>
											</div>
											<div class="form-group row">
												<label for="mapFile" class="col-sm-3 col-form-label">Floor map: </label>
												<div class="col-sm-6">
													<input type="file" class="form-control-file" id="mapFile" required="required"
														name="mapFile" />
												</div>
												<p class="col-sm-1">*</p>
											</div>
										</div>
										<div class="modal-footer">
											<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
											<button type="submit" class="btn btn-primary">Create</button>
										</div>
									</form>
								</div>
							</div>
						</div>

						<!-- Edit Floor Info -->
						<div class="modal fade" id="floorInfoModal${counter.count}" tabindex="-1" role="dialog"
							aria-labelledby="Floor Info" aria-hidden="true">
							<div class="modal-dialog modal-dialog-centered" role="document">
								<div class="modal-content">
									<form action="${pageContext.request.contextPath}/floor/edit" method="post"
										enctype="multipart/form-data">
										<div class="modal-header">
											<h5 class="modal-title" id="exampleModalLongTitle">Edit floor's information</h5>
											<button type="button" class="close" data-dismiss="modal" aria-label="Close">
												<span aria-hidden="true">&times;</span>
											</button>
										</div>
										<div class="modal-body">
											<input type="hidden" class="form-control" id="id" name="id" value="${floorDto.id }"
												placeholder="Floor's id" readonly="readonly">
											<div class="form-group row">
												<label for="name" class="col-sm-3 col-form-label">Floor name:</label>
												<div class="col-sm-6">
													<input type="text" class="form-control" id="name" name="name" value="${floorDto.name }"
														placeholder="Floor's name" required="required" />
												</div>
												<p class="col-sm-1">*</p>
											</div>
											<div class="form-group row">
												<label for="fileMap" class="col-sm-3 col-form-label">Floor map: </label>
												<div class="col-sm-6">
													<input type="file" class="form-control-file" id="fileMap" name="fileMap" />
												</div>
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

						<!-- Deletion Modal -->
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
										<c:if test="${not empty floorDto.listLocation }">
											<p style="color: black;">
												<strong>${floorDto.name }</strong>
												contain ${floorDto.listLocation.size() } location(s). Please remove these location(s)
												first.
											</p>
										</c:if>
										<c:if test="${empty floorDto.listLocation }">
											<p style="color: black;">Confirm deletion of ${floorDto.name }?</p>
										</c:if>
									</div>
									<div class="modal-footer">
										<c:if test="${empty floorDto.listLocation }">
											<button type="button" class="btn btn-secondary" data-dismiss="modal">No</button>
											<form action="${pageContext.request.contextPath}/floor/removeFloor" method="post">
												<input type="hidden" name="id" value="${floorDto.id }" />
												<button type="submit" class="btn btn-danger">Yes</button>
											</form>
										</c:if>
										<c:if test="${not empty floorDto.listLocation }">
											<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
										</c:if>
									</div>
								</div>
							</div>
						</div>

						<!-- Image Modal -->
						<div class="modal fade bd-example-modal-xl" id="floorImageModal${counter.count }"
							tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
							<div class="modal-dialog modal-dialog-centered modal-xl" role="document">
								<div class="modal-content">
									<div class="modal-header">
										<h5 class="modal-title" id="exampleModalLongTitle">Map of ${floorDto.name }</h5>
										<button type="button" class="close" data-dismiss="modal" aria-label="Close">
											<span aria-hidden="true">&times;</span>
										</button>
									</div>
									<div class="modal-body">
										<img id="floorImage" alt="No image found" src="${floorDto.linkMap }">
									</div>
								</div>
							</div>
						</div>
					</c:forEach>

					<div id="navigate-footer">
						<a class="btn btn-outline-success" href="${pageContext.request.contextPath}/building">
							<i class="fas fa-arrow-circle-left"> Back to ${sessionScope.building.name }</i>
						</a>
					</div>
				</div>
			</c:if>
		</div>
	</div>
</body>
</html>