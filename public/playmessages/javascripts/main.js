function notify(msg, type) {
	$.notify[type](msg, {
		autoClose: 3000,
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
        .load(window.urls.sourcesUrl, {
            key:key
        });
}

function fnDeleteKey(key) {
    $.ajax(window.urls.deleteUrl, {
        type: "POST",
        data: {
            key: key
        }
    }).done(function(msg) {
		handleResult(msg);
        if (msg.success) {
            var oTable = $('#table').dataTable();
            oTable.fnDeleteRow( $('#table tr[name="'+key+'"]').get(0) );
        }
    });
}

function fnKeepKey(key) {
    $.ajax(window.urls.keepUrl, {
        type: "POST",
        data: {
            key: key
        }
    }).done(function(msg) {
		handleResult(msg);
        if (msg.success) {
            var obj = $('#table tr[name="'+key+'"]');
            obj.removeClass("obsolete");
            obj.addClass("keep");
        }
    });
}

function fnUnkeepKey(key) {
    $.ajax(window.urls.unkeepUrl, {
        type: "POST",
        data: {
            key: key
        }
    }).done(function(msg) {
		handleResult(msg);
        if (msg.success) {
            var obj = $('#table tr[name="'+key+'"]');
            obj.removeClass("keep");
            obj.addClass("obsolete");
        }
    });
}

function fnIgnoreKey(key) {
    $.ajax(window.urls.ignoreUrl, {
        type: "POST",
        data: {
            key: key
        }
    }).done(function(msg) {
		handleResult(msg);
        if (msg.success) {
            var oTable = $('#table').dataTable();
            var obj = $('#table tr[name="'+key+'"]');
            oTable.fnDeleteRow( obj.get(0) );
        }
    });
}

function fnUnignoreAll() {
    $.ajax(window.urls.unignoreUrl, {
        type: "POST",
        data: {
        }
    }).done(function(msg) {
		var obj = $(".notification-area");
		obj.html(msg);
		var oTable = $('#table').dataTable();
		$.each($(".notification-area tr"), function(index, value) {
			oTable.fnAddTr(value);
		});
		makejEditable(oTable.$('td.edit'));
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
