<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Location Page</title>
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css">
<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.7.0/css/all.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/location.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/stepper.css">
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
		<c:if test="${not empty sessionScope.location }">
			<div id="content-header" class="shadow p-3 mb-3 bg-white rounded">
				<c:if test="${not empty sessionScope.building && not empty sessionScope.floor }">
					<p>
						<strong>${sessionScope.building.name } - ${sessionScope.floor.name } -
							${sessionScope.location.name }</strong>
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

			<div id="content-body-list" class="shadow p-3 mb-3 bg-white rounded">
				<div id="list-group">
					<div id="list-group-1" class="list-group-item">Location's neighbor(s):</div>
					<button id="list-group-2" data-toggle="modal" data-target="#addNeighborModal"
						class="list-group-item list-group-item-action list-group-item-secondary">
						<i class="fas fa-plus"> Attach neighbor</i>
					</button>
				</div>
				<div id="content-body-list-body">
					<div class="collapse show" id="neighbourCollapse">
						<div class="list-group">
							<div id="content-body-list-item-1" class="list-group-item list-group-item-secondary">
								<div id="list-item-1-item-1">Name</div>
								<div id="list-item-1-item-2">Orientation</div>
								<div id="list-item-1-item-3">Distance</div>
								<div id="list-item-1-item-4"></div>
							</div>
							<c:forEach var="neighbourDto" items="${sessionScope.location.listLocationBeside}">
								<div id="content-body-list-item-1" class="list-group-item">
									<div id="list-item-1-item-1">${neighbourDto.name }</div>
									<div id="list-item-1-item-2">${neighbourDto.orientation }</div>
									<div id="list-item-1-item-3">${neighbourDto.distance }</div>
									<div id="list-item-1-item-4">
										<form action="${pageContext.request.contextPath}/neighbour/remove" method="post">
											<input type="hidden" name="locationId" value="${sessionScope.location.id }" /> <input
												type="hidden" name="neighbourId" value="${neighbourDto.id }" /> <input type="hidden"
												name="currentPage" value="floor" />
											<button type="submit" class="btn btn-warning">
												<i class="fas fa-trash-alt"></i>
											</button>
										</form>
									</div>
								</div>
							</c:forEach>
						</div>
					</div>
				</div>
			</div>

			<div id="content-body-list" class="shadow p-3 mb-3 bg-white rounded">
				<div id="list-group">
					<div id="list-group-1" class="list-group-item">Location's room(s):</div>
					<a id="list-group-2" href="${pageContext.request.contextPath}/room/map"
						class="list-group-item list-group-item-action list-group-item-info">
						<i class="fas fa-plus"> Create Room</i>
					</a>
				</div>
				<div id="content-body-list-body">
					<div class="list-group">
						<div id="content-body-list-item-2" class="list-group-item list-group-item-info">
							<div id="list-item-2-item-1">Name</div>
							<div id="list-item-2-item-2">X</div>
							<div id="list-item-2-item-3">Y</div>
							<div id="list-item-2-item-4">Special room</div>
							<div id="list-item-2-item-5"></div>
						</div>
						<c:forEach var="roomDto" items="${sessionScope.location.listRoom}">
							<div id="content-body-list-item-2" class="list-group-item">
								<div id="list-item-2-item-1">${roomDto.name }</div>
								<div id="list-item-2-item-2">${roomDto.ratioX}</div>
								<div id="list-item-2-item-3">${roomDto.ratioY}</div>
								<div id="list-item-2-item-4">
									<c:if test="${roomDto.specialRoom == true}">
										<strong>YES</strong>
									</c:if>
									<c:if test="${roomDto.specialRoom == false}">
										<strong>NO</strong>
									</c:if>
								</div>
								<div id="list-item-2-item-5">
									<form action="${pageContext.request.contextPath}/room/remove" method="post">
										<input type="hidden" name="locationId" value="${sessionScope.location.id }" /> <input
											type="hidden" name="roomId" value="${roomDto.id }" /> <input type="hidden"
											name="currentPage" value="floor" />
										<button type="submit" class="btn btn-warning">
											<i class="fas fa-trash-alt"></i>
										</button>
									</form>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
			</div>

			<!-- Add Neighbour Modal -->
			<div class="modal fade" id="addNeighborModal" tabindex="-1" role="dialog"
				aria-labelledby="addNeighbourModal" aria-hidden="true">
				<div class="modal-dialog modal-dialog-centered" role="document">
					<div class="modal-content">
						<form action="${pageContext.request.contextPath}/neighbour" method="post">
							<div class="modal-header">
								<h5 class="modal-title" id="exampleModalLongTitle">Attach neighbor for
									${sessionScope.location.name }</h5>
								<button type="button" class="close" data-dismiss="modal" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
							</div>
							<div class="modal-body">
								<div class="form-group row">
									<label for="myInput" class="col-sm-2 col-form-label">Search: </label>
									<div class="col-sm-7">
										<input class="form-control" type="text" id="myInput" onkeyup="myFunction()"
											placeholder="Find location">
									</div>
								</div>
								<div id="allLocation" class="list-group">
									<a class="list-group-item active">Location(s)</a>
									<c:forEach var="locationList" items="${sessionScope.allLocations }">
										<c:if test="${locationList.id != sessionScope.location.id}">
											<a class="list-group-item list-group-item-action py-3"
												onclick="document.getElementById('neighbourId').value='${locationList.id}'; document.getElementById('neighbourName').value='${locationList.name}';">
												${locationList.name } </a>
										</c:if>
									</c:forEach>
								</div>
								<input type="hidden" class="form-control" id="neighbourId" name="id" placeholder="ID"
									readonly="readonly">
								<div class="form-group row">
									<label for="orientation" class="col-sm-3 col-form-label">Neighbor: </label>
									<div class="col-sm-6">
										<input type="text" required="required" class="form-control" id="neighbourName"
											placeholder="Neighbor" name="name" onkeydown="return false;">
									</div>
									<p class="col-sm-1">*</p>
								</div>
								<div class="form-group row">
									<label for="orientation" class="col-sm-3 col-form-label">Orientation: </label>
									<div class="col-sm-4">
										<select id="orientation" name="orientation" class="form-control">
											<option value="LEFT">Left</option>
											<option value="RIGHT">Right</option>
											<option value="FRONT">Front</option>
											<option value="BACK">Back</option>
											<option value="UP">Up</option>
											<option value="DOWN">Down</option>
										</select>
									</div>
									<p class="col-sm-1">*</p>
								</div>
								<div class="form-group row">
									<label for="distance" class="col-sm-3 col-form-label">Distance: </label>
									<div class="col-sm-4">
										<input type="number" required="required" step="0.01" class="form-control" name="distance"
											placeholder="Distance">
									</div>
									<p class="col-sm-1">*</p>
								</div>
								<input type="hidden" name="locationId" value="${sessionScope.location.id }" />
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
								<button type="submit" class="btn btn-primary">Attach Neighbor</button>
							</div>
						</form>
					</div>
				</div>
			</div>
		</c:if>
	</div>

	<script>
		function myFunction() {
			var input, filter, div, a, i, txtValue;
			input = document.getElementById("myInput");
			filter = input.value.toUpperCase();
			div = document.getElementById("allLocation");
			a = div.getElementsByTagName("a");
			for (i = 0; i < a.length; i++) {
				/* a = li[i].getElementsByTagName("a")[0]; */
				txtValue = a[i].textContent || a[i].innerText;
				if (txtValue.toUpperCase().indexOf(filter) > -1) {
					a[i].style.display = "";
				} else {
					a[i].style.display = "none";
				}
			}
		};
	</script>
</body>
</html>