<%@ include file="headerInclude.jsp"%>

<LINK REL="STYLESHEET" TYPE="text/css" HREF="css/jSuggest.css">

<script language="javascript">
 $.isSession(); 
</script>

<form action="" method="post" name="form1" id="form1">
  <table width="100%" border="0" cellpadding="10" cellspacing="0" >
  <tr> <td height="20" class="tblheader">Patient List  <div id="selpatfltr" align="left">   </div></td></tr>
  <tr> <td height="20" >
       <div class="fllbl" style="padding-top:5px" > Facilities list:&nbsp;</div>
	 <div class="flinp" >
     <ul id="selFac" style="height:24px">
       <li>       <div id="cntFac">&nbsp;-&nbsp;</div>

       	 <ul id="selFacIn" style="width:220px; padding-left:3px">
			<%=sub.facin%>
         </ul>
       </li>
     </ul>  
     </div>

  
  </td></tr>

</table>
<div style="height:500px; overflow:scroll">
<table border="1" cellpadding="1" cellspacing="0" width="100%" id="pltbl">
 
</table>
</div>
<table border="0" cellpadding="0" cellspacing="0" width="100%" id="acctb3" bgcolor="#EEEEEE">
  <tr>
    <td height="30" colspan="5" align="center">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
     <td height="30" colspan="5" align="center">&nbsp;</td>

  </tr>
  <tr>
    <td height="30" colspan="6" align="right">&nbsp;  <input type="button" id="app_cod" value="New Group" onclick="$.selAggr('list');" /> &nbsp;&nbsp; </td>
  </tr>

</table>
<input name="pat_id_sel" id="pat_id_sel" type="hidden" value="" />

</form>
<script language="javascript">
	//alert(tblpatlist);
	
	$("#pltbl").html(tblpathead+"/n"+tblpatlist);
	
	$('#pltbl').click(function(e) {
    	var clicked = $(e.target).parent('tr');
   		var this_id = clicked.attr("id");
		if(this_id == undefined) return;
 	//alert(clicked.attr("id"));
		$("#pat_id_sel").val(this_id);
		$.selPatient(0);
	});
	$('#pltbl').mouseover(function(e) {
    	var clicked = $(e.target).parent('tr');
		var this_id = clicked.attr("id");
		if(this_id == undefined) return;

    	clicked.addClass("jSH");
    	//clicked.css('background', '#ffeaaa');

	});

	$('#pltbl').mouseout(function(e) {
    	var clicked = $(e.target).parent('tr');
		var this_id = clicked.attr("id");
		if(this_id == undefined) return;

    	clicked.removeClass("jSH");
		//clicked.css('background', '');
	});

//	$("#selpatfltr").html($("#tr1").data("selPatCnt") + " Patient(s) selected   " +$("#tr1").data("selPatFltr"));
	$("#pat").html($("#tr1").data("selPatCnt") + " Patient(s) selected   ");
	$("#selpatfltr").html($("#tr1").data("selPatFltr"));
	$("#selFac").menu({
		create: function( event, ui ) {
			$("#cntFac").html($(this).find("ul li").length+ " Items ");	
		},

  		select: function( event, ui ) {
			var id = $(this).attr("aria-activedescendant");
			var idarr = new Array();
        	idarr= id.split('-');
			if(idarr.length >1){
				if(idarr[0] === "fcl"){
					$("#mainFacList").multiselect("widget").find(":checkbox").each(function(){
						var val = this.value;
						if(val*1 == idarr[1]*1){
							if($(this).prop("checked") == false) this.click();
							//$("#mainFacList").multiselect("refrech");	
						}
   						//console.log("find checkbox val ", val);
					});	
				}
			}

		}
	});

</script>