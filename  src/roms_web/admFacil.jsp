<%@ include file="headerInclude.jsp"%>
<%@ include file="subheader.jsp"%>
<script src="js/jquery.jtable.min.js" type="text/javascript"></script>

<link href="css/jtable/metro/blue/jtable.css" rel="stylesheet" type="text/css" />

<%

int NameID = 22; //facil Edit 


%>

<%@ include file="uniheader.jsp"%>


<div style="width:auto; height:550px; overflow:scroll; " >

<div style="width:60%;margin-right:20%;margin-left:20%;text-align:center;">
<div id="PersonTableContainer"></div>
</div>
        
</div> 
 
 
 
 <script type="text/javascript">
    $(document).ready(function () {
        $('#PersonTableContainer').jtable({
            title: 'Table of Facilities',
            actions: {
                listAction: 'CRUDController?action=list',
                createAction:'CRUDController?action=create',
                updateAction: 'CRUDController?action=update',
                deleteAction: 'CRUDController?action=delete'
            },
            fields: {
                facilID: {
                    title:'ID',
                    key: true,
                    list: true,
                    create:true,
                    edit:true
                },
                facilName: {
                    title: 'Facility Name',
                    width: '30%',
                    edit:true
                }    
            }
        });
         $('#PersonTableContainer').jtable('load');
    });
 
</script>

 
 