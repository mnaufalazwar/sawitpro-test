$(document).ready(function(){
	
	$("#simpleUpload").click(function(){
		$.ajax({
			url:'/uploadImages',
			success: function() {
				alert("File upload complete.");
			}
		});
	});

	$("#readImages").click(function(){
    		$.ajax({
    			url:'/readImageText',
    			success: function() {
    				alert("Images reading complete.");
    			}
    		});
    	});
	
});
