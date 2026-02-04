var timeId;
var showTextId;
var cancelId;
var deviceInfo;

$(document).ready(function(){
	timeId=$("#timeId");
	showTextId=$("#showTextId");
	cancelId=$("#cancelId");
	deviceInfo=$("#deviceInfoId");
	
	deviceInfo.click(function()
	{
		
		//alert("Device Info");
		showDeviceInfo();
	});
	
	cancelId.click(function(){
		cancel();
	});
	
	timeId.click(function(){
		setTime();
	});
	showTextId.click(function()
	{
		showText();
	});
	
	
	
});


function setTime()
{
	url = "/mp63/set_time";
	$.get(url, function(responseString)
	{
		alert(responseString);
		
	}).done(function(){
		
	}).fail(function(){
		
	});
}

function showText()
	{
	url = "/mp63/show_text";
	$.get(url, function(responseString)
	{
		alert(responseString);
		
	}).done(function(){
		
	}).fail(function(){
		
	});
	}
function cancel()
{
	url = "/mp63/cancel";
	$.get(url, function(responseString)
	{
		alert(responseString);
		
	}).done(function(){
		
	}).fail(function(){
		
	});
}

function showDeviceInfo()
{
	//alert("Device Info");
	url = "/mp63/device_info";
	$.get(url, function(responseJson)
	{
		alert("Device Info: "+JSON.stringify(responseJson));
		
	}).done(function(){
		
	}).fail(function(){
		
	});
}