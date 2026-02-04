var loadKek;
var downloadMasterKey;
var downloadWorkKey;
var calMac;
var keyIndexId;

$(document).ready(function()
{
	loadKek=$("#loadKek");
	downloadMasterKey= $("#downloadMasterKey");
	downloadWorkKey=$("#downloadWorkKey");
	calMac=$("#calMac");
	keyIndexId=$("#keyIndexId");
	
	keyIndexId.click(function(){
		setKeyIndex();
	});
	
	downloadWorkKey.click(function(){
		callLoadWorkKey();
	})
	
	downloadMasterKey.click(function(){
		callLoadMasterKey();
	});
	
	loadKek.click(function()
	{
		callLoadKek();
	});
	calMac.click(function(){
		callCalMac();
	})
});


function callLoadKek()
{
	url = "/mp63/load_kek";
var inputData ={"key":"3438336362623738623465343262373865653832363539363630636637653539","kcv":"314539373132"}
	

$.ajax({
  url:url,
  type:"POST",
  data:JSON.stringify(inputData),
  contentType:"application/json; charset=utf-8",
  dataType:"json",
  success: function(data, textStatus){
	try {
           
         alert(data.data);

        } catch (e) {
            alert("Output is not valid JSON: " + e);
        }
	
  },
      error: function (request, status, error) {
        alert("Error"+status+" Error:  "+error);
      },
      complete: function (response) {
        
      }
})	

	
	
}

function callLoadMasterKey()
{
	url = "/mp63/load_master_key";
	
	var inputData={"key":"DCA68732FA5C3D7C4FADE87D4C35F15A","kcv":"636B8475"}
	
$.ajax({
  url:url,
  type:"POST",
  data: JSON.stringify(inputData),
  contentType:"application/json; charset=utf-8",
  dataType:"json",
  success: function(data, textStatus){
	try {
           
         alert(data.data);

        } catch (e) {
            alert("Output is not valid JSON: " + e);
        }
	
  },
      error: function (request, status, error) {
        alert("Error"+status+" Error:  "+error);
      },
      complete: function (response) {
        
      }
})	

	
}

function callLoadWorkKey()
{
	url = "/mp63/load_work_key";
	
	
		var inputData={"pinKey":"5A03BE8CA46539D374A09DE646A1D781FA643500","macKey":"B7C60530D82A361516E938B5343D2F7770914381","tdkKey":"BEACB88C47037E8CF6364C12AC451E58BCAAB600"}//B7C60530D82A361516E938B5343D2F7770914381
		      //"macKey":"B7C60530D82A361516E938B5343D2F7770914381"                                                                //BEACB88C47037E8CF6364C12AC451E58 
	
$.ajax({
  url:url,
  type:"POST",
  data:JSON.stringify(inputData),
  contentType:"application/json; charset=utf-8",
  dataType:"json",
  success: function(data, textStatus){
	try {
           
         alert(data.data);

        } catch (e) {
            alert("Output is not valid JSON: " + e);
        }
	
  },
      error: function (request, status, error) {
        alert("Error"+status+" Error:  "+error);
      },
      complete: function (response) {
        
      }
})	

}
function callCalMac()
{
	url = "/mp63/cal_mac";
	var inputData = {"inputData":"0102030405060708"}
	
$.ajax({
  url:url,
  type:"POST",
  data:   JSON.stringify(inputData),
  contentType:"application/json; charset=utf-8",
  dataType:"json",
  success: function(data, textStatus){
	try {
           
         alert(data.data);

        } catch (e) {
            alert("Output is not valid JSON: " + e);
        }
	
  },
      error: function (request, status, error) {
        alert("Error"+status+" Error:  "+error);
      },
      complete: function (response) {
        
      }
})	
}
function setKeyIndex()
{
	url = "/mp63/key_index";
	var inputData ={"keyIndex":"0"}
		
$.ajax({
  url:url,
  type:"POST",
  data:  JSON.stringify(inputData),
  contentType:"application/json; charset=utf-8",
  dataType:"json",
  success: function(data, textStatus){
	try {
           
         alert(data.data);

        } catch (e) {
            alert("Output is not valid JSON: " + e);
        }
	
  },
      error: function (request, status, error) {
        alert("Error"+status+" Error:  "+error);
      },
      complete: function (response) {
        
      }
})	
}
