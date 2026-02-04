var signByPrivateKeys;
var verifyvendorId;
 var form_data;
var updateFirmwareId;

$(document).ready(function()
{
	signByPrivateKeys=$("#signDataId");
	verifyvendorId= $("#verifyvendorId");
	updateFirmwareId=$("#updateFirmwareId");
	
	signByPrivateKeys.click(function(){
		let file = document.getElementById("privateKey").files[0];
  if(file) {
          form_data = new FormData();
          form_data.append("file", file);
          form_data.append("data","969b5b7084e1cc34a24add51cacd8e93bc23950235c5b908edfe88c398048c6c");
         signData();
  } else { 
    alert("select a file..."); 
  }
		 
	});
	
	verifyvendorId.click(function(){
		
		verifyVendor();
	});
	
	
	
	updateFirmwareId.click(function(){
		alert("File Clicked");
	let file = document.getElementById("updateFirmwareFileId").files[0];
      if(file) {
          form_data = new FormData();
          form_data.append("file", file);
            alert("File Added");
               updateDeviceFirmware();
             } else { 
               alert("select a file..."); 
           }
		
	});
	
});
function signData()
{
	
    url = "/mp63/sign_data";
	$.ajax({
            url: url, // point to server-side controller method
            dataType: 'text', // what to expect back from the server
            cache: false,
            contentType: false,
            processData: false,
            data: form_data,
            type: 'post',
            //enctype: 'multipart/form-data',
            success: function (response) {
	              signedDataId.innerText=response
                  alert("Success"); // display success response from the server
                },
             error: function (response) {
              alert(response); // display error response from the server
               }
            });	

}
function verifyVendor()
{
	var form_data = new FormData();
	 form_data.append("signData", document.getElementById('signedDataId').innerHTML);//"0E9ADA6E6E5A7C54CB985E8590D7A51E244A6A25DA70DE0B1D828C9A6237C29C1AA2EC05D6F9F46B94FA5CB16819AF12944F50097BA7DCEF3ACF130B7C978DF3D8325425F90FA1CAA3191ED7D492BE32807788857B610CE4BCCC2D20C8E484823865140483CBA8CE8E758F8379A8EABE992B194A21340E30F9BC5AFFAAC10ED2C727703DDBFD77DB1E56417CD2D0E03D806A2DC93D12710C492D3969F29EF97F8203049D152DF26C19FF52E4C4015386192E0442F6CBDE8366D510C500038E4DB9B9794027EFBD81C5C4ACB35C50CB401EE20E126491E5A2567F18EF42D294DA065ABE24FA635206E3697051F651293E03D8563E340A8DACC3DBD42E9A168C8B");
          form_data.append("plainData","969b5b7084e1cc34a24add51cacd8e93bc23950235c5b908edfe88c398048c6c");
	url = "/mp63/verify_vendor";
	$.ajax({
            url: url, // point to server-side controller method
            dataType: 'text', // what to expect back from the server
            cache: false,
            contentType: false,
            processData: false,
            data: form_data,
            type: 'post',
            //enctype: 'multipart/form-data',
            success: function (response) {
	               //signedDataId.data=response
                    document.getElementById('signedDataId').innerHTML=response
                  alert(response); // display success response from the server
                },
             error: function (response) {
              alert(response); // display error response from the server
               }
            });	
}

function updateDeviceFirmware()
{
	alert("File Added");
    url = "/mp63/update_firmware";
	$.ajax({
            url: url, // point to server-side controller method
            dataType: 'text', // what to expect back from the server
            cache: false,
            contentType: false,
            processData: false,
            data: form_data,
            type: 'post',
            //enctype: 'multipart/form-data',
            success: function (response) {
	              
                  alert(response); // display success response from the server
                },
             error: function (response) {
              alert(response); // display error response from the server
               }
            });		
	
	
	
}