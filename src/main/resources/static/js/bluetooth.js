var buttonLoad;
var deviceListTitle;
var deviceList;
var connectDevice;
var disConnectDevice;
var selectVendorId;
var connectMode;
var isConnectDeviceId;



 var selectedDevice;
var connectionModeValue;
var selectedVendorId=-1;
$(document).ready(function()
{
	
	buttonLoad=$("#loadDevices");
	deviceListTitle=$("#deviceListTitle");
	deviceList=$("#deviceList");
	connectDevice=$("#connectDevice");
	disConnectDevice=$("#disConnectDevice");
	connectMode=$("#connectMode");
	selectVendorId=$("#selectVendorId");
	isConnectDeviceId=$("#isConnectDevice");
	
	isConnectDeviceId.click(function(){
		checkConnection();
	});
	
	selectVendorId.change(function(){
		selectedVendorId = $(this).children("option:selected").val();
		setVendorId();
	});
	
	connectMode.change(function(){
		if(selectedVendorId==-1)
		{
			$('select').prop('selectedIndex', 0);
			alert("Select Vendor Id");
		}else{
			connectionModeValue = $(this).children("option:selected").val();
			setConnectionMode();
		}
		
		
	});
	
	 deviceList.change(function(){
       selectedDevice = $(this).children("option:selected").val();
        //alert("You have selected the country - " + selectedCountry);
    });
	
	disConnectDevice.click(function(){
		if(selectedVendorId==-1)
		{
			alert("Select vendor Id");
		}
		else if (connectionModeValue==null)
		{
			alert("Select Connection Mode");
		}
	else if(selectedDevice==null&&connectionModeValue=='Bluetooth')
		{
			alert("Select Connection Device");
		}
		else
		{
			disconnectDevice();
		}
		
	});
	
	connectDevice.click(function()
	{
		if(selectedVendorId==-1)
		{
			alert("Select vendor Id");
		}
		else if (connectionModeValue==null)
		{
			alert("Select Connection Mode");
		}
		else if(selectedDevice==null&&connectionModeValue=='Bluetooth')
		{
			
			alert("Select Connection Device"+connectionModeValue);
		}
		else{
			connectSelectedDevice();
		}
		
		
	});
	
	
	buttonLoad.click(function()
	{
		loadDevicess();
	});
	
});

function loadDevicess()
{
	deviceList.empty();
   $("<option>").val("NA").text("Please Wait while device is searching.....").appendTo(deviceList);	
	//alert("Please Wait while device is searching.....");
	
	url = "/mp63/device_list";
	$.get(url, function(responseJson){
		deviceList.empty();
		$.each(responseJson, function(index, device){
			//alert(device.name);
			
			$("<option>").val(device.address).text(device.name).appendTo(deviceList);
		});
		
	}).done(function(){
		
		
	}).fail(function(){
		
	});
}
function disconnectDevice()
{
	url = "/mp63/disconnect_device";
	$.get(url, function(responseString)
	{
		alert(responseString);
		
	}).done(function(){
		
	}).fail(function(){
		
	});
	
}
 
function connectSelectedDevice()
{
	//alert("selec  "+selectedDevice);
	url = "/mp63/connect_device?mAddress="+selectedDevice;
	$.get(url, function(responseString)
	{
		alert(responseString);
		
	}).done(function(){
		//alert("done  "+selectedDevice);
		
	}).fail(function(){
		//alert("fail  "+selectedDevice);
	});
}

function setVendorId()
{
	url = "/mp63/request_vendor_id?vendorId="+selectedVendorId;
	$.get(url, function(responseString)
	{
		alert(responseString);
		
	}).done(function(){
		//alert("done  "+selectedDevice);
		
	}).fail(function(){
		//alert("fail  "+selectedDevice);
	});
}
function setConnectionMode()
{
	url = "/mp63/request_connection_mode?vendorId="+selectedVendorId+"&connectionMode="+connectionModeValue;
	$.get(url, function(responseString)
	{
		alert(responseString);
		
	}).done(function(){
		//alert("done  "+selectedDevice);
		
	}).fail(function(){
		//alert("fail  "+selectedDevice);
	});
}
function checkConnection()
{
	url = "/mp63/is_device_connected";
	$.get(url, function(responseString)
	{
		alert(responseString);
		
	}).done(function(){
		
	}).fail(function(){
		alert("Please check service started or not");
	});
}
