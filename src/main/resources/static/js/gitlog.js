$("#gitlog").click(function () {
	$.blockUI({ message: '<div><img src="/img/loading.gif" alt="loading"></div>' });
	$.post({
		url: "/",
		cache: false,
		ifModified: true,
		data: {
			"type": $("#type").val(),
			"startDate": $("#startDate").val(),
			"endDate": $("#endDate").val()
		},
		success: function (result) {
			if (result.success) {
				$(".table tr:gt(0)").remove();
				if (result.data.length != 0) {
					for (var int = 0; int < result.data.length; int++) {
						var row = result.data[int];
						var newRow = "<tr><td>" + row.name + "</td>" +
							"<td>" + row.percent + "</td>" +
							"<td>" + row.add + "</td>" +
							"<td>" + row.delete + "</td>" +
							"<td>" + row.fileCount + "</td>" +
							"<td>" + row.firstCommit + "</td>" +
							"<td>" + row.lastCommit + "</td></tr>";
						$(".table tr:last").after(newRow);
					}
				} else {

				}
			} else {
				alert(result.message)
			}
			$.unblockUI();
		}
	});

});

var remain = 0;
var int;

function setProgress() {
	$.post({
		url: "/getRemain",
		cache: false,
		ifModified: true,
		success: function (result) {
			if (result.success) {
				var a = (remain * 100 - result.data * 100) / remain;
				$(".progress-bar").css("width", a + "%");
				if (result.data == 0) {
					clearInterval(int);
					$(".progress-bar").removeClass("progress-bar-success");
					$(".progress-bar").addClass("progress-bar-success");
				}
			} else {
				alert(result.message)
			}
		}
	});
}


$("#pull").click(function () {
	$.blockUI({ message: '<div><img src="/img/loading.gif" alt="loading"></div>' });
	$.post({
		url: "/pullProjects",
		cache: false,
		ifModified: true,
		success: function (result) {
			if (result.success) {
				remain = result.data;
				$(".progress-bar").css("width", "0%");
				$(".progress-bar").removeClass("progress-bar-success");
				$(".progress-bar").addClass("progress-bar-striped");
				$("#progress").removeClass("hidden");
				int = self.setInterval("setProgress()", 1000);
			} else {
				alert(result.message)
			}
		}
	});
	$.unblockUI();
});

