<%@ include file="headerInclude.jsp"%>
<%
String ron="class='tr_ron'";
String ros="class='tr_ron' disabled";
    romspkg.combob cmb;
    if(sub.pt != null){
        cmb = sub.pt.cb;
   	int i;
	romspkg.patient pt = sub.pt;
%>

<style type="text/css">
<!--
.style2 {
	font-size: 10px;
	font-family: Arial, Helvetica, sans-serif;
}
-->
</style>
<LINK REL="STYLESHEET" TYPE="text/css" HREF="css/styles.css">
<link href="css/jquery-ui.css" rel="stylesheet">

<form action="" method="post" name="testform" id="testform">
<table width="100%" border="0" cellpadding="0" cellspacing="0" bgcolor="#EEEEEE" >
<tr> <td height="45" class="tblheader">Patient Profile </td>
<td class="tblheader" style="text-align:right">
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

</td>
</tr>
</table>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr border="1" >
     <td height="40"  nowrap="nowrap" width="50px">
	 <div class="fllbl" > Patient ID:&nbsp;</div></td>
     <td height="40" colspan=1 nowrap="nowrap" class='lpad' >

<!--	 <div class="flinp" style="width:50px;"><input type="text" class="tr_ro1" name="tag" value="<%=pt.patID%>" size=5 readonly="readonly" > </div>
-->
	 <div class="flinp" style="width:40px; font-weight:bold"><%=pt.patID%></div>
</td>
<td height="40"  nowrap="nowrap" width="50px">
	 <div class="fllbl" > Password:&nbsp;</div></td>
<td height="40" colspan=2  nowrap="nowrap" class='lpad' >
     <div class="flinp" style="width:50px; font-weight:bold">
	   <input type=text <%=ros%> name="pwd" id="pwd"  onFocus="this.select();" value="<%=pt.pwd%>" size="15" />
   </div>
</td>

<td height="40"   nowrap="nowrap" width="50px">
	 <div class="fllbl" > E-mail:&nbsp;</div></td>
<td height="40" colspan=3  nowrap="nowrap" class='lpad' >
     <div class="flinp" style="width:50px; font-weight:bold">
	   <input type=text <%=ros%> name="email" id="email"  onFocus="this.select();" value="<%=pt.email%>" size="25" />
   </div>
</td>

</tr>

  <tr border="1" >
     <td height="40"  nowrap="nowrap">
     <div class="fllbl" >Last name:&nbsp;</div></td>
     <td height="40" colspan=9  nowrap="nowrap" class='lpad' >
     <div class="flinp" style="width:300px;" >
     	<input type="text" <%=ros%> name="ln" id="ln" value="<%=pt.Last_Name%>" size="40" tabindex="1">
     </div>
     <div class="fllbl" > First name:&nbsp;</div>
     
	 <div class="flinp" style="width:240px;">
     <input type="text" <%=ros%> name="fn" id = "fn" value="<%=pt.First_Name %>" size="40" tabindex="3">
     </div>
      
     </td>	 
 </tr>
 <tr border="1" >
 <td height="40"  nowrap="nowrap">
      <div class="fllbl" >Mid. name:&nbsp;</div>
      </td>
     <td height="40" colspan=9  nowrap="nowrap" class='lpad' >
	 <div class="flinp" style="width:140px;">
     <input type="text" <%=ros%> name="mn" id="mn" value="<%=pt.Middle_Name%>" size="20" tabindex="3"></div>
     </td>
	 
 </tr>
 <tr>
     <td><div class="fllbl">Gender:&nbsp; <br>&nbsp;</div> </td>
          <td height="40" colspan=9  nowrap="nowrap" class='lpad' >
<div class="flinp" style="width:170px;" >
    <select id="gnd" name="gnd" size="1"  <%=ros%> tabindex="4">
         <% cmb.cb[0].setSelVal((pt.Gender));
		 for (i=0; i< cmb.cb[0].size ;i++)out.print(cmb.cb[0].oprw[i]);  %>
    </select>
 </div> 
 
<div class="fllb0" style="width:90px;" >Occupation:&nbsp;</div>
<div class="flinp" style="width:190px;"> 
     <select id="occ" name="occ"  size="1"  <%=ros%> tabindex="4"> 
        <% cmb.cb[3].setSelVal((sub.pt.Occupation));
		for (i=0; i< cmb.cb[3].size ;i++)out.print(cmb.cb[3].oprw[i]);  %>
     </select>
     <img src="pics/add1.gif" width="18" height="16" alt="Add new item" onclick="$.addCod('occ',3)" vspace="-10px" style="cursor:pointer; vertical-align:top;"/>
</div>
<div class="fllb0" style="width:100px;" >Date of Birth: &nbsp; <br>(mm/dd/yyyy)&nbsp; </div>
<div class="flinp" style="width:100px;" name="data">
      <input id="bth" type="text" <%=ros%> name="bth" value="<%=pt.Date_Birth%>" size="15"
      onkeydown="$.showCalKey(event,this);" ondblclick="$.showCal(this);" tabindex="6" onfocus="this.select();"/>
      </div>
            
      </td>
  </tr>
  <tr>
     <td height="40"><div class="fllbl">
	 Marital status:&nbsp;</div> </td>
     <td height="40" colspan=9  nowrap="nowrap" class='lpad' > 
     <div class="flinp" style="width:150px;">   
     <select id="mrst" name="mrst" size="1"  <%=ros%> tabindex="7"> 
        <% cmb.cb[1].setSelVal((pt.Marital_Status));
		for (i=0; i< cmb.cb[1].size ;i++)out.print(cmb.cb[1].oprw[i]);   %>
     </select>
     <img src="pics/add1.gif" width="18" height="16" alt="Add new item" onclick="$.addCod('mrst',1)" 
     style="cursor:pointer; vertical-align:top;"/>
     </div>
	 <div class="fllb0" style="width:100px;" >Language:&nbsp;</div> 
     <div class="flinp" style="width:110px;">
     <select id="lng" name="lng" size="1"  <%=ros%> tabindex="8"> 
        <% cmb.cb[2].setSelVal((pt.Language));
		for (i=0; i< cmb.cb[2].size ;i++)out.print(cmb.cb[2].oprw[i].toUpperCase());   %>
        </select>
     <img src="pics/add1.gif" width="18" height="16" alt="Add new item" onclick="$.addCod('lng',2)"
      vspace="-10px" style="cursor:pointer; vertical-align:top;"/>   
     </div>
       
     <div class="fllb0" style="width:90px;" >Home phone:&nbsp;</div>
     <div class="flinp" style="width:130px;">
     <input name="phon" type="tel" <%=ros%> id="tel" value="<%=pt.Home_Phone%>" size="18" maxLength=18 tabindex="9" onBlur="chkPhone(this.value,this.id);" />
     </div>
     </td>	 
  </tr>
  <tr>
     <td height="40" ><div class="fllbl">
	 Street adress:&nbsp;</div> </td>
     <td height="40" colspan=9  nowrap="nowrap" class='lpad' >
     <input id="str" type=text <%=ros%> name="str" value="<%=pt.Street%>" size=100 tabindex="10">
	 </td>
  </tr>
  <tr bordercolor="#006666">
    <td height="40" >
      <div class="fllbl">City/Town:&nbsp;</div> </td>
       <td height="40" colspan=9  nowrap="nowrap" class='lpad' >  
       <div class="flinp" style="width:180px;">  
       <select id="cit" name="cit" size="1"  <%=ros%>  tabindex="11"> 
        <% cmb.cb[6].setSelVal((pt.City));
		for (i=0; i< cmb.cb[6].size ;i++)out.print(cmb.cb[6].oprw[i].toUpperCase());   %>
         </select>
         <img src="pics/add1.gif" width="18" height="16" alt="Add new item" onclick="$.addCod('cit',6, $('#cit').val())"
         style="cursor:pointer; vertical-align:top;"/>
      </div>
    <div class="fllb0" style="width:80px;" >Province:&nbsp;</div>
    <div class="flinp" style="width:230px;"> 
     <select name="prov" id="prov" size="1" <%=ros%> tabindex="12"> 
        <% cmb.cb[7].setSelVal((pt.Province));
		for (i=0; i< cmb.cb[7].size ;i++)out.print(cmb.cb[7].oprw[i].toUpperCase());   %></select>
	</div>
    <div class="fllb0" style="width:90px; border:thin;" >Postal code:&nbsp;</div>
     <div class="flinp" style="width:60px; border:thin;"> 
  <input type=text <%=ros%> name="pst" id="pst"  onFocus="this.select();"
  value="<%=pt.Postal.toUpperCase()%>" size=10 tabindex="13"/></div>
</td>
</tr>
<tr>
     <td height="40"><div class="fllbl">
	 Insurer:&nbsp;</div></td> 
     <td height="40" colspan=9  nowrap="nowrap" class='lpad' >
     <div class="flinp" style="width:380px;">
        <select id="insr" name="insr" size="1"  <%=ros%> tabindex="14"> 
        <% cmb.cb[5].setSelVal((pt.Insurer));
		for (i=0; i< cmb.cb[5].size ;i++)out.print(cmb.cb[5].oprw[i]);   %></select>
        <img src="pics/add1.gif" width="18" height="16" alt="Add new item" onclick="$.addCod('insr',5)" vspace="-10px" style="cursor:pointer; vertical-align:top;"/> </div>
	 <div class="fllb0" style="width:60px;" >Sector:&nbsp;</div>
     <div class="flinp" style="width:110px;">     
          <select id="futp" name="futp" size="1" <%=ros%> tabindex="15"> 
        <% cmb.cb[8].setSelVal((pt.FundingType));
		for (i=0; i< cmb.cb[8].size ;i++)out.print(cmb.cb[8].oprw[i]);   %></select>
        <img src="pics/add1.gif" width="18" height="16" alt="Add new item" onclick="$.addCod('futp',8)"
         vspace="-10px" style="cursor:pointer; vertical-align:top;"/>        
     </div>
     </td>
</tr>
<tr>
    <td height="40" ><div class="fllbl">
	 Employer:&nbsp;</div></td>
    <td height="40" colspan=9  nowrap="nowrap" class='lpad' > 
    <div class="flinp" style="width:110px;">
    <select id="empl" name="empl" size="1"  <%=ros%> tabindex="16"> 
        <% cmb.cb[12].setSelVal((pt.Employer));
		for (i=0; i< cmb.cb[12].size ;i++)out.print(cmb.cb[12].oprw[i]);  %></select>
         <img src="pics/add1.gif" width="18" height="16" alt="Add new item" 
         onclick="$.addCod('empl',12)" vspace="-10px" style="cursor:pointer; vertical-align:top;"/>
        </div>

     </td>
</tr>
<tr>
<td><div class="fllbl">
Claim Number:&nbsp;</div> </td>
     <td height="20" colspan=9  nowrap="nowrap" class='lpad' >
     <div class="flinp" style="width:160px;">
   <input type=text <%=ros%> id="clnm" name="clnm" value="<%=pt.Cliam%>" size=20 tabindex="17"></div>
   <div class="fllb0" style="width:200px;" >Case Management Firm:&nbsp;</div>
      <div class="flinp" style="width:110px;">     
          <select id="csmn" name="csmn" size="1"  <%=ros%> tabindex="18"> 
        <% cmb.cb[9].setSelVal((pt.CaseMgtFirm));
		for (i=0; i< cmb.cb[9].size ;i++)out.print(cmb.cb[9].oprw[i]);   %></select>
        <img src="pics/add1.gif" width="18" height="16" alt="Add new item" onclick="$.addCod('csmn',9)"
         vspace="-10px" style="cursor:pointer; vertical-align:top;"/>
     </div>
 
</td>
</tr>
<tr>
     <td height="40"><div class="fllbl">
      Family Doctor:&nbsp;</div> </td>
     <td height="40" colspan=9  nowrap="nowrap" class='lpad' >
     <div class="flinp" style="width:240px;">
        <select id="fmdr" name="fmdr" size="1" <%=ros%> tabindex="19"> 
        <% cmb.cb[11].setSelVal((pt.FamilyDoctor));
		for (i=0; i< cmb.cb[11].size ;i++)out.print(cmb.cb[11].oprw[i]);   %></select>
        <img src="pics/add1.gif" width="18" height="16" alt="Add new item" onclick="$.addCod('fmdr',11)"
         style="cursor:pointer; vertical-align:top;"/>
		</div>
       
	 <div class="fllb0" style="width:100px;" >Referred By:&nbsp;</div>
      <div class="flinp" style="width:110px;">  
      <select id="rfrd" name="rfrd" size="1" <%=ros%> tabindex="20"> 
        <% cmb.cb[10].setSelVal((pt.ReferredBy));
		for (i=0; i< cmb.cb[10].size ;i++)out.print(cmb.cb[10].oprw[i]);  %></select>
        <img src="pics/add1.gif" width="18" height="16" alt="Add new item" onclick="$.addCod('rfrd',10)"
         style="cursor:pointer; vertical-align:top;"/>
		</div>
     </td>
</tr>
<tr><td colspan="110">&nbsp;</td>

</tr>
</table>
<table border="0" cellpadding="0" cellspacing="0" width="100%" id="acctb3">
  <tr>
    <td height="30" colspan="5" align="center">&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="button" id="edtbtn"  name="edtbtn" value="Edit" onClick="$.editForm();"
    />
    &nbsp;&nbsp;
    <input type="button" id="delbtn" name="delbtn" value="Delete" onClick="$.delFormPat()"
    />
    &nbsp;&nbsp;
    <input disabled="true" type="button" name="savbtn" id="savbtn" value="Save" tabindex="30"  onClick="$.saveFormPat()" />
    &nbsp;&nbsp;
    <input disabled="true" type="button" name="clsbtn" value="Cancel" tabindex="31" id = "clsbtn"  onClick="$.resForm()" />
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    
    &nbsp;&nbsp;</td>
  </tr>
</table>
 <input type="hidden"  name="tag" value="<%=pt.patID%>" > 
</form>
 <span class="tooltip" id="tooltip">This is tooltip.</span>
<input id="urlsave" type="hidden" value="patsave" />
<input id="urldel" type="hidden" value="patdel" />
<%}// end if statement%>

