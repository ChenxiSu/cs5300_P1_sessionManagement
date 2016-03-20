/**
 * 
 */
function validation(){
	var text = document.getElementById("inputText").value;
	var len = text.length;
	if(len>12) {
		console.log("faf");
		document.getElementById("warning").innerText("words limit has been reached!")
	}else{
		document.getElementById("warning").innerText("");
	}
	
}
/**
 * @returns {Boolean}
 */
function alertOverLong(){
	var text = document.getElementById("inputText").value;
	var len = text.length;
	if(len>12) {
		alert("Please limit your letters within 512!");
		return false;
	}
	
}