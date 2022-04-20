/**
* @NApiVersion 2.0
* @NScriptType ClientScript
**/
define(['N/ui/dialog', 'N/currentRecord', 'N/https'], function (dialog, currentRecord, https) {
    function pageinit(context) {
        var record = currentRecord.get();
        //   dialog.alert(record);
        dialog.alert(context)
    }

    function saverecord(context) {
        var record = currentRecord.get();
        //   dialog.alert(record);
        dialog.alert("saveRecord")
    }

    function validatefield(context) {
        var record = currentRecord.get();
        //   dialog.alert(record);
        dialog.alert("validateField")
    }

    function fieldchanged(context) {
        var record = currentRecord.get();
        //   dialog.alert(record);
        dialog.alert("fieldChanged")
    }
    function postsourcing(context) {
        var record = currentRecord.get();
        //   dialog.alert(record);
        dialog.alert("postSourcing")
    }

    function lineinit(context) {
        var record = currentRecord.get();
        //   dialog.alert(record);
        dialog.alert("lineInit")
    }

    function validateline(context) {
        var record = currentRecord.get();
        //   dialog.alert(record);
        dialog.alert("validateline")
    }

    function validateinsert(context) {
        var record = currentRecord.get();
        //   dialog.alert(record);
        dialog.alert("validateInsert")
    }

    function validateinsert(context) {
        var record = currentRecord.get();
        //   dialog.alert(record);
        dialog.alert("validateinsert")
    }

    function validatedelete(context) {
        var record = currentRecord.get();
        //   dialog.alert(record);
        dialog.alert("validateDelete")
    }
    

    return {
        pageInit: pageinit,
        saveRecord: saverecord,
        validateField:validatefield,
        fieldChanged:fieldchanged,
        postSourcing:postsourcing,
        lineInit:lineinit,
        validateLine:validateline,
        validateInsert:validateinsert,
        validateDelete:validatedelete

    };
})