<script type="text/javascript">
if ($("#bth")){
    $('#bth').mask('99/99/9999').blur(function(){ValidateDate(this);}); 
    $("#fn,#ln,#mn,#str,#clnm").on('keyup',function() {
        this.value = this.value.toUpperCase();
        return false;
    });
};
if($("#tel")){
	$("#tel").mask("(999) 999-9999");
};

$("#ln, #fn, #mn").on("change keyup input click", function() {
	var re =/[^a-zA-Z0-9_-]/g;
    if (this.value.match(re)) {
        this.value = this.value.replace(re, '');
    }
});
$("#clnm").on("change keyup input click", function() {
	var re = /[^a-zA-Z0-9]/g;
    if (this.value.match(re)) {
        this.value = this.value.replace(re, '');
    }
});


$("#pst").on("change keyup input click, blur, focusout", function(e) {
	var val = $("#prov").val();
	this.value = this.value.toUpperCase().trim();
	//console.log(" #pst change  e.type = ", e.type);
	var str = this.value; 
	if (val > 20 ){
		console.log(" #pst change p1");
		var re =/[^0-9]/g;
		if (this.value.match(re)) {
        	this.value = this.value.replace(re, '');
 		}
		this.value = this.value.substr(0,5);
	}else if (val > 0 ){
		
		var re =/[^a-ceghj-npr-tv-z0-9 ]/gi;
		if (this.value.match(re)) {
        	this.value = this.value.replace(re, '');
 		}
		this.value = this.value.substr(0,7);
		console.log(" #pst 0-3 = ", this.value.substr(0,3),  "   4-3 ",this.value.substr(3,4), " all  ", this.value.substr(0,3)+" "+this.value.substr(3,4));
		//if (str.length >= 4  && str.substr(3,1) != " "){
		//	this.value = this.value.substr(0,3)+" "+this.value.substr(3,4);
		//}
		
		if (str.length == 3){
			this.value = this.value.substr(0,3)+" ";
		}
		if(e.type == "focusout" || e.type == "blur"){
			if (str.match(/[a-ceghj-npr-tvxy]\d[a-ceghj-npr-tv-z](\s)?\d[a-ceghj-npr-tv-z]\d\s*$/i)) {
				console.log(" #pst  true postal code");
				$("#pst").removeClass("tr_err");
				$(".ziptip").css({
           			visibility:'hidden'
        		});

				return true;
			}else{
				console.log(" #pst  wrong postal code");
				setTimeout(function (){
					$("#pst").focus().addClass("tr_err");
					showMsg($("#pst"), "Wrong postal code");
					},200);
				return false;
				
			}
		}

/*		switch (str.length ){
		 case 1:
		 	re =/[^A-Z]/;
			if (!this.value.match(re))this.value='';
		 break
		 case 2:
		 	re =/[a-ceghj-npr-tvxy]\d/i;
			if (!this.value.match(re))this.value='';
		 ;
		 break
		 case 3:
		 ;
		 break
		 case 4:
		 ;
		 break
		 case 5:
		 ;
		 break
		 case 6:
		 ;
		 break
		 case 7:
		 ;
		 break
		}*/
	}else{
		console.log(" #pst change p3");
		var re = /[^a-zA-Z0-9]/g;
    	if (this.value.match(re)) {
        	this.value = this.value.replace(re, '');
    	}
		
	}
 });
 
 
 
 
  function showMsg(fld,msg){
	  var el = $(".tooltip");
	  el.css({visibility:'visible',
             top: $(fld).offset().top - 22,
             left: $(fld).offset().left+5,
             height: 12
       		 });
	  el.html(msg);		
 
  }
  $("#testform").on("click, keydown", function(e) {
	$(".tooltip").css({visibility:'hidden'  }).html("...");
  });
  
	 
 
$("#edtbtn, #delbtn, #savbtn, #clsbtn").button();
//$("#selFacList").multiselect();
//$("#selFacList").bind("change", function(event, ui){
//    console.log("  selFacLict change  ",$("#selFacList").multiselect("getChecked"));
//});
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
	
	$(function() {
   		$(".inp-checkbox").each(function(i) {
            var newCheckBox = createCheckBox($(this), i);
    });  
 
});
</script>