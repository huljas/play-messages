$(function() {
	$(document).ajaxError(function(event, jqxhr, settings, exception) {
		notify(jqxhr.statusText, "error");
	});
})

function notify(msg, type) {
	$.notify[type](msg, {
		autoClose: 5000,
		close: true
	});
}

function handleResult(result) {
	if (result && result.notification) {
		notify(result.notification.message, result.notification.notificationType);
	}
}

function fnShowSources(key) {
    $("#sources").dialog('open').dialog('option', {
        title: $("#sources").attr("name") + ' "'+key+'"',
        maxHeight: 600
    });
    $("#sources-target")
        .html('<div class="loading"></div>')
        .load(jsRoutes.controllers.playmessages.MessagesController.sources(key).url);
        
    return false;
}

function fnDeleteKey(key) {
	jsRoutes.controllers.playmessages.MessagesController.delete(key).ajax({
		success: function(msg) {
			handleResult(msg);
			if (msg.success) {
				var obj = $('#table tr[name="'+key+'"]').get(0);
				$('#table').dataTable().fnDeleteRow( obj );
			}
		}
	});
}

function fnKeepKey(key) {
	jsRoutes.controllers.playmessages.MessagesController.keep(key).ajax({
        success: function(msg) {
			handleResult(msg);
			if (msg.success) {
				var obj = $('#table tr[name="'+key+'"]');
				obj.removeClass("obsolete");
				obj.addClass("keep");
			}
		}
    });
}

function fnUnkeepKey(key) {
	jsRoutes.controllers.playmessages.MessagesController.unkeep(key).ajax({
        success: function(msg) {
			handleResult(msg);
			if (msg.success) {
				var obj = $('#table tr[name="'+key+'"]');
				obj.removeClass("keep");
				obj.addClass("obsolete");
			}
		}
    });
}

function fnIgnoreKey(key) {
	jsRoutes.controllers.playmessages.MessagesController.ignore(key).ajax({
        success: function(msg) {
			handleResult(msg);
			if (msg.success) {
				var obj = $('#table tr[name="'+key+'"]').get(0);
				$('#table').dataTable().fnDeleteRow( obj );
			}
		}
    });
}

function fnUnignoreAll() {
	return fnUnignore('');
}

function fnUnignore(key) {
	jsRoutes.controllers.playmessages.MessagesController.unignore(key).ajax({
        success: function(msg) {
			var obj = $(".notification-area");
			obj.html(msg);
			var oTable = $('#table').dataTable();
			$.each($(".notification-area tr"), function(index, value) {
				oTable.fnAddTr(value);
			});
			makejEditable(oTable.$('td.edit'));
		}
    });
}

$.fn.dataTableExt.oApi.fnAddTr = function ( oSettings, nTr, bRedraw ) {
    if ( typeof bRedraw == 'undefined' )
    {
        bRedraw = true;
    }
      
    var nTds = nTr.getElementsByTagName('td');
    if ( nTds.length != oSettings.aoColumns.length )
    {
        alert( 'Warning: not adding new TR - columns and TD elements must match' );
        return;
    }
      
    var aData = [];
    for ( var i=0 ; i<nTds.length ; i++ )
    {
        aData.push( nTds[i].innerHTML );
    }
      
    /* Add the data and then replace DataTable's generated TR with ours */
    var iIndex = this.oApi._fnAddData( oSettings, aData );
    nTr._DT_RowIndex = iIndex;
    oSettings.aoData[ iIndex ].nTr = nTr;
      
    oSettings.aiDisplay = oSettings.aiDisplayMaster.slice();
      
    if ( bRedraw )
    {
        this.oApi._fnReDraw( oSettings );
    }
};
