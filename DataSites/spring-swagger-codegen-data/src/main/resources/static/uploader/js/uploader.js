function dropHandler(ev) {
  console.log('File(s) dropped');

  // Prevent default behavior (Prevent file from being opened)
  ev.preventDefault();

  if (ev.dataTransfer.items) {
    // Use DataTransferItemList interface to access the file(s)
    for (var i = 0; i < ev.dataTransfer.items.length; ++i) {
      // If dropped items aren't files, reject them
      if (ev.dataTransfer.items[i].kind === 'file') {
        uploadFile(ev.dataTransfer.items[i].getAsFile());
      }
    }
  } else {
    // Use DataTransfer interface to access the file(s)
    for (var i = 0; i < ev.dataTransfer.files.length; ++i) {
			uploadFile(ev.dataTransfer.files[i]);
    }
  }
}

function uploadFile(file) {
	$.ajax({
	        url: "/files",
	        type: "POST",
	        data: file,
	        processData: false,
					contentType : "application/octet-stream",
	        success: function(){
	           alert("Upload completed.");
						 listFiles();
	        },
	        error:function(){
	          alert("Upload failed.");
	        }
	    });
}

function dragOverHandler(ev) {
  ev.preventDefault(); // Prevent default behavior (Prevent file from being opened)
}

function listFiles() {
	$.ajax({
	        url: "/files",
	        type: "GET",
	        success: function(data){
						filesListed(data);
	        },
	        error:function(){
	          console.log("File listing failed.");
	        }
	    });
}

function filesListed(data){
	var parts = window.location.href.split("/");
	var baseUri = parts[0]+"//"+parts[2]+"/files/";
	var fileList = $("#file-list");
	fileList.empty();
	for(let i=0;i<data.length;++i){
		let file = data[i];
		fileList.append("<p class=\"image\" id=\""+file.id+"\"><img src=\"/files/"+file.id+"\" height=\"300px\"><br>URL: "+baseUri+file.id+"<br>Uploaded: "+file.timestamp+"</p>")
	}
}
