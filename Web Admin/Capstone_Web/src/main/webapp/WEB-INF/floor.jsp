<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Building Data Management</title>
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
				<strong>${sessionScope.building.name } - Building's data</strong>
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
			<div class="md-step active">
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

		<div id="content-body-floor" class="shadow p-3 mb-3 bg-white rounded">
			<div class="wraper">
				<div class="actionWraper">
					<span class="bold left percent-70">FLOOR(s)</span>
					<button class="btn btn-custom-1 left percent-30" data-toggle="modal"
						data-target="#createFloorModal">Create new floor</button>
				</div>
			</div>

			<c:if test="${empty sessionScope.building.listFloor }">
				<div class="empty">
					<h5>This building don't contain any floor!</h5>
				</div>
			</c:if>
			<c:if test="${not empty sessionScope.building.listFloor }">
				<div class="list-group">
					<div id="building-list" class="list-group list-group-horizontal">
						<p id="floor-list-item-1" class="list-group-item">Floor's name</p>
						<p id="floor-list-item-2" class="list-group-item">Action</p>
					</div>
					<c:forEach var="floorDto" items="${sessionScope.building.listFloor }" varStatus="floorCounter">
						<div id="floor-list" class="list-group list-group-horizontal">
							<div id="floor-list-item-1" class="list-group-item">
								<p>${floorDto.name }</p>
								<span data-toggle="tooltip" data-placement="bottom"
									title="This floor have ${floorDto.listLocation.size()} location(s)"
									class="badge badge-custom badge-pill">${floorDto.listLocation.size()} location(s)</span>
							</div>
							<!-- Button trigger modal -->
							<button id="floor-list-item-2" data-toggle="modal"
								data-target="#floorDetailModal${floorCounter.count}"
								class="list-group-item list-group-item-action">
								<i class="far fa-edit"></i>
							</button>
						</div>

						<!-- Floor Detail Modal -->
						<div class="modal fade bd-example-modal-lg" id="floorDetailModal${floorCounter.count}"
							tabindex="-1" role="dialog" aria-labelledby="Floor Detail Modal" aria-hidden="true"
							style="overflow-y: hidden;">
							<div id="floorModal" class="modal-dialog modal-lg">
								<div class="modal-content">
									<div class="modal-header">
										<h5 class="modal-title" id="exampleModalLongTitle">${floorDto.name }</h5>
										<button type="button" class="close"
											onclick="$('#floorDetailModal${floorCounter.count}').modal('hide')" aria-label="Close">
											<span aria-hidden="true">&times;</span>
										</button>
									</div>
									<div id="floor-info-body" class="modal-body">
										<!-- floor information -->
										<div id="floorWraper" class="shadow p-3 mb-3 bg-white rounded">
											<form action="${pageContext.request.contextPath}/floor/edit" method="post"
												enctype="multipart/form-data">
												<input type="hidden" class="form-control" name="id" value="${floorDto.id }"
													placeholder="Floor's id" readonly="readonly">
												<div id="floor-header" class="form-group row">
													<h5>Edit floor's information</h5>
												</div>
												<div class="form-group row">
													<label class="col-sm-3 col-form-label">Floor name</label>
													<div class="col-sm-6">
														<input type="text" class="form-control" name="name" value="${floorDto.name }"
															placeholder="Floor's name" required="required" />
													</div>
													<p class="col-sm-1">*</p>
												</div>
												<div class="form-group row">
													<label for="mapFile" class="col-sm-3 col-form-label">Floor map</label>
													<div class="col-sm-7">
														<input type="file" class="form-control-file" onchange="checkImageSize(this.id)"
															id="mapFile${floorCounter.count}" name="mapFile" />
													</div>
												</div>

												<button type="submit" style="float: right;" class="btn btn-custom-1">Save
													changes</button>
												<button type="button" style="float: left;" class="btn btn-danger" data-toggle="modal"
													data-target="#floorDisableModal${floorCounter.count }">Disable this floor</button>
											</form>
										</div>

										<div class="actionWraper">
											<span class="bold left percent-70"> LOCATION(s) </span>
											<a href="${pageContext.request.contextPath}/location/create?floorId=${floorDto.id}"
												class="btn btn-custom-1 right percent-30"> Create new location </a>
										</div>

										<c:if test="${empty floorDto.listLocation }">
											<div class="empty">
												<h5>This floor don't contain any location!</h5>
											</div>
										</c:if>

										<c:if test="${not empty floorDto.listLocation }">
											<div id="location-list-wraper" class="list-group">
												<div id="location-list-header" class="list-group list-group-horizontal">
													<p id="location-list-item-1" class="list-group-item">Location name</p>
													<p id="location-list-item-2" class="list-group-item">QR Code</p>
													<p id="location-list-item-3" class="list-group-item">Action</p>
												</div>
												<c:forEach var="locationDto" items="${floorDto.listLocation}"
													varStatus="locationCounter">
													<div id="location-list" class="list-group list-group-horizontal">
														<div id="location-list-item-1" class="list-group-item">${locationDto.name }</div>
														<button id="location-list-item-2" class="list-group-item" data-toggle="modal"
															onclick="$('#locationQrModal${floorCounter.count}_${locationCounter.count}').appendTo('body');"
															data-target="#locationQrModal${floorCounter.count}_${locationCounter.count}">
															<i class="fas fa-qrcode"></i>
														</button>
														<button id="location-list-item-3" class="list-group-item" data-toggle="modal"
															onclick="$('#neighborAndRoomModal${floorCounter.count}_${locationCounter.count}').appendTo('body');"
															data-target="#neighborAndRoomModal${floorCounter.count}_${locationCounter.count}">
															<i class="far fa-edit"></i>
														</button>
													</div>

													<!-- qrCode modal -->
													<div class="modal fade"
														id="locationQrModal${floorCounter.count}_${locationCounter.count}" aria-hidden="true">
														<div class="modal-dialog modal-dialog-centered" role="document">
															<div class="modal-content">
																<div class="modal-header">
																	<h5 class="modal-title">QR Code of "${locationDto.name }"</h5>
																	<button type="button" class="close"
																		onclick="$('#locationQrModal${floorCounter.count}_${locationCounter.count}').modal('hide')"
																		aria-label="Close">
																		<span aria-hidden="true">&times;</span>
																	</button>
																</div>
																<div id="qrCodeWraper" class="modal-body">
																	<img alt="Please upload this building if you don't see the QR Code."
																		src="${locationDto.linkQr}" />
																</div>
															</div>
														</div>
													</div>

													<!-- location neighbor and room modal -->
													<div class="modal fade bd-example-modal-lg"
														id="neighborAndRoomModal${floorCounter.count}_${locationCounter.count}" tabindex="-1"
														role="dialog" aria-hidden="true">
														<div class="modal-dialog modal-dialog-centered modal-lg" role="document">
															<div class="modal-content" style="min-height: 80vh;">
																<div class="modal-header">
																	<h5 class="modal-title" id="exampleModalLongTitle">${locationDto.name }</h5>
																	<button type="button" class="close"
																		onclick="$('#neighborAndRoomModal${floorCounter.count}_${locationCounter.count}').modal('hide')"
																		aria-label="Close">
																		<span aria-hidden="true">&times;</span>
																	</button>
																</div>
																<div class="modal-body">
																	<!-- location information -->
																	<div id="locationWraper">
																		<a class="btn btn-custom-1"
																			href="${pageContext.request.contextPath}/location/edit?floorId=${floorDto.id}&locationId=${locationDto.id}">
																			Edit this location </a>
																		<button type="button" data-toggle="modal"
																			data-target="#locationDisableModal${floorCounter.count}_${locationCounter.count}"
																			class="btn btn-danger">Disable this location</button>
																	</div>

																	<div id="content-body${floorCounter.count}_${locationCounter.count}"
																		style="width: 100%; float: left;">
																		<div class="card">
																			<div id="collapseOne${floorCounter.count}_${locationCounter.count}"
																				class="collapse show" aria-labelledby="headingOne"
																				data-parent="#content-body${floorCounter.count}_${locationCounter.count}">
																				<div class="card-body">
																					<form action="${pageContext.request.contextPath}/room/remove" method="post">
																						<c:if test="${empty locationDto.listRoom }">
																							<div class="empty">
																								<h5>This location don't have any room.</h5>
																							</div>
																						</c:if>

																						<c:if test="${not empty locationDto.listRoom }">
																							<div id="header-wraper">
																								<h5>All rooms of ${locationDto.name }</h5>
																							</div>
																							<table class="table">
																								<thead>
																									<tr>
																										<th scope="col">Room Name</th>
																										<th scope="col">(X | Y)</th>
																										<th scope="col">Special Room</th>
																										<th scope="col">Action</th>
																									</tr>
																								</thead>
																								<tbody>
																									<c:forEach var="roomDto" items="${locationDto.listRoom}"
																										varStatus="counter">
																										<tr>
																											<td>${roomDto.name }</td>
																											<td>(${roomDto.ratioX } | ${roomDto.ratioY })</td>
																											<td>${roomDto.specialRoom }</td>
																											<td>
																												<!-- key of modal, important! -->
																												<input type="hidden" name="modalKey"
																													value="${floorCounter.count}_${locationCounter.count}">

																												<!-- checkboxes -->
																												<input type="checkbox"
																													name="roomGroup${floorCounter.count}_${locationCounter.count}"
																													value="${roomDto.id }"
																													onchange="roomRemoveBtnState(this.name, '${floorCounter.count}_${locationCounter.count}')">
																											</td>
																										</tr>
																									</c:forEach>
																								</tbody>
																							</table>
																						</c:if>
																						<button id="locationDetailBtn" type="button" class="btn btn-outline-success"
																							data-toggle="collapse"
																							data-target="#collapseTwo${floorCounter.count}_${locationCounter.count}"
																							aria-expanded="true"
																							aria-controls="collapseTwo${floorCounter.count}_${locationCounter.count}">Next</button>
																						<a id="locationDetailBtn"
																							href="${pageContext.request.contextPath}/room/create?floorId=${floorDto.id}&locationId=${locationDto.id}"
																							class="btn btn-custom-1">Create room</a>
																						<button type="submit"
																							id="roomRemoveBtn${floorCounter.count}_${locationCounter.count}"
																							class="btn btn-danger" disabled="disabled">Remove selected</button>
																					</form>
																				</div>
																			</div>
																		</div>
																		<div class="card">
																			<div id="collapseTwo${floorCounter.count}_${locationCounter.count}"
																				class="collapse" aria-labelledby="headingTwo"
																				data-parent="#content-body${floorCounter.count}_${locationCounter.count}">
																				<div class="card-body">
																					<form action="#" method="post">
																						<c:if test="${empty locationDto.listLocationBeside}">
																							<div class="empty">
																								<h5>This location don't have any neighbour</h5>
																							</div>
																						</c:if>

																						<c:if test="${not empty locationDto.listLocationBeside }">
																							<div id="header-wraper">
																								<h5>All neighbors of ${locationDto.name }</h5>
																							</div>
																							<table class="table">
																								<thead>
																									<tr>
																										<th scope="col">Neighbor Name</th>
																										<th scope="col">Orientation</th>
																										<th scope="col">Distance</th>
																										<th scope="col">Status</th>
																										<th scope="col">Action</th>
																									</tr>
																								</thead>
																								<tbody>
																									<c:forEach var="neighbourDto" items="${locationDto.listLocationBeside}"
																										varStatus="counter">
																										<tr>
																											<td>${neighbourDto.name }</td>
																											<td>${neighbourDto.orientation }</td>
																											<td>${neighbourDto.distance }(m)</td>
																											<td>
																												<c:if test="${neighbourDto.active}">
																													<span class="active">
																														<i class="fas fa-circle"></i>
																														ACTIVE
																													</span>
																												</c:if>
																												<c:if test="${not neighbourDto.active}">
																													<span class="inactive">
																														<i class="fas fa-circle"></i>
																														INACTIVE
																													</span>
																												</c:if>
																											</td>
																											<th>
																												<!-- key of modal, important -->
																												<input type="hidden" name="modalKey"
																													value="${floorCounter.count}_${locationCounter.count}" />
																												<input type="hidden" name="locationId" value="${locationDto.id }" />
																												<input type="checkbox"
																													name="neighbourGroup${floorCounter.count}_${locationCounter.count}"
																													onchange="neighbourBtnState(this.name, '${floorCounter.count}_${locationCounter.count}')"
																													value="${neighbourDto.id}" />
																											</th>
																										</tr>
																									</c:forEach>
																								</tbody>
																							</table>
																						</c:if>
																						<button id="neighbourStatusBtn${floorCounter.count}_${locationCounter.count}"
																							class="btn btn-success" disabled="disabled" type="submit"
																							formaction="${pageContext.request.contextPath}/neighbour/changeStatus">Enable/Disable
																							selected</button>
																						<a
																							href="${pageContext.request.contextPath}/neighbour/create?floorId=${floorDto.id}&locationId=${locationDto.id}"
																							id="locationDetailBtn" class="btn btn-custom-1">Attach Neighbor</a>
																						<button id="locationDetailBtn" class="btn btn-outline-success" type="button"
																							data-toggle="collapse"
																							data-target="#collapseOne${floorCounter.count}_${locationCounter.count}"
																							aria-expanded="false"
																							aria-controls="collapseOne${floorCounter.count}_${locationCounter.count}">Back</button>
																						<button type="submit" class="btn btn-danger" disabled="disabled"
																							formaction="${pageContext.request.contextPath}/neighbour/remove"
																							id="neighbourRemoveBtn${floorCounter.count}_${locationCounter.count}">Remove
																							selected</button>
																					</form>
																				</div>
																			</div>
																		</div>
																	</div>
																</div>
															</div>
														</div>

														<!-- location disable modal -->
														<div class="modal fade"
															id="locationDisableModal${floorCounter.count}_${locationCounter.count}" tabindex="-1"
															role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
															<div class="modal-dialog" role="document">
																<div class="modal-content">
																	<div class="modal-header warning">
																		<h5 class="modal-title" id="exampleModalLabel">Warning message</h5>
																		<button type="button" class="close"
																			onclick="$('#locationDisableModal${floorCounter.count}_${locationCounter.count}').modal('hide')"
																			aria-label="Close">
																			<span aria-hidden="true">&times;</span>
																		</button>
																	</div>
																	<div class="modal-body">
																		<p>Do you want to disable this location?</p>
																		<small>(Warning, all relations of other locations with this location will
																			be removed as well)</small>
																	</div>
																	<div class="modal-footer">
																		<button type="button" class="btn btn-secondary"
																			onclick="$('#locationDisableModal${floorCounter.count}_${locationCounter.count}').modal('hide')">Close</button>
																		<form action="${pageContext.request.contextPath}/location/remove" method="post">
																			<input type="hidden" name="id" value="${locationDto.id }" />
																			<button type="submit" class="btn btn-custom-1">Yes</button>
																		</form>
																	</div>
																</div>
															</div>
														</div>
													</div>
												</c:forEach>
											</div>
										</c:if>

										<!-- floor disable modal -->
										<div class="modal fade" id="floorDisableModal${floorCounter.count }" tabindex="-1"
											role="dialog" aria-labelledby="disableModal" aria-hidden="true">
											<div class="modal-dialog" role="document">
												<div class="modal-content">
													<div class="modal-header warning">
														<h5 class="modal-title" id="exampleModalLabel">Warning message!</h5>
														<button type="button" class="close"
															onclick="$('#floorDisableModal${floorCounter.count}').modal('hide')"
															aria-label="Close">
															<span aria-hidden="true">&times;</span>
														</button>
													</div>

													<div class="modal-body">
														<c:if test="${floorDto.listLocation.size() > 0}">
															<p>Please disable all location(s) belong to ${floorDto.name}!</p>
														</c:if>
														<c:if test="${floorDto.listLocation.size() == 0}">
															<p>Do you want to disable ${floorDto.name}?</p>
														</c:if>
													</div>
													<div class="modal-footer">
														<button type="button" class="btn btn-secondary"
															onclick="$('#floorDisableModal${floorCounter.count}').modal('hide')">Cancel</button>
														<c:if test="${floorDto.listLocation.size() == 0}">
															<form action="${pageContext.request.contextPath}/floor/remove" method="post">
																<input type="hidden" value="${floorDto.id}" name="id" />
																<button type="submit" class="btn btn-custom-1">Confirm</button>
															</form>
														</c:if>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</c:forEach>
				</div>

				<div id="navigate-footer">
					<a class="btn btn-outline-success" href="${pageContext.request.contextPath}/building">
						<i class="fas fa-arrow-circle-left"> Back to ${sessionScope.building.name }</i>
					</a>
					<a class="btn btn-outline-success" style="float: right;"
						href="${pageContext.request.contextPath}/building/upload">
						Next
						<i class="fas fa-arrow-circle-right"></i>
					</a>
				</div>
			</c:if>

			<!-- create floor modal -->
			<div class="modal fade" id="createFloorModal" tabindex="-1" role="dialog"
				aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
				<div class="modal-dialog modal-dialog-centered" role="document">
					<div class="modal-content">
						<form action="${pageContext.request.contextPath}/floor/create" method="post"
							enctype="multipart/form-data">
							<div class="modal-header">
								<h5 class="modal-title" id="exampleModalLongTitle">Create new floor</h5>
								<button type="button" class="close" data-dismiss="modal" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
							</div>
							<div class="modal-body">
								<div class="form-group row">
									<label for="name" class="col-sm-3 col-form-label">Floor name </label>
									<div class="col-sm-6">
										<input type="text" required="required" class="form-control" id="name" name="name"
											placeholder="Floor name" />
									</div>
									<p class="col-sm-1">*</p>
								</div>
								<div class="form-group row">
									<label for="mapFile" class="col-sm-3 col-form-label">Floor map </label>
									<div class="col-sm-6">
										<input type="file" class="form-control-file" id="mapFile"
											onchange="checkImageSize(this.id)" required="required" name="mapFile" />
									</div>
									<p class="col-sm-1">*</p>
								</div>
							</div>
							<div class="modal-footer">
								<button type="submit" class="btn btn-custom-1">Create</button>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript">
		$(function() {
			$('[data-toggle="tooltip"]').tooltip()
		});

		function checkImageSize(id) {
			var uploadFile = document.getElementById(id);
			if (uploadFile.files[0].size > 10485760) {
				alert("Size of current file is too big! Please choose file that are below 10MB.");
				uploadFile.value = "";
			}
		}

		function roomRemoveBtnState(name, btnIdNumber) {
			var i;
			var x = document.getElementsByName(name);
			var buttonName = "roomRemoveBtn"
			var button = document.getElementById(buttonName + btnIdNumber);
			for (i = 0; i < x.length; i++) {
				if (x[i].checked == true) {
					button.disabled = false;
					break;
				} else {
					button.disabled = true;
				}
			}
		}

		function neighbourBtnState(name, btnIdNumber) {
			var i;
			var x = document.getElementsByName(name);
			var removeBtnName = "neighbourRemoveBtn";
			var statusBtnName = "neighbourStatusBtn";
			var removeBtn = document
					.getElementById(removeBtnName + btnIdNumber);
			var statusBtn = document
					.getElementById(statusBtnName + btnIdNumber);
			for (i = 0; i < x.length; i++) {
				if (x[i].checked == true) {
					removeBtn.disabled = false;
					statusBtn.disabled = false;
					break;
				} else {
					removeBtn.disabled = true;
					statusBtn.disabled = true;
				}
			}
		}
	</script>
</body>
</html>