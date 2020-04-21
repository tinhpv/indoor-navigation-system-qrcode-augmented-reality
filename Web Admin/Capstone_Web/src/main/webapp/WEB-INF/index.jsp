<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Index Page</title>
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
			<div>
				<img src="${pageContext.request.contextPath}/images/icon.png" />
				<h2>Placeholder*</h2>
			</div>
		</div>
		<div class="content">
			<div class="content-header shadow p-3 mb-3 bg-white rounded">
				<h1>INQR Dashboard</h1>
			</div>
			<div class="wraper">
				<div id="actionWraper">
					<button class="btn btn-outline-success" data-toggle="modal" data-target="#createCompanyModal">
						<i class="fas fa-plus"> New Company</i>
					</button>
				</div>
				<div id="messageWraper">
					<span class="success">${requestScope.uploadSuccess }</span>
				</div>
			</div>
			<!-- <input style="float: right;" id="txtCompany" type="text" onkeyup="filterCompanyListByName()"
				placeholder="Search company"> -->
			<div class="content-body shadow p-3 mb-3 bg-white rounded">
				<c:if test="${not empty sessionScope.companyList }">
					<div id="company-list" class="list-group">
						<div id="list-header" class="list-group-item">
							<h5>
								List of all companies
								<a href="${pageContext.request.contextPath}/company/getAllCompanies">
									<i class="fas fa-sync-alt"></i>
								</a>
							</h5>
						</div>
						<div class="list-group list-group-horizontal-lg">
							<div id="company-list-header-1" class="list-group-item">Company name</div>
							<div id="company-list-header-2" class="list-group-item">More Detail</div>
						</div>
						<c:forEach var="companyDto" items="${sessionScope.companyList }" varStatus="counter">
							<div class="list-group list-group-horizontal-lg">
								<div id="company-list-item-1" class="list-group-item">${companyDto.name }
									<span class="badge badge-primary badge-pill" data-toggle="tooltip" data-placement="bottom"
										title="This company have ${companyDto.listBuilding.size()} building(s)">${companyDto.listBuilding.size()}
										building(s)</span>
								</div>
								<button id="company-list-item-2" data-toggle="modal"
									data-target="#buildingListModal${counter.count}"
									class="list-group-item list-group-item-action">
									<i class="fas fa-info-circle"></i>
								</button>
							</div>

							<!-- Company Building Modal -->
							<div class="modal fade" id="buildingListModal${counter.count}" tabindex="-1" role="dialog"
								aria-labelledby="Building List" aria-hidden="true">
								<div class="modal-dialog modal-lg" role="document">
									<div class="modal-content">
										<div class="modal-header">
											<h5 class="modal-title">Information of ${companyDto.name }</h5>
											<button type="button" class="close" data-dismiss="modal" aria-label="Close">
												<span aria-hidden="true">&times;</span>
											</button>
										</div>
										<div class="modal-body">
											<div id="buildingWraper" class="shadow p-3 mb-3 bg-white rounded">
												<form action="${pageContext.request.contextPath}/company/update" method="post">
													<div id="form-header">
														<h5>Edit company information</h5>
													</div>
													<div class="form-group row">
														<label class="col-sm-4 col-form-label">Company name</label>
														<input type="hidden" name="id" value="${companyDto.id }" />
														<div class="col-sm-7">
															<input type="text" class="form-control" required="required" name="name"
																value="${companyDto.name }" />
														</div>
														<p class="col-sm-1">*</p>
													</div>
													<button type="submit" class="btn btn-primary">Save Changes</button>
												</form>
											</div>

											<div id="actionWraper">
												<a
													href="${pageContext.request.contextPath}/building/create?companyId=${companyDto.id}&companyName=${companyDto.name}"
													type="button" class="btn btn-outline-success" data-toggle="tooltip"
													data-placement="top" title="Create a new building for this company">
													<i class="fas fa-plus"> New Building</i>
												</a>
											</div>

											<div id="building-list" class="list-group">
												<div id="list-header" class="list-group-item">
													<h5>All building(s) of ${companyDto.name}</h5>
												</div>
												<c:forEach var="buildingDto" items="${companyDto.listBuilding }">
													<div class="list-group list-group-horizontal-lg">
														<div id="building-list-item-1" class="list-group-item">${buildingDto.name}</div>
														<a
															href="${pageContext.request.contextPath}/building/getQRCode/${buildingDto.name}?id=${buildingDto.id }"
															id="building-list-item-2" data-toggle="tooltip" data-placement="bottom"
															title="Download QR Codes of this building" class="list-group-item">
															<i class="fas fa-download"></i>
														</a>
														<a
															href="${pageContext.request.contextPath}/building/getBuilding?buildingId=${buildingDto.id}"
															id="building-list-item-3" class="list-group-item" data-toggle="tooltip"
															data-placement="bottom" title="Manage this building">
															<i class="fas fa-info-circle"></i>
														</a>
													</div>
												</c:forEach>
											</div>
										</div>
									</div>
								</div>
							</div>
						</c:forEach>
					</div>
				</c:if>
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
							<div class="modal-body">
								<div class="form-group row">
									<label for="name" class="col-sm-5 col-form-label">Company Name</label>
									<div class="col-sm-7">
										<input type="text" class="form-control" id="name" name="name">
									</div>
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
		</div>
	</div>
	<script type="text/javascript">
		$(function() {
			$('[data-toggle="tooltip"]').tooltip()
		})
	</script>
</body>
</html>