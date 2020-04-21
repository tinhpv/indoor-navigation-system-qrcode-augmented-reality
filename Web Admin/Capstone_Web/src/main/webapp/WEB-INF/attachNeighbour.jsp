<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/point.css" />
<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.7.0/css/all.css">
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css">
<script src="https://code.jquery.com/jquery-3.4.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>
</head>
<body>
	<c:if test="${empty sessionScope.building }">
		<c:redirect url="${pageContext.request.contextPath}/"></c:redirect>
	</c:if>

	<img id="imgMap" src="${sessionScope.floor.linkMap }" />
	<input id="imgSrc" type="hidden" value="${sessionScope.floor.linkMap }" />

	<div>
		<div id="canvasWraper">
			<canvas id="canvas"></canvas>
		</div>
		<div id="content">
			<div id="content-header" class="shadow p-3 mb-3 bg-white rounded">
				<a class="btn btn-outline-success" href="${pageContext.request.contextPath}/floor">
					<i class="fas fa-arrow-circle-left"> Back</i>
				</a>
			</div>

			<div id="content-body" class="shadow p-3 mb-3 bg-white rounded">
				<form action="${pageContext.request.contextPath}/neighbour/create" method="post">
					<div id="content-body-header">
						<h5>Attach neighbor to ${sessionScope.location.name }</h5>
					</div>
					<div class="form-group row">
						<label for="myInput" class="col-sm-2 col-form-label">Search </label>
						<div class="col-sm-7">
							<input class="form-control" type="text" id="myInput" onkeyup="myFunction()"
								placeholder="Find location">
						</div>
					</div>
					<div id="allLocation" class="list-group">
						<div class="list-group-item active">All locations</div>
						<c:forEach var="locationDto" items="${sessionScope.filterdList }">
							<c:if test="${locationDto.id != sessionScope.location.id}">
								<a class="list-group-item list-group-item-action py-3"
									onclick="document.getElementById('neighbourId').value='${locationDto.id}'; document.getElementById('neighbourName').value='${locationDto.name}'; draw2(${sessionScope.location.ratioX } ,${sessionScope.location.ratioY } , '', ${locationDto.ratioX}, ${locationDto.ratioY });">
									${locationDto.name } </a>
							</c:if>
						</c:forEach>
					</div>
					<input type="hidden" class="form-control" id="neighbourId" name="id" placeholder="ID"
						readonly="readonly">
					<div class="form-group row">
						<label for="orientation" class="col-sm-3 col-form-label">Neighbor </label>
						<div class="col-sm-7">
							<input type="text" required="required" class="form-control" id="neighbourName"
								placeholder="Neighbor" name="name" onkeydown="return false;">
						</div>
						<p class="col-sm-1">*</p>
					</div>
					<div class="form-group row">
						<label for="orientation" class="col-sm-3 col-form-label">Orientation </label>
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
						<label for="distance" class="col-sm-3 col-form-label">Distance </label>
						<div class="col-sm-4">
							<input type="number" required="required" step="0.01" class="form-control" name="distance"
								placeholder="Distance">
						</div>
						<p class="col-sm-1">*</p>
					</div>
					<input type="hidden" name="locationId" value="${sessionScope.location.id }" />
					<div class="action-wraper">
						<button type="button" class="left btn btn-success" data-toggle="tooltip"
							data-placement="bottom" title="All neighbours of this location" onclick="showAllNeighbors()">
							<i class="fas fa-search-location"></i>
						</button>
						<button type="button" class="left btn btn-secondary" data-toggle="tooltip"
							data-placement="bottom" title="Clear picture" onclick="clearCanvas()">
							<i class="fas fa-sync-alt"></i>
						</button>
						<button type="submit" class="right btn btn-primary">Attach Neighbor</button>
					</div>
				</form>
			</div>
		</div>
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
	
		window.onload = function() {
			var canvas = document.getElementById("canvas");
			var ctx = canvas.getContext("2d");

			var img = document.getElementById("imgMap");

			ctx.canvas.width = img.width;
			ctx.canvas.height = img.height;

			ctx.drawImage(img, 0, 0, img.width, img.height);

			img.style.display = "none";

			drawCurrent(${sessionScope.location.ratioX } ,${sessionScope.location.ratioY });
		}
		
		function showAllNeighbors() {
			var neighbourId = [], neighbourData = [], neighbourInfo = [], i;
			<c:forEach items="${sessionScope.location.listLocationBeside}" var="neighbourDto">
			neighbourId.push("${neighbourDto.id}");
			</c:forEach>
			<c:forEach items="${sessionScope.floor.listLocation}" var="locationDto">
			if (neighbourId.includes("${locationDto.id}")) {
				neighbourInfo.push("${locationDto.name}" , ${locationDto.ratioX}, ${locationDto.ratioY});
				neighbourData.push(neighbourInfo);
				neighbourInfo = [];
			}
			</c:forEach>
			
			for (i = 0; i < neighbourData.length;i++) {
				draw2(${sessionScope.location.ratioX }, ${sessionScope.location.ratioY }, neighbourData[i][0], neighbourData[i][1], neighbourData[i][2]);
			}
		}

		function draw2(startRatioX, startRatioY, neighbourName, endRatioX, endRatioY) {
			var canvas = document.getElementById("canvas");
			var ctx = document.getElementById("canvas").getContext("2d");
			ctx.beginPath();
			ctx.fillStyle = "#FF0000";
			ctx.arc(startRatioX * canvas.width, startRatioY * canvas.height, 4, 0,
					Math.PI * 2, true)
			ctx.closePath();
			ctx.fill();
			
			ctx.beginPath();
			ctx.fillStyle = "#000000";
			ctx.arc(endRatioX * canvas.width, endRatioY * canvas.height, 4, 0,
					Math.PI * 2, true)
			ctx.closePath();
			ctx.fill();

			ctx.beginPath();
			ctx.lineWidth = 3;
			ctx.moveTo(startRatioX * canvas.width, startRatioY * canvas.height);
			ctx.lineTo(endRatioX * canvas.width, endRatioY * canvas.height);
			ctx.stroke();
			ctx.closePath();
			
			ctx.font = "10px Arial";
			ctx.textAlign = "center";
			ctx.fillText(neighbourName, endRatioX * canvas.width, (endRatioY * canvas.height) + 15);
		}
		
		function drawCurrent(ratioX, ratioY) {
			var canvas = document.getElementById("canvas");
			var ctx = document.getElementById("canvas").getContext("2d");
			ctx.beginPath();
			ctx.fillStyle = "#FF0000";
			ctx.arc(ratioX * canvas.width, ratioY * canvas.height, 5, 0,
					Math.PI * 2, true)
			ctx.closePath();
			ctx.fill();
		}

		function clearCanvas() {
			var canvas = document.getElementById("canvas");
			var ctx = document.getElementById("canvas").getContext("2d");
			ctx.clearRect(0, 0, canvas.width, canvas.height);

			var img = new Image;
			img.src = document.getElementById("imgSrc").value;
			img.style.maxWidth = "70vw";
			img.style.maxHeight = "99vh";

			ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
			
			drawCurrent(${sessionScope.location.ratioX } ,${sessionScope.location.ratioY });
		}
		

		$(function() {
			$('[data-toggle="tooltip"]').tooltip()
		})
	</script>
</body>
</html>