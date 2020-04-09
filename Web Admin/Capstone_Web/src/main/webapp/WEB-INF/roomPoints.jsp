<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.7.0/css/all.css">
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css">
<script src="https://code.jquery.com/jquery-3.4.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>
</head>
<body>
	<script>
		window.onload = function() {
			var canvas = document.getElementById("canvas");
			var ctx = canvas.getContext("2d");

			var img = document.getElementById("imgMap");

			ctx.canvas.width = img.width;
			ctx.canvas.height = img.height;

			ctx.drawImage(img, 0, 0, img.width, img.height);

			img.style.display = "none";

			/* document.getElementById("mapDiv").remove(); */
		}

		function draw() {
			var canvas = document.getElementById("canvas");
			var ctx = document.getElementById("canvas").getContext("2d");
			var ratioX = document.getElementById("ratioX").value;
			var ratioY = document.getElementById("ratioY").value;
			ctx.beginPath();
			ctx.arc(ratioX * canvas.width, ratioY * canvas.height, 5, 0,
					Math.PI * 2, true)
			ctx.closePath();
			ctx.fill();

		}

		document.addEventListener("DOMContentLoaded", init, false);

		function init() {
			var canvas = document.getElementById("canvas");
			canvas.addEventListener("mousedown", getPosition, false);
		}

		function getPosition(event) {
			var x = new Number();
			var y = new Number();
			var canvas = document.getElementById("canvas");

			if (event.x != undefined && event.y != undefined) {
				x = event.x;
				y = event.y;
			} else // Firefox method to get the position
			{
				x = event.clientX + document.body.scrollLeft
						+ document.documentElement.scrollLeft;
				y = event.clientY + document.body.scrollTop
						+ document.documentElement.scrollTop;
			}

			x -= canvas.offsetLeft;
			y -= canvas.offsetTop;

			document.getElementById("ratioX").value = (x / canvas.width)
					.toFixed(5);
			document.getElementById("ratioY").value = (y / canvas.height)
					.toFixed(5);

			/* alert("x: " + (x / canvas.width) + "  y: " + (y / canvas.height); */
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
		}
	</script>

	<img id="imgMap" style="max-height: 99vh; max-width: 70vw;" src="${sessionScope.floor.linkMap }" />

	<div>
		<div style="float: left; width: 70%; text-align: center;">
			<canvas id="canvas"></canvas>
		</div>
		<div style="float: left; width: 29%; margin-left: 0.5%; margin-right: 0.5%;">
			<nav class="navbar navbar-dark bg-dark">
				<form class="form-inline">
					<a class="btn btn-light"
						href="${pageContext.request.contextPath}/location?id=${sessionScope.location.id }">
						<i class="fas fa-backward"> Quay lại</i>
					</a>
				</form>
			</nav>

			<hr class="col-xs-12">

			<form action="${pageContext.request.contextPath}/room" method="post">
				<h2>Thêm phòng cho "${sessionScope.location.name }"</h2>
				<input type="hidden" value="${sessionScope.location.id }" name="locationId">
				<div style="padding: 10px 10px 10px 10px;">
					<div class="form-group">
						<label for="ratioX">Ratio X</label>
						<input type="number" step="0.00001" class="form-control" id="ratioX" name="ratioX"
							placeholder="Tọa độ X" required="required" />
					</div>
					<div class="form-group">
						<label for="ratioY">Ratio Y</label>
						<input type="number" step="0.00001" class="form-control" id="ratioY" name="ratioY"
							placeholder="Tọa độ Y" required="required" />
					</div>
					<button class="btn btn-success" onclick="draw()" type="button">Biểu diễn trên bản đồ</button>
					<input id="imgSrc" type="hidden" value="${sessionScope.floor.linkMap }" />
					<button class="btn btn-secondary" onclick="clearCanvas()" type="button">Làm mới</button>
					<i class="fas fa-long-arrow-alt-left">Ấn vào hình để biết được tọa độ của điểm.</i>
				</div>

				<hr class="col-xs-12">

				<div class="form-group">
					<label for="name">Tên phòng</label>
					<input type="text" class="form-control" id="name" name="name" placeholder="Enter Location Name"
						required="required" />
				</div>
				<div class="form-check">
					<input class="form-check-input" type="checkbox" value="true" id="specialRoom"
						name="specialRoom" />
					<label class="form-check-label" for="specialRoom">Phòng đặc biệt</label>
				</div>
				<button type="submit" class="btn btn-primary">Tạo địa điểm</button>
			</form>
			<p style="color: green;">${requestScope.createSuccess }</p>

		</div>
	</div>

</body>
</html>