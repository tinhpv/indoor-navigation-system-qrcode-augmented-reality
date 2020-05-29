<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Dashboard</title>
<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.7.0/css/all.css">
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/index.css" />
<script src="https://code.jquery.com/jquery-3.4.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>
</head>
<body>
	<div class="bg">
		<div class="icon"
			style="background-image: url('${pageContext.request.contextPath}/images/background.png');">
		</div>
		<div class="content">
			<div class="content-header">
				<!-- <h1>INQR Dashboard</h1> -->
				<img src="${pageContext.request.contextPath}/images/headerimage.png" />
			</div>
			<div class="wraper">
				<div id="actionWraper">
					<span class="bold left percent-70"> COMPANY(s) </span>
					<button class="btn btn-custom-1 right percent-30" data-toggle="modal"
						data-target="#createCompanyModal">Create new company</button>
				</div>
			</div>
			<div class="content-body">
				<c:if test="${not empty sessionScope.companyList }">
					<div id="company-list" class="list-group">
						<%-- <div class="list-header">
							<div id="company-list-title">
								<h5>List of all companies</h5>
							</div>
							<div id="company-list-action" data-toggle="tooltip" data-placement="left"
								title="Refresh below list">
								<a href="${pageContext.request.contextPath}/company/getAllCompanies">
									<i class="fas fa-sync-alt"></i>
								</a>
							</div>
						</div> --%>
						<div class="list-body">
							<!-- <div id="company-list-header" class="list-group list-group-horizontal">
								<div id="company-list-header-1" class="list-group-item">Company name</div>
								<div id="company-list-header-2" class="list-group-item">More Detail</div>
							</div> -->
							<c:forEach var="companyDto" items="${sessionScope.companyList }" varStatus="counter">
								<div class="list-group list-group-horizontal">
									<div id="company-list-item-1" class="list-group-item">
										<p>${companyDto.name }</p>
										<span class="badge badge-custom badge-pill" data-toggle="tooltip" data-placement="bottom"
											title="This company have ${companyDto.listBuilding.size()} building(s)">${companyDto.listBuilding.size()}
											building(s)</span>
									</div>
									<button id="company-list-item-2" data-toggle="modal"
										data-target="#buildingListModal${counter.count}"
										class="list-group-item list-group-item-action">
										<i class="far fa-edit"></i>
									</button>
								</div>

								<!-- Company Building Modal -->
								<div class="modal fade" id="buildingListModal${counter.count}" tabindex="-1" role="dialog"
									aria-labelledby="Building List" aria-hidden="true">
									<div class="modal-dialog modal-lg" role="document">
										<div class="modal-content">
											<div class="modal-header">
												<h5 class="modal-title">${companyDto.name }</h5>
												<button type="button" class="close" data-dismiss="modal" aria-label="Close">
													<span aria-hidden="true">&times;</span>
												</button>
											</div>
											<div class="modal-body">
												<div id="buildingWraper">
													<form action="${pageContext.request.contextPath}/company/update" method="post">
														<input type="hidden" name="id" value="${companyDto.id }" />
														<div class="form-group row">
															<label id="company-label" class="col-sm-12">Change company's name</label>
															<input id="companyName" type="text" class="form-control form-control-lg col-sm-8"
																required="required" name="name" value="${companyDto.name }" />
															<button type="submit" class="btn btn-custom-1 col-sm-3">Save Changes</button>
														</div>
													</form>
												</div>

												<hr class="col-sm-12">

												<div id="actionWraper">
													<span class="bold left percent-80"> BUILDING(s) </span>
													<button type="button" class="btn btn-custom-1 right percent-20" data-toggle="modal"
														data-target="#createBuildingModal${counter.count}">Create new building</button>
												</div>

												<c:if test="${empty companyDto.listBuilding }">
													<div class="empty">
														<h5>There are no building belong to this company!</h5>
													</div>
												</c:if>
												<c:if test="${not empty companyDto.listBuilding }">
													<div id="building-list" class="list-group">
														<div class="list-header" class="list-group-item">
															<div class="list-group list-group-horizontal-lg">
																<p id="building-list-item-1" class="list-group-item">Building's name</p>
																<p id="building-list-item-2" class="list-group-item">Expiration Date</p>
																<p id="building-list-item-3" class="list-group-item">Status</p>
																<p id="building-list-item-4" class="list-group-item">Action</p>
															</div>
														</div>
														<c:forEach var="buildingDto" items="${companyDto.listBuilding }">
															<div class="list-group list-group-horizontal-lg">
																<div id="building-list-item-1" class="list-group-item">${buildingDto.name}</div>
																<div id="building-list-item-2" class="list-group-item">${buildingDto.dayExpired }</div>
																<div id="building-list-item-3" class="list-group-item">
																	<c:if test="${buildingDto.active}">
																		<span class="active">
																			<i class="fas fa-circle"></i>
																			ACTIVE
																		</span>
																	</c:if>
																	<c:if test="${not buildingDto.active}">
																		<span class="inactive">
																			<i class="fas fa-circle"></i>
																			INACTIVE
																		</span>
																	</c:if>
																</div>
																<div id="building-list-item-4" class="list-group-item">
																	<a class="btn btn-info btn-sm" data-toggle="tooltip" data-placement="bottom"
																		title="Download QR-Codes of this building"
																		href="${pageContext.request.contextPath}/building/getQRCode/${buildingDto.name}?id=${buildingDto.id }">
																		<i class="fas fa-file-download"></i>
																	</a>
																	<a class="btn btn-custom-1 btn-sm" data-toggle="tooltip" data-placement="bottom"
																		title="Manage this building"
																		href="${pageContext.request.contextPath}/building/getBuilding?buildingId=${buildingDto.id}">
																		<i class="far fa-edit"></i>
																	</a>
																</div>
															</div>
														</c:forEach>
													</div>
												</c:if>

												<!-- create building modal -->
												<div class="modal fade" id="createBuildingModal${counter.count}" tabindex="-1"
													role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
													<div class="modal-dialog modal-dialog-centered" role="document">
														<div class="modal-content">
															<form action="${pageContext.request.contextPath}/building/create" method="post">
																<div class="modal-header">
																	<h5 class="modal-title" id="exampleModalLongTitle">Create new building for
																		${companyDto.name}</h5>
																	<button type="button" class="close"
																		onclick="$('#createBuildingModal${counter.count}').modal('hide')"
																		aria-label="Close">
																		<span aria-hidden="true">&times;</span>
																	</button>
																</div>
																<div class="modal-body">
																	<input type="hidden" name="companyId" value="${companyDto.id}" />
																	<div class="form-group row">
																		<label for="name" class="col-sm-3 col-form-table">Name</label>
																		<div class="col-sm-7">
																			<input type="text" id="name" class="form-control" name="name"
																				placeholder="E.g. Fpt District 9 Campus" required="required" />
																		</div>
																		<p class="col-sm-1">*</p>
																	</div>
																	<div class="form-group row">
																		<label for="desc" class="col-sm-3 col-form-table">Description</label>
																		<div class="col-sm-8">
																			<textarea id="desc" class="form-control" name="description" required="required"
																				placeholder="E.g. This building is large."></textarea>
																		</div>
																		<p class="col-sm-1">*</p>
																	</div>
																	<div class="form-group row">
																		<label for="dayExpired" class="col-sm-3 col-form-label">Expired date</label>
																		<div class="col-sm-5">
																			<input type="date" class="default-date form-control" id="dayExpired"
																				name="dayExpired" required="required" />
																		</div>
																		<p class="col-sm-1">*</p>
																	</div>
																</div>
																<div class="modal-footer">
																	<button type="submit" class="btn btn-primary">Create</button>
																</div>
															</form>
														</div>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</c:forEach>
						</div>
					</div>
				</c:if>
			</div>
			<div id="messageWraper">
				<span class="success">${requestScope.uploadSuccess}${requestScope.createSuccess}</span>
			</div>

			<!-- create company modal -->
			<div class="modal fade" id="createCompanyModal" tabindex="-1" role="dialog"
				aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
				<div class="modal-dialog modal-dialog-centered" role="document">
					<div class="modal-content">
						<form action="${pageContext.request.contextPath}/company/create" method="post">
							<div class="modal-header">
								<h5 class="modal-title" id="exampleModalLongTitle">Create new company</h5>
								<button type="button" class="close" data-dismiss="modal" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
							</div>
							<div class="modal-body margin-middle">
								<div class="form-group row">
									<label for="name" class="col-sm-4 col-form-label">Company Name</label>
									<div class="col-sm-7">
										<input type="text" class="form-control" id="name" name="name" placeholder="E.g. Công ty phần mềm ABC">
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
		window.onload = function() {
			var i;
			var listOfDateInput = document
					.getElementsByClassName('default-date');
			var defaultDate = new Date(new Date().getTime() + 24 * 60 * 60
					* 1000 * 30 - new Date().getTimezoneOffset() * 60 * 1000)
					.toISOString().substr(0, 10);
			for (i = 0; i < listOfDateInput.length; i++) {
				listOfDateInput[i].value = defaultDate;
			}
		}
	</script>
</body>
</html>